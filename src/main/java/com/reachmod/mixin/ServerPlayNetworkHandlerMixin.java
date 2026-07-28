package com.reachmod.mixin;

import com.reachmod.network.ReachNetworking;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    // 1. تعديل مسافة ضرب وتفاعل الكائنات (Entities)
    @ModifyConstant(
        method = "onPlayerInteractEntity",
        constant = @Constant(doubleValue = 36.0)
    )
    private double modifyEntityInteractionDistance(double original) {
        double customReach = ReachNetworking.getPlayerReach(player.getUuid());
        double totalReach = customReach + 1.5;
        return totalReach * totalReach;
    }

    // 2. تعديل مسافة التفاعل مع البلوكات بالزر الأيمن (وضع البلوكات / استخدام الأجهزة)
    @ModifyConstant(
        method = "onPlayerInteractBlock",
        constant = @Constant(doubleValue = 64.0)
    )
    private double modifyBlockInteractionDistance(double original) {
        double customReach = ReachNetworking.getPlayerReach(player.getUuid());
        double totalReach = customReach + 2.0;
        return totalReach * totalReach;
    }

    // 3. تعديل مسافة تكسير البلوكات بالزر الأيسر (Mining / Breaking)
    @ModifyConstant(
        method = "onPlayerAction",
        constant = @Constant(doubleValue = 36.0)
    )
    private double modifyBlockMiningDistance(double original) {
        double customReach = ReachNetworking.getPlayerReach(player.getUuid());
        double totalReach = customReach + 2.0;
        return totalReach * totalReach;
    }
}
