package org.solstice.rollingStones.content.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import org.solstice.euclidsElements.api.effectHolder.EffectHolder;
import org.solstice.rollingStones.registry.ModRegistryKeys;

import java.util.List;

public record Upgrade (
        Definition definition,
        ComponentMap effects
) implements EffectHolder {

    public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Definition.CODEC.forGetter(Upgrade::definition),
            EnchantmentEffectComponentTypes.COMPONENT_MAP_CODEC.optionalFieldOf("effects", ComponentMap.EMPTY).forGetter(Upgrade::effects)
    ).apply(instance, Upgrade::new));

    public static final Codec<RegistryEntry<Upgrade>> ENTRY_CODEC = RegistryFixedCodec.of(ModRegistryKeys.UPGRADE);


    @Override
    public ComponentMap getEffects() {
        return this.effects;
    }

    @Override
    public EffectHolder.Definition getDefinition() {
        return this.definition;
    }

    public static MutableText getName(RegistryEntry<Upgrade> upgrade) {
        return Text.translatable(
                upgrade.getKey().orElseThrow().getValue().toTranslationKey("upgrade")
        );
    }

    public static MutableText getTooltip(RegistryEntry<Upgrade> upgrade, int tier) {
        Text name = getName(upgrade);
        MutableText tooltip = Text.translatable("item.smithing_stone.tooltip", tier, name);
        Texts.setStyleIfAbsent(tooltip, Style.EMPTY.withColor(Formatting.GOLD));
        return tooltip;
    }

    public static MutableText getFullName(RegistryEntry<Upgrade> upgrade, int tier) {
        MutableText name = getName(upgrade);

        int maxTier = upgrade.value().getDefinition().getMaxLevel();
        if (maxTier != 1) {
            Text tierTooltip = Text.translatable("upgrade.tiers", tier, maxTier);
//            Text tierTooltip = Text.translatable("upgrade.tiers",
//                    Text.translatable("enchantment.level." + tier),
//                    Text.translatable("enchantment.level." + maxTier)
//            );
            name.append(tierTooltip);
        }

        Texts.setStyleIfAbsent(name, Style.EMPTY.withColor(Formatting.GOLD));
        return name;
    }

    public record Definition (
            RegistryEntryList<Item> supportedItems,
            int maxTier,
            List<AttributeModifierSlot> slots
    ) implements EffectHolder.Definition {

        public static final MapCodec<Definition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                RegistryCodecs.entryList(RegistryKeys.ITEM).fieldOf("supported_items").forGetter(Definition::supportedItems),
                Codecs.rangedInt(1, 255).fieldOf("max_tier").forGetter(Definition::maxTier),
                AttributeModifierSlot.CODEC.listOf().fieldOf("slots").forGetter(Definition::slots)
        ).apply(instance, Definition::new));

        @Override
        public int getMaxLevel() {
            return this.maxTier;
        }

        @Override
        public List<AttributeModifierSlot> getSlots() {
            return this.slots;
        }

    }

}
