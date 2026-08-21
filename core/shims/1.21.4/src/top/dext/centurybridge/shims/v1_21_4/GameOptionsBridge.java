package top.dext.centurybridge.shims.v1_21_4;

import net.minecraft.class_315;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Three public boolean options were dropped from GameOptions across the
 * corridor. A mod reading one compiles to GETFIELD against class_315, which
 * fails at runtime once the field is gone -- but Mixin can put the field back,
 * and a field that exists is all the instruction needs.
 *
 * These are deliberately plain fields rather than accessors: the call sites are
 * field reads, so only a real field satisfies them. The values are the
 * settings' vanilla defaults, which is the honest answer for an option the game
 * no longer exposes -- a mod asking "is this on?" gets the state the feature
 * effectively has now. Writes land here too and simply have no effect on
 * vanilla, which matches reality: there is nothing left for them to control.
 *
 * Eight mods in the 1.20.1 corpus read field_1866 alone.
 */
@Mixin(class_315.class)
public abstract class GameOptionsBridge {

    /** removed @1.20.1->1.20.2 */
    public boolean field_1866 = true;

    /** removed @1.20.1->1.20.2 */
    public boolean field_1880 = false;

    /** removed @1.20.1->1.20.2 */
    public boolean field_1893 = false;
}
