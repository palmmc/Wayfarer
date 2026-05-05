package com.wayfarer;

import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.client.WayfarerRenderer;
import com.wayfarer.network.S2CWaypointPacket;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class WayfarerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        try {
            Class<?> helperClass = Class.forName("net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper");
            java.lang.reflect.Method registerMethod = helperClass.getMethod("registerKeyBinding", net.minecraft.client.KeyMapping.class);
            registerMethod.invoke(null, com.wayfarer.client.WayfarerKeys.LOCATOR_BAR_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            WayfarerRenderer.render(
                    context.matrices(),
                    context.gameRenderer().getMainCamera(),
                    context.consumers());
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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            com.wayfarer.client.WayfarerCommands.register(dispatcher);
        });
    }
}
