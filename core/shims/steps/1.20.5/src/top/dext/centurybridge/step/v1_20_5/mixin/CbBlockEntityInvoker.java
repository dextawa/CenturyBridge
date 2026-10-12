package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import top.dext.centurybridge.step.v1_20_5.rt.CbBlockEntityNbt;

/** BlockEntity's protected saveAdditional, for a mod's own this.saveAdditional(tag) (CbBlockEntityNbt) */
@Mixin(net.minecraft.class_2586.class)
public interface CbBlockEntityInvoker extends CbBlockEntityNbt.Saver {

    @Override
    @Invoker("method_11007")
    void cb$saveAdditional(net.minecraft.class_2487 tag, net.minecraft.class_7225.class_7874 registries);
}
