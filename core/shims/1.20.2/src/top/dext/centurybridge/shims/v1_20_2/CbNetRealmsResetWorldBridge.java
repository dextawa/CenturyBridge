package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_2561;
import net.minecraft.class_4410;
import net.minecraft.class_4877;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.dext.centurybridge.rt.v1_20_2.CbNetRealmsAccess;

/**
 * RealmsResetWorldScreen as 1.20.1's constructors made it (superCtorReshapes onto 1.20.2's
 * constructor, which the layer's access widener opens; CbNetRealms.cbLegacyResetScreen): the slot and
 * reset title set after construction (setSlot / setResetTitle), the back button's label, the
 * after-upload callback handed down to the upload screens, and 1.20.1's slot for an upload -- the
 * active one when the screen was made without a slot (-1). A screen made the 1.20.2 way has a slot,
 * no callback and no label of its own, and is left as it is.
 */
@Mixin(value = class_4410.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsResetWorldBridge {

    /** slot: final in 1.20.2's jar, mutable again at runtime (the generated access-widener block: 1.20.1 had it mutable) */
    @Shadow
    public int field_19998;

    @Shadow
    @Final
    @Mutable
    private class_2561 field_20501;

    /** was setSlot(slot) */
    public void method_21379(int slot) {
        this.field_19998 = slot;
    }

    /** was setResetTitle(title): the title of the screen the reset task runs in */
    public void method_21376(class_2561 title) {
        this.field_20501 = title;
    }
}
