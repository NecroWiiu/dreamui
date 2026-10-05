package com.dreamui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

/** Plays assets/dreamui/sounds/music/bam_bam.ogg in menus, pause menu and inventory. */
public final class DreamMusic {
    private static final Identifier SOUND_ID = new Identifier("dreamui", "menu_music");
    private static final Identifier FILE = new Identifier("dreamui", "sounds/music/bam_bam.ogg");

    private static Track current;
    private static Boolean available;
    private static int recheck;

    private DreamMusic() {}

    private static boolean available(MinecraftClient mc) {
        if (available == null || (!available && ++recheck > 200)) {
            recheck = 0;
            available = mc.getResourceManager().getResource(FILE).isPresent();
        }
        return available;
    }

    /** True while our track should own the music channel. */
    public static boolean active() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || !Boolean.TRUE.equals(available)) return false;
        Screen s = mc.currentScreen;
        if (s == null) return false;
        if (mc.world == null) return true;
        return !(s instanceof ChatScreen) && !(s instanceof AbstractSignEditScreen);
    }

    public static void tick(MinecraftClient mc) {
        if (!available(mc)) return;
        if (active()) {
            mc.getMusicTracker().stop();
            if (current == null || current.isDone() || !mc.getSoundManager().isPlaying(current)) {
                current = new Track();
                mc.getSoundManager().play(current);
            }
            current.fadeOut = false;
        } else if (current != null) {
            current.fadeOut = true;
        }
    }

    private static final class Track extends MovingSoundInstance {
        boolean fadeOut;

        Track() {
            super(SoundEvent.of(SOUND_ID), SoundCategory.MUSIC, Random.create());
            this.repeat = true;
            this.repeatDelay = 0;
            this.volume = 1.0f;
            this.relative = true;
            this.attenuationType = SoundInstance.AttenuationType.NONE;
        }

        @Override
        public void tick() {
            if (fadeOut) {
                volume -= 0.03f;
                if (volume <= 0f) setDone();
            } else if (volume < 1f) {
                volume = Math.min(1f, volume + 0.05f);
            }
        }

        @Override
        public boolean shouldAlwaysPlay() { return true; }
    }
}
