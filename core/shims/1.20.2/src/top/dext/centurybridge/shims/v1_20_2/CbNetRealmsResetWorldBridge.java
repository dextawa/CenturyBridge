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
@Mixin(class_4410.class)
public abstract class CbNetRealmsResetWorldBridge implements CbNetRealmsAccess.Callback, CbNetRealmsAccess.ResetScreen {

    /** slot: final in 1.20.2's jar, mutable again at runtime (the generated access-widener block: 1.20.1 had it mutable) */
    @Shadow
    public int field_19998;

    @Shadow
    @Final
    @Mutable
    private class_2561 field_20501;

    @Shadow
    @Final
    private class_4877 field_20001;

    @Unique
    private Runnable cb$callback;

    @Unique
    private class_2561 cb$buttonTitle;

    /** was setSlot(slot) */
    public void method_21379(int slot) {
        this.field_19998 = slot;
    }

    /** was setResetTitle(title): the title of the screen the reset task runs in */
    public void method_21376(class_2561 title) {
        this.field_20501 = title;
    }

    @Override
    public void cb$setLegacyCallback(Runnable callback) {
        this.cb$callback = callback;
    }

    @Override
    public Runnable cb$legacyCallback() {
        return cb$callback;
    }

    @Override
    public void cb$setLegacyButtonTitle(class_2561 title) {
        this.cb$buttonTitle = title;
    }

    /** the back button: labelled as 1.20.1's constructor was told */
    @ModifyArg(method = "method_25426", at = @At(value = "INVOKE",
        target = "Lnet/minecraft/class_4185;method_46430(Lnet/minecraft/class_2561;Lnet/minecraft/class_4185$class_4241;)Lnet/minecraft/class_4185$class_7840;"),
        index = 0, require = 1)
    private class_2561 cb$labelBackButton(class_2561 title) {
        return cb$buttonTitle != null ? cb$buttonTitle : title;
    }

    /** the upload button: 1.20.1 uploaded into the active slot when the screen had none */
    @Redirect(method = "method_25212", at = @At(value = "FIELD", target = "Lnet/minecraft/class_4410;field_19998:I",
        opcode = Opcodes.GETFIELD), require = 1)
    private int cb$uploadSlot(class_4410 self) {
        return field_19998 != -1 ? field_19998 : field_20001.field_22612;
    }
}
