package com.wayfarer.client;

import com.wayfarer.config.WayfarerConfig;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class WayfarerConfigScreen {

        public static Screen create(Screen parent) {
                PreviewRenderer.resetAnimation();

                WaypointPreviewRenderer previewRenderer = new WaypointPreviewRenderer();

                return YetAnotherConfigLib.createBuilder()
                                .title(Component.translatable("config.wayfarer.title"))
                                .category(ConfigCategory.createBuilder()
                                                .name(Component.translatable("config.wayfarer.category.general"))
                                                .option(Option.<Float>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointScale"))
                                                                .description(OptionDescription.createBuilder()
                                                                                .text(Component.translatable(
                                                                                                "config.wayfarer.waypointScale.description"))
                                                                                .customImage(previewRenderer)
                                                                                .build())
                                                                .binding(1.0f, () -> WayfarerConfig.waypointScale,
                                                                                val -> {
                                                                                        WayfarerConfig.waypointScale = val;
                                                                                        PreviewRenderer.resetAnimation();
                                                                                })
                                                                .controller(opt -> FloatSliderControllerBuilder
                                                                                .create(opt)
                                                                                .range(0.5f, 3.0f)
                                                                                .step(0.05f)
                                                                                .formatValue(val -> Component.literal(
                                                                                                (int) (val * 100)
                                                                                                                + "%")))
                                                                .build())
                                                .option(Option.<String>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointLabelFormat"))
                                                                .description(OptionDescription.createBuilder()
                                                                                .text(Component.translatable(
                                                                                                "config.wayfarer.waypointLabelFormat.description"))
                                                                                .customImage(previewRenderer)
                                                                                .build())
                                                                .binding("%s (%dm)",
                                                                                () -> WayfarerConfig.waypointLabelFormat,
                                                                                val -> {
                                                                                        WayfarerConfig.waypointLabelFormat = val;
                                                                                        PreviewRenderer.resetAnimation();
                                                                                })
                                                                .controller(StringControllerBuilder::create)
                                                                .build())
                                                .option(Option.<Integer>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointViewDistance"))
                                                                .description(OptionDescription.of(Component
                                                                                .translatable("config.wayfarer.waypointViewDistance.description")))
                                                                .binding(-1, () -> WayfarerConfig.waypointViewDistance,
                                                                                val -> WayfarerConfig.waypointViewDistance = val)
                                                                .controller(opt -> IntegerSliderControllerBuilder
                                                                                .create(opt)
                                                                                .range(-1, 2000)
                                                                                .step(50))
                                                                .build())
                                                .build())
                                .category(ConfigCategory.createBuilder()
                                                .name(Component.translatable("config.wayfarer.category.animations"))
                                                .option(Option.<WayfarerConfig.AnimationCurve>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointFadeInCurve"))
                                                                .description(OptionDescription.createBuilder()
                                                                                .text(Component.translatable(
                                                                                                "config.wayfarer.waypointFadeInCurve.description"))
                                                                                .customImage(previewRenderer)
                                                                                .build())
                                                                .binding(WayfarerConfig.AnimationCurve.EXPO_IN,
                                                                                () -> WayfarerConfig.waypointFadeInCurve,
                                                                                val -> {
                                                                                        WayfarerConfig.waypointFadeInCurve = val;
                                                                                        PreviewRenderer.resetAnimation();
                                                                                })
                                                                .controller(opt -> EnumControllerBuilder.create(opt)
                                                                                .enumClass(WayfarerConfig.AnimationCurve.class)
                                                                                .formatValue(v -> Component
                                                                                                .literal(v.name())))
                                                                .build())
                                                .option(Option.<Integer>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointFadeInDuration"))
                                                                .description(OptionDescription.createBuilder()
                                                                                .text(Component.translatable(
                                                                                                "config.wayfarer.waypointFadeInDuration.description"))
                                                                                .customImage(previewRenderer)
                                                                                .build())
                                                                .binding(300, () -> WayfarerConfig.waypointFadeInDuration,
                                                                                val -> {
                                                                                        WayfarerConfig.waypointFadeInDuration = val;
                                                                                        PreviewRenderer.resetAnimation();
                                                                                })
                                                                .controller(opt -> IntegerSliderControllerBuilder
                                                                                .create(opt)
                                                                                .range(100, 1000)
                                                                                .step(50))
                                                                .build())
                                                .option(Option.<WayfarerConfig.AnimationCurve>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointFadeOutCurve"))
                                                                .description(OptionDescription.createBuilder()
                                                                                .text(Component.translatable(
                                                                                                "config.wayfarer.waypointFadeOutCurve.description"))
                                                                                .customImage(previewRenderer)
                                                                                .build())
                                                                .binding(WayfarerConfig.AnimationCurve.EXPO_OUT,
                                                                                () -> WayfarerConfig.waypointFadeOutCurve,
                                                                                val -> {
                                                                                        WayfarerConfig.waypointFadeOutCurve = val;
                                                                                        PreviewRenderer.resetAnimation();
                                                                                })
                                                                .controller(opt -> EnumControllerBuilder.create(opt)
                                                                                .enumClass(WayfarerConfig.AnimationCurve.class)
                                                                                .formatValue(v -> Component
                                                                                                .literal(v.name())))
                                                                .build())
                                                .option(Option.<Integer>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.waypointFadeOutDuration"))
                                                                .description(OptionDescription.createBuilder()
                                                                                .text(Component.translatable(
                                                                                                "config.wayfarer.waypointFadeOutDuration.description"))
                                                                                .customImage(previewRenderer)
                                                                                .build())
                                                                .binding(300, () -> WayfarerConfig.waypointFadeOutDuration,
                                                                                val -> {
                                                                                        WayfarerConfig.waypointFadeOutDuration = val;
                                                                                        PreviewRenderer.resetAnimation();
                                                                                })
                                                                .controller(opt -> IntegerSliderControllerBuilder
                                                                                .create(opt)
                                                                                .range(100, 1000)
                                                                                .step(50))
                                                                .build())
                                                .build())
                                .category(ConfigCategory.createBuilder()
                                                .name(Component.translatable("config.wayfarer.category.visibility"))
                                                .option(Option.<WayfarerConfig.VisibilityMode>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.showWaypointLabels"))
                                                                .description(OptionDescription.of(Component
                                                                                .translatable("config.wayfarer.showWaypointLabels.description")))
                                                                .binding(WayfarerConfig.VisibilityMode.ALWAYS,
                                                                                () -> WayfarerConfig.showWaypointLabels,
                                                                                val -> WayfarerConfig.showWaypointLabels = val)
                                                                .controller(opt -> EnumControllerBuilder.create(opt)
                                                                                .enumClass(WayfarerConfig.VisibilityMode.class))
                                                                .build())
                                                .option(Option.<WayfarerConfig.VisibilityMode>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.showWaypointIcons"))
                                                                .description(OptionDescription.of(Component
                                                                                .translatable("config.wayfarer.showWaypointIcons.description")))
                                                                .binding(WayfarerConfig.VisibilityMode.ALWAYS,
                                                                                () -> WayfarerConfig.showWaypointIcons,
                                                                                val -> WayfarerConfig.showWaypointIcons = val)
                                                                .controller(opt -> EnumControllerBuilder.create(opt)
                                                                                .enumClass(WayfarerConfig.VisibilityMode.class))
                                                                .build())
                                                .option(Option.<WayfarerConfig.LocatorVisibilityMode>createBuilder()
                                                                .name(Component.translatable(
                                                                                "config.wayfarer.showLocatorIcons"))
                                                                .description(OptionDescription.of(Component
                                                                                .translatable("config.wayfarer.showLocatorIcons.description")))
                                                                .binding(WayfarerConfig.LocatorVisibilityMode.ALWAYS,
                                                                                () -> WayfarerConfig.showLocatorIcons,
                                                                                val -> WayfarerConfig.showLocatorIcons = val)
                                                                .controller(opt -> EnumControllerBuilder.create(opt)
                                                                                .enumClass(WayfarerConfig.LocatorVisibilityMode.class))
                                                                .build())
                                                .build())
                                .save(WayfarerConfig::save)
                                .build()
                                .generateScreen(parent);
        }
}
