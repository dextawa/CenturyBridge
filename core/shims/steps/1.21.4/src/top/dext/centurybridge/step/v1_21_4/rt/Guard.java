package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

/**
 * Runtime support for this layer's override hooks.
 *
 * Guard (cbEnter / cbExit): the new shape synthesized in a mod class forwards to the mod's old
 * override once per object and call; if that override's own super call comes back into the
 * synthesized method, it falls through to super instead of forwarding again.
 *
 * Frames (cbPush / cbPop / cbArg*): a hook that carries its arguments records what vanilla passed
 * to the new shape before forwarding to the old method; the old method's super call, rewritten to
 * the new shape, reads back the arguments the old shape has no place for. The innermost frame for
 * the object answers, so a nested call on the same instance (a block is a singleton) reads its own
 * arguments; with no frame for the object (a super call from outside a hooked call) the argument is
 * its type's zero value.
 */
public final class Guard {

    private static final ThreadLocal<Set<Object>> ACTIVE =
        ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    private static final ThreadLocal<ArrayDeque<Object[]>> FRAMES = ThreadLocal.withInitial(ArrayDeque::new);

    public static boolean cbEnter(Object self) {
        return ACTIVE.get().add(self);
    }

    public static void cbExit(Object self) {
        ACTIVE.get().remove(self);
    }

    public static void cbPush(Object self, Object[] args) {
        FRAMES.get().push(new Object[] {self, args});
    }

    public static void cbPop() {
        FRAMES.get().pop();
    }

    public static Object cbArgL(Object self, int i) {
        for (Object[] f : FRAMES.get()) { // innermost first
            if (f[0] == self) {
                return ((Object[]) f[1])[i];
            }
        }
        return null;
    }

    public static boolean cbArgZ(Object self, int i) {
        Object v = cbArgL(self, i);
        return v != null && (Boolean) v;
    }

    public static byte cbArgB(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Byte) v;
    }

    public static char cbArgC(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Character) v;
    }

    public static short cbArgS(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Short) v;
    }

    public static int cbArgI(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0 : (Integer) v;
    }

    public static long cbArgJ(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0L : (Long) v;
    }

    public static float cbArgF(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0f : (Float) v;
    }

    public static double cbArgD(Object self, int i) {
        Object v = cbArgL(self, i);
        return v == null ? 0d : (Double) v;
    }

    private Guard() {
    }
}
