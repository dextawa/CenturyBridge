package top.dext.centurybridge.step.v1_21_9.rt;

import net.minecraft.class_1267;
import net.minecraft.class_1928;
import net.minecraft.class_1934;
import net.minecraft.class_3806;
import net.minecraft.class_3808;
import net.minecraft.server.MinecraftServer;

/**
 * DedicatedServerProperties' fields as 1.21.8 code reads them. 1.21.9 keeps these properties under the same field
 * names but as Settings.MutableValue holders (javap: 1.21.8 {@code public final boolean field_16807}, 1.21.9
 * {@code public final class_3808<class_3806>.class_3809<Boolean> field_16807}); the server itself reads the holder's
 * get() (1.21.9 DedicatedServer.acceptsTransfers: {@code getProperties().acceptsTransfers.get()}), so 1.21.8's read is
 * the holder's value. pvp, spawn-monsters and enable-command-block left the properties: 1.21.9 reads the pvp,
 * spawnMonsters and commandBlocksEnabled game rules in their place, so 1.21.8's read is the running server's rule, and
 * 1.21.8's property default before a server is up.
 */
public final class CbServerProperties {

    private CbServerProperties() {
    }

    public static boolean field_16807(class_3806 p) { return p.field_16807.get(); }

    public static String field_16825(class_3806 p) { return p.field_16825.get(); }

    public static boolean field_16827(class_3806 p) { return p.field_16827.get(); }

    public static boolean field_16805(class_3806 p) { return p.field_16805.get(); }

    public static class_1267 field_16840(class_3806 p) { return p.field_16840.get(); }

    public static class_1934 field_16841(class_3806 p) { return p.field_16841.get(); }

    public static int field_16816(class_3806 p) { return p.field_16816.get(); }

    public static int field_16845(class_3806 p) { return p.field_16845.get(); }

    public static int field_16844(class_3806 p) { return p.field_16844.get(); }

    public static int field_34883(class_3806 p) { return p.field_34883.get(); }

    public static int field_16814(class_3806 p) { return p.field_16814.get(); }

    public static boolean field_24453(class_3806 p) { return p.field_24453.get(); }

    public static boolean field_35564(class_3806 p) { return p.field_35564.get(); }

    public static int field_24454(class_3806 p) { return p.field_24454.get(); }

    public static int field_52352(class_3806 p) { return p.field_52352.get(); }

    public static boolean field_48270(class_3806 p) { return p.field_48270.get(); }

    /**
     * 1.21.8's acceptsTransfers was a plain mutable field the server read on every transfer; 1.21.9's field is mutable
     * too and holds the holder the server reads, so the write installs a holder with the same key and serializer and
     * the new value
     */
    public static void field_48270(class_3806 p, boolean value) {
        p.field_48270 = with(p, p.field_48270, value);
    }

    /**
     * MinecraftServer.setFlightAllowed on a dedicated server (CbServerRestoredMixin): 1.21.8 set the server's flag in
     * memory, which its isFlightAllowed answered; 1.21.9's DedicatedServer.isFlightAllowed answers the allow-flight
     * holder, and its own setter rewrites (and saves) the properties. 1.21.8's write saved nothing, so the holder is
     * replaced in memory. The field is final on 1.21.9: the layer's access widener makes it mutable.
     */
    public static void field_16807(class_3806 p, boolean value) {
        p.field_16807 = with(p, p.field_16807, value);
    }

    /** a holder with the old one's key and serializer and the new value (Settings.MutableValue's own constructor) */
    private static <V> class_3808<class_3806>.class_3809<V> with(class_3806 p, class_3808<class_3806>.class_3809<V> old, V value) {
        return p.new class_3809<V>(old.field_16852, value, old.field_16851);
    }

    /** pvp: 1.21.9's pvp game rule; before a server is up, 1.21.8's default (true) */
    public static boolean field_16833(class_3806 p) {
        return rule(class_1928.field_62434, true);
    }

    /** spawn-monsters: 1.21.9's spawnMonsters game rule; before a server is up, 1.21.8's default (true) */
    public static boolean field_16835(class_3806 p) {
        return rule(class_1928.field_62436, true);
    }

    /** enable-command-block: 1.21.9's commandBlocksEnabled game rule; before a server is up, 1.21.8's default (false) */
    public static boolean field_16806(class_3806 p) {
        return rule(class_1928.field_62916, false);
    }

    private static boolean rule(class_1928.class_4313<class_1928.class_4310> key, boolean before) {
        MinecraftServer s = CbProfiles.cbServer();
        return s == null ? before : s.method_3767().method_8355(key);
    }
}
