package net.p3pp3rf1y.sophisticatedbackpacks.client.render;

import net.fabricmc.fabric.api.client.rendering.v1.BlockColorRegistry;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlockEntity;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import java.util.List;

/**
 * Block colour sources for the placed backpacks.
 * <p>
 * The item side is handled by {@link BackpackItemTints}; the block model has the same {@code tintindex} 0 (cloth) and
 * 1 (border), and 26.3 only colours a face when the block itself has a {@link BlockTintSource} at that list index -
 * without this the placed backpack renders pure white because the cloth texture is a grey, tintable overlay.
 */
public final class BackpackBlockTints {
	private static final BlockTintSource MAIN_COLOR = new BackpackBlockTint(true);
	private static final BlockTintSource ACCENT_COLOR = new BackpackBlockTint(false);

	private BackpackBlockTints() {
	}

	public static void register() {
		Block[] backpacks = {
				ModBlocks.BACKPACK.get(), ModBlocks.COPPER_BACKPACK.get(), ModBlocks.IRON_BACKPACK.get(),
				ModBlocks.GOLD_BACKPACK.get(), ModBlocks.DIAMOND_BACKPACK.get(), ModBlocks.NETHERITE_BACKPACK.get()
		};
		BlockColorRegistry.register(List.of(MAIN_COLOR, ACCENT_COLOR), backpacks);
	}

	private record BackpackBlockTint(boolean mainColor) implements BlockTintSource {
		@Override
		public int color(BlockState state) {
			return -1;
		}

		@Override
		public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
			BackpackBlockEntity be = WorldHelper.getBlockEntity(level, pos, BackpackBlockEntity.class).orElse(null);
			if (be == null) {
				return -1;
			}
			return mainColor ? be.getBackpackWrapper().getMainColor() : be.getBackpackWrapper().getAccentColor();
		}
	}
}
