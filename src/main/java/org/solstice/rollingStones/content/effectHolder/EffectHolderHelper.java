package org.solstice.rollingStones.content.effectHolder;

import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableInt;
import org.solstice.rollingStones.registry.ModComponentTypes;

import java.util.List;

public class EffectHolderHelper {

    public static final List<ComponentType<? extends EffectHolderComponent<?>>> EFFECT_HOLDER_COMPONENTS = List.of(
            ModComponentTypes.UPGRADES,
            DataComponentTypes.ENCHANTMENTS
    );

    public static void forEachEffectHolder(ItemStack stack, Consumer consumer) {
        EFFECT_HOLDER_COMPONENTS.forEach(componentType -> {
            EffectHolderComponent<?> component = stack.getOrDefault(componentType, null);
            if (component == null) return;
            component.getEffects().forEach(consumer::accept);
        });
    }

    public static void forEachEffectHolder(ItemStack stack, EquipmentSlot slot, LivingEntity entity, ContextAwareConsumer contextAwareConsumer) {
        if (stack.isEmpty()) return;

        EFFECT_HOLDER_COMPONENTS.forEach(componentType -> {
            EffectHolderComponent<?> component = stack.getOrDefault(componentType, null);
            if (component == null) return;

            EnchantmentEffectContext context = new EnchantmentEffectContext(stack, slot, entity);
            component.getEffects().forEach((entry, level) -> {
                if (entry.value().slotMatches(slot)) {
                    contextAwareConsumer.accept(entry, level, context);
                }
            });
        });
    }

    public static void forEachEffectHolder(LivingEntity entity, ContextAwareConsumer consumer) {
        for(EquipmentSlot slot : EquipmentSlot.values()) {
            forEachEffectHolder(entity.getEquippedStack(slot), slot, entity, consumer);
        }
    }


	public static int getMaxDurability(ItemStack stack, int base) {
		MutableFloat result = new MutableFloat(base);
		forEachEffectHolder(stack, (effectHolder, level) ->
			effectHolder.value().modifyMaxDurability(level, result)
		);
		return result.intValue();
	}


    @FunctionalInterface
    public interface Consumer {
        void accept(RegistryEntry<? extends EffectHolder> enchantment, int level);
    }

    @FunctionalInterface
    public interface ContextAwareConsumer {
        void accept(RegistryEntry<? extends EffectHolder> enchantment, int level, EnchantmentEffectContext context);
    }

}
