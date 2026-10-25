package top.dext.centurybridge.step.v1_21_9.rt;

import java.util.Map;
import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1453;
import net.minecraft.class_1657;
import net.minecraft.class_2487;
import net.minecraft.class_269;
import net.minecraft.class_3218;
import net.minecraft.class_3222;

/**
 * Player members 1.21.9 moved or reshaped (javap of both releases). 1.21.9 put Avatar (class_11890) under Player for
 * what players and mannequins share (main arm, model customisation, dimensions: methodRenames / fieldRenames), moved
 * the shoulder entities' saved data to ServerPlayer and syncs only the shoulder parrots' variants to clients.
 */
public final class CbAvatar {
    private CbAvatar() {
    }

    /** Entity.forceSetRotation(yRot, xRot) / new ClientboundPlayerRotationPacket(yRot, xRot): 1.21.8's angles were absolute (relative = false) */
    public static boolean absolute() {
        return false;
    }

    /** new ClientboundPlayerRotationPacket(yRot, xRot): absolute angles */
    public static net.minecraft.class_10265 rotationPacket(float yRot, float xRot) {
        return new net.minecraft.class_10265(yRot, absolute(), xRot, absolute());
    }

    /** Player.getScoreboard(): 1.21.8's body (the level's scoreboard) */
    public static class_269 method_7327(class_1657 player) {
        return player.method_73183().method_8428();
    }

    /** Entity.killedEntity(level, killed) entered directly by a 1.21.8 override: the source that killed it (its last damage source) */
    public static class_1282 killedSource(class_1297 self, class_3218 level, class_1309 killed) {
        return CbFapiCommon.killingSource(killed);
    }

    // ------------------------------------------------------------------ shoulder entities

    /**
     * a shoulder entity's data where 1.21.9 keeps only the parrot's variant (a client's player): what 1.21.8 synced for a
     * vanilla shoulder parrot, its id and variant; empty for none
     */
    private static class_2487 parrotTag(Optional<class_1453.class_7989> variant) {
        class_2487 tag = new class_2487();
        variant.ifPresent(v -> {
            tag.method_10582("id", "minecraft:parrot");
            tag.method_10569("Variant", v.field_41557);
        });
        return tag;
    }

    /** Player.getShoulderEntityLeft(): ServerPlayer keeps the data; any other player answers its parrot */
    public static class_2487 method_7356(class_1657 player) {
        return player instanceof class_3222 sp ? sp.method_74074() : parrotTag(player.method_74136());
    }

    /** Player.getShoulderEntityRight() */
    public static class_2487 method_7308(class_1657 player) {
        return player instanceof class_3222 sp ? sp.method_74075() : parrotTag(player.method_74137());
    }

    /** Player.setShoulderEntityLeft(tag): ServerPlayer keeps it (and syncs the parrot); any other player its parrot */
    public static void method_7273(class_1657 player, class_2487 tag) {
        if (player instanceof class_3222 sp) {
            sp.method_74076(tag);
        } else {
            player.method_74133(class_1657.method_74135(tag));
        }
    }

    /** Player.setShoulderEntityRight(tag) */
    public static void method_7345(class_1657 player, class_2487 tag) {
        if (player instanceof class_3222 sp) {
            sp.method_74077(tag);
        } else {
            player.method_74134(class_1657.method_74135(tag));
        }
    }

    /** Player.setEntityOnShoulder(tag): ServerPlayer's; any other player never held shoulder entities on its own (1.21.8's ran on the server) */
    public static boolean method_7298(class_1657 player, class_2487 tag) {
        return player instanceof class_3222 sp && sp.method_74073(tag);
    }

    // ------------------------------------------------------------------ cloak and bob (Player's on 1.21.8, the client's ClientAvatarState on 1.21.9)

    /**
     * a player holding 1.21.9's ClientAvatarState (a client's AbstractClientPlayer: the layer's CbClientAvatarMixin):
     * cloak 0-2 = x/y/zCloak, 3-5 = x/y/zCloakO; bob 0 = bob, 1 = oBob
     */
    public interface Cloak {
        double cb$cloak(int i);

        void cb$cloak(int i, double v);

        float cb$bob(int i);

        void cb$bob(int i, float v);
    }

    /**
     * {cloak 0-5, bob 0-1} of a player without a ClientAvatarState (a server's): 1.21.9 computes the cloak and bob only
     * for client players, so such a player keeps what 1.21.8 code set
     */
    private static final Map<class_1657, double[]> OTHER = new WeakHashMap<>();

    private static synchronized double[] other(class_1657 p) {
        return OTHER.computeIfAbsent(p, k -> new double[8]);
    }

    /** Player.xCloak */
    public static double field_7500(class_1657 p) {
        return p instanceof Cloak c ? c.cb$cloak(0) : (double) other(p)[0];
    }

    public static void field_7500(class_1657 p, double v) {
        if (p instanceof Cloak c) {
            c.cb$cloak(0, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[0] = v;
            }
        }
    }

    /** Player.yCloak */
    public static double field_7521(class_1657 p) {
        return p instanceof Cloak c ? c.cb$cloak(1) : (double) other(p)[1];
    }

    public static void field_7521(class_1657 p, double v) {
        if (p instanceof Cloak c) {
            c.cb$cloak(1, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[1] = v;
            }
        }
    }

    /** Player.zCloak */
    public static double field_7499(class_1657 p) {
        return p instanceof Cloak c ? c.cb$cloak(2) : (double) other(p)[2];
    }

    public static void field_7499(class_1657 p, double v) {
        if (p instanceof Cloak c) {
            c.cb$cloak(2, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[2] = v;
            }
        }
    }

    /** Player.xCloakO */
    public static double field_7524(class_1657 p) {
        return p instanceof Cloak c ? c.cb$cloak(3) : (double) other(p)[3];
    }

    public static void field_7524(class_1657 p, double v) {
        if (p instanceof Cloak c) {
            c.cb$cloak(3, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[3] = v;
            }
        }
    }

    /** Player.yCloakO */
    public static double field_7502(class_1657 p) {
        return p instanceof Cloak c ? c.cb$cloak(4) : (double) other(p)[4];
    }

    public static void field_7502(class_1657 p, double v) {
        if (p instanceof Cloak c) {
            c.cb$cloak(4, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[4] = v;
            }
        }
    }

    /** Player.zCloakO */
    public static double field_7522(class_1657 p) {
        return p instanceof Cloak c ? c.cb$cloak(5) : (double) other(p)[5];
    }

    public static void field_7522(class_1657 p, double v) {
        if (p instanceof Cloak c) {
            c.cb$cloak(5, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[5] = v;
            }
        }
    }

    /** Player.bob */
    public static float field_7483(class_1657 p) {
        return p instanceof Cloak c ? c.cb$bob(0) : (float) other(p)[6];
    }

    public static void field_7483(class_1657 p, float v) {
        if (p instanceof Cloak c) {
            c.cb$bob(0, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[6] = v;
            }
        }
    }

    /** Player.oBob */
    public static float field_7505(class_1657 p) {
        return p instanceof Cloak c ? c.cb$bob(1) : (float) other(p)[7];
    }

    public static void field_7505(class_1657 p, float v) {
        if (p instanceof Cloak c) {
            c.cb$bob(1, v);
        } else {
            synchronized (CbAvatar.class) {
                other(p)[7] = v;
            }
        }
    }
}
