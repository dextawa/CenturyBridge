package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v26_1.facade.ChatStatus;
import top.dext.centurybridge.step.v26_1.rt.CbWallGameRenderer;

/**
 * 1.21.11 Minecraft members 26.1 dropped, given back on Minecraft itself (covers):
 * <ul>
 * <li>delayCrashRaw(report): the crash thrown on the next tick as given, where delayCrash fills the report
 *     in first -- 26.1's delayCrash is BlockableEventLoop's after fillReport, so this is BlockableEventLoop's
 *     own.</li>
 * <li>setWindowActive(active): 1.21.11's window-focus flag (isWindowActive's answer, set by the window's
 *     focus callback); 26.1's Window keeps that flag.</li>
 * <li>getChatStatus(): 1.21.11's ChatStatus (facade/ChatStatus) from the same sources, in the same order:
 *     chat hidden in the options, chat not allowed by the launcher, chat not allowed by the account.</li>
 * <li>noRender: 1.21.11's public flag that made runTick skip the game renderer for a frame; 26.1's runTick
 *     runs it as update, extract and render, each skipped while it is set (the runTick call sites only, as
 *     1.21.11 gated only its runTick's); opening a screen clears it, as 1.21.11's setScreen did.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(Minecraft.class)
public abstract class CbWallMinecraftMixin extends ReentrantBlockableEventLoop<Runnable> implements CbWallGameRenderer.CbWallMinecraftWindow {

    @Shadow
    @Final
    private Window window;

    @Shadow
    @Final
    private boolean allowsChat;

    /** 1.21.11 Minecraft.noRender */
    public boolean noRender;

    @Shadow
    private UserApiService.UserProperties userProperties() {
        throw new AssertionError();
    }

    protected CbWallMinecraftMixin(String name, boolean propagatesCrashes) {
        super(name, propagatesCrashes);
    }

    public void delayCrashRaw(CrashReport report) {
        super.delayCrash(report);
    }

    @Override
    public void setWindowActive(boolean active) {
        ((CbWallWindowAccess) (Object) this.window).cb$setFocused(active);
    }

    public ChatStatus getChatStatus() {
        Minecraft self = (Minecraft) (Object) this;
        if (self.options.chatVisibility().get() == ChatVisiblity.HIDDEN) {
            return ChatStatus.DISABLED_BY_OPTIONS;
        }
        if (!this.allowsChat) {
            return ChatStatus.DISABLED_BY_LAUNCHER;
        }
        return !this.userProperties().flag(UserApiService.UserFlag.CHAT_ALLOWED) ? ChatStatus.DISABLED_BY_PROFILE : ChatStatus.ENABLED;
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;update(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void cb$update(GameRenderer renderer, DeltaTracker deltaTracker, boolean renderLevel) {
        if (!this.noRender) {
            renderer.update(deltaTracker, renderLevel);
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;extract(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void cb$extract(GameRenderer renderer, DeltaTracker deltaTracker, boolean renderLevel) {
        if (!this.noRender) {
            renderer.extract(deltaTracker, renderLevel);
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/GameRenderer;render(Lnet/minecraft/client/DeltaTracker;Z)V"))
    private void cb$render(GameRenderer renderer, DeltaTracker deltaTracker, boolean renderLevel) {
        if (!this.noRender) {
            renderer.render(deltaTracker, renderLevel);
        }
    }

    @Shadow
    public Screen screen;

    @Inject(method = "setScreen", at = @At("TAIL"))
    private void cb$screenClearsNoRender(Screen screen, CallbackInfo ci) {
        if (this.screen != null) {
            this.noRender = false;
        }
    }
}
