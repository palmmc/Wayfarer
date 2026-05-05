---
title: API
date: 2026-05-03T03:32:55Z
updated: 2026-05-03T03:32:55Z
authors:
  - palm1
---

The main part of the API is `com.wayfarer.api.WayfarerRegistry`; it manages waypoint registration and synchronization.

### Waypoint Types

**`WaypointType`**
Determines a preset for how the waypoint is rendered and interact with.

- `STANDARD`: Displays an icon and text.
- `ICON`: Only displays an icon.
- `FOLDED`: Only displays an icon, but waypoint text folds out when a player looks at it.

### Locator Types

**`LocatorType`**
Determines a preset for how the waypoint shows up on the locator bar.

- `STANDARD`: Uses a stanard vanilla locator icon, and renders the waypoint icon when key is held.
- `HOVER`: Only renders the waypoint icon when key is held.
- `ICON`: Always uses the waypoint icon.
- `HIDDEN`: Waypoint will not render on the locator bar.

### Network Requests

Wayfarer handles S2C packets to make it easy to send waypoints from the server to clients with the mod installed.

```java
// Sends a waypoint to player.
WayfarerRegistry.sendWaypoint(
    ServerPlayer player, 
    String name, 
    BlockPos pos, 
    Identifier icon, 
    int color
);

// Send a request to clear network based waypoints for player.
WayfarerRegistry.clearWaypoints(ServerPlayer player);
```

### Client-Side Features

If the mod is only using client-side, you can also render waypoints statically.

#### Static Registration

```java
// Registers a waypoint for player.
WayfarerRegistry.registerWaypoint(
    String name, 
    BlockPos pos, 
    Identifier iconId, 
    int color, 
    WaypointType type, 
    LocatorType locatorType
);

// Clears all static and network waypoints.
WayfarerRegistry.clearWaypoints();
```

#### Dynamic Providers
You can register a custom `WaypointProvider` to supply a list of waypoints dynamically:
```java
WayfarerRegistry.registerProvider(new WayfarerRegistry.WaypointProvider() {
    @Override
    public Collection<Waypoint> getWaypoints() {
        return myDynamicWaypointsList;
    }
});
```

#### Transformers
And you can also register a `WaypointTransformer` to 'hook' and modify or filter waypoints before they are rendered:
```java
WayfarerRegistry.registerTransformer(new WayfarerRegistry.WaypointTransformer() {
    @Override
    public Waypoint transform(Waypoint waypoint) {
        return waypoint;
    }
});
```