package com.huang.neverhungry;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Never_hungry implements ModInitializer {
	public static final String MOD_ID = "never_hungry";
	public static final Logger LOGGER = LoggerFactory.getLogger("never_hungry");

	// 饱食徽章
	public static final Item FULLNESS_CHARM = new Item(new Item.Settings().maxCount(1));

	// 秒杀剑
	public static final InstantKillSword INSTANT_KILL_SWORD = new InstantKillSword();

	@Override
	public void onInitialize() {
		// 注册饱食徽章
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "fullness_charm"), FULLNESS_CHARM);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> entries.add(FULLNESS_CHARM));

		// 注册秒杀剑
		Registry.register(Registries.ITEM, new Identifier(MOD_ID, "instant_kill_sword"), INSTANT_KILL_SWORD);
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(INSTANT_KILL_SWORD));

		// 监听方块破坏事件，强制破坏基岩
		PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
			if (player.getMainHandStack().getItem() == INSTANT_KILL_SWORD) {
				if (state.getBlock() == Blocks.BEDROCK) {
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					System.out.println("💥 基岩被秒挖了！");
					return false;
				}
			}
			return true;
		});

		// 服务器 Tick 事件（饱食徽章逻辑）
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (PlayerEntity player : server.getPlayerManager().getPlayerList()) {
				FullnessCharmHandler.tick(player);
			}
		});

		LOGGER.info("========================================");
		LOGGER.info("永远不饿模组已加载！");
		LOGGER.info("饱食徽章 ID: never_hungry:fullness_charm");
		LOGGER.info("秒杀剑 ID: never_hungry:instant_kill_sword");
		LOGGER.info("========================================");
		System.out.println("永远不饿模组已加载！");
	}
}