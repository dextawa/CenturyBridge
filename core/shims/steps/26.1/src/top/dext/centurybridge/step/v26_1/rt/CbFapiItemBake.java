package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.item.ItemModel;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

import java.util.ArrayDeque;
import java.util.Iterator;

/**
 * 1.21.11's ItemModel.Unbaked.bake(context) on 26.1's bake(context, transformation). 26.1 hands every unbaked item
 * model the transformation its parents accumulated (item model definitions gained a "transformation"); 1.21.11 had
 * none, so a 1.21.11 bake is a bake without one.
 * <p>
 * A mod's 1.21.11 override is reached through the override hook on bake (frames: this class, which keeps Guard's
 * frames and also the transformation of every hooked bake in progress on the thread). A 1.21.11 call of bake(context)
 * -- the mod's super call into Fabric API's WrapperUnbakedItemModel, or a call on any unbaked model, typically the
 * wrapped one -- is reshaped to bake(context, transformation) with the transformation of the innermost hooked bake of
 * that context on the thread: what vanilla passed the mod's model, which the 1.21.11 override, delegating, would have
 * handed on had it known of it. Outside any hooked bake it is the identity: 1.21.11's bake without a transformation.
 */
@Environment(EnvType.CLIENT)
public final class CbFapiItemBake {
    private CbFapiItemBake() {
    }

    private static final Matrix4fc IDENTITY = new Matrix4f();
    private static final ThreadLocal<ArrayDeque<Object[]>> BAKES = ThreadLocal.withInitial(ArrayDeque::new);

    // ---- frames (the hook's: Guard's, plus the transformation) ----

    public static void cbPush(Object self, Object[] args) {
        Guard.cbPush(self, args);
        BAKES.get().push(args);
    }

    public static void cbPop() {
        Guard.cbPop();
        BAKES.get().pop();
    }

    public static Object cbArgL(Object self, int i) {
        return Guard.cbArgL(self, i);
    }

    public static Object cbArgOrL(Object self, int i, Object dflt) {
        return Guard.cbArgOrL(self, i, dflt);
    }

    public static boolean cbArgZ(Object self, int i) {
        return Guard.cbArgZ(self, i);
    }

    public static boolean cbArgOrZ(Object self, int i, boolean dflt) {
        return Guard.cbArgOrZ(self, i, dflt);
    }

    public static byte cbArgB(Object self, int i) {
        return Guard.cbArgB(self, i);
    }

    public static byte cbArgOrB(Object self, int i, byte dflt) {
        return Guard.cbArgOrB(self, i, dflt);
    }

    public static char cbArgC(Object self, int i) {
        return Guard.cbArgC(self, i);
    }

    public static char cbArgOrC(Object self, int i, char dflt) {
        return Guard.cbArgOrC(self, i, dflt);
    }

    public static short cbArgS(Object self, int i) {
        return Guard.cbArgS(self, i);
    }

    public static short cbArgOrS(Object self, int i, short dflt) {
        return Guard.cbArgOrS(self, i, dflt);
    }

    public static int cbArgI(Object self, int i) {
        return Guard.cbArgI(self, i);
    }

    public static int cbArgOrI(Object self, int i, int dflt) {
        return Guard.cbArgOrI(self, i, dflt);
    }

    public static long cbArgJ(Object self, int i) {
        return Guard.cbArgJ(self, i);
    }

    public static long cbArgOrJ(Object self, int i, long dflt) {
        return Guard.cbArgOrJ(self, i, dflt);
    }

    public static float cbArgF(Object self, int i) {
        return Guard.cbArgF(self, i);
    }

    public static float cbArgOrF(Object self, int i, float dflt) {
        return Guard.cbArgOrF(self, i, dflt);
    }

    public static double cbArgD(Object self, int i) {
        return Guard.cbArgD(self, i);
    }

    public static double cbArgOrD(Object self, int i, double dflt) {
        return Guard.cbArgOrD(self, i, dflt);
    }

    // ---- the transformation a 1.21.11 bake(context) passes on ----

    /** the transformation of the innermost hooked bake of {@code context} on this thread, else the identity */
    public static Matrix4fc transform(ItemModel.BakingContext context) {
        Iterator<Object[]> it = BAKES.get().iterator();
        while (it.hasNext()) {
            Object[] args = it.next();
            if (args.length > 1 && args[0] == context && args[1] instanceof Matrix4fc matrix) {
                return matrix;
            }
        }
        return IDENTITY;
    }

    /** frameDefaults: a mod's old bake entered directly, its super call's transformation */
    public static Matrix4fc transform(ItemModel.Unbaked self, ItemModel.BakingContext context) {
        return transform(context);
    }
}
