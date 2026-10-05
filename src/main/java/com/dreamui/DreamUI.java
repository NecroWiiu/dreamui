package com.dreamui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

public final class DreamUI {
    public static final Identifier BG = new Identifier("dreamui", "textures/gui/background.png");
    public static final int TEX_W = 1599, TEX_H = 900;

    private static Screen last;
    private static long openTime = Util.getMeasuringTimeMs();

    private DreamUI() {}

    public static void onScreen(Screen s) {
        if (s != last) {
            last = s;
            openTime = Util.getMeasuringTimeMs();
        }
    }

    public static float ageMs() { return Util.getMeasuringTimeMs() - openTime; }

    public static float easeOut(float t) { float u = 1f - MathHelper.clamp(t, 0f, 1f); return 1f - u * u * u; }

    /** Graphics/video/shader screens are left untouched (Sodium, Iris, Reese's, vanilla Video Settings...). */
    public static boolean isExcluded(Screen s) {
        if (s == null) return false;
        String n = s.getClass().getName().toLowerCase();
        return n.contains("sodium") || n.contains("iris") || n.contains("videooptions")
            || n.contains("videosettings") || n.contains("graphics") || n.contains("shader")
            || n.contains("embeddium") || n.contains("indium") || n.contains("optifine");
    }

    public static int mulAlpha(int argb, float a) {
        int al = (int) (((argb >>> 24) & 0xFF) * MathHelper.clamp(a, 0f, 1f));
        return (al << 24) | (argb & 0xFFFFFF);
    }

    public static int lerp(int c1, int c2, float t) {
        t = MathHelper.clamp(t, 0f, 1f);
        int a = (int) MathHelper.lerp(t, (c1 >>> 24) & 0xFF, (c2 >>> 24) & 0xFF);
        int r = (int) MathHelper.lerp(t, (c1 >> 16) & 0xFF, (c2 >> 16) & 0xFF);
        int g = (int) MathHelper.lerp(t, (c1 >> 8) & 0xFF, (c2 >> 8) & 0xFF);
        int b = (int) MathHelper.lerp(t, c1 & 0xFF, c2 & 0xFF);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    /** Horizontal magenta -> cyan accent bar (matches the artwork's logo). */
    public static void accentBar(DrawContext ctx, int x, int y, int w, int h, float alpha) {
        if (w <= 0) return;
        int step = 3;
        for (int i = 0; i < w; i += step) {
            float t = w <= 1 ? 0 : i / (float) (w - 1);
            int c = lerp(0xFFFF5CE1, 0xFF5CF2FF, t);
            ctx.fill(x + i, y, Math.min(x + i + step, x + w), y + h, mulAlpha(c, alpha));
        }
    }

    /** Animated artwork background (slow zoom + mouse parallax). */
    public static void drawBackground(DrawContext ctx, int w, int h, double mx, double my, boolean dim) {
        float t = (Util.getMeasuringTimeMs() % 600000L) / 1000f;
        float zoom = 1.07f + 0.02f * MathHelper.sin(t * 0.18f);
        float imgAspect = TEX_W / (float) TEX_H;
        float dw, dh;
        if (w / (float) h > imgAspect) { dw = w; dh = w / imgAspect; } else { dh = h; dw = h * imgAspect; }
        dw *= zoom; dh *= zoom;
        float px = (float) ((mx / Math.max(1, w)) - 0.5) * -14f + MathHelper.sin(t * 0.12f) * 6f;
        float py = (float) ((my / Math.max(1, h)) - 0.5) * -8f;
        float x = (w - dw) / 2f + px, y = (h - dh) / 2f + py;

        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(dw / TEX_W, dh / TEX_H, 1f);
        ctx.drawTexture(BG, 0, 0, TEX_W, TEX_H, 0f, 0f, TEX_W, TEX_H, TEX_W, TEX_H);
        ctx.getMatrices().pop();

        if (dim) {
            ctx.fill(0, 0, w, h, 0xB30B0614);
            ctx.fillGradient(0, 0, w, h, 0x302A0F55, 0x501A0838);
        } else {
            ctx.fillGradient(0, 0, w, h / 4, 0x80000000, 0x00000000);
            ctx.fillGradient(0, h / 2, w, h, 0x00000000, 0xD0080412);
        }
    }

    public static double scaledMouseX(MinecraftClient mc) {
        return mc.mouse.getX() * mc.getWindow().getScaledWidth() / (double) mc.getWindow().getWidth();
    }

    public static double scaledMouseY(MinecraftClient mc) {
        return mc.mouse.getY() * mc.getWindow().getScaledHeight() / (double) mc.getWindow().getHeight();
    }
}
