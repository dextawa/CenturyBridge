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
 * The injectors into 1.20.2's own methods that go with {@link CbNetRealmsClientBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(class_4341.class)
public abstract class CbNetRealmsClientBridgeHooks {
    /** the text execute() hands RealmsError.parse right before it builds the exception it throws */
    @ModifyArg(method = "method_20998", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4345;method_30162(ILjava/lang/String;)Lnet/minecraft/class_4345;"),
        index = 1, require = 1)
    private String cb$rememberBody(String body) {
        CbNetRealms.cbRememberBody(body);
        return body;
    }
}
