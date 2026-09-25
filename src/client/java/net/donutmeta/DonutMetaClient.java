package net.donutmeta;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/** Client entrypoint. This mod intentionally performs read-only price collection. */
public final class DonutMetaClient implements ClientModInitializer {
    public static final String MOD_ID = "donutmeta";
    public static final MetaConfig CONFIG = MetaConfig.load();
    public static final MetaScraper SCRAPER = new MetaScraper(CONFIG);
    private static KeyBinding startKey;
    private static KeyBinding stopKey;
    private static KeyBinding panelKey;

    @Override
    public void onInitializeClient() {
        startKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.donutmeta.start", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.category.donutmeta"));
        stopKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.donutmeta.stop", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_F, "key.category.donutmeta"));
        panelKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.donutmeta.panel", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category.donutmeta"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (startKey.wasPressed()) SCRAPER.start(client);
            while (stopKey.wasPressed()) SCRAPER.stop(client);
            while (panelKey.wasPressed()) client.setScreen(new MetaPanelScreen(client.currentScreen));
            SCRAPER.tick(client);
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (SCRAPER.isRunning()) MetaHud.render(drawContext, SCRAPER);
        });
    }
}
