package com.dreamui.mixin;

import com.dreamui.DreamUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SliderWidget.class)
public abstract class SliderWidgetMixin {
    @Shadow protected double value;

    @Inject(method = "renderButton", at = @At("HEAD"), cancellable = true)
    private void dreamui$render(DrawContext ctx, int mx, int my, float delta, CallbackInfo ci) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (DreamUI.isExcluded(mc.currentScreen)) return;
        SliderWidget w = (SliderWidget) (Object) this;
        float e = DreamUI.easeOut((DreamUI.ageMs() - Math.min(240f, w.getY() * 0.55f)) / 320f);
        boolean hot = (w.isHovered() || w.isFocused()) && w.active;
        int x = w.getX(), y = w.getY(), W = w.getWidth(), H = w.getHeight();

        ctx.getMatrices().push();
        ctx.getMatrices().translate(0, (1f - e) * 10f, 0);
        ctx.fill(x, y, x + W, y + H, DreamUI.mulAlpha(hot ? 0xD0261046 : 0xA0140A24, e));
        int fillW = (int) ((W - 2) * MathHelper.clamp(value, 0, 1));
        DreamUI.accentBar(ctx, x + 1, y + 1, fillW, H - 2, e * 0.35f);
        int kx = x + 1 + fillW - 2;
        ctx.fill(kx, y, kx + 4, y + H, DreamUI.mulAlpha(hot ? 0xFFFFFFFF : 0xFFD8CCEE, e));
        ctx.drawBorder(x, y, W, H, DreamUI.mulAlpha(hot ? 0xFFB46BFF : 0x40FFFFFF, e));
        if (e > 0.06f) {
            ctx.drawCenteredTextWithShadow(mc.textRenderer, w.getMessage(), x + W / 2, y + (H - 8) / 2,
                DreamUI.mulAlpha(w.active ? 0xFFFFFFFF : 0xFF77708A, e));
        }
        ctx.getMatrices().pop();
        ci.cancel();
    }
}
