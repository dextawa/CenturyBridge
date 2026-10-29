package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Three public boolean options were dropped from GameOptions at 1.20.2 (debugEnabled moved into
 * DebugHud, the other two were retired). A mod reading one compiles to GETFIELD against class_315,
 * which fails at runtime once the field is gone -- but Mixin can put the field back, and a field
 * that exists is all the instruction needs.
 *
 * Plain fields rather than accessors: the call sites are field reads, so only a real field satisfies
 * them. The values are the settings' vanilla defaults. Writes land here and have no effect on vanilla,
 * which matches reality. (Ported from the 1.20.4 client battery; ten corpus mods read field_1866.)
 */
@org.spongepowered.asm.mixin.Mixin(value = class_315.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class GameOptionsBridge {
    /** debugEnabled, removed @1.20.1->1.20.2 */
    public boolean field_1866 = true;
    /** removed @1.20.1->1.20.2 */
    public boolean field_1880 = false;
    /** removed @1.20.1->1.20.2 */
    public boolean field_1893 = false;
}
