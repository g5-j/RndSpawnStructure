package com.reachmod;

import com.reachmod.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReachMod implements ModInitializer {
    public static final String MOD_ID = "reachmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ReachNetworking.registerServerPackets();

        // Clean up player data on disconnect
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ReachNetworking.removePlayer(handler.getPlayer().getUuid());
        });
    }
}
