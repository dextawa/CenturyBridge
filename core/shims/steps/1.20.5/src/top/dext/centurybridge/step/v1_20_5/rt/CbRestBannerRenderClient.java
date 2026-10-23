package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's banner rendering API on 1.20.5. 1.20.4 rendered a banner from a list of (pattern holder,
 * color) pairs -- the base pattern first -- and kept a Material per registered pattern key in
 * Sheets.BANNER_MATERIALS / SHIELD_MATERIALS (public maps mods read and filled); 1.20.5 renders a base
 * color and pattern layers and makes each material from the pattern's asset id when first asked.
 *
 * Patterns cross as CbBlockBanner has them: 1.20.4 code holds the facade, a key names the pattern. A
 * material a mod put into one of the maps under a key is the one 1.20.5's Sheets answers for that key
 * (CbRestSheetsMixin); every other key gets 1.20.4's material for it -- the pattern's texture under
 * entity/banner/ or entity/shield/, what 1.20.5 derives from the asset id too.
 */
@Environment(EnvType.CLIENT)
public final class CbRestBannerRenderClient {

    /** Sheets.BANNER_MATERIALS: the key's material, made on first use; a mod's put wins */
    private static final Map<net.minecraft.class_5321<?>, net.minecraft.class_4730> BANNER = new Materials(true);
    /** Sheets.SHIELD_MATERIALS */
    private static final Map<net.minecraft.class_5321<?>, net.minecraft.class_4730> SHIELD = new Materials(false);

    /**
     * 1.20.4's map of materials by pattern key: answers the material 1.20.4 made for every key the
     * pattern registry has, keeps what a mod puts
     */
    private static final class Materials extends java.util.AbstractMap<net.minecraft.class_5321<?>, net.minecraft.class_4730> {
        private final boolean banner;
        final Map<net.minecraft.class_5321<?>, net.minecraft.class_4730> put = new java.util.concurrent.ConcurrentHashMap<>();

        Materials(boolean banner) {
            this.banner = banner;
        }

        private net.minecraft.class_4730 made(net.minecraft.class_5321<?> key) {
            net.minecraft.class_2960 id = key.method_29177();
            return new net.minecraft.class_4730(banner ? net.minecraft.class_4722.field_21706 : net.minecraft.class_4722.field_21707,
                id.method_45138(banner ? "entity/banner/" : "entity/shield/"));
        }

        @Override
        public net.minecraft.class_4730 get(Object key) {
            net.minecraft.class_4730 m = put.get(key);
            if (m != null || !(key instanceof net.minecraft.class_5321<?> k) || !CbBlockBanner.registry().method_35842(rawKey(k))) {
                return m;
            }
            return made(k);
        }

        @Override
        public net.minecraft.class_4730 put(net.minecraft.class_5321<?> key, net.minecraft.class_4730 value) {
            net.minecraft.class_4730 old = get(key);
            put.put(key, value);
            return old;
        }

        @Override
        public net.minecraft.class_4730 remove(Object key) {
            net.minecraft.class_4730 old = get(key);
            put.remove(key);
            return old;
        }

        @Override
        public java.util.Set<Entry<net.minecraft.class_5321<?>, net.minecraft.class_4730>> entrySet() {
            java.util.Map<net.minecraft.class_5321<?>, net.minecraft.class_4730> all = new java.util.LinkedHashMap<>();
            for (Object k : CbBlockBanner.registry().method_42021()) {
                net.minecraft.class_5321<?> key = (net.minecraft.class_5321<?>) k;
                all.put(key, made(key));
            }
            all.putAll(put);
            return java.util.Collections.unmodifiableMap(all).entrySet();
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static net.minecraft.class_5321 rawKey(net.minecraft.class_5321<?> key) {
        return (net.minecraft.class_5321) key;
    }

    /** Sheets.BANNER_MATERIALS */
    public static Map<net.minecraft.class_5321<?>, net.minecraft.class_4730> field_28252() {
        return BANNER;
    }

    /** Sheets.SHIELD_MATERIALS */
    public static Map<net.minecraft.class_5321<?>, net.minecraft.class_4730> field_28253() {
        return SHIELD;
    }

    /** CbRestSheetsMixin: the material a mod put for this pattern's key, null when none */
    public static net.minecraft.class_4730 putFor(net.minecraft.class_6880<net.minecraft.class_2582> pattern, boolean banner) {
        java.util.Optional<net.minecraft.class_5321<net.minecraft.class_2582>> key = pattern.method_40230();
        return key.isEmpty() ? null : ((Materials) (banner ? BANNER : SHIELD)).put.get(key.get());
    }

    /** a pattern key as 1.20.5's holder: the world's entry of the key, else the record the key gives */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880<net.minecraft.class_2582> holderOf(net.minecraft.class_5321 key) {
        java.util.Optional<net.minecraft.class_2378<net.minecraft.class_2582>> live = CbBlockBanner.live();
        if (live.isPresent()) {
            java.util.Optional<net.minecraft.class_6880.class_6883<net.minecraft.class_2582>> h = live.get().method_40264(key);
            if (h.isPresent()) {
                return h.get();
            }
        }
        return net.minecraft.class_6880.method_40223(CbBlockBanner.record(key));
    }

    /** Sheets.getBannerMaterial(key) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_4730 method_33081(net.minecraft.class_5321 key) {
        net.minecraft.class_4730 m = BANNER.get(key);
        return m != null ? m : net.minecraft.class_4722.method_33081(holderOf(key));
    }

    /** Sheets.getShieldMaterial(key) */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_4730 method_33083(net.minecraft.class_5321 key) {
        net.minecraft.class_4730 m = SHIELD.get(key);
        return m != null ? m : net.minecraft.class_4722.method_33083(holderOf(key));
    }

    /**
     * 1.20.4 Sheets.getAllMaterials(consumer): the shulker, banner, shield, sign, hanging sign, bed and
     * chest materials, in 1.20.4's order
     */
    public static void method_24066(java.util.function.Consumer<net.minecraft.class_4730> consumer) {
        consumer.accept(net.minecraft.class_4722.field_21710);
        net.minecraft.class_4722.field_21711.forEach(consumer);
        BANNER.values().forEach(consumer);
        SHIELD.values().forEach(consumer);
        net.minecraft.class_4722.field_21712.values().forEach(consumer);
        net.minecraft.class_4722.field_40515.values().forEach(consumer);
        for (net.minecraft.class_4730 bed : net.minecraft.class_4722.field_21713) {
            consumer.accept(bed);
        }
        consumer.accept(net.minecraft.class_4722.field_21714);
        consumer.accept(net.minecraft.class_4722.field_21715);
        consumer.accept(net.minecraft.class_4722.field_21716);
        consumer.accept(net.minecraft.class_4722.field_21717);
        consumer.accept(net.minecraft.class_4722.field_21718);
        consumer.accept(net.minecraft.class_4722.field_21719);
        consumer.accept(net.minecraft.class_4722.field_21720);
        consumer.accept(net.minecraft.class_4722.field_21721);
        consumer.accept(net.minecraft.class_4722.field_21722);
        consumer.accept(net.minecraft.class_4722.field_21723);
    }

    /** 1.20.4 BannerRenderer.renderPatterns(.., patterns) */
    public static void method_29999(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, int overlay,
                                    net.minecraft.class_630 flag, net.minecraft.class_4730 material, boolean banner,
                                    List<Pair<net.minecraft.class_6880<?>, net.minecraft.class_1767>> patterns) {
        method_23802(pose, buffers, light, overlay, flag, material, banner, patterns, false);
    }

    /**
     * 1.20.4 BannerRenderer.renderPatterns(.., patterns, glint): the flag part in the material, then at most
     * 17 of the (pattern, color) pairs -- the base pair first -- each in its pattern's material, tinted by
     * the color; a pattern without a key draws nothing, as 1.20.4's unwrapKey().map(..) left it out
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void method_23802(net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, int overlay,
                                    net.minecraft.class_630 flag, net.minecraft.class_4730 material, boolean banner,
                                    List<Pair<net.minecraft.class_6880<?>, net.minecraft.class_1767>> patterns, boolean glint) {
        flag.method_22698(pose, material.method_30001(buffers, net.minecraft.class_1921::method_23572, glint), light, overlay);
        for (int i = 0; i < 17 && i < patterns.size(); i++) {
            Pair<net.minecraft.class_6880<?>, net.minecraft.class_1767> p = patterns.get(i);
            float[] c = p.getSecond().method_7787();
            java.util.Optional<net.minecraft.class_5321> key = (java.util.Optional) p.getFirst().method_40230();
            if (key.isPresent()) {
                net.minecraft.class_4730 m = banner ? method_33081(key.get()) : method_33083(key.get());
                flag.method_22699(pose, m.method_24145(buffers, net.minecraft.class_1921::method_23588), light, overlay, c[0], c[1], c[2], 1.0F);
            }
        }
    }

    private CbRestBannerRenderClient() {
    }
}
