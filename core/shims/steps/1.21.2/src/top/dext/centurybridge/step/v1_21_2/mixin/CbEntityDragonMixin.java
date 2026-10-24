package top.dext.centurybridge.step.v1_21_2.mixin;

import net.minecraft.class_1299;
import net.minecraft.class_1510;
import net.minecraft.class_1937;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.step.v1_21_2.rt.CbEntityDragon;

/** An EnderDragon's flight history knows its dragon, whose 1.21.1 getLatencyPos override answers it (CbEntityDragon). */
@Mixin(class_1510.class)
public abstract class CbEntityDragonMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$owner(class_1299<? extends class_1510> type, class_1937 level, CallbackInfo ci) {
        ((CbEntityDragon.History) ((class_1510) (Object) this).field_52493).cb$owner((class_1510) (Object) this);
    }
}
