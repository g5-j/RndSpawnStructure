package com.reachmod;

import com.reachmod.config.ModConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReachMod implements ModInitializer {
    public static final String MOD_ID = "reachmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Reach Mod...");
        // تحميل الإعدادات عند تشغيل المود
        ModConfig.getInstance().load();
    }
}
