package org.solstice.rollingStones.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import org.solstice.rollingStones.registry.RollingAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

	@Inject(method = "createLivingAttributes", at = @At("RETURN"), cancellable = true)
	private static void appendLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> cir) {
		DefaultAttributeContainer.Builder builder = cir.getReturnValue();
		builder.add(RollingAttributes.MAX_PULL_TIME_MULTIPLIER);
		cir.setReturnValue(builder);
	}

}
