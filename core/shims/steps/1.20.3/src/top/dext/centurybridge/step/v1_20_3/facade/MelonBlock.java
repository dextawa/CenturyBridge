package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_2195;
import net.minecraft.class_2246;
import net.minecraft.class_2513;
import net.minecraft.class_4970;

/**
 * 1.20.2 MelonBlock (gone in 1.20.3, whose melon is a plain Block): the StemGrownBlock of the melon stems.
 * Blocks.MELON_STEM / ATTACHED_MELON_STEM are still a StemBlock and an AttachedStemBlock in 1.20.3.
 */
public class MelonBlock extends StemGrownBlock {

    public MelonBlock(class_4970.class_2251 properties) {
        super(properties);
    }

    @Override
    public class_2513 method_10679() {
        return (class_2513) class_2246.field_46287;
    }

    @Override
    public class_2195 method_10680() {
        return (class_2195) class_2246.field_46285;
    }
}
