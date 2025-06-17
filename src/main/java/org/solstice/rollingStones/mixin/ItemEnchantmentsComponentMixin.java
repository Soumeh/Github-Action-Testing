package org.solstice.rollingStones.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.*;

import java.util.function.Consumer;

@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin {

    @Unique private static final Text ENCHANTMENTS_TEXT = Text.translatable("item.enchantments").formatted(Formatting.GRAY);

    @Shadow @Final Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments;
    @Shadow @Final boolean showInTooltip;

    @WrapMethod(method = "appendTooltip")
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type, Operation<Void> original) {
        if (!this.showInTooltip) return;

        RegistryWrapper.WrapperLookup wrapperLookup = context.getRegistryLookup();
		if (wrapperLookup == null) return;

		RegistryEntryList<Enchantment> orderedEnchantments = wrapperLookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOptional(EnchantmentTags.TOOLTIP_ORDER).orElseThrow();

        if (!this.enchantments.isEmpty())
            tooltip.accept(ENCHANTMENTS_TEXT);

        orderedEnchantments.forEach(enchantment -> {
            int level = this.enchantments.getInt(enchantment);
            if (level > 0) addEnchantmentTooltip(enchantment, level, tooltip);
        });

        this.enchantments.forEach((enchantment, level) -> {
            if (!orderedEnchantments.contains(enchantment))
                addEnchantmentTooltip(enchantment, level, tooltip);
        });
    }

    @Unique
    private static void addEnchantmentTooltip(RegistryEntry<Enchantment> enchantment, int level, Consumer<Text> tooltip) {
        Text result = Text.empty()
                .append(ScreenTexts.SPACE)
                .append(Enchantment.getName(enchantment, level));
        tooltip.accept(result);
    }

}
