package org.solstice.rollingStones.mixin.technical;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.ItemModels;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedModelManager;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.registry.ModComponentTypes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemModels.class)
public abstract class ItemModelsMixin {

	@Shadow public abstract BakedModel getModel(Item item);

	@Shadow @Final private BakedModelManager modelManager;

	@Redirect(
		method = "getModel(Lnet/minecraft/item/ItemStack;)Lnet/minecraft/client/render/model/BakedModel;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/render/item/ItemModels;getModel(Lnet/minecraft/item/Item;)Lnet/minecraft/client/render/model/BakedModel;"
		)
	)
	private BakedModel getCustomModel(ItemModels instance, Item item, @Local(argsOnly = true) ItemStack stack) {
		Identifier customModelId = stack.getOrDefault(ModComponentTypes.CUSTOM_ITEM_MODEL, null);
		if (customModelId == null) return this.getModel(item);
		return this.modelManager.getModel(ModelIdentifier.ofInventoryVariant(customModelId));
	}

}
