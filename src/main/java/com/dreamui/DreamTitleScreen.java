package com.dreamui;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerWarningScreen;
import net.minecraft.client.gui.screen.option.LanguageOptionsScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class DreamTitleScreen extends Screen {
    public DreamTitleScreen() {
        super(Text.translatable("narrator.screen.title"));
    }

    @Override
    protected void init() {
        final int w = 200, h = 20, gap = 4, half = (w - gap) / 2;
        int total = 4 * h + 3 * gap;
        int y = Math.min((int) (height * 0.62), height - total - 26);
        int x = width / 2 - w / 2;

        ButtonWidget sp = addDrawableChild(ButtonWidget.builder(Text.translatable("menu.singleplayer"),
            b -> client.setScreen(new SelectWorldScreen(this))).dimensions(x, y, w, h).build());

        ButtonWidget mp = addDrawableChild(ButtonWidget.builder(Text.translatable("menu.multiplayer"), b -> {
            Screen next = client.options.skipMultiplayerWarning
                ? new MultiplayerScreen(this) : new MultiplayerWarningScreen(this);
            client.setScreen(next);
        }).dimensions(x, y + h + gap, w, h).build());
        mp.active = client.isMultiplayerEnabled();

        boolean hasModMenu = FabricLoader.getInstance().isModLoaded("modmenu");
        ButtonWidget mods = addDrawableChild(ButtonWidget.builder(Text.literal("Mods"), b -> {
            Screen s = modsScreen();
            if (s != null) client.setScreen(s);
        }).dimensions(x, y + 2 * (h + gap), half, h).build());
        mods.active = hasModMenu;

        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.options"),
            b -> client.setScreen(new OptionsScreen(this, client.options)))
            .dimensions(x + half + gap, y + 2 * (h + gap), half, h).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("options.language"),
            b -> client.setScreen(new LanguageOptionsScreen(this, client.options, client.getLanguageManager())))
            .dimensions(x, y + 3 * (h + gap), half, h).build());

        addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), b -> client.scheduleStop())
            .dimensions(x + half + gap, y + 3 * (h + gap), half, h).build());
    }

    /** Opens Mod Menu through reflection so the mod works with or without it. */
    private Screen modsScreen() {
        try {
            Class<?> c = Class.forName("com.terraformersmc.modmenu.gui.ModsScreen");
            return (Screen) c.getConstructor(Screen.class).newInstance(this);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public boolean shouldCloseOnEsc() { return false; }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        DreamUI.drawBackground(ctx, width, height, mx, my, false);
        super.render(ctx, mx, my, delta);

        int mods = FabricLoader.getInstance().getAllMods().size();
        String left = "Minecraft 1.20.1  \u2022  Fabric  \u2022  " + mods + " mods";
        ctx.drawTextWithShadow(textRenderer, left, 6, height - 12, 0x99FFFFFF);
        String right = "Copyright Mojang AB";
        ctx.drawTextWithShadow(textRenderer, right, width - textRenderer.getWidth(right) - 6, height - 12, 0x77FFFFFF);
    }
}
