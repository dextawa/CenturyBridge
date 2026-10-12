package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The connection's latency: 1.20.1 kept it in ServerPlayer.latency, which mods could write (CbServerPlayers). */
@Mixin(net.minecraft.class_8609.class)
public interface CbCommonListenerLatencyAccess {

    @Accessor("field_45019")
    void cb$setLatency(int latency);
}
