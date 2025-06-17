package org.solstice.rollingStones.content.block;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stat;
import net.minecraft.stat.Stats;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.content.block.entity.StrongboxEntity;
import org.solstice.rollingStones.registry.RollingBlockEntityTypes;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractStrongboxBlock extends AbstractChestBlock<StrongboxEntity> {

	public static final VoxelShape SHAPE = Block.createCuboidShape(1, 0, 1, 15, 14, 15);

	public static boolean isChestBlocked(WorldAccess world, BlockPos pos) {
		return hasBlockOnTop(world, pos) || hasCatOnTop(world, pos);
	}

	private static boolean hasBlockOnTop(BlockView world, BlockPos pos) {
		BlockPos abovePos = pos.up();
		return world.getBlockState(abovePos).isSolidBlock(world, abovePos);
	}

	private static boolean hasCatOnTop(WorldAccess world, BlockPos pos) {
		Box box = new Box(pos).expand(1, 2, 1);
//		Box box = new Box(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1);
//		Box box = new Box(pos.toCenterPos(), pos.toCenterPos());
		List<CatEntity> list = world.getNonSpectatingEntities(CatEntity.class, box);
		for (CatEntity entity : list) {
			if (entity.isInSittingPose()) {
				return true;
			}
		}
		return false;
	}

	public AbstractStrongboxBlock(Settings settings, Supplier<BlockEntityType<? extends StrongboxEntity>> supplier) {
		super(settings, supplier);
	}

	protected Stat<Identifier> getOpenStat() {
		return Stats.CUSTOM.getOrCreateStat(Stats.OPEN_CHEST);
	}

	@Override
	protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		ItemScatterer.onStateReplaced(state, newState, world, pos);
		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	protected BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		return SHAPE;
	}

	@Override
	protected boolean hasComparatorOutput(BlockState state) {
		return true;
	}

	@Override
	protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	protected boolean canPathfindThrough(BlockState state, NavigationType type) {
		return false;
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new StrongboxEntity(pos, state);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		if (world.getBlockEntity(pos) instanceof StrongboxEntity strongboxEntity)
			strongboxEntity.onScheduledTick(state, world, pos, random);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
		return world.isClient ? validateTicker(type, RollingBlockEntityTypes.STRONGBOX, StrongboxEntity::clientTick) : null;
	}

	@Override
	public DoubleBlockProperties.PropertySource<? extends ChestBlockEntity> getBlockEntitySource(BlockState state, World world, BlockPos pos, boolean ignoreBlocked) {
		return null;
	}

}
