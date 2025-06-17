package org.solstice.rollingStones.registry;

import com.mojang.serialization.Codec;
import net.minecraft.predicate.item.ItemSubPredicate;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.predicate.UpgradesPredicate;

public class RollingPredicates {

	public static void init() {}

	public static final ItemSubPredicate.Type<UpgradesPredicate.Upgrades> UPGRADES = register("upgrades", UpgradesPredicate.Upgrades.CODEC);
	public static final ItemSubPredicate.Type<UpgradesPredicate.StoredUpgrades> STORED_UPGRADES = register("stored_upgrades", UpgradesPredicate.StoredUpgrades.CODEC);

	private static <T extends ItemSubPredicate> ItemSubPredicate.Type<T> register(String name, Codec<T> codec) {
		return Registry.register(Registries.ITEM_SUB_PREDICATE_TYPE, RollingStones.of(name), new ItemSubPredicate.Type<>(codec));
	}

}
