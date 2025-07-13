package org.solstice.rollingStones.registry;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.solstice.rollingStones.RollingStones;

public class RollingDamageSources {

	public static void init() {}

	public static final RegistryKey<DamageType> BROKEN_FINGERS = of("broken_fingers");

	public static DamageSource brokenFingers(World world, Vec3d position) {
		return new DamageSource(
			world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(BROKEN_FINGERS),
			position
		);
	}

	public static RegistryKey<DamageType> of(String path) {
		return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, RollingStones.of(path));
	}

}
