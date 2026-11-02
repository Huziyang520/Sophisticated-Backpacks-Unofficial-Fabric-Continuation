package net.p3pp3rf1y.sophisticatedbackpacks.mixin.common;

import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.SeagrassBlock;
import net.minecraft.world.level.block.TallGrassBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WebBlock;
import net.p3pp3rf1y.sophisticatedbackpacks.api.SophisticatedShearable;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Marks shearable vegetation so the tool swapper treats shears as effective on it.
 * <p>
 * 26.3 merged {@code DeadBushBlock} into {@link BushBlock}, so the bush family is covered by that
 * base class instead of a dedicated dead bush type.
 */
@Mixin({BushBlock.class, LeavesBlock.class, SeagrassBlock.class, TallGrassBlock.class, VineBlock.class, WebBlock.class})
public class ShearableBlocksMixin implements SophisticatedShearable {
}
