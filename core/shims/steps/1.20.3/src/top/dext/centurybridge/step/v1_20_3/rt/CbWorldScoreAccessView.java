package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_2561;
import net.minecraft.class_267;
import net.minecraft.class_9014;
import net.minecraft.class_9022;

/**
 * A 1.20.3 ScoreAccess over a 1.20.2 Score, for a 1.20.2 call of OperationArgument.Operation.apply(Score, Score)
 * (CbWorldScores.access): get and set are the score's 1.20.2 getScore / setScore (so setting announces the change
 * the 1.20.2 way), lock and unlock its setLocked; a new display or number format is stored on the score and
 * announced, as 1.20.3's own ScoreAccess does.
 */
public final class CbWorldScoreAccessView implements class_9014 {

    private final class_267 score;

    CbWorldScoreAccessView(class_267 score) {
        this.score = score;
    }

    /** the 1.20.2 score this access reads and writes */
    public class_267 score() {
        return this.score;
    }

    private CbWorldScoreV v() {
        return (CbWorldScoreV) this.score;
    }

    @Override
    public int method_55409() {
        return this.v().method_1126();
    }

    @Override
    public void method_55410(int value) {
        this.v().method_1128(value);
    }

    @Override
    public boolean method_55416() {
        return this.score.method_1131();
    }

    @Override
    public void method_55417() {
        this.score.method_1125(false);
    }

    @Override
    public void method_55418() {
        this.score.method_1125(true);
    }

    @Override
    public class_2561 method_55419() {
        return this.score.method_55407();
    }

    @Override
    public void method_55411(class_2561 display) {
        this.score.method_55405(display);
        CbWorldScores.announce(this.score);
    }

    @Override
    public void method_55412(class_9022 format) {
        this.score.method_55406(format);
        CbWorldScores.announce(this.score);
    }
}
