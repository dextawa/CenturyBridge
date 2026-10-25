package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_437;
import net.minecraft.class_4325;
import net.minecraft.class_4877;
import net.minecraft.class_4905;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.dext.centurybridge.rt.v1_20_2.CbNetLegacyRealmsPlay;
import top.dext.centurybridge.rt.v1_20_2.Guard;

/**
 * RealmsMainScreen: 1.20.1's newScreen(), and its instance play(server, screen), which 1.20.2 made
 * static. Vanilla's main screen plays with itself as the screen (its play button and server entries,
 * as in 1.20.1, where it called this.play); a subclass overriding 1.20.1's play gets those calls --
 * once: its own super.play runs vanilla's (CbNetRealms.cbSuperPlay holds the guard for the screen).
 */
@Mixin(value = class_4325.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbNetRealmsMainScreenBridge extends class_4905 {

    @Shadow
    @Final
    private class_437 field_19482;

    protected CbNetRealmsMainScreenBridge() {
        super(null);
    }

    /** was newScreen(): a fresh main screen over the same parent, set up at this one's size */
    public class_4325 method_20902() {
        class_4325 screen = new class_4325(field_19482);
        screen.method_25423(this.field_22787, this.field_22789, this.field_22790);
        return screen;
    }
}
