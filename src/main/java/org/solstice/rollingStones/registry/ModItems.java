package org.solstice.rollingStones.registry;

import net.minecraft.item.Item;
import net.minecraft.util.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.item.SmithingStoneItem;
import org.solstice.rollingStones.content.upgrade.Upgrades;

import java.util.function.Function;

public class ModItems {

    public static final DeferredRegister.Items REGISTRY = DeferredRegister.Items.createItems(RollingStones.MOD_ID);

	public static final DeferredItem<Item> SMITHING_STONE = register("smithing_stone", Item::new, new Item.Settings());
    public static final DeferredItem<Item> TIER_ONE_DURABILITY_SMITHING_STONE = register("tier_one_durability_smithing_stone",
            settings -> new SmithingStoneItem(Upgrades.DURABILITY, 1, settings),
            new Item.Settings()
                    .rarity(Rarity.COMMON)
    );
    public static final DeferredItem<Item> TIER_TWO_DURABILITY_SMITHING_STONE = register("tier_two_durability_smithing_stone",
            settings -> new SmithingStoneItem(Upgrades.DURABILITY, 2, settings),
            new Item.Settings()
                    .rarity(Rarity.UNCOMMON)
    );
    public static final DeferredItem<Item> TIER_THREE_DURABILITY_SMITHING_STONE = register("tier_three_durability_smithing_stone",
            settings -> new SmithingStoneItem(Upgrades.DURABILITY, 3, settings),
            new Item.Settings()
                    .rarity(Rarity.RARE)
    );

    private static DeferredItem<Item> register(String name, Function<Item.Settings, Item> constructor, Item.Settings settings) {
        return REGISTRY.registerItem(name, constructor, settings);
    }

}
