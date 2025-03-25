package org.solstice.rollingStones.mixin.technical;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.solstice.rollingStones.content.effectHolder.EffectHolderComponent;
import org.spongepowered.asm.mixin.*;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;

@Mixin(ItemEnchantmentsComponent.class)
public class ItemEnchantmentsComponentMixin implements EffectHolderComponent<Enchantment> {

    @Unique private static final Text ENCHANTMENTS_TEXT = Text.translatable("item.enchantments").formatted(Formatting.GRAY);

    @Shadow @Final Object2IntOpenHashMap<RegistryEntry<Enchantment>> enchantments;
    @Shadow @Final boolean showInTooltip;

    @Override
    public Object2IntOpenHashMap<RegistryEntry<Enchantment>> getEffects() {
        return this.enchantments;
    }


    /**
     * @author Solstice
     * @reason Annotated enchantment tooltips
     */
    @Overwrite
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!this.showInTooltip) return;

        RegistryWrapper.WrapperLookup lookup = context.getRegistryLookup();
        RegistryEntryList<Enchantment> orderedEnchantments = getTooltipOrderList(lookup, RegistryKeys.ENCHANTMENT, EnchantmentTags.TOOLTIP_ORDER);

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

    private static <T> RegistryEntryList<T> getTooltipOrderList(@Nullable RegistryWrapper.WrapperLookup registryLookup, RegistryKey<Registry<T>> registryRef, TagKey<T> tooltipOrderTag) {
        if (registryLookup != null) {
            Optional<RegistryEntryList.Named<T>> optional = registryLookup.getWrapperOrThrow(registryRef).getOptional(tooltipOrderTag);
            if (optional.isPresent()) return optional.get();
        }

        return RegistryEntryList.of(new RegistryEntry[0]);
    }

}
