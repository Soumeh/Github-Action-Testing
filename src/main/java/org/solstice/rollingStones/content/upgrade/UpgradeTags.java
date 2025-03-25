package org.solstice.rollingStones.content.upgrade;

import net.minecraft.registry.tag.TagKey;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.registry.ModRegistryKeys;

public class UpgradeTags {

    public static final TagKey<Upgrade> TOOLTIP_ORDER = create("tooltip_order");

    private static TagKey<Upgrade> create(String name) {
        return TagKey.of(ModRegistryKeys.UPGRADE, RollingStones.of(name));
    }

}
