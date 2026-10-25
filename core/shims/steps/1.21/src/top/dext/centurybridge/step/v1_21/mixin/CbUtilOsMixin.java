package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.step.v1_21.rt.CbUtilOs;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/**
 * Util.OS (class_156$class_158): restores 1.20.6's getOpenUrlArguments(URL) (method_674(URL)), whose body 1.21 kept over a
 * URI (getOpenUriArguments(URI), the same strings from toString and the scheme). Windows and macOS override it
 * (CbUtilOsWindowsMixin / CbUtilOsMacMixin restore those). 1.21's openUri asks for the command line in method_671; that ask
 * goes through the restored method when the URI is a URL that writes back as the same string, so a 1.20.6 mod's mixin
 * decides the command as on 1.20.6; any other URI (one no URL can stand for, or one a URL would spell differently) keeps
 * 1.21's answer.
 */
@Mixin(net.minecraft.class_156.class_158.class)
public abstract class CbUtilOsMixin implements CbUtilOs.Restored {

    /** 1.21 Util.OS.getOpenUriArguments(uri) */
    @Shadow
    protected abstract String[] method_674(java.net.URI uri);

    /** 1.20.6 Util.OS.getOpenUrlArguments(url) (protected in 1.20.6) */
    @Override
    @CbVanillaReads(value = "CbUtilOsMixin: openUri's process start (method_671) takes its command line from this method "
        + "(cb$arguments replaces 1.21's getOpenUriArguments(URI)) when the URI is a URL with the same spelling; the body is 1.20.6's "
        + "(URL.toString / URL.getProtocol / String.replace stand as in 1.20.6). For any other URI 1.21's method answers and this one "
        + "is not called",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN", "Inject@INVOKE", "Redirect@INVOKE",
            "ModifyArg@INVOKE", "ModifyArgs@INVOKE", "ModifyExpressionValue@INVOKE", "WrapOperation@INVOKE", "Inject@CONSTANT",
            "ModifyConstant@CONSTANT"})
    public String[] method_674(java.net.URL url) {
        String s = url.toString();
        if ("file".equals(url.getProtocol())) {
            s = s.replace("file:", "file://");
        }
        return new String[] {"xdg-open", s};
    }

    @Redirect(method = "method_671(Ljava/net/URI;)Ljava/lang/Process;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/class_156$class_158;method_674(Ljava/net/URI;)[Ljava/lang/String;"))
    private String[] cb$arguments(net.minecraft.class_156.class_158 os, java.net.URI uri) {
        // os is this (method_671 asks this.getOpenUriArguments(uri))
        java.net.URL url = CbUtilOs.sameUrl(uri);
        return url != null ? ((CbUtilOs.Restored) (Object) os).method_674(url) : this.method_674(uri);
    }
}
