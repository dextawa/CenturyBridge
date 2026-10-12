package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * LocalPlayer's 1.20.1 getServerBrand() / setServerBrand(brand). In 1.20.1 the brand lived on the player
 * -- vanilla copied the connection's brand into it on login and whenever the server sent it, and the debug
 * screen and crash reports read it there; 1.20.2 keeps the one brand on the connection's network handler
 * and reads it there. So the old pair reads and writes that brand: what vanilla shows is what a mod set.
 */
@Mixin(net.minecraft.class_746.class)
public abstract class CbGuiLocalPlayerBridge {

    /** 1.20.1 getServerBrand() */
    public String method_3135() {
        return ((net.minecraft.class_746) (Object) this).field_3944.method_52790();
    }

    /** 1.20.1 setServerBrand(brand) */
    public void method_3146(String brand) {
        ((CbGuiNetworkBrandAccessor) ((net.minecraft.class_746) (Object) this).field_3944).cb$setBrand(brand);
    }
}
