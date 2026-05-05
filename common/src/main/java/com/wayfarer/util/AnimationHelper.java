package com.wayfarer.util;

import com.wayfarer.config.WayfarerConfig;

public class AnimationHelper {
    public static float applyCurve(float progress, WayfarerConfig.AnimationCurve curve) {
        return switch (curve) {
            case LINEAR -> progress;
            case EXPO_IN -> progress == 0 ? 0 : (float) Math.pow(2, 10 * progress - 10);
            case EXPO_OUT -> progress == 1 ? 1 : 1 - (float) Math.pow(2, -10 * progress);
            case SPRING -> {
                float c4 = (float) (2 * Math.PI) / 3;
                yield progress == 0 ? 0
                        : (progress == 1 ? 1
                                : (float) (Math.pow(2, -10 * progress) * Math.sin((progress * 10 - 0.75) * c4) + 1));
            }
            case BOUNCE -> {
                float n1 = 7.5625f;
                float d1 = 2.75f;
                if (progress < 1 / d1) {
                    yield n1 * progress * progress;
                } else if (progress < 2 / d1) {
                    yield n1 * (progress -= 1.5f / d1) * progress + 0.75f;
                } else if (progress < 2.5 / d1) {
                    yield n1 * (progress -= 2.25f / d1) * progress + 0.9375f;
                } else {
                    yield n1 * (progress -= 2.625f / d1) * progress + 0.984375f;
                }
            }
        };
    }

    public static float lerp(float progress, float start, float end) {
        return start + (end - start) * progress;
    }
}
