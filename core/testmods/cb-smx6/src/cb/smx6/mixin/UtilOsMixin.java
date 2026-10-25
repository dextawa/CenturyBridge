package cb.smx6.mixin;

import cb.smx6.Driver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A 1.20.6 mod on Util.OS.getOpenUrlArguments(url) (class_156$class_158.method_674(URL)): records the URL vanilla asks
 * with and answers the command "true" (so nothing is opened on the test server).
 */
@Mixin(net.minecraft.class_156.class_158.class)
public abstract class UtilOsMixin {
    @Inject(method = "method_674(Ljava/net/URL;)[Ljava/lang/String;", at = @At("HEAD"), cancellable = true, remap = false)
    private void cbsmx6$open(java.net.URL url, CallbackInfoReturnable<String[]> cir) {
        Driver.openAsked++;
        Driver.openUrl = String.valueOf(url);
        cir.setReturnValue(new String[] {"true"});
    }
}
