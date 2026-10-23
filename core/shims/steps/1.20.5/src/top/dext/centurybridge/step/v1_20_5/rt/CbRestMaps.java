package top.dext.centurybridge.step.v1_20_5.rt;

import top.dext.centurybridge.step.v1_20_5.facade.class_20$class_21;

/**
 * 1.20.4's map API on 1.20.5.
 *
 * <ul>
 * <li>Map ids: 1.20.4 passed a map's id as an int (an OptionalInt for an item frame's map), 1.20.5 as a
 *     MapId record of that int.</li>
 * <li>Decoration types: 1.20.4's MapDecoration.Type enum is the step's facade
 *     ({@link class_20$class_21}); 1.20.5 has the MapDecorationType registry, whose vanilla entries
 *     carry the enum constants' names and properties.</li>
 * <li>1.20.4 members 1.20.5 dropped, with 1.20.4's bodies: MapDecoration.getImage,
 *     MapBanner.load (its NBT form), MapItemSavedData.scaled(int), MapPatch's public fields (a record's
 *     accessors in 1.20.5).</li>
 * </ul>
 */
public final class CbRestMaps {

    /** a 1.20.4 map id as 1.20.5's MapId */
    public static net.minecraft.class_9209 mapId(int id) {
        return new net.minecraft.class_9209(id);
    }

    /** the int a 1.20.5 MapId stands for */
    public static int mapIdNumber(net.minecraft.class_9209 id) {
        return id.comp_2315();
    }

    /** an item frame's 1.20.4 map id (empty: no map) as 1.20.5's nullable MapId */
    public static net.minecraft.class_9209 mapIdOf(java.util.OptionalInt id) {
        return id.isPresent() ? new net.minecraft.class_9209(id.getAsInt()) : null;
    }

    /** a nullable 1.20.5 MapId as 1.20.4's OptionalInt */
    public static java.util.OptionalInt mapIdOptional(net.minecraft.class_9209 id) {
        return id == null ? java.util.OptionalInt.empty() : java.util.OptionalInt.of(id.comp_2315());
    }

    /** a 1.20.4 decoration type as the 1.20.5 type registered under its name; null for null */
    public static net.minecraft.class_6880<net.minecraft.class_9428> typeHolder(class_20$class_21 type) {
        return type == null ? null : type.holder();
    }

    /**
     * the 1.20.4 decoration type of a 1.20.5 one: the constant of the same name; null for null, and for
     * a type 1.20.4 had no constant for (1.20.5's trial_chambers, a type a 1.20.5 mod registered)
     */
    public static class_20$class_21 typeOf(net.minecraft.class_6880<net.minecraft.class_9428> type) {
        return class_20$class_21.of(type);
    }

    /**
     * 1.20.4 MapDecoration.getImage(): the type's icon, which 1.20.4 kept as the constant's ordinal. A
     * type 1.20.4 had no constant for has no icon of 1.20.4's: -1, which no 1.20.4 constant has.
     */
    public static byte method_92(net.minecraft.class_20 decoration) {
        class_20$class_21 type = class_20$class_21.of(decoration.comp_1842());
        return type == null ? (byte) -1 : type.method_98();
    }

    /**
     * 1.20.4 MapBanner.load(tag): the position under Pos (an X/Y/Z compound), the color under Color
     * (white for a missing or unknown name), the name under Name as component JSON when present.
     */
    public static net.minecraft.class_17 method_67(net.minecraft.class_2487 tag) {
        net.minecraft.class_2338 pos = CbRestNbt.method_10691(tag.method_10562("Pos"));
        net.minecraft.class_1767 color = net.minecraft.class_1767.method_7793(tag.method_10558("Color"),
            net.minecraft.class_1767.field_7952);
        net.minecraft.class_2561 name = tag.method_10545("Name")
            ? net.minecraft.class_2561.class_2562.method_10877(tag.method_10558("Name"), CbRegistries.current())
            : null;
        return new net.minecraft.class_17(pos, color, java.util.Optional.ofNullable(name));
    }

    /**
     * 1.20.4 MapBanner.save(): Pos (an X/Y/Z compound), Color (the dye's name), Name (component JSON)
     * when the banner has one -- of the values handed over, so a cover and a mod class read their own.
     */
    public static net.minecraft.class_2487 saveBanner(net.minecraft.class_2338 pos, net.minecraft.class_1767 color,
                                                      net.minecraft.class_2561 name) {
        net.minecraft.class_2487 tag = new net.minecraft.class_2487();
        tag.method_10566("Pos", CbRestNbt.method_10692(pos));
        tag.method_10582("Color", color.method_7792());
        if (name != null) {
            tag.method_10582("Name", net.minecraft.class_2561.class_2562.method_10867(name, CbRegistries.current()));
        }
        return tag;
    }

    /**
     * Added to a mod MapBanner class that overrides 1.20.4's save() (adapter hook): its save(), which the
     * banner list below reads (vanilla's is the cover of CbRestMapBannerMixin, 1.20.4's own keys only).
     */
    public interface OwnSave {
        net.minecraft.class_2487 method_74();
    }

    /**
     * The banner list 1.20.5's MapItemSavedData.save encoded with MapBanner.CODEC, one compound per
     * banner in the map's order, with what a banner class overriding 1.20.4's save() wrote there besides
     * 1.20.4's own keys (Pos, Color, Name, which the codec writes as pos, color, name): 1.20.4 saved
     * each banner as its save(). 1.20.4's MapBanner.load never read those keys back either.
     */
    public static net.minecraft.class_2520 withBannerSaves(net.minecraft.class_2520 encoded,
                                                         java.util.Collection<net.minecraft.class_17> banners) {
        if (!(encoded instanceof net.minecraft.class_2499 list) || list.size() != banners.size()) {
            return encoded;
        }
        int i = 0;
        for (net.minecraft.class_17 banner : banners) {
            net.minecraft.class_2520 element = list.method_10534(i++);
            if ((Object) banner instanceof OwnSave saves && element instanceof net.minecraft.class_2487 compound) {
                net.minecraft.class_2487 own = saves.method_74();
                if (own == null) {
                    continue;
                }
                for (String key : own.method_10541()) {
                    if (!key.equals("Pos") && !key.equals("Color") && !key.equals("Name")) {
                        compound.method_10566(key, own.method_10580(key).method_10707());
                    }
                }
            }
        }
        return encoded;
    }

    /**
     * 1.20.4 MapItemSavedData.scaled(n): a fresh map at the same center, n scale steps further (clamped
     * to 0..4), with the same tracking flags and dimension. 1.20.5's scaled() is n = 1.
     */
    public static net.minecraft.class_22 method_32364(net.minecraft.class_22 data, int n) {
        if (n == 1) {
            return data.method_32364();
        }
        Tracking flags = (Tracking) data;
        return net.minecraft.class_22.method_32363(data.field_116, data.field_115,
            (byte) net.minecraft.class_3532.method_15340(data.field_119 + n, 0, 4), flags.cbRest$trackingPosition(),
            flags.cbRest$unlimitedTracking(), data.field_118);
    }

    /** MapItemSavedData's private tracking flags (CbRestMapItemSavedDataMixin) */
    public interface Tracking {
        boolean cbRest$trackingPosition();

        boolean cbRest$unlimitedTracking();
    }

    /** 1.20.4 MapPatch.startX */
    public static int field_27892(net.minecraft.class_22.class_5637 patch) {
        return patch.comp_2316();
    }

    /** 1.20.4 MapPatch.startY */
    public static int field_27893(net.minecraft.class_22.class_5637 patch) {
        return patch.comp_2317();
    }

    /** 1.20.4 MapPatch.width */
    public static int field_27894(net.minecraft.class_22.class_5637 patch) {
        return patch.comp_2318();
    }

    /** 1.20.4 MapPatch.height */
    public static int field_27895(net.minecraft.class_22.class_5637 patch) {
        return patch.comp_2319();
    }

    /** 1.20.4 MapPatch.mapColors */
    public static byte[] field_27896(net.minecraft.class_22.class_5637 patch) {
        return patch.comp_2320();
    }

    private CbRestMaps() {
    }
}
