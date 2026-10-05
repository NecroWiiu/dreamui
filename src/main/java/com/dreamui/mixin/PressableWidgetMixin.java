package com.dreamui.mixin;

import com.dreamui.DreamUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PressableWidget.class)
public abstract class PressableWidgetMixin {
    @Unique private float dreamui$hover;
    @Unique private long dreamui$last;

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    private void dreamui$render(DrawContext ctx, int mx, int my, float delta, CallbackInfo ci) {
        Object self = this;
        if (!(self instanceof ButtonWidget) && !(self instanceof CyclingButtonWidget)) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (DreamUI.isExcluded(mc.currentScreen)) return;

        ClickableWidget w = (ClickableWidget) self;
        long now = Util.getMeasuringTimeMs();
        long dt = dreamui$last == 0 ? 16 : Math.min(100, now - dreamui$last);
        dreamui$last = now;
        float target = (w.isHovered() || w.isFocused()) && w.active ? 1f : 0f;
        dreamui$hover += (target - dreamui$hover) * (1f - (float) Math.exp(-dt / 70.0));
        float hv = dreamui$hover;

        // staggered slide/fade-in when a screen opens
        float delay = Math.min(240f, w.getY() * 0.55f);
        float e = DreamUI.easeOut((DreamUI.ageMs() - delay) / 320f);

        int x = w.getX(), y = w.getY(), W = w.getWidth(), H = w.getHeight();
        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, (1f - e) * 10f, 0);

        int bg = DreamUI.lerp(w.active ? 0xA0140A24 : 0x80100A1A, 0xE03B1670, hv);
        ctx.fill(x, y, x + W, y + H, DreamUI.mulAlpha(bg, e));
        ctx.fillGradient(x + 1, y + 1, x + W - 1, y + H / 2, DreamUI.mulAlpha(DreamUI.lerp(0x10FFFFFF, 0x30FFFFFF, hv), e), 0x00FFFFFF);
        ctx.drawBorder(x, y, W, H, DreamUI.mulAlpha(DreamUI.lerp(0x40FFFFFF, 0xFFB46BFF, hv), e));
        DreamUI.accentBar(ctx, x + (int) ((W - 2) * (1f - hv) / 2f) + 1, y + H - 2,
            (int) ((W - 2) * hv), 1, e);

        if (e > 0.06f) {
            int tc = w.active ? DreamUI.lerp(0xFFD8CCEE, 0xFFFFFFFF, hv) : 0xFF77708A;
            ctx.drawCenteredTextWithShadow(mc.textRenderer, w.getMessage(),
                x + W / 2, y + (H - 8) / 2, DreamUI.mulAlpha(tc, e));
        }
        ctx.getMatrices().pop();
        ci.cancel();
    }
}
