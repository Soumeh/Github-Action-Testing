package org.solstice.rollingStones;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solstice.rollingStones.handler.RollingItemChanges;
import org.solstice.rollingStones.handler.RollingLootTableChanges;
import org.solstice.rollingStones.registry.*;

public class RollingStones implements ModInitializer {

    public static final String MOD_ID = "rolling_stones";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier of(String path) {
        return Identifier.of(MOD_ID, path);
    }

	@Override
	public void onInitialize() {
		RollingRegistries.init();
		RollingAttributes.init();

		RollingItems.init();
		RollingBlocks.init();
		RollingBlockEntityTypes.init();
		RollingComponentTypes.init();
		RollingRecipeSerializers.init();

		RollingLootFunctionTypes.init();
		RollingAdvancementCriteria.init();
		RollingPredicates.init();

		RollingTags.init();

		DefaultItemComponentEvents.MODIFY.register(RollingItemChanges::modifyItems);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(RollingItemChanges::addSmithingStones);
		RollingLootTableChanges.modifyVillagerTrades();
		LootTableEvents.MODIFY.register(RollingLootTableChanges::modifyLootTables);
	}

}
