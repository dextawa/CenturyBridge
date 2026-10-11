package top.dext.centurybridge.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * An ordered run of frozen boundary segments. Resolution walks the segments in
 * release order, so every verdict carries attribution: WHICH boundary killed
 * or changed the symbol. Within the intermediary era identity is name
 * equality; the Mojmap frontier segment (1.21.11 -> 26.1) adds rename tables.
 */
public final class Chain {
    /** release order of the intermediary era (frontier + 26.x appended later) */
    public static final List<String> RELEASES = List.of(
        "1.20.1", "1.20.2", "1.20.3", "1.20.4", "1.20.5", "1.20.6",
        "1.21", "1.21.1", "1.21.2", "1.21.3", "1.21.4", "1.21.5", "1.21.6",
        "1.21.7", "1.21.8", "1.21.9", "1.21.10", "1.21.11",
        // year-based era: ships unobfuscated, intermediary is dead -- the
        // 1.21.11 -> 26.1 segment is a whole-namespace wall (intermediary ->
        // mojang), not just a stub diff
        "26.1", "26.1.1", "26.1.2", "26.2");

    private static final Pattern METHOD_PAT = Pattern.compile("method_\\d+");
    private static final Pattern FIELD_PAT = Pattern.compile("field_\\d+");
    private static final Pattern CLASS_PAT = Pattern.compile("net/minecraft/class_\\d+");

    public enum Level { OK, L2, L3 }

    /** a verdict plus the boundary that caused it */
    public record Issue(Level level, String boundary) {
        public static final Issue OK = new Issue(Level.OK, null);

        public Issue worst(Issue other) {
            return other.level.ordinal() > this.level.ordinal() ? other : this;
        }
    }

    public final String from;
    public final String to;
    public final List<Segment> segments = new ArrayList<>();
    /** "owner.name(desc)" -> runtime class carrying a same-name static replacement */
    public final java.util.Map<String, String> staticRedirects = new java.util.HashMap<>();
    /** instance calls redirected to a static (receiver becomes arg 0) -- for interface targets Mixin cannot touch */
    public final java.util.Map<String, String> instanceRedirects = new java.util.HashMap<>();
    /** GETSTATIC field reads redirected to a same-name field on a runtime class */
    public final java.util.Map<String, String> fieldRedirects = new java.util.HashMap<>();
    /** GETSTATIC field reads redirected to a zero-arg getter method (name = field name)
     *  on a runtime class -- for holderified fields whose value must be unwrapped lazily
     *  (a static-field target would evaluate at Statics class-init, before the registry binds) */
    public final java.util.Map<String, String> fieldGetters = new java.util.HashMap<>();
    /** dead vanilla classes renamed to facade classes shipped in the runtime bridge */
    public final java.util.Map<String, String> classRenames = new java.util.HashMap<>();
    /**
     * super/implements-only renames: a mod class that extends/implements the key
     * (a vanilla type now final/record, or an interface that became a class) has
     * ONLY its superName/interfaces and its super-invocations (INVOKESPECIAL to
     * the old type) rewritten to the value (a CB facade). Unlike classRenames,
     * every OTHER reference to the key in the class is left untouched -- so a
     * class that both subclasses the dead type AND reads a vanilla constant of
     * that type (the type-split that defeats a global classRename) keeps the
     * constant access on the real type while its own hierarchy points at the
     * facade. The facade must mirror the full method surface the subclass calls.
     */
    public final java.util.Map<String, String> superRenames = new java.util.HashMap<>();
    /** instance fields renamed in place (resolution reaches relocated/renamed fields via hierarchy) */
    public final java.util.Map<String, String> fieldRenames = new java.util.HashMap<>();
    /** methods renamed in place (relocated-to-super methods resolve via hierarchy) */
    public final java.util.Map<String, String> methodRenames = new java.util.HashMap<>();
    /**
     * GETFIELD "owner.name:desc" -> "runtimeClass[#member]": an instance field
     * read rewritten to a static getter taking the receiver (record components
     * are private fields; EnchantmentInstance.enchantment/level, Minecraft.screen).
     */
    public final java.util.Map<String, String> instanceFieldGetters = new java.util.HashMap<>();
    /**
     * INSTANCEOF "type" -> "runtimeClass#predicate": a type test against a class
     * that no longer describes the runtime object (EnchantedBookItem is a plain
     * Item since 1.21.2) becomes INVOKESTATIC predicate(Object)Z.
     */
    public final java.util.Map<String, String> instanceOfRedirects = new java.util.HashMap<>();
    /** examined-and-classified damage (quirk tier / structural / etc): "owner" or "owner.name" -> reason */
    public final java.util.Map<String, String> ledger = new java.util.HashMap<>();


    /** A cover/redirect recorded on any ancestor applies to this call site too. */
    public boolean coveredAnywhere(String owner, String nameDesc) {
        for (String o : selfAndSupers(owner)) {
            if (shimCovers.contains(o + "." + nameDesc)) {
                return true;
            }
        }
        return false;
    }

    /** Look a member up in a redirect map, walking the hierarchy. */
    public String redirectAnywhere(java.util.Map<String, String> map,
                                   String owner, String nameDesc) {
        String home = redirectHome(map, owner, nameDesc);
        return home == null ? null : map.get(home + "." + nameDesc);
    }

    /** The class (owner or an ancestor) the redirect map keys the member on; null when unwired. */
    public String redirectHome(java.util.Map<String, String> map, String owner, String nameDesc) {
        for (String o : selfAndSupers(owner)) {
            if (map.containsKey(o + "." + nameDesc)) {
                return o;
            }
        }
        return null;
    }

    /**
     * Home for an instance call, in the JVM's method-resolution order: the class and
     * its superclasses, then their superinterfaces. A member keyed on an interface
     * serves a call made through an implementing class (ResultContainer implements
     * RecipeHolder) exactly as the JVM would resolve it; the bridge static takes the
     * receiver as that interface, which the implementing instance satisfies.
     */
    public String instanceRedirectHome(java.util.Map<String, String> map, String owner, String nameDesc) {
        String home = redirectHome(map, owner, nameDesc);
        if (home != null) {
            return home;
        }
        java.util.Deque<String> todo = new java.util.ArrayDeque<>();
        for (String o : selfAndSupers(owner)) {
            todo.addAll(interfacesOf.getOrDefault(o, java.util.List.of()));
        }
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String i = todo.poll();
            if (!seen.add(i)) {
                continue;
            }
            if (map.containsKey(i + "." + nameDesc)) {
                return i;
            }
            todo.addAll(interfacesOf.getOrDefault(i, java.util.List.of()));
        }
        return null;
    }

    public String ledgerReason(String owner, String name) {
        return ledgerReason(owner, name, null);
    }

    /**
     * The reason a dead member is accounted for, or null. A key may name the exact
     * shape ("owner.name(desc)" / "owner.name:desc") or every member of that name
     * ("owner.name"); the exact one wins, so constructors -- all named <init> --
     * can be accounted one descriptor at a time.
     */
    public String ledgerReason(String owner, String name, String desc) {
        String r = null;
        if (name != null) {
            // the entry may be recorded against any class in the hierarchy, since
            // that is where the member is declared
            String exact = desc == null ? null : desc.startsWith("(") ? name + desc : name + ":" + desc;
            for (String o : selfAndSupers(owner)) {
                r = exact == null ? null : ledger.get(o + "." + exact);
                if (r == null) {
                    r = ledger.get(o + "." + name);
                }
                if (r != null) {
                    break;
                }
            }
            // A class-level entry accounts for the members of a class that is itself
            // gone. When the class survives on the target, a dead member (or dead
            // constructor) is damage in its own right: falling back to the class entry
            // would file a real NoSuchMethodError as "known".
            if (r == null && survivesOnTarget(owner)) {
                return null;
            }
        }
        if (r == null) {
            r = ledger.get(owner);
        }
        if (r == null) {
            int inner = owner.indexOf('$');
            if (inner > 0) {
                r = ledger.get(owner.substring(0, inner)); // outer-class entries cover inner classes
            }
        }
        return r;
    }

    /** true only when the target stub was loaded and still has the class (under its analysis name) */
    private boolean survivesOnTarget(String owner) {
        return !targetClasses.isEmpty() && targetClasses.contains(owner);
    }
    /** old signatures restored at runtime by shim mixins -- neither issues nor tombstones */
    public final java.util.Set<String> shimCovers = new java.util.HashSet<>();

    /**
     * Library owners that were classes when the corpus was compiled and are
     * interfaces on the target (DFU 7 turned DataResult into one). Declared,
     * because the stubs carry Minecraft only; Minecraft's own kind changes are
     * read from the stubs (see {@link #interfaceOnTarget}).
     */
    public final java.util.Set<String> interfaceized = new java.util.HashSet<>();

    /**
     * Whether a call into {@code owner} must be an interface call on the target,
     * or null when the converter cannot know the owner's runtime kind (a mod
     * class, a facade compiled into the bridge jar, no target stub). The JVM
     * resolves a call-site's constant-pool ref against the RUNTIME class: an
     * invokeinterface on what is now a class (1.20.2 BlockPointer became a
     * record) or an invokevirtual on what is now an interface (1.20.2
     * NumberRange) is an IncompatibleClassChangeError before any bridge runs.
     */
    public Boolean interfaceOnTarget(String owner) {
        if (interfaceized.contains(owner)) {
            return Boolean.TRUE;
        }
        if (targetClasses.contains(owner)) {
            return targetInterfaces.contains(owner);
        }
        return null;
    }

    private java.util.Set<String> kindFlips;

    /**
     * Owners whose kind differs between the source and the target: a class that
     * merely calls one has damage even when nothing it names is gone.
     */
    public java.util.Set<String> kindFlips() {
        if (kindFlips == null) {
            java.util.Set<String> out = new java.util.HashSet<>(interfaceized);
            for (String c : superOf.keySet()) {
                if (targetClasses.contains(c) && sourceInterfaces.contains(c) != targetInterfaces.contains(c)) {
                    out.add(c);
                }
            }
            kindFlips = out;
        }
        return kindFlips;
    }

    /**
     * Hooks that gained a trailing RegistryWrapper.WrapperLookup parameter on
     * this target (the 1.20.5 component wall): name -> [old desc, new desc].
     * Damage is two-sided -- an old-shape override is orphaned, an old-shape
     * call site references a descriptor that no longer exists -- so the
     * rewrite both synthesizes forwarding overrides and appends a lookup at
     * call sites. Declared per-target in shims-<to>.json ("lookupHooks"):
     * a chain whose target predates the wall must leave these calls alone.
     */
    public final java.util.Map<String, String[]> lookupHooks = new java.util.HashMap<>();

    /**
     * Generic orphaned-override reconnection ("overrideHooks" in
     * shims-<to>.json): vanilla stopped calling a method mods override,
     * replaced by one with a new name and a new parameter type (26.2:
     * Screen.render(GuiGraphics..) -> extractRenderState(GuiGraphicsExtractor..)).
     * For every mod class declaring the old shape but not the new, the rewrite
     * synthesizes the new shape: it wraps the changed argument through
     * {@code wrap} and forwards to the old override, guarded per object by
     * {@code guard}.cbEnter/cbExit so a super-call chain that lands back in
     * the new shape falls through to INVOKESPECIAL super instead of recursing.
     */
    public static final class OverrideHook {
        public String oldName, oldDesc, newName, newDesc;
        /** "owner.name(desc)" static that converts the new-typed argument to the old type. */
        public String wrapOwner, wrapName, wrapDesc;
        /**
         * "owner.name(desc)" static that converts the OLD override's return
         * value to the NEW return type (Item.use: TypedActionResult facade ->
         * InteractionResult). Null when the return type did not change.
         */
        public String unwrapOwner, unwrapName, unwrapDesc;
        /** internal class name providing cbEnter(Object)Z and cbExit(Object)V */
        public String guard;
        /** hierarchy filters: apply only to classes under / not under this vanilla class */
        public String onlyUnder, notUnder;
        /**
         * For arity changes: for each OLD parameter, the index of the NEW
         * parameter to pass (e.g. mouseScrolled(DDD) -> (DDDD): [0, 1, 3]).
         * Null means same arity, positional.
         */
        public int[] args;
    }

    public final java.util.List<OverrideHook> overrideHooks = new java.util.ArrayList<>();

    public java.util.List<OverrideHook> overrideHooksForOld(String name, String desc) {
        java.util.List<OverrideHook> out = new java.util.ArrayList<>();
        for (OverrideHook h : overrideHooks) {
            if (h.oldName.equals(name) && h.oldDesc.equals(desc)) {
                out.add(h);
            }
        }
        return out;
    }

    public OverrideHook overrideHookForNew(String name, String desc) {
        for (OverrideHook h : overrideHooks) {
            if (h.newName.equals(name) && h.newDesc.equals(desc)) {
                return h;
            }
        }
        return null;
    }

    /**
     * Constructor redirects ("ctorRedirects" in shims-<to>.json):
     * "owner.<init>(desc)V" -> "runtimeClass#factory". Mixin cannot add a
     * constructor, but a NEW site can be rewritten: NEW/DUP/args/INVOKESPECIAL
     * becomes args/INVOKESTATIC factory(args)->owner, which leaves the exact
     * same stack. Only NEW sites qualify -- a subclass super(...) call has no
     * NEW to drop and stays damage.
     */
    public final java.util.Map<String, String> ctorRedirects = new java.util.HashMap<>();

    /** owners that have at least one constructor redirect (fast pre-check) */
    public final java.util.Set<String> ctorRedirectOwners = new java.util.HashSet<>();

    /**
     * Constructors the target actually has, owner -> descriptors, keyed in the
     * analysis (intermediary) namespace. Segment data knows member NAMES, and
     * every constructor is named <init>: without this a dead constructor call
     * site was simply invisible to the accounting (7841 Identifier sites at
     * 26.2 never counted). Empty when no target stub was available.
     */
    public final java.util.Map<String, java.util.Set<String>> targetCtors = new java.util.HashMap<>();
    /**
     * Every method DECLARED on each target-stub class, "name(desc)" in the analysis
     * namespace (mojang-era stubs are translated back through the wall table). Mixin
     * resolves @Inject/@Shadow/@Overwrite/@Invoker targets in the target class itself,
     * not up the hierarchy -- a method that moved to a superclass is an apply-time
     * failure even though the member still exists.
     */
    public final java.util.Map<String, java.util.Set<String>> targetDecls = new java.util.HashMap<>();

    /** every class of the target stub, analysis namespace (empty when no stub was available) */
    public final java.util.Set<String> targetClasses = new java.util.HashSet<>();
    /**
     * Target-stub classes that are FINAL on the target (26.2: Enchantment is a
     * final record). A mod class extending one is refused by the JVM at load
     * (IncompatibleClassChangeError) -- no bridge can help, and a verdict that
     * says ok_* for such a mod is a lie.
     */
    public final java.util.Set<String> targetFinals = new java.util.HashSet<>();
    /** target-stub classes that are interfaces on the target */
    public final java.util.Set<String> targetInterfaces = new java.util.HashSet<>();
    /** classes CB's own access widener re-opens with `extendable` -- fabric loader
     *  strips their ACC_FINAL at runtime, so a mod subclass loads and stays a real
     *  instance (instanceof holds, vanilla takes it back). In the analysis
     *  (intermediary) namespace, so hierarchyBreak can consult it directly. */
    public final java.util.Set<String> awExtendable = new java.util.HashSet<>();

    /**
     * Forward wall translation (intermediary -> mojang) for rewriting a mod's
     * own access-widener file, which the class rewrite never touches. Past the
     * 1.21.11->26.1 wall the runtime expects AW entries in the mojang-named
     * namespace that the converted bytecode uses; an unrewritten {@code v1
     * intermediary} AW with {@code class_NNNN} names is rejected at load
     * ("Namespace (intermediary) does not match current runtime namespace").
     * Populated by {@link #loadTargetCtors} only when the chain crosses the
     * wall; empty otherwise (pre-wall AW is already in the right namespace).
     * Keys/values are internal names (class_NNNN -> net/minecraft/...);
     * member maps key "owner.name" (intermediary) -> mojang simple name.
     */
    public final java.util.Map<String, String> wallClassFwd = new java.util.HashMap<>();
    public final java.util.Map<String, String> wallFieldFwd = new java.util.HashMap<>();
    public final java.util.Map<String, String> wallMethodFwd = new java.util.HashMap<>();

    /**
     * Why a mod class's {@code extends}/{@code implements} of {@code type}
     * cannot load on the target, or null when it can (or cannot be judged:
     * no stub, a class outside the stub, a facade rename we ship). Death of
     * the type, finality, and class/interface kind flips are all load-time
     * fatal for the subclass, whatever the ledger says about the type.
     */
    public String hierarchyBreak(String type, boolean asInterface) {
        String t = type;
        if (classRenames.containsKey(t)) {
            t = classRenames.get(t);
            if (!t.startsWith("net/minecraft/")) {
                return null; // onto a facade we ship
            }
        } else if (!targetClasses.contains(t)) {
            // A class that survives in the target under its (possibly wall-renamed)
            // name is NOT a hierarchy break, whatever the per-segment death tables
            // say: past the namespace wall the segment data is in the dead
            // intermediary namespace and records every surviving vanilla class as
            // "gone", but loadTargetCtors has already proven it exists in the
            // target jar (and the convert rename pass rewrites the extends to its
            // real name). Only when the class is genuinely absent from the target
            // do we consult resolveClass for the boundary that killed it.
            Issue r = resolveClass(t);
            if (r.level() != Level.OK) {
                return "gone @" + r.boundary();
            }
            return null;
        }
        if (!targetClasses.contains(t)) {
            return null;
        }
        if (asInterface) {
            return targetInterfaces.contains(t) ? null : "a class, not an interface, in " + to;
        }
        if (targetInterfaces.contains(t)) {
            return "an interface in " + to;
        }
        if (targetFinals.contains(t)) {
            // CB's access widener re-opens this class with `extendable`: fabric
            // loader strips its ACC_FINAL before the mod subclass loads, so the
            // extends is legal and the object is still a real instance.
            return awExtendable.contains(t) ? null : "final in " + to;
        }
        return null;
    }

    /** true unless the owner is a known target-stub class that does NOT declare name(desc). */
    /** injector anchor check/repair against real target bodies; null = unavailable */
    public MixinRepair mixinRepair;

    public boolean declaredOnTarget(String owner, String name, String desc) {
        java.util.Set<String> decl = targetDecls.get(owner);
        if (decl == null) {
            return true; // not a stub class (mod-owned, or dead: class death is reported elsewhere)
        }
        if (desc == null) {
            for (String s : decl) {
                if (s.startsWith(name + "(")) {
                    return true;
                }
            }
            return false;
        }
        return decl.contains(name + desc);
    }

    /**
     * Index the target's constructors. For a real-name (26.x) target the jar is
     * mojang-named: owners and descriptor classes are translated back through
     * the inter->mojang table so they compare against intermediary call sites;
     * classes born after the wall stay mojang-named and match nothing, which
     * is right -- no old mod can name them.
     */
    public void loadTargetCtors(Path stubJar, Path interToMojangTsv) throws IOException {
        java.util.Map<String, String> back = new java.util.HashMap<>();
        java.util.Map<String, String> memberBack = new java.util.HashMap<>();
        java.util.Map<String, String> fieldBack = new java.util.HashMap<>();
        java.util.Map<String, String> fwd = new java.util.HashMap<>();
        if (interToMojangTsv != null && java.nio.file.Files.exists(interToMojangTsv)) {
            for (String ln : java.nio.file.Files.readAllLines(interToMojangTsv)) {
                String[] p = ln.split("\t");
                if (p.length >= 3 && p[0].equals("C")) {
                    back.put(p[2], p[1]);
                    fwd.put(p[1], p[2]);
                    wallClassFwd.put(p[1], p[2]);
                } else if (p.length >= 3 && p[0].equals("M")) {
                    memberBack.put(p[2], p[1]); // "mojOwner.mojName" -> "interOwner.interName"
                    // "interOwner.interName" -> mojang simple name, for AW rewrite
                    wallMethodFwd.put(p[1], p[2].substring(p[2].lastIndexOf('.') + 1));
                } else if (p.length >= 3 && p[0].equals("F")) {
                    fieldBack.put(p[2], p[1]); // a field is declared on exactly one class: no climb
                    wallFieldFwd.put(p[1], p[2].substring(p[2].lastIndexOf('.') + 1));
                }
            }
        }
        var idx = top.dext.centurybridge.data.StubDiff.index(stubJar);
        java.util.regex.Pattern cls = java.util.regex.Pattern.compile("L([^;]+);");
        for (var e : idx.superOf.entrySet()) {
            targetSuperOf.put(back.getOrDefault(e.getKey(), e.getKey()), back.getOrDefault(e.getValue(), e.getValue()));
        }
        for (var e : idx.interfacesOf.entrySet()) {
            targetInterfacesOf.put(back.getOrDefault(e.getKey(), e.getKey()),
                e.getValue().stream().map(i -> back.getOrDefault(i, i)).toList());
        }
        for (var e : idx.classAccess.entrySet()) {
            String owner = back.getOrDefault(e.getKey(), e.getKey());
            targetClasses.add(owner);
            if ((e.getValue() & org.objectweb.asm.Opcodes.ACC_FINAL) != 0) {
                targetFinals.add(owner);
            }
            if ((e.getValue() & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
                targetInterfaces.add(owner);
            }
        }
        for (var e : idx.methods.entrySet()) {
            String owner = back.getOrDefault(e.getKey(), e.getKey());
            java.util.Set<String> decls = targetDecls.computeIfAbsent(owner, k -> new java.util.HashSet<>());
            for (var me : e.getValue().entrySet()) {
                String name = me.getKey();
                if (!back.isEmpty()) {
                    // the wall table lists each member once, on its ROOT declaration; an
                    // override on a subclass shares the intermediary name, so climb the
                    // (source-era) hierarchy until an ancestor's mapping is found
                    String mapped = memberBack.get(e.getKey() + "." + name);
                    if (mapped == null) {
                        for (String anc : selfAndSupers(owner)) {
                            String mojAnc = fwd.get(anc);
                            mapped = mojAnc == null ? null : memberBack.get(mojAnc + "." + name);
                            if (mapped != null) {
                                break;
                            }
                        }
                    }
                    if (mapped != null) {
                        name = mapped.substring(mapped.lastIndexOf('.') + 1);
                    }
                }
                for (String d : me.getValue()) {
                    String bd = back.isEmpty() ? d
                        : cls.matcher(d).replaceAll(mr -> "L" + java.util.regex.Matcher.quoteReplacement(
                            back.getOrDefault(mr.group(1), mr.group(1))) + ";");
                    decls.add(name + bd);
                    if (!name.equals(me.getKey())) {
                        // past the wall the runtime name IS the mojang one: a mixin
                        // spelling it that way (mojang-mapped bundled jar, whose
                        // descriptors are mojang-named as well) resolves too
                        decls.add(me.getKey() + d);
                    }
                    if (name.equals("<init>")) {
                        targetCtors.computeIfAbsent(owner, k -> new java.util.HashSet<>()).add(bd);
                    }
                }
            }
        }
        // fields, as "name:desc" -- @Shadow fields and @Accessor targets resolve on
        // the target class itself exactly like methods do (a field that moved up
        // or down the hierarchy is an apply-time failure, not a survivor)
        for (var e : idx.fields.entrySet()) {
            String owner = back.getOrDefault(e.getKey(), e.getKey());
            java.util.Set<String> decls = targetDecls.computeIfAbsent(owner, k -> new java.util.HashSet<>());
            for (var fe : e.getValue().entrySet()) {
                String name = fe.getKey();
                if (!back.isEmpty()) {
                    String mapped = fieldBack.get(e.getKey() + "." + name);
                    if (mapped == null) {
                        // a record component's field shares its accessor's name; the
                        // table lists it once, as the method (comp_NNNN)
                        mapped = memberBack.get(e.getKey() + "." + name);
                    }
                    if (mapped != null) {
                        name = mapped.substring(mapped.lastIndexOf('.') + 1);
                    }
                }
                for (String d : fe.getValue()) {
                    String bd = back.isEmpty() ? d
                        : cls.matcher(d).replaceAll(mr -> "L" + java.util.regex.Matcher.quoteReplacement(
                            back.getOrDefault(mr.group(1), mr.group(1))) + ";");
                    decls.add(name + ":" + bd);
                    if (!name.equals(fe.getKey())) {
                        decls.add(fe.getKey() + ":" + d); // mojang spelling + descriptor, see above
                    }
                }
            }
        }
        loadAwExtendable(back);
    }

    /**
     * Load CB's own `extendable class` entries from core/shims/&lt;to&gt;/resources/cb.accesswidener
     * into {@link #awExtendable}, in the analysis (intermediary) namespace. The AW is
     * authored in the runtime namespace -- intermediary for pre-wall targets, official
     * (mojang) for 26.x -- so past the wall each name is translated back to intermediary
     * via {@code back} (mojang -&gt; intermediary) to match how hierarchyBreak sees it.
     */
    private void loadAwExtendable(java.util.Map<String, String> back) throws IOException {
        Path aw = Path.of("core/shims/" + to + "/resources/cb.accesswidener");
        if (!java.nio.file.Files.exists(aw)) {
            return;
        }
        for (String ln : java.nio.file.Files.readAllLines(aw)) {
            String s = ln.trim();
            if (s.startsWith("#") || s.isEmpty()) {
                continue;
            }
            // "extendable class net/minecraft/<name>" (also accept the historical
            // "extend-able"/"transitive-extendable" spellings fabric tolerates)
            String[] p = s.split("\\s+");
            if (p.length >= 3 && p[1].equals("class") && p[0].endsWith("extendable")) {
                String name = p[2];
                awExtendable.add(back.getOrDefault(name, name));
            }
        }
    }

    /** field form of {@link #declaredOnTarget}: true unless a known target-stub class lacks name:desc. */
    public boolean fieldDeclaredOnTarget(String owner, String name, String desc) {
        java.util.Set<String> decl = targetDecls.get(owner);
        if (decl == null) {
            return true;
        }
        if (desc == null) {
            for (String s : decl) {
                if (s.startsWith(name + ":")) {
                    return true;
                }
            }
            return false;
        }
        return decl.contains(name + ":" + desc);
    }

    /**
     * Constructor calls with no NEW site (super(...) / this(...)) cannot be
     * routed to a static factory; when only the tail argument changed shape,
     * the call is adapted in place instead: a converter runs on the top of the
     * operand stack and the INVOKESPECIAL takes the new descriptor.
     * key "owner.<init>(oldDesc)V" -> [newDesc, convClass#convName, convDesc].
     */
    public final java.util.Map<String, String[]> superCtorAdapts = new java.util.HashMap<>();

    /**
     * General constructor-call reshaping ("superCtorReshapes" in
     * shims-<to>.json) -- the full form of which {@link #superCtorAdapts} is
     * the one-tail-argument special case. Applies to super(...) / this(...)
     * delegations (no NEW site, so no factory can serve them) and, when no
     * ctorRedirect claims the NEW site, to plain {@code new} sites as well.
     * The engine spills the old arguments into fresh locals above the method's
     * max_locals, re-pushes them in the new order (converting / dropping /
     * supplying as the ops say) and swaps the INVOKESPECIAL descriptor.
     * Frames stay valid: the sequence is straight-line and the temporaries are
     * dead at every existing frame.
     *
     * <pre>
     * "superCtorReshapes": {
     *   "net/minecraft/class_1874.&lt;init&gt;(Lnet/minecraft/class_3956;Lnet/minecraft/class_2960;Ljava/lang/String;...FI)V": {
     *     "desc": "(Lnet/minecraft/class_3956;Ljava/lang/String;...FI)V",
     *     "ops":  ["keep", "drop", "keep", "keep", "keep", "keep", "keep", "keep"]
     *   },
     *   "net/minecraft/class_489.&lt;init&gt;(Lnet/minecraft/class_1720;...Lnet/minecraft/class_2960;)V": {
     *     "desc": "(Lnet/minecraft/class_1720;...Lnet/minecraft/class_2960;Lnet/minecraft/class_2960;Lnet/minecraft/class_2960;)V",
     *     "ops":  ["keep", "keep", "keep", "keep", "keep",
     *              "push top/dext/centurybridge/rt/v1_20_2/Statics#cbLitProgress()Lnet/minecraft/class_2960;",
     *              "push top/dext/centurybridge/rt/v1_20_2/Statics#cbBurnProgress()Lnet/minecraft/class_2960;"]
     *   }
     * }
     * </pre>
     *
     * {@code ops} is read in NEW-argument order; each op emits zero or one new
     * argument and consumes zero or more OLD arguments:
     * <ul>
     * <li>{@code keep} -- re-push the next old argument unchanged.</li>
     * <li>{@code drop} -- consume the next old argument, push nothing.</li>
     * <li>{@code conv OWNER#name(desc)R} -- push the next old argument and run
     *     the static converter on it; its return type is the new argument.
     *     The converter's parameter type must equal the old argument type.</li>
     * <li>{@code push OWNER#name()R} -- call a zero-argument static supplier;
     *     consumes no old argument (a new parameter with a default).</li>
     * <li>{@code keep:N} / {@code conv:I,J,... OWNER#name(desc)R} -- the
     *     explicit form names old argument indices (0-based) instead of taking
     *     the next one in sequence: reordering, duplicating, or folding
     *     several old arguments into one converter. When any op is explicit
     *     the sequential coverage check is waived.</li>
     * </ul>
     * Longs and doubles are handled (two-slot locals, typed load/store). The
     * argument types the ops produce must equal {@code desc}'s parameter list
     * exactly; a mismatch is rejected when the wiring loads, not at a mod's
     * call site. Old ops in sequence must cover every old argument (add
     * {@code drop} for the ones that go away).
     *
     * Precedence at one call site: a NEW site with a {@code ctorRedirects}
     * factory takes the factory; otherwise a reshape wins over a
     * {@code superCtorAdapts} tail adapter for the same key. The full audit
     * credits {@code super-ctor-reshape} / {@code ctor-reshape} per rewritten
     * site; a dead constructor still reached after the pre-pass shows up as
     * {@code SUPER-CTOR-ISSUE} / {@code CTOR-ISSUE} (or its ledger reason).
     */
    public final java.util.Map<String, CtorReshape> superCtorReshapes = new java.util.HashMap<>();

    /** one parsed "superCtorReshapes" entry: the new descriptor and the ops that build its arguments */
    public static final class CtorReshape {
        public final String newDesc;
        public final java.util.List<ReshapeOp> ops;

        CtorReshape(String newDesc, java.util.List<ReshapeOp> ops) {
            this.newDesc = newDesc;
            this.ops = ops;
        }
    }

    /** one reshape op: which old arguments to re-push, and the static to run on them (null = plain keep/drop) */
    public static final class ReshapeOp {
        /** old argument indices pushed, in order; empty for a push (supplier) */
        public final int[] from;
        /** static converter/supplier; null for keep and drop */
        public final String owner, name, desc;
        public final boolean drop;

        ReshapeOp(int[] from, String owner, String name, String desc, boolean drop) {
            this.from = from;
            this.owner = owner;
            this.name = name;
            this.desc = desc;
            this.drop = drop;
        }
    }

    /**
     * Parse and validate one reshape. Rejects, with the offending key in the
     * message: unknown ops, an index past the old arity, a converter whose
     * parameters do not match the old argument types, a void converter,
     * uncovered trailing old arguments (sequential form), and a produced
     * argument list that differs from {@code newDesc}.
     */
    public static CtorReshape parseReshape(String key, String newDesc, java.util.List<String> opStrs) {
        String oldDesc = key.substring(key.indexOf('('));
        org.objectweb.asm.Type[] old = org.objectweb.asm.Type.getArgumentTypes(oldDesc);
        org.objectweb.asm.Type[] want = org.objectweb.asm.Type.getArgumentTypes(newDesc);
        java.util.List<ReshapeOp> ops = new java.util.ArrayList<>();
        java.util.List<org.objectweb.asm.Type> produced = new java.util.ArrayList<>();
        int cursor = 0;
        boolean explicit = false;
        String where = "superCtorReshapes " + key;
        for (String s : opStrs) {
            String t = s.trim();
            int sp = t.indexOf(' ');
            String head = sp < 0 ? t : t.substring(0, sp);
            String rest = sp < 0 ? "" : t.substring(sp + 1).trim();
            String kind = head;
            String idxs = null;
            int colon = head.indexOf(':');
            if (colon >= 0) {
                kind = head.substring(0, colon);
                idxs = head.substring(colon + 1);
            }
            int[] from;
            if (kind.equals("push")) {
                from = new int[0];
            } else if (idxs != null) {
                explicit = true;
                String[] parts = idxs.split(",");
                from = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    from[i] = Integer.parseInt(parts[i].trim());
                    if (from[i] < 0 || from[i] >= old.length) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' names old argument "
                            + from[i] + " but the old constructor has " + old.length);
                    }
                }
            } else {
                if (cursor >= old.length) {
                    throw new IllegalArgumentException(where + ": op '" + t + "' has no old argument left to consume");
                }
                from = new int[] {cursor++};
            }
            switch (kind) {
                case "keep" -> {
                    if (!rest.isEmpty() || from.length != 1) {
                        throw new IllegalArgumentException(where + ": malformed op '" + t + "'");
                    }
                    ops.add(new ReshapeOp(from, null, null, null, false));
                    produced.add(old[from[0]]);
                }
                case "drop" -> {
                    if (!rest.isEmpty() || from.length != 1) {
                        throw new IllegalArgumentException(where + ": malformed op '" + t + "'");
                    }
                    ops.add(new ReshapeOp(from, null, null, null, true));
                }
                case "conv", "push" -> {
                    int hash = rest.indexOf('#');
                    int paren = rest.indexOf('(');
                    if (hash <= 0 || paren <= hash) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' needs OWNER#name(desc)R");
                    }
                    String cOwner = rest.substring(0, hash);
                    String cName = rest.substring(hash + 1, paren);
                    String cDesc = rest.substring(paren);
                    org.objectweb.asm.Type[] p = org.objectweb.asm.Type.getArgumentTypes(cDesc);
                    if (p.length != from.length) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' takes " + p.length
                            + " parameter(s) but consumes " + from.length + " old argument(s)");
                    }
                    for (int i = 0; i < p.length; i++) {
                        if (!p[i].equals(old[from[i]])) {
                            throw new IllegalArgumentException(where + ": op '" + t + "' parameter " + i + " is "
                                + p[i].getDescriptor() + " but old argument " + from[i] + " is " + old[from[i]].getDescriptor());
                        }
                    }
                    org.objectweb.asm.Type r = org.objectweb.asm.Type.getReturnType(cDesc);
                    if (r.getSort() == org.objectweb.asm.Type.VOID) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' returns void");
                    }
                    ops.add(new ReshapeOp(from, cOwner, cName, cDesc, false));
                    produced.add(r);
                }
                default -> throw new IllegalArgumentException(where + ": unknown op '" + t + "'");
            }
        }
        if (!explicit && cursor != old.length) {
            throw new IllegalArgumentException(where + ": " + (old.length - cursor)
                + " trailing old argument(s) not covered by ops (add keep/drop)");
        }
        if (!produced.equals(java.util.Arrays.asList(want))) {
            throw new IllegalArgumentException(where + ": ops produce " + produced + " but desc declares "
                + java.util.Arrays.asList(want));
        }
        return new CtorReshape(newDesc, ops);
    }

    /**
     * Redirects that apply to INVOKESPECIAL super-calls only ("superRedirects"):
     * "owner.name(desc)" -> "runtimeClass[#member]", receiver as arg 0. The
     * method still exists on the target with the same descriptor, so a plain
     * instance redirect would be wrong for virtual calls -- but the OLD
     * super-call contract differs (1.20.1 Screen.render drew the widgets and
     * nothing else; since 1.20.2 it draws the background first, over whatever
     * the mod drew). Only a mod's own super.render(..) is rerouted.
     */
    public final java.util.Map<String, String> superRedirects = new java.util.HashMap<>();

    /** fabric.mod.json dependency id migrations declared per target
     *  ("depRenames" in shims-<to>.json), on top of the per-segment ones. */
    public final java.util.Map<String, String> depRenames = new java.util.HashMap<>();

    /** Runtime class serving cbLookup() for the hooks above ("lookupRt"). */
    public String lookupRt;

    /** cbLookup()'s descriptor -- intermediary through 1.21.11, mojang names
     *  past the namespace wall ("lookupDesc" in shims-<to>.json). */
    public String lookupDesc = "()Lnet/minecraft/class_7225$class_7874;";

    /**
     * True when this target is past the 1.20.5 Block.use split
     * (method_9534 6-arg -> method_55766 5-arg): old-shape overrides need a
     * synthesized new-shape hook or vanilla never calls them again.
     */
    public boolean blockUseSplit;

    /**
     * child -> superclass, read from the OLD stub. A mod calls a method through
     * whatever class it holds a reference to, but the declaration -- and hence
     * the ledger entry, cover or redirect -- lives wherever the member is
     * actually declared. Looking up only the call site's owner missed the skin
     * accessors on class_742, onCraft on class_1792 and ChatHud's accessor, all
     * of which were written off as dead while a bridge already existed.
     */
    public final java.util.Map<String, String> superOf = new java.util.HashMap<>();
    /** source-version class -> its directly implemented interfaces (game classes only) */
    public final java.util.Map<String, java.util.List<String>> interfacesOf = new java.util.HashMap<>();
    /** source-stub classes that were interfaces when the corpus was compiled */
    public final java.util.Set<String> sourceInterfaces = new java.util.HashSet<>();
    /** overridable methods each source-stub class declares, "name(desc)": the shapes mod overrides bound to */
    public final java.util.Map<String, java.util.Set<String>> sourceDecls = new java.util.HashMap<>();
    /** the target stub's hierarchy, analysis namespace */
    public final java.util.Map<String, String> targetSuperOf = new java.util.HashMap<>();
    public final java.util.Map<String, java.util.List<String>> targetInterfacesOf = new java.util.HashMap<>();

    public static boolean intermediaryMethod(String name) {
        return METHOD_PAT.matcher(name).matches();
    }

    /** the source class a mod declaration overrode: the first ancestor (source hierarchy) declaring the shape */
    public String sourceDeclarer(String name, String desc, java.util.List<String> roots) {
        String shape = name + desc;
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(roots);
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            if (sourceDecls.getOrDefault(c, java.util.Set.of()).contains(shape)) {
                return c;
            }
            String sup = superOf.get(c);
            if (sup != null) {
                todo.add(sup);
            }
            todo.addAll(interfacesOf.getOrDefault(c, java.util.List.of()));
        }
        return null;
    }

    /** whether a target ancestor still declares the shape; true when the target is unknown */
    public boolean targetDeclares(String name, String desc, java.util.List<String> roots) {
        if (targetDecls.isEmpty()) {
            return true;
        }
        String shape = name + desc;
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(roots);
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            if (targetDecls.getOrDefault(c, java.util.Set.of()).contains(shape)) {
                return true;
            }
            String sup = targetSuperOf.get(c);
            if (sup != null) {
                todo.add(sup);
            }
            todo.addAll(targetInterfacesOf.getOrDefault(c, java.util.List.of()));
        }
        return false;
    }

    /** Walk owner and every ancestor, in order, so callers can resolve via any of them. */
    public java.util.List<String> selfAndSupers(String owner) {
        java.util.List<String> chainUp = new java.util.ArrayList<>();
        String cur = owner;
        for (int hops = 0; cur != null && hops < 12; hops++) {
            chainUp.add(cur);
            cur = superOf.get(cur);
            if (cur != null && !cur.startsWith("net/minecraft/")) {
                break;   // left the game's own hierarchy
            }
        }
        return chainUp;
    }

    /** Load the class hierarchy of a stub jar so member lookups can walk it. */
    public void loadHierarchy(Path stubJar) throws IOException {
        try (java.util.zip.ZipInputStream zin =
                 new java.util.zip.ZipInputStream(Files.newInputStream(stubJar))) {
            java.util.zip.ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class") || !e.getName().startsWith("net/minecraft/")) {
                    continue;
                }
                new org.objectweb.asm.ClassReader(zin.readAllBytes()).accept(
                    new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                        @Override
                        public void visit(int v, int a, String name, String sig,
                                          String sup, String[] itf) {
                            if (sup != null) {
                                superOf.put(name, sup);
                            }
                            if (itf != null && itf.length > 0) {
                                interfacesOf.put(name, java.util.List.of(itf));
                            }
                            if ((a & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
                                sourceInterfaces.add(name);
                            }
                            owner = name;
                        }

                        String owner;

                        @Override
                        public org.objectweb.asm.MethodVisitor visitMethod(int a, String name, String desc,
                                                                           String sig, String[] ex) {
                            if ((a & (org.objectweb.asm.Opcodes.ACC_STATIC | org.objectweb.asm.Opcodes.ACC_PRIVATE)) == 0
                                    && !name.startsWith("<")) {
                                sourceDecls.computeIfAbsent(owner, k -> new java.util.HashSet<>()).add(name + desc);
                            }
                            return null;
                        }
                    }, org.objectweb.asm.ClassReader.SKIP_CODE
                       | org.objectweb.asm.ClassReader.SKIP_DEBUG
                       | org.objectweb.asm.ClassReader.SKIP_FRAMES);
            }
        }
    }
    /** source-version class -> "client" | "datagen" (absent = common/server) */
    public final java.util.Map<String, String> classSide = new java.util.HashMap<>();

    public String sideOf(String internalName) {
        return classSide.getOrDefault(internalName, "common");
    }

    private Chain(String from, String to) {
        this.from = from;
        this.to = to;
    }

    private static String[] splitSig(String sig) {
        int p = sig.indexOf('(');
        return new String[] {sig.substring(0, p), sig.substring(p)};
    }

    public static Chain load(Path segmentsDir, String from, String to) throws IOException {
        int a = RELEASES.indexOf(from);
        int b = RELEASES.indexOf(to);
        if (a < 0 || b < 0 || a >= b) {
            throw new IllegalArgumentException("bad chain range " + from + " -> " + to);
        }
        Chain c = new Chain(from, to);
        for (int i = a; i < b; i++) {
            c.segments.add(Segment.load(
                segmentsDir.resolve(RELEASES.get(i) + "__" + RELEASES.get(i + 1) + ".json")));
        }
        Path shims = segmentsDir.resolve("shims-" + to + ".json");
        if (java.nio.file.Files.exists(shims)) {
            var gson = new com.google.gson.Gson();
            var root = gson.fromJson(java.nio.file.Files.readString(shims), com.google.gson.JsonObject.class);
            if (root.has("staticRedirects")) {
                var sr = root.getAsJsonObject("staticRedirects");
                for (String k : sr.keySet()) {
                    c.staticRedirects.put(k, sr.get(k).getAsString());
                }
            }
            if (root.has("instanceRedirects")) {
                var ir = root.getAsJsonObject("instanceRedirects");
                for (String k : ir.keySet()) {
                    c.instanceRedirects.put(k, ir.get(k).getAsString());
                }
            }
            if (root.has("fieldRedirects")) {
                var fr = root.getAsJsonObject("fieldRedirects");
                for (String k : fr.keySet()) {
                    c.fieldRedirects.put(k, fr.get(k).getAsString());
                }
            }
            if (root.has("fieldGetters")) {
                var fg = root.getAsJsonObject("fieldGetters");
                for (String k : fg.keySet()) {
                    c.fieldGetters.put(k, fg.get(k).getAsString());
                }
            }
            if (root.has("interfaceized")) {
                for (var el : root.getAsJsonArray("interfaceized")) {
                    c.interfaceized.add(el.getAsString());
                }
            }
            if (root.has("lookupHooks")) {
                var lh = root.getAsJsonObject("lookupHooks");
                for (String k : lh.keySet()) {
                    var arr = lh.getAsJsonArray(k);
                    c.lookupHooks.put(k, new String[] {
                        arr.get(0).getAsString(), arr.get(1).getAsString()});
                }
            }
            if (root.has("overrideHooks")) {
                for (var el : root.getAsJsonArray("overrideHooks")) {
                    var o = el.getAsJsonObject();
                    OverrideHook h = new OverrideHook();
                    String[] osig = splitSig(o.get("old").getAsString());
                    String[] nsig = splitSig(o.get("new").getAsString());
                    h.oldName = osig[0];
                    h.oldDesc = osig[1];
                    h.newName = nsig[0];
                    h.newDesc = nsig[1];
                    if (o.has("wrap")) {
                        String w = o.get("wrap").getAsString();
                        int dot = w.lastIndexOf('.', w.indexOf('('));
                        h.wrapOwner = w.substring(0, dot);
                        String[] wsig = splitSig(w.substring(dot + 1));
                        h.wrapName = wsig[0];
                        h.wrapDesc = wsig[1];
                    }
                    if (o.has("unwrap")) {
                        String u = o.get("unwrap").getAsString();
                        int dot = u.lastIndexOf('.', u.indexOf('('));
                        h.unwrapOwner = u.substring(0, dot);
                        String[] usig = splitSig(u.substring(dot + 1));
                        h.unwrapName = usig[0];
                        h.unwrapDesc = usig[1];
                    }
                    h.guard = o.has("guard") ? o.get("guard").getAsString() : null;
                    h.onlyUnder = o.has("onlyUnder") ? o.get("onlyUnder").getAsString() : null;
                    if (o.has("args")) {
                        var arr = o.getAsJsonArray("args");
                        h.args = new int[arr.size()];
                        for (int i = 0; i < arr.size(); i++) {
                            h.args[i] = arr.get(i).getAsInt();
                        }
                    }
                    h.notUnder = o.has("notUnder") ? o.get("notUnder").getAsString() : null;
                    c.overrideHooks.add(h);
                }
            }
            if (root.has("depRenames")) {
                var dr = root.getAsJsonObject("depRenames");
                for (String k : dr.keySet()) {
                    c.depRenames.put(k, dr.get(k).getAsString());
                }
            }
            if (root.has("instanceFieldGetters")) {
                var ifg = root.getAsJsonObject("instanceFieldGetters");
                for (String k : ifg.keySet()) {
                    c.instanceFieldGetters.put(k, ifg.get(k).getAsString());
                }
            }
            if (root.has("instanceOfRedirects")) {
                var ior = root.getAsJsonObject("instanceOfRedirects");
                for (String k : ior.keySet()) {
                    c.instanceOfRedirects.put(k, ior.get(k).getAsString());
                }
            }
            if (root.has("superRedirects")) {
                var sr2 = root.getAsJsonObject("superRedirects");
                for (String k : sr2.keySet()) {
                    c.superRedirects.put(k, sr2.get(k).getAsString());
                }
            }
            if (root.has("superCtorAdapts")) {
                var sca = root.getAsJsonObject("superCtorAdapts");
                for (String k : sca.keySet()) {
                    var o = sca.getAsJsonObject(k);
                    c.superCtorAdapts.put(k, new String[] {
                        o.get("desc").getAsString(), o.get("conv").getAsString(),
                        o.get("convDesc").getAsString() });
                    c.ctorRedirectOwners.add(k.substring(0, k.indexOf('.')));
                }
            }
            if (root.has("superCtorReshapes")) {
                var scr = root.getAsJsonObject("superCtorReshapes");
                for (String k : scr.keySet()) {
                    var o = scr.getAsJsonObject(k);
                    java.util.List<String> ops = new java.util.ArrayList<>();
                    for (var el : o.getAsJsonArray("ops")) {
                        ops.add(el.getAsString());
                    }
                    c.superCtorReshapes.put(k, parseReshape(k, o.get("desc").getAsString(), ops));
                    c.ctorRedirectOwners.add(k.substring(0, k.indexOf(".<init>")));
                }
            }
            if (root.has("ctorRedirects")) {
                var cr = root.getAsJsonObject("ctorRedirects");
                for (String k : cr.keySet()) {
                    c.ctorRedirects.put(k, cr.get(k).getAsString());
                    c.ctorRedirectOwners.add(k.substring(0, k.indexOf(".<init>")));
                }
            }
            if (root.has("lookupDesc")) {
                c.lookupDesc = root.get("lookupDesc").getAsString();
            }
            if (root.has("lookupRt")) {
                c.lookupRt = root.get("lookupRt").getAsString();
            }
            if (root.has("blockUseSplit")) {
                c.blockUseSplit = root.get("blockUseSplit").getAsBoolean();
            }
            if (root.has("classRenames")) {
                var cr = root.getAsJsonObject("classRenames");
                for (String k : cr.keySet()) {
                    c.classRenames.put(k, cr.get(k).getAsString());
                }
            }
            if (root.has("superRenames")) {
                var sr = root.getAsJsonObject("superRenames");
                for (String k : sr.keySet()) {
                    c.superRenames.put(k, sr.get(k).getAsString());
                }
            }
            if (root.has("fieldRenames")) {
                var fr = root.getAsJsonObject("fieldRenames");
                for (String k : fr.keySet()) {
                    c.fieldRenames.put(k, fr.get(k).getAsString());
                }
            }
            if (root.has("methodRenames")) {
                var mr = root.getAsJsonObject("methodRenames");
                for (String k : mr.keySet()) {
                    c.methodRenames.put(k, mr.get(k).getAsString());
                }
            }
            if (root.has("ledger")) {
                var lg = root.getAsJsonObject("ledger");
                for (String k : lg.keySet()) {
                    c.ledger.put(k, lg.get(k).getAsString());
                }
            }
            if (root.has("covers")) {
                for (var e : root.getAsJsonArray("covers")) {
                    c.shimCovers.add(e.getAsString());
                }
            }
        }
        Path sides = segmentsDir.resolve("sides-" + from + ".json");
        if (java.nio.file.Files.exists(sides)) {
            var gson = new com.google.gson.Gson();
            var root = gson.fromJson(java.nio.file.Files.readString(sides), com.google.gson.JsonObject.class);
            for (String side : new String[] {"client", "datagen"}) {
                if (root.has(side)) {
                    for (var e : root.getAsJsonArray(side)) {
                        c.classSide.put(e.getAsString(), side);
                    }
                }
            }
        }
        return c;
    }

    public Issue resolveMember(char kind, String name) {
        Pattern pat = kind == 'm' ? METHOD_PAT : FIELD_PAT;
        if (!pat.matcher(name).matches()) {
            return Issue.OK; // stable / non-intermediary name
        }
        Issue result = Issue.OK;
        for (Segment s : segments) {
            String boundary = s.from + "->" + s.to;
            if ((kind == 'm' ? s.mg : s.fg).contains(name)) {
                return new Issue(Level.L3, boundary); // first death wins
            }
            if (result.level() == Level.OK && (kind == 'm' ? s.mc : s.fc).contains(name)) {
                result = new Issue(Level.L2, boundary);
            }
        }
        return result;
    }

    public Issue resolveClass(String internal) {
        if (!CLASS_PAT.matcher(internal).matches()) {
            return Issue.OK;
        }
        for (Segment s : segments) {
            if (s.cg.contains(internal)) {
                return new Issue(Level.L3, s.from + "->" + s.to);
            }
        }
        return Issue.OK;
    }

    /** member verdict combined with deaths of any class buried in its descriptor */
    public Issue resolveSpec(char kind, String name, String desc) {
        Issue r = resolveMember(kind, name);
        if (desc != null) {
            var m = CLASS_PAT.matcher(desc);
            while (m.find()) {
                r = r.worst(resolveClass(m.group()));
            }
        }
        return r;
    }
}
