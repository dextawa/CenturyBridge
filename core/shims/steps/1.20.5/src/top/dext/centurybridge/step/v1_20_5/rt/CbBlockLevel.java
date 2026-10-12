package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4's Level API on 1.20.5.
 *
 * <p>Map data: 1.20.4 named a map's saved data by its storage key ("map_" + id, MapItem.makeKey)
 * and counted free ids as an int; 1.20.5 takes a MapId, whose key() is that same name. A name of
 * the "map_N" form is that map's MapId. 1.20.4's ServerLevel read and wrote any saved-data name
 * through the overworld's data storage, so another name still goes there on a server level; 1.20.4's
 * ClientLevel only ever held the "map_N" names the map packets filled in (setMapData was a no-op),
 * so on any other level another name finds nothing and stores nothing. A mod level class overriding
 * the old shapes is asked with the name itself (bridge hooks: MapId -> key()).
 *
 * <p>dimensionTypeId(): 1.20.4 kept the key of the level's dimension type (taken in the constructor
 * from the holder, unwrapKey().orElseThrow()); 1.20.5 dropped the method, the holder's key is the
 * same value. A mod level class overriding it keeps its override reachable (adapter) and its answer
 * is the dimension type 1.20.4 sent a joining player (CbBlockServerPlayerMixin).
 *
 * <p>createFireworks: 1.20.4 handed the rocket's "Fireworks" tag, of which only the Explosions list
 * was read (FireworkParticles.Starter: Type, Flicker, Trail, Colors, FadeColors of each); 1.20.5
 * hands those explosions as FireworkExplosion components.
 *
 * <p>potionBrewing(): abstract in 1.20.5, where brewing became an instance; 1.20.4 brewing was
 * static, the same for every level. A 1.20.4 level class gets its server's brewing, or -- no
 * server -- the brewing 1.20.5 builds for the level's enabled features.
 */
public final class CbBlockLevel {

    /** the 1.20.4 getMapData(String) a mod level class overrides */
    public interface LegacyGetMap {
        net.minecraft.class_22 method_17891(String id);
    }

    /** the 1.20.4 setMapData(String, data) a mod level class overrides */
    public interface LegacySetMap {
        void method_17890(String id, net.minecraft.class_22 data);
    }

    /** the 1.20.4 dimensionTypeId() a mod level class overrides */
    public interface LegacyDimensionType {
        net.minecraft.class_5321<net.minecraft.class_2874> method_44013();
    }

    /** super's getMapData / setMapData, reached from such a class's super calls */
    public interface SuperGetMap {
        net.minecraft.class_22 cb$super$method_17891(net.minecraft.class_9209 id);
    }

    public interface SuperSetMap {
        void cb$super$method_17890(net.minecraft.class_9209 id, net.minecraft.class_22 data);
    }

    /** the MapId a 1.20.4 map storage name stands for, or null when it is not one ("map_" + an int as MapId.key() writes it) */
    public static net.minecraft.class_9209 mapId(String name) {
        if (name == null || !name.startsWith("map_")) {
            return null;
        }
        String digits = name.substring(name.length() > 4 && name.charAt(4) == '-' ? 5 : 4);
        if (digits.isEmpty()) {
            return null;
        }
        for (int i = 0; i < digits.length(); i++) {
            char c = digits.charAt(i);
            if (c < '0' || c > '9') {
                return null;
            }
        }
        try {
            net.minecraft.class_9209 id = new net.minecraft.class_9209(Integer.parseInt(name.substring(4)));
            return id.method_56814().equals(name) ? id : null; // "map_007" is no map's key
        } catch (NumberFormatException e) {
            return null; // out of int range: no map's key either
        }
    }

    /** MapId -> 1.20.4's storage name */
    public static String mapKey(net.minecraft.class_9209 id) {
        return id == null ? null : id.method_56814();
    }

    /** getFreeMapId(): MapId -> int */
    public static int mapIdNumber(net.minecraft.class_9209 id) {
        return id.comp_2315();
    }

    /** a mod level's getFreeMapId() as 1.20.5's MapId */
    public static net.minecraft.class_9209 mapIdOf(int id) {
        return new net.minecraft.class_9209(id);
    }

    private static net.minecraft.class_26 storage(net.minecraft.class_3218 level) {
        return level.method_8503().method_30002().method_17983();
    }

    /** Level.getMapData(String) called on any level */
    public static net.minecraft.class_22 method_17891(net.minecraft.class_1937 self, String name) {
        if (self instanceof LegacyGetMap legacy) {
            return legacy.method_17891(name);
        }
        net.minecraft.class_9209 id = mapId(name);
        if (id != null) {
            return self.method_17891(id);
        }
        return self instanceof net.minecraft.class_3218 server
            ? storage(server).method_20786(net.minecraft.class_22.method_52611(), name) : null;
    }

    /** Level.setMapData(String, data) called on any level */
    public static void method_17890(net.minecraft.class_1937 self, String name, net.minecraft.class_22 data) {
        if (self instanceof LegacySetMap legacy) {
            legacy.method_17890(name, data);
            return;
        }
        net.minecraft.class_9209 id = mapId(name);
        if (id != null) {
            self.method_17890(id, data);
        } else if (self instanceof net.minecraft.class_3218 server) {
            storage(server).method_123(name, data);
        }
    }

    /** getMapData(MapId) of a mod level class overriding the 1.20.4 shape */
    public static net.minecraft.class_22 getMapData(LegacyGetMap self, net.minecraft.class_9209 id) {
        return self.method_17891(id.method_56814());
    }

    /** setMapData(MapId, data) of such a class */
    public static void setMapData(LegacySetMap self, net.minecraft.class_9209 id, net.minecraft.class_22 data) {
        self.method_17890(id.method_56814(), data);
    }

    /** such a class's super.getMapData(name): super's map for a map name, a server level's storage for another */
    public static net.minecraft.class_22 superGetMapData(SuperGetMap self, String name) {
        net.minecraft.class_9209 id = mapId(name);
        if (id != null) {
            return self.cb$super$method_17891(id);
        }
        return self instanceof net.minecraft.class_3218 server
            ? storage(server).method_20786(net.minecraft.class_22.method_52611(), name) : null;
    }

    /** such a class's super.setMapData(name, data) */
    public static void superSetMapData(SuperSetMap self, String name, net.minecraft.class_22 data) {
        net.minecraft.class_9209 id = mapId(name);
        if (id != null) {
            self.cb$super$method_17890(id, data);
        } else if (self instanceof net.minecraft.class_3218 server) {
            storage(server).method_123(name, data);
        }
    }

    /** Level.dimensionTypeId() called on any level */
    public static net.minecraft.class_5321<net.minecraft.class_2874> method_44013(net.minecraft.class_1937 self) {
        return self instanceof LegacyDimensionType legacy ? legacy.method_44013() : superDimensionTypeId(self);
    }

    /** 1.20.4's Level.dimensionTypeId(): the key of the level's dimension type (a mod's super call) */
    public static net.minecraft.class_5321<net.minecraft.class_2874> superDimensionTypeId(net.minecraft.class_1937 self) {
        return self.method_40134().method_40230().orElseThrow();
    }

    /** a level's 1.20.4 fireworks tag as 1.20.5's explosions (Level.createFireworks) */
    public static java.util.List<net.minecraft.class_9283> fireworkExplosions(net.minecraft.class_2487 tag) {
        java.util.List<net.minecraft.class_9283> out = new java.util.ArrayList<>();
        if (tag != null) {
            net.minecraft.class_2499 explosions = tag.method_10554("Explosions", 10);
            for (int i = 0; i < explosions.size(); i++) {
                net.minecraft.class_2487 e = explosions.method_10602(i);
                out.add(new net.minecraft.class_9283(
                    net.minecraft.class_9283.class_1782.method_7813(e.method_10571("Type")),
                    it.unimi.dsi.fastutil.ints.IntArrayList.wrap(e.method_10561("Colors")),
                    it.unimi.dsi.fastutil.ints.IntArrayList.wrap(e.method_10561("FadeColors")),
                    e.method_10577("Trail"), e.method_10577("Flicker")));
            }
        }
        return out;
    }

    /** 1.20.5's explosions as the 1.20.4 tag a mod level's createFireworks override read */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2487 fireworksTag(java.util.List explosions) {
        net.minecraft.class_2487 tag = new net.minecraft.class_2487();
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (Object o : explosions) {
            net.minecraft.class_9283 e = (net.minecraft.class_9283) o;
            net.minecraft.class_2487 t = new net.minecraft.class_2487();
            t.method_10567("Type", (byte) e.comp_2386().method_7816());
            t.method_10556("Flicker", e.comp_2390());
            t.method_10556("Trail", e.comp_2389());
            t.method_10539("Colors", e.comp_2387().toIntArray());
            t.method_10539("FadeColors", e.comp_2388().toIntArray());
            list.add(t);
        }
        tag.method_10566("Explosions", list);
        return tag;
    }

    private static final java.util.Map<net.minecraft.class_7699, net.minecraft.class_1845> BREWING =
        new java.util.concurrent.ConcurrentHashMap<>();

    /** potionBrewing() of a 1.20.4 level class (abstractFills) */
    public static net.minecraft.class_1845 potionBrewing(net.minecraft.class_1937 self) {
        net.minecraft.server.MinecraftServer server = self.method_8503();
        return server != null ? server.method_59777()
            : BREWING.computeIfAbsent(self.method_45162(), net.minecraft.class_1845::method_8076);
    }

    private CbBlockLevel() {
    }
}
