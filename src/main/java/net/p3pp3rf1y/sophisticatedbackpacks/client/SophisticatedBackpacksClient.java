package net.p3pp3rf1y.sophisticatedbackpacks.client;

import fuzs.forgeconfigapiport.fabric.api.v5.client.ConfigScreenFactoryRegistry;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.ClientTooltipComponentCallback;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModItemsClient;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;

import javax.annotation.Nullable;

@Environment(EnvType.CLIENT)
public class SophisticatedBackpacksClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		KeybindHandler.registerKeyMappings();
		KeybindHandler.register();

		ClientEventHandler.registerHandlers();

		ModItemsClient.registerScreens();

		ClientTooltipComponentCallback.EVENT.register(SophisticatedBackpacksClient::registerTooltipComponent);

		ConfigScreenFactoryRegistry.INSTANCE.register(SophisticatedBackpacks.MOD_ID,
				(modId, parent) -> new ConfigurationScreen(modId, parent));
	}
	@Nullable
	private static ClientTooltipComponent registerTooltipComponent(TooltipComponent data) {
		if (data instanceof BackpackItem.BackpackContentsTooltip) {
			return new ClientBackpackContentsTooltip((BackpackItem.BackpackContentsTooltip) data);
		}

		return null;
	}
}
