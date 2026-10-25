package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_4048;
import net.minecraft.class_4050;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbLegacyPose;
import top.dext.centurybridge.rt.v1_20_2.CbVanillaReads;

/**
 * 1.20.1's Entity.canEnterPose / getBoundingBoxForPose back (s1misc pose bridge), 1.20.1 bodies:
 * the box the entity's dimensions in that pose span at its position, and whether the level has
 * room for it (deflated by 1.0E-7). 1.20.2's player tests its own copy of that, which
 * CbEntityPoseVanillaMixin answers through canEnterPose (see also CbEntityPose). Declared public;
 * 1.20.1's were protected.
 */
@Mixin(value = class_1297.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityPoseLegacy implements CbLegacyPose {

    @CbVanillaReads("CbEntityPoseVanillaMixin: Player.canPlayerFitWithinBlocksAndEntitiesWhen answers through it")
    @Override
    public boolean method_20233(class_4050 pose) {
        class_1297 self = (class_1297) (Object) this;
        return self.method_37908().method_8587(self, this.method_20343(pose).method_1011(1.0E-7));
    }

    @CbVanillaReads("CbEntityPoseVanillaMixin: Player.canPlayerFitWithinBlocksAndEntitiesWhen answers through canEnterPose, which tests it")
    @Override
    public class_238 method_20343(class_4050 pose) {
        class_1297 self = (class_1297) (Object) this;
        class_4048 dims = self.method_18377(pose);
        float half = dims.field_18067 / 2.0F;
        class_243 from = new class_243(self.method_23317() - (double) half, self.method_23318(), self.method_23321() - (double) half);
        class_243 to = new class_243(self.method_23317() + (double) half, self.method_23318() + (double) dims.field_18068,
            self.method_23321() + (double) half);
        return new class_238(from, to);
    }

    @Override
    public boolean cb$legacyCanEnterPose(class_4050 pose) {
        return this.method_20233(pose);
    }
}
