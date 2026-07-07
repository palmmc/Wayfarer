package com.wayfarer;

import com.wayfarer.network.S2CWaypointPacket;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class WayfarerFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        CommonClass.init();
        PayloadTypeRegistry.clientboundPlay().register(S2CWaypointPacket.TYPE, S2CWaypointPacket.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(com.wayfarer.network.S2CWaypointSyncPacket.TYPE, com.wayfarer.network.S2CWaypointSyncPacket.STREAM_CODEC);
    }
}
