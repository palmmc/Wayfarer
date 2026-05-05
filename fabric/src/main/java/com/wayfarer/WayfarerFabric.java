package com.wayfarer;

import com.wayfarer.network.S2CWaypointPacket;
import java.lang.reflect.Method;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class WayfarerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        try {
            Method playS2C = PayloadTypeRegistry.class.getMethod("playS2C");
            PayloadTypeRegistry registry = (PayloadTypeRegistry) playS2C.invoke(null);
            registry.register(S2CWaypointPacket.TYPE, S2CWaypointPacket.STREAM_CODEC);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
