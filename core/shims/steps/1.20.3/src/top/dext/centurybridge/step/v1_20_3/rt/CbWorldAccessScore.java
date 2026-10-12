package top.dext.centurybridge.step.v1_20_3.rt;

import net.minecraft.class_2561;
import net.minecraft.class_266;
import net.minecraft.class_267;
import net.minecraft.class_269;
import net.minecraft.class_9014;
import net.minecraft.class_9022;

/**
 * A 1.20.2 Score over a 1.20.3 ScoreAccess, for a 1.20.2 OperationArgument.Operation that 1.20.3 hands two
 * ScoreAccess (CbWorldScores.operation / scoreOf): reading and setting the value, locking, and the display go
 * through the access, as 1.20.3's own operations do. A ScoreAccess does not name its owner, objective or
 * scoreboard, so asking this score for one of them is refused with that reason; 1.20.3 code reads the score's
 * number format nowhere through a Score it did not store itself, and refuses it here likewise.
 */
public final class CbWorldAccessScore extends class_267 {

    private final class_9014 access;

    CbWorldAccessScore(class_9014 access) {
        this.access = access;
    }

    /** the ScoreAccess this score reads and writes */
    public class_9014 access() {
        return this.access;
    }

    // ------------------------------------------------------------------ 1.20.2 Score

    public int method_1126() {
        return this.access.method_55409();
    }

    public void method_1128(int score) {
        this.access.method_55410(score);
    }

    /** add(amount): the access refuses a read-only score with the message 1.20.2's add refused it with */
    public void method_1124(int amount) {
        this.access.method_55414(amount);
    }

    public void method_1130() {
        this.method_1124(1);
    }

    public void method_1132() {
        this.method_1128(0);
    }

    public class_266 method_1127() {
        throw unknown("objective");
    }

    public String method_1129() {
        throw unknown("owner");
    }

    public class_269 method_1122() {
        throw unknown("scoreboard");
    }

    // ------------------------------------------------------------------ 1.20.3 Score, over the access

    @Override
    public int method_55397() {
        return this.access.method_55409();
    }

    @Override
    public void method_55401(int value) {
        this.access.method_55410(value);
    }

    @Override
    public boolean method_1131() {
        return this.access.method_55416();
    }

    @Override
    public void method_1125(boolean locked) {
        if (locked) {
            this.access.method_55418();
        } else {
            this.access.method_55417();
        }
    }

    @Override
    public class_2561 method_55407() {
        return this.access.method_55419();
    }

    @Override
    public void method_55405(class_2561 display) {
        this.access.method_55411(display);
    }

    @Override
    public class_9022 method_55400() {
        throw unknown("number format");
    }

    @Override
    public void method_55406(class_9022 format) {
        this.access.method_55412(format);
    }

    private static UnsupportedOperationException unknown(String what) {
        return new UnsupportedOperationException("Score: 1.20.3 handed this score over as a ScoreAccess, which does not know its "
            + what);
    }
}
