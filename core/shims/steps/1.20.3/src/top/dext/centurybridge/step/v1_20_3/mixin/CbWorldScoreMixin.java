package top.dext.centurybridge.step.v1_20_3.mixin;

import net.minecraft.class_266;
import net.minecraft.class_267;
import net.minecraft.class_269;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldScoreV;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldScores;

/**
 * 1.20.2's Score on 1.20.3's (see CbWorldScoreV): the scoreboard, objective and owner the score is stored under,
 * and 1.20.2's methods with their 1.20.2 bodies over 1.20.3's value (covers). setScore announces through the
 * scoreboard's 1.20.3 onScoreChanged what 1.20.2 announced through onScoreChanged(Score): a change of value, and
 * a new score's first value.
 */
@Mixin(class_267.class)
public abstract class CbWorldScoreMixin implements CbWorldScoreV {

    @Shadow
    public abstract int method_55397();

    @Shadow
    public abstract void method_55401(int value);

    @Unique
    private class_269 cb$scoreboard;

    @Unique
    private class_266 cb$objective;

    @Unique
    private String cb$owner;

    @Unique
    private boolean cb$forceUpdate;

    @Override
    public void cb$attach(class_269 scoreboard, class_266 objective, String owner, boolean forceUpdate) {
        if (!this.cb$attached()) {
            this.cb$scoreboard = scoreboard;
            this.cb$objective = objective;
            this.cb$owner = owner;
            this.cb$forceUpdate = forceUpdate;
        }
    }

    @Override
    public boolean cb$attached() {
        return this.cb$scoreboard != null || this.cb$objective != null || this.cb$owner != null;
    }

    @Override
    public int method_1126() {
        return this.method_55397();
    }

    @Override
    public void method_1128(int score) {
        int old = this.method_55397();
        this.method_55401(score);
        if (old != score || this.cb$forceUpdate) {
            this.cb$forceUpdate = false;
            CbWorldScores.scoreChanged(this.method_1122(), (class_267) (Object) this);
        }
    }

    @Override
    public void method_1124(int amount) {
        if (this.cb$objective.method_1116().method_1226()) {
            throw new IllegalStateException("Cannot modify read-only score");
        }
        this.method_1128(this.method_1126() + amount);
    }

    @Override
    public void method_1130() {
        this.method_1124(1);
    }

    @Override
    public void method_1132() {
        this.method_1128(0);
    }

    @Override
    public class_266 method_1127() {
        return this.cb$objective;
    }

    @Override
    public String method_1129() {
        return this.cb$owner;
    }

    @Override
    public class_269 method_1122() {
        return this.cb$scoreboard;
    }
}
