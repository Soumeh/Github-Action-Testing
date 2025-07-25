package org.solstice.rollingStones.content.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.content.block.entity.StrongboxEntity;
import org.solstice.rollingStones.registry.RollingBlockEntityTypes;

import java.util.function.Supplier;

public class StrongboxBlock extends AbstractStrongboxBlock implements Waterloggable {

	public static final MapCodec<StrongboxBlock> CODEC = createCodec(settings -> new StrongboxBlock(settings, () -> RollingBlockEntityTypes.STRONGBOX));

	public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
	public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

	@Override
	protected MapCodec<? extends AbstractChestBlock<StrongboxEntity>> getCodec() {
		return CODEC;
	}

	public StrongboxBlock(Settings settings, Supplier<BlockEntityType<? extends StrongboxEntity>> supplier) {
		super(settings, supplier);
		this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.NORTH).with(WATERLOGGED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, WATERLOGGED);
	}

	@Override
	public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new StrongboxEntity(pos, state);
	}

	@Override
	protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
		if (isChestBlocked(world, pos)) return ActionResult.PASS;
		if (world.isClient) return ActionResult.PASS;

		if (world.getBlockEntity(pos) instanceof StrongboxEntity strongboxEntity) {
			int strength;
			StatusEffectInstance effect = player.getStatusEffect(StatusEffects.STRENGTH);
			if (effect != null) strength = 4 * (1 + effect.getAmplifier());
			else strength = 1;

			boolean tryOpen = strongboxEntity.tryOpening(world, pos, strength);
			if (!tryOpen) return ActionResult.FAIL;

			if (strongboxEntity.canOpen()) {
				player.openHandledScreen(strongboxEntity);
				player.incrementStat(this.getOpenStat());
			} else {
				strongboxEntity.scheduleClose();
			}
		}

		return ActionResult.SUCCESS;
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
