package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;

/** StatsScreen is a StatsUpdateListener again (facade/StatsUpdateListener), as in 1.20.4: its own onStatsUpdated() answers the interface */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_447.class)
public abstract class CbClientStatsScreenMixin implements top.dext.centurybridge.step.v1_20_5.facade.StatsUpdateListener {
}
