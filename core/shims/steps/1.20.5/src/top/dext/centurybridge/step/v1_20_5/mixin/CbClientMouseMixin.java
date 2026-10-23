package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * MouseHandler.turnPlayer(): 1.20.4's public turnPlayer() measured the time since its last call itself and
 * turned the player by the accumulated mouse movement (when the mouse is grabbed and the window focused),
 * then cleared it. 1.20.5's handleAccumulatedMovement() measures the time and calls a private
 * turnPlayer(elapsed) when grabbed and a player exists. The cover keeps 1.20.4's own clock, so a 1.20.4
 * caller's turn is timed as 1.20.4 timed it; the turn itself is 1.20.5's private one, the body 1.20.4's
 * turnPlayer ran after its checks (smoothing, tutorial, the player's turn).
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_312.class)
public abstract class CbClientMouseMixin {

    @Shadow
    private double field_1789;

    @Shadow
    private double field_1787;

    @Shadow
    private net.minecraft.class_310 field_1779;

    @Shadow
    public abstract boolean method_1613();

    @Shadow
    private void method_1606(double elapsed) {
    }

    /** 1.20.4's lastMouseEventTime (field_1785), the turn clock */
    @Unique
    private double cb$lastTurn = Double.MIN_VALUE;

    /** 1.20.4 MouseHandler.turnPlayer() */
    public void method_1606() {
        double now = net.minecraft.class_3673.method_15974();
        double elapsed = now - this.cb$lastTurn;
        this.cb$lastTurn = now;
        if (this.method_1613() && this.field_1779.method_1569()) {
            this.method_1606(elapsed);
        }
        this.field_1789 = 0.0D;
        this.field_1787 = 0.0D;
    }
}
