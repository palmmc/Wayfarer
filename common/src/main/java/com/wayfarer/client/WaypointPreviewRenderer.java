package com.wayfarer.client;

import dev.isxander.yacl3.gui.image.ImageRenderer;
import net.minecraft.client.gui.GuiGraphics;

public class WaypointPreviewRenderer implements ImageRenderer {
    @Override
    public int render(GuiGraphics graphics, int x, int y, int renderWidth, float tickDelta) {
        PreviewRenderer.render(graphics, x, y, renderWidth);
        return 100;
    }

    @Override
    public void close() {
    }
}
