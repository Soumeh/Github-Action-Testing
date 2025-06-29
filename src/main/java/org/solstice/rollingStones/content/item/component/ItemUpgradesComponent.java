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
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.solstice.euclidsElements.effectHolder.api.component.EffectHolderComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public record ItemUpgradesComponent (
        Object2IntOpenHashMap<RegistryEntry<Upgrade>> upgrades,
        boolean showInTooltip
) implements EffectHolderComponent<Upgrade>, TooltipAppender {

	public static final ItemUpgradesComponent DEFAULT = new ItemUpgradesComponent(new Object2IntOpenHashMap<>(), true);

	public static final Codec<Integer> TIER_CODEC = Codec.intRange(0, 255);

	public static final Codec<Object2IntOpenHashMap<RegistryEntry<Upgrade>>> MAP_CODEC = Codec.unboundedMap(Upgrade.ENTRY_CODEC, TIER_CODEC)
		.xmap(Object2IntOpenHashMap::new, Function.identity()
	);

	public static final Codec<ItemUpgradesComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		MAP_CODEC.fieldOf("tiers").forGetter(ItemUpgradesComponent::upgrades),
		Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ItemUpgradesComponent::showInTooltip)
	).apply(instance, ItemUpgradesComponent::new));

	public static final PacketCodec<RegistryByteBuf, ItemUpgradesComponent> PACKET_CODEC = PacketCodec.tuple(
		PacketCodecs.map(Object2IntOpenHashMap::new, Upgrade.ENTRY_PACKET_CODEC, PacketCodecs.VAR_INT),
		ItemUpgradesComponent::upgrades,
		PacketCodecs.BOOL,
		ItemUpgradesComponent::showInTooltip,
		ItemUpgradesComponent::new
	);

	public static final Text UPGRADES_TEXT = Text.translatable("item.upgrades").formatted(Formatting.GRAY);

	public static ItemUpgradesComponent single(RegistryEntry<Upgrade> entry, int tier) {
		return new ItemUpgradesComponent(
			new Object2IntOpenHashMap<>(Map.of(entry, tier)),
			true
		);
	}

	@Override
    public Object2IntOpenHashMap<RegistryEntry<Upgrade>> getEffects() {
        return this.upgrades;
    }

    @Override
    public void appendTooltip(Item.TooltipContext context, Consumer<Text> tooltip, TooltipType type) {
        if (!this.showInTooltip) return;

        if (!this.upgrades.isEmpty())
            tooltip.accept(UPGRADES_TEXT);

        this.upgrades.forEach((upgrade, tier) ->
            addUpgradeTooltip(upgrade, tier, tooltip)
        );
    }

    private static void addUpgradeTooltip(RegistryEntry<Upgrade> upgrade, int tier, Consumer<Text> tooltip) {
        Text result = Text.empty()
                .append(ScreenTexts.SPACE)
                .append(Upgrade.getFullName(upgrade, tier));
        tooltip.accept(result);
    }

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (!(object instanceof ItemUpgradesComponent component)) return false;
		if (component.showInTooltip != this.showInTooltip) return false;
		return component.upgrades.equals(this.upgrades);
	}

	public boolean isEmpty() {
		return this.upgrades.isEmpty();
	}

	public static class Builder {

		private final Object2IntOpenHashMap<RegistryEntry<Upgrade>> upgrades = new Object2IntOpenHashMap<>();
		private final boolean showInTooltip;

		public Builder(ItemUpgradesComponent component) {
			this(component.upgrades, component.showInTooltip);
		}

		protected Builder(Object2IntOpenHashMap<RegistryEntry<Upgrade>> upgrades, boolean showInTooltip) {
			this.upgrades.putAll(upgrades);
			this.showInTooltip = showInTooltip;
		}

		protected Builder(boolean showInTooltip) {
			this.showInTooltip = showInTooltip;
		}

		public static Builder create() {
			return new Builder(true);
		}

		public void set(RegistryEntry<Upgrade> upgrade, int tier) {
			if (tier <= 0) this.upgrades.removeInt(upgrade);
			this.upgrades.put(upgrade, Math.min(tier, 255));
		}

		public void add(RegistryEntry<Upgrade> upgrade, int level) {
			if (level > 0) this.upgrades.merge(upgrade, Math.min(level, 255), Integer::max);
		}

		public int getTier(RegistryEntry<Upgrade> upgrade) {
			return this.upgrades.getOrDefault(upgrade, 0);
		}

		public ItemUpgradesComponent build() {
			return new ItemUpgradesComponent(this.upgrades, this.showInTooltip);
		}

	}

}
