package org.solstice.rollingStones.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GrindstoneScreenHandler;
import org.solstice.rollingStones.registry.RollingEnchantmentEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GrindstoneScreenHandler.class)
public class GrindstoneScreenHandlerMixin {

	@Inject(
		method = "getOutputStack",
		at = @At("HEAD"),
		cancellable = true
	)
	private void checkForCursed(
		ItemStack firstInput, ItemStack secondInput, CallbackInfoReturnable<ItemStack> cir
	) {
		boolean primaryCursed = EnchantmentHelper.hasAnyEnchantmentsWith(firstInput, RollingEnchantmentEffects.PREVENT_REPAIRING);
		boolean secondaryCursed = EnchantmentHelper.hasAnyEnchantmentsWith(secondInput, RollingEnchantmentEffects.PREVENT_REPAIRING);

		if (primaryCursed || secondaryCursed) cir.setReturnValue(ItemStack.EMPTY);
	}

}
