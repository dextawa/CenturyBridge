package top.dext.centurybridge.step.v1_21.mixin;

import java.util.ArrayDeque;
import java.util.function.Function;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.class_1058;
import net.minecraft.class_1087;
import net.minecraft.class_1088;
import net.minecraft.class_1091;
import net.minecraft.class_2960;
import net.minecraft.class_1100;
import net.minecraft.class_3665;
import net.minecraft.class_4730;
import net.minecraft.class_7775;
import net.minecraft.class_793;
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
 * handed each model's bake and 1.21 no longer passes (CbRenderModelBake.idOf). Its bakeUncached runs each model's bake
 * with that id as the one being baked on the thread (CbRenderModelBake.baking: only while a converted mod mixes the
 * restored FaceBakery.bakeQuad(.., id), which CbRenderFaceBakeryRouteMixin hands it).
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

    @WrapOperation(method = "method_61077(Lnet/minecraft/class_1100;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_793;method_3446(Lnet/minecraft/class_7775;Lnet/minecraft/class_793;Ljava/util/function/Function;Lnet/minecraft/class_3665;Z)Lnet/minecraft/class_1087;"))
    private class_1087 cb$bakeGenerated(class_793 model, class_7775 baker, class_793 parent, Function<class_4730, class_1058> sprites, class_3665 state,
                                        boolean gui3d, Operation<class_1087> original) {
        return CbRenderModelBake.baking(this.cb$currentModelId(), () -> original.call(model, baker, parent, sprites, state, gui3d));
    }

    @WrapOperation(method = "method_61077(Lnet/minecraft/class_1100;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_1100;method_4753(Lnet/minecraft/class_7775;Ljava/util/function/Function;Lnet/minecraft/class_3665;)Lnet/minecraft/class_1087;"))
    private class_1087 cb$bake(class_1100 model, class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state,
                               Operation<class_1087> original) {
        return CbRenderModelBake.baking(this.cb$currentModelId(), () -> original.call(model, baker, sprites, state));
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
