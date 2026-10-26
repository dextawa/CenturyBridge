package top.dext.centurybridge.step.v1_20_3.mixin;

import java.util.Optional;
import net.minecraft.class_151;
import net.minecraft.class_2338;
import net.minecraft.class_2382;
import net.minecraft.class_2415;
import net.minecraft.class_2470;
import net.minecraft.class_2633;
import net.minecraft.class_2680;
import net.minecraft.class_2776;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3485;
import net.minecraft.class_3488;
import net.minecraft.class_3492;
import net.minecraft.class_3499;
import net.minecraft.class_3532;
import net.minecraft.class_3544;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldStructureBlockV;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * StructureBlockEntity's 1.20.2 loading methods, back with their 1.20.2 bodies (covers): loadStructure(level,
 * unlessSameSize) looks the structure up (an invalid name or a missing structure loads nothing), and
 * loadStructure(level, unlessSameSize, template) takes the template's author when it has one and its size,
 * announces a size change to the clients, and places the structure unless asked to skip one of another size.
 * getStructurePath() is the structure name's path, "" without one. 1.20.3's placeStructure /
 * placeStructureIfSameSize / loadStructureInfo are the same steps cut differently (the author is overwritten
 * by an empty one, a size change is not sent).
 */
@Mixin(value = class_2633.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldStructureBlockEntityMixin implements CbWorldStructureBlockV {

    @Shadow
    private class_2960 field_12102;

    @Shadow
    private String field_12104;

    @Shadow
    private class_2338 field_12092;

    @Shadow
    private class_2382 field_12100;

    @Shadow
    private class_2415 field_12093;

    @Shadow
    private class_2470 field_12105;

    @Shadow
    private class_2776 field_12094;

    @Shadow
    private boolean field_12099;

    @Shadow
    private float field_12101;

    @Shadow
    private long field_12103;

    @Override
    public boolean method_11368(class_3218 level, boolean unlessSameSize) {
        if (this.field_12094 != class_2776.field_12697 || this.field_12102 == null) {
            return false;
        }
        class_3485 manager = level.method_14183();
        Optional<class_3499> template;
        try {
            template = manager.method_15094(this.field_12102);
        } catch (class_151 e) {
            return false;
        }
        if (template.isEmpty()) {
            return false;
        }
        return this.method_21864(level, unlessSameSize, template.get());
    }

    @Override
    public boolean method_21864(class_3218 level, boolean unlessSameSize, class_3499 template) {
        class_2633 self = (class_2633) (Object) this;
        class_2338 pos = self.method_11016();
        if (!class_3544.method_15438(template.method_15181())) {
            this.field_12104 = template.method_15181();
        }
        class_2382 size = template.method_15160();
        boolean sameSize = this.field_12100.equals(size);
        if (!sameSize) {
            this.field_12100 = size;
            self.method_5431();
            class_2680 state = level.method_8320(pos);
            level.method_8413(pos, state, state, 3);
        }
        if (unlessSameSize && !sameSize) {
            return false;
        }
        class_3492 settings = new class_3492().method_15125(this.field_12093).method_15123(this.field_12105)
            .method_15133(this.field_12099);
        if (this.field_12101 < 1.0F) {
            settings.method_16183().method_16184(new class_3488(class_3532.method_15363(this.field_12101, 0.0F, 1.0F)))
                .method_15112(class_2633.method_20048(this.field_12103));
        }
        class_2338 at = pos.method_10081(this.field_12092);
        template.method_15172(level, at, at, settings, class_2633.method_20048(this.field_12103), 2);
        return true;
    }

    @Override
    public String method_21865() {
        return this.field_12102 == null ? "" : this.field_12102.method_12832();
    }
}
