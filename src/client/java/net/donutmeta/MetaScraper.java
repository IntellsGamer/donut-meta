package net.donutmeta;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Random;

/**
 * A paced, read-only state machine. It never sends inventory clicks, purchase commands, or sell commands.
 * The random wait is a courtesy rate limit, not a mechanism intended to bypass server rules or detection.
 */
public final class MetaScraper {
    private enum Stage { IDLE, SEND_ORDER, READ_ORDER, SEND_AUCTION, READ_AUCTION }
    private final MetaConfig config;
    private final Random random = new Random();
    private final Map<String, MarketListing> results = new LinkedHashMap<>();
    private Stage stage = Stage.IDLE;
    private int index;
    private long nextActionAt;
    private long pendingOrder;
    private int delivered, requested;
    private boolean running;

    public MetaScraper(MetaConfig config) { this.config = config; }
    public boolean isRunning() { return running; }
    public Map<String, MarketListing> results() { return Map.copyOf(results); }

    public void start(MinecraftClient client) {
        if (!config.enabled || !isDonutServer(client)) return;
        running = true; index = 0; stage = Stage.SEND_ORDER; nextActionAt = System.currentTimeMillis();
    }
    public void stop(MinecraftClient client) {
        running = false; stage = Stage.IDLE; pendingOrder = 0; delivered = requested = 0;
    }
    public void tick(MinecraftClient client) {
        if (!running) return;
        if (!isDonutServer(client)) { stop(client); return; }
        if (System.currentTimeMillis() < nextActionAt) return;
        List<String> queries = config.watchedItems.stream().filter(s -> !s.isBlank()).toList();
        if (queries.isEmpty()) { stop(client); return; }
        if (index >= queries.size()) index = 0;
        String query = queries.get(index);
        switch (stage) {
            case SEND_ORDER -> { send(client, "order " + query); stage = Stage.READ_ORDER; waitForGui(); }
            case READ_ORDER -> {
                OrderData data = readOrder(client);
                // Ignore closed/fully-filled orders as requested.
                if (data.price > 0 && data.requested > data.delivered) { pendingOrder = data.price; delivered = data.delivered; requested = data.requested; stage = Stage.SEND_AUCTION; }
                else advance();
                nextActionAt = System.currentTimeMillis() + delay();
            }
            case SEND_AUCTION -> { send(client, "ah " + query); stage = Stage.READ_AUCTION; waitForGui(); }
            case READ_AUCTION -> {
                long sell = readLowestAuction(client);
                if (sell > 0) results.put(query, new MarketListing(query, pendingOrder, sell, delivered, requested));
                advance(); nextActionAt = System.currentTimeMillis() + delay();
            }
            default -> { }
        }
    }
    private void send(MinecraftClient client, String command) {
        if (client.player != null && client.player.networkHandler != null) client.player.networkHandler.sendChatCommand(command);
    }
    private void waitForGui() { nextActionAt = System.currentTimeMillis() + delay(); }
    private void advance() { index++; stage = Stage.SEND_ORDER; pendingOrder = 0; delivered = requested = 0; }
    private long delay() {
        int low = Math.max(250, Math.min(config.minDelayMs, config.maxDelayMs));
        int high = Math.max(low, config.maxDelayMs);
        int jitter = Math.max(0, config.jitterMs);
        return low + random.nextInt(high - low + 1) + (jitter == 0 ? 0 : random.nextInt(jitter * 2 + 1) - jitter);
    }
    private static boolean isDonutServer(MinecraftClient client) {
        if (client.getCurrentServerEntry() == null) return false;
        String host = client.getCurrentServerEntry().address.toLowerCase().split(":")[0];
        return host.equals("donutsmp.net") || host.endsWith(".donutsmp.net");
    }
    private OrderData readOrder(MinecraftClient client) {
        long price = 0; int[] progress = {0, 0};
        for (String line : visibleTooltipText(client)) {
            OptionalLong found = PriceText.money(line);
            if (found.isPresent() && price == 0) price = found.getAsLong();
            int[] foundProgress = PriceText.delivery(line);
            if (foundProgress[1] > 0) progress = foundProgress;
        }
        return new OrderData(price, progress[0], progress[1]);
    }
    private long readLowestAuction(MinecraftClient client) {
        long lowest = Long.MAX_VALUE;
        for (String line : visibleTooltipText(client)) {
            OptionalLong value = PriceText.money(line);
            if (value.isPresent()) lowest = Math.min(lowest, value.getAsLong());
        }
        return lowest == Long.MAX_VALUE ? 0 : lowest;
    }
    private static List<String> visibleTooltipText(MinecraftClient client) {
        Screen screen = client.currentScreen;
        if (screen == null || client.player == null || client.player.currentScreenHandler == null) return List.of();
        List<String> lines = new ArrayList<>();
        for (Slot slot : client.player.currentScreenHandler.slots) {
            ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            for (Text text : stack.getTooltip(Item.TooltipContext.DEFAULT, client.player, TooltipType.BASIC)) lines.add(text.getString());
        }
        return lines;
    }
    private record OrderData(long price, int delivered, int requested) { }
}
