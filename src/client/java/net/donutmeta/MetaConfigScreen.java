package net.donutmeta;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/** Vanilla-widget config screen, avoiding a mandatory configuration-library dependency. */
public final class MetaConfigScreen extends Screen {
    private final Screen parent;
    private TextFieldWidget threshold, minDelay, maxDelay, jitter, watched;
    public MetaConfigScreen(Screen parent) { super(Text.literal("Donut META configuration")); this.parent = parent; }
    @Override protected void init() {
        int left = width / 2 - 100;
        threshold = field(left, 45, Double.toString(DonutMetaClient.CONFIG.minimumProfitMillions));
        minDelay = field(left, 75, Integer.toString(DonutMetaClient.CONFIG.minDelayMs));
        maxDelay = field(left, 105, Integer.toString(DonutMetaClient.CONFIG.maxDelayMs));
        jitter = field(left, 135, Integer.toString(DonutMetaClient.CONFIG.jitterMs));
        watched = field(left, 165, String.join(";", DonutMetaClient.CONFIG.watchedItems));
        addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Scraper: " + (DonutMetaClient.CONFIG.enabled ? "ON" : "OFF")), b -> { DonutMetaClient.CONFIG.enabled = !DonutMetaClient.CONFIG.enabled; b.setMessage(Text.literal("Scraper: " + (DonutMetaClient.CONFIG.enabled ? "ON" : "OFF"))); }).dimensions(left, 195, 200, 20).build());
        addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Save"), b -> save()).dimensions(left, 225, 98, 20).build());
        addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Cancel"), b -> close()).dimensions(left + 102, 225, 98, 20).build());
    }
    private TextFieldWidget field(int x, int y, String value) { TextFieldWidget field = new TextFieldWidget(textRenderer, x, y, 200, 20, Text.empty()); field.setText(value); addDrawableChild(field); return field; }
    private void save() {
        try {
            DonutMetaClient.CONFIG.minimumProfitMillions = Math.max(0, Double.parseDouble(threshold.getText()));
            DonutMetaClient.CONFIG.minDelayMs = Math.max(250, Integer.parseInt(minDelay.getText()));
            DonutMetaClient.CONFIG.maxDelayMs = Math.max(DonutMetaClient.CONFIG.minDelayMs, Integer.parseInt(maxDelay.getText()));
            DonutMetaClient.CONFIG.jitterMs = Math.max(0, Integer.parseInt(jitter.getText()));
            DonutMetaClient.CONFIG.watchedItems = java.util.Arrays.stream(watched.getText().split(";")).map(String::trim).filter(s -> !s.isEmpty()).toList();
            DonutMetaClient.CONFIG.save(); close();
        } catch (NumberFormatException ignored) { }
    }
    @Override public void close() { if (client != null) client.setScreen(parent); }
    @Override public void render(net.minecraft.client.gui.DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta); context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 16, 0xFFFFFF);
        String[] labels = { "Minimum profit (millions)", "Minimum delay (ms)", "Maximum delay (ms)", "Jitter (ms)", "Watched queries (; separated)" };
        for (int i = 0; i < labels.length; i++) context.drawTextWithShadow(textRenderer, labels[i], width / 2 - 100, 32 + i * 30, 0xCCCCCC);
    }
}
