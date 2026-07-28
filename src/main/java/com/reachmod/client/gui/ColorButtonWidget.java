package com.reachmod.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ColorButtonWidget extends ButtonWidget {
    private int backgroundColor;
    private final int borderColor;

    public ColorButtonWidget(int x, int y, int width, int height, Text message, PressAction onPress, int backgroundColor, int borderColor) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int colorToRender = this.active ? (this.isHovered() ? adjustBrightness(backgroundColor, 1.2f) : backgroundColor) : 0xFF444444;

        // رسم خلفية الزر بالكود
        context.fill(getX(), getY(), getX() + width, getY() + height, colorToRender);

        // رسم إطار للزر
        context.fill(getX(), getY(), getX() + width, getY() + 1, borderColor);
        context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, borderColor);
        context.fill(getX(), getY(), getX() + 1, getY() + height, borderColor);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, borderColor);

        // رسم النص المكتوب على الزر
        int textColor = this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                getMessage(),
                getX() + width / 2,
                getY() + (height - 8) / 2,
                textColor
        );
    }

    private int adjustBrightness(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
