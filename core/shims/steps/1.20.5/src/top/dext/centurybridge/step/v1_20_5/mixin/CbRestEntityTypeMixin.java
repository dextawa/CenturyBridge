package top.dext.centurybridge.step.v1_20_5.mixin;

import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_20_5.rt.CbVanillaReads;

/**
 * EntityType.create(level, consumer, pos, reason, ..): where 1.20.4's create(level, tag, consumer, ..)
 * handed the new mob's finalizeSpawn the tag it was given, 1.20.5's passes none. The create takes the
 * tag a 1.20.4 caller handed over at its start and pairs it with that finalizeSpawn call
 * (rt/CbRestSpawn; CbRestEntityTypeMixinHooks, default priority); the pairing is cleared after the call.
 * <p>
 * 1.20.4 getAABB(x, y, z): the type's dimensions as a box standing at the point -- 1.20.5's
 * getSpawnAABB scales it by the type's spawn dimensions scale, which 1.20.4 did not have.
 */
@Mixin(value = net.minecraft.class_1299.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbRestEntityTypeMixin {

    /** 1.20.4 getAABB(x, y, z) */
    public net.minecraft.class_238 method_17683(double x, double y, double z) {
        net.minecraft.class_1299<?> type = (net.minecraft.class_1299<?>) (Object) this;
        float half = type.method_17685() / 2.0F;
        return new net.minecraft.class_238(x - half, y, z - half, x + half, y + type.method_17686(), z + half);
    }
}
