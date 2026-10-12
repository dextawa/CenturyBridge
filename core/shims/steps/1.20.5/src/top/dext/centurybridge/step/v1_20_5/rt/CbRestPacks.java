package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 packs on 1.20.5. 1.20.4 named a pack by a String id and a builtin flag (PackResources.packId(),
 * isBuiltin(); pack suppliers and pack resources took the flag at construction); 1.20.5 names it by a
 * PackLocationInfo (id, title, pack source, known-pack identity). The flag travels as the location's
 * source: BUILT_IN for a pack 1.20.4 called builtin (1.20.5's own vanilla and feature packs come with
 * BUILT_IN / FEATURE). A supplier keeps the flag it was built with (recorded here; its openPrimary /
 * openFull apply it, CbRestPackSuppliersMixin).
 *
 * Not carried over: 1.20.4's one reader of the flag, RegistryDataLoader (builtin: stable lifecycle),
 * reads the known-pack identity in 1.20.5, which no 1.20.4 pack has -- registry entries a 1.20.4
 * builtin pack loads count as experimental.
 */
public final class CbRestPacks {

    /** a pack class's own 1.20.4 isBuiltin(), when its class declares one (1.20.4's default was false) */
    private static final ClassValue<java.lang.invoke.MethodHandle> IS_BUILTIN = new ClassValue<>() {
        @Override
        protected java.lang.invoke.MethodHandle computeValue(Class<?> type) {
            for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
                try {
                    java.lang.reflect.Method m = c.getDeclaredMethod("method_45178");
                    if (m.getReturnType() == boolean.class && !java.lang.reflect.Modifier.isStatic(m.getModifiers())) {
                        m.setAccessible(true);
                        return java.lang.invoke.MethodHandles.lookup().unreflect(m);
                    }
                } catch (NoSuchMethodException | IllegalAccessException e) {
                    // not declared here: look further up
                }
            }
            return null;
        }
    };

    /** pack suppliers 1.20.4 built as builtin (the flag 1.20.5's suppliers no longer take) */
    private static final java.util.Map<Object, Boolean> BUILTIN_SUPPLIERS =
        java.util.Collections.synchronizedMap(new java.util.WeakHashMap<>());

    private static boolean own(net.minecraft.class_3262 pack, boolean fallback) {
        java.lang.invoke.MethodHandle m = IS_BUILTIN.get(pack.getClass());
        if (m == null) {
            return fallback;
        }
        try {
            return (boolean) m.invoke(pack);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    /** whether a 1.20.5 location stands for what 1.20.4 called a builtin pack */
    public static boolean builtin(net.minecraft.class_9224 location) {
        net.minecraft.class_5352 source = location.comp_2331();
        return source == net.minecraft.class_5352.field_25348 || source == net.minecraft.class_5352.field_40048;
    }

    /** a 1.20.4 pack id and builtin flag as a 1.20.5 location: the id as plain-text title, no known pack */
    public static net.minecraft.class_9224 locationOf(String id, boolean builtin) {
        return new net.minecraft.class_9224(id, net.minecraft.class_2561.method_43470(id),
            builtin ? net.minecraft.class_5352.field_25348 : net.minecraft.class_5352.field_25347, java.util.Optional.empty());
    }

    /** a 1.20.4 pack id alone (a supplier's open call): not builtin unless the supplier was (it applies its flag) */
    public static net.minecraft.class_9224 locationOf(String id) {
        return locationOf(id, false);
    }

    /** the location 1.20.4's Pack factories made of an id, title and pack source */
    public static net.minecraft.class_9224 location(String id, net.minecraft.class_2561 title, net.minecraft.class_5352 source) {
        return new net.minecraft.class_9224(id, title, source, java.util.Optional.empty());
    }

    /** 1.20.4's required / default position, as 1.20.5's selection config (1.20.4's readMetaAndCreate: not fixed) */
    public static net.minecraft.class_9225 selection(boolean required, net.minecraft.class_3288.class_3289 position) {
        return new net.minecraft.class_9225(required, position, false);
    }

    /** 1.20.4 VanillaPackResourcesBuilder.build(): the pack 1.20.4 named "vanilla", builtin */
    public static net.minecraft.class_9224 vanillaLocation() {
        return locationOf("vanilla", true);
    }

    /**
     * 1.20.4 Pack.create(id, title, required, supplier, info, position, fixedPosition, source): 1.20.5's
     * Pack of that location, metadata and selection
     */
    public static net.minecraft.class_3288 method_14456(String id, net.minecraft.class_2561 title, boolean required,
                                                       net.minecraft.class_3288.class_7680 resources,
                                                       net.minecraft.class_3288.class_7679 info,
                                                       net.minecraft.class_3288.class_3289 position, boolean fixedPosition,
                                                       net.minecraft.class_5352 source) {
        return new net.minecraft.class_3288(location(id, title, source), resources, info,
            new net.minecraft.class_9225(required, position, fixedPosition));
    }

    /**
     * 1.20.4 PackResources.isBuiltin() called on any pack: the pack's own 1.20.4 method where its class has
     * one (a mod's pack), else what its location says
     */
    public static boolean method_45178(net.minecraft.class_3262 pack) {
        java.lang.invoke.MethodHandle m = IS_BUILTIN.get(pack.getClass());
        return m != null ? own(pack, false) : builtin(pack.method_56926());
    }

    /** 1.20.4 PackResources.super.isBuiltin(): the interface's default, false */
    public static boolean isBuiltinDefault(net.minecraft.class_3262 pack) {
        return false;
    }

    /**
     * the location a 1.20.5 pack class itself holds (its field, not a virtual location(): a mod's
     * isBuiltin override, hooked into location(), reads it through its super call)
     */
    public interface Located {
        net.minecraft.class_9224 cbRest$location();
    }

    /** 1.20.4 PackResources.isBuiltin(), the shape a mod's pack class overrides */
    public interface LegacyBuiltin {
        boolean method_45178();
    }

    /** 1.20.4 AbstractPackResources.isBuiltin() as a super call: the flag its constructor took (the location's) */
    public static boolean superIsBuiltin(net.minecraft.class_3255 pack) {
        return builtin(((Located) pack).cbRest$location());
    }

    /** 1.20.4 CompositePackResources.isBuiltin() as a super call: its primary pack's (the primary's location) */
    public static boolean superIsBuiltin(net.minecraft.class_8614 pack) {
        return builtin(((Located) pack).cbRest$location());
    }

    /**
     * location() of a pack class overriding 1.20.4's isBuiltin() (overrideHooks bridge): the location its
     * 1.20.5 class holds -- or, for a 1.20.4 class implementing PackResources itself, its packId() -- with
     * the source its own isBuiltin() answers
     */
    public static net.minecraft.class_9224 locationOfOwnBuiltin(LegacyBuiltin pack) {
        boolean own = pack.method_45178();
        net.minecraft.class_9224 base = pack instanceof Located l ? l.cbRest$location() : null;
        if (base == null) {
            return locationOf(((net.minecraft.class_3262) pack).method_14409(), own);
        }
        if (own == builtin(base)) {
            return base;
        }
        return new net.minecraft.class_9224(base.comp_2329(), base.comp_2330(),
            own ? net.minecraft.class_5352.field_25348 : net.minecraft.class_5352.field_25347, base.comp_2332());
    }

    /** a 1.20.5 location's id, the 1.20.4 shape's String (a mod supplier's open call) */
    public static String id(net.minecraft.class_9224 location) {
        return location == null ? null : location.comp_2329();
    }

    /** 1.20.4 Resource.isBuiltin(): its source pack's */
    public static boolean method_45305(net.minecraft.class_3298 resource) {
        return method_45178(resource.method_45304());
    }

    /**
     * the location of a 1.20.4 pack class (abstractFill for 1.20.5's abstract location()): its own
     * packId() as id and as plain-text title, BUILT_IN when its own isBuiltin() says so (DEFAULT
     * otherwise), no known pack
     */
    public static net.minecraft.class_9224 location(net.minecraft.class_3262 pack) {
        return locationOf(pack.method_14409(), own(pack, false));
    }

    /** a supplier 1.20.4 built with the builtin flag (the constructor's post step) */
    public static void supplierBuiltin(net.minecraft.class_3258.class_8615 supplier, boolean builtin) {
        if (builtin) {
            BUILTIN_SUPPLIERS.put(supplier, Boolean.TRUE);
        }
    }

    /** the same for a directory pack supplier */
    public static void supplierBuiltin(net.minecraft.class_3259.class_8619 supplier, boolean builtin) {
        if (builtin) {
            BUILTIN_SUPPLIERS.put(supplier, Boolean.TRUE);
        }
    }

    /** CbRestPackSuppliersMixin: the location a supplier opens with, builtin when 1.20.4 built the supplier so */
    public static net.minecraft.class_9224 withSupplierFlag(Object supplier, net.minecraft.class_9224 location) {
        if (location == null || builtin(location) || !BUILTIN_SUPPLIERS.containsKey(supplier)) {
            return location;
        }
        return new net.minecraft.class_9224(location.comp_2329(), location.comp_2330(), net.minecraft.class_5352.field_25348,
            location.comp_2332());
    }

    /**
     * 1.20.4 FolderRepositorySource.discoverPacks(dir, validator, builtin, output): 1.20.5 builds the
     * suppliers without the flag; each one handed to the output is recorded with it first
     */
    public static java.util.function.BiConsumer<java.nio.file.Path, net.minecraft.class_3288.class_7680> flagSuppliers(
            boolean builtin, java.util.function.BiConsumer<java.nio.file.Path, net.minecraft.class_3288.class_7680> output) {
        if (!builtin) {
            return output;
        }
        return (path, supplier) -> {
            BUILTIN_SUPPLIERS.put(supplier, Boolean.TRUE);
            output.accept(path, supplier);
        };
    }

    private CbRestPacks() {
    }
}
