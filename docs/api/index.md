---
title: Documentation
icon: lucide/code
version_selector: true
nav:
  - "Documentation": "index.md"
  - "API Reference": "reference.md"
  - "Examples": "examples.md"
date: 2026-05-03T03:32:55Z
updated: 2026-05-03T03:32:55Z
authors:
  - palm1
---
**Wayfarer** implements a waypoint rendering system and extends upon the locator bar in a way that can utilized by mod developers for their projects.

## Depending on Wayfarer

To make Wayfarer a dependency for your project, you will need to add it to your `build.gradle` (or `build.gradle.kts` for Kotlin).

It should look something like this:

=== "Groovy"

    ```groovy
    repositories {
        maven {
            url "https://api.modrinth.com/maven"
        }
    }

    dependencies {
        // Example for Fabric
        modImplementation "maven.modrinth:wayfarerlib:{{ mod_version }}-fabric"
        
        // Example for NeoForge
        implementation "maven.modrinth:wayfarerlib:{{ mod_version }}-neoforge"
    }
    ```

=== "Kotlin"

    ```kotlin
    repositories {
        maven {
            url = uri("https://api.modrinth.com/maven")
        }
    }

    dependencies {
        // Example for Fabric
        modImplementation("maven.modrinth:wayfarerlib:{{ mod_version }}-fabric")
        
        // Example for NeoForge
        implementation("maven.modrinth:wayfarerlib:{{ mod_version }}-neoforge")
    }
    ```

!!! note
    This snippet targets version `{{ mod_version }}` for Minecraft `{{ mod_game_versions }}`.

## Best of Luck!
If you have further questions about the API, you can join our [Discord Server](https://discord.gg/Fuuxaq6KPh).