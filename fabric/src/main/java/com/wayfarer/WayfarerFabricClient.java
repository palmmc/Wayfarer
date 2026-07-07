package com.wayfarer;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.network.S2CWaypointPacket;
import com.wayfarer.network.S2CWaypointSyncPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class WayfarerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            Class<?> helperClass = Class.forName("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper");
            java.lang.reflect.Method registerMethod = helperClass.getMethod("registerKeyBinding",
                    net.minecraft.client.KeyMapping.class);
            registerMethod.invoke(null, com.wayfarer.client.WayfarerKeys.LOCATOR_BAR_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ClientPlayNetworking.registerGlobalReceiver(S2CWaypointPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.clear()) {
                    if (payload.name().isEmpty()) {
                        WayfarerRegistry.NETWORK_PROVIDER.clear();
                    } else {
                        WayfarerRegistry.NETWORK_PROVIDER.removeByName(payload.name());
                    }
                } else {
                    WayfarerRegistry.NETWORK_PROVIDER.addOrUpdate(new WayfarerRegistry.Waypoint(
                            payload.name(),
                            payload.pos(),
                            payload.icon(),
                            payload.color(),
                            payload.waypointType(),
                            payload.locatorType()));
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(S2CWaypointSyncPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                WayfarerRegistry.NETWORK_PROVIDER.clearNonLocators();
                for (WayfarerRegistry.Waypoint wp : payload.waypoints()) {
                    WayfarerRegistry.NETWORK_PROVIDER.addOrUpdate(wp);
                }
            });
        });

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            com.wayfarer.client.WayfarerCommands.register(dispatcher);
        });
    }
}
