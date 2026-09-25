package net.donutmeta;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Persisted, deliberately small configuration. Prices are stored in millions for clear UI input. */
public final class MetaConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("donut-meta.json");
    public boolean enabled = true;
    public double minimumProfitMillions = 5.0;
    public int minDelayMs = 1100;
    public int maxDelayMs = 1900;
    public int jitterMs = 350;
    public boolean autoScanHighValue = false;
    public List<String> watchedItems = new ArrayList<>(List.of("elytra unbreaking 3 mending", "diamond sword sharpness 5 unbreaking 3 mending"));

    public static MetaConfig load() {
        try {
            if (Files.exists(FILE)) return GSON.fromJson(Files.readString(FILE), MetaConfig.class);
        } catch (IOException | RuntimeException ignored) { }
        return new MetaConfig();
    }

    public void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(this));
        } catch (IOException ignored) { }
    }
}
