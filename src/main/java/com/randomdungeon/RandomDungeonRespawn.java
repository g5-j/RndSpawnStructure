package com.randomdungeon;

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
        // الاستماع لحدث إعادة إحياء اللاعب بعد الموت
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (alive) return; // ننفذ الكود فقط في حال كان الإحياء ناتجاً عن الموت

            MinecraftServer server = newPlayer.getServer();
            if (server == null) return;

            // 1. اختيار عالم عشوائي من العوالم المتاحة (فانيلا أو مودات)
            List<ServerWorld> worlds = new ArrayList<>();
            server.getWorlds().forEach(worlds::add);
            if (worlds.isEmpty()) return;

            ServerWorld targetWorld = worlds.get(ThreadLocalRandom.current().nextInt(worlds.size()));

            // 2. جلب جميع الدنجنات والهياكل المسجلة في السيرفر (فانيلا + مودات + داتاباك)
            Registry<Structure> structureRegistry = targetWorld.getRegistryManager().get(RegistryKeys.STRUCTURE);
            List<RegistryEntry<Structure>> allStructures = new ArrayList<>();
            structureRegistry.getIndexedEntries().forEach(allStructures::add);

            if (allStructures.isEmpty()) return;

            // خلط الدنجنات عشوائياً لتجربتها
            Collections.shuffle(allStructures);

            // 3. البحث عن موقع دنجن والرسپنة داخله
            for (RegistryEntry<Structure> structureEntry : allStructures) {
                // نقطة بحث عشوائية داخل نطاق 5000 بلوكة
                BlockPos searchOrigin = new BlockPos(
                        ThreadLocalRandom.current().nextInt(-5000, 5000),
                        64,
                        ThreadLocalRandom.current().nextInt(-5000, 5000)
                );

                // البحث عن أقرب إحداثيات للدنجن المحدد
                BlockPos structurePos = targetWorld.locateStructure(
                        RegistryEntryList.of(structureEntry),
                        searchOrigin,
                        100, // نطاق البحث بـ Chunks
                        false
                );

                if (structurePos != null) {
                    // العثور على ارتفاع آمن (مساحة للهواء فوق بلوكة صلبة)
                    BlockPos safePos = findSafeY(targetWorld, structurePos);

                    // نقل اللاعب فوراً إلى الدنجن
                    newPlayer.teleport(
                            targetWorld,
                            safePos.getX() + 0.5,
                            safePos.getY(),
                            safePos.getZ() + 0.5,
                            newPlayer.getYaw(),
                            newPlayer.getPitch()
                    );
                    break; // تم النقل بنجاح، نخرج من الحلقة
                }
            }
        });
    }

    // دالة لحساب ارتفاع آمن للرسپنة داخل/فوق الدنجن لعدم الخنق داخل البلوكات
    private static BlockPos findSafeY(ServerWorld world, BlockPos pos) {
        int x = pos.getX();
        int z = pos.getZ();
        int minY = world.getBottomY() + 5;
        int maxY = world.getTopY() - 5;

        // مسح رأسي للبحث عن مساحة هواء بارتفاع بلوكتين فوق بلوكة صلبة
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

        // في حال عدم العثور على مكان مغلق وآمن، يتم الرسپنة على السطح المباشر فوق الدنجن
        return world.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos);
    }
}
