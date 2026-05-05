package com.wayfarer.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.wayfarer.api.WayfarerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;

public class WayfarerCommands {
    public static <S> void register(CommandDispatcher<S> dispatcher) {
        dispatcher.register(LiteralArgumentBuilder.<S>literal("wayfarer")
                .then(LiteralArgumentBuilder.<S>literal("debug")
                        .executes(context -> {
                            Minecraft client = Minecraft.getInstance();
                            if (client.player != null) {
                                BlockPos pos = client.player.blockPosition().above(10);
                                WayfarerRegistry.registerWaypoint(
                                        "Debug Waypoint",
                                        pos,
                                        Identifier.fromNamespaceAndPath("minecraft", "textures/item/diamond.png"),
                                        0xFFADD8E6);
                            }
                            return 1;
                        })));
    }
}
