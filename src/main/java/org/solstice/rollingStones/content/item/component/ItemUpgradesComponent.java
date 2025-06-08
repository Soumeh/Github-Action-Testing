package org.solstice.rollingStones.content.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.solstice.euclidsElements.effectHolder.api.component.EffectHolderComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public record ItemUpgradesComponent (
        Object2IntOpenHashMap<RegistryEntry<Upgrade>> upgrades,
        boolean showInTooltip
) implements EffectHolderComponent<Upgrade>, TooltipAppender {

    private static final Codec<Object2IntOpenHashMap<RegistryEntry<Upgrade>>> INLINE_CODEC = Codec.unboundedMap(Upgrade.ENTRY_CODEC, Codec.intRange(0, 255))
            .xmap(Object2IntOpenHashMap::new, Function.identity());

    public static final Codec<ItemUpgradesComponent> BASE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            INLINE_CODEC.fieldOf("tiers").forGetter(ItemUpgradesComponent::upgrades),
            Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter(ItemUpgradesComponent::showInTooltip)
    ).apply(instance, ItemUpgradesComponent::new));

    public static final Codec<ItemUpgradesComponent> CODEC = Codec.withAlternative(BASE_CODEC, INLINE_CODEC,
            upgrades -> new ItemUpgradesComponent(upgrades, true)
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

	public static class Builder {

		private final Object2IntOpenHashMap<RegistryEntry<Upgrade>> upgrades;
		private final boolean showInTooltip;

		protected Builder(ItemUpgradesComponent component) {
			this.upgrades = new Object2IntOpenHashMap<>(component.upgrades);
			this.showInTooltip = component.showInTooltip;
		}

		public static Builder of(ItemUpgradesComponent component) {
			return new Builder(component);
		}

		public void set(RegistryEntry<Upgrade> upgrade, int tier) {
			if (tier <= 0) this.upgrades.removeInt(upgrade);
			else this.upgrades.put(upgrade, Math.min(tier, 255));
		}

		public void add(RegistryEntry<Upgrade> upgrade, int tier) {
			if (tier > 0) this.upgrades.merge(upgrade, Math.min(tier, 255), Integer::max);
		}

		public void remove(Predicate<RegistryEntry<Upgrade>> predicate) {
			this.upgrades.keySet().removeIf(predicate);
		}

		public int getTier(RegistryEntry<Upgrade> upgrade) {
			return this.upgrades.getOrDefault(upgrade, 0);
		}

		public Set<RegistryEntry<Upgrade>> getUpgrades() {
			return this.upgrades.keySet();
		}

		public ItemUpgradesComponent build() {
			return new ItemUpgradesComponent(this.upgrades, this.showInTooltip);
		}

	}

}
