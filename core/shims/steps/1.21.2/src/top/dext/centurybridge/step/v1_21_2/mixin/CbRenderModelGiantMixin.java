package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 GiantZombieModel.isAggressive(Giant): false; and javac's bridge from the AbstractZombieModel erasure */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_3969.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderModelGiantMixin {

    public boolean method_17792(net.minecraft.class_1570 giant) {
        return false;
    }

    public boolean method_17790(net.minecraft.class_1588 monster) {
        return this.method_17792((net.minecraft.class_1570) monster);
    }
}
