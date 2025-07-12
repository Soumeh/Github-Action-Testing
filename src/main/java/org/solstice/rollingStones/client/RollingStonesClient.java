package org.solstice.rollingStones.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import org.solstice.rollingStones.client.content.entity.renderer.StrongboxRenderer;
import org.solstice.rollingStones.client.registry.*;
import org.solstice.rollingStones.registry.RollingBlocks;

public class RollingStonesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		RollingEntityRenderers.init();
		BuiltinItemRendererRegistry.INSTANCE.register(RollingBlocks.STRONGBOX, new StrongboxRenderer.ItemRenderer());
	}

}
