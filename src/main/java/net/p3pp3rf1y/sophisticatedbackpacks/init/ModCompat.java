package net.p3pp3rf1y.sophisticatedbackpacks.init;

import net.p3pp3rf1y.sophisticatedbackpacks.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatInfo;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatRegistry;

import static net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks.MOD_ID;
import static net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds.LITEMATICA;

public class ModCompat {
	private ModCompat() {
	}

	public static void register() {
		CompatRegistry registry = CompatRegistry.getRegistry(MOD_ID);
	}
}
