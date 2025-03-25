package org.solstice.rollingStones.mixin.technical;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import org.solstice.rollingStones.content.effectHolder.EffectHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Enchantment.Definition.class)
public class EnchantmentDefinitionMixin implements EffectHolder.Definition {

    @Shadow @Final private int maxLevel;
    @Shadow @Final private List<AttributeModifierSlot> slots;

    @Override
    public int getMaxTier() {
        return this.maxLevel;
    }

    @Override
    public List<AttributeModifierSlot> getSlots() {
        return this.slots;
    }

}
