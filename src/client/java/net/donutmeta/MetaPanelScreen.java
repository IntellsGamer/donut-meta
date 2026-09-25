package net.donutmeta;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

/** Live read-only results panel; configuration remains available through Mod Menu. */
public final class MetaPanelScreen extends Screen {
    private final Screen parent;
    public MetaPanelScreen(Screen parent) { super(Text.literal("Donut META")); this.parent = parent; }
    @Override protected void init() {
        addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Configure"), b -> client.setScreen(new MetaConfigScreen(this))).dimensions(width / 2 - 154, height - 28, 150, 20).build());
        addDrawableChild(net.minecraft.client.gui.widget.ButtonWidget.builder(Text.literal("Done"), b -> close()).dimensions(width / 2 + 4, height - 28, 150, 20).build());
    }
    @Override public void close() { if (client != null) client.setScreen(parent); }
    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 18, 0xFFFFFF);
        int y = 45;
        context.drawTextWithShadow(textRenderer, "Query", 20, y, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "Order buy", width / 2 - 80, y, 0xAAAAAA);
        context.drawTextWithShadow(textRenderer, "AH sell / profit", width - 180, y, 0xAAAAAA);
        y += 14;
        for (MarketListing entry : DonutMetaClient.SCRAPER.results().values()) {
            context.drawTextWithShadow(textRenderer, entry.query(), 20, y, entry.isMeta(DonutMetaClient.CONFIG) ? 0x55FF55 : 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, MetaHud.format(entry.orderBuy()), width / 2 - 80, y, 0xFFFFFF);
            context.drawTextWithShadow(textRenderer, MetaHud.format(entry.auctionSell()) + " / " + MetaHud.format(entry.profit()), width - 180, y, 0xFFFFFF);
            y += 14;
        }
        if (y == 59) context.drawCenteredTextWithShadow(textRenderer, "No results yet. Start on donutsmp.net with R.", width / 2, y + 12, 0xAAAAAA);
    }
}
