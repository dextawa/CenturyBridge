package top.dext.centurybridge.step.v1_21_4.mixin;

import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_2586;
import net.minecraft.class_2591;
import net.minecraft.class_2680;
import net.minecraft.class_3417;
import net.minecraft.class_3419;
import net.minecraft.class_4466;
import net.minecraft.class_4482;
import net.minecraft.class_5712;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestBeehive;

/**
 * BeehiveBlockEntity.addOccupant(entity) of 1.21.3 (CbRestBeehive): 1.21.4's takes only a Bee and also drops
 * its leash. 1.21.3's body: any entity, stopped riding and ejected, stored as an occupant; a bee's saved
 * flower kept (unless fire is near, then on a coin flip); the enter sound and block change event; the entity
 * discarded; and BlockEntity.setChanged() -- the plain one, not the hive's own override, as 1.21.3's
 * super.setChanged() was.
 */
@Mixin(class_4482.class)
public abstract class CbRestBeehiveMixin extends class_2586 implements CbRestBeehive.Legacy {

    @Shadow
    private List<?> field_20423;

    @Shadow
    private class_2338 field_20424;

    @Shadow
    public abstract void method_35292(class_4482.class_9309 occupant);

    @Shadow
    private boolean method_21857() {
        throw new AssertionError();
    }

    private CbRestBeehiveMixin(class_2591<?> type, class_2338 pos, class_2680 state) {
        super(type, pos, state);
    }

    @Override
    public void cb$addOccupant(class_1297 entity) {
        if (this.field_20423.size() >= 3) {
            return;
        }
        entity.method_5848();
        entity.method_5772();
        this.method_35292(class_4482.class_9309.method_57586(entity));
        if (this.field_11863 != null) {
            if (entity instanceof class_4466 bee && bee.method_21779()
                    && (!this.method_21857() || this.field_11863.field_9229.method_43056())) {
                this.field_20424 = bee.method_21778();
            }
            class_2338 pos = this.method_11016();
            this.field_11863.method_43128(null, pos.method_10263(), pos.method_10264(), pos.method_10260(),
                class_3417.field_20609, class_3419.field_15245, 1.0F, 1.0F);
            this.field_11863.method_43276(class_5712.field_28733, pos, class_5712.class_7397.method_43286(entity, this.method_11010()));
        }
        entity.method_31472();
        super.method_5431();
    }
}
