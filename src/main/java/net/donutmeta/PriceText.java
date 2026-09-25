package net.donutmeta;

import java.util.OptionalLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Parser for the visible DonutSMP-style tooltip strings, e.g. "$ 335M each" and "0/8". */
public final class PriceText {
    private static final Pattern MONEY = Pattern.compile("\\$\\s*([0-9][0-9,]*(?:\\.[0-9]+)?)\\s*([kmbt]?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern DELIVERY = Pattern.compile("(?:delivered\\s*)?(\\d+)\\s*/\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
    private PriceText() { }
    public static OptionalLong money(String text) {
        Matcher match = MONEY.matcher(text.replace(" ", ""));
        if (!match.find()) return OptionalLong.empty();
        double value = Double.parseDouble(match.group(1).replace(",", ""));
        long multiplier = switch (match.group(2).toLowerCase()) { case "k" -> 1_000L; case "m" -> 1_000_000L; case "b" -> 1_000_000_000L; case "t" -> 1_000_000_000_000L; default -> 1L; };
        return OptionalLong.of((long) (value * multiplier));
    }
    public static int[] delivery(String text) {
        Matcher match = DELIVERY.matcher(text);
        return match.find() ? new int[] { Integer.parseInt(match.group(1)), Integer.parseInt(match.group(2)) } : new int[] { 0, 0 };
    }
}
