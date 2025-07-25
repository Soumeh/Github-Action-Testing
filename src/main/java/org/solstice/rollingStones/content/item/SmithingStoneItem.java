package org.solstice.rollingStones.content.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.content.upgrade.UpgradeHelper;
import org.solstice.rollingStones.registry.RollingItems;

public class SmithingStoneItem extends Item {

    public SmithingStoneItem(Settings settings) {
        super(settings);
    }

	public static ItemStack forUpgrade(RegistryEntry<Upgrade> entry, int tier) {
		ItemStack stack = RollingItems.SMITHING_STONE_TIERS.getOrDefault(tier, ItemStack.EMPTY).copy();
		UpgradeHelper.apply(stack, builder -> builder.set(entry, tier));
		return stack;
	}

	public ItemStack onUpgrade(ItemStack stack, RegistryWrapper.WrapperLookup lookup, Random random) {
		return stack;
	}

}
