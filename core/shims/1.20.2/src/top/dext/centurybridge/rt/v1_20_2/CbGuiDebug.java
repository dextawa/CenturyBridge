package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 debug renderer API on 1.20.2. 1.20.2 moved the renderers' data into the debug payloads' records
 * (bee, hive, brain, goal, structure piece) and keys tracked game events and structure boxes by registry key;
 * these convert a 1.20.1 call's values into what the 1.20.2 renderer takes (callReshapes conv) and what the
 * 1.20.2 renderer is handed back into the 1.20.1 shape a mod subclass overrides (overrideHooks wrap / bridge).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbGuiDebug {

    // BeeDebugRenderer

    /** callReshapes conv: the 1.20.2 record of a 1.20.1 HiveInfo */
    public static net.minecraft.class_8718.class_8719 cbHiveInfo(top.dext.centurybridge.facade.v1_20_2.class_4703$class_4705 hive) {
        return new net.minecraft.class_8718.class_8719(hive.field_21543, hive.field_21544, hive.field_21545, hive.field_21625,
            hive.field_21626);
    }

    /** callReshapes conv: the game time a 1.20.1 HiveInfo was last seen, which 1.20.2 takes beside the record */
    public static long cbHiveLastSeen(top.dext.centurybridge.facade.v1_20_2.class_4703$class_4705 hive) {
        return hive.field_21546;
    }

    /** overrideHooks bridge: 1.20.2's addOrUpdateHiveInfo(info, lastSeen) reaching a 1.20.1 override */
    public static void cbAddHive(CbGuiLegacyBeeRenderer self, net.minecraft.class_8718.class_8719 info, long lastSeen) {
        self.method_23807(new top.dext.centurybridge.facade.v1_20_2.class_4703$class_4705(info.comp_1694(), info.comp_1695(),
            info.comp_1696(), info.comp_1697(), info.comp_1698(), lastSeen));
    }

    /**
     * callReshapes conv: the 1.20.2 record of a 1.20.1 BeeInfo. Its goals and blacklisted hives are views of the
     * BeeInfo's own collections (1.20.1 a List and a Set, 1.20.2 a Set and a List), so what a mod adds to them
     * later is drawn, as in 1.20.1, whose renderer kept the BeeInfo itself.
     */
    public static net.minecraft.class_8707.class_5243 cbBeeInfo(top.dext.centurybridge.facade.v1_20_2.class_4703$class_5243 bee) {
        return new net.minecraft.class_8707.class_5243(bee.field_24322, bee.field_24323, cbVec(bee.field_24324), bee.field_24325,
            bee.field_24326, bee.field_24327, bee.field_24328, cbSetView(bee.field_24329), cbListView(bee.field_24330));
    }

    /** overrideHooks wrap: the 1.20.1 BeeInfo of the record 1.20.2 hands addOrUpdateBeeInfo */
    public static top.dext.centurybridge.facade.v1_20_2.class_4703$class_5243 cbLegacyBeeInfo(net.minecraft.class_8707.class_5243 bee) {
        top.dext.centurybridge.facade.v1_20_2.class_4703$class_5243 old = new top.dext.centurybridge.facade.v1_20_2.class_4703$class_5243(
            bee.comp_1649(), bee.comp_1650(), bee.comp_1651(), bee.comp_1652(), bee.comp_1653(), bee.comp_1654(), bee.comp_1655());
        old.field_24329.addAll(bee.comp_1656());
        old.field_24330.addAll(bee.comp_1657());
        return old;
    }

    // BrainDebugRenderer

    /**
     * callReshapes conv: the 1.20.2 record of a 1.20.1 BrainDump, holding the BrainDump's own lists and sets, so
     * what a mod adds to them later is drawn, as in 1.20.1, whose renderer kept the BrainDump itself
     */
    public static net.minecraft.class_8708.class_4232 cbBrainDump(top.dext.centurybridge.facade.v1_20_2.class_4207$class_4232 d) {
        return new net.minecraft.class_8708.class_4232(d.field_18923, d.field_18924, d.field_19328, d.field_18925, d.field_19329,
            d.field_22406, d.field_22407, cbVec(d.field_18926), d.field_19372, d.field_19330, d.field_19373, d.field_38348,
            d.field_18927, d.field_18928, d.field_19374, d.field_19375, d.field_18930, d.field_25287);
    }

    /** overrideHooks wrap: the 1.20.1 BrainDump of the record 1.20.2 hands addOrUpdateBrainDump */
    public static top.dext.centurybridge.facade.v1_20_2.class_4207$class_4232 cbLegacyBrainDump(net.minecraft.class_8708.class_4232 d) {
        top.dext.centurybridge.facade.v1_20_2.class_4207$class_4232 old = new top.dext.centurybridge.facade.v1_20_2.class_4207$class_4232(
            d.comp_1659(), d.comp_1660(), d.comp_1661(), d.comp_1662(), d.comp_1663(), d.comp_1664(), d.comp_1665(), d.comp_1666(),
            d.comp_1667(), d.comp_1668(), d.comp_1669(), d.comp_1670());
        old.field_18927.addAll(d.comp_1671());
        old.field_18928.addAll(d.comp_1672());
        old.field_19374.addAll(d.comp_1673());
        old.field_19375.addAll(d.comp_1674());
        old.field_18930.addAll(d.comp_1675());
        old.field_25287.addAll(d.comp_1676());
        return old;
    }

    // GoalSelectorDebugRenderer

    /**
     * callReshapes conv: the one position 1.20.2 draws a mob's goals above, the first goal's. 1.20.1 drew each
     * goal above its own position; vanilla gave every goal of a mob that mob's position.
     */
    public static net.minecraft.class_2338 cbGoalsPos(java.util.List<top.dext.centurybridge.facade.v1_20_2.class_4205$class_4206> goals) {
        return goals.isEmpty() ? net.minecraft.class_2338.field_10980 : goals.get(0).field_18782;
    }

    /**
     * callReshapes conv: the goals as 1.20.2 records, a view of the 1.20.1 list, so what a mod adds to it later is
     * drawn, as in 1.20.1, whose renderer kept the list itself
     */
    public static java.util.List<net.minecraft.class_8716.class_8717> cbGoals(
            java.util.List<top.dext.centurybridge.facade.v1_20_2.class_4205$class_4206> goals) {
        return new java.util.AbstractList<>() {
            @Override
            public net.minecraft.class_8716.class_8717 get(int index) {
                top.dext.centurybridge.facade.v1_20_2.class_4205$class_4206 g = goals.get(index);
                return new net.minecraft.class_8716.class_8717(g.field_18783, g.field_18785, g.field_18784);
            }

            @Override
            public int size() {
                return goals.size();
            }
        };
    }

    /** overrideHooks bridge: 1.20.2's addGoalSelector(mobId, pos, goals) reaching a 1.20.1 override, every goal at the mob's position */
    public static void cbAddGoalSelector(CbGuiLegacyGoalRenderer self, int mobId, net.minecraft.class_2338 pos,
                                         java.util.List<net.minecraft.class_8716.class_8717> goals) {
        java.util.List<top.dext.centurybridge.facade.v1_20_2.class_4205$class_4206> old = new java.util.ArrayList<>(goals.size());
        for (net.minecraft.class_8716.class_8717 g : goals) {
            old.add(new top.dext.centurybridge.facade.v1_20_2.class_4205$class_4206(pos, g.comp_1690(), g.comp_1692(), g.comp_1691()));
        }
        self.method_19430(mobId, old);
    }

    // GameEventListenerRenderer

    /**
     * callReshapes conv: the registry key 1.20.2 tracks a game event by. Every GameEvent has one: its constructor
     * takes an intrusive holder of the game event registry, which refuses to freeze with an unregistered one
     * and to make holders once frozen.
     */
    public static net.minecraft.class_5321<net.minecraft.class_5712> cbGameEventKey(net.minecraft.class_5712 event) {
        return net.minecraft.class_7923.field_41171.method_29113(event)
            .orElseThrow(() -> new IllegalArgumentException("unregistered game event " + event));
    }

    /** overrideHooks wrap: the GameEvent of the key 1.20.2 hands trackGameEvent */
    public static net.minecraft.class_5712 cbGameEvent(net.minecraft.class_5321<net.minecraft.class_5712> key) {
        return net.minecraft.class_7923.field_41171.method_29107(key);
    }

    // StructureRenderer

    /** callReshapes conv: 1.20.2's piece records of 1.20.1's parallel lists (piece i is a start when isStart i is true) */
    public static java.util.List<net.minecraft.class_8726.class_8727> cbPieces(java.util.List<net.minecraft.class_3341> pieces,
                                                                             java.util.List<Boolean> isStart) {
        java.util.List<net.minecraft.class_8726.class_8727> out = new java.util.ArrayList<>(pieces.size());
        for (int i = 0; i < pieces.size(); i++) {
            out.add(new net.minecraft.class_8726.class_8727(pieces.get(i), isStart.get(i)));
        }
        return out;
    }

    /**
     * callReshapes conv: the level 1.20.2 files structure boxes under, for the dimension type 1.20.1 filed them
     * under. 1.20.1 drew them while the client's level was of that type: the client's level when it is, else
     * the level named like the type (vanilla's levels are: overworld, the_nether, the_end); with no level or
     * an unregistered type, a key no level has.
     */
    public static net.minecraft.class_5321<net.minecraft.class_1937> cbLevelOf(net.minecraft.class_2874 type) {
        net.minecraft.class_638 level = net.minecraft.class_310.method_1551().field_1687;
        if (level != null && level.method_8597().equals(type)) {
            return level.method_27983();
        }
        net.minecraft.class_2960 id = level == null ? null
            : level.method_30349().method_30530(net.minecraft.class_7924.field_41241).method_10221(type);
        return net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41223,
            id != null ? id : new net.minecraft.class_2960("centurybridge", "unregistered_dimension_type"));
    }

    /** overrideHooks bridge: 1.20.2's addBoundingBox(box, pieces, level) reaching a 1.20.1 override */
    public static void cbAddBoundingBox(CbGuiLegacyStructureRenderer self, net.minecraft.class_3341 box,
                                        java.util.List<net.minecraft.class_8726.class_8727> pieces,
                                        net.minecraft.class_5321<net.minecraft.class_1937> dimension) {
        java.util.List<net.minecraft.class_3341> boxes = new java.util.ArrayList<>(pieces.size());
        java.util.List<Boolean> starts = new java.util.ArrayList<>(pieces.size());
        for (net.minecraft.class_8726.class_8727 p : pieces) {
            boxes.add(p.comp_1714());
            starts.add(p.comp_1715());
        }
        self.method_3871(box, boxes, starts, cbDimensionTypeOf(dimension));
    }

    /** the type of the level a key names: the client level's when it is that level, else the type registered under its name, else null */
    private static net.minecraft.class_2874 cbDimensionTypeOf(net.minecraft.class_5321<net.minecraft.class_1937> dimension) {
        net.minecraft.class_638 level = net.minecraft.class_310.method_1551().field_1687;
        if (level == null) {
            return null;
        }
        if (level.method_27983().equals(dimension)) {
            return level.method_8597();
        }
        return level.method_30349().method_30530(net.minecraft.class_7924.field_41241).method_10223(dimension.method_29177());
    }

    // shared

    /** a 1.20.1 Position as the Vec3 1.20.2 records hold */
    private static net.minecraft.class_243 cbVec(net.minecraft.class_2374 pos) {
        if (pos == null || pos instanceof net.minecraft.class_243) {
            return (net.minecraft.class_243) pos;
        }
        return new net.minecraft.class_243(pos.method_10216(), pos.method_10214(), pos.method_10215());
    }

    /** a list read as a Set: its elements in its order (duplicates included, as a list draws them) */
    private static <E> java.util.Set<E> cbSetView(java.util.List<E> list) {
        return new java.util.AbstractSet<>() {
            @Override
            public java.util.Iterator<E> iterator() {
                return list.iterator();
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public boolean contains(Object o) {
                return list.contains(o);
            }
        };
    }

    /** a set read as a List, in the set's iteration order */
    private static <E> java.util.List<E> cbListView(java.util.Set<E> set) {
        return new java.util.AbstractList<>() {
            @Override
            public E get(int index) {
                if (index < 0 || index >= set.size()) {
                    throw new IndexOutOfBoundsException(index);
                }
                java.util.Iterator<E> it = set.iterator();
                for (int i = 0; i < index; i++) {
                    it.next();
                }
                return it.next();
            }

            @Override
            public int size() {
                return set.size();
            }

            @Override
            public java.util.Iterator<E> iterator() {
                return set.iterator();
            }

            @Override
            public boolean contains(Object o) {
                return set.contains(o);
            }
        };
    }

    private CbGuiDebug() {
    }
}
