package com.wayfarer.client;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class WaypointPreviewRenderer implements ImageRenderer {
    @Override
    public int render(GuiGraphicsExtractor graphics, int x, int y, int renderWidth, float tickDelta) {
        PreviewRenderer.render(graphics, x, y, renderWidth);
        return 100;
    }

    @Override
    public void close() {
    }
}
