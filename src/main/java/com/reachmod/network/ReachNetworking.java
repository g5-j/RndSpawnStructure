package com.reachmod.network;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReachNetworking {
    public static final Identifier C2S_REACH_SYNC_PACKET = new Identifier("reachmod", "reach_sync");
    
    // Server-side storage for each player's active reach distance
    private static final Map<UUID, Double> PLAYER_REACH_MAP = new HashMap<>();

    // Register server-side packet listener
    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(C2S_REACH_SYNC_PACKET, (server, player, handler, buf, responseSender) -> {
            double requestedReach = buf.readDouble();
            
            // Server-side sanity cap (e.g., max 20 blocks to prevent excessive values)
            double sanitizedReach = Math.min(requestedReach, 20.0);
            
            server.execute(() -> {
                PLAYER_REACH_MAP.put(player.getUuid(), sanitizedReach);
            });
        });
    }

    // Client-side method to send the reach distance to the server
    public static void sendReachSyncPacket(double reachDistance) {
        if (ClientPlayNetworking.canSend(C2S_REACH_SYNC_PACKET)) {
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeDouble(reachDistance);
            ClientPlayNetworking.send(C2S_REACH_SYNC_PACKET, buf);
        }
    }

    // Retrieve player's server-side reach (defaults to vanilla 4.5 if not sent)
    public static double getPlayerReach(UUID playerUuid) {
        return PLAYER_REACH_MAP.getOrDefault(playerUuid, 4.5);
    }

    public static void removePlayer(UUID playerUuid) {
        PLAYER_REACH_MAP.remove(playerUuid);
    }
}
