package com.catroidvania.gearhelper;

import com.fox2code.foxloader.config.ConfigEntry;
import com.fox2code.foxloader.loader.Mod;

public class GearHelper extends Mod {
    public static final GearHelperConfig CONFIG = new GearHelperConfig();

    @Override
    public void onPreInit() {
        this.setConfigObject(CONFIG);
    }

    public static class GearHelperConfig {
        @ConfigEntry(configName = "Enabled")
        public boolean enabled = true;
    }
}
