package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_4970;
import net.minecraft.class_8805;
import net.minecraft.class_8812;

/**
 * 1.20.2 SandBlock (gone in 1.20.3): a FallingBlock with its own dust colour. 1.20.3's ColoredFallingBlock
 * keeps the colour and answers getDustColor with it.
 */
public class SandBlock extends class_8812 {

    /** 1.20.2's (private) dust colour field */
    public final int field_11462;

    public SandBlock(int dustColor, class_4970.class_2251 properties) {
        super(new class_8805(dustColor), properties);
        this.field_11462 = dustColor;
    }
}
