package com.randomdungeon;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.structure.Structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomDungeonRespawn implements ModInitializer {

    @Override
    public void onInitialize() {
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (alive) return;

            MinecraftServer server = newPlayer.getServer();
            if (server == null) return;

            // 1. اختيار عالم عشوائي من العوالم المحمّلة
            List<ServerWorld> worlds = new ArrayList<>();
            server.getWorlds().forEach(worlds::add);
            if (worlds.isEmpty()) return;

            ServerWorld targetWorld = worlds.get(ThreadLocalRandom.current().nextInt(worlds.size()));

            // 2. جلب الدنجنات والمباني باستخدام streamEntries()
            Registry<Structure> structureRegistry = targetWorld.getRegistryManager().get(RegistryKeys.STRUCTURE);
            List<RegistryEntry<Structure>> allStructures = structureRegistry.streamEntries().toList();

            if (allStructures.isEmpty()) return;

            List<RegistryEntry<Structure>> shuffledStructures = new ArrayList<>(allStructures);
            Collections.shuffle(shuffledStructures);

            // 3. البحث عن دنجن ورسپنة اللاعب داخله
            for (RegistryEntry<Structure> structureEntry : shuffledStructures) {
                BlockPos searchOrigin = new BlockPos(
                        ThreadLocalRandom.current().nextInt(-5000, 5000),
                        64,
                        ThreadLocalRandom.current().nextInt(-5000, 5000)
                );

                // locateStructure ترجع Pair في 1.20.1
                Pair<BlockPos, RegistryEntry<Structure>> result = targetWorld.locateStructure(
                        RegistryEntryList.of(structureEntry),
                        searchOrigin,
                        100,
                        false
                );

                if (result != null && result.getFirst() != null) {
                    BlockPos structurePos = result.getFirst();
                    BlockPos safePos = findSafeY(targetWorld, structurePos);

                    newPlayer.teleport(
                            targetWorld,
                            safePos.getX() + 0.5,
                            safePos.getY(),
                            safePos.getZ() + 0.5,
                            newPlayer.getYaw(),
                            newPlayer.getPitch()
                    );
                    break;
                }
            }
        });
    }

    private static BlockPos findSafeY(ServerWorld world, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        int minY = world.getBottomY() + 5;
        int maxY = world.getTopY() - 5;

        for (int y = minY; y < maxY; y++) {
            BlockPos checkPos = new BlockPos(x, y, z);
            BlockPos above1 = checkPos.up();
            BlockPos above2 = checkPos.up(2);

            if (world.getBlockState(checkPos).isSolidBlock(world, checkPos) &&
                world.getBlockState(above1).isAir() &&
                world.getBlockState(above2).isAir()) {
                return above1;
            }
        }

        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }
}
