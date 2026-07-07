package com.wayfarer;

import com.wayfarer.client.WayfarerConfigScreen;
import com.wayfarer.network.S2CWaypointPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod("wayfarer")
public class WayfarerNeoForge {
    public WayfarerNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        CommonClass.init();
        modEventBus.addListener(this::registerNetworking);

        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            ClientOnly.init(modEventBus, modContainer);
        }
    }

    private static class ClientOnly {
        static void init(IEventBus modEventBus, ModContainer modContainer) {
            modEventBus.addListener(WayfarerNeoForgeClient::onRegisterKeys);
            modEventBus.addListener(ClientOnly::onClientSetup);
            NeoForge.EVENT_BUS.addListener(WayfarerNeoForgeClient::onRegisterCommands);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                    (container, screen) -> WayfarerConfigScreen.create(screen));
        }

        private static void onClientSetup(FMLClientSetupEvent event) {
            WayfarerNeoForgeClient.init();
        }

        private static void handlePacket(S2CWaypointPacket packet, IPayloadContext context) {
            WayfarerNeoForgeClient.handlePacket(packet, context);
        }

        private static void handlePacket(com.wayfarer.network.S2CWaypointSyncPacket packet, IPayloadContext context) {
            WayfarerNeoForgeClient.handlePacket(packet, context);
        }
    }

    private void registerNetworking(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("wayfarer").optional();
        registrar.playToClient(
                S2CWaypointPacket.TYPE,
                S2CWaypointPacket.STREAM_CODEC,
                (packet, context) -> {
                    if (FMLEnvironment.getDist() == Dist.CLIENT) {
                        ClientOnly.handlePacket(packet, context);
                    }
                });
        registrar.playToClient(
                com.wayfarer.network.S2CWaypointSyncPacket.TYPE,
                com.wayfarer.network.S2CWaypointSyncPacket.STREAM_CODEC,
                (packet, context) -> {
                    if (FMLEnvironment.getDist() == Dist.CLIENT) {
                        ClientOnly.handlePacket(packet, context);
                    }
                });
    }
}
