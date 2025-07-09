package org.solstice.rollingStones.mixin;

import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.registry.RollingEnchantmentEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {

	@Shadow @Final private Property levelCost;

	public AnvilScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Redirect(
            method = "updateResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;set(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;",
                    ordinal = 1
            )
    )
    private <T> T ignore(ItemStack instance, ComponentType<? super T> type, T value) {
        return null;
    }

	@Inject(
		method = "updateResult",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/screen/AnvilScreenHandler;sendContentUpdates()V"
		),
		cancellable = true
	)
	private void checkForCursed(
		CallbackInfo ci
	) {
		boolean primaryCursed = EnchantmentHelper.hasAnyEnchantmentsWith(
			this.input.getStack(0),
			RollingEnchantmentEffects.PREVENT_REPAIRING
		);
		boolean secondaryCursed = EnchantmentHelper.hasAnyEnchantmentsWith(
			this.input.getStack(1),
			RollingEnchantmentEffects.PREVENT_REPAIRING
		);
		if (primaryCursed || secondaryCursed) {
			this.levelCost.set(0);
			ci.cancel();
		}
	}

}
