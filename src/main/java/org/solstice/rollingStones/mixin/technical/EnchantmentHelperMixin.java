package org.solstice.rollingStones.mixin.technical;

import com.mojang.datafixers.util.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.component.ComponentType;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.effect.EnchantmentEffectTarget;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.apache.commons.lang3.mutable.MutableObject;
import org.solstice.rollingStones.content.effectHolder.EffectHolderHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void forEachEnchantment(ItemStack stack, EnchantmentHelper.Consumer consumer) {
        EffectHolderHelper.forEachEffectHolder(stack, consumer);
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void forEachEnchantment(ItemStack stack, EquipmentSlot slot, LivingEntity entity, EnchantmentHelper.ContextAwareConsumer consumer) {
        EffectHolderHelper.forEachEffectHolder(stack, slot, entity, consumer);
    }







    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getItemDamage(ServerWorld world, ItemStack stack, int base) {
        MutableFloat result = new MutableFloat((float)base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyItemDamage(world, level, stack, result)
        );
        return result.intValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getAmmoUse(ServerWorld world, ItemStack weaponStack, ItemStack projectileStack, int base) {
        MutableFloat result = new MutableFloat((float)base);
        EffectHolderHelper.forEachEffectHolder(weaponStack, (effectHolder, level) ->
                effectHolder.value().modifyAmmoUse(world, level, projectileStack, result)
        );
        return result.intValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getBlockExperience(ServerWorld world, ItemStack stack, int base) {
        MutableFloat result = new MutableFloat((float)base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyBlockExperience(world, level, stack, result)
        );
        return result.intValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getMobExperience(ServerWorld world, @Nullable Entity attacker, Entity target, int base) {
        if (!(attacker instanceof LivingEntity living)) return base;

        MutableFloat result = new MutableFloat((float)base);
        EffectHolderHelper.forEachEffectHolder(living, (effectHolder, level, context) ->
                effectHolder.value().modifyMobExperience(world, level, context.stack(), target, result)
        );
        return result.intValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static boolean isInvulnerableTo(ServerWorld world, LivingEntity target, DamageSource source) {
        MutableBoolean result = new MutableBoolean();
        EffectHolderHelper.forEachEffectHolder(target, (effectHolder, level, context) ->
                result.setValue(result.isTrue() || effectHolder.value().hasDamageImmunityTo(world, level, target, source))
        );
        return result.isTrue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getProtectionAmount(ServerWorld world, LivingEntity target, DamageSource source) {
        MutableFloat result = new MutableFloat(0.0F);
        EffectHolderHelper.forEachEffectHolder(target, (effectHolder, level, context) ->
                effectHolder.value().modifyDamageProtection(world, level, context.stack(), target, source, result)
        );
        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getDamage(ServerWorld world, ItemStack stack, Entity target, DamageSource source, float base) {
        MutableFloat result = new MutableFloat(base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyDamage(world, level, stack, target, source, result)
        );
        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getSmashDamagePerFallenBlock(ServerWorld world, ItemStack stack, Entity target, DamageSource source, float base) {
        MutableFloat result = new MutableFloat(base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifySmashDamagePerFallenBlock(world, level, stack, target, source, result)
        );
        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getArmorEffectiveness(ServerWorld world, ItemStack stack, Entity target, DamageSource source, float base) {
        MutableFloat result = new MutableFloat(base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyArmorEffectiveness(world, level, stack, target, source, result)
        );
        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float modifyKnockback(ServerWorld world, ItemStack stack, Entity target, DamageSource damageSource, float baseKnockback) {
        MutableFloat result = new MutableFloat(baseKnockback);
        EffectHolderHelper.forEachEffectHolder(stack,(effectHolder, level) ->
                effectHolder.value().modifyKnockback(world, level, stack, target, damageSource, result)
        );
        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void onTargetDamaged(ServerWorld world, Entity target, DamageSource source, @Nullable ItemStack weaponStack) {
        if (target instanceof LivingEntity livingTarget) {
            EffectHolderHelper.forEachEffectHolder(livingTarget,
                (effectHolder, level, context) ->
                    effectHolder.value().onTargetDamaged(world, level, context, EnchantmentEffectTarget.VICTIM, target, source)
            );
        }

        if (weaponStack == null) return;

        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {
            EffectHolderHelper.forEachEffectHolder(weaponStack, EquipmentSlot.MAINHAND, livingAttacker,
                (effectHolder, level, context) ->
                    effectHolder.value().onTargetDamaged(world, level, context, EnchantmentEffectTarget.ATTACKER, target, source)
            );
        }
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void applyLocationBasedEffects(ServerWorld world, LivingEntity target) {
        EffectHolderHelper.forEachEffectHolder(target,
            (effectHolder, level, context) ->
                effectHolder.value().applyLocationBasedEffects(world, level, context, target)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void applyLocationBasedEffects(ServerWorld world, ItemStack stack, LivingEntity target, EquipmentSlot slot) {
        EffectHolderHelper.forEachEffectHolder(stack, slot, target,
            (effectHolder, level, context) ->
                effectHolder.value().applyLocationBasedEffects(world, level, context, target)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void removeLocationBasedEffects(LivingEntity target) {
        EffectHolderHelper.forEachEffectHolder(target,
            (effectHolder, level, context) ->
                effectHolder.value().removeLocationBasedEffects(level, context, target)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void removeLocationBasedEffects(ItemStack stack, LivingEntity target, EquipmentSlot slot) {
        EffectHolderHelper.forEachEffectHolder(stack, slot, target,
            (effectHolder, level, context) ->
                effectHolder.value().removeLocationBasedEffects(level, context, target)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void onTick(ServerWorld world, LivingEntity target) {
        EffectHolderHelper.forEachEffectHolder(target,
            (effectHolder, level, context) ->
                effectHolder.value().onTick(world, level, context, target)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getProjectileCount(ServerWorld world, ItemStack stack, Entity user, int base) {
        MutableFloat result = new MutableFloat((float)base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyProjectileCount(world, level, stack, user, result)
        );
        return Math.max(0, result.intValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getProjectileSpread(ServerWorld world, ItemStack stack, Entity target, float base) {
        MutableFloat result = new MutableFloat(base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyProjectileSpread(world, level, stack, target, result)
        );
        return Math.max(0.0F, result.floatValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getProjectilePiercing(ServerWorld world, ItemStack weaponStack, ItemStack projectileStack) {
        MutableFloat result = new MutableFloat(0.0F);
        EffectHolderHelper.forEachEffectHolder(weaponStack, (effectHolder, level) ->
                effectHolder.value().modifyProjectilePiercing(world, level, projectileStack, result)
        );
        return Math.max(0, result.intValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void onProjectileSpawned(ServerWorld world, ItemStack stack, PersistentProjectileEntity projectileEntity, Consumer<Item> onBreak) {
        Entity owner = projectileEntity.getOwner();
        LivingEntity livingOwner = owner instanceof LivingEntity ? (LivingEntity) owner : null;

        EnchantmentEffectContext context = new EnchantmentEffectContext(stack, null, livingOwner, onBreak);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().onProjectileSpawned(world, level, context, projectileEntity)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void onHitBlock(ServerWorld world, ItemStack stack, @Nullable LivingEntity user, Entity enchantedEntity, @Nullable EquipmentSlot slot, Vec3d pos, BlockState state, Consumer<Item> onBreak) {
        EnchantmentEffectContext context = new EnchantmentEffectContext(stack, slot, user, onBreak);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().onHitBlock(world, level, context, enchantedEntity, pos, state)
        );
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getRepairWithXp(ServerWorld world, ItemStack stack, int base) {
        MutableFloat result = new MutableFloat((float)base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyRepairWithXp(world, level, stack, result)
        );
        return Math.max(0, result.intValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getEquipmentDropChance(ServerWorld world, LivingEntity target, DamageSource source, float base) {
        MutableFloat result = new MutableFloat(base);

        Random random = target.getRandom();
        EffectHolderHelper.forEachEffectHolder(target, (effectHolder, level, context) -> {
            LootContext lootcontext = Enchantment.createEnchantedDamageLootContext(world, level, target, source);
            effectHolder.value().getEffect(EnchantmentEffectComponentTypes.EQUIPMENT_DROPS).forEach((effect) -> {
                if (effect.enchanted() == EnchantmentEffectTarget.VICTIM && effect.affected() == EnchantmentEffectTarget.VICTIM && effect.test(lootcontext))
                    result.setValue(effect.effect().apply(level, random, result.floatValue()));
            });
        });
        Entity attacker = source.getAttacker();
        if (attacker instanceof LivingEntity livingAttacker) {
            EffectHolderHelper.forEachEffectHolder(livingAttacker, (effectHolder, level, context) -> {
                LootContext lootcontext = Enchantment.createEnchantedDamageLootContext(world, level, target, source);
                effectHolder.value().getEffect(EnchantmentEffectComponentTypes.EQUIPMENT_DROPS).forEach((effect) -> {
                    if (effect.enchanted() == EnchantmentEffectTarget.ATTACKER && effect.affected() == EnchantmentEffectTarget.VICTIM && effect.test(lootcontext))
                        result.setValue(effect.effect().apply(level, random, result.floatValue()));
                });
            });
        }

        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void applyAttributeModifiers(ItemStack stack, AttributeModifierSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> consumer) {
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
            effectHolder.value().getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach(effect -> {
                if (effectHolder.value().getDefinition().getSlots().contains(slot))
                    consumer.accept(effect.attribute(), effect.createAttributeModifier(level, slot));
            }
        ));
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static void applyAttributeModifiers(ItemStack stack, EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeModifierConsumer) {
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
            effectHolder.value().getEffect(EnchantmentEffectComponentTypes.ATTRIBUTES).forEach(effect -> {
                if (effectHolder.value().slotMatches(slot))
                    attributeModifierConsumer.accept(effect.attribute(), effect.createAttributeModifier(level, slot));
            }
        ));
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getFishingLuckBonus(ServerWorld world, ItemStack stack, Entity user) {
        MutableFloat result = new MutableFloat(0.0F);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyFishingLuckBonus(world, level, stack, user, result)
        );
        return Math.max(0, result.intValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getFishingTimeReduction(ServerWorld world, ItemStack stack, Entity user) {
        MutableFloat result = new MutableFloat(0.0F);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyFishingTimeReduction(world, level, stack, user, result)
        );
        return Math.max(0.0F, result.floatValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static int getTridentReturnAcceleration(ServerWorld world, ItemStack stack, Entity user) {
        MutableFloat result = new MutableFloat(0.0F);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyTridentReturnAcceleration(world, level, stack, user, result)
        );
        return Math.max(0, result.intValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getCrossbowChargeTime(ItemStack stack, LivingEntity user, float base) {
        MutableFloat result = new MutableFloat(base);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyCrossbowChargeTime(user.getRandom(), level, result)
        );
        return Math.max(0.0F, result.floatValue());
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static float getTridentSpinAttackStrength(ItemStack stack, LivingEntity user) {
        MutableFloat result = new MutableFloat(0.0F);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) ->
                effectHolder.value().modifyTridentSpinAttackStrength(user.getRandom(), level, result)
        );
        return result.floatValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite
    public static boolean hasAnyEnchantmentsWith(ItemStack stack, ComponentType<?> componentType) {
        MutableBoolean result = new MutableBoolean(false);
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) -> {
            if (effectHolder.value().getEffects().contains(componentType)) result.setTrue();
        });
        return result.booleanValue();
    }

    /**
     * @author Solstice
     * @reason Use EffectHolderHelper
     */
    @Overwrite @Nullable
    public static <T> Pair<T, Integer> getEffectListAndLevel(ItemStack stack, ComponentType<T> componentType) {
        MutableObject<Pair<T, Integer>> result = new MutableObject<>();
        EffectHolderHelper.forEachEffectHolder(stack, (effectHolder, level) -> {
            if (result.getValue() == null || result.getValue().getSecond() < level) {
                T t = effectHolder.value().getEffects().get(componentType);
                if (t != null) result.setValue(Pair.of(t, level));
            }
        });
        return result.getValue();
    }

}
