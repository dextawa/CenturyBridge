package top.dext.centurybridge.step.v1_21.mixin;

import java.util.ArrayDeque;

import net.minecraft.class_1087;
import net.minecraft.class_1088;
import net.minecraft.class_1091;
import net.minecraft.class_2960;
import net.minecraft.class_3665;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.dext.centurybridge.step.v1_21.rt.CbRenderModelBake;

/**
 * ModelBakery.ModelBakerImpl, remembering which model it is baking: the location it was made for (a top-level
 * model) and every location bake(id, state) is asked for while that bake runs (a nested model) -- the id 1.20.6
 * handed each model's bake and 1.21 no longer passes (CbRenderModelBake.idOf).
 */
@Mixin(targets = "net.minecraft.class_1088$class_7778")
public abstract class CbRenderModelBakerMixin implements CbRenderModelBake.ModelIdSource {

    @Unique
    private class_1091 cb$location;

    @Unique
    private final ArrayDeque<class_2960> cb$ids = new ArrayDeque<>();

    @Inject(method = "<init>", at = @At("RETURN"))
    private void cb$rememberLocation(class_1088 bakery, class_1088.class_9826 sprites, class_1091 location, CallbackInfo ci) {
        this.cb$location = location;
    }

    @Inject(method = "method_45873", at = @At("HEAD"))
    private void cb$enter(class_2960 id, class_3665 state, CallbackInfoReturnable<class_1087> cir) {
        this.cb$ids.push(id);
    }

    @Inject(method = "method_45873", at = @At("RETURN"))
    private void cb$leave(class_2960 id, class_3665 state, CallbackInfoReturnable<class_1087> cir) {
        this.cb$ids.pop();
    }

    @Override
    public class_2960 cb$currentModelId() {
        class_2960 nested = this.cb$ids.peek();
        if (nested != null) {
            return nested;
        }
        return this.cb$location == null ? null : this.cb$location.comp_2875();
    }
}
