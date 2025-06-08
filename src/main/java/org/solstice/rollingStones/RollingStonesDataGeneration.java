package org.solstice.rollingStones;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.*;
import org.solstice.euclidsElements.autoDatagen.api.generator.*;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.content.upgrade.Upgrades;
import org.solstice.rollingStones.registry.RollingEnchantments;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

public class RollingStonesDataGeneration implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();
		pack.addProvider(AutoLanguageGenerator::new);
		pack.addProvider(AutoModelGenerator::new);
	}

	@Override
	public void buildRegistry(RegistryBuilder builder) {
		builder.addRegistry(RollingRegistryKeys.UPGRADE, Upgrades::bootstrap);
		builder.addRegistry(RegistryKeys.ENCHANTMENT, RollingEnchantments::bootstrap);
	}

	public RegistryKey<Upgrade> of(String name) {
		return RegistryKey.of(RollingRegistryKeys.UPGRADE, RollingStones.of(name));
	}

}
