package org.solstice.rollingStones.content.effectHolder;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.Collections;
import java.util.Set;

public interface EffectHolderComponent<T extends EffectHolder> {

    Object2IntOpenHashMap<RegistryEntry<T>> getEffects();

    default int getLevel(RegistryEntry<T> effect) {
        return this.getEffects().getInt(effect);
    }

    default Set<Object2IntMap.Entry<RegistryEntry<T>>> entrySet() {
        return Collections.unmodifiableSet(this.getEffects().object2IntEntrySet());
    }

    default int size() {
        return this.getEffects().size();
    }

    default boolean isEmpty() {
        return this.getEffects().isEmpty();
    }

}
