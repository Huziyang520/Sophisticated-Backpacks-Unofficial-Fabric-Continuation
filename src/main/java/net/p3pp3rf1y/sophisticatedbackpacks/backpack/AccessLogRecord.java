package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;

public class AccessLogRecord {
	private final Identifier backpackItemRegistryName;
	private final UUID backpackUuid;
	private final String playerName;
	private final String backpackName;
	private final int clothColor;
	private final int trimColor;
	private final long accessTime;
	private final int columnsTaken;

	public AccessLogRecord(Identifier backpackItemRegistryName, UUID backpackUuid, String playerName, String backpackName, int clothColor, int trimColor, long accessTime, int columnsTaken) {
		this.backpackItemRegistryName = backpackItemRegistryName;
		this.backpackUuid = backpackUuid;
		this.playerName = playerName;
		this.backpackName = backpackName;
		this.clothColor = clothColor;
		this.trimColor = trimColor;
		this.accessTime = accessTime;
		this.columnsTaken = columnsTaken;
	}

	public UUID getBackpackUuid() {
		return backpackUuid;
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getBackpackName() {
		return backpackName;
	}

	public int getClothColor() {
		return clothColor;
	}

	public int getTrimColor() {
		return trimColor;
	}

	public long getAccessTime() {
		return accessTime;
	}

	public int getColumnsTaken() {
		return columnsTaken;
	}

	public Identifier getBackpackItemRegistryName() {
		return backpackItemRegistryName;
	}

	public CompoundTag serializeToNBT() {
		CompoundTag ret = new CompoundTag();
		ret.putString("backpackItemRegistryName", backpackItemRegistryName.toString());
		ret.put("backpackUuid", net.minecraft.core.UUIDUtil.CODEC.encodeStart(net.minecraft.nbt.NbtOps.INSTANCE, backpackUuid).getOrThrow());
		ret.putString("playerName", playerName);
		ret.putString("backpackName", backpackName);
		ret.putInt("clothColor", clothColor);
		ret.putInt("trimColor", trimColor);
		ret.putLong("accessTime", accessTime);
		ret.putInt("columnsTaken", columnsTaken);
		return ret;
	}

	@Nullable
	public static AccessLogRecord deserializeFromNBT(CompoundTag nbt) {
		Tag uuidTag = nbt.get("backpackUuid");
		String itemId = nbt.getString("backpackItemRegistryName").orElse("");
		if (uuidTag == null || itemId.isEmpty()) {
			return null;
		}
		Optional<UUID> uuid = net.minecraft.core.UUIDUtil.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, uuidTag).result();
		if (uuid.isEmpty()) {
			return null;
		}
		Identifier item;
		try {
			item = Identifier.parse(itemId);
		} catch (RuntimeException e) {
			return null;
		}
		return new AccessLogRecord(
				item,
				uuid.get(),
				nbt.getString("playerName").orElse(""),
				nbt.getString("backpackName").orElse(""),
				nbt.getInt("clothColor").orElse(0),
				nbt.getInt("trimColor").orElse(0),
				nbt.getLong("accessTime").orElse(0L),
				nbt.getInt("columnsTaken").orElse(0)
		);
	}
}
