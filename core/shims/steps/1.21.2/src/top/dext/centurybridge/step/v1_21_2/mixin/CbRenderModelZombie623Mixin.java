package top.dext.centurybridge.step.v1_21_2.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_2.rt.CbVanillaReads;

/** 1.21.1 ZombieModel.isAggressive(Zombie): zombie.isAggressive(); and javac's bridge from the AbstractZombieModel erasure */
@Environment(EnvType.CLIENT)
@Mixin(value = net.minecraft.class_623.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRenderModelZombie623Mixin {

    public boolean method_17793(net.minecraft.class_1642 zombie) {
        return zombie.method_6510();
    }

    public boolean method_17790(net.minecraft.class_1588 monster) {
        return this.method_17793((net.minecraft.class_1642) monster);
    }
}
