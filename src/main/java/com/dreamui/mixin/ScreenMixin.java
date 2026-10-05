package com.dreamui.mixin;

import com.dreamui.DreamUI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(method = "renderBackground(Lnet/minecraft/client/gui/DrawContext;)V", at = @At("HEAD"), cancellable = true)
    private void dreamui$background(DrawContext ctx, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        if (DreamUI.isExcluded(self)) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.world == null) {
            DreamUI.drawBackground(ctx, self.width, self.height,
                DreamUI.scaledMouseX(mc), DreamUI.scaledMouseY(mc), true);
        } else {
            ctx.fillGradient(0, 0, self.width, self.height, 0xC0140A26, 0xD0200F3C);
        }
        ci.cancel();
    }
}
