package com.wayfarer;

import com.wayfarer.config.WayfarerConfig;

public class CommonClass {
    public static void init() {
        Constants.LOG.info("Initializing Wayfarer...");
        WayfarerConfig.load();

        Constants.LOG.info("Wayfarer successfully loaded.");
    }
}
