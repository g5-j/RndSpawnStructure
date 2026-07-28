package com.reachmod.client.gui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;

public class ColorButtonWidget extends PressableWidget {
    private int backgroundColor;
    private final int borderColor;
    private final ButtonWidget.PressAction onPressAction;

    public ColorButtonWidget(int x, int y, int width, int height, Text message, ButtonWidget.PressAction onPressAction, int backgroundColor, int borderColor) {
        super(x, y, width, height, message);
        this.onPressAction = onPressAction;
        this.backgroundColor = backgroundColor;
        this.borderColor = borderColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public void onPress() {
        if (this.onPressAction != null) {
            this.onPressAction.onPress(null);
        }
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        if (!this.visible) return;

        int colorToRender = this.active ? (this.isHovered() ? adjustBrightness(backgroundColor, 1.2f) : backgroundColor) : 0xFF444444;

        // رسم خلفية الزر
        context.fill(getX(), getY(), getX() + width, getY() + height, colorToRender);

        // رسم إطار الزر
        context.fill(getX(), getY(), getX() + width, getY() + 1, borderColor);
        context.fill(getX(), getY() + height - 1, getX() + width, getY() + height, borderColor);
        context.fill(getX(), getY(), getX() + 1, getY() + height, borderColor);
        context.fill(getX() + width - 1, getY(), getX() + width, getY() + height, borderColor);

        // رسم النص داخل الزر
        int textColor = this.active ? 0xFFFFFFFF : 0xFFA0A0A0;
        context.drawCenteredTextWithShadow(
                MinecraftClient.getInstance().textRenderer,
                getMessage(),
                getX() + width / 2,
                getY() + (height - 8) / 2,
                textColor
        );
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
        this.appendDefaultNarrations(builder);
    }

    private int adjustBrightness(int color, float factor) {
        int a = (color >> 24) & 0xFF;
        int r = Math.min(255, (int) (((color >> 16) & 0xFF) * factor));
        int g = Math.min(255, (int) (((color >> 8) & 0xFF) * factor));
        int b = Math.min(255, (int) ((color & 0xFF) * factor));
        return (a << 24) | (r << 16) | (g << 8) | b;
    }
}
