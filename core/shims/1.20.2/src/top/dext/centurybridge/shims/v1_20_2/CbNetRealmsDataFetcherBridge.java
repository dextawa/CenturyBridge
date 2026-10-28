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
 * RealmsDataFetcher.liveStatsTask: 1.20.1 polled the live player lists of the player's Realms every
 * 10 seconds for whoever subscribed (its main screen did); 1.20.2 dropped the task, the client call
 * (RealmsClient.getLiveStats) stays. Put back as 1.20.1 built it; a task only runs while subscribed.
 */
@Mixin(class_7578.class)
public abstract class CbNetRealmsDataFetcherBridge {

    @Shadow
    @Final
    @org.spongepowered.asm.mixin.Shadow
    public class_7581 field_39682;

    /** was liveStatsTask */
    @org.spongepowered.asm.mixin.Shadow
    public class_7581.class_7586<class_4882> field_39684;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$liveStats(class_4341 client, CallbackInfo ci) {
        field_39684 = field_39682.method_44629("live stats", client::method_21018, Duration.ofSeconds(10L),
            class_7587.field_39714);
    }
}
