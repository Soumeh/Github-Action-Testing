package org.solstice.rollingStones.content.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.NumberRange;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

import java.util.Optional;

public record UpgradePredicate (
	Optional<RegistryEntryList<Upgrade>> upgrades,
	NumberRange.IntRange tiers
) {

	public static final Codec<UpgradePredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		RegistryCodecs.entryList(RollingRegistryKeys.UPGRADE).optionalFieldOf("upgrades").forGetter(UpgradePredicate::upgrades),
		NumberRange.IntRange.CODEC.optionalFieldOf("tiers", NumberRange.IntRange.ANY).forGetter(UpgradePredicate::tiers)
	).apply(instance, UpgradePredicate::new));

	public boolean test(ItemUpgradesComponent component) {
		if (this.upgrades.isPresent()) {
			for(RegistryEntry<Upgrade> registryEntry : this.upgrades.get()) {
				if (this.testTier(component, registryEntry)) return true;
			}
			return false;
		}

		if (this.tiers != NumberRange.IntRange.ANY) {
			for (Object2IntMap.Entry<RegistryEntry<Upgrade>> entry : component.upgrades().object2IntEntrySet()) {
				if (this.tiers.test(entry.getIntValue())) return true;
			}
			return false;
		}

		return !component.isEmpty();
	}

	private boolean testTier(ItemUpgradesComponent component, RegistryEntry<Upgrade> entry) {
		int tier = component.getEffects().getInt(entry);
		if (tier == 0) return false;
		return this.tiers == NumberRange.IntRange.ANY || this.tiers.test(tier);
	}

}
