package net.p3pp3rf1y.sophisticatedbackpacks.api;

import net.p3pp3rf1y.sophisticatedcore.api.energy.EnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyStorageUpgradeWrapper {
	@Nullable
	EnergyStorage wrapStorage(@Nullable EnergyStorage energyStorage);
}
