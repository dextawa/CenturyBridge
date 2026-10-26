package top.dext.centurybridge.step.v26_1.mixin;

import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.CrashReport;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.ChatVisiblity;
import net.minecraft.util.thread.ReentrantBlockableEventLoop;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v26_1.facade.ChatStatus;
import top.dext.centurybridge.step.v26_1.facade.ItemRenderer;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import top.dext.centurybridge.step.v26_1.rt.CbWallGameRenderer;
import top.dext.centurybridge.step.v26_1.rt.CbWallLegacyCalls;

/**
 * 1.21.11 Minecraft members 26.1 dropped, given back on Minecraft itself (covers):
 * <ul>
 * <li>delayCrashRaw(report): the crash thrown on the next tick as given, where delayCrash fills the report
 *     in first -- 26.1's delayCrash is BlockableEventLoop's after fillReport, so this is BlockableEventLoop's
 *     own.</li>
 * <li>setWindowActive(active): 1.21.11's window-focus flag (isWindowActive's answer, set by the window's
 *     focus callback); 26.1's Window keeps that flag.</li>
 * <li>getItemRenderer(): facade/ItemRenderer's one instance (26.1 has no ItemRenderer; its helpers are
 *     ItemFeatureRenderer's).</li>
 * <li>getChatStatus(): 1.21.11's ChatStatus (facade/ChatStatus) from the same sources, in the same order:
 *     chat hidden in the options, chat not allowed by the launcher, chat not allowed by the account.</li>
 * <li>noRender: 1.21.11's public flag that made runTick skip the game renderer for a frame; 26.1 runs the
 *     game renderer as update, extract and render in renderFrame(boolean), which runTick calls; each is
 *     skipped while the flag is set (only those call sites, as 1.21.11 gated only its runTick's); opening a
 *     screen clears it, as 1.21.11's setScreen did (mixin/CbWallMinecraftReadsMixin).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
@Mixin(value = Minecraft.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallMinecraftMixin extends ReentrantBlockableEventLoop<Runnable>
        implements CbWallGameRenderer.CbWallMinecraftWindow, CbWallLegacyCalls.MinecraftLegacy {

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

    @Override
    public boolean cb$noRender() {
        return this.noRender;
    }

    @Override
    public void cb$clearNoRender() {
        this.noRender = false;
    }

    public ItemRenderer getItemRenderer() {
        return ItemRenderer.CB$INSTANCE;
    }
}
