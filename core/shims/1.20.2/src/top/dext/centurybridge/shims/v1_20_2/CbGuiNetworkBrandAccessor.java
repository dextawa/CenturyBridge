package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** The connection's server brand, which 1.20.2 keeps on the network handler (CbGuiLocalPlayerBridge). */
@Mixin(net.minecraft.class_8673.class)
public interface CbGuiNetworkBrandAccessor {

    @Accessor("field_45591")
    void cb$setBrand(String brand);
}
