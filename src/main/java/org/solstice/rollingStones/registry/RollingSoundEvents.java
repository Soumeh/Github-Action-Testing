package org.solstice.rollingStones.registry;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.RollingStones;

public class RollingSoundEvents {

	public static void init() {}

	public static final SoundEvent STRONGBOX_OPEN = register("block.strongbox.open");
	public static final SoundEvent STRONGBOX_CLOSE = register("block.strongbox.close");
	public static final SoundEvent STRONGBOX_SLAM = register("block.strongbox.slam");

	public static SoundEvent register(String name) {
		Identifier id = RollingStones.of(name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

	public static RegistryEntry<SoundEvent> registerReference(String name) {
		Identifier id = RollingStones.of(name);
		return Registry.registerReference(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}

}
