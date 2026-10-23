package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * LevelRenderer's jukebox sounds, game and level, and its notifying of nearby entities: 1.20.6's playStreamingMusic(sound, pos) on them (CbRenderLevel).
 */
@Mixin(net.minecraft.class_761.class)
public interface CbRenderLevelRendererAccess {

    @Accessor("field_4119")
    java.util.Map<net.minecraft.class_2338, net.minecraft.class_1113> cb$playingJukeboxSongs();

    @Accessor("field_4088")
    net.minecraft.class_310 cb$minecraft();

    @Accessor("field_4085")
    net.minecraft.class_638 cb$level();

    @Invoker("method_3247")
    void cb$notifyNearbyEntities(net.minecraft.class_1937 level, net.minecraft.class_2338 pos, boolean playing);
}
