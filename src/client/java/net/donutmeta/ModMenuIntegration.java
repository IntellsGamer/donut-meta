package net.donutmeta;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

/** Makes the same vanilla config screen available from Mod Menu when it is installed. */
public final class ModMenuIntegration implements ModMenuApi {
    @Override public ConfigScreenFactory<?> getModConfigScreenFactory() { return MetaConfigScreen::new; }
}
