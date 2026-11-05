package top.dext.centurybridge.step.v1_21_11.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_801;

/**
 * 1.21.10 ItemModelGenerator.Span (class_801$class_802, reached through a mod's access widener): a run of edge pixels
 * facing one way (facing, min, max, anchor). 1.21.11 dropped the class (its generator collects class_801$class_12295
 * pixel faces instead); the facade is 1.21.10's data holder: the constructor sets min = max = start, expand(pos)
 * widens [min, max] to pos (javap -c -p of 1.21.10's class). SpanFacing (class_801$class_803) survives in 1.21.11.
 */
@Environment(EnvType.CLIENT)
public final class ItemModelGeneratorSpan {
    public final class_801.class_803 field_4271;
    public int field_4274;
    public int field_4273;
    public final int field_4272;

    public ItemModelGeneratorSpan(class_801.class_803 facing, int start, int anchor) {
        this.field_4271 = facing;
        this.field_4274 = start;
        this.field_4273 = start;
        this.field_4272 = anchor;
    }

    /** expand(pos) */
    public void method_3483(int pos) {
        if (pos < this.field_4274) {
            this.field_4274 = pos;
        } else if (pos > this.field_4273) {
            this.field_4273 = pos;
        }
    }

    /** getFacing() */
    public class_801.class_803 method_3484() {
        return this.field_4271;
    }

    /** getMin() */
    public int method_3487() {
        return this.field_4274;
    }

    /** getMax() */
    public int method_3485() {
        return this.field_4273;
    }

    /** getAnchor() */
    public int method_3486() {
        return this.field_4272;
    }
}
