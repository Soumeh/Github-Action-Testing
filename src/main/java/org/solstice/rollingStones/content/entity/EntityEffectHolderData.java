package org.solstice.rollingStones.content.entity;

import net.minecraft.enchantment.effect.EnchantmentLocationBasedEffect;
import org.solstice.rollingStones.content.effectHolder.EffectHolder;

import java.util.Map;
import java.util.Set;

public interface EntityEffectHolderData {

    Map<EffectHolder, Set<EnchantmentLocationBasedEffect>> getLocationBasedEffects();

}
