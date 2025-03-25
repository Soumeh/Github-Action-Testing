package org.solstice.rollingStones.registry;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import org.solstice.rollingStones.content.upgrade.Upgrade;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModRegistries {

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(ModRegistryKeys.UPGRADE, Upgrade.CODEC, Upgrade.CODEC);
    }

}
