package com.huang.neverhungry;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;  // ← 添加这一行！

public class FullnessCharmHandler {

    // 这个方法会在每个游戏刻被调用（每秒20次）
    public static void tick(PlayerEntity player) {
        // 如果玩家不在生存/冒险模式，或已经死亡，不处理
        if (player.isCreative() || player.isSpectator() || player.isDead()) {
            return;
        }

        // 检查玩家背包里是否有我们的徽章（直接引用主类中的物品）
        // 正确写法：将 Item 包装成 ItemStack
        boolean hasCharm = player.getInventory().contains(new ItemStack(Never_hungry.FULLNESS_CHARM));
        if (hasCharm) {
            // 如果饥饿值没有满，就补充1点（半格饥饿值），并消耗一点食物饱和度
            if (player.getHungerManager().getFoodLevel() < 20) {
                player.getHungerManager().add(1, 0.1f);
            }
        }
    }
}