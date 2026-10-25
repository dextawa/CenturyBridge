package top.dext.centurybridge.step.v1_21.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21.rt.CbVanillaReads;

/** Util.OS.OSX (class_156$class_158$2): 1.20.6's override of getOpenUrlArguments(URL), restored (see CbUtilOsMixin) */
@Mixin(targets = "net.minecraft.class_156$class_158$2", priority = 1)
public abstract class CbUtilOsMacMixin {

    /** 1.20.6 Util.OS.OSX.getOpenUrlArguments(url) */
    @CbVanillaReads(value = "CbUtilOsMixin: openUri's process start (method_671) takes its command line from this override through "
        + "the restored Util.OS.getOpenUrlArguments(URL) when the URI is a URL with the same spelling; the body is 1.20.6's",
        carries = {"Inject@HEAD", "Inject@RETURN", "Inject@TAIL", "ModifyReturnValue@RETURN", "Inject@INVOKE", "Redirect@INVOKE",
            "ModifyArg@INVOKE", "ModifyArgs@INVOKE", "ModifyExpressionValue@INVOKE", "WrapOperation@INVOKE", "Inject@CONSTANT",
            "ModifyConstant@CONSTANT"})
    public String[] method_674(java.net.URL url) {
        return new String[] {"open", url.toString()};
    }
}
