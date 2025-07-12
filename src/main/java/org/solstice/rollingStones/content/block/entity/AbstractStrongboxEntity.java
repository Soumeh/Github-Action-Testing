package org.solstice.rollingStones.content.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LidOpenable;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.registry.RollingSoundEvents;

public abstract class AbstractStrongboxEntity extends LootableContainerBlockEntity implements LidOpenable {

	private static final Integer MAX_OPEN_PROGRESS = 8;

	private int closeTicks = -1;

	private int openProgress = 0;
	private int lastOpenProgress = 0;

	private float animationProgress = 0;
	private float lastAnimationProgress = 0;
	private final ViewerCountManager stateManager;

	protected AbstractStrongboxEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
		super(blockEntityType, blockPos, blockState);
		this.stateManager = new StrongboxEntity.StrongboxViewerManager(this);
	}

	public boolean canOpen() {
		return this.openProgress == MAX_OPEN_PROGRESS;
	}

	public void scheduleClose() {
		this.scheduleClose(60);
	}

	public void scheduleClose(int closeTicks) {
		this.closeTicks = closeTicks;
	}

	public boolean tryOpening(World world, BlockPos pos, int strength) {
		if (this.closeTicks > 50) return false;

		float power = (float) this.openProgress / MAX_OPEN_PROGRESS;
		float volume = Math.max(power, 0.5F);
		if (power != 1) world.playSound(null, pos, RollingSoundEvents.STRONGBOX_OPEN, SoundCategory.BLOCKS, volume, power);
		this.updateOpening(Math.min(this.openProgress + strength, MAX_OPEN_PROGRESS));
		return true;
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

	@Override
	public void onOpen(PlayerEntity player) {
		if (!this.removed && !player.isSpectator()) this.stateManager.openContainer(
			player, this.getWorld(), this.getPos(), this.getCachedState()
		);
	}

	@Override
	public void onClose(PlayerEntity player) {
		if (!this.removed && !player.isSpectator()) this.stateManager.closeContainer(
			player, this.getWorld(), this.getPos(), this.getCachedState()
		);
	}

	@Override
	protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);
		this.openProgress = nbt.getInt("open_progress");
		this.lastOpenProgress = nbt.getInt("last_open_progress");
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.writeNbt(nbt, registryLookup);
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

	@Override
	protected Text getContainerName() {
		RegistryEntry<BlockEntityType<?>> entry = Registries.BLOCK_ENTITY_TYPE.getEntry(this.getType());
		String key = entry.getKey().orElseThrow().getValue().toTranslationKey("container");
		return Text.translatable(key);
	}

	public static void update(World world, BlockPos p, BlockState s, StrongboxEntity entity) {
		entity.update(world);
	}

	public void update(World world) {
		if (this.closeTicks >= 0) {
			this.closeTicks--;
		}

		if (world.isClient) this.clientUpdate();
		else if (this.closeTicks == 0) this.serverUpdate(world);
	}

	public void serverUpdate(World world) {
		if (this.stateManager.getViewerCount() > 0) return;
		if (this.openProgress >= MAX_OPEN_PROGRESS) return;

		float power = (float) this.openProgress / MAX_OPEN_PROGRESS;
		float volume = Math.max(power, 0.5F);
		float pitch = Math.max(2 - power * 2, 1);
		world.playSound(null, this.getPos(), RollingSoundEvents.STRONGBOX_SLAM, SoundCategory.BLOCKS, volume, pitch);
		this.updateOpening(0, -1);
	}

	public void clientUpdate() {
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

		private final AbstractStrongboxEntity entity;

		public StrongboxViewerManager(AbstractStrongboxEntity entity) {
			this.entity = entity;
		}

		@Override
		protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
			world.playSound(null, pos, RollingSoundEvents.STRONGBOX_OPEN, SoundCategory.BLOCKS);
		}

		@Override
		protected void onContainerClose(World world, BlockPos pos, BlockState state) {
			world.playSound(null, this.entity.getPos(), RollingSoundEvents.STRONGBOX_CLOSE, SoundCategory.BLOCKS);
			this.entity.scheduleClose(-1);
			this.entity.updateOpening(0);
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
