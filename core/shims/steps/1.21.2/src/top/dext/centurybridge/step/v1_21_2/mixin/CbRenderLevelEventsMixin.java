package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbRenderLevel;

/**
 * 1.21.2's LevelEventHandler hands its level events and jukebox songs to the renderer's 1.21.1 methods when the renderer
 * is a mod subclass of LevelRenderer: 1.21.1 called LevelRenderer.levelEvent / globalLevelEvent (from ClientLevel) and
 * playJukeboxSong / stopJukeboxSongAndNotifyNearby (from levelEvent), so such a subclass's overrides ran there. The
 * subclass's super call reaches this handler again and runs it as is (CbRenderLevel.routes / route).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_9959.class)
public abstract class CbRenderLevelEventsMixin {

    @Shadow
    @Final
    private net.minecraft.class_761 field_53070;

    @Inject(method = "method_62194", at = @At("HEAD"), cancellable = true)
    private void cb$levelEvent(int type, net.minecraft.class_2338 pos, int data, CallbackInfo ci) {
        if (CbRenderLevel.routes(this.field_53070, CbRenderLevel.LEVEL_EVENT)) {
            ci.cancel();
            CbRenderLevel.route(CbRenderLevel.LEVEL_EVENT, () -> ((CbRenderLevel.LegacyEvents) this.field_53070).method_8567(type, pos, data));
        }
    }

    @Inject(method = "method_62187", at = @At("HEAD"), cancellable = true)
    private void cb$globalLevelEvent(int type, net.minecraft.class_2338 pos, int data, CallbackInfo ci) {
        if (CbRenderLevel.routes(this.field_53070, CbRenderLevel.GLOBAL_LEVEL_EVENT)) {
            ci.cancel();
            CbRenderLevel.route(CbRenderLevel.GLOBAL_LEVEL_EVENT, () -> ((CbRenderLevel.LegacyEvents) this.field_53070).method_8564(type, pos, data));
        }
    }

    @Inject(method = "method_62193", at = @At("HEAD"), cancellable = true)
    private void cb$playSong(net.minecraft.class_6880<net.minecraft.class_9793> song, net.minecraft.class_2338 pos, CallbackInfo ci) {
        if (CbRenderLevel.routes(this.field_53070, CbRenderLevel.PLAY_SONG)) {
            ci.cancel();
            CbRenderLevel.route(CbRenderLevel.PLAY_SONG, () -> ((CbRenderLevel.LegacyEvents) this.field_53070).method_60891(song, pos));
        }
    }

    @Inject(method = "method_62195", at = @At("HEAD"), cancellable = true)
    private void cb$stopSong(net.minecraft.class_2338 pos, CallbackInfo ci) {
        if (CbRenderLevel.routes(this.field_53070, CbRenderLevel.STOP_SONG)) {
            ci.cancel();
            CbRenderLevel.route(CbRenderLevel.STOP_SONG, () -> ((CbRenderLevel.LegacyEvents) this.field_53070).method_60889(pos));
        }
    }
}
