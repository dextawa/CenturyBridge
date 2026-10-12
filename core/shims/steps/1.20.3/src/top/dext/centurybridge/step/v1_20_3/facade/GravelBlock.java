package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_4970;
import net.minecraft.class_8805;
import net.minecraft.class_8812;

/**
 * 1.20.2 GravelBlock (gone in 1.20.3): a FallingBlock whose falling dust is -8356741. 1.20.3 builds gravel
 * as a ColoredFallingBlock with that colour, which answers getDustColor with it.
 */
public class GravelBlock extends class_8812 {

    public GravelBlock(class_4970.class_2251 properties) {
        super(new class_8805(-8356741), properties);
    }
}
