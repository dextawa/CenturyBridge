package top.dext.centurybridge.facade.v1_20_2;

import java.util.Arrays;

/**
 * Facade for 1.20.1 net/minecraft/class_4324 (com.mojang.realmsclient.KeyCombo), deleted in 1.20.2
 * with the Realms main screen's typed key combinations: a sequence of characters that runs its action
 * once typed in order. Self-contained, so the 1.20.1 class whole; mods' references are renamed here
 * (classRenames).
 */
public class class_4324 {

    public final char[] field_19453;

    public int field_19454;

    public final Runnable field_19455;

    public class_4324(char[] chars, Runnable onCompletion) {
        this.field_19455 = onCompletion;
        if (chars.length < 1) {
            throw new IllegalArgumentException("Must have at least one char");
        }
        this.field_19453 = chars;
    }

    public class_4324(char[] chars) {
        this(chars, () -> {
        });
    }

    /** keyPressed(c): the next character of the combination advances it; the last one runs the action */
    public boolean method_20833(char c) {
        if (c == field_19453[field_19454++]) {
            if (field_19454 == field_19453.length) {
                method_20832();
                field_19455.run();
                return true;
            }
        } else {
            method_20832();
        }
        return false;
    }

    /** reset() */
    public void method_20832() {
        field_19454 = 0;
    }

    @Override
    public String toString() {
        return "KeyCombo{chars=" + Arrays.toString(field_19453) + ", matchIndex=" + field_19454 + "}";
    }
}
