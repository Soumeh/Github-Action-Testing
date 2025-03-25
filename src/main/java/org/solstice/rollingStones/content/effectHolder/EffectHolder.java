package org.solstice.rollingStones.content.effectHolder;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public interface EffectHolder {

    ComponentMap getEffects();

    Definition getDefinition();

    default int getMaxTier() {
        return this.getDefinition().getMaxTier();
    }

    default boolean slotMatches(EquipmentSlot equipmentSlot) {
        return this.getDefinition().getSlots().stream().anyMatch(slot -> slot.matches(equipmentSlot));
    }


    static <T> void applyEffects(List<EnchantmentEffectEntry<T>> entries, LootContext context, Consumer<T> effectConsumer) {
        entries.forEach(effect -> {
            if (effect.test(context)) {
                effectConsumer.accept(effect.effect());
            }
        });
    }

    default <T> List<T> getEffect(ComponentType<List<T>> type) {
        return this.getEffects().getOrDefault(type, List.of());
    }


    default void onTick(ServerWorld world, int level, EnchantmentEffectContext context, Entity user) {
        applyEffects(
                this.getEffect(EnchantmentEffectComponentTypes.TICK),
                Enchantment.createEnchantedEntityLootContext(world, level, user, user.getPos()),
                effect -> effect.apply(world, level, context, user, user.getPos())
        );
    }

    default void onProjectileSpawned(ServerWorld world, int level, EnchantmentEffectContext context, Entity user) {
        applyEffects(
                this.getEffect(EnchantmentEffectComponentTypes.PROJECTILE_SPAWNED),
                Enchantment.createEnchantedEntityLootContext(world, level, user, user.getPos()),
                effect -> effect.apply(world, level, context, user, user.getPos())
        );
    }

    default void onHitBlock(ServerWorld world, int level, EnchantmentEffectContext context, Entity enchantedEntity, Vec3d pos, BlockState state) {
        applyEffects(
                this.getEffect(EnchantmentEffectComponentTypes.HIT_BLOCK),
                Enchantment.createHitBlockLootContext(world, level, enchantedEntity, pos, state),
                effect -> effect.apply(world, level, context, enchantedEntity, pos)
        );
    }

    default void modifyValue(ComponentType<EnchantmentValueEffect> type, Random random, int level, MutableFloat value) {
        EnchantmentValueEffect effect = this.getEffects().get(type);
        if (effect != null) {
            value.setValue(effect.apply(level, random, value.floatValue()));
        }
    }
    
    default void modifyValue(ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type, ServerWorld world, int level, ItemStack stack, MutableFloat value) {
        applyEffects(
                this.getEffect(type),
                Enchantment.createEnchantedItemLootContext(world, level, stack),
                effect -> value.setValue(effect.apply(level, world.getRandom(), value.getValue()))
        );
    }

    default void modifyValue(ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type, ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat value) {
        applyEffects(
                this.getEffect(type),
                Enchantment.createEnchantedEntityLootContext(world, level, user, user.getPos()),
                effect -> value.setValue(effect.apply(level, user.getRandom(), value.floatValue()))
        );
    }

    default void modifyValue(ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type, ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat value) {
        applyEffects(
                this.getEffect(type),
                Enchantment.createEnchantedDamageLootContext(world, level, user, damageSource),
                effect -> value.setValue(effect.apply(level, user.getRandom(), value.floatValue()))
        );
    }

    default boolean hasDamageImmunityTo(ServerWorld world, int level, Entity user, DamageSource damageSource) {
        LootContext context = Enchantment.createEnchantedDamageLootContext(world, level, user, damageSource);

        for(EnchantmentEffectEntry<DamageImmunityEnchantmentEffect> effect : this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_IMMUNITY)) {
            if (effect.test(context)) return true;
        }
        return false;
    }

    default void modifyDamageProtection(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damageProtection) {
        LootContext context = Enchantment.createEnchantedDamageLootContext(world, level, user, damageSource);

        for(EnchantmentEffectEntry<EnchantmentValueEffect> effect : this.getEffect(EnchantmentEffectComponentTypes.DAMAGE_PROTECTION)) {
            if (effect.test(context)) damageProtection.setValue(
                    effect.effect().apply(level, user.getRandom(), damageProtection.floatValue())
            );
        }
    }

    default void modifyItemDamage(ServerWorld world, int level, ItemStack stack, MutableFloat itemDamage) {
        this.modifyValue(EnchantmentEffectComponentTypes.ITEM_DAMAGE, world, level, stack, itemDamage);
    }

    default void modifyAmmoUse(ServerWorld world, int level, ItemStack projectileStack, MutableFloat ammoUse) {
        this.modifyValue(EnchantmentEffectComponentTypes.AMMO_USE, world, level, projectileStack, ammoUse);
    }

    default void onTargetDamaged(ServerWorld world, int level, EnchantmentEffectContext context, EnchantmentEffectTarget target, Entity user, DamageSource damageSource) {
        for(TargetedEnchantmentEffect<EnchantmentEntityEffect> effect : this.getEffect(EnchantmentEffectComponentTypes.POST_ATTACK)) {
            if (target == effect.enchanted())
                Enchantment.applyTargetedEffect(effect, world, level, context, user, damageSource);
        }
    }

    default void modifyProjectilePiercing(ServerWorld world, int level, ItemStack stack, MutableFloat projectilePiercing) {
        this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_PIERCING, world, level, stack, projectilePiercing);
    }

    default void modifyBlockExperience(ServerWorld world, int level, ItemStack stack, MutableFloat blockExperience) {
        this.modifyValue(EnchantmentEffectComponentTypes.BLOCK_EXPERIENCE, world, level, stack, blockExperience);
    }

    default void modifyMobExperience(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat mobExperience) {
        this.modifyValue(EnchantmentEffectComponentTypes.MOB_EXPERIENCE, world, level, stack, user, mobExperience);
    }

    default void modifyRepairWithXp(ServerWorld world, int level, ItemStack stack, MutableFloat repairWithXp) {
        this.modifyValue(EnchantmentEffectComponentTypes.REPAIR_WITH_XP, world, level, stack, repairWithXp);
    }

    default void modifyTridentReturnAcceleration(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat tridentReturnAcceleration) {
        this.modifyValue(EnchantmentEffectComponentTypes.TRIDENT_RETURN_ACCELERATION, world, level, stack, user, tridentReturnAcceleration);
    }

    default void modifyTridentSpinAttackStrength(Random random, int level, MutableFloat tridentSpinAttackStrength) {
        this.modifyValue(EnchantmentEffectComponentTypes.TRIDENT_SPIN_ATTACK_STRENGTH, random, level, tridentSpinAttackStrength);
    }

    default void modifyFishingTimeReduction(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat fishingTimeReduction) {
        this.modifyValue(EnchantmentEffectComponentTypes.FISHING_TIME_REDUCTION, world, level, stack, user, fishingTimeReduction);
    }

    default void modifyFishingLuckBonus(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat fishingLuckBonus) {
        this.modifyValue(EnchantmentEffectComponentTypes.FISHING_LUCK_BONUS, world, level, stack, user, fishingLuckBonus);
    }

    default void modifyDamage(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat damage) {
        this.modifyValue(EnchantmentEffectComponentTypes.DAMAGE, world, level, stack, user, damageSource, damage);
    }

    default void modifySmashDamagePerFallenBlock(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat smashDamagePerFallenBlock) {
        this.modifyValue(EnchantmentEffectComponentTypes.SMASH_DAMAGE_PER_FALLEN_BLOCK, world, level, stack, user, damageSource, smashDamagePerFallenBlock);
    }

    default void modifyKnockback(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat knockback) {
        this.modifyValue(EnchantmentEffectComponentTypes.KNOCKBACK, world, level, stack, user, damageSource, knockback);
    }

    default void modifyArmorEffectiveness(ServerWorld world, int level, ItemStack stack, Entity user, DamageSource damageSource, MutableFloat armorEffectiveness) {
        this.modifyValue(EnchantmentEffectComponentTypes.ARMOR_EFFECTIVENESS, world, level, stack, user, damageSource, armorEffectiveness);
    }

    default void modifyProjectileCount(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat projectileCount) {
        this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_COUNT, world, level, stack, user, projectileCount);
    }

    default void modifyProjectileSpread(ServerWorld world, int level, ItemStack stack, Entity user, MutableFloat projectileSpread) {
        this.modifyValue(EnchantmentEffectComponentTypes.PROJECTILE_SPREAD, world, level, stack, user, projectileSpread);
    }

    default void modifyCrossbowChargeTime(Random random, int level, MutableFloat crossbowChargeTime) {
        this.modifyValue(EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, random, level, crossbowChargeTime);
    }

    default void applyLocationBasedEffects(ServerWorld world, int level, EnchantmentEffectContext context, LivingEntity user) {
        if (context.slot() != null && !this.slotMatches(context.slot())) {
            Set<EnchantmentLocationBasedEffect> effects = user.getLocationBasedEffects().remove(this);
            if (effects != null) {
                effects.forEach((effect) -> effect.remove(context, user, user.getPos(), level));
            }
        } else {
            Set<EnchantmentLocationBasedEffect> effects = user.getLocationBasedEffects().get(this);

            for(EnchantmentEffectEntry<EnchantmentLocationBasedEffect> conditionaleffect : this.getEffect(EnchantmentEffectComponentTypes.LOCATION_CHANGED)) {
                EnchantmentLocationBasedEffect enchantmentlocationbasedeffect = (EnchantmentLocationBasedEffect)conditionaleffect.effect();
                boolean flag = effects != null && effects.contains(enchantmentlocationbasedeffect);
                if (conditionaleffect.test(Enchantment.createEnchantedLocationLootContext(world, level, user, flag))) {
                    if (!flag) {
                        if (effects == null) {
                            effects = new ObjectArraySet();
                            user.getLocationBasedEffects().put(this, effects);
                        }

                        effects.add(enchantmentlocationbasedeffect);
                    }

                    enchantmentlocationbasedeffect.apply(world, level, context, user, user.getPos(), !flag);
                } else if (effects != null && effects.remove(enchantmentlocationbasedeffect)) {
                    enchantmentlocationbasedeffect.remove(context, user, user.getPos(), level);
                }
            }

            if (effects != null && effects.isEmpty()) {
                user.getLocationBasedEffects().remove(this);
            }
        }

    }

    default void removeLocationBasedEffects(int level, EnchantmentEffectContext context, LivingEntity user) {
        Set<EnchantmentLocationBasedEffect> set = user.getLocationBasedEffects().remove(this);
        if (set == null) return;

        for(EnchantmentLocationBasedEffect effect : set) {
            effect.remove(context, user, user.getPos(), level);
        }
    }


    interface Definition {
        int getMaxTier();
        List<AttributeModifierSlot> getSlots();
    }

}
