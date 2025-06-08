package org.solstice.rollingStones.content.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;
import org.solstice.euclidsElements.content.registry.EuclidsComponentTypes;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingComponentTypes;
import org.solstice.rollingStones.registry.RollingItems;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

import java.util.List;

public class SmithingStoneItem extends Item {

    private final RegistryKey<Upgrade> upgrade;
    private final int tier;

    public SmithingStoneItem(RegistryKey<Upgrade> upgrade, int tier, Settings settings) {
        super(settings);
        this.upgrade = upgrade;
        this.tier = tier;
    }

	public static ItemStack forUpgrade(RegistryEntry<Upgrade> entry, int tier) {
		ItemStack stack = new ItemStack(RollingItems.SMITHING_STONE);
		Identifier id = entry.getKey().orElseThrow().getValue();
		Identifier modelId = Registries.ITEM.getId(stack.getItem())
				.withPrefixedPath(id.getNamespace() + "." + id.getPath() + ".")
				.withSuffixedPath(tier + ".");
		stack.set(EuclidsComponentTypes.CUSTOM_ITEM_MODEL, modelId);
		stack.set(RollingComponentTypes.STORED_UPGRADES, ItemUpgradesComponent.single(entry, tier));
		stack.set(DataComponentTypes.RARITY, Rarity.values()[tier-1]);
		return stack;
	}

    public RegistryEntry<Upgrade> getUpgrade(World world) {
        return this.getUpgrade(world.getRegistryManager());
    }

    public RegistryEntry<Upgrade> getUpgrade(RegistryWrapper.WrapperLookup lookup) {
        return lookup.getWrapperOrThrow(RollingRegistryKeys.UPGRADE).getOrThrow(this.upgrade);
    }

    public int getTier() {
        return this.tier;
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        RegistryWrapper.WrapperLookup lookup = context.getRegistryLookup();
        if (lookup == null) return;

        RegistryEntry<Upgrade> upgrade = this.getUpgrade(lookup);
        tooltip.add(Upgrade.getTooltip(upgrade, tier));
    }

}
