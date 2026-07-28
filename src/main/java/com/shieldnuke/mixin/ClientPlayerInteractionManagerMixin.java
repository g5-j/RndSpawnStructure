package com.shieldnuke.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {

    @Unique
    private int shieldNuke$originalSlot = -1;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntityHead(PlayerEntity player, Entity target, CallbackInfo ci) {
        shieldNuke$originalSlot = -1;

        // 1. التحقق من أن الكائن المستهدف هو لاعب
        if (!(target instanceof PlayerEntity targetPlayer)) {
            return;
        }

        // 2. التحقق مما إذا كان اللاعب مستخدماً للشيلد (سواء اليد اليمنى أو اليسرى مع زر الحماية)
        if (!targetPlayer.isBlocking()) {
            return;
        }

        // 3. البحث عن أي فأس في الـ Hotbar (الخانات من 0 إلى 8)
        int axeSlot = -1;
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getStack(i).getItem() instanceof AxeItem) {
                axeSlot = i;
                break;
            }
        }

        // 4. إذا تم العثور على فأس ولم نكن ممسكين به حالياً
        if (axeSlot != -1 && axeSlot != player.getInventory().selectedSlot) {
            shieldNuke$originalSlot = player.getInventory().selectedSlot;

            // التبديل إلى الفأس وإرسال حزمة السيرفر فوراً
            player.getInventory().selectedSlot = axeSlot;
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(axeSlot));
            }
        }
    }

    @Inject(method = "attackEntity", at = @At("TAIL"))
    private void onAttackEntityTail(PlayerEntity player, Entity target, CallbackInfo ci) {
        // إعادة السلوت إلى وضعه الأصلي بعد تنفيذ الضربة مباشرة
        if (shieldNuke$originalSlot != -1) {
            player.getInventory().selectedSlot = shieldNuke$originalSlot;
            if (MinecraftClient.getInstance().getNetworkHandler() != null) {
                MinecraftClient.getInstance().getNetworkHandler().sendPacket(new UpdateSelectedSlotC2SPacket(shieldNuke$originalSlot));
            }
            shieldNuke$originalSlot = -1;
        }
    }
}
