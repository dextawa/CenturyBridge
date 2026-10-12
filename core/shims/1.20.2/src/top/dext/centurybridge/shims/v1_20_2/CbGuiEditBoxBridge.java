package top.dext.centurybridge.shims.v1_20_2;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * EditBox's 1.20.1 cursor methods without the selection flag: moveCursor(int), moveCursorTo(int),
 * moveCursorToStart(), moveCursorToEnd(). In 1.20.1 they extended the selection or collapsed it by the
 * box's shiftPressed state -- Screen.hasShiftDown() as of the box's last keyPressed (false before any) --
 * where 1.20.2 takes the flag as an argument and keeps no state. The state is kept again, set where
 * 1.20.1 set it, at the start of keyPressed, and each old shape passes it on to the 1.20.2 one.
 * (1.20.1 also cleared it around its own deletions; those go through 1.20.2's code, which passes false.)
 */
@Mixin(net.minecraft.class_342.class)
public abstract class CbGuiEditBoxBridge {

    @Unique
    private boolean cb$shiftPressed;

    @Inject(method = "method_25404", at = @At("HEAD"))
    private void cb$keyShift(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (((net.minecraft.class_342) (Object) this).method_20315()) {
            this.cb$shiftPressed = net.minecraft.class_437.method_25442();
        }
    }

    /** 1.20.1 moveCursor(int) */
    public void method_1855(int offset) {
        ((net.minecraft.class_342) (Object) this).method_1855(offset, this.cb$shiftPressed);
    }

    /** 1.20.1 moveCursorTo(int) */
    public void method_1883(int position) {
        ((net.minecraft.class_342) (Object) this).method_1883(position, this.cb$shiftPressed);
    }

    /** 1.20.1 moveCursorToStart() */
    public void method_1870() {
        ((net.minecraft.class_342) (Object) this).method_1870(this.cb$shiftPressed);
    }

    /** 1.20.1 moveCursorToEnd() */
    public void method_1872() {
        ((net.minecraft.class_342) (Object) this).method_1872(this.cb$shiftPressed);
    }
}
