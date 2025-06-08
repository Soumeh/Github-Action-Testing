package org.solstice.rollingStones.content.upgrade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.entry.RegistryFixedCodec;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.dynamic.Codecs;
import org.solstice.euclidsElements.effectHolder.api.EffectHolder;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

import java.util.List;
import java.util.Optional;

public record Upgrade (
	Optional<Text> description,
	Definition definition,
	ComponentMap effects
) implements EffectHolder {

    public static final Codec<Upgrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		TextCodecs.CODEC.optionalFieldOf("description").forGetter(Upgrade::description),
		Definition.CODEC.forGetter(Upgrade::definition),
		EnchantmentEffectComponentTypes.COMPONENT_MAP_CODEC.optionalFieldOf("effects", ComponentMap.EMPTY).forGetter(Upgrade::effects)
    ).apply(instance, Upgrade::new));

    public static final Codec<RegistryEntry<Upgrade>> ENTRY_CODEC = RegistryFixedCodec.of(RollingRegistryKeys.UPGRADE);


    @Override
    public ComponentMap getEffects() {
        return this.effects;
    }

    @Override
    public Definition getDefinition() {
        return this.definition;
    }

    public static MutableText getName(RegistryEntry<Upgrade> upgrade) {
		if (upgrade.value().description.isPresent()) return upgrade.value().description.get().copy();
        else return Text.translatable(upgrade.getKey().orElseThrow().getValue().toTranslationKey("upgrade"));
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

		public RegistryEntryList<Item> getSupportedItems() {
			return this.supportedItems;
		}

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
