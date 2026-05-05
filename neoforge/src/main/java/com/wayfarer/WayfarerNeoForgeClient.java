package com.wayfarer;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.client.WayfarerRenderer;
import com.wayfarer.network.S2CWaypointPacket;
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
        event.registerCategory(WayfarerKeys.WAYFARER_CATEGORY);
        event.register(WayfarerKeys.LOCATOR_BAR_KEY);
        System.out.println("[Wayfarer] Successfully registered locator bar keybind on NeoForge.");
    }

    public static void handlePacket(S2CWaypointPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (packet.clear()) {
                WayfarerRegistry.NETWORK_PROVIDER.clear();
            } else {
                WayfarerRegistry.NETWORK_PROVIDER.add(new WayfarerRegistry.Waypoint(
                        packet.name(),
                        packet.pos(),
                        packet.icon(),
                        packet.color(),
                        packet.waypointType(),
                        packet.locatorType()));
            }
        });
    }

    public static void onRegisterCommands(RegisterClientCommandsEvent event) {
        WayfarerCommands.register(event.getDispatcher());
    }

    public static void onRenderAfterTranslucentBlocks(RenderLevelStageEvent.AfterTranslucentBlocks event) {
        Minecraft client = Minecraft.getInstance();
        WayfarerRenderer.render(
                event.getPoseStack(),
                client.gameRenderer.getMainCamera(),
                client.renderBuffers().bufferSource());
    }
}
