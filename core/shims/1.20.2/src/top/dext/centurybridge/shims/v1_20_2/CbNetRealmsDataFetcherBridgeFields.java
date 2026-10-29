package top.dext.centurybridge.shims.v1_20_2;

import java.time.Duration;
import net.minecraft.class_4341;
import net.minecraft.class_4882;
import net.minecraft.class_7578;
import net.minecraft.class_7581;
import net.minecraft.class_7587;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The fields of CbNetRealmsDataFetcherBridge that restore V's fields on the target, at the layer's CbVanillaReads.PRIORITY (mixin_priority.py
 * rule 3: below every earlier layer's mixin, so a climbing earlier layer's @Shadow of them resolves); CbNetRealmsDataFetcherBridge
 * keeps its injectors at the default priority and shadows these.
 */
@org.spongepowered.asm.mixin.Mixin(value = class_7578.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsDataFetcherBridgeFields {

    /** was liveStatsTask */
    public class_7581.class_7586<class_4882> field_39684;
}
