package org.solstice.rollingStones.content.effectHolder;

import net.minecraft.registry.entry.RegistryEntry;

@FunctionalInterface
public interface EffectHolderConsumer {

    void accept(RegistryEntry<? extends EffectHolder> enchantment, int level);

}
