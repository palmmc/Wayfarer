package com.wayfarer.client;

import com.wayfarer.api.WayfarerRegistry;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

public class LocatorBarRendererState {
    public double x, y, z;
    public int color;
    public Identifier icon;
    public Player player;
    public WayfarerRegistry.LocatorType locatorType;
    public float alpha = 0f;
    public boolean active = false;

    public void update(double x, double y, double z, int color, Identifier icon, Player player, WayfarerRegistry.LocatorType locatorType) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.icon = icon;
        this.player = player;
        this.locatorType = locatorType;
        this.active = true;
    }
}
