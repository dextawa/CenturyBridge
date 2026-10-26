package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * Request.header(key, value) (method_35685), 1.20.2's body: the header added to the connection, the
 * request answered for chaining. 1.20.3 dropped the general setter (its only header is the snapshot one).
 */
@Mixin(value = net.minecraft.class_4346.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRealmsRequestMixin {

    /** connection */
    @Shadow
    protected java.net.HttpURLConnection field_19596;

    public net.minecraft.class_4346<?> method_35685(String key, String value) {
        field_19596.addRequestProperty(key, value);
        return (net.minecraft.class_4346<?>) (Object) this;
    }
}
