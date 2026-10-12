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
    public static final List<String> RELEASES = top.dext.centurybridge.res.Release.ALL;

    private static final Pattern METHOD_PAT = Pattern.compile("method_\\d+");
    private static final Pattern FIELD_PAT = Pattern.compile("field_\\d+");
    /** an intermediary class name, named inner classes included (the dead Serializer/Builder
     *  inner classes of 1.20.2 were invisible to every death table while this stopped at the
     *  outer name) */
    private static final Pattern CLASS_PAT = Pattern.compile("net/minecraft/class_\\d+(?:\\$class_\\d+)*");

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
    /**
     * Interface calls redirected to a static, receiver as arg 0 ("interfaceCallRedirects"): like
     * {@link #instanceRedirects}, but only the INVOKEINTERFACE sites (and interface method handles)
     * of the key are rewritten. For an interface this layer provides under its own name (a
     * classRenamed library interface) that a library class surviving on the target implemented on
     * the source and no longer does (1.20.1's Fabric API FabricTagBuilder implemented
     * FabricProvidedTagBuilder; 1.20.2's cannot implement the bridge's interface): the static serves
     * such receivers and calls the interface method on any other. A super call to the interface's
     * default method (INVOKESPECIAL, from a mod's own implementation) stays what it is -- redirecting
     * it to a static that calls the method on the receiver would re-enter the implementation's own
     * override.
     */
    public final java.util.Map<String, String> interfaceCallRedirects = new java.util.HashMap<>();
    /** GETSTATIC field reads redirected to a same-name field on a runtime class */
    public final java.util.Map<String, String> fieldRedirects = new java.util.HashMap<>();
    /** GETSTATIC field reads redirected to a zero-arg getter method (name = field name)
     *  on a runtime class -- for holderified fields whose value must be unwrapped lazily
     *  (a static-field target would evaluate at Statics class-init, before the registry binds) */
    public final java.util.Map<String, String> fieldGetters = new java.util.HashMap<>();
    /**
     * the data-driven types the target's CenturyBridge runtime registers (item model types,
     * tint sources...): converted resources may name them only where the runtime has them
     */
    public final java.util.Set<String> resourceTypes = new java.util.HashSet<>();
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
     * PUTFIELD "owner.name:desc" -> "runtimeClass[#member]": an instance field write rewritten to a
     * static setter(receiver, value)V, the write side of {@link #instanceFieldGetters} -- for a
     * public field whose type changed (1.20.2 PendingInvite.worldOwnerUuid: String -> UUID, the
     * setter converts) or one that is gone and lives on as other state (1.20.2 AbstractSelectionList's
     * x0/x1/y0/y1/width/height, which 1.20.3 keeps as the widget's geometry: a write moves the widget,
     * as the field write moved the list). Found up the hierarchy from the site's owner, as a getter.
     */
    public final java.util.Map<String, String> instanceFieldSetters = new java.util.HashMap<>();
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
     * Home for a super call ({@link #superRedirects}): the class the JVM resolved
     * {@code invokespecial owner.name(desc)} to in the source version -- the first class up the
     * chain declaring the member there -- when a super redirect is keyed on it; null otherwise.
     * A super call names the class the mod extends, which need not be the declaring one: a mod
     * extending ServerScoreboard calls super.getObjective on ServerScoreboard, declared by
     * Scoreboard. Keyed only on Scoreboard, such a call used to miss its super redirect and take
     * the instance redirect, whose virtual dispatch lands back in the mod's own override. A vanilla
     * class in between that declares the member itself (its own source body) stops the walk:
     * only a super redirect keyed on that class serves it. Without a source stub the first keyed
     * ancestor is taken.
     */
    public String superRedirectHome(String owner, String nameDesc) {
        for (String o : selfAndSupers(owner)) {
            if (superRedirects.containsKey(o + "." + nameDesc)) {
                return o;
            }
            if (sourceDecls.getOrDefault(o, java.util.Set.of()).contains(nameDesc)) {
                return null;
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
        /**
         * internal class name providing cbEnter(Object)Z and cbExit(Object)V. A guarded (or narrowing)
         * fall-through calls super's new shape; when the class's vanilla superclass chain has none but a
         * direct vanilla superinterface has it as a default method (1.20.3's ChangeOverTimeBlock.getNextState
         * under a copper block extending Block), it calls that interface's default instead.
         */
        public String guard;
        /** hierarchy filters: apply only to classes under / not under this vanilla class */
        public String onlyUnder, notUnder;
        /**
         * For arity changes: for each OLD parameter, the index of the NEW
         * parameter to pass (e.g. mouseScrolled(DDD) -> (DDDD): [0, 1, 3]).
         * Null means same arity, positional.
         */
        public int[] args;
        /**
         * Bridge mode, for a change no single argument wrap expresses (appendTooltip:
         * a world became a tooltip context, the list a consumer, a parameter was added):
         * the synthesized new shape calls this static with (this as {@link #adapter},
         * every new argument) and the bridge calls the old override through the
         * adapter -- an interface declaring the old shape, added to the mod class, whose
         * own method implements it. The guard works as for forwarding hooks.
         */
        public String adapter;
        public String bridgeOwner, bridgeName, bridgeDesc;
        /**
         * Carried arguments ("frames": class providing cbPush / cbPop / cbArg*): the synthesized
         * new shape records the arguments vanilla passed before forwarding to the old method, and
         * the old method's own super call -- rewritten from the old shape to the new one
         * ({@link #superCalls}) -- takes the values the old shape has no place for from that
         * record, so vanilla's implementation sees exactly what vanilla passed. Replaces the
         * guard: with every super call rewritten to an INVOKESPECIAL of the new shape there is
         * no path back into the synthesized method, and a guard keyed by the object would turn a
         * legitimately nested call on the same instance (a block is a singleton) into a
         * fall-through that skips the mod's method.
         */
        public String frames;
        /**
         * "frameDefaults" (a section of its own, keyed by the old shape): per new-argument index, the
         * static {owner, name, desc} whose value the rewritten super call passes when no frame holds the
         * object -- the old override entered directly rather than through the synthesized new shape.
         * Without one the argument is its type's zero value.
         */
        public final java.util.Map<Integer, String[]> frameDefaults = new java.util.HashMap<>();
        /** "superCalls": "rewrite": a mod's super.old(..) in a class under the hook becomes super.new(..) */
        public boolean superCalls;
        /**
         * Narrowing forward ("narrow": true), for a parameter the release widened (1.20.2: a
         * packet's handle(ClientGamePacketListener) became handle(ClientCommonPacketListener),
         * because the packet now also arrives in the configuration phase). The old override was
         * only ever handed the old type, so the synthesized new shape passes vanilla's value on
         * -- cast to the old type -- only when it is one, and otherwise falls through to super's
         * new shape (or answers the type's zero value when nothing above declares it) exactly
         * as if the mod had not overridden anything:
         * <pre>
         *   new(a):  if (!(a instanceof Old)) return super.new(a);  return this.old((Old) a);
         * </pre>
         * Every argument whose old type differs from its new one is narrowed this way, so the
         * old type must be a subtype of the new one (wiring-check holds the target to it). No
         * wrap / bridge / frames; a guard may be combined (it is checked after the type test).
         */
        public boolean narrow;
    }

    public final java.util.List<OverrideHook> overrideHooks = new java.util.ArrayList<>();

    /**
     * Adapter hooks ("adapterHooks" in shims-<to>.json): a vanilla type whose
     * overridable surface changed SHAPE, not just a signature (1.20.2
     * RecipeSerializer: read(id, json) became codec()). A mod class under
     * {@code under} that itself declares every {@code requires} shape gets
     * {@code adapter} -- a bridge interface declaring exactly those old shapes, so
     * the mod's own methods implement it -- added to its interfaces, and each
     * {@code synth} method (the new shape vanilla calls) synthesized to call a
     * static bridge with (this, args...). The bridge reaches the old methods
     * through the adapter, typed. A new shape two hooks would both synthesize in one class
     * (a covariant override declares the old shape once per vanilla supertype that had it)
     * is synthesized once, by the first of them in wiring order.
     */
    public static final class AdapterHook {
        public String under, adapter;
        /**
         * Optional cbEnter/cbExit guard class: each synthesized method then calls its bridge
         * only on first entry and falls through to super's shape on re-entry, so a bridge can
         * defer to a vanilla implementation above the mod class by calling the method again.
         */
        public String guard;
        public final java.util.List<String> requires = new java.util.ArrayList<>();
        /** {new name, new desc, call owner, call name, call desc} */
        public final java.util.List<String[]> synth = new java.util.ArrayList<>();
    }

    public final java.util.List<AdapterHook> adapterHooks = new java.util.ArrayList<>();

    /**
     * Mod overrides of a vanilla method the target made final, or replaced by one of another name
     * that vanilla calls instead ("overrideRenames"). Overriding a final method is a class load
     * failure, so no hook that leaves the old declaration in place can help: in a mod class under
     * {@code onlyUnder} (source hierarchy) a declared, non-static old shape is renamed to
     * {@code newName}, same descriptor.
     * <ul>
     * <li>with {@code superCalls} ("rename"), the class's super.old(..) calls follow it -- for a
     *     vanilla method that lives on under the new name (1.20.3: AbstractSelectionList.render is
     *     AbstractWidget.renderWidget, its updateNarration is updateWidgetNarration);</li>
     * <li>with an {@code adapter} interface (declaring the new name), the class gets the interface
     *     and the renamed method is made public, so a mixin on the final method can hand the call to
     *     the mod's method when the object implements the adapter (1.20.3's final AbstractWidget.render
     *     calling a 1.20.2 widget's own render); the mod's super.old(..) then reaches the final
     *     vanilla method again, where the mixin's guard lets it run.</li>
     * </ul>
     */
    public static final class OverrideRename {
        public String oldName, oldDesc, newName, onlyUnder, adapter;
        public boolean superCalls;
    }

    public final java.util.List<OverrideRename> overrideRenames = new java.util.ArrayList<>();

    /**
     * Implementations a vanilla type newly demands ("abstractFills" in a step's wiring): a method the
     * target declares abstract that a mod class written against the source cannot implement, because
     * the source had no such method (1.20.3's BlockBehaviour.codec(), Level.tickRateManager(),
     * ContainerSingleItem.getTheItem()). Left alone, the class loads and the first call is an
     * AbstractMethodError. A concrete class of the converted jar under {@code under} that would
     * inherit the method only as abstract on the target -- no class of the jar on its way declares
     * it, no override or adapter hook synthesizes it, and the nearest declaration up its target
     * ancestry ({@link #concreteOnTarget}) is abstract or missing -- gets it synthesized as
     * {@code public R m(args) { return call(this, args...); }}: the static answers what the source
     * API made of the situation. A class that inherits a concrete implementation keeps it, so the
     * fill never replaces vanilla behaviour.
     * <pre>
     *   "abstractFills": [{"under": "net/minecraft/class_4970", "method": "method_53969()Lcom/mojang/serialization/MapCodec;",
     *                      "call": "Owner.name(Lnet/minecraft/class_4970;)Lcom/mojang/serialization/MapCodec;"}]
     * </pre>
     * The call's first parameter is {@code under} (the receiver), the rest the method's own.
     */
    public static final class AbstractFill {
        public String under, name, desc, callOwner, callName, callDesc;
    }

    public final java.util.List<AbstractFill> abstractFills = new java.util.ArrayList<>();

    /** target-stub methods declared abstract, "owner.name(desc)" in the analysis namespace */
    public final java.util.Set<String> targetAbstracts = new java.util.HashSet<>();

    /**
     * Whether the target gives a class with these vanilla ancestors a concrete name+desc, the way the
     * JVM selects one: the nearest declaration up the superclass chain decides (an abstract
     * redeclaration -- 1.20.3's HorizontalDirectionalBlock.codec() under Block's concrete one --
     * counts as abstract); without one, a default method of an implemented interface. True when the
     * target is unknown.
     */
    public boolean concreteOnTarget(String name, String desc, java.util.List<String> roots) {
        if (targetDecls.isEmpty()) {
            return true;
        }
        String shape = name + desc;
        java.util.ArrayDeque<String> ifaces = new java.util.ArrayDeque<>();
        for (String r : roots) {
            if (targetInterfaces.contains(r)) {
                ifaces.add(r);
                continue;
            }
            for (String c = r; c != null; c = targetSuperOf.get(c)) {
                if (targetDecls.getOrDefault(c, java.util.Set.of()).contains(shape)) {
                    return !targetAbstracts.contains(c + "." + shape);
                }
                ifaces.addAll(targetInterfacesOf.getOrDefault(c, java.util.List.of()));
            }
        }
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!ifaces.isEmpty()) {
            String i = ifaces.poll();
            if (!seen.add(i)) {
                continue;
            }
            if (targetDecls.getOrDefault(i, java.util.Set.of()).contains(shape) && !targetAbstracts.contains(i + "." + shape)) {
                return true;
            }
            ifaces.addAll(targetInterfacesOf.getOrDefault(i, java.util.List.of()));
        }
        return false;
    }

    /**
     * Lambdas whose functional interface's abstract method changed shape ("lambdaAdapters"): 1.20.2's
     * ChatDecorator.decorate answers a Text where 1.20.1's answered a CompletableFuture. A lambda or
     * method reference compiled against the old shape is an invokedynamic of
     * LambdaMetafactory.metafactory with the old SAM; the class it spins implements the interface
     * with the old method only, and the new abstract method is an AbstractMethodError when vanilla
     * calls it. Key: "iface.sam(oldErasedSamDesc)" (analysis names); value:
     * <pre>
     *   {"iface": "CB functional interface declaring exactly the old SAM",
     *    "adapt": "Owner#name(LCbIface;)Liface;"}
     * </pre>
     * The converter builds the lambda as the CB interface instead -- same SAM name, sam and
     * instantiated method types, implementation handle, captured arguments: everything the old
     * bootstrap described stays true -- and passes it through the adapt static, which answers the
     * vanilla interface (implementing the new shape on top of the old one). A lambda bootstrapped by
     * altMetafactory (serializable, marker interfaces, bridges) records its interface for
     * deserialization and extra types; it is left alone and noted, never adapted silently.
     */
    public static final class LambdaAdapter {
        public String iface, sam, samDesc, legacy, adaptOwner, adaptName, adaptDesc;
    }

    /** "iface.sam(samDesc)" -> adapter, see {@link LambdaAdapter} */
    public final java.util.Map<String, LambdaAdapter> lambdaAdapters = new java.util.HashMap<>();

    public static LambdaAdapter parseLambdaAdapter(String key, String legacy, String adapt) {
        String where = "lambdaAdapters " + key;
        int paren = key.indexOf('(');
        int dot = paren < 0 ? -1 : key.lastIndexOf('.', paren);
        int hash = adapt.indexOf('#');
        int aparen = adapt.indexOf('(');
        if (dot <= 0 || hash <= 0 || aparen <= hash) {
            throw new IllegalArgumentException(where + ": key needs iface.sam(desc), adapt needs Owner#name(desc)");
        }
        LambdaAdapter la = new LambdaAdapter();
        la.iface = key.substring(0, dot);
        la.sam = key.substring(dot + 1, paren);
        la.samDesc = key.substring(paren);
        la.legacy = legacy;
        la.adaptOwner = adapt.substring(0, hash);
        la.adaptName = adapt.substring(hash + 1, aparen);
        la.adaptDesc = adapt.substring(aparen);
        String want = "(L" + legacy + ";)L" + la.iface + ";";
        if (!la.adaptDesc.equals(want)) {
            throw new IllegalArgumentException(where + ": adapt must take the CB interface and answer the vanilla one: " + want);
        }
        return la;
    }

    /**
     * Members of a dead class its facade does not provide ("facadeGaps" in
     * shims-<to>.json, "owner.name(desc)" / "owner.name:desc" -> reason), gated by
     * wiring-check against every shape the class ever had. A reference to one is
     * accounted (and tombstoned) like any other dead member instead of being taken
     * as served because the class has a facade.
     */
    public final java.util.Map<String, String> facadeGaps = new java.util.HashMap<>();

    /** whether {@code type} is reached from these vanilla roots through the source hierarchy */
    public boolean sourceSubtype(java.util.List<String> roots, String type) {
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(roots);
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            if (c.equals(type)) {
                return true;
            }
            String sup = superOf.get(c);
            if (sup != null) {
                todo.add(sup);
            }
            todo.addAll(interfacesOf.getOrDefault(c, java.util.List.of()));
        }
        return false;
    }

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
    /**
     * The SOURCE release's real method bodies (MixinBridge counts an @At anchor in the body it was
     * written for before retargeting it to what the step's wiring says stands there now); null =
     * unavailable, and no anchor is retargeted.
     */
    public MixinRepair sourceCode;

    /**
     * Whether a reference resolves on the target the way the JVM resolves it: the owner,
     * its superclasses, their interfaces. TRUE found, FALSE definitely absent (every class
     * on the way is a target-stub class and none declares it), null when the answer leaves
     * the stub (a library supertype) or the owner is not a target class.
     */
    public Boolean resolvesOnTarget(String owner, String shape) {
        if (targetDecls.isEmpty() || !targetClasses.contains(owner)) {
            return null;
        }
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(java.util.List.of(owner));
        java.util.Set<String> seen = new java.util.HashSet<>();
        boolean leftStub = false;
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            if (!targetClasses.contains(c)) {
                if (c.startsWith("java/")) {
                    Boolean jdk = jdkDeclares(c, shape);
                    if (jdk == null) {
                        leftStub = true;
                    } else if (jdk) {
                        return Boolean.TRUE;
                    }
                } else {
                    leftStub = true;
                }
                continue;
            }
            if (targetDecls.getOrDefault(c, java.util.Set.of()).contains(shape)) {
                return Boolean.TRUE;
            }
            String sup = targetSuperOf.get(c);
            if (sup != null) {
                todo.add(sup);
            }
            todo.addAll(targetInterfacesOf.getOrDefault(c, java.util.List.of()));
        }
        return leftStub ? null : Boolean.FALSE;
    }

    /** whether a JDK class (or its supertypes) declares the shape; null when it cannot be loaded */
    private static Boolean jdkDeclares(String internalName, String shape) {
        try {
            Class<?> k = Class.forName(internalName.replace('/', '.'), false, ClassLoader.getPlatformClassLoader());
            for (Class<?> c = k; c != null; c = c.getSuperclass()) {
                for (java.lang.reflect.Method m : c.getDeclaredMethods()) {
                    if ((m.getName() + org.objectweb.asm.Type.getMethodDescriptor(m)).equals(shape)) {
                        return Boolean.TRUE;
                    }
                }
                for (Class<?> i : c.getInterfaces()) {
                    for (java.lang.reflect.Method m : i.getMethods()) {
                        if ((m.getName() + org.objectweb.asm.Type.getMethodDescriptor(m)).equals(shape)) {
                            return Boolean.TRUE;
                        }
                    }
                }
            }
            return Boolean.FALSE;
        } catch (ReflectiveOperationException | LinkageError e) {
            return null;
        }
    }

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
                    if (idx.abstracts.contains(e.getKey() + "." + me.getKey() + d)) {
                        targetAbstracts.add(owner + "." + name + bd);
                    }
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
     *
     * A climb step's layer ships its own access widener: when the step's wiring is named
     * (-Dcb.wiring=&lt;root&gt;/core/segments/steps/&lt;V&gt;__&lt;to&gt;.json), the layer next to
     * it (&lt;root&gt;/core/shims/steps/&lt;to&gt;/resources/*.accesswidener) is read too. An
     * `extendable method` entry counts for its class as well: the loader makes the class of
     * an extendable method extendable (AwRules).
     */
    private void loadAwExtendable(java.util.Map<String, String> back) throws IOException {
        java.util.List<Path> files = new java.util.ArrayList<>();
        String stepWiring = System.getProperty("cb.wiring");
        if (stepWiring != null) {
            // the checkout the step's wiring belongs to: <root>/core/segments/steps/<V>__<to>.json for a
            // climb step's layer, <root>/core/segments/shims-<to>.json for step 1's (the 1.20.2 tree)
            Path w = Path.of(stepWiring).toAbsolutePath();
            Path segs = w.getParent();
            boolean step = segs != null && segs.getFileName() != null && segs.getFileName().toString().equals("steps");
            Path core = step ? segs.getParent().getParent() : segs == null ? null : segs.getParent();
            Path root = core == null ? null : core.getParent();
            Path res = root == null ? null
                : root.resolve(step ? "core/shims/steps/" + to + "/resources" : "core/shims/" + to + "/resources");
            if (res != null && java.nio.file.Files.isDirectory(res)) {
                try (var s = java.nio.file.Files.list(res)) {
                    s.filter(p -> p.getFileName().toString().endsWith(".accesswidener")).sorted().forEach(files::add);
                }
            }
        }
        if (files.isEmpty()) {
            files.add(Path.of("core/shims/" + to + "/resources/cb.accesswidener"));
        }
        for (Path aw : files) {
            if (!java.nio.file.Files.exists(aw)) {
                continue;
            }
            for (String ln : java.nio.file.Files.readAllLines(aw)) {
                String s = ln.trim();
                if (s.startsWith("#") || s.isEmpty()) {
                    continue;
                }
                // "extendable class net/minecraft/<name>" (also accept the historical
                // "extend-able"/"transitive-extendable" spellings fabric tolerates)
                String[] p = s.split("\\s+");
                if (p.length >= 3 && (p[1].equals("class") || p[1].equals("method")) && p[0].endsWith("extendable")) {
                    String name = p[2];
                    awExtendable.add(back.getOrDefault(name, name));
                }
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
     * <li>{@code drop OWNER#name(T)V} -- consume the next old argument and hand it to a void
     *     static instead of pushing it: a value the new shape has no parameter for but the
     *     bridge still needs (1.20.2 recipes lost their id argument; the id goes to the bridge
     *     that records it for the recipe being built). {@code T} must equal the old argument
     *     type. The call is made at the op's place in the sequence, before the invoke.</li>
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
     * <li>{@code conv ... as <type>} / {@code push ... as <type>} -- the static answers a
     *     supertype (Object) of the new argument and the site casts its answer to {@code type}:
     *     a type the shim cannot name in Java source, such as a package-private class that the
     *     layer's access widener opens only at runtime (1.20.2 FilePackResources takes its
     *     package-private SharedZipFileAccess).</li>
     * </ul>
     * Longs and doubles are handled (two-slot locals, typed load/store). The
     * argument types the ops produce must equal {@code desc}'s parameter list
     * exactly; a mismatch is rejected when the wiring loads, not at a mod's
     * call site. Old ops in sequence must cover every old argument (add
     * {@code drop} for the ones that go away).
     *
     * An old argument the new constructor has no place for, but whose value the
     * instance still takes another way (1.20.1 RealmsUploadScreen's after-upload
     * callback, a parameter in 1.20.1 and set on the instance by the layer in 1.20.2),
     * is handed over right after the constructor returns: {@code "post":
     * "OWNER#name(Lowner;...)V"} with {@code "postArgs": [old argument indices]} runs
     * the static on the constructed object (the NEW site's object, or {@code this}
     * after a mod constructor's super(...)) and those old arguments, still in their
     * spill locals. Straight-line code after the INVOKESPECIAL, so frames stay exact.
     *
     * Precedence at one call site: a NEW site with a {@code ctorRedirects}
     * factory takes the factory; otherwise a reshape wins over a
     * {@code superCtorAdapts} tail adapter for the same key. The full audit
     * credits {@code super-ctor-reshape} / {@code ctor-reshape} per rewritten
     * site; a dead constructor still reached after the pre-pass shows up as
     * {@code SUPER-CTOR-ISSUE} / {@code CTOR-ISSUE} (or its ledger reason).
     */
    public final java.util.Map<String, CtorReshape> superCtorReshapes = new java.util.HashMap<>();

    /**
     * Method calls whose descriptor changed at the boundary, reshaped in place ("callReshapes"
     * in a step's wiring): "owner.name(oldDesc)R" -> {"desc": "(newArgs)R'", "ops": [...],
     * "ret": "pop" | "conv OWNER#name(R')R" | "push OWNER#name()R"}. Every call site whose
     * JVM-resolved home is that key -- virtual, interface, static, and a mod's own super.m(..)
     * alike, so super semantics survive -- has its arguments spilled and re-pushed per the ops
     * (the {@link #superCtorReshapes} grammar: keep / drop / conv / push / param) and takes the
     * new descriptor; the return value is adapted after the call: dropped when the old shape
     * returned nothing, converted when it returned another type, supplied when the new shape
     * returns nothing but the old one did, passed on ("ret": "keep") when the new shape returns a
     * subtype of the old return type. Validated when the wiring loads.
     */
    public static final class CallReshape {
        public final CtorReshape args;
        public final String oldDesc;
        public final String newDesc;
        /** after the call: null (same return), "pop", or a static {owner, name, desc} */
        public final String retKind;
        public final String retOwner, retName, retDesc;

        /** whether the call's values change at all (a converter on an argument or the return), not just its shape */
        public boolean converts() {
            if ("conv".equals(retKind)) {
                return true;
            }
            for (ReshapeOp op : args.ops) {
                if (op.owner != null && !op.drop && op.from.length > 0) {
                    return true;
                }
            }
            return false;
        }

        CallReshape(CtorReshape args, String oldDesc, String newDesc, String retKind, String retOwner, String retName, String retDesc) {
            this.args = args;
            this.oldDesc = oldDesc;
            this.newDesc = newDesc;
            this.retKind = retKind;
            this.retOwner = retOwner;
            this.retName = retName;
            this.retDesc = retDesc;
        }
    }

    public final java.util.Map<String, CallReshape> callReshapes = new java.util.HashMap<>();
    /** key -> key, for {@link #instanceRedirectHome}'s JVM-order lookup */
    public final java.util.Map<String, String> callReshapeIndex = new java.util.HashMap<>();
    /** member names with a reshape: a class whose bytes mention none of them is skipped */
    public final java.util.Set<String> callReshapeNames = new java.util.HashSet<>();
    /** name + new descriptor of every reshape: what the rewritten call sites now reference */
    public final java.util.Set<String> callReshapeTargets = new java.util.HashSet<>();
    /**
     * name + descriptor of the reshapes that keep the descriptor -- conversions of values whose
     * erased type stayed while their type arguments changed (generic drift). Such a member keeps
     * its shape on a mod class that overrides it too ({@link #convertHooks} puts a converting
     * bridge in front of the mod's own method), so a call naming that mod class is converted as
     * well: the bridge expects what vanilla passes.
     */
    public final java.util.Set<String> sameShapeCalls = new java.util.HashSet<>();

    /**
     * An override of a member whose descriptor stayed while the type arguments of its parameters or
     * return changed ("convertHooks", the override side of a same-shape callReshape). Vanilla calls
     * the mod's method with W's values and reads W's from what it answers; the mod's body was written
     * for V's. The engine renames the mod's method ({@link #renamed}) and puts a bridge with the
     * original name and descriptor in its place:
     * <pre>
     *   m(a0..an):  return ret(this.m$cbW(arg0(a0) .. argn(an)));
     * </pre>
     * each converter optional. A mod's own call of m -- this.m(..), super.m(..) from a subclass --
     * goes through the call side's conversion and then the bridge, so the renamed body always sees
     * V's values. Converters should be adaptive (a value already of the wanted kind passes through)
     * and undo each other, because erasure cannot tell where a value came from.
     *
     * A converter may take the mod object first, OWNER#name(LX;T)T with X the hooked class or a
     * supertype of it, for a value whose W form depends on whose member answered it: the W
     * AttributeModifierTemplates standing for a 1.20.1 effect's own modifier map compute that
     * effect's getAttributeModifierValue, so MobEffect.getAttributeModifiers' override converter
     * needs the effect. The bridge then passes {@code this}: {@code ret(this, this.m$cbW(..))}.
     */
    public static final class ConvertHook {
        public final String under, name, desc;
        /** per parameter: {owner, name, desc} of the static turning W's value into V's, or null */
        public final String[][] args;
        /** {owner, name, desc} of the static turning the old body's V value into W's, or null */
        public final String[] ret;
        public final String renamed;

        ConvertHook(String under, String name, String desc, String[][] args, String[] ret, String renamed) {
            this.under = under;
            this.name = name;
            this.desc = desc;
            this.args = args;
            this.ret = ret;
            this.renamed = renamed;
        }
    }

    /** convertHooks by name + descriptor */
    public final java.util.Map<String, java.util.List<ConvertHook>> convertHooks = new java.util.HashMap<>();

    static String[] parseStaticRef(String where, String s) {
        String t = s.trim();
        int hash = t.indexOf('#');
        int paren = t.indexOf('(');
        if (hash <= 0 || paren <= hash) {
            throw new IllegalArgumentException(where + ": '" + s + "' is not OWNER#name(desc)R");
        }
        return new String[] {t.substring(0, hash), t.substring(hash + 1, paren), t.substring(paren)};
    }

    /** a convertHooks converter: (T)T, or (LX;T)T taking the mod object first ({@link ConvertHook}) */
    static boolean convertsInPlace(String convDesc, org.objectweb.asm.Type t) {
        org.objectweb.asm.Type[] cp = org.objectweb.asm.Type.getArgumentTypes(convDesc);
        if (!org.objectweb.asm.Type.getReturnType(convDesc).equals(t) || cp.length < 1 || cp.length > 2) {
            return false;
        }
        return cp[cp.length - 1].equals(t) && (cp.length == 1 || cp[0].getSort() == org.objectweb.asm.Type.OBJECT);
    }

    /** whether a convertHooks converter takes the mod object before the value */
    public static boolean takesReceiver(String[] conv) {
        return org.objectweb.asm.Type.getArgumentTypes(conv[2]).length == 2;
    }

    /** one "convertHooks" entry: {"under", "member": "name(desc)", "args": [ref|null..], "ret": ref} */
    public static ConvertHook parseConvertHook(com.google.gson.JsonObject o, String step) {
        String under = o.get("under").getAsString();
        String member = o.get("member").getAsString();
        String where = "convertHooks " + under + "." + member;
        int paren = member.indexOf('(');
        if (paren <= 0) {
            throw new IllegalArgumentException(where + ": member must be name(desc)");
        }
        String name = member.substring(0, paren);
        String desc = member.substring(paren);
        if (name.startsWith("<")) {
            throw new IllegalArgumentException(where + ": constructors are not overridden (superCtorReshapes)");
        }
        org.objectweb.asm.Type[] params = org.objectweb.asm.Type.getArgumentTypes(desc);
        org.objectweb.asm.Type rt = org.objectweb.asm.Type.getReturnType(desc);
        String[][] args = new String[params.length][];
        boolean any = false;
        if (o.has("args")) {
            var a = o.getAsJsonArray("args");
            if (a.size() != params.length) {
                throw new IllegalArgumentException(where + ": args lists " + a.size() + " converters for " + params.length + " parameters");
            }
            for (int i = 0; i < params.length; i++) {
                if (a.get(i).isJsonNull()) {
                    continue;
                }
                String[] ref = parseStaticRef(where, a.get(i).getAsString());
                if (!convertsInPlace(ref[2], params[i])) {
                    throw new IllegalArgumentException(where + ": converter " + i + " must be (" + params[i].getDescriptor()
                        + ")" + params[i].getDescriptor() + " or take the mod object first");
                }
                args[i] = ref;
                any = true;
            }
        }
        String[] ret = null;
        if (o.has("ret") && !o.get("ret").isJsonNull()) {
            ret = parseStaticRef(where, o.get("ret").getAsString());
            if (rt.getSort() == org.objectweb.asm.Type.VOID || !convertsInPlace(ret[2], rt)) {
                throw new IllegalArgumentException(where + ": ret must be (" + rt.getDescriptor() + ")" + rt.getDescriptor()
                    + " or take the mod object first");
            }
            any = true;
        }
        if (!any) {
            throw new IllegalArgumentException(where + ": converts nothing");
        }
        return new ConvertHook(under, name, desc, args, ret, name + "$cb" + step.replace('.', '_'));
    }

    public static CallReshape parseCallReshape(String key, String newDesc, java.util.List<String> ops, String ret) {
        String where = "callReshapes " + key;
        String oldDesc = key.substring(key.indexOf('('));
        CtorReshape args = parseReshape(key, newDesc, ops);
        org.objectweb.asm.Type oldRet = org.objectweb.asm.Type.getReturnType(oldDesc);
        org.objectweb.asm.Type newRet = org.objectweb.asm.Type.getReturnType(newDesc);
        if (oldRet.equals(newRet)) {
            if (ret == null) {
                return new CallReshape(args, oldDesc, newDesc, null, null, null, null);
            }
            // the erased return type stayed while its type arguments changed (generic drift:
            // RecipeManager.getFirstMatch answers Optional<RecipeHolder<T>> where it answered
            // Optional<T>): only a conversion of the value makes sense
            if (!ret.trim().startsWith("conv ")) {
                throw new IllegalArgumentException(where + ": return type unchanged, 'ret' can only be a same-type conv");
            }
        }
        if (oldRet.getSort() == org.objectweb.asm.Type.VOID) {
            if (ret != null && !ret.equals("pop")) {
                throw new IllegalArgumentException(where + ": the old shape returned nothing; 'ret' can only be pop");
            }
            return new CallReshape(args, oldDesc, newDesc, "pop", null, null, null);
        }
        if (ret == null) {
            throw new IllegalArgumentException(where + ": return " + newRet + " -> " + oldRet + " needs 'ret'");
        }
        if (ret.equals("keep")) {
            // the new shape returns a subtype of the old return type: the value passes unchanged
            // (the wiring gate checks the subtype relation on the target)
            if (oldRet.getSort() != org.objectweb.asm.Type.OBJECT || newRet.getSort() != org.objectweb.asm.Type.OBJECT) {
                throw new IllegalArgumentException(where + ": ret keep needs object return types, not " + newRet + " -> " + oldRet);
            }
            return new CallReshape(args, oldDesc, newDesc, null, null, null, null);
        }
        String[] kv = ret.trim().split(" ", 2);
        if (kv.length != 2 || !(kv[0].equals("conv") || kv[0].equals("push"))) {
            throw new IllegalArgumentException(where + ": malformed ret '" + ret + "'");
        }
        String rest = kv[1].trim();
        int hash = rest.indexOf('#');
        int paren = rest.indexOf('(');
        if (hash <= 0 || paren <= hash) {
            throw new IllegalArgumentException(where + ": ret needs OWNER#name(desc)R");
        }
        String rd = rest.substring(paren);
        org.objectweb.asm.Type[] rp = org.objectweb.asm.Type.getArgumentTypes(rd);
        org.objectweb.asm.Type rr = org.objectweb.asm.Type.getReturnType(rd);
        boolean ok = kv[0].equals("conv")
            ? rp.length == 1 && rp[0].equals(newRet) && rr.equals(oldRet)
            : rp.length == 0 && rr.equals(oldRet) && newRet.getSort() == org.objectweb.asm.Type.VOID;
        if (!ok) {
            throw new IllegalArgumentException(where + ": ret " + ret + " does not turn " + newRet + " into " + oldRet);
        }
        return new CallReshape(args, oldDesc, newDesc, kv[0], rest.substring(0, hash), rest.substring(hash + 1, paren), rd);
    }

    /** one parsed "superCtorReshapes" entry: the new descriptor and the ops that build its arguments */
    public static final class CtorReshape {
        public final String newDesc;
        public final java.util.List<ReshapeOp> ops;
        /** static run on the constructed object and {@link #postArgs} after the call (null = none) */
        public String postOwner, postName, postDesc;
        /** old argument indices the post static takes after the object */
        public int[] postArgs = new int[0];

        CtorReshape(String newDesc, java.util.List<ReshapeOp> ops) {
            this.newDesc = newDesc;
            this.ops = ops;
        }

        /**
         * "post": "OWNER#name(Lowner;...)V" with "postArgs": [i...] -- validated against the
         * key: the static takes the constructed class, then the named old arguments' types.
         */
        void setPost(String key, String post, int[] args) {
            String where = "superCtorReshapes " + key;
            int hash = post.indexOf('#');
            int paren = post.indexOf('(');
            if (hash <= 0 || paren <= hash) {
                throw new IllegalArgumentException(where + ": post '" + post + "' needs OWNER#name(desc)V");
            }
            org.objectweb.asm.Type[] old = org.objectweb.asm.Type.getArgumentTypes(key.substring(key.indexOf('(')));
            StringBuilder want = new StringBuilder("(L").append(key, 0, key.indexOf(".<init>")).append(';');
            for (int i : args) {
                if (i < 0 || i >= old.length) {
                    throw new IllegalArgumentException(where + ": postArgs names old argument " + i
                        + " but the old constructor has " + old.length);
                }
                want.append(old[i].getDescriptor());
            }
            want.append(")V");
            if (!post.substring(paren).equals(want.toString())) {
                throw new IllegalArgumentException(where + ": post '" + post + "' must have the descriptor " + want);
            }
            this.postOwner = post.substring(0, hash);
            this.postName = post.substring(hash + 1, paren);
            this.postDesc = post.substring(paren);
            this.postArgs = args;
        }
    }

    /** one reshape op: which old arguments to re-push, and the static to run on them (null = plain keep/drop) */
    public static final class ReshapeOp {
        /** old argument indices pushed, in order; empty for a push (supplier) */
        public final int[] from;
        /** static converter/supplier; null for keep and drop */
        public final String owner, name, desc;
        public final boolean drop;
        /** "param": the enclosing method's first parameter of this type; owner/name/desc then supply the fallback */
        public org.objectweb.asm.Type param;
        /**
         * "conv ... as &lt;type&gt;" / "push ... as &lt;type&gt;": the static answers a supertype (Object) of the
         * argument the new shape takes and the rewritten site casts it -- for a type the shim cannot
         * name in Java (a package-private class the layer's access widener opens at runtime only).
         */
        public org.objectweb.asm.Type cast;

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
            if (kind.equals("push") || kind.equals("param")) {
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
                    if (from.length != 1) {
                        throw new IllegalArgumentException(where + ": malformed op '" + t + "'");
                    }
                    ops.add(new ReshapeOp(from, null, null, null, false));
                    // "keep:N <type>": the old argument passed, unchanged, where the new shape
                    // declares a supertype of it (whoever writes the entry has checked the
                    // hierarchy; the verifier holds it to that at load)
                    produced.add(rest.isEmpty() ? old[from[0]] : org.objectweb.asm.Type.getType(rest));
                }
                case "drop" -> {
                    if (from.length != 1) {
                        throw new IllegalArgumentException(where + ": malformed op '" + t + "'");
                    }
                    if (rest.isEmpty()) {
                        ops.add(new ReshapeOp(from, null, null, null, true));
                    } else {
                        // "drop OWNER#name(T)V": the dropped old argument goes to a void static
                        int hash = rest.indexOf('#');
                        int paren = rest.indexOf('(');
                        if (hash <= 0 || paren <= hash) {
                            throw new IllegalArgumentException(where + ": op '" + t + "' needs OWNER#name(desc)V");
                        }
                        String sDesc = rest.substring(paren);
                        org.objectweb.asm.Type[] sp2 = org.objectweb.asm.Type.getArgumentTypes(sDesc);
                        if (sp2.length != 1 || !sp2[0].equals(old[from[0]])
                                || org.objectweb.asm.Type.getReturnType(sDesc).getSort() != org.objectweb.asm.Type.VOID) {
                            throw new IllegalArgumentException(where + ": op '" + t + "' must take exactly old argument "
                                + from[0] + " (" + old[from[0]].getDescriptor() + ") and return void");
                        }
                        ops.add(new ReshapeOp(from, rest.substring(0, hash), rest.substring(hash + 1, paren), sDesc, true));
                    }
                }
                case "conv", "push" -> {
                    org.objectweb.asm.Type castTo = null;
                    int as = rest.indexOf(" as ");
                    if (as >= 0) {
                        castTo = org.objectweb.asm.Type.getType(rest.substring(as + 4).trim());
                        rest = rest.substring(0, as).trim();
                    }
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
                    if (castTo != null && (castTo.getSort() != org.objectweb.asm.Type.OBJECT || r.getSort() != org.objectweb.asm.Type.OBJECT)) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' casts between non-object types");
                    }
                    ReshapeOp op = new ReshapeOp(from, cOwner, cName, cDesc, false);
                    op.cast = castTo;
                    ops.add(op);
                    produced.add(castTo != null ? castTo : r);
                }
                case "param" -> {
                    // "param:<desc> OWNER#name()R": the enclosing constructor's own first
                    // parameter of that type (a 26.x model's root ModelPart, which super()
                    // now wants and the mod's constructor was handed), else the supplier
                    if (idxs == null || idxs.isEmpty()) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' needs param:<type descriptor>");
                    }
                    org.objectweb.asm.Type pt = org.objectweb.asm.Type.getType(idxs);
                    int hash = rest.indexOf('#');
                    int paren = rest.indexOf('(');
                    if (hash <= 0 || paren <= hash || org.objectweb.asm.Type.getArgumentTypes(rest.substring(paren)).length != 0
                            || !org.objectweb.asm.Type.getReturnType(rest.substring(paren)).equals(pt)) {
                        throw new IllegalArgumentException(where + ": op '" + t + "' needs a fallback OWNER#name()" + idxs);
                    }
                    ReshapeOp op = new ReshapeOp(from, rest.substring(0, hash), rest.substring(hash + 1, paren),
                        rest.substring(paren), false);
                    op.param = pt;
                    ops.add(op);
                    produced.add(pt);
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

    /** Minecraft's own code: net/minecraft, and the packages Mojang never obfuscated */
    public static boolean minecraftOwned(String internalName) {
        return internalName.startsWith("net/minecraft/") || internalName.startsWith("com/mojang/blaze3d/")
            || internalName.startsWith("com/mojang/realmsclient/");
    }

    public static boolean intermediaryClass(String internalName) {
        return CLASS_PAT.matcher(internalName).matches();
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
        // climbing one release at a time (Main convert-climb) names each step's own wiring;
        // a path that does not exist means the step has none
        String stepWiring = System.getProperty("cb.wiring");
        Path shims = stepWiring != null ? Path.of(stepWiring) : segmentsDir.resolve("shims-" + to + ".json");
        if (Wiring.exists(shims)) {
            var root = Wiring.read(shims);
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
            if (root.has("interfaceCallRedirects")) {
                var icr = root.getAsJsonObject("interfaceCallRedirects");
                for (String k : icr.keySet()) {
                    c.interfaceCallRedirects.put(k, icr.get(k).getAsString());
                }
            }
            if (root.has("fieldRedirects")) {
                var fr = root.getAsJsonObject("fieldRedirects");
                for (String k : fr.keySet()) {
                    c.fieldRedirects.put(k, fr.get(k).getAsString());
                }
            }
            if (root.has("resourceTypes")) {
                for (var t : root.getAsJsonArray("resourceTypes")) {
                    c.resourceTypes.add(t.getAsString());
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
                    h.frames = o.has("frames") ? o.get("frames").getAsString() : null;
                    h.superCalls = o.has("superCalls") && o.get("superCalls").getAsString().equals("rewrite");
                    if (h.frames != null && (h.guard != null || h.wrapName != null || h.unwrapName != null || o.has("bridge"))) {
                        throw new IllegalArgumentException("overrideHooks " + h.oldName + h.oldDesc
                            + ": frames go with plain argument forwarding only (no guard / wrap / unwrap / bridge)");
                    }
                    if (h.superCalls && h.args == null && org.objectweb.asm.Type.getArgumentTypes(h.oldDesc).length
                            != org.objectweb.asm.Type.getArgumentTypes(h.newDesc).length) {
                        throw new IllegalArgumentException("overrideHooks " + h.oldName + h.oldDesc + ": superCalls needs args");
                    }
                    h.narrow = o.has("narrow") && o.get("narrow").getAsBoolean();
                    if (h.narrow && (h.frames != null || h.wrapName != null || o.has("bridge"))) {
                        throw new IllegalArgumentException("overrideHooks " + h.oldName + h.oldDesc
                            + ": narrow forwards the new argument itself (no wrap / bridge / frames)");
                    }
                    if (o.has("bridge")) {
                        String br = o.get("bridge").getAsString();
                        int dot = br.lastIndexOf('.', br.indexOf('('));
                        h.bridgeOwner = br.substring(0, dot);
                        String[] bsig = splitSig(br.substring(dot + 1));
                        h.bridgeName = bsig[0];
                        h.bridgeDesc = bsig[1];
                        h.adapter = o.get("adapter").getAsString();
                    }
                    c.overrideHooks.add(h);
                }
            }
            if (root.has("frameDefaults")) {
                // what a rewritten super call passes for an argument the old shape has no place for when
                // there is no frame to read it from: the mod's old override entered directly (the mod's
                // own call, not vanilla's through the synthesized new shape) -- a null NbtAccounter there
                // is an NPE in vanilla's body. Keyed by the hook's old shape; per new argument index a
                // static OWNER#name()T, OWNER#name(LX;)T taking the receiver, or OWNER#name(LX;<old args>)T
                // taking the receiver and the old call's arguments, computing what V used
                for (var e : root.getAsJsonObject("frameDefaults").entrySet()) {
                    String[] osig = splitSig(e.getKey());
                    boolean found = false;
                    for (OverrideHook h : c.overrideHooks) {
                        if (!h.oldName.equals(osig[0]) || !h.oldDesc.equals(osig[1])) {
                            continue;
                        }
                        if (h.frames == null) {
                            throw new IllegalArgumentException("frameDefaults " + e.getKey() + ": the hook carries no frames");
                        }
                        org.objectweb.asm.Type[] na = org.objectweb.asm.Type.getArgumentTypes(h.newDesc);
                        for (var d : e.getValue().getAsJsonObject().entrySet()) {
                            int j = Integer.parseInt(d.getKey());
                            if (d.getValue().getAsString().startsWith("zero:")) {
                                continue; // judged: the type's zero value is what V meant ("zero: <why>")
                            }
                            String[] ref = parseStaticRef("frameDefaults " + e.getKey(), d.getValue().getAsString());
                            org.objectweb.asm.Type[] dp = org.objectweb.asm.Type.getArgumentTypes(ref[2]);
                            org.objectweb.asm.Type[] oa = org.objectweb.asm.Type.getArgumentTypes(h.oldDesc);
                            // ()T, (receiver)T, or (receiver, every old argument)T -- DispenserBlock.dispense's
                            // new BlockState is the state at the old position: level.getBlockState(pos)
                            boolean shape = dp.length == 0 || dp[0].getSort() == org.objectweb.asm.Type.OBJECT
                                && (dp.length == 1 || dp.length == oa.length + 1
                                    && java.util.Arrays.equals(java.util.Arrays.copyOfRange(dp, 1, dp.length), oa));
                            if (j < 0 || j >= na.length || !org.objectweb.asm.Type.getReturnType(ref[2]).equals(na[j]) || !shape) {
                                throw new IllegalArgumentException("frameDefaults " + e.getKey() + ": " + d.getKey()
                                    + " must name a new argument and a static ()T, (receiver)T or (receiver, old arguments)T"
                                    + " returning that argument's type");
                            }
                            h.frameDefaults.put(j, ref);
                        }
                        found = true;
                    }
                    if (!found) {
                        throw new IllegalArgumentException("frameDefaults " + e.getKey() + ": no override hook has that old shape");
                    }
                }
            }
            if (root.has("facadeGaps")) {
                for (var e : root.getAsJsonObject("facadeGaps").entrySet()) {
                    c.facadeGaps.put(e.getKey(), e.getValue().getAsString());
                }
            }
            if (root.has("abstractFills")) {
                for (var el : root.getAsJsonArray("abstractFills")) {
                    var o = el.getAsJsonObject();
                    AbstractFill f = new AbstractFill();
                    f.under = o.get("under").getAsString();
                    String[] msig = splitSig(o.get("method").getAsString());
                    f.name = msig[0];
                    f.desc = msig[1];
                    String call = o.get("call").getAsString();
                    int dot = call.lastIndexOf('.', call.indexOf('('));
                    f.callOwner = call.substring(0, dot);
                    String[] csig = splitSig(call.substring(dot + 1));
                    f.callName = csig[0];
                    f.callDesc = csig[1];
                    String want = "(L" + f.under + ";" + f.desc.substring(1);
                    if (!f.callDesc.equals(want)) {
                        throw new IllegalArgumentException("abstractFills " + f.under + "." + f.name + f.desc
                            + ": call must take the receiver and the method's arguments and return its type: " + want);
                    }
                    c.abstractFills.add(f);
                }
            }
            if (root.has("adapterHooks")) {
                for (var el : root.getAsJsonArray("adapterHooks")) {
                    var o = el.getAsJsonObject();
                    AdapterHook h = new AdapterHook();
                    h.under = o.get("under").getAsString();
                    h.adapter = o.get("adapter").getAsString();
                    if (o.has("guard")) {
                        h.guard = o.get("guard").getAsString();
                    }
                    for (var r : o.getAsJsonArray("requires")) {
                        h.requires.add(r.getAsString());
                    }
                    for (var sy : o.getAsJsonArray("synth")) {
                        var so = sy.getAsJsonObject();
                        String[] nsig = splitSig(so.get("new").getAsString());
                        String call = so.get("call").getAsString();
                        int dot = call.lastIndexOf('.', call.indexOf('('));
                        String[] csig = splitSig(call.substring(dot + 1));
                        h.synth.add(new String[] {nsig[0], nsig[1], call.substring(0, dot), csig[0], csig[1]});
                    }
                    c.adapterHooks.add(h);
                }
            }
            if (root.has("depRenames")) {
                var dr = root.getAsJsonObject("depRenames");
                for (String k : dr.keySet()) {
                    c.depRenames.put(k, dr.get(k).getAsString());
                }
            }
            if (root.has("lambdaAdapters")) {
                for (var e : root.getAsJsonObject("lambdaAdapters").entrySet()) {
                    var o = e.getValue().getAsJsonObject();
                    c.lambdaAdapters.put(e.getKey(), parseLambdaAdapter(e.getKey(),
                        o.get("iface").getAsString(), o.get("adapt").getAsString()));
                }
            }
            if (root.has("instanceFieldGetters")) {
                var ifg = root.getAsJsonObject("instanceFieldGetters");
                for (String k : ifg.keySet()) {
                    c.instanceFieldGetters.put(k, ifg.get(k).getAsString());
                }
            }
            if (root.has("instanceFieldSetters")) {
                var ifs = root.getAsJsonObject("instanceFieldSetters");
                for (String k : ifs.keySet()) {
                    c.instanceFieldSetters.put(k, ifs.get(k).getAsString());
                }
            }
            if (root.has("overrideRenames")) {
                for (var el : root.getAsJsonArray("overrideRenames")) {
                    var o = el.getAsJsonObject();
                    OverrideRename r = new OverrideRename();
                    String[] osig = splitSig(o.get("old").getAsString());
                    r.oldName = osig[0];
                    r.oldDesc = osig[1];
                    r.newName = o.get("new").getAsString();
                    r.onlyUnder = o.get("onlyUnder").getAsString();
                    r.adapter = o.has("adapter") ? o.get("adapter").getAsString() : null;
                    r.superCalls = o.has("superCalls") && o.get("superCalls").getAsString().equals("rename");
                    if (r.newName.contains("(")) {
                        throw new IllegalArgumentException("overrideRenames " + r.oldName + r.oldDesc
                            + ": new is a method name; the descriptor stays");
                    }
                    c.overrideRenames.add(r);
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
                    CtorReshape cr = parseReshape(k, o.get("desc").getAsString(), ops);
                    if (o.has("post")) {
                        var pa = o.has("postArgs") ? o.getAsJsonArray("postArgs") : new com.google.gson.JsonArray();
                        int[] idx = new int[pa.size()];
                        for (int i = 0; i < idx.length; i++) {
                            idx[i] = pa.get(i).getAsInt();
                        }
                        cr.setPost(k, o.get("post").getAsString(), idx);
                    }
                    c.superCtorReshapes.put(k, cr);
                    c.ctorRedirectOwners.add(k.substring(0, k.indexOf(".<init>")));
                }
            }
            if (root.has("callReshapes")) {
                var cr = root.getAsJsonObject("callReshapes");
                for (String k : cr.keySet()) {
                    var o = cr.getAsJsonObject(k);
                    java.util.List<String> ops = new java.util.ArrayList<>();
                    for (var el : o.getAsJsonArray("ops")) {
                        ops.add(el.getAsString());
                    }
                    String ret = o.has("ret") ? o.get("ret").getAsString() : null;
                    CallReshape parsed = parseCallReshape(k, o.get("desc").getAsString(), ops, ret);
                    c.callReshapes.put(k, parsed);
                    c.callReshapeIndex.put(k, k);
                    String nm = k.substring(k.lastIndexOf('.', k.indexOf('(')) + 1, k.indexOf('('));
                    c.callReshapeNames.add(nm);
                    c.callReshapeTargets.add(nm + o.get("desc").getAsString());
                    if (parsed.converts() && parsed.newDesc.equals(parsed.oldDesc)) {
                        c.sameShapeCalls.add(nm + parsed.newDesc);
                    }
                }
            }
            if (root.has("convertHooks")) {
                for (var e : root.getAsJsonArray("convertHooks")) {
                    ConvertHook h = parseConvertHook(e.getAsJsonObject(), c.to);
                    c.convertHooks.computeIfAbsent(h.name + h.desc, x -> new java.util.ArrayList<>()).add(h);
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
