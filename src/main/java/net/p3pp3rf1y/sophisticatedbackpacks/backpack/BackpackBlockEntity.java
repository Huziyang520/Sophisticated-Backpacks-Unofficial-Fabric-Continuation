package net.p3pp3rf1y.sophisticatedbackpacks.backpack;

import net.p3pp3rf1y.sophisticatedcore.platform.item.SlottedStackStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.BackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.wrapper.IBackpackWrapper;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.api.IStorageWrapper;
import net.p3pp3rf1y.sophisticatedcore.controller.ControllerBlockEntityBase;
import net.p3pp3rf1y.sophisticatedcore.controller.IControllableStorage;
import net.p3pp3rf1y.sophisticatedcore.fluid.EmptyFluidHandler;
import net.p3pp3rf1y.sophisticatedcore.inventory.CachedFailedInsertInventoryHandler;
import net.p3pp3rf1y.sophisticatedcore.renderdata.RenderInfo;
import net.p3pp3rf1y.sophisticatedcore.renderdata.TankPosition;
import net.p3pp3rf1y.sophisticatedcore.upgrades.ITickableUpgrade;
import net.p3pp3rf1y.sophisticatedcore.util.RegistryHelper;
import net.p3pp3rf1y.sophisticatedcore.util.WorldHelper;

import org.jetbrains.annotations.Nullable;
import java.util.Objects;
import java.util.Optional;

import static net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackBlock.*;
import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks.BACKPACK_TILE_TYPE;

public class BackpackBlockEntity extends BlockEntity implements IControllableStorage {
	@Nullable
	private BlockPos controllerPos = null;
	private IBackpackWrapper backpackWrapper = IBackpackWrapper.Noop.INSTANCE;
	private boolean updateBlockRender = true;

	private boolean chunkBeingUnloaded = false;

	@Nullable
	private SlottedStackStorage externalItemHandler;
	@Nullable
	private IStorageFluidHandler externalFluidHandler;
	@Nullable


	public BackpackBlockEntity(BlockPos pos, BlockState state) {
		super(BACKPACK_TILE_TYPE.get(), pos, state);
	}

	public void setBackpack(ItemStack backpack) {
		backpackWrapper = BackpackWrapper.fromStack(backpack);
		backpackWrapper.setContentsChangeHandler(() -> {
			setChanged();
			updateBlockRender = false;
			WorldHelper.notifyBlockUpdate(this);
		});
		backpackWrapper.setInventorySlotChangeHandler(this::setChanged);
		backpackWrapper.setUpgradeCachesInvalidatedHandler(this::invalidateHandlers);
	}

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		CompoundTag tag = input.read("sophisticatedbackpack_data", CompoundTag.CODEC).orElseGet(CompoundTag::new);
		setBackpackFromNbt(tag);

		// If updateBlockRender exists we are in an update packet load
		if (tag.contains("updateBlockRender")) {
			if (tag.getBooleanOr("updateBlockRender", false)) {
				WorldHelper.notifyBlockUpdate(this);
			}
		} else {
			loadControllerPos(tag);

			if (level != null && !level.isClientSide()) {
				removeControllerPos();
				tryToAddToController();
			}

			WorldHelper.notifyBlockUpdate(this);
		}
	}

	@Override
	public void sophisticatedCore_onLoad() {
		super.sophisticatedCore_onLoad();
		registerWithControllerOnLoad();
	}

	private void setBackpackFromNbt(CompoundTag nbt) {
		Tag data = nbt.get("backpackData");
		if (data == null) {
			setBackpack(ItemStack.EMPTY);
			return;
		}

		Optional<RegistryAccess> registryAccess = RegistryHelper.getRegistryAccess();
		if (registryAccess.isEmpty()) {
			// The block entity is usually loaded before a level wide registry access is reachable from here; the
			// block is still a backpack, so fall back to the plain block item instead of an empty stack - an empty
			// stack is cast to BackpackItem the first time the menu opens.
			SophisticatedBackpacks.LOGGER.warn("No registry access while loading backpack data at {}, using the block item", getBlockPos());
			setBackpack(new ItemStack(getBlockState().getBlock()));
			return;
		}

		setBackpack(ItemStack.CODEC.parse(registryAccess.get().createSerializationContext(NbtOps.INSTANCE), data)
				.result()
				.map(this::fallbackIfEmpty)
				.orElseGet(() -> new ItemStack(getBlockState().getBlock())));
	}

	private ItemStack fallbackIfEmpty(ItemStack parsed) {
		return parsed.isEmpty() ? new ItemStack(getBlockState().getBlock()) : parsed;
	}

	@Override
	protected void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		CompoundTag tag = new CompoundTag();
		writeBackpack(tag, getLevel() == null ? null : getLevel().registryAccess());
		saveControllerPos(tag);
		output.store("sophisticatedbackpack_data", CompoundTag.CODEC, tag);
	}

	private void writeBackpack(CompoundTag ret, @Nullable HolderLookup.Provider registries) {
		if (registries == null) {
			return;
		}
		ItemStack backpackCopy = backpackWrapper.getBackpack().copy();
		ItemStack.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), backpackCopy)
				.result().ifPresent(tag -> ret.put("backpackData", tag));
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag ret = super.getUpdateTag(registries);
		// loadAdditional reads the backpack from the nested "sophisticatedbackpack_data" tag, so the update tag has
		// to use the same shape - writing "backpackData" at the root made every client side block entity come up with
		// an empty backpack, and opening it then threw ClassCastException and dropped the player out of the world.
		CompoundTag data = new CompoundTag();
		writeBackpack(data, registries);
		ret.put("sophisticatedbackpack_data", data);
		ret.putBoolean("updateBlockRender", updateBlockRender);
		updateBlockRender = true;
		return ret;
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	/** This is inside {@link #loadAdditional} **/
	/*@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
		CompoundTag tag = pkt.getTag();
		if (tag == null) {
			return;
		}

		setBackpackFromNbt(tag);
		if (tag.getBooleanOr("updateBlockRender", false)) {
			WorldHelper.notifyBlockUpdate(this);
		}
	}*/

	public IBackpackWrapper getBackpackWrapper() {
		return backpackWrapper;
	}

	private void invalidateHandlers() {
		sophisticatedCore_invalidateCapabilities();
		externalItemHandler = null;
		externalFluidHandler = null;
	}

	private boolean isBlockConnectionDisallowed(@Nullable Direction direction) {
		return direction != null && level != null && Config.SERVER.noConnectionBlocks.isBlockConnectionDisallowed(level.getBlockState(getBlockPos().relative(direction)).getBlock());
	}

	@Nullable
	public SlottedStackStorage getExternalItemHandler(@Nullable Direction direction) {
		if (isBlockConnectionDisallowed(direction)) {
			return null;
		}
		if (externalItemHandler == null) {
			externalItemHandler = new CachedFailedInsertInventoryHandler(() -> getBackpackWrapper().getInventoryForInputOutput(), () -> level != null ? level.getGameTime() : 0);
		}
		return externalItemHandler;
	}

	@Nullable
	public IStorageFluidHandler getExternalFluidHandler(@Nullable Direction direction) {
		if (isBlockConnectionDisallowed(direction)) {
			return null;
		}
		if (externalFluidHandler == null) {
			externalFluidHandler = getBackpackWrapper().getFluidHandler().map(IStorageFluidHandler.class::cast).orElse(EmptyFluidHandler.INSTANCE);
		}
		return externalFluidHandler;
	}

	public void refreshRenderState() {
		BlockState state = getBlockState();
		state = state.setValue(LEFT_TANK, false);
		state = state.setValue(RIGHT_TANK, false);
		RenderInfo renderInfo = backpackWrapper.getRenderInfo();
		for (TankPosition pos : renderInfo.getTankRenderInfos().keySet()) {
			if (pos == TankPosition.LEFT) {
				state = state.setValue(LEFT_TANK, true);
			} else if (pos == TankPosition.RIGHT) {
				state = state.setValue(RIGHT_TANK, true);
			}
		}
		state = state.setValue(BATTERY, renderInfo.getBatteryRenderInfo().isPresent());
		Level l = Objects.requireNonNull(level);
		l.setBlockAndUpdate(worldPosition, state);
		l.updateNeighborsAt(worldPosition, state.getBlock());
		WorldHelper.notifyBlockUpdate(this);
	}

	public static void serverTick(Level level, BlockPos blockPos, BackpackBlockEntity backpackBlockEntity) {
		if (level.isClientSide()) {
			return;
		}
		backpackBlockEntity.backpackWrapper.getUpgradeHandler().getWrappersThatImplement(ITickableUpgrade.class).forEach(upgrade -> upgrade.tick(null, level, blockPos));
	}

	@Override
	public IStorageWrapper getStorageWrapper() {
		return backpackWrapper;
	}

	@Override
	public void setControllerPos(BlockPos controllerPos) {
		this.controllerPos = controllerPos;
		setChanged();
	}

	@Override
	public Optional<BlockPos> getControllerPos() {
		return Optional.ofNullable(controllerPos);
	}

	@Override
	public void removeControllerPos() {
		controllerPos = null;
	}

	@Override
	public BlockPos getStorageBlockPos() {
		return getBlockPos();
	}

	@Override
	public Level getStorageBlockLevel() {
		return Objects.requireNonNull(getLevel());
	}

	@Override
	public boolean canConnectStorages() {
		return false;
	}

	@Override
	public void unregisterController() {
		IControllableStorage.super.unregisterController();
		backpackWrapper.unregisterOnSlotsChangeListener();
		backpackWrapper.unregisterOnInventoryHandlerRefreshListener();
	}

	@Override
	public void registerController(ControllerBlockEntityBase controllerBlockEntity) {
		IControllableStorage.super.registerController(controllerBlockEntity);
		if (level != null && !level.isClientSide()) {
			backpackWrapper.registerOnSlotsChangeListener(this::changeSlots);
			backpackWrapper.registerOnInventoryHandlerRefreshListener(this::registerInventoryStackListeners);
		}
	}

	@Override
	public void sophisticatedCore_onChunkUnloaded() {
		super.sophisticatedCore_onChunkUnloaded();
		chunkBeingUnloaded = true;
	}

	@Override
	public void setRemoved() {
		if (!chunkBeingUnloaded && level != null) {
			removeFromController();
		}
		super.setRemoved();
	}
}
