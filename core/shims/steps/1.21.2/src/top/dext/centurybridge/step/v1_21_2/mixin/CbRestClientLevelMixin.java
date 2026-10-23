package top.dext.centurybridge.step.v1_21_2.mixin;

import java.util.Deque;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_243;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_21_2.rt.CbRestClient;

/**
 * ClientLevel members of 1.21.1 that 1.21.2 dropped or reshaped, back with 1.21.1's bodies on 1.21.2's
 * state.
 *
 * Time: 1.21.1's handleSetTime called setGameTime(gameTime) and setDayTime(dayTime), the day time negative
 * when the day does not advance -- setDayTime then stored its absolute value and set the client's
 * doDaylightCycle rule, which 1.21.1's client read to advance the day. 1.21.2's client has no game rules;
 * it advances the day by its own tickDayTime flag (setTimeFromServer), which setDayTime sets instead, so
 * the day advances or stops as 1.21.1's did.
 *
 * Sky and cloud colour: 1.21.2 answers them as packed RGB, the colour its renderer draws; 1.21.1 answered a
 * Vec3 of the components (0..1), which is that colour component by component (8-bit precision, the
 * precision it is drawn with).
 */
@Environment(EnvType.CLIENT)
@Mixin(class_638.class)
public abstract class CbRestClientLevelMixin {

    @Shadow
    @Final
    private class_638.class_5271 field_24430;

    @Shadow
    @Final
    private Deque<Runnable> field_34804;

    @Shadow
    private boolean field_54500;

    /** setGameTime(time): the level data's game time */
    public void method_29089(long gameTime) {
        this.field_24430.method_177(gameTime);
    }

    /** setDayTime(time): negative stops the day (see above), the absolute value is the day time */
    public void method_8435(long dayTime) {
        if (dayTime < 0L) {
            dayTime = -dayTime;
            this.field_54500 = false;
        } else {
            this.field_54500 = true;
        }
        this.field_24430.method_165(dayTime);
    }

    /** isLightUpdateQueueEmpty() */
    public boolean method_38743() {
        return this.field_34804.isEmpty();
    }

    /** getSkyColor(cameraPos, partialTick) as components */
    public class_243 method_23777(class_243 cameraPos, float partialTick) {
        return CbRestClient.rgb(((class_638) (Object) this).method_23777(cameraPos, partialTick));
    }

    /** getCloudColor(partialTick) as components */
    public class_243 method_23785(float partialTick) {
        return CbRestClient.rgb(((class_638) (Object) this).method_23785(partialTick));
    }
}
