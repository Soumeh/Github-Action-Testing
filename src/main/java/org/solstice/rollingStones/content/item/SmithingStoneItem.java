package org.solstice.rollingStones.content.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.content.upgrade.UpgradeHelper;
import org.solstice.rollingStones.registry.RollingItems;

public class SmithingStoneItem extends Item {

    public SmithingStoneItem(Settings settings) {
        super(settings);
    }

	public static ItemStack forUpgrade(RegistryEntry<Upgrade> entry, int tier) {
		ItemStack stack = RollingItems.SMITHING_STONE_TIERS.getOrDefault(tier, ItemStack.EMPTY);
		UpgradeHelper.apply(stack, builder -> builder.set(entry, tier));
		return stack;
	}

}
