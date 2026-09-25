package net.donutmeta;

import net.minecraft.client.gui.DrawContext;

/** Minimal HUD shown only while the collection loop is active. */
public final class MetaHud {
    private MetaHud() { }
    public static void render(DrawContext context, MetaScraper scraper) {
        int x = 6, y = 6;
        context.fill(x - 3, y - 3, x + 260, y + 18 + Math.min(5, scraper.results().size()) * 10, 0xB0000000);
        context.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer, "Donut META • scanning (G: panel)", x, y, 0xFFFFFF);
        y += 12;
        int rows = 0;
        for (MarketListing listing : scraper.results().values()) {
            if (rows++ == 5) break;
            String prefix = listing.isMeta(DonutMetaClient.CONFIG) ? "META " : "";
            context.drawTextWithShadow(net.minecraft.client.MinecraftClient.getInstance().textRenderer,
                    prefix + listing.query() + "  " + format(listing.profit()), x, y, listing.isMeta(DonutMetaClient.CONFIG) ? 0x55FF55 : 0xDDDDDD);
            y += 10;
        }
    }
    static String format(long value) { return String.format("$%,d", value); }
}
