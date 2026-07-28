package com.reachmod.client.gui;

import com.reachmod.config.ModConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class ReachScreen extends Screen {
    private ColorButtonWidget toggleButton;
    private TextFieldWidget reachField;
    private ColorButtonWidget applyButton;
    private ColorButtonWidget closeButton;

    private static final int GUI_WIDTH = 260;
    private static final int GUI_HEIGHT = 80;

    public ReachScreen() {
        super(Text.literal("Reach Mod Settings"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int guiLeft = centerX - (GUI_WIDTH / 2);
        int guiTop = centerY - (GUI_HEIGHT / 2);

        ModConfig config = ModConfig.getInstance();

        // 1. زر Enable / Disable
        int toggleWidth = 70;
        int toggleHeight = 20;
        int toggleX = guiLeft + 15;
        int toggleY = centerY - 10;

        int toggleColor = config.enabled ? 0xFF2E7D32 : 0xFFC62828; // أخضر / أحمر
        Text toggleText = Text.literal(config.enabled ? "Enabled" : "Disabled");

        this.toggleButton = new ColorButtonWidget(
                toggleX, toggleY, toggleWidth, toggleHeight,
                toggleText,
                button -> {
                    config.enabled = !config.enabled;
                    updateToggleButtonState();
                    config.save();
                },
                toggleColor,
                0xFFFFFFFF
        );

        // 2. Text Field لإدخال قيمة الـ Reach
        int fieldWidth = 60;
        int fieldHeight = 20;
        int fieldX = toggleX + toggleWidth + 10;
        int fieldY = centerY - 10;

        this.reachField = new TextFieldWidget(
                this.textRenderer,
                fieldX, fieldY, fieldWidth, fieldHeight,
                Text.literal("Reach Value")
        );
        this.reachField.setText(String.valueOf(config.reachDistance));
        // قبول الأرقام والأعداد العشرية فقط
        this.reachField.setTextPredicate(text -> text.matches("^\\d*\\.?\\d*$"));

        // 3. زر Apply
        int applyWidth = 50;
        int applyHeight = 20;
        int applyX = fieldX + fieldWidth + 10;
        int applyY = centerY - 10;

        this.applyButton = new ColorButtonWidget(
                applyX, applyY, applyWidth, applyHeight,
                Text.literal("Apply"),
                button -> applySettings(),
                0xFF1565C0, // أزرق
                0xFFFFFFFF
        );

        // 4. زر الإغلاق × في الزاوية العلوية اليمنى
        int closeSize = 16;
        int closeX = guiLeft + GUI_WIDTH - closeSize - 6;
        int closeY = guiTop + 6;

        this.closeButton = new ColorButtonWidget(
                closeX, closeY, closeSize, closeSize,
                Text.literal("×"),
                button -> this.close(),
                0xFFD32F2F, // أحمر
                0xFFFFFFFF
        );

        this.addDrawableChild(this.toggleButton);
        this.addDrawableChild(this.reachField);
        this.addDrawableChild(this.applyButton);
        this.addDrawableChild(this.closeButton);
    }

    private void updateToggleButtonState() {
        ModConfig config = ModConfig.getInstance();
        int toggleColor = config.enabled ? 0xFF2E7D32 : 0xFFC62828;
        this.toggleButton.setMessage(Text.literal(config.enabled ? "Enabled" : "Disabled"));
        this.toggleButton.setBackgroundColor(toggleColor);
    }

    private void applySettings() {
        try {
            double val = Double.parseDouble(this.reachField.getText());
            ModConfig.getInstance().reachDistance = val;
            ModConfig.getInstance().save();
        } catch (NumberFormatException ignored) {
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int guiLeft = centerX - (GUI_WIDTH / 2);
        int guiTop = centerY - (GUI_HEIGHT / 2);

        // تعتيم الخلفية
        this.renderBackground(context);

        // رسم إطار وخلفية الواجهة بالكود بالكامل (بدون صور)
        context.fill(guiLeft - 2, guiTop - 2, guiLeft + GUI_WIDTH + 2, guiTop + GUI_HEIGHT + 2, 0xFF444444);
        context.fill(guiLeft, guiTop, guiLeft + GUI_WIDTH, guiTop + GUI_HEIGHT, 0xF181818);

        // عنوان الواجهة
        context.drawCenteredTextWithShadow(
                this.textRenderer,
                this.title,
                centerX,
                guiTop + 10,
                0xFFFFFF
        );

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void close() {
        applySettings(); // حفظ تلقائي عند إغلاق النافذة
        super.close();
    }
}
