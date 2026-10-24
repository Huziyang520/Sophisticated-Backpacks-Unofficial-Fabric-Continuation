package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import com.mojang.serialization.Codec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.SavedDataStorage;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackSettingsHandler;
import net.p3pp3rf1y.sophisticatedcore.SophisticatedCore;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BackpackStorage extends SavedData {
	private static final String SAVED_DATA_NAME = SophisticatedBackpacks.MOD_ID;
	public static final Codec<BackpackStorage> CODEC = CompoundTag.CODEC.xmap(BackpackStorage::fromTag, BackpackStorage::toTag);
	private static final SavedDataType<BackpackStorage> TYPE = new SavedDataType<>(
			Identifier.fromNamespaceAndPath(SophisticatedBackpacks.MOD_ID, SAVED_DATA_NAME), BackpackStorage::new, CODEC, DataFixTypes.LEVEL);

	private final Map<UUID, CompoundTag> backpackContents = new HashMap<>();
	private static final BackpackStorage clientStorageCopy = new BackpackStorage();
	private final Map<UUID, AccessLogRecord> accessLogRecords = new HashMap<>();
	/**
	 * 客户端已从服务端收到过内容的背包 uuid。背包内容不在物品组件上，而在服务端这份 SavedData 里，
	 * 所以客户端 tooltip 只能靠「有没有收到过 backpack_contents」区分「空背包」与「还没同步」。
	 * 只在 {@link #setBackpackContents}（服务端回包在客户端调用的入口）里记，
	 * {@link #getOrCreateBackpackContents} 不算——它被客户端自己读内容时调用，会把「没同步」误判成「已同步」。
	 */
	private final Set<UUID> syncedBackpackContents = new HashSet<>();

	private BackpackStorage() {
	}

	public static BackpackStorage get() {
		if (SophisticatedCore.isLogicalServerThread()) {
			MinecraftServer server = SophisticatedCore.getCurrentServer();
			if (server != null) {
				ServerLevel overworld = server.getLevel(Level.OVERWORLD);
				//noinspection ConstantConditions - by this time overworld is loaded
				SavedDataStorage storage = overworld.getDataStorage();
				return storage.computeIfAbsent(TYPE);
			}
		}
		return clientStorageCopy;
	}

	private static BackpackStorage fromTag(CompoundTag nbt) {
		BackpackStorage storage = new BackpackStorage();
		readBackpackContents(nbt, storage);
		readAccessLogs(nbt, storage);
		return storage;
	}

	private static void readAccessLogs(CompoundTag nbt, BackpackStorage storage) {
		for (Tag n : nbt.getListOrEmpty("accessLogRecords")) {
			AccessLogRecord alr = AccessLogRecord.deserializeFromNBT((CompoundTag) n);
			storage.accessLogRecords.put(alr.getBackpackUuid(), alr);
		}
	}

	private static void readBackpackContents(CompoundTag nbt, BackpackStorage storage) {
		for (Tag n : nbt.getListOrEmpty("backpackContents")) {
			CompoundTag uuidContentsPair = (CompoundTag) n;
			UUID uuid = UUID.fromString(uuidContentsPair.getStringOr("uuid", "00000000-0000-0000-0000-000000000000"));
			CompoundTag contents = uuidContentsPair.getCompoundOrEmpty("contents");
			storage.backpackContents.put(uuid, contents);
		}
	}

	private CompoundTag toTag() {
		CompoundTag ret = new CompoundTag();
		writeBackpackContents(ret);
		writeAccessLogs(ret);
		return ret;
	}

	private void writeBackpackContents(CompoundTag ret) {
		ListTag backpackContentsNbt = new ListTag();
		for (Map.Entry<UUID, CompoundTag> entry : backpackContents.entrySet()) {
			CompoundTag uuidContentsPair = new CompoundTag();
			uuidContentsPair.putString("uuid", entry.getKey().toString());
			uuidContentsPair.put("contents", entry.getValue());
			backpackContentsNbt.add(uuidContentsPair);
		}
		ret.put("backpackContents", backpackContentsNbt);
	}

	private void writeAccessLogs(CompoundTag ret) {
		ListTag accessLogsNbt = new ListTag();
		for (AccessLogRecord alr : accessLogRecords.values()) {
			accessLogsNbt.add(alr.serializeToNBT());
		}
		ret.put("accessLogRecords", accessLogsNbt);
	}

	public CompoundTag getOrCreateBackpackContents(UUID backpackUuid) {
		return backpackContents.computeIfAbsent(backpackUuid, uuid -> {
			setDirty();
			return new CompoundTag();
		});
	}

	public void putAccessLog(AccessLogRecord alr) {
		accessLogRecords.put(alr.getBackpackUuid(), alr);
		setDirty();
	}

	public void removeBackpackContents(UUID backpackUuid) {
		backpackContents.remove(backpackUuid);
		setDirty();
	}

	public void setBackpackContents(UUID backpackUuid, CompoundTag contents) {
		syncedBackpackContents.add(backpackUuid);
		if (!backpackContents.containsKey(backpackUuid)) {
			backpackContents.put(backpackUuid, contents);
			updatedBackpackSettingsFlags.add(backpackUuid);
		} else {
			CompoundTag currentContents = backpackContents.get(backpackUuid);
			for (String key : contents.keySet()) {
				//noinspection ConstantConditions - the key is one of the tag keys so there's no reason it wouldn't exist here
				currentContents.put(key, contents.get(key));

				if (key.equals(BackpackSettingsHandler.SETTINGS_TAG)) {
					updatedBackpackSettingsFlags.add(backpackUuid);
				}
			}
			setDirty();
		}
	}

	public Map<UUID, AccessLogRecord> getAccessLogs() {
		return accessLogRecords;
	}

	/**
	 * 客户端是否已经收到过这只背包的内容。tooltip 用它区分「真空背包」与「内容还没同步」。
	 */
	public boolean hasSyncedContents(UUID backpackUuid) {
		return syncedBackpackContents.contains(backpackUuid);
	}

	public int removeNonPlayerBackpackContents(boolean onlyWithEmptyInventory) {
		AtomicInteger numberRemoved = new AtomicInteger(0);
		backpackContents.entrySet().removeIf(entry -> {
			if (!accessLogRecords.containsKey(entry.getKey()) && (!onlyWithEmptyInventory || !entry.getValue().contains("inventory"))) {
				numberRemoved.incrementAndGet();
				return true;
			}
			return false;
		});
		if (numberRemoved.get() > 0) {
			setDirty();
		}
		return numberRemoved.get();
	}

	private final Set<UUID> updatedBackpackSettingsFlags = new HashSet<>();

	public boolean removeUpdatedBackpackSettingsFlag(UUID backpackUuid) {
		return updatedBackpackSettingsFlags.remove(backpackUuid);
	}

	public static void onClientWorldLoad(Minecraft mc, ClientLevel level) {
		//if (evt.getLevel().isClientSide()) {
			clientStorageCopy.backpackContents.clear();
			clientStorageCopy.accessLogRecords.clear();
			clientStorageCopy.syncedBackpackContents.clear();
		//}
	}
}
