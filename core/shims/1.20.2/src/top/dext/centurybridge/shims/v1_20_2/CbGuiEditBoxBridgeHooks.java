package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * The injectors into 1.20.2's own methods that go with {@link CbGuiEditBoxBridge} (its restored 1.20.1 members
 * stay there, at {@code CbVanillaReads.PRIORITY}, so a mod's 1.20.1 injectors can anchor inside them).
 */
@Mixin(net.minecraft.class_342.class)
public abstract class CbGuiEditBoxBridgeHooks implements top.dext.centurybridge.rt.v1_20_2.CbLegacyEditBoxShift {

    @Unique
    private boolean cb$shiftPressed;

    /** the state, for the cursor hooks' frameDefaults (a 1.20.1 override's super call with no frame) */
    @Override
    public boolean cb$legacyShiftPressed() {
        return this.cb$shiftPressed;
    }

    @Inject(method = "method_25404", at = @At("HEAD"))
    private void cb$keyShift(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (((net.minecraft.class_342) (Object) this).method_20315()) {
            this.cb$shiftPressed = net.minecraft.class_437.method_25442();
        }
    }
}
