package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;

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

	public static AccessLogRecord deserializeFromNBT(CompoundTag nbt) {
		return new AccessLogRecord(
				Identifier.parse(nbt.getString("backpackItemRegistryName").orElse("")),
				net.minecraft.core.UUIDUtil.CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt.get("backpackUuid")).getOrThrow(),
				nbt.getString("playerName").orElse(""),
				nbt.getString("backpackName").orElse(""),
				nbt.getInt("clothColor").orElse(0),
				nbt.getInt("trimColor").orElse(0),
				nbt.getLong("accessTime").orElse(0L),
				nbt.getInt("columnsTaken").orElse(0)
		);
	}
}
