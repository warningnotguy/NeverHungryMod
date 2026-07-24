package com.huang.neverhungry;

import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Ingredient;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class InstantKillSword extends SwordItem {
    public InstantKillSword() {
        super(new ToolMaterial() {
            @Override
            public int getDurability() {
                return -1;
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return Float.MAX_VALUE;
            }

            @Override
            public float getAttackDamage() {
                return Float.MAX_VALUE;
            }

            @Override
            public int getMiningLevel() {
                return Integer.MAX_VALUE;
            }

            @Override
            public int getEnchantability() {
                return Integer.MAX_VALUE;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.ofItems(net.minecraft.item.Items.NETHERITE_INGOT);
            }
        }, 1, Float.MAX_VALUE, new Item.Settings().maxCount(1));  // 攻速也炸裂
    }

    @Override
    public boolean isDamageable() {
        return false;
    }

    // 💫 手持粒子效果
    @Override
    public void inventoryTick(ItemStack stack, World world, net.minecraft.entity.Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (world.isClient) return;
        if (!(entity instanceof PlayerEntity player)) return;

        boolean isHolding = player.getMainHandStack().getItem() == this || player.getOffHandStack().getItem() == this;
        if (!isHolding) return;

        if (world.getTime() % 5 == 0) {
            ServerWorld serverWorld = (ServerWorld) world;
            double x = player.getX() + (world.random.nextDouble() - 0.5) * 2.0;
            double y = player.getY() + world.random.nextDouble() * 1.5;
            double z = player.getZ() + (world.random.nextDouble() - 0.5) * 2.0;
            serverWorld.spawnParticles(ParticleTypes.INSTANT_EFFECT, x, y, z, 1, 0, 0, 0, 0);
        }
    }

    // ⚔️ 核心：秒杀 + 击杀特效（保留 onDeath(null) 让日志炸裂）
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity player) {
            if (target instanceof PlayerEntity targetPlayer) {
                if (targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                    return false;
                }
            }

            World world = target.getWorld();

            // 💥 击杀特效
            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                        target.getX(), target.getY() + 0.5, target.getZ(),
                        1, 0, 0, 0, 0);

                for (int i = 0; i < 50; i++) {
                    double dx = (world.random.nextDouble() - 0.5) * 2.0;
                    double dy = world.random.nextDouble() * 2.0;
                    double dz = (world.random.nextDouble() - 0.5) * 2.0;
                    serverWorld.spawnParticles(ParticleTypes.INSTANT_EFFECT,
                            target.getX(), target.getY() + 0.5, target.getZ(),
                            1, dx, dy, dz, 0);
                }
            }

            // ⚔️ 秒杀目标
            target.setHealth(0.0f);
            target.onDeath(null);
            System.out.println("⚔️ 一刀秒杀！目标: " + target.getName().getString());

            // 💀 范围攻击：获取周围 50 格内所有实体
            List<net.minecraft.entity.Entity> entities = target.getWorld().getOtherEntities(target, target.getBoundingBox().expand(50.0f));



            for (net.minecraft.entity.Entity entity : entities) {
                if (entity instanceof LivingEntity living) {
                    if (living instanceof PlayerEntity p && (p.isCreative() || p.isSpectator())) {
                        continue;
                    }
                    living.setHealth(0.0f);
                    living.onDeath(null);
                    System.out.println("💥 范围秒杀！波及: " + living.getName().getString());
                }
            }
        }
        return true;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}