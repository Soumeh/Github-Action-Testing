package org.solstice.rollingStones.client.content.entity.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.model.*;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.world.World;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.client.registry.RollingEntityRenderers;
import org.solstice.rollingStones.content.block.entity.StrongboxEntity;

public class StrongboxRenderer implements BlockEntityRenderer<StrongboxEntity> {

	public static final Identifier TEXTURE = RollingStones.of("textures/block/strongbox.png");

	private final ModelPart root;
	private final ModelPart lid;

	public StrongboxRenderer(BlockEntityRendererFactory.Context context) {
		ModelPart part = context.getLayerModelPart(RollingEntityRenderers.STRONGBOX);
		this.root = part;
		this.lid = part.getChild("lid");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData model = new ModelData();
		ModelPartData modelPartData = model.getRoot();

		modelPartData.addChild("test", ModelPartBuilder.create().uv(0, 0).cuboid(0.0F, 0.0F, 0.0F, 16.0F, 16.0F, 16.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));

		modelPartData.addChild("lid", ModelPartBuilder.create().uv(0, 0).cuboid(1.0F, 0.0F, 0.0F, 14.0F, 5.0F, 14.0F), ModelTransform.pivot(0.0F, 9.0F, 1.0F));
		modelPartData.addChild("bottom", ModelPartBuilder.create().uv(0, 19).cuboid(1.0F, 0.0F, 1.0F, 14.0F, 10.0F, 14.0F), ModelTransform.NONE);
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

}
