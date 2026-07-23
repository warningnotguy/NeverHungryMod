package com.huang.neverhungry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class InstantKillSword extends SwordItem {
    public InstantKillSword() {
        super(new ToolMaterial() {
            @Override
            public int getDurability() {
                return 999999; // 超大耐久度，基本用不完
            }

            @Override
            public float getMiningSpeedMultiplier() {
                return 9.0f;
            }

            @Override
            public float getAttackDamage() {
                return 1.0f;
            }

            @Override
            public int getMiningLevel() {
                return 4;
            }

            @Override
            public int getEnchantability() {
                return 15;
            }

            @Override
            public Ingredient getRepairIngredient() {
                return Ingredient.ofItems(net.minecraft.item.Items.NETHERITE_INGOT);
            }
        }, 1, -2.4f, new Item.Settings().maxCount(1));
    }

    // 核心逻辑：攻击任何实体时直接秒杀
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        // 调用父类方法（但我们要阻止耐久度消耗）
        // 不调用 super.postHit，避免耐久度消耗

        // 检查攻击者是否是玩家
        if (attacker instanceof PlayerEntity player) {
            // 如果是玩家且目标在创造/旁观模式，不秒杀（防止误伤队友）
            if (target instanceof PlayerEntity targetPlayer) {
                if (targetPlayer.isCreative() || targetPlayer.isSpectator()) {
                    return false;
                }
            }

            // 直接设置目标生命值为 0（秒杀任何 LivingEntity）
            target.setHealth(0.0f);
            // 触发死亡动画
            target.onDeath(null);

            System.out.println("⚔️ 一刀秒杀！目标: " + target.getName().getString());
        }

        // 不消耗耐久度
        return true;
    }

    // 右键触发（可选）
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return TypedActionResult.pass(user.getStackInHand(hand));
    }
}