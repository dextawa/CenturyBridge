package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1664;
import net.minecraft.class_304;
import net.minecraft.class_315;
import net.minecraft.class_3675;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** Options.setKey / toggleModelPart of 1.21.1, back with their bodies (1.21.2 inlined them into its screens) */
@Environment(EnvType.CLIENT)
@Mixin(value = class_315.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestOptionsKeysMixin {

    /** setKey(mapping, key): the mapping bound to the key, options saved */
    public void method_1641(class_304 mapping, class_3675.class_306 key) {
        mapping.method_1422(key);
        ((class_315) (Object) this).method_1640();
    }

    /** toggleModelPart(part, enabled): the part set, the options sent to the server */
    public void method_1631(class_1664 part, boolean enabled) {
        class_315 self = (class_315) (Object) this;
        self.method_1635(part, enabled);
        self.method_1643();
    }
}
