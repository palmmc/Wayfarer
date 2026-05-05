---
title: Examples
date: 2026-05-03T03:32:55Z
updated: 2026-05-03T03:32:55Z
authors:
  - palm1
---

### 1. Simple Server-Side Death Waypoint
This example shows how to send a waypoint at a player's death location so they can recover their items.

```java
import com.wayfarer.api.WayfarerRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public class DeathpointHandler {
    public static void onPlayerRespawn(ServerPlayer player, BlockPos deathPos) {
        WayfarerRegistry.sendWaypoint(
            player, // Send to the player that died.
            "Last Death", // Name of the waypoint.
            deathPos, // Use the location where the player died.
            new Identifier("minecraft", "textures/item/bone.png"), // Use bone texture.
            0xFF0000, // Set color to red.
            WayfarerRegistry.WaypointType.STANDARD, // Standard waypoint.
            WayfarerRegistry.LocatorType.STANDARD // Standard locator.
        );
    }
}
```

### 2. Client-Side Entity Tracker
This examples shows how to draw an icon waypoint over nearby zombies.

```java
import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.Waypoint;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.monster.zombie.*;
import net.minecraft.world.phys.AABB;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ZombieTrackerProvider implements WayfarerRegistry.WaypointProvider {
    private static final Identifier ROTTEN_FLESH_ICON = new Identifier("minecraft", "textures/item/rotten_flesh.png");

    public ZombieTrackerProvider() {
        WayfarerRegistry.registerProvider(this);
    }

    @Override
    public Collection<Waypoint> getWaypoints() {
        List<Waypoint> waypoints = new ArrayList<>();
        Minecraft client = Minecraft.getInstance();
        
        if (client.level != null && client.player != null) {
            // Search for zombies in a 50 block radius around the player.
            AABB searchArea = client.player.getBoundingBox().inflate(50.0);
            for (Zombie zombie : client.level.getEntitiesOfClass(Zombie.class, searchArea)) {
                waypoints.add(new Waypoint(
                    "Zombie", // Name of the waypoint.
                    zombie.blockPosition().above(1), // Above the zombie's location.
                    ROTTEN_FLESH_ICON, // Use the rotten flesh texture.
                    0x00FF00, // Set color to green.
                    WayfarerRegistry.WaypointType.ICON, // Icon-only waypoint.
                    WayfarerRegistry.LocatorType.HIDDEN // Don't show on the locator bar.
                ));
            }
        }
        return waypoints;
    }
}
```

### 3. Filtering Waypoints with a Transformer
This example shows how you can filter waypoints based on certain criteria.

```java
import com.wayfarer.api.WayfarerRegistry;
import com.wayfarer.api.WayfarerRegistry.Waypoint;

public class DefaultColorTransformer implements WayfarerRegistry.WaypointTransformer {
    public DefaultColorTransformer() {
        WayfarerRegistry.registerTransformer(this);
    }

    @Override
    public Waypoint transform(Waypoint waypoint) {
        // Hides any waypoints that aren't the default white color.
        if (waypoint.color != 0xFFFFFF) {
            return null;
        }
        return waypoint;
    }
}
```