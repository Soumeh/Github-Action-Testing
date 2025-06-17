package org.solstice.rollingStones.client.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.client.content.entity.renderer.StrongboxRenderer;
import org.solstice.rollingStones.registry.RollingBlockEntityTypes;

public class RollingEntityRenderers {

	public static final Identifier STRONGBOX_ID = RollingStones.of("strongbox");
	public static final EntityModelLayer STRONGBOX = new EntityModelLayer(STRONGBOX_ID, "main");

	public static void init() {
		BlockEntityRendererFactories.register(RollingBlockEntityTypes.STRONGBOX, StrongboxRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(STRONGBOX, StrongboxRenderer::getTexturedModelData);
	}

}
