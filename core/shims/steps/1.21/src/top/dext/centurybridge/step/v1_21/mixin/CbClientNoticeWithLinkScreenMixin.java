package top.dext.centurybridge.step.v1_21.mixin;

import java.net.URI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbClientGui;

/**
 * NoticeWithLinkScreen made the 1.20.6 way (with a String link): copying and opening use the string itself, as
 * 1.20.6's buttons did (Util.OS.openUri(String) logs and does nothing for a string that is no URI).
 */
@Mixin(net.minecraft.class_8669.class)
public abstract class CbClientNoticeWithLinkScreenMixin {

    @Redirect(method = "method_52749", at = @At(value = "INVOKE", target = "Ljava/net/URI;toString()Ljava/lang/String;"))
    private String cb$copiedLink(URI uri) {
        String raw = CbClientGui.cbNoticeLink((net.minecraft.class_8669) (Object) this);
        return raw != null ? raw : uri.toString();
    }

    @Redirect(method = "method_52751", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_156$class_158;method_673(Ljava/net/URI;)V"))
    private void cb$openLink(net.minecraft.class_156.class_158 os, URI uri) {
        String raw = CbClientGui.cbNoticeLink((net.minecraft.class_8669) (Object) this);
        if (raw != null) {
            os.method_670(raw);
        } else {
            os.method_673(uri);
        }
    }
}
