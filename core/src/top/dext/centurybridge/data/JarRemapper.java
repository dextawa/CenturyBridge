package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.Type;

/**
 * Remaps an official (obfuscated) Minecraft jar into the intermediary
 * namespace, producing a compile-classpath stub jar (method bodies stripped)
 * so intermediary-era shim mixins can be compiled with plain javac -- no loom.
 */
public final class JarRemapper {

    /**
     * An intermediary member id in a refmap: method_, field_ and comp_ (a record component's accessor; the
     * class files' pass already maps all three, the refmap pass took only the first two, leaving every mod
     * mixin that names a record accessor -- injector selector, @At target, @Invoker -- pointing at a name
     * 26.x does not have). Preceded by "L<owner>;" it is looked up under that owner first, as the class
     * files' pass does, so an id the table holds ambiguously still maps where the refmap says whose it is.
     */
    private static final java.util.regex.Pattern MEMBER_TOKEN =
        java.util.regex.Pattern.compile("(?:L([\\w/$]+);)?((?:method|field|comp)_\\d+)(\\([^)]*\\)[^\\s\"\\\\,]*)?");
    /**
     * An intermediary class name: net/minecraft/class_N with its nested classes, and an intermediary-named
     * nested class of a class whose own name is not intermediary (com/mojang/blaze3d/opengl/GlStateManager$class_1017
     * -> GlStateManager$BlendState; net/minecraft/util/profiling/jfr/event/...$class_N). The second form is
     * anchored at the two package roots that have such classes, so it never starts at a descriptor's "L"
     * and never takes a match from the first. A match the table does not hold maps to itself.
     */
    private static final java.util.regex.Pattern CLASS_TOKEN = java.util.regex.Pattern.compile(
        "net/minecraft/class_\\d+(?:\\$[\\w$]+)?|(?:com/mojang|net/minecraft)/(?:\\w+/)*\\w+\\$class_\\d+(?:\\$[\\w$]+)?");

    /**
     * Remaps a jar from the intermediary namespace into Mojang names using a
     * composed C/M/F table (compose_mojang.py). This is the conversion stage
     * for year-versioned targets (26.1+): those jars ship unobfuscated, so an
     * intermediary-named mod must be wholly renamed before any of its
     * references resolve. Intermediary member ids are globally unique, so
     * member maps are keyed by bare name; anything not matching
     * method_/field_ passes through untouched. Code is preserved (this runs
     * on real mod jars, not classpath stubs); non-class entries are copied
     * verbatim -- refmap JSONs still carry intermediary names and need their
     * own pass later.
     */
    /**
     * The composed inter->mojang table, and the one Remapper every consumer
     * of it must share: the whole-jar conversion pass, and the build gate that
     * predicts what a converted call site will look like (WiringCheck) -- a
     * gate that translated names its own way would pass wiring the converter
     * then emits differently.
     */
    public static final class Table {
        public final java.util.Map<String, String> classes = new java.util.HashMap<>();
        /** "interOwner.name" -> mojang member name */
        public final java.util.Map<String, String> byOwner = new java.util.HashMap<>();
        /** bare intermediary member name -> mojang name, ambiguous ids removed */
        public final java.util.Map<String, String> members = new java.util.HashMap<>();
        /**
         * the last {@link #remapWithTable} run's reflective strings ({@link #reflectStrings}): "translated ..." and
         * "unproven ..." lines, for the conversion report
         */
        public final java.util.List<String> reflectNotes = new java.util.ArrayList<>();
        /** every intermediary member id the table renames on some owner (lazily, {@link #renamesId}) */
        private java.util.Set<String> renamedIds;

        /** whether the table renames the member id anywhere: an id it never names is left alone by every rename */
        boolean renamesId(String id) {
            if (renamedIds == null) {
                java.util.Set<String> ids = new java.util.HashSet<>(members.keySet());
                ids.addAll(candidates.keySet());
                for (String k : byOwner.keySet()) {
                    ids.add(k.substring(k.lastIndexOf('.') + 1));
                }
                renamedIds = ids;
            }
            return renamedIds.contains(id);
        }
        /** how many bare ids were ambiguous (owner-qualified lookup only) */
        public int ambiguous;
        /** bare intermediary method id -> every mojang name the table gives it (more than one: reused ids) */
        public final java.util.Map<String, java.util.Set<String>> candidates = new java.util.HashMap<>();
        /**
         * The target release's methods ("owner" -> name + desc, mojang names) and supertypes, and the input jar's
         * own classes' supertypes (renamed): with them a reused id resolves by descriptor ({@link #remapper}).
         * Null when no target jar is known.
         */
        public java.util.Map<String, java.util.Set<String>> targetMethods;
        public java.util.Map<String, String[]> targetSupers;
        public final java.util.Map<String, String[]> jarSupers = new java.util.HashMap<>();
        /**
         * The input jar's classes (source names) -> the methods each declares under a non-token name, descriptor
         * renamed: a token the table would rename onto one of them keeps its token ({@link #nativeClash}).
         */
        public final java.util.Map<String, java.util.Set<String>> jarNativeMethods = new java.util.HashMap<>();
        /** the input jar's classes (source names) -> their superclass and interfaces (source names) */
        public final java.util.Map<String, String[]> jarSupersSource = new java.util.HashMap<>();

        /**
         * Whether renaming {@code owner}'s token {@code name} to {@code moj} lands on a method {@code owner} or a jar
         * superclass of it already declares under that very name with the same (renamed) descriptor: a mod that
         * declared both getSerializedName() and StringRepresentable.method_15434() (redeco Upholstery), or its own
         * loadAdditional(CompoundTag) beside BlockEntity.method_11014(CompoundTag) (no-tree-punching) -- the rename
         * would give the class one name+desc twice, a ClassFormatError when it loads. The token stays: the mod's
         * own method keeps answering under the target's name, the token under its own.
         */
        boolean nativeClash(String owner, String name, String moj, String mappedDesc) {
            if (moj.equals(name) || jarNativeMethods.isEmpty()) {
                return false;
            }
            String shape = moj + mappedDesc;
            java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(java.util.List.of(owner));
            java.util.Set<String> seen = new java.util.HashSet<>();
            while (!todo.isEmpty()) {
                String c = todo.poll();
                if (!seen.add(c)) {
                    continue;
                }
                java.util.Set<String> own = jarNativeMethods.get(c);
                if (own != null && own.contains(shape)) {
                    return true;
                }
                String[] sup = jarSupersSource.get(c);
                if (sup != null) {
                    todo.addAll(java.util.List.of(sup));
                }
            }
            return false;
        }
        /** the input jar's CenturyBridge classes' declared field names ({@link #remapper}: inherited facade fields) */
        public final java.util.Map<String, java.util.Set<String>> jarFields = new java.util.HashMap<>();
        /**
         * The target's Fabric API modules (a processedMods directory; null: none): their classes join the target's
         * methods and supertypes, so a CenturyBridge class overriding a vanilla method through a Fabric API interface
         * (the 1.20.4 layer's NoOverlay implements ModResourcePack, which extends PackResources) is seen to override
         * it ({@link #remapper}'s cbOverride) and takes the method's new name past the wall instead of keeping its
         * token (an AbstractMethodError at the first call). Read once ({@link #loadTargetLibraries}).
         */
        public Path targetLibraries;
        private java.util.Map<String, java.util.Set<String>> libMethods;
        private java.util.Map<String, String[]> libSupers;
        /**
         * The adapters of the step crossing the wall (its adapterHooks, and its override / convert hooks that name an
         * adapter): adapter interface -> each {under, required V members (name + desc, intermediary)}. A CenturyBridge class keeps its token-named members past the wall, and a method whose
         * vanilla shape changed at the wall no longer overrides anything, so the hierarchy rule leaves it under its
         * token too -- while the converter gave the class the step's adapter for exactly that method, whose abstract
         * method carries the table's name with V's shape (26.1's CbWallTintClient$LegacyBlockAndTintGetter.getShade):
         * the adapter stayed unimplemented. A token method of a class that has such an adapter among its supertypes
         * and that the adapter requires takes the table's name for it on the hook's {@code under} class; nothing else
         * is renamed by this rule (remapper's adapterMember).
         */
        public final java.util.Map<String, java.util.List<Object[]>> wallAdapters = new java.util.HashMap<>();

        /** registers one adapter hook of the wall step: see {@link #wallAdapters} */
        public void wallAdapter(String adapter, String under, java.util.Collection<String> requires) {
            wallAdapters.computeIfAbsent(adapter, k -> new java.util.ArrayList<>()).add(new Object[] {under, new java.util.HashSet<>(requires)});
        }

        /**
         * The input jar's mixins (class name as in the input -> its @Mixin targets, value and targets alike) and the
         * members each declares @Shadow / @Overwrite: filled by {@link #remapWithTable} before the rename, empty for
         * every other consumer. A mixin built without a refmap names the target's members by their intermediary ids
         * in its own declarations (a @Shadow field_11515) and in its own references to them (getfield
         * Mixin.field_11515): the owner is the mixin class, which no table row names, so they kept the token across
         * the wall -- for CenturyBridge's climbed layers always (their classes keep token names on purpose), for a
         * mod's whenever the id is ambiguous in the bare-name map -- and Mixin refused the shadow at apply ("not
         * located in the target class ... No refMap loaded"). With these, a member of a mixin class whose id the
         * table holds for one of the mixin's targets takes that target's name, and a declared @Shadow / @Overwrite
         * the targets do not hold falls back to the bare-name map as a mod's member does.
         */
        public final java.util.Map<String, java.util.List<String>> mixinTargets = new java.util.HashMap<>();
        public final java.util.Map<String, java.util.Set<String>> mixinShadows = new java.util.HashMap<>();
        /** each mixin class's declared member names (fields and methods) and its superclass, as in the input ({@link #mixinMember}) */
        public final java.util.Map<String, java.util.Set<String>> mixinDeclared = new java.util.HashMap<>();
        public final java.util.Map<String, String> mixinSupers = new java.util.HashMap<>();
        /** each mixin class's interfaces, as in the input ({@link #mixinMember}) */
        public final java.util.Map<String, java.util.List<String>> mixinInterfaces = new java.util.HashMap<>();

        /**
         * Each mixin's own methods (name + descriptor as in the input), @Shadow / @Overwrite ones excluded. A member a
         * layer mixin restores on its target under the V id (1.20.2 SlotBridge's setByPlayer(ItemStack), method_48931)
         * takes the table's name of the target's member of that id -- and when the target release declares a method of
         * that name and descriptor itself (26.1 Slot.setByPlayer(ItemStack): 1.20.2's one-argument form is back), the
         * mixin's method would replace vanilla's at apply. Such a method keeps its token ({@link #keepsToken}): vanilla's
         * stays, the restored body stays reachable by the name the layer's own references use, and references through
         * the target class reach vanilla's method by the table's name.
         */
        public final java.util.Map<String, java.util.Set<String>> mixinOwnMethods = new java.util.HashMap<>();

        /**
         * A CenturyBridge interface whose methods mixins implement on their targets (1.21.11 layer: rt/CbLegacyGameRules,
         * implemented by mixin CbGameRulesMixin on GameRules with the restored 1.21.10 members, method_20744 ...). The
         * interface is a CB class, so its tokens stay; the mixin's implementations take the target's names
         * ({@link #mixinMember}: visitGameRuleTypes), and on 26.1 a call through the interface was an
         * AbstractMethodError (GameRules no longer implemented it). An interface method every implementing class of the
         * jar implements in a mixin takes the name those mixins' methods take, when they all take the same one; with any
         * other implementer (a plain CB class keeps its tokens), or mixins disagreeing, it keeps its token.
         * Input names throughout: interface -> its method name + desc; interface -> the jar's classes implementing it
         * directly (mixin or not).
         */
        public final java.util.Map<String, java.util.Set<String>> cbInterfaceMethods = new java.util.HashMap<>();
        public final java.util.Map<String, java.util.List<String>> cbImplementers = new java.util.HashMap<>();
        /** CB interface -> its abstract methods (name + desc, input names) and its own superinterfaces ({@link #inheritedBridges}) */
        public final java.util.Map<String, java.util.Set<String>> cbInterfaceAbstract = new java.util.HashMap<>();
        public final java.util.Map<String, String[]> cbInterfaceSupers = new java.util.HashMap<>();

        /**
         * A layer mixin that makes its target implement a CenturyBridge interface whose abstract method the target
         * itself declared under the same id (the 1.20.2 layer's CbNetRealmsTaskBridge: LongRunningTask implements the
         * facade class_5221, 1.20.1's ErrorCallback, through LongRunningTask's own error, method_21067, opened by the
         * layer's access widener). Past the wall the target's method carries the table's name (26.1
         * LongRunningTask.error) while the interface keeps its token for mods' implementations and calls, so nothing
         * implemented it any more (AbstractMethodError). Each such method, as {name, desc, target, newName} (input
         * names): the mixin gets a public bridge under the token calling the target's renamed method ({@link #remap}).
         * Only when the mixin declares no method of that name and descriptor itself, the table renames the target's
         * member and the target release declares the new name with the renamed descriptor in the target's hierarchy,
         * the same for every target of the mixin.
         */
        java.util.List<String[]> inheritedBridges(String mixin, Remapper r) {
            java.util.List<String[]> out = new java.util.ArrayList<>();
            java.util.List<String> targets = mixinTargets.get(mixin);
            java.util.List<String> direct = mixinInterfaces.get(mixin);
            if (targets == null || targets.isEmpty() || direct == null || targetMethods == null) {
                return out;
            }
            java.util.Set<String> own = mixinOwnMethods.getOrDefault(mixin, java.util.Set.of());
            java.util.Set<String> seen = new java.util.HashSet<>();
            java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(direct);
            java.util.Set<String> done = new java.util.HashSet<>();
            while (!todo.isEmpty()) {
                String i = todo.poll();
                if (!done.add(i)) {
                    continue;
                }
                String[] sup = cbInterfaceSupers.get(i);
                if (sup != null) {
                    todo.addAll(java.util.Arrays.asList(sup));
                }
                for (String nd : cbInterfaceAbstract.getOrDefault(i, java.util.Set.of())) {
                    int p = nd.indexOf('(');
                    String name = nd.substring(0, p);
                    String desc = nd.substring(p);
                    if (!name.startsWith("method_") || own.contains(nd) || !seen.add(nd)) {
                        continue;
                    }
                    String agreed = null;
                    for (String t : targets) {
                        String n = byOwner.get(t + "." + name);
                        if (n == null) {
                            n = members.get(name);
                        }
                        if (n == null || n.equals(name) || !targetDeclares(r.map(t), n + r.mapMethodDesc(desc), new java.util.HashSet<>())) {
                            agreed = null;
                            break;
                        }
                        if (agreed != null && !agreed.equals(n)) {
                            agreed = null;
                            break;
                        }
                        agreed = n;
                    }
                    if (agreed != null) {
                        out.add(new String[] {name, desc, targets.get(0), agreed});
                    }
                }
            }
            return out;
        }

        private boolean targetDeclares(String owner, String nameDesc, java.util.Set<String> seen) {
            if (owner == null || !seen.add(owner)) {
                return false;
            }
            java.util.Set<String> ms = targetMethods.get(owner);
            if (ms != null && ms.contains(nameDesc)) {
                return true;
            }
            String[] sup = targetSupers.get(owner);
            if (sup != null) {
                for (String x : sup) {
                    if (targetDeclares(x, nameDesc, seen)) {
                        return true;
                    }
                }
            }
            return false;
        }

        String interfaceMember(String owner, String name, String desc, Remapper r) {
            java.util.Set<String> declared = cbInterfaceMethods.get(owner);
            if (declared == null || !declared.contains(name + desc)) {
                return null;
            }
            java.util.List<String> impls = cbImplementers.get(owner);
            if (impls == null || impls.isEmpty()) {
                return null;
            }
            String agreed = null;
            for (String c : impls) {
                if (!mixinTargets.containsKey(c)) {
                    return null;
                }
                java.util.Set<String> own = mixinOwnMethods.get(c);
                if (own == null || !own.contains(name + desc)) {
                    // implemented further up (a default, a superclass): the target's or another mixin's member
                    return null;
                }
                String moj = mixinMember(c, name);
                if (moj == null || keepsToken(c, name, desc, moj, r)) {
                    moj = name;
                }
                if (agreed != null && !agreed.equals(moj)) {
                    return null;
                }
                agreed = moj;
            }
            return agreed == null || agreed.equals(name) ? null : agreed;
        }

        /** whether mixin {@code owner}'s own method {@code name desc}, renamed to {@code moj}, would land on a target method */
        boolean keepsToken(String owner, String name, String desc, String moj, Remapper r) {
            java.util.Set<String> own = mixinOwnMethods.get(owner);
            if (own == null || moj.equals(name) || targetMethods == null || !own.contains(name + desc)) {
                return false;
            }
            String md = r.mapMethodDesc(desc);
            for (String t : mixinTargets.getOrDefault(owner, java.util.List.of())) {
                java.util.Set<String> ms = targetMethods.get(r.map(t));
                if (ms != null && ms.contains(moj + md)) {
                    return true;
                }
            }
            return false;
        }

        /**
         * The source release's methods (intermediary owner -> id -> its descriptors), when the conversion has the
         * source code jar ({@link #loadSource}). A selector naming a method by id alone was unique in intermediary
         * (every method its own id) and need not be in mojang names: 1.21.11's BlockItem has method_7710 (instance)
         * and method_7714 (static), both updateCustomBlockEntityTag on 26.1, and Mixin takes the first method of a
         * bare name -- a @Redirect aimed at the static's call scanned the instance method and found nothing
         * (Critical injection failure, the server did not start). Such a selector gets the descriptor its id had.
         */
        public java.util.Map<String, java.util.Map<String, java.util.Set<String>>> sourceMethods;

        public void loadSource(Path sourceJar) throws IOException {
            sourceMethods = new java.util.HashMap<>();
            try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(sourceJar))) {
                ZipEntry e;
                while ((e = zin.getNextEntry()) != null) {
                    if (!e.getName().endsWith(".class")) {
                        continue;
                    }
                    ClassReader cr = new ClassReader(zin.readAllBytes());
                    String owner = cr.getClassName();
                    cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                        @Override
                        public org.objectweb.asm.MethodVisitor visitMethod(int acc, String n, String d, String s, String[] x) {
                            if (n.startsWith("method_") || n.startsWith("comp_")) {
                                sourceMethods.computeIfAbsent(owner, k -> new java.util.HashMap<>())
                                    .computeIfAbsent(n, k -> new java.util.TreeSet<>()).add(d);
                            }
                            return null;
                        }
                    }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                }
            }
        }

        /**
         * The descriptor to write after a bare method id of {@code owner} renamed to {@code moj}: its source
         * descriptor, renamed, when the target class has more than one method of that name; null otherwise.
         */
        String disambiguation(String owner, String id, String moj) {
            if (sourceMethods == null || targetMethods == null || id.startsWith("field_")) {
                return null;
            }
            java.util.Set<String> descs = sourceMethods.getOrDefault(owner, java.util.Map.of()).get(id);
            if (descs == null || descs.size() != 1) {
                return null;
            }
            Remapper r = remapper();
            java.util.Set<String> ms = targetMethods.get(r.map(owner));
            if (ms == null) {
                return null;
            }
            int same = 0;
            for (String nd : ms) {
                if (nd.startsWith(moj + "(")) {
                    same++;
                }
            }
            return same > 1 ? r.mapMethodDesc(descs.iterator().next()) : null;
        }

        /** the target's name for a member of mixin {@code owner}, or null when it is not a mixin member the table maps */
        String mixinMember(String owner, String name) {
            java.util.List<String> targets = mixinTargets.get(owner);
            if (targets == null) {
                return null;
            }
            for (String t : targets) {
                String hit = byOwner.get(t + "." + name);
                if (hit != null) {
                    return hit;
                }
            }
            // a member of a vanilla interface the mixin class adds to its target (1.20.3 layer CbDownloadedPackSourceMixin
            // implements RepositorySource: its method_14453 is 26.1's loadPacks, else the merged class misses it)
            for (String i : mixinInterfaces.getOrDefault(owner, java.util.List.of())) {
                String hit = i.startsWith("top/dext/centurybridge/") ? null : byOwner.get(i + "." + name);
                if (hit != null) {
                    return hit;
                }
            }
            java.util.Set<String> shadows = mixinShadows.get(owner);
            if (shadows != null && shadows.contains(name)) {
                return members.get(name);
            }
            // a member the mixin class neither declares nor shadows: one it inherits from the vanilla class it extends
            // (javac names the mixin class as owner of this.get(..) in a mixin extending Settings, 1.21.2 layer
            // CbRestServerPropertiesMixin -> 26.1 NoSuchMethodError DedicatedServerProperties.method_16740): the
            // superclass's row, else the bare-name map, as a mod class's inherited call takes it
            java.util.Set<String> declared = mixinDeclared.get(owner);
            String sup = mixinSupers.get(owner);
            if (declared != null && !declared.contains(name) && sup != null && !sup.equals("java/lang/Object")
                    && !sup.startsWith("top/dext/centurybridge/")) {
                String hit = byOwner.get(sup + "." + name);
                return hit != null ? hit : members.get(name);
            }
            return null;
        }

        /**
         * Tokens only the step's facade classes declare keep their names across the wall. A class the
         * step retires into a CB facade ({@code classRenames} to a top/dext/centurybridge/ class) keeps
         * its members' intermediary names -- the facade declares them so, and a reference naming the
         * facade passes through untouched ({@link #remapper}) -- but a reference through a mod class
         * under it (a call of an inherited facade method, the mod's own override of one) names the mod
         * class, and the bare-name map gave it the vanilla member's mojang name: 1.21.11's
         * ExpirableValue.tick() is method_24913 on the facade, and a mod subclass's this.tick() went out
         * as tick (NoSuchMethodError), its override of it as tick (never called). A token declared by any
         * other class as well (an override family reaching vanilla classes) keeps its mapping.
         * <p>
         * Only a token the facade really declares under that name is kept. A facade written against the
         * target declares the member under the table's name instead when it overrides the target's own
         * (1.21.11 ArmorDyeRecipe.matches(CraftingInput, Level), method_17701, is 26.1 Recipe.matches on the
         * facade): there the token takes the table's rename, through the mod class and through the facade
         * alike ({@link #remapper}), or the mod's override kept the token and nothing called it (ORPHAN).
         * {@code declared}: each facade's member names, its CB superclasses' included ({@link #facadeMembers});
         * null when the facades are not known, and then every facade-only token is kept.
         */
        public Table keepFacadeTokens(java.util.Map<String, String> classRenames, java.util.Map<String, java.util.Set<String>> declared) {
            java.util.Set<String> facadeOwners = facadeOwners(classRenames);
            facadeOrigins.clear();
            for (String c : facadeOwners) {
                facadeOrigins.computeIfAbsent(classRenames.get(c), x -> new java.util.ArrayList<>()).add(c);
            }
            facadeDeclared = declared;
            java.util.Map<String, java.util.Set<String>> owners = new java.util.HashMap<>();
            for (String k : byOwner.keySet()) {
                int dot = k.lastIndexOf('.');
                owners.computeIfAbsent(k.substring(dot + 1), x -> new java.util.HashSet<>()).add(k.substring(0, dot));
            }
            for (var e : owners.entrySet()) {
                if (facadeOwners.containsAll(e.getValue()) && (declared == null || declaredByFacade(e.getValue(), classRenames, e.getKey()))) {
                    members.remove(e.getKey());
                }
            }
            return this;
        }

        private boolean declaredByFacade(java.util.Set<String> owners, java.util.Map<String, String> classRenames, String token) {
            for (String o : owners) {
                java.util.Set<String> d = facadeDeclared.get(classRenames.get(o));
                if (d != null && d.contains(token)) {
                    return true;
                }
            }
            return false;
        }

        /** facade class -> the intermediary classes classRenames retire into it ({@link #keepFacadeTokens}) */
        public final java.util.Map<String, java.util.List<String>> facadeOrigins = new java.util.HashMap<>();
        /** facade class -> the member names it declares or inherits from CB superclasses; null: not known */
        public java.util.Map<String, java.util.Set<String>> facadeDeclared;

        /**
         * A reference naming a facade: the token it names when the facade declares it so (or the facades are
         * not known); otherwise the table's rename of the member of the class retired into it (or of one it
         * inherits), null if none.
         */
        String facadeMember(String facade, String name) {
            java.util.List<String> origins = facadeOrigins.get(facade);
            if (origins == null || facadeDeclared == null) {
                return null;
            }
            java.util.Set<String> d = facadeDeclared.get(facade);
            if (d != null && d.contains(name)) {
                return null;
            }
            for (String o : origins) {
                String hit = byOwner.get(o + "." + name);
                if (hit != null) {
                    return hit;
                }
            }
            // a member the facade inherits from the target (26.1 CustomRecipe.isSpecial under ArmorDyeRecipe)
            return members.get(name);
        }

        /**
         * The member names of the CB classes among {@code classes} (internal name -> node), each with those
         * of its CB superclasses: what {@link #keepFacadeTokens} reads a facade's surface from.
         */
        public static java.util.Map<String, java.util.Set<String>> facadeMembers(java.util.Map<String, org.objectweb.asm.tree.ClassNode> classes) {
            java.util.Map<String, java.util.Set<String>> out = new java.util.HashMap<>();
            for (String n : classes.keySet()) {
                if (!n.startsWith("top/dext/centurybridge/")) {
                    continue;
                }
                java.util.Set<String> names = new java.util.HashSet<>();
                java.util.Set<String> seen = new java.util.HashSet<>();
                for (org.objectweb.asm.tree.ClassNode c = classes.get(n); c != null && seen.add(c.name);
                     c = c.superName != null ? classes.get(c.superName) : null) {
                    c.methods.forEach(m -> names.add(m.name));
                    c.fields.forEach(f -> names.add(f.name));
                }
                out.put(n, names);
            }
            return out;
        }

        /** {@link #facadeMembers} of a built layer jar's CB classes */
        public static java.util.Map<String, java.util.Set<String>> facadeMembers(Path layerJar) throws IOException {
            java.util.Map<String, org.objectweb.asm.tree.ClassNode> classes = new java.util.HashMap<>();
            try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(layerJar))) {
                ZipEntry e;
                while ((e = zin.getNextEntry()) != null) {
                    if (!e.getName().endsWith(".class") || !e.getName().startsWith("top/dext/centurybridge/")) {
                        continue;
                    }
                    org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
                    new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    classes.put(cn.name, cn);
                }
            }
            return facadeMembers(classes);
        }

        /** the classes a wiring's classRenames retire into CB facades */
        public static java.util.Set<String> facadeOwners(java.util.Map<String, String> classRenames) {
            java.util.Set<String> out = new java.util.HashSet<>();
            classRenames.forEach((k, v) -> {
                if (v.startsWith("top/dext/centurybridge/")) {
                    out.add(k);
                }
            });
            return out;
        }
        /**
         * The target release's game classes and whether each is an interface (null: not known). 26.1 made
         * some 1.21.11 interfaces classes (GpuDevice, CommandEncoder, RenderPass wrap a backend now) and
         * could do the reverse; a call keeps the invoke kind and the interface flag of the type it was
         * compiled against, which the JVM refuses on the other kind (IncompatibleClassChangeError at the
         * first call). With this set the rename gives every call, method handle and invokedynamic argument
         * the kind the target declares the owner as.
         */
        public java.util.Map<String, Boolean> targetKinds;

        public Remapper remapper() {
            return new Remapper() {
                @Override
                public String map(String internalName) {
                    String hit = classes.get(internalName);
                    if (hit != null) {
                        return hit;
                    }
                    // nested classes absent from the table (anonymous/synthetic)
                    // follow their outermost mapped enclosing class
                    int d = internalName.length();
                    while ((d = internalName.lastIndexOf('$', d - 1)) > 0) {
                        String outer = classes.get(internalName.substring(0, d));
                        if (outer != null) {
                            return outer + internalName.substring(d);
                        }
                    }
                    return internalName;
                }

                private String member(String owner, String name) {
                    // a mixin's own member standing for one of its target's (mixinTargets)
                    String mx = mixinMember(owner, name);
                    if (mx != null) {
                        return mx;
                    }
                    // our runtime classes (Statics, facades, tombstones) keep their
                    // token-named members: the converter wired call sites to them
                    // by exactly that name, and nothing in the game maps them
                    if (owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/")) {
                        // a facade not declaring the token answers to the table's name (keepFacadeTokens)
                        String f = facadeMember(owner, name);
                        return f != null ? f : name;
                    }
                    String hit = byOwner.get(owner + "." + name);
                    return hit != null ? hit : members.getOrDefault(name, name);
                }

                @Override
                public String mapMethodName(String owner, String name, String desc) {
                    // comp_NNNN: a record component accessor (intermediary names them
                    // apart from method_; the table lists them as methods)
                    if (!name.startsWith("method_") && !name.startsWith("comp_")) {
                        return name;
                    }
                    String ir = interfaceMember(owner, name, desc, this);
                    if (ir != null) {
                        return ir;
                    }
                    String ad = adapterMember(owner, name, desc);
                    if (ad != null) {
                        return ad;
                    }
                    String m = member(owner, name);
                    if (m.equals(name)) {
                        String ov = cbOverride(owner, name, desc);
                        if (ov != null) {
                            return ov;
                        }
                    }
                    if (keepsToken(owner, name, desc, m, this)) {
                        return name;
                    }
                    String res = targetMethods == null ? m : byDescriptor(owner, name, desc, m);
                    return nativeClash(owner, name, res, mapMethodDesc(desc)) ? name : res;
                }

                /** a CB class's token method a wall-step adapter among its supertypes requires: see {@link Table#wallAdapters} */
                private String adapterMember(String owner, String name, String desc) {
                    if (wallAdapters.isEmpty() || !(owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/"))) {
                        return null;
                    }
                    java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(java.util.List.of(owner));
                    java.util.Set<String> seen = new java.util.HashSet<>();
                    while (!todo.isEmpty()) {
                        String o = todo.poll();
                        if (!seen.add(o)) {
                            continue;
                        }
                        for (Object[] hook : wallAdapters.getOrDefault(o, java.util.List.of())) {
                            if (!((java.util.Set<?>) hook[1]).contains(name + desc)) {
                                continue;
                            }
                            String under = (String) hook[0];
                            String to = under == null ? null : byOwner.get(under + "." + name);
                            if (to == null && under != null) {
                                to = superMember(Table.this, under, name);
                            }
                            if (to == null) {
                                to = members.get(name);
                            }
                            if (to != null) {
                                return to;
                            }
                        }
                        String[] sup = jarSupers.get(o);
                        if (sup != null) {
                            for (String x : sup) {
                                if (x != null) {
                                    todo.add(x);
                                }
                            }
                        }
                    }
                    return null;
                }

                /**
                 * A CenturyBridge class (a climbing layer's facade) keeps its token-named members (member()), but one
                 * that extends a live game class and overrides one of its methods must take the method's new name,
                 * or the override is dead past the wall: the 1.21.6 layer's ItemTagsProvider facade overrides
                 * TagsProvider.method_49651 (createContentsProvider) to copy block tags into item tags, and under its
                 * token nothing called it. Renamed when the table's name for the token, with the renamed descriptor,
                 * is a method a target game class among the CB class's supertypes declares; the jar's sites naming
                 * the CB class take the same name (this method), so its callers stay matched.
                 */
                private String cbOverride(String owner, String name, String desc) {
                    if (targetMethods == null || !(owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/"))) {
                        return null;
                    }
                    java.util.Set<String> names = new java.util.LinkedHashSet<>();
                    String g = members.get(name);
                    if (g != null) {
                        names.add(g);
                    }
                    java.util.Set<String> cand = candidates.get(name);
                    if (cand != null) {
                        names.addAll(cand);
                    }
                    String md = mapMethodDesc(desc);
                    String only = null;
                    for (String n : names) {
                        if (n.equals(name) || !gameSuperDeclares(owner, n + md, new java.util.HashSet<>())) {
                            continue;
                        }
                        if (only != null && !only.equals(n)) {
                            return null; // two live members fit: not decidable here
                        }
                        only = n;
                    }
                    return only;
                }

                /** whether a game class among the supertypes of a CB class declares nameDesc (target names) */
                private boolean gameSuperDeclares(String owner, String nameDesc, java.util.Set<String> seen) {
                    String[] sup = jarSupers.get(owner);
                    if (sup == null || !seen.add(owner)) {
                        return false;
                    }
                    for (String x : sup) {
                        if (x.startsWith("top/dext/centurybridge/") || x.startsWith("centurybridge/")) {
                            if (gameSuperDeclares(x, nameDesc, seen)) {
                                return true;
                            }
                        } else if (declares(x, nameDesc, new java.util.HashSet<>())) {
                            return true;
                        }
                    }
                    return false;
                }

                /**
                 * An id intermediary gave two members of one hierarchy (1.21.11's RepeatingPlacement.count(random,
                 * pos) and getPositions(context, random, pos) are both method_14452) maps to two names; the
                 * table keys members by owner and name only, so a call through any other owner found no
                 * mapping and an override of the other member took the wrong name. When the name chosen is
                 * not a method the target declares with this (renamed) descriptor in the owner's hierarchy,
                 * the one candidate it does declare is taken.
                 */
                private String byDescriptor(String owner, String name, String desc, String m) {
                    java.util.Set<String> cand = candidates.get(name);
                    if (cand == null || cand.size() < 2 || owner.startsWith("top/dext/centurybridge/")) {
                        return m;
                    }
                    String mo = map(owner);
                    String md = mapMethodDesc(desc);
                    if (declares(mo, m + md, new java.util.HashSet<>())) {
                        return m;
                    }
                    String only = null;
                    for (String c : cand) {
                        if (declares(mo, c + md, new java.util.HashSet<>())) {
                            if (only != null) {
                                return m;
                            }
                            only = c;
                        }
                    }
                    return only != null ? only : m;
                }

                private boolean declares(String owner, String nameDesc, java.util.Set<String> seen) {
                    if (owner == null || !seen.add(owner)) {
                        return false;
                    }
                    java.util.Set<String> ms = targetMethods.get(owner);
                    if (ms != null && ms.contains(nameDesc)) {
                        return true;
                    }
                    String[] sup = targetSupers.containsKey(owner) ? targetSupers.get(owner) : jarSupers.get(owner);
                    if (sup != null) {
                        for (String x : sup) {
                            if (declares(x, nameDesc, seen)) {
                                return true;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public String mapFieldName(String owner, String name, String desc) {
                    // a record component's backing field carries the accessor's token
                    // and takes the accessor's mojang name
                    if (!name.startsWith("field_") && !name.startsWith("comp_")) {
                        return name;
                    }
                    String m = member(owner, name);
                    if (m.equals(name) && (owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/"))) {
                        String inh = cbInheritedField(owner, name, new java.util.HashSet<>());
                        if (inh != null) {
                            return inh;
                        }
                    }
                    return m;
                }

                /**
                 * A field token named through a CenturyBridge class of the jar that does not declare it: an earlier
                 * layer's facade (the 1.20.5 layer's GustDustParticle reading Particle's onGround, field_3862, as
                 * this.field_3862) inherits it from the game class it extends, and the JVM resolves the reference
                 * there -- under the game class's new name past the wall. The table's name of the token, when the
                 * first non-CenturyBridge class up the jar's hierarchy is reached with no CenturyBridge class on
                 * the way declaring the token; null otherwise (the CB class's own field keeps its name).
                 */
                private String cbInheritedField(String owner, String name, java.util.Set<String> seen) {
                    if (!seen.add(owner)) {
                        return null;
                    }
                    java.util.Set<String> declared = jarFields.get(owner);
                    String[] sup = jarSupers.get(owner);
                    if (declared == null || sup == null || declared.contains(name)) {
                        return null; // not a class of this jar (nothing known), or its own field
                    }
                    for (String x : sup) {
                        if (x == null) {
                            continue;
                        }
                        if (x.startsWith("top/dext/centurybridge/") || x.startsWith("centurybridge/")) {
                            String r = cbInheritedField(x, name, seen);
                            if (r != null) {
                                return r;
                            }
                        } else if (!x.equals("java/lang/Object") && !x.startsWith("java/")) {
                            String hit = members.get(name);
                            if (hit != null) {
                                return hit;
                            }
                        }
                    }
                    return null;
                }

                /**
                 * A lambda or method reference names the functional interface's method in its
                 * invokedynamic (LambdaMetafactory: the call site's name is the SAM's, the interface is
                 * the descriptor's return type). ASM's default keeps that name, so a 1.21.11 lambda for a
                 * vanilla interface crossed the wall still implementing method_NNNN -- an
                 * AbstractMethodError at the first call. Other bootstraps (string concatenation, record
                 * object methods, pattern switches) never carry a token name and stay as they are.
                 */
                @Override
                public String mapInvokeDynamicMethodName(String name, String desc) {
                    if (!name.startsWith("method_") && !name.startsWith("comp_")) {
                        return name;
                    }
                    org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(desc);
                    if (ret.getSort() != org.objectweb.asm.Type.OBJECT) {
                        return name;
                    }
                    String m = member(ret.getInternalName(), name);
                    if (m.equals(name)) {
                        String inh = cbInheritedSam(ret.getInternalName(), name);
                        if (inh != null) {
                            return inh;
                        }
                    }
                    return m;
                }

                /**
                 * A lambda whose functional interface is one of the jar's CenturyBridge interfaces that inherits its
                 * abstract method from a game interface (the 1.21 layer's RefreshableSearchTree extends SearchTree,
                 * class_1129, whose method_4810 is search past the wall): the SAM is the game interface's, under its
                 * new name. Walked over the jar's source-named hierarchy (jarSupersSource) to the first game types;
                 * the table's name for the token there (or on their supertypes), null when none names it.
                 */
                private String cbInheritedSam(String owner, String name) {
                    if (!(owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/"))) {
                        return null;
                    }
                    java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(java.util.List.of(owner));
                    java.util.Set<String> seen = new java.util.HashSet<>();
                    while (!todo.isEmpty()) {
                        String o = todo.poll();
                        if (!seen.add(o)) {
                            continue;
                        }
                        if (o.startsWith("top/dext/centurybridge/") || o.startsWith("centurybridge/")) {
                            String[] sup = jarSupersSource.get(o);
                            if (sup != null) {
                                todo.addAll(java.util.List.of(sup));
                            }
                            continue;
                        }
                        String hit = byOwner.get(o + "." + name);
                        if (hit == null) {
                            hit = superMember(Table.this, o, name);
                        }
                        if (hit != null) {
                            return hit;
                        }
                    }
                    return null;
                }
            };
        }
    }

    public static Table loadTable(Path tsv) throws IOException {
        Table t = new Table();
        // owner-qualified ("interOwner.name" -> moj) wins; the bare-name map
        // covers call sites whose owner is a subclass of the declaring class,
        // and drops the handful of ids the client/server merge left ambiguous
        // (same field_/method_ id, two unrelated mojang members)
        java.util.Set<String> ambiguous = new java.util.HashSet<>();
        for (String ln : Files.readAllLines(tsv)) {
            String[] p = ln.split("\t");
            if (p.length < 3) {
                continue;
            }
            if (p[0].equals("C")) {
                t.classes.put(p[1], p[2]);
            } else if (p[0].equals("M") || p[0].equals("F")) {
                String name = p[1].substring(p[1].lastIndexOf('.') + 1);
                String moj = p[2].substring(p[2].lastIndexOf('.') + 1);
                t.byOwner.put(p[1], moj);
                if (p[0].equals("M")) {
                    t.candidates.computeIfAbsent(name, x -> new java.util.TreeSet<>()).add(moj);
                }
                String prev = t.members.putIfAbsent(name, moj);
                if (prev != null && !prev.equals(moj)) {
                    ambiguous.add(name);
                }
            }
        }
        t.members.keySet().removeAll(ambiguous);
        t.ambiguous = ambiguous.size();
        return t;
    }

    public static void remapWithTable(Path tsv, Path inJar, Path outJar) throws IOException {
        remapWithTable(tsv, inJar, outJar, null);
    }

    /** as above; with {@code targetJar}, calls also take the invoke kind the target declares their owner as ({@link Table#targetKinds}) */
    public static void remapWithTable(Path tsv, Path inJar, Path outJar, Path targetJar) throws IOException {
        remapWithTable(loadTable(tsv), inJar, outJar, targetJar);
    }

    /** as above, with a loaded table (the conversion's, facade tokens kept: {@link Table#keepFacadeTokens}) */
    public static void remapWithTable(Table table, Path inJar, Path outJar, Path targetJar) throws IOException {
        table.reflectNotes.clear();
        try (java.io.InputStream in = Files.newInputStream(inJar)) {
            collectMixins(table, in);
        }
        if (targetJar != null && Files.exists(targetJar)) {
            table.targetKinds = loadKinds(targetJar);
            loadTarget(table, targetJar);
            loadTargetLibraries(table);
            loadJarSupers(table, inJar);
        }
        int nClasses;
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar));
             ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            nClasses = remapEntries(zin, zout, table);
        }
        System.out.println("renamed " + nClasses + " classes (table: " + table.classes.size()
            + " classes, " + table.byOwner.size() + " members, " + table.ambiguous
            + " ambiguous ids owner-only) -> " + outJar);
    }

    /**
     * Intermediary names in text renamed (a refmap; a mixin annotation's string): member ids first (under the
     * owner a preceding "L<owner>;" names), then class names.
     */
    static String renameText(String text, Table table) {
        return renameText(text, table, null);
    }

    /**
     * As above; {@code context}: the @Mixin targets of the class whose annotation the text is (null: none). An id
     * written without an owner (an @Accessor / @Invoker value, a method= selector) is looked up under each target
     * first, as the class files' pass looks up the mixin's own members ({@link Table#mixinMember}); a dotted class
     * name (Mixin's targets = "net.minecraft.class_N$class_M") is renamed like the internal form.
     */
    static String renameText(String text, Table table, java.util.List<String> context) {
        return renameText(text, table, context, false);
    }

    /** {@code fieldName}: the text names a field (an @Accessor value): never given a method descriptor */
    static String renameText(String text, Table table, java.util.List<String> context, boolean fieldName) {
        Remapper remapper = table.remapper();
        text = MEMBER_TOKEN.matcher(text).replaceAll(mr -> {
            String owner = mr.group(1);
            String id = mr.group(2);
            String desc = mr.group(3);
            String moj = owner != null ? table.byOwner.get(owner + "." + id) : null;
            String ctxOwner = null;
            if (owner == null && context != null) {
                for (String t : context) {
                    String hit = table.byOwner.get(t + "." + id);
                    if (hit != null) {
                        moj = hit;
                        ctxOwner = t;
                        break;
                    }
                }
            }
            // smixin20 (swall21): a member of a class the table does not map (a CenturyBridge facade or layer class,
            // top/dext/...) keeps the name that class declares -- the explicit owner, or, for a bare id, every @Mixin
            // target of the class -- instead of taking a game member's real name by id alone
            // Only CB classes: a JDK/library owner (an anchor rewritten to java/util/Iterator.method_N by context) still
            // takes the table's name by id, as before
            boolean foreign = moj == null && (owner != null ? cbClass(owner)
                : context != null && !context.isEmpty() && context.stream().allMatch(JarRemapper::cbClass));
            if (foreign) {
                moj = id;
            }
            if (moj == null) {
                moj = table.members.getOrDefault(id, id);
            }
            String descOwner = foreign ? null : owner != null ? owner : ctxOwner;
            if (descOwner != null && desc != null && table.targetMethods != null && !id.startsWith("field_")) {
                // a reused id resolves by the descriptor written after it, as the class files' pass does
                moj = remapper.mapMethodName(descOwner, id, desc);
            }
            if (descOwner != null && desc == null && !fieldName) {
                // a bare id that was unique in intermediary and is not in the target's names: Mixin would take
                // the first method of the name (Table.sourceMethods)
                String d = table.disambiguation(descOwner, id, moj);
                if (d != null) {
                    desc = d;
                }
            }
            return java.util.regex.Matcher.quoteReplacement((owner != null ? "L" + owner + ";" : "") + moj + (desc != null ? desc : ""));
        });
        text = DOTTED_CLASS_TOKEN.matcher(text).replaceAll(mr -> java.util.regex.Matcher.quoteReplacement(
            remapper.map(mr.group().replace('.', '/')).replace('/', '.')));
        return CLASS_TOKEN.matcher(text).replaceAll(
            mr -> java.util.regex.Matcher.quoteReplacement(remapper.map(mr.group())));
    }

    /** whether c (internal or dotted form) is a CenturyBridge class (a facade or layer class) */
    private static boolean cbClass(String c) {
        String n = c.replace('.', '/');
        return n.startsWith("top/dext/centurybridge/") || n.startsWith("centurybridge/");
    }

    /** an intermediary class name in Mixin's dotted form (@Mixin targets strings) */
    private static final java.util.regex.Pattern DOTTED_CLASS_TOKEN =
        java.util.regex.Pattern.compile("net\\.minecraft\\.class_\\d+(?:\\$[\\w$]+)?");

    /**
     * The input jar's mixin classes, their targets and their @Shadow / @Overwrite members ({@link Table#mixinTargets}),
     * nested jars included (they take the same table).
     */
    private static void collectMixins(Table table, java.io.InputStream in) throws IOException {
        ZipInputStream zin = new ZipInputStream(in);
        ZipEntry e;
        while ((e = zin.getNextEntry()) != null) {
            String n = e.getName();
            if (n.startsWith("META-INF/jars/") && n.endsWith(".jar")) {
                collectMixins(table, new java.io.ByteArrayInputStream(zin.readAllBytes()));
                continue;
            }
            if (!n.endsWith(".class")) {
                continue;
            }
            org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
            new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            if (cn.name.startsWith("top/dext/centurybridge/") && (cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
                java.util.Set<String> ms = new java.util.HashSet<>();
                cn.methods.forEach(m -> ms.add(m.name + m.desc));
                table.cbInterfaceMethods.put(cn.name, ms);
                java.util.Set<String> abs = new java.util.HashSet<>();
                cn.methods.forEach(m -> {
                    if ((m.access & org.objectweb.asm.Opcodes.ACC_ABSTRACT) != 0) {
                        abs.add(m.name + m.desc);
                    }
                });
                table.cbInterfaceAbstract.put(cn.name, abs);
                table.cbInterfaceSupers.put(cn.name, cn.interfaces.toArray(new String[0]));
            }
            if (cn.interfaces != null) {
                for (String i : cn.interfaces) {
                    if (i.startsWith("top/dext/centurybridge/")) {
                        // a sub-interface is listed too: Table.interfaceMember keeps the token for it (not a mixin)
                        table.cbImplementers.computeIfAbsent(i, x -> new java.util.ArrayList<>()).add(cn.name);
                    }
                }
            }
            org.objectweb.asm.tree.AnnotationNode mixin = annotation(cn.invisibleAnnotations, MIXIN_DESC);
            if (mixin == null) {
                mixin = annotation(cn.visibleAnnotations, MIXIN_DESC);
            }
            if (mixin == null || mixin.values == null) {
                continue;
            }
            java.util.List<String> targets = new java.util.ArrayList<>();
            for (int i = 0; i + 1 < mixin.values.size(); i += 2) {
                Object v = mixin.values.get(i + 1);
                if (v instanceof java.util.List<?> l) {
                    for (Object o : l) {
                        if (o instanceof org.objectweb.asm.Type t) {
                            targets.add(t.getInternalName());
                        } else if (o instanceof String s && "targets".equals(mixin.values.get(i))) {
                            targets.add(s.replace('.', '/'));
                        }
                    }
                }
            }
            java.util.Set<String> shadows = new java.util.HashSet<>();
            for (org.objectweb.asm.tree.FieldNode f : cn.fields) {
                if (shadowOrOverwrite(f.visibleAnnotations) || shadowOrOverwrite(f.invisibleAnnotations)) {
                    shadows.add(f.name);
                }
            }
            for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
                if (shadowOrOverwrite(m.visibleAnnotations) || shadowOrOverwrite(m.invisibleAnnotations)) {
                    shadows.add(m.name);
                }
            }
            table.mixinTargets.put(cn.name, targets);
            table.mixinShadows.put(cn.name, shadows);
            java.util.Set<String> own = new java.util.HashSet<>();
            for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
                if (!shadows.contains(m.name)) {
                    own.add(m.name + m.desc);
                }
            }
            table.mixinOwnMethods.put(cn.name, own);
            java.util.Set<String> declared = new java.util.HashSet<>();
            cn.fields.forEach(f -> declared.add(f.name));
            cn.methods.forEach(m -> declared.add(m.name));
            table.mixinDeclared.put(cn.name, declared);
            if (cn.superName != null) {
                table.mixinSupers.put(cn.name, cn.superName);
            }
            table.mixinInterfaces.put(cn.name, cn.interfaces);
        }
    }

    private static final String MIXIN_DESC = "Lorg/spongepowered/asm/mixin/Mixin;";

    private static org.objectweb.asm.tree.AnnotationNode annotation(java.util.List<org.objectweb.asm.tree.AnnotationNode> l, String desc) {
        if (l != null) {
            for (org.objectweb.asm.tree.AnnotationNode a : l) {
                if (a.desc.equals(desc)) {
                    return a;
                }
            }
        }
        return null;
    }

    private static boolean shadowOrOverwrite(java.util.List<org.objectweb.asm.tree.AnnotationNode> l) {
        return annotation(l, "Lorg/spongepowered/asm/mixin/Shadow;") != null
            || annotation(l, "Lorg/spongepowered/asm/mixin/Overwrite;") != null;
    }

    /**
     * The strings of a class's Mixin / MixinExtras annotations renamed like a refmap (upstream of the
     * ClassRemapper, which renames Type values only): selectors, @At targets, accessor names a mixin spells
     * in intermediary itself -- a mod built without a refmap, or what the engine's MixinBridge wrote into an
     * annotation in the analysis namespace at the wall step. A mod's development names (a refmap's keys)
     * hold no intermediary token and pass unchanged.
     */
    private static org.objectweb.asm.ClassVisitor mixinStrings(org.objectweb.asm.ClassVisitor next, Table table) {
        // wrap[1]: inside an @Accessor, whose value names a field (Advancement.name is a record field and a
        // method of the same name: a method descriptor written after it would make Mixin look for no field)
        java.util.function.Function<org.objectweb.asm.AnnotationVisitor, org.objectweb.asm.AnnotationVisitor>[] wrap =
            new java.util.function.Function[2];
        // the class being visited: its @Mixin targets are the owner of an id its annotations write without one
        java.util.List<String>[] context = new java.util.List[1];
        for (int k = 0; k < 2; k++) {
        final int w = k;
        wrap[k] = av -> new org.objectweb.asm.AnnotationVisitor(org.objectweb.asm.Opcodes.ASM9, av) {
            @Override
            public void visit(String name, Object value) {
                super.visit(name, value instanceof String s ? renameText(s, table, context[0], w == 1) : value);
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String name, String desc) {
                org.objectweb.asm.AnnotationVisitor n = super.visitAnnotation(name, desc);
                return n == null ? null : wrap[w].apply(n);
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitArray(String name) {
                org.objectweb.asm.AnnotationVisitor n = super.visitArray(name);
                return n == null ? null : wrap[w].apply(n);
            }
        };
        }
        java.util.function.BiFunction<String, org.objectweb.asm.AnnotationVisitor, org.objectweb.asm.AnnotationVisitor> top =
            (desc, av) -> av == null ? null
                : desc.equals("Lorg/spongepowered/asm/mixin/gen/Accessor;") ? wrap[1].apply(av)
                : desc.startsWith("Lorg/spongepowered/asm/mixin/") || desc.startsWith("Lcom/llamalad7/mixinextras/") ? wrap[0].apply(av) : av;
        return new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9, next) {
            @Override
            public void visit(int version, int access, String name, String sig, String superName, String[] ifs) {
                context[0] = table.mixinTargets.get(name);
                super.visit(version, access, name, sig, superName, ifs);
            }

            @Override
            public org.objectweb.asm.AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return top.apply(desc, super.visitAnnotation(desc, visible));
            }

            @Override
            public org.objectweb.asm.MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                return new org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM9, super.visitMethod(acc, name, desc, sig, ex)) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return top.apply(d, super.visitAnnotation(d, visible));
                    }
                };
            }

            @Override
            public org.objectweb.asm.FieldVisitor visitField(int acc, String name, String desc, String sig, Object value) {
                return new org.objectweb.asm.FieldVisitor(org.objectweb.asm.Opcodes.ASM9, super.visitField(acc, name, desc, sig, value)) {
                    @Override
                    public org.objectweb.asm.AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return top.apply(d, super.visitAnnotation(d, visible));
                    }
                };
            }
        };
    }

    /**
     * One jar layer. Bundled libraries under META-INF/jars/ are jars of their
     * own (the loader extracts and loads them like top-level mods) and get the
     * same rename recursively -- the converter rewrites them, BytecodeVerify
     * checks them, and until now the wall pass skipped them wholesale: every
     * jar-in-jar library crossed into 26.x still spelling intermediary names.
     */
    /** a nested jar marked "centurybridge:verbatim" (JarProcessor.verbatimLibrary) */
    private static boolean verbatim(byte[] jar) throws IOException {
        try (ZipInputStream nin = new ZipInputStream(new java.io.ByteArrayInputStream(jar))) {
            ZipEntry n;
            while ((n = nin.getNextEntry()) != null) {
                if (n.getName().equals("fabric.mod.json")) {
                    return top.dext.centurybridge.engine.JarProcessor.verbatimLibrary(nin.readAllBytes());
                }
            }
        }
        return false;
    }

    /** an intermediary class or member id as a string constant spells it */
    private static final java.util.regex.Pattern INTER_ID =
        java.util.regex.Pattern.compile("(method|field|comp)_\\d+|net[./]minecraft[./]class_\\d+([$][\\w$]+)?");

    /**
     * String constants naming intermediary classes or members that flow into reflection (Class.forName /
     * ClassLoader.loadClass, Class.getDeclaredMethod / getMethod / getDeclaredField / getField, MethodHandles.Lookup
     * find*, a layer's CbSuper.special(owner, name, type)), spelled as the target declares them: the rename rewrites
     * references in bytecode, never strings, so past the wall each of these finds nothing (a climbed layer's
     * Class.forName("net.minecraft.class_3258$class_8616"): ClassNotFoundException at 26.x). The constant is traced to
     * its call (SourceInterpreter: a single ldc reaching the name argument). A class name goes through the table's
     * classes; a member through the owner the call names when that is a class constant (Foo.class, or a forName of a
     * constant), else through the bare id when the table gives it exactly one name. An intermediary-shaped constant
     * reaching such a call that the table does not translate is left and listed as unproven ({@link
     * Table#reflectNotes}); a member the table does not rename (a token a layer keeps, a V member only a layer
     * restores) is that too. Null: nothing to change
     */
    static byte[] reflectStrings(ClassReader cr, Table table, Remapper remapper) {
        boolean any = false;
        for (int i = 1; i < cr.getItemCount() && !any; i++) {
            int off = cr.getItem(i);
            if (off > 0 && cr.readByte(off - 1) == 8) { // CONSTANT_String
                String s;
                try {
                    s = cr.readUTF8(off, new char[cr.getMaxStringLength()]);
                } catch (RuntimeException ex) {
                    continue;
                }
                any = s != null && INTER_ID.matcher(s).matches();
            }
        }
        if (!any) {
            return null;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        cr.accept(cn, 0);
        boolean changed = false;
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.instructions.size() == 0) {
                continue;
            }
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue>[] frames;
            try {
                frames = new org.objectweb.asm.tree.analysis.Analyzer<>(new org.objectweb.asm.tree.analysis.SourceInterpreter())
                    .analyze(cn.name, m);
            } catch (org.objectweb.asm.tree.analysis.AnalyzerException ex) {
                continue;
            }
            org.objectweb.asm.tree.AbstractInsnNode[] insns = m.instructions.toArray();
            for (int k = 0; k < insns.length; k++) {
                if (!(insns[k] instanceof org.objectweb.asm.tree.MethodInsnNode mi) || frames[k] == null) {
                    continue;
                }
                int[] role = reflectRole(mi);
                if (role == null) {
                    continue;
                }
                org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue> f = frames[k];
                int argc = Type.getArgumentTypes(mi.desc).length + (mi.getOpcode() == org.objectweb.asm.Opcodes.INVOKESTATIC ? 0 : 1);
                int base = f.getStackSize() - argc;
                org.objectweb.asm.tree.LdcInsnNode nameLdc = singleLdc(f.getStack(base + role[1]));
                if (nameLdc == null || !(nameLdc.cst instanceof String name) || !INTER_ID.matcher(name).matches()) {
                    continue;
                }
                String where = cn.name + "." + m.name + ": " + mi.owner.substring(mi.owner.lastIndexOf('/') + 1) + "." + mi.name + " \"" + name + "\"";
                String to = null;
                String why;
                if (role[2] == 'c') {
                    String internal = name.replace('.', '/');
                    String mapped = remapper.map(internal);
                    to = mapped.equals(internal) ? null : name.indexOf('/') >= 0 ? mapped : mapped.replace('/', '.');
                    why = "the table does not rename the class";
                } else {
                    String owner = role[0] < 0 ? null : classConstant(f.getStack(base + role[0]), frames, insns, m);
                    if (owner != null && (owner.startsWith("top/dext/centurybridge/") || owner.startsWith("centurybridge/"))) {
                        // CenturyBridge's own classes keep their token-named members past the wall (the remapper's
                        // rule); a facade not declaring the token answers to the table's name (keepFacadeTokens)
                        String fm = table.facadeMember(owner, name);
                        if (fm != null && !fm.equals(name)) {
                            nameLdc.cst = fm;
                            changed = true;
                            table.reflectNotes.add("reflection string translated past the wall: " + where + " -> \"" + fm + "\"");
                        }
                        continue;
                    }
                    if (owner != null) {
                        to = table.byOwner.get(owner + "." + name);
                        if (to == null) {
                            to = superMember(table, owner, name);
                        }
                    }
                    if (to == null) {
                        to = table.members.get(name);
                    }
                    why = owner == null ? "owner not a class constant, and the table gives the id more than one name"
                        : "the table renames no member " + name + " of " + owner + " or its supertypes";
                }
                if (to == null && role[2] != 'c' && !table.renamesId(name)) {
                    continue; // an id the table renames nowhere: no class past the wall spells it otherwise
                }
                if (to != null && !to.equals(name)) {
                    nameLdc.cst = to;
                    changed = true;
                    table.reflectNotes.add("reflection string translated past the wall: " + where + " -> \"" + to + "\"");
                } else if (to == null) {
                    table.reflectNotes.add("reflection string unproven past the wall: " + where + " (" + why + ")");
                }
            }
        }
        if (!changed) {
            return null;
        }
        ClassWriter w = new ClassWriter(0);
        cn.accept(w);
        return w.toByteArray();
    }

    /**
     * {owner argument index or -1 (none / the receiver of a Class method: index 0), name argument index, kind 'c' class
     * / 'm' method / 'f' field} of a reflective call, argument indices counting the receiver of an instance call
     */
    private static int[] reflectRole(org.objectweb.asm.tree.MethodInsnNode mi) {
        String o = mi.owner;
        String n = mi.name;
        if (o.equals("java/lang/Class")) {
            if (n.equals("forName") && mi.desc.startsWith("(Ljava/lang/String;")) return new int[] {-1, 0, 'c'};
            if (n.equals("getDeclaredMethod") || n.equals("getMethod")) return new int[] {0, 1, 'm'};
            if (n.equals("getDeclaredField") || n.equals("getField")) return new int[] {0, 1, 'f'};
            return null;
        }
        if (o.equals("java/lang/ClassLoader") && n.equals("loadClass") && mi.desc.startsWith("(Ljava/lang/String;")) {
            return new int[] {-1, 1, 'c'};
        }
        if (o.equals("java/lang/invoke/MethodHandles$Lookup")) {
            switch (n) {
                case "findVirtual", "findStatic", "findSpecial":
                    return new int[] {1, 2, 'm'};
                case "findGetter", "findSetter", "findStaticGetter", "findStaticSetter", "findVarHandle", "findStaticVarHandle":
                    return new int[] {1, 2, 'f'};
                default:
                    return null;
            }
        }
        if (o.endsWith("/CbSuper") && n.equals("special") && mi.desc.startsWith("(Ljava/lang/Class;Ljava/lang/String;")) {
            return new int[] {0, 1, 'm'};
        }
        return null;
    }

    private static org.objectweb.asm.tree.LdcInsnNode singleLdc(org.objectweb.asm.tree.analysis.SourceValue v) {
        if (v == null || v.insns.size() != 1) return null;
        return v.insns.iterator().next() instanceof org.objectweb.asm.tree.LdcInsnNode l ? l : null;
    }

    /** the internal name of the class a value always is: an ldc of a class, or Class.forName of a constant, else null */
    private static String classConstant(org.objectweb.asm.tree.analysis.SourceValue v,
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue>[] frames,
            org.objectweb.asm.tree.AbstractInsnNode[] insns, org.objectweb.asm.tree.MethodNode m) {
        if (v == null || v.insns.size() != 1) return null;
        org.objectweb.asm.tree.AbstractInsnNode src = v.insns.iterator().next();
        if (src instanceof org.objectweb.asm.tree.LdcInsnNode l && l.cst instanceof Type t && t.getSort() == Type.OBJECT) {
            return t.getInternalName();
        }
        if (src instanceof org.objectweb.asm.tree.MethodInsnNode mi && mi.owner.equals("java/lang/Class") && mi.name.equals("forName")) {
            int k = m.instructions.indexOf(mi);
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.SourceValue> f = frames[k];
            if (f == null) return null;
            int argc = Type.getArgumentTypes(mi.desc).length;
            org.objectweb.asm.tree.LdcInsnNode l = singleLdc(f.getStack(f.getStackSize() - argc));
            return l != null && l.cst instanceof String s ? s.replace('.', '/') : null;
        }
        return null;
    }

    /** the table's name for the id on a supertype of the owner (the input jar's and the target's hierarchy), or null */
    private static String superMember(Table table, String owner, String name) {
        if (table.targetSupers == null) return null;
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(java.util.List.of(owner));
        java.util.Set<String> seen = new java.util.HashSet<>();
        while (!todo.isEmpty()) {
            String o = todo.poll();
            if (!seen.add(o)) continue;
            String hit = table.byOwner.get(o + "." + name);
            if (hit != null) return hit;
            String[] up = table.targetSupers.get(o);
            if (up == null) up = table.targetSupers.get(table.classes.getOrDefault(o, o));
            if (up != null) {
                for (String s : up) {
                    if (s == null) continue;
                    todo.add(s);
                    // the target's supertypes are in its own names: back to the table's intermediary key
                    for (java.util.Map.Entry<String, String> e : table.classes.entrySet()) {
                        if (e.getValue().equals(s)) {
                            todo.add(e.getKey());
                            break;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static int remapEntries(ZipInputStream zin, ZipOutputStream zout, Table table) throws IOException {
        java.util.Map<String, String> byOwner = table.byOwner;
        java.util.Map<String, String> members = table.members;
        Remapper remapper = table.remapper();
        int nClasses = 0;
        java.util.Set<String> written = new java.util.HashSet<>();
        {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory()) {
                    continue;
                }
                // shaded fat jars sometimes carry literal duplicate entries;
                // first wins, same as every classloader would resolve them
                if (!written.add(e.getName())) {
                    continue;
                }
                byte[] data = zin.readAllBytes();
                if (e.getName().startsWith("META-INF/jars/") && e.getName().endsWith(".jar") && verbatim(data)) {
                    zout.putNextEntry(new ZipEntry(e.getName())); // a library a layer gives back whole: byte for byte
                    zout.write(data);
                    zout.closeEntry();
                    continue;
                }
                if (e.getName().startsWith("META-INF/jars/") && e.getName().endsWith(".jar")) {
                    java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
                    try (ZipInputStream nin = new ZipInputStream(new java.io.ByteArrayInputStream(data));
                         ZipOutputStream nout = new ZipOutputStream(bos)) {
                        nClasses += remapEntries(nin, nout, table);
                    }
                    zout.putNextEntry(new ZipEntry(e.getName()));
                    zout.write(bos.toByteArray());
                    zout.closeEntry();
                    continue;
                }
                if (!e.getName().endsWith(".class")) {
                    if (e.getName().endsWith(".accesswidener")) {
                        // the loader validates the AW namespace against the
                        // runtime before a single class loads: an intermediary
                        // header on a 26.x server is fatal at boot
                        String text = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        StringBuilder sb = new StringBuilder();
                        for (String ln : text.split("\n", -1)) {
                            String t = ln.trim();
                            if (t.startsWith("accessWidener")) {
                                ln = ln.replace("intermediary", "official");
                            } else if (!t.isEmpty() && !t.startsWith("#")) {
                                String[] w = ln.split("\\s+");
                                // accessible|extendable|mutable  kind  owner  [name  desc]
                                if (w.length >= 4 && (w[1].equals("method") || w[1].equals("field"))) {
                                    String hit = byOwner.get(w[2] + "." + w[3]);
                                    if (hit == null) {
                                        hit = members.getOrDefault(w[3], w[3]);
                                    }
                                    w[3] = hit;
                                }
                                if (w.length >= 3) {
                                    w[2] = remapper.map(w[2]);
                                }
                                if (w.length >= 5) {
                                    w[4] = CLASS_TOKEN.matcher(w[4]).replaceAll(
                                        mr -> java.util.regex.Matcher.quoteReplacement(
                                            remapper.map(mr.group())));
                                }
                                ln = String.join(" ", w);
                            }
                            sb.append(ln).append("\n");
                        }
                        text = sb.toString();
                        if (text.endsWith("\n")) {
                            text = text.substring(0, text.length() - 1);
                        }
                        data = text.getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    }
                    if (e.getName().endsWith(".json") && e.getName().contains("refmap")) {
                        // refmaps carry intermediary names as JSON strings
                        // ("Lnet/minecraft/class_310;method_1507()V"); rewrite
                        // them textually or every mixin target goes stale
                        String text = new String(data, java.nio.charset.StandardCharsets.UTF_8);
                        data = renameText(text, table).getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    }
                    zout.putNextEntry(new ZipEntry(e.getName()));
                    zout.write(data);
                    zout.closeEntry();
                    continue;
                }
                ClassReader cr = new ClassReader(data);
                ClassWriter cw = new ClassWriter(0);
                byte[] reflected = reflectStrings(cr, table, remapper);
                if (reflected != null) {
                    cr = new ClassReader(reflected);
                }
                java.util.List<String[]> bridges = table.inheritedBridges(cr.getClassName(), remapper);
                org.objectweb.asm.ClassVisitor sink = table.targetKinds == null ? cw : new KindFix(cw, table.targetKinds);
                if (!bridges.isEmpty()) {
                    sink = new InheritedBridges(sink, bridges, remapper);
                }
                cr.accept(mixinStrings(new ClassRemapper(sink, remapper), table), 0);
                String outName = remapper.map(cr.getClassName()) + ".class";
                if (!outName.equals(e.getName()) && !written.add(outName)) {
                    continue;
                }
                zout.putNextEntry(new ZipEntry(outName));
                zout.write(cw.toByteArray());
                zout.closeEntry();
                nClasses++;
            }
        }
        return nClasses;
    }

    /** adds {@link Table#inheritedBridges}' bridges to a mixin class (output names: the visitor sits after the remapper) */
    private static final class InheritedBridges extends org.objectweb.asm.ClassVisitor {
        private final java.util.List<String[]> bridges;
        private final Remapper remapper;
        private String owner;

        InheritedBridges(org.objectweb.asm.ClassVisitor cv, java.util.List<String[]> bridges, Remapper remapper) {
            super(org.objectweb.asm.Opcodes.ASM9, cv);
            this.bridges = bridges;
            this.remapper = remapper;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            owner = name;
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public void visitEnd() {
            for (String[] b : bridges) {
                String desc = remapper.mapMethodDesc(b[1]);
                String target = remapper.map(b[2]);
                org.objectweb.asm.MethodVisitor mv = super.visitMethod(org.objectweb.asm.Opcodes.ACC_PUBLIC, b[0], desc, null, null);
                mv.visitCode();
                mv.visitVarInsn(org.objectweb.asm.Opcodes.ALOAD, 0);
                int slot = 1;
                for (org.objectweb.asm.Type t : org.objectweb.asm.Type.getArgumentTypes(desc)) {
                    mv.visitVarInsn(t.getOpcode(org.objectweb.asm.Opcodes.ILOAD), slot);
                    slot += t.getSize();
                }
                mv.visitMethodInsn(org.objectweb.asm.Opcodes.INVOKEVIRTUAL, target, b[3], desc, false);
                org.objectweb.asm.Type ret = org.objectweb.asm.Type.getReturnType(desc);
                mv.visitInsn(ret.getOpcode(org.objectweb.asm.Opcodes.IRETURN));
                mv.visitMaxs(Math.max(slot, ret.getSize()), slot);
                mv.visitEnd();
                System.out.println("wall bridge: " + owner + "." + b[0] + desc + " -> " + target + "." + b[3]
                    + " (a CenturyBridge interface method the mixin's target implemented under the token)");
            }
            super.visitEnd();
        }
    }

    public static void remapToIntermediary(Path tiny, Path inJar, Path outJar) throws IOException {
        TinyMappings.Obf obf = TinyMappings.loadObf(tiny);

        // The tiny file names a method once, on its declaring class. An
        // override in a subclass carries the same obf name and must get the
        // same intermediary name -- tiny-remapper propagates this through the
        // hierarchy at runtime, and a stub that does not leaves Screen.render
        // obfuscated below Drawable.render: javac then refuses subclasses,
        // mixin-check misses targets, the self-recursion gate goes blind.
        java.util.Map<String, byte[]> entries = new java.util.LinkedHashMap<>();
        java.util.Map<String, String> superOf = new java.util.HashMap<>();
        java.util.Map<String, String[]> ifacesOf = new java.util.HashMap<>();
        // owner.name+desc of every private method: a private method is not inherited, so a subclass
        // method of the same obf name and descriptor is never "its" override (ChestBoat's
        // clearItemStacks once took Boat's private controlBoat's name: the loader names it after
        // ContainerEntity, which declares it)
        java.util.Set<String> privates = new java.util.HashSet<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(inJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory() || !e.getName().endsWith(".class")) {
                    continue; // classpath stub: classes only
                }
                byte[] data = zin.readAllBytes();
                entries.put(e.getName(), data);
                ClassReader cr = new ClassReader(data);
                superOf.put(cr.getClassName(), cr.getSuperName());
                ifacesOf.put(cr.getClassName(), cr.getInterfaces());
                String cls = cr.getClassName();
                cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                    @Override
                    public org.objectweb.asm.MethodVisitor visitMethod(int acc, String n, String d, String s, String[] x) {
                        if ((acc & org.objectweb.asm.Opcodes.ACC_PRIVATE) != 0) {
                            privates.add(cls + "." + n + d);
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }

        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                return obf.classes.getOrDefault(internalName, internalName);
            }

            @Override
            public String mapMethodName(String owner, String name, String desc) {
                String hit = obf.methods.get(owner + "." + name + desc);
                if (hit != null) {
                    return hit;
                }
                // climb as the JVM resolves: the superclass chain first, then the interfaces; a private
                // method of an ancestor is not inherited and owns nothing below it
                java.util.List<String> chain = new java.util.ArrayList<>();
                for (String cur = superOf.get(owner); cur != null; cur = superOf.get(cur)) {
                    chain.add(cur);
                }
                for (String cur : chain) {
                    String h = obf.methods.get(cur + "." + name + desc);
                    if (h != null && !privates.contains(cur + "." + name + desc)) {
                        return h;
                    }
                }
                java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
                java.util.Set<String> seen = new java.util.HashSet<>();
                todo.add(owner);
                todo.addAll(chain);
                while (!todo.isEmpty()) {
                    String cur = todo.poll();
                    if (!seen.add(cur)) {
                        continue;
                    }
                    String[] ifs = ifacesOf.get(cur);
                    if (ifs == null) {
                        continue;
                    }
                    for (String i : ifs) {
                        String h = obf.methods.get(i + "." + name + desc);
                        if (h != null && !privates.contains(i + "." + name + desc)) {
                            return h;
                        }
                        todo.add(i);
                    }
                }
                return name;
            }

            @Override
            public String mapFieldName(String owner, String name, String desc) {
                // a reference through a subclass (putfield Horse.at) names the
                // declaring superclass's field: climb like methods do
                for (String cur = owner; cur != null; cur = superOf.get(cur)) {
                    String h = obf.fields.get(cur + "." + name + ":" + desc);
                    if (h != null) {
                        return h;
                    }
                    String[] ifs = ifacesOf.get(cur);
                    if (ifs != null) {
                        for (String i : ifs) {
                            String hi = obf.fields.get(i + "." + name + ":" + desc);
                            if (hi != null) {
                                return hi;
                            }
                        }
                    }
                }
                return name;
            }
        };

        int classes = 0;
        try (ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(outJar))) {
            for (byte[] data : entries.values()) {
                ClassReader cr = new ClassReader(data);
                ClassWriter cw = new ClassWriter(0);
                // CB_REMAP_KEEP_CODE=1: keep bodies -- an analysis jar for injector
                // anchor checks (MixinRepair) where no loader-remapped runtime exists
                cr.accept(new ClassRemapper(cw, remapper), System.getenv("CB_REMAP_KEEP_CODE") != null
                    ? ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES
                    : ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                String newName = remapper.map(cr.getClassName());
                zout.putNextEntry(new ZipEntry(newName + ".class"));
                zout.write(cw.toByteArray());
                zout.closeEntry();
                classes++;
            }
        }
        System.out.println("remapped " + classes + " classes -> " + outJar);
    }

    /** the target's game classes' methods and supertypes ({@link Table#targetMethods}) */
    public static void loadTarget(Table table, Path jar) throws IOException {
        java.util.Map<String, java.util.Set<String>> methods = new java.util.HashMap<>();
        java.util.Map<String, String[]> supers = new java.util.HashMap<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                String n = e.getName();
                if (!n.endsWith(".class") || !(n.startsWith("net/minecraft/") || n.startsWith("com/mojang/"))) {
                    continue;
                }
                org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
                new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                java.util.Set<String> ms = new java.util.HashSet<>();
                for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
                    ms.add(m.name + m.desc);
                }
                methods.put(cn.name, ms);
                java.util.List<String> sup = new java.util.ArrayList<>();
                if (cn.superName != null) {
                    sup.add(cn.superName);
                }
                sup.addAll(cn.interfaces);
                supers.put(cn.name, sup.toArray(new String[0]));
            }
        }
        table.targetMethods = methods;
        table.targetSupers = supers;
    }

    /** the target's Fabric API classes into its methods and supertypes ({@link Table#targetLibraries}); game classes win */
    private static void loadTargetLibraries(Table table) throws IOException {
        if (table.targetLibraries == null || !Files.isDirectory(table.targetLibraries)) {
            return;
        }
        if (table.libMethods == null) {
            table.libMethods = new java.util.HashMap<>();
            table.libSupers = new java.util.HashMap<>();
            java.util.List<Path> jars;
            try (java.util.stream.Stream<Path> st = Files.list(table.targetLibraries)) {
                jars = st.filter(f -> f.toString().endsWith(".jar")).sorted().toList();
            }
            for (Path j : jars) {
                try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(j))) {
                    ZipEntry e;
                    while ((e = zin.getNextEntry()) != null) {
                        String n = e.getName();
                        if (!n.endsWith(".class") || n.startsWith("net/minecraft/") || n.startsWith("com/mojang/")) {
                            continue;
                        }
                        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
                        new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                        java.util.Set<String> ms = new java.util.HashSet<>();
                        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
                            ms.add(m.name + m.desc);
                        }
                        java.util.List<String> sup = new java.util.ArrayList<>();
                        if (cn.superName != null) {
                            sup.add(cn.superName);
                        }
                        sup.addAll(cn.interfaces);
                        table.libMethods.putIfAbsent(cn.name, ms);
                        table.libSupers.putIfAbsent(cn.name, sup.toArray(new String[0]));
                    }
                }
            }
        }
        table.libMethods.forEach(table.targetMethods::putIfAbsent);
        table.libSupers.forEach(table.targetSupers::putIfAbsent);
    }

    /**
     * An earlier climbing layer's CenturyBridge classes' supertypes, renamed past the wall, into {@link Table#jarSupers}
     * (the input jar's own classes, loaded per jar by {@link #loadJarSupers}, take precedence): a layer calling an
     * intermediary member through another layer's facade that inherits it from a game class (the 1.20.5 layer's
     * RealmsObjectSelectionList.method_25342 on the 1.21.2 layer's facade, AbstractSelectionList.getRowLeft) found
     * no game supertype for that facade, and the call kept the token ({@code cbOverride}). Only supertypes: the
     * layer's fields and native methods stay out of the input jar's own checks.
     */
    public static void loadLayerSupers(Table table, Path layerJar) throws IOException {
        Remapper r = table.remapper();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(layerJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class") || !e.getName().startsWith("top/dext/centurybridge/")) {
                    continue;
                }
                ClassReader cr = new ClassReader(zin.readAllBytes());
                java.util.List<String> sup = new java.util.ArrayList<>();
                if (cr.getSuperName() != null) {
                    sup.add(r.map(cr.getSuperName()));
                }
                for (String i : cr.getInterfaces()) {
                    sup.add(r.map(i));
                }
                table.jarSupers.putIfAbsent(r.map(cr.getClassName()), sup.toArray(new String[0]));
            }
        }
    }

    /** the input jar's classes' supertypes, renamed as the jar will be ({@link Table#jarSupers}) */
    private static void loadJarSupers(Table table, Path jar) throws IOException {
        Remapper r = table.remapper();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassReader cr = new ClassReader(zin.readAllBytes());
                java.util.List<String> sup = new java.util.ArrayList<>();
                if (cr.getSuperName() != null) {
                    sup.add(r.map(cr.getSuperName()));
                }
                for (String i : cr.getInterfaces()) {
                    sup.add(r.map(i));
                }
                table.jarSupers.put(r.map(cr.getClassName()), sup.toArray(new String[0]));
                java.util.List<String> supSrc = new java.util.ArrayList<>();
                if (cr.getSuperName() != null) {
                    supSrc.add(cr.getSuperName());
                }
                supSrc.addAll(java.util.List.of(cr.getInterfaces()));
                table.jarSupersSource.put(cr.getClassName(), supSrc.toArray(new String[0]));
                java.util.Set<String> nat = new java.util.HashSet<>();
                cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                    @Override
                    public org.objectweb.asm.MethodVisitor visitMethod(int acc, String n, String d, String sig, String[] x) {
                        if (!n.startsWith("method_") && !n.startsWith("comp_") && !n.startsWith("<")) {
                            nat.add(n + r.mapMethodDesc(d));
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                if (!nat.isEmpty()) {
                    table.jarNativeMethods.put(cr.getClassName(), nat);
                }
                if (cr.getClassName().startsWith("top/dext/centurybridge/") || cr.getClassName().startsWith("centurybridge/")) {
                    java.util.Set<String> fields = new java.util.HashSet<>();
                    cr.accept(new org.objectweb.asm.ClassVisitor(org.objectweb.asm.Opcodes.ASM9) {
                        @Override
                        public org.objectweb.asm.FieldVisitor visitField(int acc, String n, String d, String sig, Object v) {
                            fields.add(n);
                            return null;
                        }
                    }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    table.jarFields.put(cr.getClassName(), fields);
                }
            }
        }
    }

    /** every game class of a jar and whether it is an interface */
    private static java.util.Map<String, Boolean> loadKinds(Path jar) throws IOException {
        java.util.Map<String, Boolean> kinds = new java.util.HashMap<>();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                String n = e.getName();
                if (!n.endsWith(".class") || !(n.startsWith("net/minecraft/") || n.startsWith("com/mojang/"))) {
                    continue;
                }
                ClassReader cr = new ClassReader(zin.readAllBytes());
                kinds.put(cr.getClassName(), (cr.getAccess() & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0);
            }
        }
        return kinds;
    }

    /** gives calls on target game classes the invoke kind the target declares the owner as (runs after the rename) */
    private static final class KindFix extends org.objectweb.asm.ClassVisitor {
        private final java.util.Map<String, Boolean> kinds;

        KindFix(org.objectweb.asm.ClassVisitor cv, java.util.Map<String, Boolean> kinds) {
            super(org.objectweb.asm.Opcodes.ASM9, cv);
            this.kinds = kinds;
        }

        private org.objectweb.asm.Handle fix(org.objectweb.asm.Handle h) {
            Boolean itf = this.kinds.get(h.getOwner());
            if (itf == null || itf == h.isInterface()) {
                return h;
            }
            int tag = h.getTag();
            if (tag == org.objectweb.asm.Opcodes.H_INVOKEINTERFACE && !itf) {
                tag = org.objectweb.asm.Opcodes.H_INVOKEVIRTUAL;
            } else if (tag == org.objectweb.asm.Opcodes.H_INVOKEVIRTUAL && itf) {
                tag = org.objectweb.asm.Opcodes.H_INVOKEINTERFACE;
            }
            return new org.objectweb.asm.Handle(tag, h.getOwner(), h.getName(), h.getDesc(), itf);
        }

        private Object fixValue(Object v) {
            return v instanceof org.objectweb.asm.Handle h ? fix(h) : v;
        }

        @Override
        public org.objectweb.asm.MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] exc) {
            org.objectweb.asm.MethodVisitor mv = super.visitMethod(acc, name, desc, sig, exc);
            return mv == null ? null : new org.objectweb.asm.MethodVisitor(org.objectweb.asm.Opcodes.ASM9, mv) {
                @Override
                public void visitMethodInsn(int op, String owner, String n, String d, boolean itf) {
                    Boolean want = KindFix.this.kinds.get(owner);
                    if (want != null && want != itf) {
                        if (op == org.objectweb.asm.Opcodes.INVOKEINTERFACE && !want) {
                            op = org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
                        } else if (op == org.objectweb.asm.Opcodes.INVOKEVIRTUAL && want) {
                            op = org.objectweb.asm.Opcodes.INVOKEINTERFACE;
                        }
                        itf = want;
                    }
                    super.visitMethodInsn(op, owner, n, d, itf);
                }

                @Override
                public void visitInvokeDynamicInsn(String n, String d, org.objectweb.asm.Handle bsm, Object... args) {
                    Object[] fixed = new Object[args.length];
                    for (int i = 0; i < args.length; i++) {
                        fixed[i] = fixValue(args[i]);
                    }
                    super.visitInvokeDynamicInsn(n, d, fix(bsm), fixed);
                }

                @Override
                public void visitLdcInsn(Object value) {
                    super.visitLdcInsn(fixValue(value));
                }
            };
        }
    }
}
