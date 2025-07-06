package org.solstice.rollingStones;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
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
		RollingRegistryKeys.init();
		RollingRegistries.init();
		RollingAttributes.init();
		RollingComponentTypes.init();

		RollingItems.init();
		RollingBlocks.init();
		RollingBlockEntityTypes.init();
		RollingRecipeSerializers.init();

		RollingEnchantmentEffects.init();
		RollingLootFunctionTypes.init();
		RollingAdvancementCriteria.init();
		RollingPredicates.init();

		RollingTags.init();
		RollingSoundEvents.init();
		RollingCommands.init();

		RollingItemChanges.modifyItemGroups();
		DefaultItemComponentEvents.MODIFY.register(RollingItemChanges::modifyItems);
		RollingLootTableChanges.modifyVillagerTrades();
		LootTableEvents.MODIFY.register(RollingLootTableChanges::modifyLootTables);
	}

}
