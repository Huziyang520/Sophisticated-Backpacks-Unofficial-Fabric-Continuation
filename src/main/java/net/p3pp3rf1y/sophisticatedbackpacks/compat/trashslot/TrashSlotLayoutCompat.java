package net.p3pp3rf1y.sophisticatedbackpacks.compat.trashslot;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.resources.Identifier;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedcore.client.gui.SettingsScreen;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;

/**
 * Supplies the storage GUI's live dimensions to the host's layout contract.
 * The host only exposes a fixed screen rectangle, so this is wired reflectively.
 */
public final class TrashSlotLayoutCompat {
	private static final String API = "net.blay09.mods.trashslot.api.TrashSlotAPI";
	private static final String LAYOUT = "net.blay09.mods.trashslot.api.layout.TrashContainerLayout";
	private static final String CONTEXT = "net.blay09.mods.trashslot.api.layout.TrashSlotContainerContext";
	private static final Identifier BACKPACK = SophisticatedBackpacks.getRL("backpack");
	private static final Identifier SETTINGS = SophisticatedBackpacks.getRL("settings");

	private static boolean probed;
	private static Class<?> layoutType;
	private static Class<?> contextType;
	private static Method registerLayout;
	private static Method getDefaultLayout;
	private static Method getSnap;
	private static Method getSnaps;
	private static Method getDefaultSnap;
	private static Method getAvailability;
	private static Method isEnabledByDefault;
	private static Object defaultLayout;

	private TrashSlotLayoutCompat() {
	}

	public static void onScreenInit(Screen screen) {
		if (!(screen instanceof AbstractContainerScreen<?>) || !probe()) {
			return;
		}
		Identifier id = screen instanceof StorageScreenBase<?> ? BACKPACK
				: screen instanceof SettingsScreen ? SETTINGS : null;
		if (id == null) {
			return;
		}
		try {
			defaultLayout = getDefaultLayout.invoke(null);
			registerLayout.invoke(null, id, createLayout((AbstractContainerScreen<?>) screen));
		} catch (Throwable ignored) {
			// Host absent or API changed; the data-driven layout remains in effect.
		}
	}

	private static boolean probe() {
		if (probed) {
			return layoutType != null;
		}
		probed = true;
		try {
			Class<?> api = Class.forName(API);
			layoutType = Class.forName(LAYOUT);
			contextType = Class.forName(CONTEXT);
			registerLayout = api.getMethod("registerLayout", Identifier.class, layoutType);
			getDefaultLayout = api.getMethod("getDefaultLayout");
			getSnap = layoutType.getMethod("getSnap", contextType, Identifier.class);
			getSnaps = layoutType.getMethod("getSnaps", contextType);
			getDefaultSnap = layoutType.getMethod("getDefaultSnap", contextType);
			getAvailability = layoutType.getMethod("getAvailability");
			isEnabledByDefault = layoutType.getMethod("isEnabledByDefault");
			return true;
		} catch (Throwable t) {
			layoutType = null;
			return false;
		}
	}

	private static Object createLayout(AbstractContainerScreen<?> screen) {
		InvocationHandler handler = (proxy, method, args) -> {
			switch (method.getName()) {
				case "getBounds":
					return Optional.ofNullable(boundsOf(screen));
				case "getAllBounds": {
					Rect2i bounds = boundsOf(screen);
					return bounds == null ? List.of() : List.of(bounds);
				}
				case "createContext":
					return createContext(screen, proxy);
				case "getSnap":
					return getSnap.invoke(defaultLayout, args[0], args[1]);
				case "getSnaps":
					return getSnaps.invoke(defaultLayout, args[0]);
				case "getDefaultSnap":
					return getDefaultSnap.invoke(defaultLayout, args[0]);
				case "getAvailability":
					return getAvailability.invoke(defaultLayout);
				case "isEnabledByDefault":
					return isEnabledByDefault.invoke(defaultLayout);
				case "toString":
					return "SophisticatedBackpacksTrashSlotLayout";
				case "hashCode":
					return System.identityHashCode(proxy);
				case "equals":
					return proxy == args[0];
				default:
					return defaultValue(method.getReturnType());
			}
		};
		return Proxy.newProxyInstance(TrashSlotLayoutCompat.class.getClassLoader(), new Class<?>[] {layoutType}, handler);
	}

	private static Object createContext(AbstractContainerScreen<?> screen, Object layout) {
		InvocationHandler handler = (proxy, method, args) -> {
			switch (method.getName()) {
				case "screen":
					return screen;
				case "layout":
					return layout;
				case "rect":
					return Optional.ofNullable(boundsOf(screen));
				case "toString":
					return "SophisticatedBackpacksTrashSlotContext";
				case "hashCode":
					return System.identityHashCode(proxy);
				case "equals":
					return proxy == args[0];
				default:
					return defaultValue(method.getReturnType());
			}
		};
		return Proxy.newProxyInstance(TrashSlotLayoutCompat.class.getClassLoader(), new Class<?>[] {contextType}, handler);
	}

	private static Rect2i boundsOf(AbstractContainerScreen<?> screen) {
		if (screen instanceof StorageScreenBase<?> storageScreen) {
			return storageScreen.getGuiBounds();
		}
		if (screen instanceof SettingsScreen settingsScreen) {
			return settingsScreen.getGuiBounds();
		}
		return null;
	}

	private static Object defaultValue(Class<?> type) {
		if (type == boolean.class) {
			return false;
		}
		if (type == int.class) {
			return 0;
		}
		if (type == long.class) {
			return 0L;
		}
		return null;
	}
}
