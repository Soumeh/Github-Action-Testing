package org.solstice.rollingStones.mixin.technical;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.solstice.rollingStones.content.effectHolder.EffectHolder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements EffectHolder {

    @Shadow @Final private ComponentMap effects;
    @Shadow @Final private Enchantment.Definition definition;

    @Override
    public ComponentMap getEffects() {
        return this.effects;
    }

    @Override
    public Definition getDefinition() {
        return this.definition;
    }

    @Redirect(
            method = "getName",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/text/Texts;setStyleIfAbsent(Lnet/minecraft/text/MutableText;Lnet/minecraft/text/Style;)Lnet/minecraft/text/MutableText;",
                    ordinal = 1
            )
    )
    private static MutableText enchantmentColor (
            MutableText style,
            Style component,
            @Local MutableText result,
            @Local(argsOnly = true) RegistryEntry<Enchantment> enchantment,
            @Local(argsOnly = true) int level
    ) {
        if (level >= enchantment.value().getMaxLevel()) Texts.setStyleIfAbsent(result, Style.EMPTY.withColor(Formatting.LIGHT_PURPLE));
        else Texts.setStyleIfAbsent(result, Style.EMPTY.withColor(Formatting.BLUE));
        return null;
    }

}
