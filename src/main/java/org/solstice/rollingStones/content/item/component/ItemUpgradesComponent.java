package org.solstice.rollingStones.content.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.solstice.rollingStones.content.effectHolder.EffectHolderComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.content.upgrade.UpgradeTags;
import org.solstice.rollingStones.registry.ModRegistryKeys;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public record ItemUpgradesComponent(
        Object2IntOpenHashMap<RegistryEntry<Upgrade>> upgrades,
        boolean showInTooltip
) implements EffectHolderComponent<Upgrade>, TooltipAppender {

    public static final Text UPGRADES_TEXT = Text.translatable("item.upgrades").formatted(Formatting.GRAY);

    private static final Codec<Object2IntOpenHashMap<RegistryEntry<Upgrade>>> INLINE_CODEC = Codec.unboundedMap(Upgrade.ENTRY_CODEC, Codec.intRange(0, 255))
            .xmap(Object2IntOpenHashMap::new, Function.identity());

    public static final Codec<ItemUpgradesComponent> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            INLINE_CODEC.fieldOf("tiers").forGetter(ItemUpgradesComponent::upgrades),
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ItemUpgradesComponent::showInTooltip)
    ).apply(instance, ItemUpgradesComponent::new));

    public static final Codec<ItemUpgradesComponent> CODEC = Codec.withAlternative(BASE_CODEC, INLINE_CODEC,
            upgrades -> new ItemUpgradesComponent(upgrades, true)
    );

    public static final PacketCodec<RegistryByteBuf, ItemUpgradesComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.map(Object2IntOpenHashMap::new, Upgrade.ENTRY_PACKET_CODEC, PacketCodecs.VAR_INT),
            component -> component.upgrades, PacketCodecs.BOOL,
            component -> component.showInTooltip, ItemUpgradesComponent::new
    );

    @Override
    public Object2IntOpenHashMap<RegistryEntry<Upgrade>> getEffects() {
        return this.upgrades;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!this.showInTooltip) return;

        if (!this.upgrades.isEmpty())
            tooltip.accept(UPGRADES_TEXT);

        this.upgrades.forEach((upgrade, level) -> {
            addUpgradeTooltip(upgrade, level, tooltip);
        });
    }

    private static void addUpgradeTooltip(RegistryEntry<Upgrade> upgrade, int level, Consumer<Text> tooltip) {
        Text result = Text.empty()
                .append(ScreenTexts.SPACE)
                .append(Upgrade.getFullName(upgrade, level));
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
