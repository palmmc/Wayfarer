package com.wayfarer;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.client.WayfarerRenderer;
import com.wayfarer.network.S2CWaypointPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;

public class WayfarerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            java.lang.reflect.Method registerMethod = Class
                    .forName("net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper")
                    .getMethod("registerKeyMapping", net.minecraft.client.KeyMapping.class);
            registerMethod.invoke(null, com.wayfarer.client.WayfarerKeys.LOCATOR_BAR_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        LevelRenderEvents.BEFORE_GIZMOS.register(context -> {
            WayfarerRenderer.render(
                    context.poseStack(),
                    context.gameRenderer().mainCamera(),
                    context.submitNodeCollector(),
                    context.levelState().cameraRenderState);
        });

        ClientPlayNetworking.registerGlobalReceiver(S2CWaypointPacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                if (payload.clear()) {
                    WayfarerRegistry.NETWORK_PROVIDER.clear();
                } else {
                    WayfarerRegistry.NETWORK_PROVIDER.add(new WayfarerRegistry.Waypoint(
                            payload.name(),
                            payload.pos(),
                            payload.icon(),
                            payload.color(),
                            payload.waypointType(),
                            payload.locatorType()));
                }
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(com.wayfarer.network.S2CWaypointSyncPacket.TYPE, (payload, context) -> {
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
