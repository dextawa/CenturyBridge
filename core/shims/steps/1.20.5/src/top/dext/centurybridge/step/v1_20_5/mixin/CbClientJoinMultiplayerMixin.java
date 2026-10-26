package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * JoinMultiplayerScreen.setToolTip(lines): 1.20.4 kept the lines and drew them after the screen, once, then
 * cleared them every frame; 1.20.5 queues a deferred tooltip through Screen.setTooltip(List) (what its own
 * server entries call now). The same lines, the same one-frame lifetime.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_500.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientJoinMultiplayerMixin {

    /** 1.20.4 JoinMultiplayerScreen.setToolTip(lines) */
    public void method_2528(java.util.List<net.minecraft.class_2561> lines) {
        ((net.minecraft.class_437) (Object) this).method_47414(
            com.google.common.collect.Lists.transform(lines, net.minecraft.class_2561::method_30937));
    }
}
