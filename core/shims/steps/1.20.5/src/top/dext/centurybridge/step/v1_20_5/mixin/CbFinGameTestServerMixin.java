package top.dext.centurybridge.step.v1_20_5.mixin;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.class_2470;
import net.minecraft.class_4514;
import net.minecraft.class_6306;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinGameTests;

/**
 * GameTestServer created by a 1.20.4 mod with its own batches (rt/CbFinGameTests.createServer): 1.20.5's
 * initServer batches the test functions itself; the 1.20.4 batches replace those, made runnable as 1.20.4's
 * server ran them (GameTestRunner.runTestBatches(batches, corner, Rotation.NONE, overworld, ...)).
 */
@Mixin(class_6306.class)
public abstract class CbFinGameTestServerMixin implements CbFinGameTests.LegacyBatchServer {

    @Shadow
    private List<class_4514> field_33158;

    @Unique
    private List<class_4514> cb$legacyBatches;

    @Override
    public void cb$setLegacyBatches(List<class_4514> batches) {
        this.cb$legacyBatches = batches;
    }

    @Inject(method = "method_3823()Z", at = @At("RETURN"))
    private void cb$legacyBatches(CallbackInfoReturnable<Boolean> cir) {
        if (this.cb$legacyBatches != null) {
            this.field_33158 = Lists.newArrayList(CbFinGameTests.runnable(this.cb$legacyBatches, class_2470.field_11467,
                ((class_6306) (Object) this).method_30002()));
        }
    }
}
