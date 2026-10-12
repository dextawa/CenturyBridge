package top.dext.centurybridge.step.v1_20_5.rt;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import top.dext.centurybridge.step.v1_20_5.facade.class_2582;

/**
 * 1.20.4's banner patterns on 1.20.5.
 *
 * <p>1.20.4 kept banner patterns in a static registry, BuiltInRegistries.BANNER_PATTERN: objects with a
 * hash name (the pattern's code in banner NBT) that mods built, extended and registered at
 * initialization, textured and named after their registry key (location(key, ..),
 * "block.minecraft.banner." + the key's short language key). 1.20.5 made them a datapack registry of
 * records (asset id, translation key) and banner NBT a component of layers naming the registry id.
 *
 * <p>Mod code keeps 1.20.4's world: the 1.20.4 class is facade/class_2582 there (classRenames), and
 * BuiltInRegistries.BANNER_PATTERN is {@link #registry()} -- a registry of those, holding 1.20.4's 41
 * vanilla patterns under their 1.20.4 codes and every pattern a mod registers. The game keeps 1.20.5's:
 * when a world's datapack registry loads, each pattern registered here that the datapacks did not
 * define is added to it as the record its key gives -- asset id the key's location, translation key
 * "block.minecraft.banner." + its short language key, which are 1.20.4's texture paths
 * (entity/banner/, entity/shield/) and names exactly -- and reaches clients with the other entries the
 * server sends them. Values cross between the two by key only: a record handed to 1.20.4 code becomes
 * the facade registered under its key (one is made, with the id as its hash name, for a pattern only a
 * datapack defines), a facade handed to the game the world's record of its key. The facade registry
 * mirrors the world's tags (CbBlockBannerTagsMixin).
 */
public final class CbBlockBanner {

    /** 1.20.4's vanilla patterns, in the order its BannerPatterns.bootstrap registered them */
    @SuppressWarnings("unchecked")
    private static final List<net.minecraft.class_5321<net.minecraft.class_2582>> VANILLA = List.of(
        net.minecraft.class_7446.field_39151, net.minecraft.class_7446.field_39152, net.minecraft.class_7446.field_39153,
        net.minecraft.class_7446.field_39154, net.minecraft.class_7446.field_39155, net.minecraft.class_7446.field_39156,
        net.minecraft.class_7446.field_39157, net.minecraft.class_7446.field_39158, net.minecraft.class_7446.field_39159,
        net.minecraft.class_7446.field_39160, net.minecraft.class_7446.field_39161, net.minecraft.class_7446.field_39162,
        net.minecraft.class_7446.field_39163, net.minecraft.class_7446.field_39164, net.minecraft.class_7446.field_39165,
        net.minecraft.class_7446.field_39166, net.minecraft.class_7446.field_39167, net.minecraft.class_7446.field_39168,
        net.minecraft.class_7446.field_39169, net.minecraft.class_7446.field_39170, net.minecraft.class_7446.field_39171,
        net.minecraft.class_7446.field_39172, net.minecraft.class_7446.field_39173, net.minecraft.class_7446.field_39174,
        net.minecraft.class_7446.field_39175, net.minecraft.class_7446.field_39176, net.minecraft.class_7446.field_39136,
        net.minecraft.class_7446.field_39137, net.minecraft.class_7446.field_39138, net.minecraft.class_7446.field_39139,
        net.minecraft.class_7446.field_39140, net.minecraft.class_7446.field_39141, net.minecraft.class_7446.field_39142,
        net.minecraft.class_7446.field_39143, net.minecraft.class_7446.field_39144, net.minecraft.class_7446.field_39145,
        net.minecraft.class_7446.field_39146, net.minecraft.class_7446.field_39147, net.minecraft.class_7446.field_39148,
        net.minecraft.class_7446.field_39149, net.minecraft.class_7446.field_39150);

    /** a facade made for a pattern no registry knows (built and never registered by a mod): no texture, no name */
    private static final net.minecraft.class_2960 UNREGISTERED = new net.minecraft.class_2960("centurybridge", "unregistered_banner_pattern");

    private static volatile net.minecraft.class_2370<class_2582> registry;

    /** BuiltInRegistries.BANNER_PATTERN: 1.20.4's registry of patterns (see the class comment) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2378 field_41165() {
        return registry();
    }

    /** the registry of 1.20.4's patterns; made with 1.20.4's vanilla ones on first use */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2370<class_2582> registry() {
        net.minecraft.class_2370<class_2582> r = registry;
        if (r == null) {
            synchronized (CbBlockBanner.class) {
                r = registry;
                if (r == null) {
                    r = new net.minecraft.class_2370<>((net.minecraft.class_5321) net.minecraft.class_7924.field_41252,
                        com.mojang.serialization.Lifecycle.stable());
                    for (net.minecraft.class_5321 key : VANILLA) {
                        r.method_10272(key, new class_2582(codeOf(key)), net.minecraft.class_9248.field_49136);
                    }
                    registry = r;
                }
            }
        }
        return r;
    }

    /** a pattern's 1.20.4 code by its id: 1.20.4's for a vanilla pattern, else the id itself (CbBannerPatterns) */
    private static String codeOf(net.minecraft.class_5321<?> key) {
        String id = key.method_29177().toString();
        String code = CbBannerPatterns.CODES.get(id);
        return code != null ? code : id;
    }

    /** the world's banner pattern registry, when one is loaded */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static Optional<net.minecraft.class_2378<net.minecraft.class_2582>> live() {
        return (Optional) CbRegistries.currentAccess().method_33310(net.minecraft.class_7924.field_41252);
    }

    /** the record 1.20.4's key gives a pattern: what 1.20.5's BannerPatterns.register makes of a key */
    public static net.minecraft.class_2582 record(net.minecraft.class_5321<?> key) {
        net.minecraft.class_2960 id = key.method_29177();
        return new net.minecraft.class_2582(id, "block.minecraft.banner." + id.method_43903());
    }

    // ---------------------------------------------------------------- the game's registry

    /**
     * A world's banner pattern registry has loaded its datapacks (CbBlockBannerLoaderMixin): on the
     * server each pattern registered here that the datapacks did not define is added as the record its
     * key gives; on both sides each pattern only a datapack defines gets its facade.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void loaded(net.minecraft.class_2385 live, boolean fromDatapacks) {
        if (live.method_30517() != net.minecraft.class_7924.field_41252) {
            return;
        }
        net.minecraft.class_2370<class_2582> reg = registry();
        synchronized (CbBlockBanner.class) {
            if (fromDatapacks) {
                for (Object o : reg.method_42021()) {
                    net.minecraft.class_5321 key = (net.minecraft.class_5321) o;
                    if (!live.method_35842(key)) {
                        live.method_10272(key, record(key), net.minecraft.class_9248.field_49136);
                    }
                }
            }
            for (Object o : live.method_42021()) {
                net.minecraft.class_5321 key = (net.minecraft.class_5321) o;
                if (!reg.method_35842(key)) {
                    reg.method_10272(key, new class_2582(codeOf(key)), net.minecraft.class_9248.field_49136);
                }
            }
        }
    }

    /** the world's tags were bound (CbBlockBannerTagsMixin): the same tags, by key, on the registry of 1.20.4's patterns */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void tagsBound(net.minecraft.class_2378 bound, Map<net.minecraft.class_6862, List<net.minecraft.class_6880>> tags) {
        net.minecraft.class_2370<class_2582> reg = registry();
        if (bound == reg || bound.method_30517() != net.minecraft.class_7924.field_41252) {
            return;
        }
        Map out = new java.util.HashMap();
        tags.forEach((tag, holders) -> {
            List mapped = new ArrayList();
            for (net.minecraft.class_6880 h : holders) {
                h.method_40230().ifPresent(k -> mapped.add(facadeHolderOf((net.minecraft.class_5321) k)));
            }
            out.put(tag, mapped);
        });
        reg.method_40257(out);
    }

    // ---------------------------------------------------------------- crossing by key

    /** the facade registered under a key, made (the id as its hash name) for a key only the world knows */
    @SuppressWarnings({"rawtypes", "unchecked"})
    static net.minecraft.class_6880<class_2582> facadeHolderOf(net.minecraft.class_5321 key) {
        net.minecraft.class_2370<class_2582> reg = registry();
        Optional<net.minecraft.class_6880.class_6883<class_2582>> h = reg.method_40264(key);
        if (h.isPresent()) {
            return h.get();
        }
        synchronized (CbBlockBanner.class) {
            h = reg.method_40264(key);
            return h.isPresent() ? h.get() : reg.method_10272(key, new class_2582(codeOf(key)), net.minecraft.class_9248.field_49136);
        }
    }

    /** a record holder handed to 1.20.4 code: the facade of its key (a direct one for a pattern with no key) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880<class_2582> facadeHolder(net.minecraft.class_6880 holder) {
        Optional<net.minecraft.class_5321> key = holder.method_40230();
        if (key.isPresent()) {
            return facadeHolderOf(key.get());
        }
        Object v = holder.comp_349();
        if (v instanceof class_2582) {
            return holder;
        }
        net.minecraft.class_2582 rec = (net.minecraft.class_2582) v;
        return net.minecraft.class_6880.method_40223(new class_2582(rec.comp_2456().toString()));
    }

    /** a facade holder handed to the game: the world's record of its key, else the record the key gives */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880<net.minecraft.class_2582> recordHolder(net.minecraft.class_6880 holder) {
        Object v = holder.comp_349();
        if (v instanceof net.minecraft.class_2582) {
            return holder;
        }
        Optional<net.minecraft.class_5321> key = holder.method_40230();
        if (key.isEmpty()) {
            key = (Optional) registry().method_29113((class_2582) v);
        }
        if (key.isEmpty()) {
            return net.minecraft.class_6880.method_40223(new net.minecraft.class_2582(UNREGISTERED, "block.minecraft.banner.centurybridge.unregistered"));
        }
        Optional<net.minecraft.class_2378<net.minecraft.class_2582>> live = live();
        if (live.isPresent()) {
            Optional<net.minecraft.class_6880.class_6883<net.minecraft.class_2582>> h = live.get().method_40264(key.get());
            if (h.isPresent()) {
                return h.get();
            }
        }
        return net.minecraft.class_6880.method_40223(record(key.get()));
    }

    /** a pattern's 1.20.4 hash name: its facade's */
    public static String hashOf(net.minecraft.class_6880<net.minecraft.class_2582> holder) {
        return facadeHolder(holder).comp_349().field_11824;
    }

    /** a pattern's 1.20.4 hash name by its id (vanilla's code, a mod's own hash name, else the id): for 1.20.4 banner NBT */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String hashOf(String id) {
        net.minecraft.class_2960 rl = net.minecraft.class_2960.method_12829(id);
        if (rl == null) {
            return id;
        }
        return facadeHolderOf(net.minecraft.class_5321.method_29179((net.minecraft.class_5321) net.minecraft.class_7924.field_41252, rl))
            .comp_349().field_11824;
    }

    /** the id of the pattern a 1.20.4 hash name names, null when none: for 1.20.4 banner NBT */
    public static String idOfHash(String hash) {
        net.minecraft.class_6880<class_2582> h = byHash(hash);
        return h == null ? null : h.method_40230().map(k -> k.method_29177().toString()).orElse(null);
    }

    /**
     * BannerPattern.byHash(hash): 1.20.4's body (the registered pattern whose hash name it is), then --
     * for a pattern only the world's datapacks define, whose 1.20.4-era NBT carries its id -- the id.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_6880<class_2582> byHash(String hash) {
        net.minecraft.class_6880<class_2582> found = registry().method_40270()
            .filter(h -> h.comp_349().field_11824.equals(hash)).findAny().orElse(null);
        if (found != null) {
            return found;
        }
        net.minecraft.class_2960 rl = net.minecraft.class_2960.method_12829(hash);
        Optional<net.minecraft.class_2378<net.minecraft.class_2582>> live = live();
        if (rl == null || live.isEmpty()) {
            return null;
        }
        net.minecraft.class_5321 key = net.minecraft.class_5321.method_29179((net.minecraft.class_5321) net.minecraft.class_7924.field_41252, rl);
        return live.get().method_35842(key) ? facadeHolderOf(key) : null;
    }

    /** BannerPatterns.bootstrap(registry): 1.20.4's vanilla patterns registered into it, the last one answered */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class_2582 method_43721(net.minecraft.class_2378 registry) {
        class_2582 last = null;
        for (net.minecraft.class_5321 key : VANILLA) {
            last = (class_2582) net.minecraft.class_2378.method_39197(registry, key, new class_2582(codeOf(key)));
        }
        return last;
    }

    // ---------------------------------------------------------------- the 1.20.4 registry through the world's registry access

    /** RegistryAccess.registryOrThrow(key): 1.20.4 code asking for the banner pattern registry gets 1.20.4's */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2378 method_30530(net.minecraft.class_5455 access, net.minecraft.class_5321 key) {
        return key == net.minecraft.class_7924.field_41252 ? registry() : access.method_30530(key);
    }

    /** RegistryAccess.registry(key) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional method_33310(net.minecraft.class_5455 access, net.minecraft.class_5321 key) {
        return key == net.minecraft.class_7924.field_41252 ? Optional.of(registry()) : access.method_33310(key);
    }

    /** HolderLookup.Provider.lookupOrThrow(key) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_7225.class_7226 method_46762(net.minecraft.class_7225.class_7874 provider, net.minecraft.class_5321 key) {
        return key == net.minecraft.class_7924.field_41252 ? registry().method_46771() : provider.method_46762(key);
    }

    /** HolderLookup.Provider.lookup(key) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Optional method_46759(net.minecraft.class_7225.class_7874 provider, net.minecraft.class_5321 key) {
        return key == net.minecraft.class_7924.field_41252 ? Optional.of(registry().method_46771()) : provider.method_46759(key);
    }

    /** RegistryAccess.super.registryOrThrow(key) from a mod's own registry access: the interface's default body, the banner key as above */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_2378 superRegistryOrThrow(net.minecraft.class_5455 access, net.minecraft.class_5321 key) {
        if (key == net.minecraft.class_7924.field_41252) {
            return registry();
        }
        return (net.minecraft.class_2378) ((Optional<Object>) access.method_33310(key))
            .orElseThrow(() -> new IllegalStateException("Missing registry: " + key));
    }

    /** HolderLookup.Provider.super.lookupOrThrow(key), likewise */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_7225.class_7226 superLookupOrThrow(net.minecraft.class_7225.class_7874 provider, net.minecraft.class_5321 key) {
        if (key == net.minecraft.class_7924.field_41252) {
            return registry().method_46771();
        }
        return (net.minecraft.class_7225.class_7226) ((Optional<Object>) provider.method_46759(key))
            .orElseThrow(() -> new IllegalStateException("Registry " + key.method_29177() + " not found"));
    }

    // ---------------------------------------------------------------- BannerBlockEntity

    /** BannerBlockEntity's patterns and base color (CbBlockBannerAccessor) */
    public interface Banner {
        net.minecraft.class_9307 cb$patterns();

        net.minecraft.class_1767 cb$baseColor();

        void cb$setBaseColor(net.minecraft.class_1767 color);
    }

    /** 1.20.4's fromItem(stack) a mod banner class overrides */
    public interface LegacyFromItem {
        void method_38992(net.minecraft.class_1799 stack);
    }

    /** 1.20.4's getPatterns() a mod banner class overrides */
    public interface LegacyPatterns {
        List<Pair<net.minecraft.class_6880<class_2582>, net.minecraft.class_1767>> method_10911();
    }

    /** the stack's layers, null without any (1.20.4: no Patterns list in its block entity tag) */
    private static net.minecraft.class_9307 layersOf(net.minecraft.class_1799 stack) {
        net.minecraft.class_9307 layers = stack.method_57824(net.minecraft.class_9334.field_49619);
        return layers == null || layers.comp_2428().isEmpty() ? null : layers;
    }

    /** getItemPatterns(stack): 1.20.4's NBT list of the stack's patterns, {Pattern: hash name, Color: dye id}; null without any */
    public static net.minecraft.class_2499 method_24281(net.minecraft.class_1799 stack) {
        net.minecraft.class_9307 layers = layersOf(stack);
        if (layers == null) {
            return null;
        }
        net.minecraft.class_2499 list = new net.minecraft.class_2499();
        for (net.minecraft.class_9307.class_9308 layer : layers.comp_2428()) {
            net.minecraft.class_2487 tag = new net.minecraft.class_2487();
            tag.method_10582("Pattern", hashOf(layer.comp_2429()));
            tag.method_10569("Color", layer.comp_2430().method_7789());
            list.add(tag);
        }
        return list;
    }

    /** getPatternCount(stack) */
    public static int method_10910(net.minecraft.class_1799 stack) {
        net.minecraft.class_9307 layers = layersOf(stack);
        return layers == null ? 0 : layers.comp_2428().size();
    }

    /** removeLastPattern(stack): the stack's last pattern taken off */
    public static void method_10905(net.minecraft.class_1799 stack) {
        net.minecraft.class_9307 layers = layersOf(stack);
        if (layers != null) {
            stack.method_57379(net.minecraft.class_9334.field_49619, layers.method_57571());
        }
    }

    /** createPatterns(base, list): 1.20.4's body -- the base in its color, then each listed pattern the registry knows, in its color */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List method_24280(net.minecraft.class_1767 base, net.minecraft.class_2499 list) {
        List out = new ArrayList();
        out.add(Pair.of(registry().method_40290((net.minecraft.class_5321) net.minecraft.class_7446.field_39151), base));
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                net.minecraft.class_2487 tag = list.method_10602(i);
                net.minecraft.class_6880<class_2582> pattern = byHash(tag.method_10558("Pattern"));
                if (pattern != null) {
                    out.add(Pair.of(pattern, net.minecraft.class_1767.method_7791(tag.method_10550("Color"))));
                }
            }
        }
        return out;
    }

    /** getPatterns() called on any banner */
    @SuppressWarnings({"rawtypes"})
    public static List method_10911(net.minecraft.class_2573 self) {
        return self instanceof LegacyPatterns legacy ? legacy.method_10911() : superGetPatterns(self);
    }

    /** 1.20.4's getPatterns body (a mod's super call): the base in the banner's color, then its layers */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List superGetPatterns(net.minecraft.class_2573 self) {
        Banner b = (Banner) self;
        List out = new ArrayList();
        out.add(Pair.of(registry().method_40290((net.minecraft.class_5321) net.minecraft.class_7446.field_39151), b.cb$baseColor()));
        for (net.minecraft.class_9307.class_9308 layer : b.cb$patterns().comp_2428()) {
            out.add(Pair.of(facadeHolder(layer.comp_2429()), layer.comp_2430()));
        }
        return out;
    }

    /**
     * 1.20.5's getPatterns() asked of a banner overriding 1.20.4's: the override's list as layers. 1.20.4's
     * list began with the base pattern in the banner's color, which 1.20.5 draws from the base color
     * itself; such a first entry is left out, any other first entry is a layer like the rest.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9307 getPatterns(LegacyPatterns self) {
        List<Pair<net.minecraft.class_6880<class_2582>, net.minecraft.class_1767>> list = self.method_10911();
        net.minecraft.class_9307.class_3750 b = new net.minecraft.class_9307.class_3750();
        for (int i = 0; i < list.size(); i++) {
            Pair<net.minecraft.class_6880<class_2582>, net.minecraft.class_1767> p = list.get(i);
            if (i == 0 && p.getSecond() == ((Banner) self).cb$baseColor()
                    && p.getFirst().method_40230().map(k -> k == (Object) net.minecraft.class_7446.field_39151).orElse(false)) {
                continue;
            }
            b.method_16376(recordHolder(p.getFirst()), p.getSecond());
        }
        return b.method_57573();
    }

    /** layers 1.20.4 code built as (pattern, color) pairs, for the game (a renderer, a component): the base pair left out as above */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_9307 layers(List pairs, net.minecraft.class_1767 base) {
        net.minecraft.class_9307.class_3750 b = new net.minecraft.class_9307.class_3750();
        for (int i = 0; i < pairs.size(); i++) {
            Pair p = (Pair) pairs.get(i);
            net.minecraft.class_6880 h = (net.minecraft.class_6880) p.getFirst();
            if (i == 0 && p.getSecond() == base
                    && ((Optional<Object>) h.method_40230()).map(k -> k == (Object) net.minecraft.class_7446.field_39151).orElse(false)) {
                continue;
            }
            b.method_16376(recordHolder(h), (net.minecraft.class_1767) p.getSecond());
        }
        return b.method_57573();
    }

    /** fromItem(stack) called on any banner */
    public static void method_38992(net.minecraft.class_2573 self, net.minecraft.class_1799 stack) {
        if (self instanceof LegacyFromItem legacy) {
            legacy.method_38992(stack);
        } else {
            superFromItem(self, stack);
        }
    }

    /**
     * 1.20.4's fromItem(stack) body (a mod's super call): the stack's patterns and custom name taken as
     * the banner's -- 1.20.5 applies them from the stack's components (applyComponents, so the
     * placement hook on applyComponentsFromItemStack does not run again).
     */
    public static void superFromItem(net.minecraft.class_2573 self, net.minecraft.class_1799 stack) {
        self.method_58685(stack.method_58658(), stack.method_57380());
    }

    /** fromItem(stack, color) of a banner overriding 1.20.4's fromItem(stack) (CbBlockBannerMixin): 1.20.4's body, the color then the override */
    public static void fromItemWithColor(LegacyFromItem self, net.minecraft.class_1799 stack, net.minecraft.class_1767 color) {
        ((Banner) self).cb$setBaseColor(color);
        self.method_38992(stack);
    }

    // ---------------------------------------------------------------- other vanilla APIs carrying patterns

    /** LoomMenu's selectable patterns (CbBlockLoomAccessor) */
    public interface Loom {
        List<net.minecraft.class_6880<net.minecraft.class_2582>> cb$selectablePatterns();
    }

    /** LoomMenu.getSelectablePatterns(): the loom's patterns as 1.20.4's */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List method_43706(net.minecraft.class_1726 menu) {
        return facadeHolders(menu.method_43706());
    }

    /** a mod loom's super.getSelectablePatterns(): the field, as 1.20.4's (no virtual call back into the override) */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static List superSelectablePatterns(net.minecraft.class_1726 menu) {
        return facadeHolders(((Loom) menu).cb$selectablePatterns());
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List facadeHolders(List holders) {
        List out = new ArrayList();
        for (Object h : holders) {
            out.add(facadeHolder((net.minecraft.class_6880) h));
        }
        return out;
    }

    /** SetBannerPatternFunction.Builder.addPattern(holder, color): the world's record of the pattern */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_5592.class_6157 method_35533(net.minecraft.class_5592.class_6157 builder, net.minecraft.class_6880 pattern,
            net.minecraft.class_1767 color) {
        return builder.method_35533(recordHolder(pattern), color);
    }

    /** the color of a 1.20.4 render list's leading base pair (its first entry, the base pattern), null when it has none */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static net.minecraft.class_1767 baseOf(List pairs) {
        if (pairs.isEmpty()) {
            return null;
        }
        Pair p = (Pair) pairs.get(0);
        net.minecraft.class_6880 h = (net.minecraft.class_6880) p.getFirst();
        return ((Optional<Object>) h.method_40230()).map(k -> k == (Object) net.minecraft.class_7446.field_39151).orElse(false)
            ? (net.minecraft.class_1767) p.getSecond() : null;
    }

    private CbBlockBanner() {
    }
}
