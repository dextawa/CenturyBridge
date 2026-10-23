package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 entity members (area s4fin world/entity) whose 1.20.5 form takes an argument 1.20.4 fixed, or
 * moved to another class:
 * <ul>
 * <li>BehaviorUtils.lockGazeAndWalkToEachOther and AnimalMakeLove: 1.20.5 takes how close the two walk;
 *     1.20.4 always walked them to within 2 blocks.</li>
 * <li>AttributeModifier.Operation.fromValue(id): 1.20.4's lookup, which refused an unknown id.</li>
 * <li>IronGolem.Crackiness moved to Crackiness (shared with wolf armor); byFraction is the golem's.</li>
 * </ul>
 */
public final class CbEntityMisc {

    /** the distance 1.20.4's BehaviorUtils.lockGazeAndWalkToEachOther walked the two entities to */
    public static int walkCloseEnough() {
        return 2;
    }

    /** AttributeModifier.Operation.fromValue(id) (1.20.4: an unknown id is an error; 1.20.5's BY_ID clamps) */
    public static net.minecraft.class_1322.class_1323 operation(int id) {
        for (net.minecraft.class_1322.class_1323 op : net.minecraft.class_1322.class_1323.values()) {
            if (op.method_56082() == id) {
                return op;
            }
        }
        throw new IllegalArgumentException("No operation with value " + id);
    }

    /** IronGolem.Crackiness.byFraction(fraction): the golem's crackiness levels */
    public static net.minecraft.class_9273.class_4621 crackiness(float fraction) {
        return net.minecraft.class_9273.field_49210.method_57282(fraction);
    }

    /**
     * AttributeInstance.getModifiers(operation) (CbEntityAttributeInstanceMixin): 1.20.5's live map of one
     * operation's modifiers by id, as 1.20.4's live set
     */
    public static java.util.Set<net.minecraft.class_1322> modifierSet(java.util.Map<java.util.UUID, net.minecraft.class_1322> byId) {
        return new java.util.AbstractSet<>() {
            @Override
            public java.util.Iterator<net.minecraft.class_1322> iterator() {
                return byId.values().iterator();
            }

            @Override
            public int size() {
                return byId.size();
            }

            @Override
            public boolean contains(Object o) {
                return o instanceof net.minecraft.class_1322 m && m.equals(byId.get(m.comp_2447()));
            }

            @Override
            public boolean add(net.minecraft.class_1322 m) {
                return !m.equals(byId.put(m.comp_2447(), m));
            }

            @Override
            public boolean remove(Object o) {
                return o instanceof net.minecraft.class_1322 m && byId.remove(m.comp_2447(), m);
            }
        };
    }

    /** Shulker.getProgressAabb / getProgressDeltaAabb: 1.20.5 scales the box; 1.20.4's was the unscaled one */
    public static float unitScale() {
        return 1.0F;
    }

    /** Painting.loadVariant(tag)'s 1.20.4 body: the "variant" id, in the painting variant registry */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static java.util.Optional method_48929(net.minecraft.class_2487 tag) {
        return java.util.Optional.ofNullable(net.minecraft.class_2960.method_12829(tag.method_10558("variant")))
            .map(id -> net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41209, id))
            .flatMap(key -> net.minecraft.class_7923.field_41182.method_40264((net.minecraft.class_5321) key));
    }

    /** Arrow.getCustomColor(stack): the stack's custom potion color (1.20.4's CustomPotionColor), else -1 */
    public static int arrowColor(net.minecraft.class_1799 stack) {
        net.minecraft.class_1844 c = stack.method_57825(net.minecraft.class_9334.field_49651, net.minecraft.class_1844.field_49274);
        return c.comp_2379().orElse(-1);
    }

    /** ThrowableProjectile.getGravity(): 1.20.5's double gravity as 1.20.4's float */
    public static float gravityFloat(double gravity) {
        return (float) gravity;
    }

    /** a 1.20.4 override's float gravity as 1.20.5's double */
    public static double gravityDouble(float gravity) {
        return gravity;
    }

    /** Raid.getLeaderBannerInstance(): 1.20.5 takes the banner patterns; the running world's */
    public static net.minecraft.class_7871<net.minecraft.class_2582> bannerPatterns() {
        return CbRegistries.current().method_46762(net.minecraft.class_7924.field_41252);
    }

    // ---------------------------------------------------------------- WindCharge(type, breeze, level)

    /** new WindCharge(type, breeze, level): 1.20.4's -- at the breeze's x, sneeze height and z, owned by it */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_8956 windCharge(net.minecraft.class_1299 type, net.minecraft.class_8949 breeze, net.minecraft.class_1937 level) {
        net.minecraft.class_8956 charge = new net.minecraft.class_8956(type, level);
        windChargeBuilt(charge, breeze);
        return charge;
    }

    /** after a 1.20.4 subclass's super(type, breeze, level) (post): placed and owned as 1.20.4's constructor did */
    public static void windChargeBuilt(net.minecraft.class_8956 charge, net.minecraft.class_8949 breeze) {
        charge.method_5808(breeze.method_23317(), breeze.method_55009(), breeze.method_23321(), charge.method_36454(), charge.method_36455());
        charge.method_7432(breeze);
    }

    // ---------------------------------------------------------------- LongJump

    /** LongJump.isClearTransition(breeze, target)'s 1.20.4 body: within 50 blocks and nothing in the way */
    public static boolean method_55027(net.minecraft.class_8949 breeze, net.minecraft.class_243 target) {
        net.minecraft.class_243 from = new net.minecraft.class_243(breeze.method_23317(), breeze.method_23318(), breeze.method_23321());
        if (from.method_1022(target) > 50.0) {
            return false;
        }
        return breeze.method_37908().method_17742(new net.minecraft.class_3959(from, target, net.minecraft.class_3959.class_3960.field_17558,
            net.minecraft.class_3959.class_242.field_1348, breeze)).method_17783() == net.minecraft.class_239.class_240.field_1333;
    }

    // ---------------------------------------------------------------- PoiManager(path, ...)

    /**
     * The RegionStorageInfo 1.20.5's ChunkMap names a dimension's poi storage with, read back from the folder
     * 1.20.4's caller passed (DimensionType.getStorageFolder's layout: the world folder for the overworld, DIM-1 /
     * DIM1 in it for the nether and the end, dimensions/namespace/path for the rest, "poi" inside).
     */
    public static net.minecraft.class_9240 poiStorage(java.nio.file.Path poi) {
        java.nio.file.Path dim = poi.toAbsolutePath().normalize().getParent();
        java.nio.file.Path root = dim;
        net.minecraft.class_5321<net.minecraft.class_1937> key = net.minecraft.class_1937.field_25179;
        String name = dim.getFileName() == null ? "" : dim.getFileName().toString();
        if (name.equals("DIM-1")) {
            key = net.minecraft.class_1937.field_25180;
            root = dim.getParent();
        } else if (name.equals("DIM1")) {
            key = net.minecraft.class_1937.field_25181;
            root = dim.getParent();
        } else {
            for (java.nio.file.Path p = dim; p != null && p.getParent() != null; p = p.getParent()) {
                if (p.getFileName() != null && p.getFileName().toString().equals("dimensions")) {
                    java.nio.file.Path rel = p.relativize(dim);
                    if (rel.getNameCount() >= 2) {
                        StringBuilder path = new StringBuilder();
                        for (int i = 1; i < rel.getNameCount(); i++) {
                            path.append(i > 1 ? "/" : "").append(rel.getName(i));
                        }
                        net.minecraft.class_2960 id = net.minecraft.class_2960.method_12829(rel.getName(0) + ":" + path);
                        if (id != null) {
                            key = net.minecraft.class_5321.method_29179(net.minecraft.class_7924.field_41223, id);
                            root = p.getParent();
                        }
                    }
                    break;
                }
            }
        }
        String level = root == null || root.getFileName() == null ? "" : root.getFileName().toString();
        return new net.minecraft.class_9240(level, key, "poi");
    }

    private CbEntityMisc() {
    }
}
