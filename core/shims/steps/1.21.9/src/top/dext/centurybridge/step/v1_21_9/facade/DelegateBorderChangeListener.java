package top.dext.centurybridge.step.v1_21_9.facade;

import net.minecraft.class_2780;
import net.minecraft.class_2784;

/**
 * 1.21.8's BorderChangeListener.DelegateBorderChangeListener (class_2780$class_3976, gone in 1.21.9, where every
 * dimension keeps its own border and vanilla no longer chains them): a listener that repeats each change of the
 * border it listens to on the border it was built with. Bodies as 1.21.8 had them (javap -c): every
 * BorderChangeListener method calls the same-meaning setter of the delegate, all of which 1.21.9's WorldBorder
 * still declares. Public and extendable, as 1.21.8's was; every method keeps its intermediary name, so a mod's
 * override and super call land here.
 */
public class DelegateBorderChangeListener implements class_2780 {
    public final class_2784 field_17652;

    public DelegateBorderChangeListener(class_2784 worldBorder) {
        this.field_17652 = worldBorder;
    }

    /** onBorderSizeSet */
    @Override
    public void method_11934(class_2784 border, double size) {
        this.field_17652.method_11969(size);
    }

    /** onBorderSizeLerping */
    @Override
    public void method_11931(class_2784 border, double oldSize, double newSize, long time) {
        this.field_17652.method_11957(oldSize, newSize, time);
    }

    /** onBorderCenterSet */
    @Override
    public void method_11930(class_2784 border, double x, double z) {
        this.field_17652.method_11978(x, z);
    }

    /** onBorderSetWarningTime */
    @Override
    public void method_11932(class_2784 border, int warningTime) {
        this.field_17652.method_11975(warningTime);
    }

    /** onBorderSetWarningBlocks */
    @Override
    public void method_11933(class_2784 border, int warningBlocks) {
        this.field_17652.method_11967(warningBlocks);
    }

    /** onBorderSetDamagePerBlock */
    @Override
    public void method_11929(class_2784 border, double damagePerBlock) {
        this.field_17652.method_11955(damagePerBlock);
    }

    /** onBorderSetDamageSafeZOne */
    @Override
    public void method_11935(class_2784 border, double safeZone) {
        this.field_17652.method_11981(safeZone);
    }
}
