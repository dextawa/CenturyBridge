package cb.smx4n.mixin;

import cb.smx4n.Driver;
import java.util.Map;
import net.minecraft.class_1044;
import net.minecraft.class_1060;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Native 1.21.4 mixins on TextureManager.register(id, texture): HEAD, RETURN, and a @Redirect of its Map.put. */
@Mixin(class_1060.class)
public abstract class RegisterMixin {
    @Inject(method = "method_4616", at = @At("HEAD"))
    private void cb$head(class_2960 id, class_1044 texture, CallbackInfo ci) {
        Driver.REG_HEAD.incrementAndGet();
        Driver.registered(texture);
    }

    @Inject(method = "method_4616", at = @At("RETURN"))
    private void cb$ret(class_2960 id, class_1044 texture, CallbackInfo ci) {
        Driver.REG_RETURN.incrementAndGet();
    }

    @Redirect(method = "method_4616", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object cb$put(Map<Object, Object> map, Object key, Object value) {
        Driver.REG_PUT.incrementAndGet();
        return map.put(key, value);
    }
}
