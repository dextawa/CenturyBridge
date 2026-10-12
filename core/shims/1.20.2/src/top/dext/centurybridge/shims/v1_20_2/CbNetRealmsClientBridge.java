package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_4341;
import net.minecraft.class_4346;
import net.minecraft.class_4355;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealms;

/**
 * RealmsClient: the two availability queries 1.20.1 had (vanilla called neither on a mod's client;
 * 1.20.2 dropped them), and the response text of a failed request, which 1.20.1's
 * RealmsServiceException kept as rawResponse and 1.20.2 parses away (CbNetRealms.cbLegacyView).
 */
@Mixin(class_4341.class)
public abstract class CbNetRealmsClientBridge {

    @Shadow
    private String method_21011(String path) {
        throw new AssertionError();
    }

    @Shadow
    private String method_20998(class_4346<?> request) throws class_4355 {
        throw new AssertionError();
    }

    /** was mcoEnabled(): whether the service says Realms is available ("mco/available") */
    public Boolean method_21021() throws class_4355 {
        String url = method_21011("mco/available");
        return Boolean.valueOf(method_20998(class_4346.method_21040(url)));
    }

    /** was stageAvailable(): whether the service says the stage environment is ("mco/stageAvailable") */
    public Boolean method_21024() throws class_4355 {
        String url = method_21011("mco/stageAvailable");
        return Boolean.valueOf(method_20998(class_4346.method_21040(url)));
    }

    /** the text execute() hands RealmsError.parse right before it builds the exception it throws */
    @ModifyArg(method = "method_20998", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4345;method_30162(ILjava/lang/String;)Lnet/minecraft/class_4345;"),
        index = 1, require = 1)
    private String cb$rememberBody(String body) {
        CbNetRealms.cbRememberBody(body);
        return body;
    }
}
