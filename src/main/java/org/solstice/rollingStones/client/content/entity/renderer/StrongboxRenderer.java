package org.solstice.rollingStones.client.content.entity.renderer;

import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.block.entity.StrongboxEntity;
import org.solstice.rollingStones.registry.RollingBlocks;

public class StrongboxRenderer implements BlockEntityRenderer<StrongboxEntity> {

	public static final Identifier TEXTURE = RollingStones.of("textures/block/strongbox.png");
	public static final EntityModelLayer LAYER = new EntityModelLayer(RollingStones.of("strongbox"), "main");

	private final ModelPart root;
	private final ModelPart lid;

	public StrongboxRenderer(BlockEntityRendererFactory.Context context) {
		ModelPart root = context.getLayerModelPart(LAYER);
		this.root = root;
		this.lid = root.getChild("lid");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData model = new ModelData();
		ModelPartData data = model.getRoot();

		data.addChild("lid",
			ModelPartBuilder.create()
				.uv(0, 0)
				.cuboid(-7, -5, -14, 14, 5, 14),
			ModelTransform.pivot(8, 7, 15)
		);
		data.addChild("base",
			ModelPartBuilder.create()
				.uv(0, 19)
				.cuboid(1, 0, 1, 14, 10, 14),
			ModelTransform.pivot(0, 6, 0)
		);

		return TexturedModelData.of(model, 64, 64);
	}

	@Override
	public void render(StrongboxEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
		matrices.push();

		World world = entity.getWorld();
		BlockState state = world != null ? entity.getCachedState() : Blocks.CHEST.getDefaultState().with(ChestBlock.FACING, Direction.SOUTH);

		float rotation = state.get(ChestBlock.FACING).asRotation();
		matrices.translate(0.5F, 0.5F, 0.5F);
		matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-rotation));
		matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
		matrices.translate(-0.5F, -0.5F, -0.5F);

		VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.getEntitySolid(TEXTURE));

		float openFactor = 0;
		if (world != null && world.getBlockEntity(entity.getPos()) instanceof StrongboxEntity strongboxEntity) {
			openFactor = strongboxEntity.getAnimationProgress(tickDelta);
		}
		this.lid.pitch = -(openFactor * ((float)Math.PI / 2F));
		this.root.render(matrices, vertices, light, overlay);

		matrices.pop();
	}

	public static class ItemRenderer implements BuiltinItemRendererRegistry.DynamicItemRenderer {

		public static final StrongboxEntity ENTITY = new StrongboxEntity(BlockPos.ORIGIN, RollingBlocks.STRONGBOX.getDefaultState());

		@Override
		public void render(ItemStack stack, ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
			MinecraftClient.getInstance().getBlockEntityRenderDispatcher().renderEntity(
				ENTITY, matrices, vertexConsumers, light, overlay
			);
		}

	}

}
