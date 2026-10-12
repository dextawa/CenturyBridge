package top.dext.centurybridge.step.v1_21_7.rt;

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
 * its type's zero value -- or, through cbArgOr*, the default the wiring names for it (frameDefaults:
 * what V used when there was no such argument).
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

    /** the frame's argument i when a frame holds the object (whatever vanilla passed, null too), else dflt */
    public static Object cbArgOrL(Object self, int i, Object dflt) {
        for (Object[] f : FRAMES.get()) {
            if (f[0] == self) {
                return ((Object[]) f[1])[i];
            }
        }
        return dflt;
    }

    private static boolean framed(Object self) {
        for (Object[] f : FRAMES.get()) {
            if (f[0] == self) {
                return true;
            }
        }
        return false;
    }

    public static boolean cbArgOrZ(Object self, int i, boolean dflt) {
        return framed(self) ? cbArgZ(self, i) : dflt;
    }

    public static byte cbArgOrB(Object self, int i, byte dflt) {
        return framed(self) ? cbArgB(self, i) : dflt;
    }

    public static char cbArgOrC(Object self, int i, char dflt) {
        return framed(self) ? cbArgC(self, i) : dflt;
    }

    public static short cbArgOrS(Object self, int i, short dflt) {
        return framed(self) ? cbArgS(self, i) : dflt;
    }

    public static int cbArgOrI(Object self, int i, int dflt) {
        return framed(self) ? cbArgI(self, i) : dflt;
    }

    public static long cbArgOrJ(Object self, int i, long dflt) {
        return framed(self) ? cbArgJ(self, i) : dflt;
    }

    public static float cbArgOrF(Object self, int i, float dflt) {
        return framed(self) ? cbArgF(self, i) : dflt;
    }

    public static double cbArgOrD(Object self, int i, double dflt) {
        return framed(self) ? cbArgD(self, i) : dflt;
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
