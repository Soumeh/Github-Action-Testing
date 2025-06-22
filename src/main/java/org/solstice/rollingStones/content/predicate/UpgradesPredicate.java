package org.solstice.rollingStones.content.predicate;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.item.ComponentSubPredicate;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.registry.RollingComponentTypes;

import java.util.List;
import java.util.function.Function;

public abstract class UpgradesPredicate implements ComponentSubPredicate<ItemUpgradesComponent> {

	private final List<UpgradePredicate> upgrades;

	protected UpgradesPredicate(List<UpgradePredicate> upgrades) {
		this.upgrades = upgrades;
	}

	public static <T extends UpgradesPredicate> Codec<T> createCodec(Function<List<UpgradePredicate>, T> predicateFunction) {
		return UpgradePredicate.CODEC.listOf().xmap(predicateFunction, UpgradesPredicate::getUpgrades);
	}

	protected List<UpgradePredicate> getUpgrades() {
		return this.upgrades;
	}

	public boolean test(ItemStack stack, ItemUpgradesComponent component) {
		for (UpgradePredicate predicate : this.upgrades) {
			if (!predicate.test(component)) return false;
		}
		return true;
	}

	public static Upgrades upgrades(List<UpgradePredicate> upgrades) {
		return new Upgrades(upgrades);
	}

	public static StoredUpgrades storedUpgrades(List<UpgradePredicate> storedEnchantments) {
		return new StoredUpgrades(storedEnchantments);
	}

	public static class Upgrades extends UpgradesPredicate {

		public static final Codec<Upgrades> CODEC = createCodec(Upgrades::new);

		protected Upgrades(List<UpgradePredicate> list) {
			super(list);
		}

		public ComponentType<ItemUpgradesComponent> getComponentType() {
			return RollingComponentTypes.UPGRADES;
		}

	}

	public static class StoredUpgrades extends UpgradesPredicate {

		public static final Codec<StoredUpgrades> CODEC = createCodec(StoredUpgrades::new);

		protected StoredUpgrades(List<UpgradePredicate> list) {
			super(list);
		}

		public ComponentType<ItemUpgradesComponent> getComponentType() {
			return RollingComponentTypes.STORED_UPGRADES;
		}

	}

}
