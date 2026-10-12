package top.dext.centurybridge.step.v1_20_3.facade;

import net.minecraft.class_4970;
import net.minecraft.class_8923;

/**
 * 1.20.2 AbstractGlassBlock (gone in 1.20.3). 1.20.3's TransparentBlock is the same class under a new
 * name: a HalfTransparentBlock with an empty visual shape, full shade brightness and skylight passing
 * through -- the three overrides AbstractGlassBlock made, which this facade inherits from it.
 */
public abstract class AbstractGlassBlock extends class_8923 {

    public AbstractGlassBlock(class_4970.class_2251 properties) {
        super(properties);
    }
}
