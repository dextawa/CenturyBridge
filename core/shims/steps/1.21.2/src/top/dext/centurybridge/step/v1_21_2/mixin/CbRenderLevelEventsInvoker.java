package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/** LevelEventHandler's jukebox song handling (private), for CbRenderLevel */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_9959.class)
public interface CbRenderLevelEventsInvoker {

    @Invoker("method_62193")
    void cb$playJukeboxSong(net.minecraft.class_6880<net.minecraft.class_9793> song, net.minecraft.class_2338 pos);

    @Invoker("method_62195")
    void cb$stopJukeboxSongAndNotifyNearby(net.minecraft.class_2338 pos);
}
