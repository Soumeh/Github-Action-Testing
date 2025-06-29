package org.solstice.rollingStones.client.registry;

import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import org.solstice.rollingStones.client.content.entity.renderer.StrongboxRenderer;
import org.solstice.rollingStones.registry.RollingBlockEntityTypes;

public class RollingEntityRenderers {

	public static void init() {
		BlockEntityRendererFactories.register(RollingBlockEntityTypes.STRONGBOX, StrongboxRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(StrongboxRenderer.LAYER, StrongboxRenderer::getTexturedModelData);
	}

}
