package org.solstice.rollingStones.mixin.client;

import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.solstice.rollingStones.registry.RollingAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(ModelPredicateProviderRegistry.class)
public class ModelPredicateProviderRegistryMixin {

	@ModifyArg(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/item/ModelPredicateProviderRegistry;register(Lnet/minecraft/item/Item;Lnet/minecraft/util/Identifier;Lnet/minecraft/client/item/ClampedModelPredicateProvider;)V",
			ordinal = 0
		),
		slice = @Slice(
			from = @At(
				value = "CONSTANT",
				args = "stringValue=pull"
			)
		),
		index = 2
	)
	private static ClampedModelPredicateProvider updateBowPredicateProvider(ClampedModelPredicateProvider provider) {
		return ModelPredicateProviderRegistryMixin::bowPredicateProvider;
	}

	@Unique
	private static float bowPredicateProvider(ItemStack stack, ClientWorld world, LivingEntity entity, int seed) {
		if (entity == null || entity.getActiveItem() != stack) return 0.0F;
		float multiplier = (float)(stack.getMaxUseTime(entity) - entity.getItemUseTimeLeft()) / 20.0F;
		return Math.min(multiplier, (float)entity.getAttributeValue(RollingAttributes.MAX_PULL_TIME_MULTIPLIER));
	}

}
