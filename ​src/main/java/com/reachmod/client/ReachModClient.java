package com.reachmod.client;

import com.reachmod.client.gui.ReachScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ReachModClient implements ClientModInitializer {
    public static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        // تسجيل زر الخيار X كـ Keybind قابل للتعديل
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.reachmod.open_gui",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "category.reachmod.general"
        ));

        // الاستماع لضغطات الزر لفتح وإغلاق الواجهة
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                if (client.currentScreen instanceof ReachScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new ReachScreen());
                }
            }
        });
    }
}
