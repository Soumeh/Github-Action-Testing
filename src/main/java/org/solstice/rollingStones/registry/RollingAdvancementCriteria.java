package org.solstice.rollingStones.registry;

import net.minecraft.advancement.criterion.Criterion;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.criterion.UpgradedItemCriterion;

public class RollingAdvancementCriteria {

	public static void init() {}

	public static final UpgradedItemCriterion UPGRADED_ITEM = register("upgraded_item", new UpgradedItemCriterion());

	public static <T extends Criterion<?>> T register(String name, T criterion) {
		return Registry.register(Registries.CRITERION, RollingStones.of(name), criterion);
	}

}
