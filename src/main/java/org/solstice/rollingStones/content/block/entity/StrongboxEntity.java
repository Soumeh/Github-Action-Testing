package org.solstice.rollingStones.content.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.registry.RollingBlockEntityTypes;
import org.solstice.rollingStones.registry.RollingSoundEvents;

public class StrongboxEntity extends LootableContainerBlockEntity implements LidOpenable {

	@Override
	protected Text getContainerName() {
		RegistryEntry<BlockEntityType<?>> entry = Registries.BLOCK_ENTITY_TYPE.getEntry(this.getType());
		String key = entry.getKey().orElseThrow().getValue().toTranslationKey("container");
		return Text.translatable(key);
	}

	@Override
	public int size() {
		return 27;
	}

	private static final Integer MAX_OPEN_PROGRESS = 7;

	private DefaultedList<ItemStack> inventory;
	private final ViewerCountManager stateManager;

	private int openProgress = 0;
	private int lastOpenProgress = 0;
	private float animationProgress = 0;
	private float lastAnimationProgress = 0;

	public StrongboxEntity(BlockPos pos, BlockState state) {
		super(RollingBlockEntityTypes.STRONGBOX, pos, state);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		this.stateManager = new StrongboxViewerManager(this);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		if (!this.readLootTable(nbt)) {
			Inventories.readNbt(nbt, this.inventory, registryLookup);
		}
		this.openProgress = nbt.getInt("open_progress");
		this.lastOpenProgress = nbt.getInt("last_open_progress");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
		if (!this.writeLootTable(nbt)) {
			Inventories.writeNbt(nbt, this.inventory, registryLookup);
		}
		nbt.putInt("open_progress", this.openProgress);
		nbt.putInt("last_open_progress", this.lastOpenProgress);
	}

	@Override
	public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
		return createNbt(registryLookup);
	}

	public void scheduleUpdate(World world, BlockPos pos) {
		this.scheduleUpdate(world, pos, 50);
	}

	public void scheduleUpdate(World world, BlockPos pos, int time) {
		world.scheduleBlockTick(pos, world.getBlockState(pos).getBlock(), time);
	}

	public void onScheduledTick(BlockState s, ServerWorld world, BlockPos pos, Random r) {
		if (this.stateManager.getViewerCount() > 0) return;
		if (this.openProgress == MAX_OPEN_PROGRESS) {
			this.updateOpening(0);
			world.playSound(null, pos, RollingSoundEvents.STRONGBOX_CLOSE, SoundCategory.BLOCKS, 0.5F, 1);
		} else if (this.openProgress < MAX_OPEN_PROGRESS) {
			this.updateOpening(0, -1);
			world.playSound(null, pos, RollingSoundEvents.STRONGBOX_SLAM, SoundCategory.BLOCKS, 0.75F, 1);
		}
	}

	@Override
	protected DefaultedList<ItemStack> getHeldStacks() {
		return this.inventory;
	}

	@Override
	protected void setHeldStacks(DefaultedList<ItemStack> inventory) {
		this.inventory = inventory;
	}

	@Override
	protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
		return GenericContainerScreenHandler.createGeneric9x3(syncId, playerInventory, this);
	}

	@Override
	public void onOpen(PlayerEntity player) {
		if (!this.removed && !player.isSpectator())
			this.stateManager.openContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!this.removed && !player.isSpectator())
			this.stateManager.closeContainer(player, this.getWorld(), this.getPos(), this.getCachedState());
	}

	public void incrementOpening(World world, BlockPos pos) {
		float pitch = (float)this.openProgress / MAX_OPEN_PROGRESS;
		pitch = 1.5F - pitch * 1.5F;
		world.playSound(null, pos, RollingSoundEvents.STRONGBOX_OPEN, SoundCategory.BLOCKS, 1, pitch);
		this.lastOpenProgress = this.openProgress;
		this.openProgress = Math.min(this.openProgress + 1, MAX_OPEN_PROGRESS);
		this.markDirty();
		((ServerWorld)world).getChunkManager().markForUpdate(pos);
	}

	public void updateOpening(int progress) {
		this.lastOpenProgress = this.openProgress;
		this.openProgress = progress;
		this.markDirty();
		ServerWorld world = (ServerWorld)this.getWorld();
		if (world != null) world.getChunkManager().markForUpdate(pos);
	}

	public void updateOpening(int progress, int lastProgress) {
		this.openProgress = progress;
		this.lastOpenProgress = lastProgress;
		ServerWorld world = (ServerWorld)this.getWorld();
		if (world != null) world.getChunkManager().markForUpdate(pos);
	}

	public boolean canOpen() {
		return this.openProgress == MAX_OPEN_PROGRESS;
	}

	public static void clientTick(World w, BlockPos p, BlockState s, StrongboxEntity entity) {
		entity.animateLid();
	}

	public void animateLid() {
		this.lastAnimationProgress = this.animationProgress;
		float maxAnimationValue = (float) this.openProgress / MAX_OPEN_PROGRESS;

		float stepBase = (float) Math.abs(this.openProgress - this.lastOpenProgress) / 4;
		float step = stepBase / MAX_OPEN_PROGRESS;

		if (this.openProgress > this.lastOpenProgress) {
			this.animationProgress = Math.min(this.animationProgress + step, maxAnimationValue);
		} else if (this.openProgress < this.lastOpenProgress)
			this.animationProgress = Math.max(this.animationProgress - step, maxAnimationValue);
	}

	@Override
	public float getAnimationProgress(float delta) {
		return MathHelper.lerp(delta, this.lastAnimationProgress, this.animationProgress);
	}

	public static class StrongboxViewerManager extends ViewerCountManager {

		private final StrongboxEntity entity;

		public StrongboxViewerManager(StrongboxEntity entity) {
			this.entity = entity;
		}

		@Override
		protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
			world.playSound(null, pos, RollingSoundEvents.STRONGBOX_OPEN, SoundCategory.BLOCKS, 1, 1);
		}

		@Override
		protected void onContainerClose(World world, BlockPos pos, BlockState state) {
			this.entity.scheduleUpdate(world, pos);
		}

		@Override
		protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {}

		@Override
		protected boolean isPlayerViewing(PlayerEntity player) {
			if (player.currentScreenHandler instanceof GenericContainerScreenHandler container) {
				Inventory inventory = container.getInventory();
				return inventory.equals(this.entity);
			}
			return false;
		}

	}

}
