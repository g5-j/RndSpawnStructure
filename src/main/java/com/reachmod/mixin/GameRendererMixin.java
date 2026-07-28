package com.reachmod.mixin;

import com.reachmod.config.ModConfig;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @ModifyConstant(method = "updateTargetedEntity", constant = @Constant(doubleValue = 6.0D))
    private double modifyExtendedReach(double original) {
        if (ModConfig.getInstance().enabled) {
            return ModConfig.getInstance().reachDistance;
        }
        return original;
    }
}
