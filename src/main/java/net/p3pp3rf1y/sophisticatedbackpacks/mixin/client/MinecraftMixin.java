package net.p3pp3rf1y.sophisticatedbackpacks.mixin.client;

import net.minecraft.client.Minecraft;
import net.p3pp3rf1y.sophisticatedbackpacks.client.ClientEventHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.3 删除了 Fabric 的 ClientPickBlockApplyCallback，中键取方块改为挂在原版拾取路径前。
 */
@Mixin(Minecraft.class)
public class MinecraftMixin {
	@Inject(method = "pickBlockOrEntity", require = 1, at = @At("HEAD"))
	private void sophisticatedBackpacks$pickBlockFromBackpack(CallbackInfo ci) {
		Minecraft minecraft = (Minecraft) (Object) this;
		ClientEventHandler.handleBlockPick(minecraft.player, minecraft.hitResult);
	}
}
