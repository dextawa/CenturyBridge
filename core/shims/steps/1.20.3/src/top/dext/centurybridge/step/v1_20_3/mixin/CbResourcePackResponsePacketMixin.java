package top.dext.centurybridge.step.v1_20_3.mixin;

import org.spongepowered.asm.mixin.Mixin;

/** ServerboundResourcePackPacket.getAction() of 1.20.2 on 1.20.3's record: its action accessor (virtually, see overrideRenames) */
@Mixin(net.minecraft.class_2856.class)
public abstract class CbResourcePackResponsePacketMixin {

    public net.minecraft.class_2856.class_2857 method_32308() {
        return ((net.minecraft.class_2856) (Object) this).comp_2164();
    }
}
