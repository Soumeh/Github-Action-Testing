package org.solstice.rollingStones.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.registry.RollingAdvancementCriteria;
import org.solstice.rollingStones.registry.RollingRecipeSerializers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {

	@Shadow @Nullable private RecipeEntry<SmithingRecipe> currentRecipe;

	@Inject(method = "onTakeOutput", at = @At("HEAD"))
	private void triggerUpgradeCriterion(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
		if (player.getWorld().isClient()) return;
		if (this.currentRecipe == null) return;
		if (!this.currentRecipe.value().getSerializer().equals(RollingRecipeSerializers.SMITHING_UPGRADE)) return;

		RollingAdvancementCriteria.UPGRADED_ITEM.trigger((ServerPlayerEntity)player, stack, this.currentRecipe.id());
	}

}
