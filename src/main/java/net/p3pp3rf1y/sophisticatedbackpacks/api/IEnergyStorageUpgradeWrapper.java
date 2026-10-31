package net.p3pp3rf1y.sophisticatedbackpacks.api;

import team.reborn.energy.api.EnergyStorage;

import javax.annotation.Nullable;

public interface IEnergyStorageUpgradeWrapper {
	@Nullable
	EnergyStorage wrapStorage(@Nullable EnergyStorage energyStorage);
}
