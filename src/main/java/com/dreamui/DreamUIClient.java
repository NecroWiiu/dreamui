package com.dreamui;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Util;

public class DreamUIClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Mark when each screen opens (drives fades and staggered button animations)
        ScreenEvents.BEFORE_INIT.register((client, screen, w, h) -> DreamUI.onScreen(screen));

        // Fade-in overlay drawn after everything else
        ScreenEvents.AFTER_INIT.register((client, screen, w, h) ->
            ScreenEvents.afterRender(screen).register((s, ctx, mx, my, delta) -> fade(client, s, ctx)));

        ClientTickEvents.END_CLIENT_TICK.register(DreamMusic::tick);
    }

    private static void fade(MinecraftClient mc, Screen s, DrawContext ctx) {
        if (DreamUI.isExcluded(s)) return;
        boolean inWorld = mc.world != null;
        float dur = inWorld ? 140f : 380f;
        float max = inWorld ? 0.30f : 1.0f;
        float t = Math.min(1f, DreamUI.ageMs() / dur);
        float a = (1f - DreamUI.easeOut(t)) * max;
        if (a < 0.01f) return;
        ctx.fill(0, 0, s.width, s.height, ((int) (a * 255) << 24) | 0x07030D);
    }
}
