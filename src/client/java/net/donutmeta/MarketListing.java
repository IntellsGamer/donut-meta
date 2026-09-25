package net.donutmeta;

/** A read-only snapshot of one configured market query. */
public record MarketListing(String query, long orderBuy, long auctionSell, int delivered, int requested) {
    public long profit() { return auctionSell > 0 && orderBuy > 0 ? auctionSell - orderBuy : 0; }
    public boolean isMeta(MetaConfig config) { return profit() >= (long) (config.minimumProfitMillions * 1_000_000L); }
}
