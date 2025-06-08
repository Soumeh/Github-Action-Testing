package org.solstice.rollingStones.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.solstice.rollingStones.content.block.entity.StrongboxBlockEntity;
import org.solstice.rollingStones.registry.RollingBlockEntityTypes;

import java.util.function.Supplier;

public class StrongboxBlock extends AbstractStrongboxBlock implements Waterloggable {

	public static final MapCodec<StrongboxBlock> CODEC = createCodec(settings -> new StrongboxBlock(settings, () -> RollingBlockEntityTypes.STRONGBOX));

	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	@Override
	protected MapCodec<? extends AbstractChestBlock<StrongboxBlockEntity>> getCodec() {
		return CODEC;
	}

	public StrongboxBlock(Settings settings, Supplier<BlockEntityType<? extends StrongboxBlockEntity>> supplier) {
		super(settings, supplier);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (isChestBlocked(world, pos)) return ActionResult.PASS;
		if (world.isClient) return ActionResult.SUCCESS;

		if (world.getBlockEntity(pos) instanceof StrongboxBlockEntity strongboxBlockEntity) {
			strongboxBlockEntity.incrementOpening(world, pos);
			if (strongboxBlockEntity.canOpen()) {
				player.openHandledScreen(strongboxBlockEntity);
				player.incrementStat(this.getOpenStat());
				PiglinBrain.onGuardedBlockInteracted(player, true);
			} else {
				strongboxBlockEntity.scheduleUpdate(world, pos);
			}
		}

		return ActionResult.CONSUME;
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getHorizontalPlayerFacing().getOpposite();
		FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());
		return this.getDefaultState()
			.with(FACING, direction)
			.with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
	}

	@Override
	protected BlockState rotate(BlockState state, BlockRotation rotation) {
		return state.with(FACING, rotation.rotate(state.get(FACING)));
	}

	@Override
	protected BlockState mirror(BlockState state, BlockMirror mirror) {
		return state.rotate(mirror.getRotation(state.get(FACING)));
	}

}
