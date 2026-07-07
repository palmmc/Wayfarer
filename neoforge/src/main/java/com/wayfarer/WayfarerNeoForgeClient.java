package com.wayfarer;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.client.WayfarerRenderer;
import com.wayfarer.network.S2CWaypointPacket;
import com.wayfarer.network.S2CWaypointSyncPacket;
import net.minecraft.client.Minecraft;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import com.wayfarer.client.WayfarerCommands;
import com.wayfarer.client.WayfarerKeys;

public class WayfarerNeoForgeClient {
    public static void init() {
    }

    public static void onRegisterKeys(RegisterKeyMappingsEvent event) {
        event.register(WayfarerKeys.LOCATOR_BAR_KEY);
        System.out.println("[Wayfarer] Successfully registered locator bar keybind on NeoForge.");
    }

    public static void handlePacket(S2CWaypointPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.clear()) {
                if (packet.name().isEmpty()) {
                    WayfarerRegistry.NETWORK_PROVIDER.clear();
                } else {
                    WayfarerRegistry.NETWORK_PROVIDER.removeByName(packet.name());
                }
            } else {
                WayfarerRegistry.NETWORK_PROVIDER.addOrUpdate(new WayfarerRegistry.Waypoint(
                        packet.name(),
                        packet.pos(),
                        packet.icon(),
                        packet.color(),
                        packet.waypointType(),
                        packet.locatorType()));
            }
        });
    }

    public static void handleSyncPacket(S2CWaypointSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            WayfarerRegistry.NETWORK_PROVIDER.clearNonLocators();
            for (WayfarerRegistry.Waypoint wp : packet.waypoints()) {
                WayfarerRegistry.NETWORK_PROVIDER.addOrUpdate(wp);
            }
        });
    }

    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        WayfarerCommands.register(event.getDispatcher());
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            Minecraft client = Minecraft.getInstance();
            WayfarerRenderer.render(
                    event.getPoseStack(),
                    event.getCamera(),
                    client.renderBuffers().bufferSource());
        }
    }
}
