package org.solstice.rollingStones.mixin.technical;

import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.render.model.BlockStatesLoader;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.JsonUnbakedModel;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Map;

@Mixin(ModelLoader.class)
public abstract class ModelLoaderMixin {

	@Shadow protected abstract void loadInventoryVariantItemModel(Identifier id);

	@Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/model/ModelLoader;loadInventoryVariantItemModel(Lnet/minecraft/util/Identifier;)V"))
	private void ignored(ModelLoader instance, Identifier id) {}

	@Inject(
		method = "<init>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V",
			ordinal = 0,
			shift = At.Shift.AFTER
		)
	)
	private void loadAllItemModels (
		BlockColors blockColors,
		Profiler profiler,
		Map<Identifier, JsonUnbakedModel> jsonUnbakedModels,
		Map<Identifier, List<BlockStatesLoader.SourceTrackedData>> blockStates,
		CallbackInfo ci
	) {
		jsonUnbakedModels.keySet().stream()
			.filter(id -> id.getPath().startsWith("models/item"))
			.map(id -> id.withPath(path -> path.replace("models/item/", "").replace(".json", "")))
			.forEach(this::loadInventoryVariantItemModel);
	}

}
