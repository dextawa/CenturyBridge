package top.dext.centurybridge.step.v26_1.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.animal.goat.Goat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.11 Goat members 26.1 dropped, given back (covers): addHorns() / removeHorns() set both horn flags, as
 * 1.21.11's did (SENSOR_TYPES / MEMORY_TYPES: rt/CbWallGoats).
 */
@Mixin(value = Goat.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallGoatMixin {

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_HAS_LEFT_HORN;

    @Shadow
    @Final
    private static EntityDataAccessor<Boolean> DATA_HAS_RIGHT_HORN;

    public void addHorns() {
        ((Goat) (Object) this).getEntityData().set(DATA_HAS_LEFT_HORN, true);
        ((Goat) (Object) this).getEntityData().set(DATA_HAS_RIGHT_HORN, true);
    }

    public void removeHorns() {
        ((Goat) (Object) this).getEntityData().set(DATA_HAS_LEFT_HORN, false);
        ((Goat) (Object) this).getEntityData().set(DATA_HAS_RIGHT_HORN, false);
    }
}
