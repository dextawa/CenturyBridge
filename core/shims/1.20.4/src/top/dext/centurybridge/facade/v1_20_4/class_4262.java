package top.dext.centurybridge.facade.v1_20_4;

/**
 * Facade for 1.20.1 net/minecraft/class_4262 (AbstractGlassBlock), deleted at 1.20.3
 * when vanilla folded it into class_8923 (TransparentBlock). TransparentBlock is its
 * real successor: it carries exactly the three overrides that made AbstractGlassBlock
 * glass (empty visual shape, shade brightness 1.0, skylight passes down), so a mod's
 * glass subclass keeps the occlusion and lighting it had. Reached via classRenames
 * (every reference, not only the extends clause). Member names are intermediary tokens.
 */
public abstract class class_4262 extends net.minecraft.class_8923 {

    protected class_4262(net.minecraft.class_4970.class_2251 properties) {
        super(properties);
    }
}
