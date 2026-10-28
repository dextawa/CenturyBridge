package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * Static check of everything Mixin resolves by NAME at PREPARE/apply time --
 * the part javac cannot see. A shim tree that compiles clean can still take
 * the whole game down on the first boot because an {@code @Accessor} names a
 * field that no longer exists, or an {@code @At} selector points at a method
 * whose descriptor moved. The loader reports exactly one such failure per
 * boot; this reports all of them against the real target jar.
 *
 * Checks, per mixin class:
 *   @Accessor / @Invoker   member must be declared on the target class
 *   @Shadow                field/method must be declared on the target
 *   @Overwrite             method must be declared on the target
 *   @Inject & friends      every "method" entry must resolve on the target
 *   @At(target = ...)      the selector's owner must declare the member
 *   non-private statics    Mixin throws InvalidMixinException at apply for a
 *                          class mixin's static method unless it is private,
 *                          synthetic or an @Overwrite
 *   covers                 a mixin declares the exact old shape, with the
 *                          static-ness of the ORIGINAL member (older stubs)
 *
 * And, in the target method's BODY (the release's code jar: the intermediary
 * stubs carry no code, so -Dcb.codeJar names it; without it the target jar is
 * read, which has code past the wall), what Mixin does at apply:
 *   injection points       every @At of type INVOKE / INVOKE_ASSIGN / FIELD / NEW
 *                          / INVOKE_STRING (HEAD / RETURN / TAIL too) is matched
 *                          against the instructions as Mixin's own InjectionPoint
 *                          classes match them: target owner/name/desc, opcode,
 *                          ordinal, args (ldc=, class=), and the injector's
 *                          slices (from = first match, to = last match). An
 *                          injector whose matches add up to less than its
 *                          require (the annotation's, else the mixin config's
 *                          injectors.defaultRequire, else Mixin's 0) is the
 *                          "Critical injection failure ... Scanned 0 target(s)"
 *                          that takes the game down; a require-0 injector that
 *                          matches nothing is listed as silent (the mixin
 *                          applies, the feature is gone). A method selector
 *                          resolving does not mean its call is still in there.
 *   handler signatures     @Inject: (target args, CallbackInfo[Returnable]) or
 *                          (CallbackInfo[Returnable]) alone, plus captured locals;
 *                          @Redirect: Injector.validateParams over the matched
 *                          instruction's shape (receiver + call args -> return,
 *                          field get/put, constructor) followed by a prefix of the
 *                          target's arguments, @Coerce honoured; @ModifyArg: the
 *                          argument type in and out, or every call argument in.
 *                          A name-only selector keeps resolving after a later
 *                          release changed the target's descriptor; the handler
 *                          no longer fits and Mixin throws InvalidInjectionException.
 * Point types this does not model (STORE, LOAD, CONSTANT, JUMP, ...) and targets
 * without code in the code source are counted and listed as unscanned, never
 * passed silently.
 *
 *   mixin-check <compiledShimDir> <targetJar> [<shims.json> [<inter-to-mojang.tsv> [<stubDir> <targetVersion>]]]
 *   -Dcb.codeJar=<jar with code>   -Dcb.mixinConfigDir=<dir with *.mixins.json>   -Dcb.fapi=<processedMods>
 */
public final class MixinCheck {

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String OVERWRITE = "Lorg/spongepowered/asm/mixin/Overwrite;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    private static final String INJECT_PKG = "Lorg/spongepowered/asm/mixin/injection/";
    /** MixinBridge.ARGS_WRAPPED: a W-shaped @Inject handler converting captured W arguments through wrap statics */
    private static final String ARGS_WRAPPED = "Ltop/dext/centurybridge/ArgsWrapped;";

    private final Map<String, ClassNode> jar = new HashMap<>();
    /** the packages the game jar has classes in: an owner elsewhere is a library's (brigadier, DFU, ...) */
    private final java.util.Set<String> jarPackages = new java.util.HashSet<>();
    private final List<String> problems = new ArrayList<>();

    /** mixin class -> its target internal names, for the covers check */
    private final Map<String, List<String>> mixinTargets = new HashMap<>();
    private final Map<String, ClassNode> mixinNodes = new HashMap<>();

    public static void run(Path classesDir, Path targetJar) throws IOException {
        run(classesDir, targetJar, null);
    }

    public static void run(Path classesDir, Path targetJar, Path shimsJson) throws IOException {
        run(classesDir, targetJar, shimsJson, null);
    }

    /** inter->mojang table for real-name targets: covers are keyed in the analysis namespace */
    private final Map<String, String> classMap = new HashMap<>();
    private final Map<String, String> memberMap = new HashMap<>();
    /** the converter's own wall rename (JarRemapper), so a cover is checked under the name the call site gets */
    private org.objectweb.asm.commons.Remapper wall;

    public static void run(Path classesDir, Path targetJar, Path shimsJson, Path table) throws IOException {
        run(classesDir, targetJar, shimsJson, table, null, null);
    }

    public static void run(Path classesDir, Path targetJar, Path shimsJson, Path table,
                           Path stubDir, String targetVersion) throws IOException {
        MixinCheck c = new MixinCheck();
        c.index(targetJar);
        c.indexFabricApi();
        c.indexLibraries();
        c.indexDependencies();
        c.indexCode(targetJar);
        c.indexExtraJars();
        c.readMixinConfigs(classesDir);
        if (stubDir != null && targetVersion != null) {
            c.sources = SourceStubs.before(stubDir, targetVersion);
        }
        if (table != null && Files.exists(table)) {
            // the conversion's descriptor choice for ids the table gives two names (Table.loadTarget)
            JarRemapper.Table t = JarRemapper.loadTable(table);
            JarRemapper.loadTarget(t, targetJar);
            c.wall = t.remapper();
            for (String ln : Files.readAllLines(table)) {
                String[] p = ln.split("\t");
                if (p.length < 3) {
                    continue;
                }
                if (p[0].equals("C")) {
                    c.classMap.put(p[1], p[2]);
                } else {
                    c.memberMap.put(p[1], p[2].substring(p[2].lastIndexOf('.') + 1));
                }
            }
        }
        int mixins = 0;
        List<ClassNode> own = new ArrayList<>();
        try (var stream = Files.walk(classesDir)) {
            for (Path p : (Iterable<Path>) stream.filter(f -> f.toString().endsWith(".class"))::iterator) {
                byte[] b = Files.readAllBytes(p);
                ClassNode cn = new ClassNode();
                new ClassReader(b).accept(cn, ClassReader.SKIP_CODE);
                boolean isMixin = find(cn.invisibleAnnotations, MIXIN) != null || find(cn.visibleAnnotations, MIXIN) != null;
                if (isMixin) {
                    // with code: a W-shaped handler's argument conversions are judged by the calls it makes (checkWrapped)
                    cn = new ClassNode();
                    new ClassReader(b).accept(cn, 0);
                }
                own.add(cn);
                // the layer's own classes are targets too (a mixin the climb retargeted onto a facade it ships);
                // the game jar wins for a name both have
                if (!isMixin && c.jar.putIfAbsent(cn.name, cn) == null) {
                    c.codeBytes.putIfAbsent(cn.name, b);
                }
            }
        }
        c.indexMerged(own);
        for (ClassNode cn : own) {
            if (c.check(cn)) {
                mixins++;
            }
        }
        if (shimsJson != null && top.dext.centurybridge.engine.Wiring.exists(shimsJson)) {
            c.checkCovers(shimsJson);
        }
        c.sources.close();
        System.out.println("mixin-check: " + mixins + " mixin classes against " + targetJar.getFileName()
            + ", " + c.problems.size() + " unresolved targets");
        for (String s : c.problems) {
            System.out.println("    " + s);
        }
        System.out.println("mixin-check bodies (" + c.codeSource + "): " + c.scanned + " injectors scanned, "
            + c.silent.size() + " silent (require 0, nothing matched), " + c.unscanned.size() + " unscanned");
        for (String s : c.silent) {
            System.out.println("    silent: " + s);
        }
        for (String s : c.unscanned) {
            System.out.println("    unscanned: " + s);
        }
        if (!c.unlisted.isEmpty()) {
            java.util.Collections.sort(c.unlisted);
            System.out.println("mixin-check: " + c.unlisted.size() + " mixin classes no config lists (never applied, not checked)");
            for (String s : c.unlisted) {
                System.out.println("    unlisted: " + s);
            }
        }
        String sitesOut = System.getProperty("cb.redirectSites");
        if (sitesOut != null && !sitesOut.isEmpty()) {
            Files.write(Path.of(sitesOut), c.redirectSites);
        }
        if (!c.problems.isEmpty()) {
            System.exit(1);
        }
    }

    /**
     * The target's game libraries (-Dcb.libs=<its libraries dir>, as wiring-check reads them): a mixin may target a library
     * class the game loads through Knot (authlib's records, whose fields a layer makes mutable), so its targets resolve against
     * the library jars too. The game jar wins for a class both carry; natives and Minecraft's own classes are skipped.
     */
    private void indexLibraries() throws IOException {
        String libs = System.getProperty("cb.libs");
        if (libs == null || libs.isEmpty() || !Files.isDirectory(Path.of(libs))) {
            return;
        }
        List<Path> jars;
        try (var s = Files.walk(Path.of(libs))) {
            jars = s.filter(f -> f.toString().endsWith(".jar") && !f.getFileName().toString().contains("-natives-")).sorted().toList();
        }
        for (Path lib : jars) {
            try (var z = new java.util.zip.ZipFile(lib.toFile())) {
                for (var en : java.util.Collections.list(z.entries())) {
                    String n = en.getName();
                    if (!n.endsWith(".class") || n.startsWith("META-INF/") || n.startsWith("net/minecraft/")) {
                        continue;
                    }
                    ClassNode cn = new ClassNode();
                    new ClassReader(z.getInputStream(en).readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                    if (jar.putIfAbsent(cn.name, cn) == null) {
                        int slash = cn.name.lastIndexOf('/');
                        jarPackages.add(slash < 0 ? "" : cn.name.substring(0, slash));
                    }
                }
            }
        }
    }

    /**
     * The separately released mods the layer declares in depends (-Dcb.depJars / -Dcb.layerModJson, {@link DepJars}, as
     * wiring-check reads them): a name the layer resolves in one of them resolves against its jar, as against a game
     * library; a jar the layer does not declare is a problem. The game jar and its libraries win for a class both carry.
     */
    private void indexDependencies() throws IOException {
        for (ClassNode cn : DepJars.read(problems).values()) {
            if (jar.putIfAbsent(cn.name, cn) == null) {
                int slash = cn.name.lastIndexOf('/');
                jarPackages.add(slash < 0 ? "" : cn.name.substring(0, slash));
            }
        }
    }

    /**
     * The target's Fabric API (-Dcb.fapi=<its processedMods>, as wiring-check reads it): a mixin may target a Fabric API
     * class, and an @At may name one; both resolve against its modules, bodies included. The game jar wins.
     */
    private void indexFabricApi() throws IOException {
        String fapi = System.getProperty("cb.fapi");
        if (fapi == null || fapi.isEmpty() || !Files.isDirectory(Path.of(fapi))) {
            return;
        }
        try (var s = Files.list(Path.of(fapi))) {
            for (Path j : (Iterable<Path>) s.filter(f -> f.toString().endsWith(".jar")).sorted()::iterator) {
                try (var z = new java.util.zip.ZipFile(j.toFile())) {
                    for (var en : java.util.Collections.list(z.entries())) {
                        if (!en.getName().endsWith(".class") || !en.getName().startsWith("net/fabricmc/")) {
                            continue;
                        }
                        byte[] b = z.getInputStream(en).readAllBytes();
                        ClassNode cn = new ClassNode();
                        new ClassReader(b).accept(cn, ClassReader.SKIP_CODE);
                        if (jar.putIfAbsent(cn.name, cn) == null) {
                            codeBytes.put(cn.name, b);
                            int slash = cn.name.lastIndexOf('/');
                            jarPackages.add(slash < 0 ? "" : cn.name.substring(0, slash));
                        }
                    }
                }
            }
        }
    }

    // ---- method bodies: what an injector actually finds at apply ----

    /** class bytes with code, by internal name: the release's code jar (or the target jar), plus Fabric API's */
    private final Map<String, byte[]> codeBytes = new HashMap<>();
    private final Map<String, ClassNode> codeNodes = new HashMap<>();
    private String codeSource = "no code";
    private int scanned;
    private final List<String> silent = new ArrayList<>();
    private final List<String> unscanned = new ArrayList<>();
    private final List<String> unlisted = new ArrayList<>();

    /**
     * Other mods' jars installed beside the checked one (-Dcb.extraJars=<jar>[<path separator><jar>...]): a mixin may
     * target their classes (a climbed layer's mixin retargeted onto a later layer's facade), bodies included.
     */
    private void indexExtraJars() throws IOException {
        String extra = System.getProperty("cb.extraJars");
        if (extra == null || extra.isEmpty()) {
            return;
        }
        List<ClassNode> extraMixins = new ArrayList<>();
        for (String j : extra.split(java.io.File.pathSeparator)) {
            if (j.isEmpty()) {
                continue;
            }
            try (var z = new java.util.zip.ZipFile(j)) {
                java.util.Set<String> listed = listedMixins(z);
                for (var en : java.util.Collections.list(z.entries())) {
                    if (!en.getName().endsWith(".class") || en.getName().startsWith("META-INF/")) {
                        continue;
                    }
                    byte[] b = z.getInputStream(en).readAllBytes();
                    ClassNode cn = new ClassNode();
                    new ClassReader(b).accept(cn, ClassReader.SKIP_CODE);
                    if (listed.contains(cn.name)) {
                        extraMixins.add(cn);
                    }
                    if (jar.putIfAbsent(cn.name, cn) == null) {
                        codeBytes.putIfAbsent(cn.name, b);
                    }
                }
            }
        }
        // the members those jars' listed class mixins merge onto game classes are there for this jar's mixins that apply
        // after them (a climbed layer's injector on a member a later layer restores: 1.21.2 MappedRegistry.bindTags)
        indexMerged(extraMixins);
    }

    /** the class mixins a jar's mixin configs list (fabric.mod.json "mixins" -> each config's package + mixins/client/server) */
    private static java.util.Set<String> listedMixins(java.util.zip.ZipFile z) throws IOException {
        java.util.Set<String> out = new java.util.HashSet<>();
        var fmj = z.getEntry("fabric.mod.json");
        if (fmj == null) {
            return out;
        }
        com.google.gson.JsonObject mod;
        try {
            mod = com.google.gson.JsonParser.parseString(new String(z.getInputStream(fmj).readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
        } catch (RuntimeException ex) {
            return out;
        }
        if (!mod.has("mixins")) {
            return out;
        }
        for (var ref : mod.getAsJsonArray("mixins")) {
            String name = ref.isJsonObject() ? ref.getAsJsonObject().get("config").getAsString() : ref.getAsString();
            var ce = z.getEntry(name);
            if (ce == null) {
                continue;
            }
            com.google.gson.JsonObject cfg;
            try {
                cfg = com.google.gson.JsonParser.parseString(new String(z.getInputStream(ce).readAllBytes(), java.nio.charset.StandardCharsets.UTF_8)).getAsJsonObject();
            } catch (RuntimeException ex) {
                continue;
            }
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) {
                    continue;
                }
                for (var e : cfg.getAsJsonArray(key)) {
                    out.add((pkg.isEmpty() ? "" : pkg + "/") + e.getAsString().replace('.', '/'));
                }
            }
        }
        return out;
    }
    /** mixin internal name -> its config's injectors.defaultRequire (Mixin's own default is 0) */
    private final Map<String, Integer> defaultRequire = new HashMap<>();
    private String currentMixin;

    private void indexCode(Path targetJar) throws IOException {
        String cj = System.getProperty("cb.codeJar");
        Path code = cj != null && !cj.isEmpty() ? Path.of(cj) : targetJar;
        if (!Files.exists(code)) {
            problems.add("code jar " + code + " not found: injection points cannot be matched");
            return;
        }
        codeSource = code.getFileName().toString();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(code))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.getName().endsWith(".class")) {
                    byte[] b = zin.readAllBytes();
                    String n = e.getName().substring(0, e.getName().length() - 6);
                    codeBytes.put(n, b); // the game's code wins over a Fabric API class of the same name
                }
            }
        }
    }

    private ClassNode codeClass(String name) {
        return codeNodes.computeIfAbsent(name, n -> {
            byte[] b = codeBytes.get(n);
            if (b == null) {
                return null;
            }
            ClassNode cn = new ClassNode();
            new ClassReader(b).accept(cn, 0);
            return cn;
        });
    }

    /**
     * The mixin configs (every *.json with a "package" next to the classes, and -Dcb.mixinConfigDir): which mixin a
     * config lists, and the config's injectors.defaultRequire -- the require of every injector that sets none.
     */
    private void readMixinConfigs(Path classesDir) throws IOException {
        List<Path> dirs = new ArrayList<>();
        dirs.add(classesDir);
        String extra = System.getProperty("cb.mixinConfigDir");
        if (extra != null && !extra.isEmpty() && Files.isDirectory(Path.of(extra))) {
            dirs.add(Path.of(extra));
        }
        for (Path d : dirs) {
            List<Path> jsons;
            try (var s = Files.list(d)) {
                jsons = s.filter(f -> f.toString().endsWith(".json")).toList();
            }
            for (Path j : jsons) {
                com.google.gson.JsonObject o;
                try {
                    var el = com.google.gson.JsonParser.parseString(Files.readString(j));
                    if (!el.isJsonObject() || !el.getAsJsonObject().has("package")) {
                        continue;
                    }
                    o = el.getAsJsonObject();
                } catch (RuntimeException ex) {
                    continue; // not a mixin config
                }
                String pkg = o.get("package").getAsString().replace('.', '/');
                int req = 0;
                if (o.has("injectors") && o.getAsJsonObject("injectors").has("defaultRequire")) {
                    req = o.getAsJsonObject("injectors").get("defaultRequire").getAsInt();
                }
                for (String sec : new String[] {"mixins", "client", "server"}) {
                    if (o.has(sec) && o.get(sec).isJsonArray()) {
                        for (var m : o.getAsJsonArray(sec)) {
                            defaultRequire.put(pkg + "/" + m.getAsString().replace('.', '/'), req);
                        }
                    }
                }
            }
        }
    }

    private void index(Path targetJar) throws IOException {
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(targetJar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(zin.readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                jar.put(cn.name, cn);
                int slash = cn.name.lastIndexOf('/');
                jarPackages.add(slash < 0 ? "" : cn.name.substring(0, slash));
            }
        }
    }

    /** @return true if the class was a mixin (and got checked). */
    private boolean check(ClassNode cn) {
        AnnotationNode mixin = find(cn.invisibleAnnotations, MIXIN);
        if (mixin == null) {
            mixin = find(cn.visibleAnnotations, MIXIN);
        }
        if (mixin == null) {
            return false;
        }
        // with mixin configs at hand, a mixin no config lists is never applied (the climb's converter delists the
        // ones it cannot carry): listed apart, not judged as if it loaded
        if (!defaultRequire.isEmpty() && !defaultRequire.containsKey(cn.name)) {
            unlisted.add(cn.name.substring(cn.name.lastIndexOf('/') + 1));
            return false;
        }
        List<String> targets = new ArrayList<>();
        Object value = get(mixin, "value");
        if (value instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof Type t) {
                    targets.add(t.getInternalName());
                }
            }
        }
        Object names = get(mixin, "targets");
        if (names instanceof List<?> l) {
            for (Object o : l) {
                targets.add(o.toString().replace('.', '/'));
            }
        }
        String me = cn.name.substring(cn.name.lastIndexOf('/') + 1);
        mixinTargets.put(cn.name, targets);
        mixinNodes.put(cn.name, cn);
        currentMixin = cn.name;
        // MixinApplicatorStandard.checkMethodVisibility: a whole-game crash at apply
        if ((cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) == 0) {
            for (MethodNode m : cn.methods) {
                if ((m.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0
                        && (m.access & (org.objectweb.asm.Opcodes.ACC_PRIVATE | org.objectweb.asm.Opcodes.ACC_SYNTHETIC)) == 0
                        && !m.name.equals("<clinit>")
                        && !has(m.visibleAnnotations, m.invisibleAnnotations, OVERWRITE)) {
                    problems.add(me + ": non-private static method " + m.name + m.desc
                        + " (Mixin refuses it at apply; a static shape belongs on Statics behind a staticRedirect)");
                }
            }
        }
        for (MethodNode m : cn.methods) {
            if (has(m.visibleAnnotations, m.invisibleAnnotations, ARGS_WRAPPED)) {
                checkWrapped(me, cn, m);
            }
        }
        for (String t : targets) {
            ClassNode target = jar.get(t);
            if (target == null) {
                problems.add(me + ": @Mixin target " + t + " not in jar");
                continue;
            }
            // a class mixin on an interface target dies at PREPARE (26.2 turned
            // BlockModel into an interface); only an interface mixin may target one
            if ((target.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0
                    && (cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) == 0) {
                problems.add(me + ": class mixin targets interface " + t);
            }
            for (FieldNode f : cn.fields) {
                if (has(f.visibleAnnotations, f.invisibleAnnotations, SHADOW)
                        && !declaresField(target, f.name, f.desc) && !mergedBefore(t, f.name + ":" + f.desc, cn.name)) {
                    problems.add(me + ": @Shadow field " + f.name + ":" + f.desc + " not declared on " + t);
                }
            }
            for (MethodNode m : cn.methods) {
                checkMethod(me, m, target);
                // A plain bridge overload that matches a method the target already
                // declares does not add an overload: Mixin merges over it, and if
                // the target's is static the JVM throws IncompatibleClassChangeError
                // at every existing call site (26.2: BreezeModel.createBodyLayer).
                if (!m.name.equals("<init>") && !m.name.equals("<clinit>")
                        && (m.access & org.objectweb.asm.Opcodes.ACC_STATIC) == 0
                        && (m.visibleAnnotations == null || m.visibleAnnotations.isEmpty())
                        && (m.invisibleAnnotations == null || m.invisibleAnnotations.isEmpty())) {
                    for (MethodNode tm : target.methods) {
                        if (tm.name.equals(m.name) && tm.desc.equals(m.desc)) {
                            problems.add(me + ": overload " + m.name + m.desc + " already declared on " + target.name
                                + ((tm.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0 ? " (static: ICCE at call sites)" : " (would be overwritten)"));
                        }
                    }
                }
            }
        }
        return true;
    }

    /**
     * CASE 18: a W-shaped @Inject handler the converter wrote for a mixinTargets entry or an override hook with "wrap"
     * (MixinBridge.ARGS_WRAPPED) hands the mod's handler V's value of a W argument through a wrap static. Each static
     * it calls outside its own class must resolve where the handler runs -- a static of that exact descriptor on a class
     * of the game, its libraries, Fabric API, the checked jar or the jars beside it -- and take exactly one argument; a
     * later release (climb_mixin_check) that loses one turns the handler into a NoSuchMethodError at its first call.
     */
    private void checkWrapped(String me, ClassNode mx, MethodNode h) {
        if (h.instructions == null || h.instructions.size() == 0) {
            problems.add(me + ": wrapped-argument handler " + h.name + h.desc + " has no body");
            return;
        }
        int wraps = 0;
        for (AbstractInsnNode in = h.instructions.getFirst(); in != null; in = in.getNext()) {
            if (!(in instanceof MethodInsnNode mi) || mi.getOpcode() != org.objectweb.asm.Opcodes.INVOKESTATIC
                    || mi.owner.equals(mx.name)) {
                continue;
            }
            ClassNode owner = jar.get(mi.owner);
            boolean found = owner != null && walk(owner, c -> c.methods.stream().anyMatch(m -> m.name.equals(mi.name)
                && m.desc.equals(mi.desc) && (m.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0));
            if (!found) {
                problems.add(me + ": wrapped-argument handler " + h.name + " calls " + mi.owner + "." + mi.name + mi.desc
                    + ", no static of that descriptor here");
            }
            if (Type.getArgumentTypes(mi.desc).length == 1 && Type.getReturnType(mi.desc).getSort() >= Type.ARRAY) {
                wraps++;
            }
        }
        if (wraps == 0) {
            problems.add(me + ": wrapped-argument handler " + h.name + h.desc + " converts no argument (marked ArgsWrapped)");
        }
    }

    /**
     * A "covers" entry tells the converter an old call shape is served by a
     * runtime overload, so the call site is left alone. If no mixin targeting
     * that owner (or an ancestor) declares the name+descriptor, the wiring is
     * lying: the converter reports "covered" and the game throws
     * NoSuchMethodError. Prunes that cut a method but not its cover did this.
     */
    private void checkCovers(Path shimsJson) throws IOException {
        var root = top.dext.centurybridge.engine.Wiring.read(shimsJson);
        if (!root.has("covers")) {
            return;
        }
        // the converter's view of a call site: every class of the descriptor goes
        // through the facade classRenames (a dead class we re-provide, or a vanilla
        // merge target) BEFORE the namespace wall, so a cover whose shape names
        // class_332 is served only by a mixin declaring the GuiGraphics FACADE --
        // vanilla's own render(GuiGraphics,...) is a different descriptor and
        // must not count as serving it
        Map<String, String> renames = new HashMap<>();
        if (root.has("classRenames")) {
            for (var e : root.getAsJsonObject("classRenames").entrySet()) {
                renames.put(e.getKey(), e.getValue().getAsString());
            }
        }
        var facade = new org.objectweb.asm.commons.SimpleRemapper(renames);
        // a call shape the converter itself rewrites (lookupHooks: append the
        // registry lookup, call the new descriptor) never reaches a mixin
        Map<String, String> hooked = new HashMap<>();
        if (root.has("lookupHooks")) {
            for (var e : root.getAsJsonObject("lookupHooks").entrySet()) {
                hooked.put(e.getKey(), e.getValue().getAsJsonArray().get(0).getAsString());
            }
        }
        int missing = 0;
        for (var el : root.getAsJsonArray("covers")) {
            String key = el.getAsString();
            int paren = key.indexOf('(');
            // the owner/name split is the last '.' BEFORE the descriptor: a '/'
            // inside the descriptor's class names is not the end of the owner
            // (searching after the last '/' skipped every cover with a reference
            // parameter -- the gate was passing on nothing)
            int dot = paren < 0 ? -1 : key.lastIndexOf('.', paren);
            if (dot < 0 || paren < 0) {
                continue; // field-shaped cover: not a method overload
            }
            String owner = key.substring(0, dot);
            String name = key.substring(dot + 1, paren);
            if (key.substring(paren).equals(hooked.get(name))) {
                continue; // rewritten by the converter, not served by an overload
            }
            Integer orig = sources.methodAccess(owner, name, key.substring(paren));
            String desc = facade.mapMethodDesc(key.substring(paren));
            if (!classMap.isEmpty()) {
                // real-name target: the bridge declares mojang names for members
                // the table knows, tokens for the rest (see translate_tree.py)
                // as the converter renames it (owner-qualified, else the bare token: a call through a
                // subclass of the declaring class -- 1.21.11 Minecraft.setWindowActive is
                // WindowEventHandler's method_15995, a row the table has only for the interface)
                name = wall != null ? wall.mapMethodName(owner, name, key.substring(paren)) : memberMap.getOrDefault(owner + "." + name, name);
            }
            // an owner merged into a surviving vanilla class carries its covers
            // there; one renamed onto a facade we ship is checked by direct target
            owner = renames.getOrDefault(owner, owner);
            if (!classMap.isEmpty()) {
                owner = classMap.getOrDefault(owner, owner);
                desc = java.util.regex.Pattern.compile("L([^;]+);").matcher(desc).replaceAll(
                    mr -> "L" + java.util.regex.Matcher.quoteReplacement(classMap.getOrDefault(mr.group(1), mr.group(1))) + ";");
            }
            // the converter leaves a covered call site alone, opcode included: a
            // static original is called with invokestatic, which no mixin-added
            // (necessarily instance) method answers -- IncompatibleClassChangeError
            if (orig != null && (orig & org.objectweb.asm.Opcodes.ACC_STATIC) != 0) {
                ClassNode native_ = jar.get(owner);
                final String sName = name;
                final String sDesc = desc;
                if (native_ != null && walk(native_, cn -> cn.methods.stream().anyMatch(m -> m.name.equals(sName)
                        && m.desc.equals(sDesc) && (m.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0))) {
                    continue; // the shape came back on the target as a static: served natively
                }
                List<String> by = new ArrayList<>();
                for (var e : mixinNodes.entrySet()) {
                    for (MethodNode m : e.getValue().methods) {
                        if (m.name.equals(name) && m.desc.equals(desc)) {
                            by.add(e.getKey());
                        }
                    }
                }
                problems.add("cover of a static method (call sites are invokestatic; serve it with a staticRedirect): " + key
                    + (by.isEmpty() ? "" : " [declared by " + String.join(",", by) + " as " + name + desc + "]"));
                missing++;
                continue;
            }
            // a bridge on an ancestor serves subclass call sites (the converter
            // climbs the same way), and an interface mixin's default method
            // serves every implementor (one multi-target mixin covered seven
            // mouseScrolled subclasses through their shared interface) -- so
            // walk superclasses AND interfaces, and match the exact name+desc:
            // a same-name overload of another arity is not this shape
            final String fName = name;
            final String fDesc = desc;
            java.util.function.Predicate<ClassNode> bridged = cn -> {
                for (var e : mixinTargets.entrySet()) {
                    if (!e.getValue().contains(cn.name)) {
                        continue;
                    }
                    for (MethodNode m : mixinNodes.get(e.getKey()).methods) {
                        if (m.name.equals(fName) && m.desc.equals(fDesc)) {
                            return true;
                        }
                    }
                }
                return false;
            };
            boolean served = false;
            ClassNode start = jar.get(owner);
            if (start != null) {
                served = walk(start, bridged);
            } else {
                // owner not in the target jar (a facade or a dead class): only a
                // mixin naming it directly can serve
                for (var e : mixinTargets.entrySet()) {
                    if (e.getValue().contains(owner)) {
                        for (MethodNode m : mixinNodes.get(e.getKey()).methods) {
                            if (m.name.equals(name) && m.desc.equals(desc)) {
                                served = true;
                            }
                        }
                    }
                }
            }
            // the target itself may declare the exact shape (cover kept for a
            // symbol that came back): that serves the call site too
            if (!served) {
                ClassNode t = jar.get(owner);
                served = t != null && hasMethod(t, name, desc);
            }
            if (!served) {
                problems.add("cover without a bridge: " + key);
                missing++;
            }
        }
        if (missing > 0) {
            System.out.println("covers: " + missing + " entries name a shape no mixin provides");
        }
    }

    /** game class -> name+desc -> the lowest priority of a layer class mixin merging a plain method of that shape onto it */
    private final Map<String, Map<String, Integer>> merged = new HashMap<>();
    private final Map<String, Integer> priorities = new HashMap<>();

    /**
     * The plain methods the layer's class mixins merge onto game classes (a restored V member, a private static one
     * included -- Mixin merges a mixin's private statics under their names): an @Invoker naming one resolves when the
     * merging mixin applies first (lower priority), as Mixin applies a target's mixins in priority order.
     */
    private void indexMerged(List<ClassNode> own) {
        for (ClassNode cn : own) {
            AnnotationNode mixin = find(cn.invisibleAnnotations, MIXIN);
            if (mixin == null) {
                mixin = find(cn.visibleAnnotations, MIXIN);
            }
            if (mixin == null) {
                continue;
            }
            Object pr = get(mixin, "priority");
            int priority = pr instanceof Integer i ? i : 1000;
            priorities.put(cn.name, priority);
            if ((cn.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
                continue;
            }
            List<String> targets = new ArrayList<>();
            if (get(mixin, "value") instanceof List<?> l) {
                for (Object o : l) {
                    if (o instanceof Type t) {
                        targets.add(t.getInternalName());
                    }
                }
            }
            if (get(mixin, "targets") instanceof List<?> l) {
                for (Object o : l) {
                    targets.add(o.toString().replace('.', '/'));
                }
            }
            for (MethodNode m : cn.methods) {
                if (m.name.startsWith("<") || (m.access & (org.objectweb.asm.Opcodes.ACC_ABSTRACT | org.objectweb.asm.Opcodes.ACC_SYNTHETIC)) != 0
                        || m.visibleAnnotations != null && !m.visibleAnnotations.isEmpty()
                        || m.invisibleAnnotations != null && m.invisibleAnnotations.stream().anyMatch(a -> a.desc.startsWith("Lorg/spongepowered/"))) {
                    continue;
                }
                for (String t : targets) {
                    merged.computeIfAbsent(t, k -> new HashMap<>()).merge(m.name + m.desc, priority, Math::min);
                }
            }
            // a field the mixin merges as it is (no Mixin annotation: not a @Shadow, not a @Unique Mixin may rename),
            // keyed "name:desc": a later mixin's @Shadow of it resolves (Mixin applies in priority order onto the
            // target's node; a layer restoring a field W dropped, shadowed by a climbed earlier layer)
            for (FieldNode f : cn.fields) {
                if ((f.access & org.objectweb.asm.Opcodes.ACC_SYNTHETIC) != 0
                        || f.visibleAnnotations != null && !f.visibleAnnotations.isEmpty()
                        || f.invisibleAnnotations != null && f.invisibleAnnotations.stream().anyMatch(an -> an.desc.startsWith("Lorg/spongepowered/"))) {
                    continue;
                }
                for (String t : targets) {
                    merged.computeIfAbsent(t, k -> new HashMap<>()).merge(f.name + ":" + f.desc, priority, Math::min);
                }
            }
        }
    }

    /** a method of that name (and descriptor, when given) merged onto the target by a mixin applying before this one */
    private boolean mergedForInjection(String target, String name, String desc, String mixin) {
        int own = priorities.getOrDefault(mixin, 1000);
        for (Map.Entry<String, Integer> e : merged.getOrDefault(target, Map.of()).entrySet()) {
            String k = e.getKey();
            if (e.getValue() < own && (desc != null ? k.equals(name + desc) : k.startsWith(name + "("))) {
                return true;
            }
        }
        return false;
    }

    private boolean mergedBefore(String target, String nameDesc, String invokerMixin) {
        Integer by = merged.getOrDefault(target, Map.of()).get(nameDesc);
        return by != null && by < priorities.getOrDefault(invokerMixin, 1000);
    }

    private void checkMethod(String me, MethodNode m, ClassNode target) {
        List<AnnotationNode> all = new ArrayList<>();
        if (m.visibleAnnotations != null) {
            all.addAll(m.visibleAnnotations);
        }
        if (m.invisibleAnnotations != null) {
            all.addAll(m.invisibleAnnotations);
        }
        for (AnnotationNode a : all) {
            switch (a.desc) {
                case SHADOW -> {
                    if (!declaresMethod(target, m.name, m.desc)) {
                        problems.add(me + ": @Shadow method " + m.name + m.desc + " not declared on " + target.name);
                    }
                }
                case OVERWRITE -> {
                    if (!hasMethod(target, m.name, m.desc)) {
                        problems.add(me + ": @Overwrite " + m.name + m.desc + " not declared on " + target.name);
                    }
                }
                case ACCESSOR -> {
                    String name = str(get(a, "value"));
                    boolean setter = Type.getReturnType(m.desc).getSort() == Type.VOID;
                    String fdesc = setter ? Type.getArgumentTypes(m.desc)[0].getDescriptor()
                                          : Type.getReturnType(m.desc).getDescriptor();
                    if (name == null) {
                        name = derive(m.name, "get", "is", "set");
                    }
                    if (name == null || !hasField(target, name, fdesc)) {
                        problems.add(me + ": @Accessor " + m.name + " -> " + target.name + "." + name + ":" + fdesc + " missing");
                    }
                }
                case INVOKER -> {
                    String name = str(get(a, "value"));
                    if (name == null) {
                        name = derive(m.name, "call", "invoke", "new", "create");
                    }
                    // a constructor invoker (@Invoker("<init>"), static, returning the target type)
                    // binds the constructor taking the invoker's arguments
                    String want = "<init>".equals(name)
                        ? m.desc.substring(0, m.desc.lastIndexOf(')') + 1) + "V" : m.desc;
                    // a selector with its descriptor (the climb's converter writes "name(desc)ret" when it renames an
                    // invoked method): name and descriptor as given -- a 1.21.2 client applied one on BlockBehaviour without a WARN
                    if (name != null && name.contains("(") && !name.startsWith("<init>")) {
                        want = name.substring(name.indexOf('('));
                        name = name.substring(0, name.indexOf('('));
                    }
                    if ("<init>".equals(name)) {
                        // Mixin applies a constructor invoker only when it returns the target class itself
                        // (not a supertype) and the target declares that constructor (constructors are not
                        // inherited): otherwise the mixin is refused at apply
                        String ret = m.desc.substring(m.desc.lastIndexOf(')') + 1);
                        if (!ret.equals("L" + target.name + ";")) {
                            problems.add(me + ": @Invoker(\"<init>\") " + m.name + " returns " + ret + ", not the target " + target.name);
                        } else if (!declaresMethod(target, "<init>", want)) {
                            problems.add(me + ": @Invoker " + m.name + " -> " + target.name + ".<init>" + want + " missing");
                        }
                    } else if (name == null || !hasMethod(target, name, want) && !mergedBefore(target.name, name + want, currentMixin)) {
                        problems.add(me + ": @Invoker " + m.name + " -> " + target.name + "." + name + want + " missing");
                    }
                }
                default -> {
                    if (a.desc.startsWith(INJECT_PKG)) {
                        checkInjection(me, m, a, target);
                    }
                }
            }
        }
    }

    /** injectors that take exactly one @At (in the Mixin API the runtimes load) */
    private static final java.util.Set<String> SINGLE_AT = java.util.Set.of(
        "Lorg/spongepowered/asm/mixin/injection/Redirect;",
        "Lorg/spongepowered/asm/mixin/injection/ModifyVariable;",
        "Lorg/spongepowered/asm/mixin/injection/ModifyArg;",
        "Lorg/spongepowered/asm/mixin/injection/ModifyArgs;");

    private void checkInjection(String me, MethodNode m, AnnotationNode a, ClassNode target) {
        Object methods = get(a, "method");
        if (methods instanceof List<?> l) {
            // Mixin (TargetSelectors.findRootTargets / validate, sponge-mixin 0.17.3): each selector must match at least its
            // quantifier's minimum -- 0 unless it says "+" or "{n,..}" -- and the injector as a whole at least one method
            // ("could not find any targets matching a or b"). An alternative no method answers beside one that matches
            // is no failure (the 1.20.2 layer's @Redirect on TextureManager {method_4618, method_4619} at 1.21.2)
            List<String> dead = new ArrayList<>();
            boolean anyMatched = false;
            for (Object o : l) {
                String sel = o.toString();
                // an owner-qualified selector ("Lowner;name(desc)", MixinRepair's moved injectors) names the member
                String bare = sel.startsWith("L") && sel.indexOf(';') > 0 && (sel.indexOf('(') < 0 || sel.indexOf(';') < sel.indexOf('('))
                    ? sel.substring(sel.indexOf(';') + 1) : sel;
                String name = bare.contains("(") ? bare.substring(0, bare.indexOf('(')) : bare;
                String desc = bare.contains("(") ? bare.substring(bare.indexOf('(')) : null;
                int min = 0;
                java.util.regex.Matcher q = java.util.regex.Pattern.compile("(\\+|\\{(\\d*)(,\\d*)?\\}|\\*)$").matcher(name);
                if (q.find()) {
                    min = q.group(1).equals("+") ? 1 : q.group(2) != null && !q.group(2).isEmpty() ? Integer.parseInt(q.group(2)) : 0;
                    name = name.substring(0, q.start());
                }
                boolean ok = desc == null ? hasMethodNamed(target, name) : hasMethod(target, name, desc);
                // or merged by a class mixin (this jar's, or a listed one of another installed jar) applying first
                ok = ok || mergedForInjection(target.name, name, desc, currentMixin);
                anyMatched |= ok;
                if (!ok) {
                    dead.add(sel);
                    if (min > 0) {
                        problems.add(me + ": " + short_(a.desc) + " " + m.name + " method=\"" + sel + "\" (at least " + min + ") not on " + target.name);
                    }
                }
            }
            if (!anyMatched && !dead.isEmpty()) {
                for (String sel : dead) {
                    problems.add(me + ": " + short_(a.desc) + " " + m.name + " method=\"" + sel + "\" not on " + target.name);
                }
            }
        }
        Object at = get(a, "at");
        // Redirect / ModifyVariable / ModifyArg(s) take ONE @At in sponge-mixin 0.17.3 (Fabric Loader
        // 0.19.3 servers run it) and an array in 0.17.4: compiled against the newer API the value is
        // an array, which the older reads as a ClassCastException before any mixin applies
        if (at instanceof List<?> && SINGLE_AT.contains(a.desc)) {
            problems.add(me + ": " + short_(a.desc) + " " + m.name + " encodes at as an array (compiled against a Mixin API"
                + " newer than the runtimes'; build against the oldest sponge-mixin)");
        }
        List<AnnotationNode> ats = new ArrayList<>();
        if (at instanceof AnnotationNode an) {
            ats.add(an);
        } else if (at instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof AnnotationNode an) {
                    ats.add(an);
                }
            }
        }
        for (AnnotationNode an : ats) {
            String sel = str(get(an, "target"));
            if (sel == null || sel.isEmpty()) {
                continue;
            }
            if ("NEW".equals(str(get(an, "value")))) {
                // a constructor, named as Mixin's BeforeNew reads it (newTarget): the type and its <init>
                String[] nt = newTarget(sel);
                ClassNode nc = nt[0] == null ? null : jar.get(nt[0]);
                int ns = nt[0] == null ? -1 : nt[0].lastIndexOf('/');
                if (nc == null && nt[0] != null && !jarPackages.contains(ns < 0 ? "" : nt[0].substring(0, ns))) {
                    continue; // a library's or the JDK's class (new ArrayList): Mixin matches the name in the body
                }
                if (nc == null || nt[1] != null && !hasMethod(nc, "<init>", nt[1])) {
                    problems.add(me + ": @At(NEW) target \"" + sel + "\" names " + (nt[0] == null ? "no type"
                        : nc == null ? "type " + nt[0] + ", not in the jar" : "no constructor " + nt[1] + " of " + nt[0])
                        + " (" + m.name + ")");
                }
                continue;
            }
            // Lowner;name(desc)ret   |   Lowner;name:desc   |   name(desc)ret on the target
            String owner = target.name;
            String rest = sel;
            if (sel.startsWith("L") && sel.contains(";")) {
                owner = sel.substring(1, sel.indexOf(';'));
                rest = sel.substring(sel.indexOf(';') + 1);
            }
            ClassNode oc = jar.get(owner);
            if (oc == null) {
                // an owner in a package of the game is a game class the jar must have; one in another
                // package (com/mojang/brigadier, com/mojang/serialization) is a library's, which Mixin
                // matches by name in the target method's code without loading it
                int slash = owner.lastIndexOf('/');
                if (jarPackages.contains(slash < 0 ? "" : owner.substring(0, slash))) {
                    problems.add(me + ": @At owner " + owner + " not in jar (" + m.name + ")");
                }
                continue;
            }
            boolean ok;
            if (rest.contains("(")) {
                ok = hasMethod(oc, rest.substring(0, rest.indexOf('(')), rest.substring(rest.indexOf('(')));
            } else if (rest.contains(":")) {
                ok = hasField(oc, rest.substring(0, rest.indexOf(':')), rest.substring(rest.indexOf(':') + 1));
            } else {
                ok = hasMethodNamed(oc, rest) || hasFieldNamed(oc, rest);
            }
            if (!ok) {
                problems.add(me + ": @At target \"" + sel + "\" unresolved (" + m.name + ")");
            }
        }
        scanBodies(me, m, a, target);
    }

    private static final String INJECT = "Lorg/spongepowered/asm/mixin/injection/Inject;";
    private static final String REDIRECT = "Lorg/spongepowered/asm/mixin/injection/Redirect;";
    private static final String MODIFY_ARG = "Lorg/spongepowered/asm/mixin/injection/ModifyArg;";
    private static final String COERCE = "Lorg/spongepowered/asm/mixin/injection/Coerce;";
    private static final Type CI = Type.getObjectType("org/spongepowered/asm/mixin/injection/callback/CallbackInfo");
    private static final Type CIR = Type.getObjectType("org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable");
    /** injectors whose @At points are matched here (ModifyConstant has none; MixinExtras is not used by the layers) */
    private static final java.util.Set<String> AT_INJECTORS = java.util.Set.of(INJECT, REDIRECT, MODIFY_ARG,
        "Lorg/spongepowered/asm/mixin/injection/ModifyArgs;", "Lorg/spongepowered/asm/mixin/injection/ModifyVariable;");

    /**
     * What the injector finds in the target method's body: the instructions its @At points match (summed over every
     * target method and point, as Mixin counts injections against require), and whether the handler's descriptor fits
     * each target / matched instruction the way Mixin's injectors check it at apply.
     */
    private void scanBodies(String me, MethodNode h, AnnotationNode a, ClassNode target) {
        if (!AT_INJECTORS.contains(a.desc)) {
            return;
        }
        String what = me + ": " + short_(a.desc) + " " + h.name + " in " + target.name;
        if (codeBytes.isEmpty()) {
            unscanned.add(what + " (no code source)");
            return;
        }
        ClassNode code = codeClass(target.name);
        if (code == null) {
            unscanned.add(what + " (target class has no code in " + codeSource + ")");
            return;
        }
        List<MethodNode> tms = targetMethods(a, code);
        if (tms.isEmpty()) {
            return; // the selector itself is reported above
        }
        List<AnnotationNode> ats = new ArrayList<>();
        Object at = get(a, "at");
        if (at instanceof AnnotationNode an) {
            ats.add(an);
        } else if (at instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof AnnotationNode an) {
                    ats.add(an);
                }
            }
        }
        Map<String, AnnotationNode> slices = new HashMap<>();
        Object sl = get(a, "slice");
        for (Object o : sl instanceof List<?> l ? l : sl == null ? List.of() : List.of(sl)) {
            if (o instanceof AnnotationNode s) {
                slices.put(str(get(s, "id")) == null ? "" : str(get(s, "id")), s);
            }
        }
        boolean handlerStatic = (h.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0;
        int found = 0;
        boolean unknown = false;
        for (MethodNode tm : tms) {
            if (tm.instructions == null || tm.instructions.size() == 0) {
                problems.add(what + "." + tm.name + tm.desc + ": target method has no body (abstract/native): nothing to inject into");
                continue;
            }
            boolean targetStatic = (tm.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0;
            if (targetStatic && !handlerStatic) {
                problems.add(what + "." + tm.name + tm.desc + ": non-static handler on a static target");
            }
            if (a.desc.equals(INJECT)) {
                checkCallback(what, h, a, tm);
            }
            if (a.desc.endsWith("ModifyVariable;")) {
                // ModifyVariableInjector: (the local's type) then a prefix of the target's arguments, returning the type
                Type t = Type.getReturnType(h.desc);
                String err = validate(h, tm, t, List.of(t));
                if (err != null) {
                    problems.add(what + "." + tm.name + tm.desc + ": handler " + h.desc + " " + err);
                }
            }
            currentInjector = a;
            currentWhat = what + "." + tm.name + tm.desc;
            currentCode = code;
            for (AnnotationNode an : ats) {
                List<AbstractInsnNode> hits = match(an, tm, slices, a.desc.endsWith("ModifyVariable;") ? Type.getReturnType(h.desc) : null);
                if (hits == null) {
                    unknown = true;
                    continue;
                }
                found += hits.size();
                if (a.desc.equals(REDIRECT)) {
                    for (AbstractInsnNode insn : hits) {
                        redirectSites.add(target.name + "\t" + tm.name + tm.desc + "\t" + tm.instructions.indexOf(insn) + "\t"
                            + insnText(insn) + "\t" + currentMixin + "." + h.name);
                    }
                }
                if (a.desc.equals(REDIRECT) || a.desc.equals(MODIFY_ARG)) {
                    java.util.Set<String> shapes = new java.util.HashSet<>();
                    for (AbstractInsnNode insn : hits) {
                        String err = a.desc.equals(REDIRECT) ? checkRedirect(h, tm, insn, an) : checkModifyArg(h, a, tm, insn);
                        if (err != null && shapes.add(err)) {
                            problems.add(what + "." + tm.name + tm.desc + ": handler " + h.desc + " " + err);
                        }
                    }
                }
            }
        }
        scanned++;
        Object r = get(a, "require");
        int require = r instanceof Integer i && i >= 0 ? i : defaultRequire.getOrDefault(currentMixin, 0);
        if (found >= Math.max(require, 1)) {
            return;
        }
        String pts = ats.stream().map(an -> str(get(an, "value")) + (get(an, "target") != null ? " " + get(an, "target") : ""))
            .collect(java.util.stream.Collectors.joining(", "));
        if (unknown) {
            unscanned.add(what + " [" + pts + "]: point type not modelled, " + found + " matched by the rest");
        } else if (found < require) {
            problems.add(what + ": " + found + " injection point(s) matched, require " + require + " [" + pts + "] in "
                + tms.stream().map(t -> t.name + t.desc).collect(java.util.stream.Collectors.joining(", "))
                + " (Critical injection failure at apply)");
        } else {
            silent.add(what + " [" + pts + "]: 0 matched (require 0: applies, does nothing)");
        }
    }

    /** the target methods a selector picks: name-only without '*' is the first method of that name, as Mixin selects */
    private static List<MethodNode> targetMethods(AnnotationNode a, ClassNode code) {
        List<MethodNode> out = new ArrayList<>();
        Object methods = get(a, "method");
        if (!(methods instanceof List<?> l)) {
            return out;
        }
        for (Object o : l) {
            String sel = o.toString();
            if (sel.startsWith("L") && sel.indexOf(';') > 0 && (sel.indexOf('(') < 0 || sel.indexOf(';') < sel.indexOf('('))) {
                sel = sel.substring(sel.indexOf(';') + 1);
            }
            String name = sel.contains("(") ? sel.substring(0, sel.indexOf('(')) : sel;
            String desc = sel.contains("(") ? sel.substring(sel.indexOf('(')) : null;
            boolean all = name.endsWith("*");
            if (all) {
                name = name.substring(0, name.length() - 1);
            }
            for (MethodNode m : code.methods) {
                boolean nameOk = all ? m.name.startsWith(name) : m.name.equals(name);
                if (nameOk && (desc == null || m.desc.equals(desc)) && !out.contains(m)) {
                    out.add(m);
                    if (!all && desc == null) {
                        break;
                    }
                }
            }
        }
        return out;
    }

    /** a member selector as Mixin's MemberInfo reads it: [Lowner;|owner.]name[(desc)ret | :desc]; nulls match anything */
    private record Sel(String owner, String name, String desc) {
        static Sel parse(String s) {
            String owner = null;
            String rest = s;
            int semi = s.indexOf(';');
            int paren = s.indexOf('(');
            int colon = s.indexOf(':');
            int end = paren >= 0 ? paren : colon >= 0 ? colon : s.length();
            if (s.startsWith("L") && semi > 0 && semi < end) {
                owner = s.substring(1, semi);
                rest = s.substring(semi + 1);
            } else if (s.lastIndexOf('.', end - 1) > 0) {
                int dot = s.lastIndexOf('.', end - 1);
                owner = s.substring(0, dot).replace('.', '/');
                rest = s.substring(dot + 1);
            }
            String name = rest;
            String desc = null;
            if (rest.contains("(")) {
                name = rest.substring(0, rest.indexOf('('));
                desc = rest.substring(rest.indexOf('('));
            } else if (rest.contains(":")) {
                name = rest.substring(0, rest.indexOf(':'));
                desc = rest.substring(rest.indexOf(':') + 1);
            }
            return new Sel(owner, name.isEmpty() ? null : name, desc);
        }

        boolean matches(String o, String n, String d) {
            return (owner == null || owner.equals(o)) && (name == null || name.equals(n)) && (desc == null || desc.equals(d));
        }
    }

    private static String arg(AnnotationNode at, String key) {
        if (get(at, "args") instanceof List<?> l) {
            for (Object o : l) {
                String s = o.toString();
                if (s.startsWith(key + "=")) {
                    return s.substring(key.length() + 1);
                }
            }
        }
        return null;
    }

    /**
     * The instructions one @At picks in {@code tm} (null: a point type not modelled here). Ordinal counts among the
     * matches inside the slice, as Mixin's InjectionPoint.find does.
     */
    private List<AbstractInsnNode> match(AnnotationNode at, MethodNode tm, Map<String, AnnotationNode> slices, Type local) {
        int from = 0;
        int to = tm.instructions.size() - 1;
        String sliceId = str(get(at, "slice"));
        AnnotationNode slice = slices.get(sliceId == null ? "" : sliceId);
        if (slice != null) {
            if (get(slice, "from") instanceof AnnotationNode f) {
                List<AbstractInsnNode> m = match(f, tm, Map.of(), null);
                if (m == null) {
                    return null;
                }
                from = m.isEmpty() ? Integer.MAX_VALUE : tm.instructions.indexOf(m.get(0));
            }
            if (get(slice, "to") instanceof AnnotationNode t) {
                List<AbstractInsnNode> m = match(t, tm, Map.of(), null);
                if (m == null) {
                    return null;
                }
                to = m.isEmpty() ? -1 : tm.instructions.indexOf(m.get(m.size() - 1));
            }
        }
        String value = str(get(at, "value"));
        String target = str(get(at, "target"));
        Object ordO = get(at, "ordinal");
        int ordinal = ordO instanceof Integer i ? i : -1;
        Object opO = get(at, "opcode");
        int opcode = opO instanceof Integer i ? i : -1;
        Sel sel = target == null || target.isEmpty() ? new Sel(null, null, null) : Sel.parse(target);
        List<AbstractInsnNode> hits = new ArrayList<>();
        AbstractInsnNode[] insns = tm.instructions.toArray();
        AbstractInsnNode lastReturn = null;
        for (int i = Math.max(from, 0); i <= to && i < insns.length; i++) {
            AbstractInsnNode n = insns[i];
            boolean hit;
            switch (value == null ? "" : value) {
                case "HEAD" -> hit = hits.isEmpty() && n.getOpcode() >= 0;
                case "RETURN" -> hit = n.getOpcode() >= org.objectweb.asm.Opcodes.IRETURN && n.getOpcode() <= org.objectweb.asm.Opcodes.RETURN;
                case "TAIL" -> {
                    if (n.getOpcode() >= org.objectweb.asm.Opcodes.IRETURN && n.getOpcode() <= org.objectweb.asm.Opcodes.RETURN) {
                        lastReturn = n;
                    }
                    hit = false;
                }
                case "INVOKE", "INVOKE_ASSIGN" -> hit = n instanceof MethodInsnNode mi && sel.matches(mi.owner, mi.name, mi.desc)
                    && (value.equals("INVOKE") || Type.getReturnType(mi.desc).getSort() != Type.VOID);
                case "INVOKE_STRING" -> {
                    String ldc = arg(at, "ldc");
                    AbstractInsnNode prev = n.getPrevious();
                    while (prev != null && prev.getOpcode() < 0) {
                        prev = prev.getPrevious();
                    }
                    hit = n instanceof MethodInsnNode mi && sel.matches(mi.owner, mi.name, mi.desc)
                        && mi.desc.equals("(Ljava/lang/String;)V")
                        && prev instanceof LdcInsnNode l && l.cst instanceof String s && s.equals(ldc);
                }
                case "FIELD" -> hit = n instanceof FieldInsnNode fi && sel.matches(fi.owner, fi.name, fi.desc)
                    && (opcode < 0 || fi.getOpcode() == opcode);
                case "NEW" -> {
                    // BeforeNew: the ordinal counts among the NEWs of the type; the constructor descriptor filters after
                    String[] nt = newTarget(arg(at, "class") != null ? arg(at, "class") : target);
                    hit = n instanceof TypeInsnNode ti && ti.getOpcode() == org.objectweb.asm.Opcodes.NEW
                        && (nt[0] == null || nt[0].equals(ti.desc));
                }
                case "STORE", "LOAD" -> {
                    // AfterStoreLocal / BeforeLoadLocal: a store / load of the local the ModifyVariable discriminator picks
                    // in the frame there (after the store, at the load) -- localAt models Mixin's Locals.getLocalsAt
                    if (local == null) {
                        return null;
                    }
                    int op = local.getOpcode(value.equals("STORE") ? org.objectweb.asm.Opcodes.ISTORE : org.objectweb.asm.Opcodes.ILOAD);
                    if (n.getOpcode() != op) {
                        hit = false;
                        break;
                    }
                    Integer slot = localAt(tm, value.equals("STORE") ? i + 1 : i, local);
                    if (slot == null) {
                        return null;
                    }
                    hit = slot >= 0 && ((org.objectweb.asm.tree.VarInsnNode) n).var == slot;
                }
                default -> {
                    return null;
                }
            }
            if (hit) {
                hits.add(n);
            }
        }
        if ("TAIL".equals(value) && lastReturn != null) {
            hits.add(lastReturn);
        }
        if (ordinal >= 0) {
            hits = ordinal < hits.size() ? List.of(hits.get(ordinal)) : List.of();
        }
        if ("NEW".equals(value)) {
            String ctor = newTarget(arg(at, "class") != null ? arg(at, "class") : target)[1];
            if (ctor != null) {
                List<AbstractInsnNode> kept = new ArrayList<>();
                for (AbstractInsnNode h : hits) {
                    MethodInsnNode init = ctorAfter((TypeInsnNode) h);
                    if (init != null && init.desc.equals(ctor)) {
                        kept.add(h);
                    }
                }
                hits = kept;
            }
        }
        return hits;
    }

    /** the injector, its report prefix and the target's code class being matched (localAt reads the discriminator) */
    private AnnotationNode currentInjector;
    private String currentWhat;
    private ClassNode currentCode;
    private final Map<MethodNode, org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.BasicValue>[]> frames
        = new java.util.IdentityHashMap<>();

    /**
     * The local the current @ModifyVariable's discriminator (LocalVariableDiscriminator, sponge-mixin 0.8.7 javap)
     * picks in the frame before instruction {@code at} of {@code tm}: its slot, -1 when none fits (the point does not
     * inject there), null when not modelled (a name discriminator without a local variable table). Locals are the
     * target's LVT entries live there or, without one, the types a verifier's analysis gives each slot (Mixin's
     * Locals.getLocalsAt); argsOnly: the method's arguments. Candidates are the locals from the first argument on
     * (baseArgIndex) whose type equals the handler's exactly; ordinal counts locals of that type from slot 0;
     * implicit (no ordinal / index / name) needs exactly one candidate -- otherwise Mixin throws
     * InvalidImplicitDiscriminatorException at apply whatever the require, reported as a problem here.
     */
    private Integer localAt(MethodNode tm, int at, Type type) {
        AnnotationNode a = currentInjector;
        int ordinal = get(a, "ordinal") instanceof Integer o ? o : -1;
        int index = get(a, "index") instanceof Integer x ? x : -1;
        List<String> names = new ArrayList<>();
        if (get(a, "name") instanceof List<?> l) {
            for (Object o : l) {
                names.add(o.toString());
            }
        }
        boolean argsOnly = Boolean.TRUE.equals(get(a, "argsOnly"));
        boolean isStatic = (tm.access & org.objectweb.asm.Opcodes.ACC_STATIC) != 0;
        int base = isStatic ? 0 : 1;
        Type[] locals = new Type[tm.maxLocals];
        String[] lnames = new String[tm.maxLocals];
        if (argsOnly) {
            int s = base;
            for (Type t : Type.getArgumentTypes(tm.desc)) {
                if (s < locals.length) {
                    locals[s] = t;
                }
                s += t.getSize();
            }
        } else if (tm.localVariables != null && !tm.localVariables.isEmpty()) {
            for (org.objectweb.asm.tree.LocalVariableNode lv : tm.localVariables) {
                int s = tm.instructions.indexOf(lv.start);
                int e = tm.instructions.indexOf(lv.end);
                if (s <= at && at < e && lv.index < locals.length) {
                    locals[lv.index] = Type.getType(lv.desc);
                    lnames[lv.index] = lv.name;
                }
            }
        } else {
            if (!names.isEmpty() && ordinal < 0 && index < base) {
                return null;
            }
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.BasicValue>[] fs = frames(tm);
            if (fs == null) {
                return null;
            }
            org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.BasicValue> f = at < fs.length ? fs[at] : null;
            if (f == null) {
                return -1; // unreachable code: the analysis has no frame, Mixin finds nothing to modify there
            }
            for (int s = 0; s < f.getLocals() && s < locals.length; s++) {
                org.objectweb.asm.tree.analysis.BasicValue v = f.getLocal(s);
                locals[s] = v == null || v.getType() == null || v == org.objectweb.asm.tree.analysis.BasicValue.UNINITIALIZED_VALUE ? null : v.getType();
            }
        }
        int ord = -1;
        int count = 0;
        int implicit = -1;
        for (int s = 0; s < locals.length; s++) {
            if (locals[s] == null || !locals[s].equals(type)) {
                continue;
            }
            ord++;
            if (s < base) {
                continue;
            }
            count++;
            implicit = s;
            if (ordinal > -1 ? ordinal == ord : index >= base ? index == s : !names.isEmpty() && names.contains(lnames[s])) {
                return s;
            }
        }
        if (ordinal < 0 && index < base && names.isEmpty()) {
            if (count == 1) {
                return implicit;
            }
            if (count > 1) {
                problems.add(currentWhat + ": implicit @ModifyVariable finds " + count + " " + type.getDescriptor()
                    + " locals at instruction " + at + " (InvalidImplicitDiscriminatorException at apply)");
            }
        }
        return -1;
    }

    /** the frames a verifier's analysis gives the target method (class hierarchy from the code source), or null */
    private org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.BasicValue>[] frames(MethodNode tm) {
        if (frames.containsKey(tm)) {
            return frames.get(tm);
        }
        ClassNode owner = currentCode;
        org.objectweb.asm.tree.analysis.Frame<org.objectweb.asm.tree.analysis.BasicValue>[] out;
        try {
            List<Type> itfs = new ArrayList<>();
            for (String s : owner.interfaces) {
                itfs.add(Type.getObjectType(s));
            }
            org.objectweb.asm.tree.analysis.SimpleVerifier v = new org.objectweb.asm.tree.analysis.SimpleVerifier(
                    org.objectweb.asm.Opcodes.ASM9, Type.getObjectType(owner.name),
                    owner.superName == null ? null : Type.getObjectType(owner.superName), itfs,
                    (owner.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0) {
                @Override
                protected boolean isInterface(Type t) {
                    ClassNode c = hierarchyClass(t.getInternalName());
                    return c != null && (c.access & org.objectweb.asm.Opcodes.ACC_INTERFACE) != 0;
                }

                @Override
                protected Type getSuperClass(Type t) {
                    ClassNode c = hierarchyClass(t.getInternalName());
                    return c == null ? (t.getInternalName().equals("java/lang/Object") ? null : Type.getObjectType("java/lang/Object"))
                        : c.superName == null ? null : Type.getObjectType(c.superName);
                }

                @Override
                protected Class<?> getClass(Type t) {
                    throw new IllegalStateException("not loaded: " + t);
                }

                @Override
                protected boolean isAssignableFrom(Type to, Type from) {
                    if (to.equals(from) || to.getInternalName().equals("java/lang/Object") || isInterface(to)) {
                        return true; // interfaces: as the JVM's verifier, any reference may stand for one
                    }
                    if (to.getSort() == Type.ARRAY || from.getSort() == Type.ARRAY) {
                        return to.getSort() == Type.ARRAY && from.getSort() == Type.ARRAY && to.getDimensions() == from.getDimensions()
                            && to.getElementType().getSort() == Type.OBJECT && from.getElementType().getSort() == Type.OBJECT
                            && isAssignableFrom(to.getElementType(), from.getElementType());
                    }
                    return subtype(from.getInternalName(), to.getInternalName(), new java.util.HashSet<>());
                }
            };
            out = new org.objectweb.asm.tree.analysis.Analyzer<>(v).analyze(owner.name, tm);
        } catch (org.objectweb.asm.tree.analysis.AnalyzerException | RuntimeException ex) {
            out = null;
        }
        frames.put(tm, out);
        return out;
    }

    /** a class of the code source, or a JDK class's shape read from the running JDK (null: unknown, taken as Object's subclass) */
    private ClassNode hierarchyClass(String name) {
        ClassNode c = codeClass(name);
        if (c != null || !name.startsWith("java/")) {
            return c;
        }
        return jdkNodes.computeIfAbsent(name, n -> {
            try (java.io.InputStream in = ClassLoader.getSystemResourceAsStream(n + ".class")) {
                if (in == null) {
                    return null;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(in.readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                return cn;
            } catch (java.io.IOException ex) {
                return null;
            }
        });
    }

    private final Map<String, ClassNode> jdkNodes = new HashMap<>();

    /**
     * whether class {@code c} is {@code sup} or extends / implements it. The analysis is after the locals' types, not a
     * verification: a class the code source and the JDK do not hold (a library's) counts as compatible
     */
    private boolean subtype(String c, String sup, java.util.Set<String> seen) {
        if (c == null || !seen.add(c)) {
            return false;
        }
        if (c.equals(sup)) {
            return true;
        }
        ClassNode n = hierarchyClass(c);
        if (n == null) {
            return !c.equals("java/lang/Object");
        }
        if (subtype(n.superName, sup, seen)) {
            return true;
        }
        for (String i : n.interfaces) {
            if (subtype(i, sup, seen)) {
                return true;
            }
        }
        return false;
    }

    /**
     * What @At("NEW") looks for, as Mixin's BeforeNew reads its class / target (sponge-mixin 0.8.7, javap): the
     * string with '.' as '/', parsed as a MemberInfo (descriptor split off at '(' or ':', then the owner before the
     * last '.' or inside "L...;", a name holding '/' taken as the owner), its type MemberInfo.toCtorType -- the
     * descriptor's return type when it is a method descriptor ("(I)Lfoo/Bar;" -> foo/Bar, but
     * "Lfoo/Bar;<init>(I)V" -> "V": a NEW of nothing), else the owner, else a bare name -- and its constructor
     * descriptor toCtorDesc (the descriptor with return type V). {type, ctorDesc}; nulls: any.
     */
    static String[] newTarget(String raw) {
        if (raw == null || raw.isEmpty()) {
            return new String[] {null, null};
        }
        String s = raw.replace('.', '/').replaceAll("\\s", "");
        String desc = null;
        int paren = s.indexOf('(');
        int colon = s.indexOf(':');
        if (paren > -1) {
            desc = s.substring(paren);
            s = s.substring(0, paren);
        } else if (colon > -1) {
            desc = s.substring(colon + 1);
            s = s.substring(0, colon);
        }
        String owner = null;
        String name = s;
        int semi = s.indexOf(';');
        if (semi > -1 && s.startsWith("L")) {
            owner = s.substring(1, semi);
            name = s.substring(semi + 1);
        }
        if (owner == null && name.indexOf('/') > -1) {
            owner = name;
            name = "";
        }
        String type;
        if (desc != null && desc.startsWith("(") && desc.indexOf(')') > -1) {
            String r = desc.substring(desc.indexOf(')') + 1);
            type = r.startsWith("L") && r.endsWith(";") ? r.substring(1, r.length() - 1) : r;
        } else if (owner != null) {
            type = owner;
        } else if (!name.isEmpty() && desc == null) {
            type = name;
        } else {
            type = desc != null ? desc : raw;
        }
        String ctor = desc == null ? null
            : desc.startsWith("(") && desc.indexOf(')') > -1 ? desc.substring(0, desc.indexOf(')') + 1) + "V" : desc;
        return new String[] {type, ctor};
    }

    /** the constructor call that completes a NEW: the next INVOKESPECIAL <init> of the type at the same nesting */
    private static MethodInsnNode ctorAfter(TypeInsnNode n) {
        int depth = 0;
        for (AbstractInsnNode i = n.getNext(); i != null; i = i.getNext()) {
            if (i instanceof TypeInsnNode t && t.getOpcode() == org.objectweb.asm.Opcodes.NEW && t.desc.equals(n.desc)) {
                depth++;
            } else if (i instanceof MethodInsnNode mi && mi.getOpcode() == org.objectweb.asm.Opcodes.INVOKESPECIAL
                    && mi.name.equals("<init>") && mi.owner.equals(n.desc)) {
                if (depth == 0) {
                    return mi;
                }
                depth--;
            }
        }
        return null;
    }

    /** CallbackInjector / Callback.checkDescriptor: exact (args, CI), or (CI) alone, or (args, CI, locals...) when capturing */
    private void checkCallback(String what, MethodNode h, AnnotationNode a, MethodNode tm) {
        Type[] targs = Type.getArgumentTypes(tm.desc);
        Type ci = Type.getReturnType(tm.desc).getSort() == Type.VOID ? CI : CIR;
        Type[] hargs = Type.getArgumentTypes(h.desc);
        List<Type> full = new ArrayList<>(List.of(targs));
        full.add(ci);
        boolean locals = get(a, "locals") != null;
        boolean ok = Type.getReturnType(h.desc).getSort() == Type.VOID
            && (sameArgs(h, hargs, full, false)
                || (hargs.length == 1 && hargs[0].equals(ci))
                || (locals && sameArgs(h, hargs, full, true)));
        if (!ok) {
            problems.add(what + "." + tm.name + tm.desc + ": handler " + h.desc + " is not (target args, "
                + ci.getClassName().substring(ci.getClassName().lastIndexOf('.') + 1) + ") -- Invalid descriptor at apply");
        }
    }

    /**
     * Callback.checkDescriptor's comparison: equal types, or a @Coerce'd non-array reference parameter (Mixin then
     * checks the target type is assignable to it -- not repeated here); {@code prefix}: captured locals may follow.
     */
    private static boolean sameArgs(MethodNode h, Type[] hargs, List<Type> want, boolean prefix) {
        if (prefix ? hargs.length < want.size() : hargs.length != want.size()) {
            return false;
        }
        for (int i = 0; i < want.size(); i++) {
            if (!hargs[i].equals(want.get(i)) && !(hargs[i].getSort() == Type.OBJECT && coerced(h, i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Every instruction a @Redirect takes over, one line each: target class, method, instruction index in the
     * release's code, the instruction, the redirecting mixin.handler. Written to -Dcb.redirectSites=<file> so a
     * caller can find two mods / layers redirecting one call site (Mixin keeps the first, skips the other with a
     * "@Redirect conflict" WARN, and a required one then fails its injection check).
     */
    private final List<String> redirectSites = new ArrayList<>();

    private static String insnText(AbstractInsnNode insn) {
        if (insn instanceof MethodInsnNode mi) {
            return mi.owner + "." + mi.name + mi.desc;
        }
        if (insn instanceof FieldInsnNode fi) {
            return fi.owner + "." + fi.name + ":" + fi.desc;
        }
        if (insn instanceof TypeInsnNode ti) {
            return "new " + ti.desc;
        }
        return "opcode " + insn.getOpcode();
    }

    private static boolean coerced(MethodNode h, int index) {
        if (index < 0) {
            return find(h.invisibleAnnotations, COERCE) != null;
        }
        return h.invisibleParameterAnnotations != null && index < h.invisibleParameterAnnotations.length
            && find(h.invisibleParameterAnnotations[index], COERCE) != null;
    }

    /** Injector.validateParams: return type, then the instruction's arguments, then a prefix of the target's arguments */
    private static String validate(MethodNode h, MethodNode tm, Type ret, List<Type> args) {
        Type hret = Type.getReturnType(h.desc);
        if (!hret.equals(ret) && !coerced(h, -1)) {
            return "returns " + hret.getDescriptor() + ", expected " + ret.getDescriptor();
        }
        Type[] hargs = Type.getArgumentTypes(h.desc);
        if (hargs.length < args.size()) {
            return "has too few arguments, expected " + args.get(hargs.length).getDescriptor() + " at index " + hargs.length;
        }
        for (int i = 0; i < args.size(); i++) {
            if (!hargs[i].equals(args.get(i)) && !coerced(h, i)) {
                return "has " + hargs[i].getDescriptor() + " at index " + i + ", expected " + args.get(i).getDescriptor();
            }
        }
        Type[] targs = Type.getArgumentTypes(tm.desc);
        for (int i = args.size(), j = 0; i < hargs.length; i++, j++) {
            if (j >= targs.length) {
                return "has " + (hargs.length - i) + " unexpected additional argument(s)";
            }
            if (!hargs[i].equals(targs[j]) && !coerced(h, i)) {
                return "captures " + hargs[i].getDescriptor() + " at index " + i + ", target argument is " + targs[j].getDescriptor();
            }
        }
        return null;
    }

    /** RedirectInjector: the shape of the redirected instruction */
    private static String checkRedirect(MethodNode h, MethodNode tm, AbstractInsnNode insn, AnnotationNode at) {
        List<Type> args = new ArrayList<>();
        Type ret;
        if (insn instanceof MethodInsnNode mi) {
            if (mi.getOpcode() != org.objectweb.asm.Opcodes.INVOKESTATIC) {
                args.add(Type.getObjectType(mi.owner));
            }
            args.addAll(List.of(Type.getArgumentTypes(mi.desc)));
            ret = Type.getReturnType(mi.desc);
        } else if (insn instanceof FieldInsnNode fi) {
            if (arg(at, "array") != null) {
                return null; // array element redirects: not modelled
            }
            Type ft = Type.getType(fi.desc);
            switch (fi.getOpcode()) {
                case org.objectweb.asm.Opcodes.GETFIELD -> { args.add(Type.getObjectType(fi.owner)); ret = ft; }
                case org.objectweb.asm.Opcodes.GETSTATIC -> ret = ft;
                case org.objectweb.asm.Opcodes.PUTFIELD -> { args.add(Type.getObjectType(fi.owner)); args.add(ft); ret = Type.VOID_TYPE; }
                default -> { args.add(ft); ret = Type.VOID_TYPE; }
            }
        } else if (insn instanceof TypeInsnNode ti) {
            MethodInsnNode ctor = ctorAfter(ti);
            if (ctor == null) {
                return null;
            }
            args.addAll(List.of(Type.getArgumentTypes(ctor.desc)));
            ret = Type.getObjectType(ti.desc);
        } else {
            return null;
        }
        return validate(h, tm, ret, args);
    }

    /** ModifyArgInjector: (T)T for the chosen argument, or every call argument in and the chosen one out */
    private static String checkModifyArg(MethodNode h, AnnotationNode a, MethodNode tm, AbstractInsnNode insn) {
        if (!(insn instanceof MethodInsnNode mi)) {
            return null;
        }
        Type[] cargs = Type.getArgumentTypes(mi.desc);
        Type hret = Type.getReturnType(h.desc);
        int index = get(a, "index") instanceof Integer i ? i : -1;
        if (index < 0) {
            for (int i = 0; i < cargs.length; i++) {
                if (cargs[i].equals(hret)) {
                    if (index >= 0) {
                        return "matches several arguments of " + mi.name + mi.desc + " (set index)";
                    }
                    index = i;
                }
            }
            if (index < 0) {
                return "returns " + hret.getDescriptor() + ", which no argument of " + mi.name + mi.desc + " has";
            }
        }
        if (index >= cargs.length) {
            return "index " + index + " out of range for " + mi.name + mi.desc;
        }
        if (!cargs[index].equals(hret)) {
            return "returns " + hret.getDescriptor() + ", argument " + index + " of " + mi.name + mi.desc + " is " + cargs[index].getDescriptor();
        }
        Type[] hargs = Type.getArgumentTypes(h.desc);
        if (hargs.length == 1) {
            return hargs[0].equals(cargs[index]) ? null : "takes " + hargs[0].getDescriptor() + ", argument is " + cargs[index].getDescriptor();
        }
        return validate(h, tm, hret, List.of(cargs));
    }

    // ---- helpers ----

    private static String derive(String mname, String... prefixes) {
        for (String p : prefixes) {
            if (mname.startsWith(p) && mname.length() > p.length()) {
                String rest = mname.substring(p.length());
                // Mixin keeps an all-uppercase remainder as-is (getCRITERIA -> CRITERIA)
                return rest.equals(rest.toUpperCase()) ? rest
                    : Character.toLowerCase(rest.charAt(0)) + rest.substring(1);
            }
        }
        return null;
    }

    // Lookups climb supertypes. Mixin itself resolves most of these on the
    // target class only, but our intermediary stubs are built by a remapper
    // that does not propagate inherited member names the way the runtime
    // does (MappedRegistry.add is method_10272 at runtime, still "a" in the
    // stub because the tiny file lists it under the interface). Climbing
    // trades a few missed true positives for zero stub-induced false ones.
    private boolean hasField(ClassNode c, String name, String desc) {
        return walk(c, cn -> {
            for (FieldNode f : cn.fields) {
                if (f.name.equals(name) && (desc == null || f.desc.equals(desc))) {
                    return true;
                }
            }
            return false;
        });
    }

    private boolean hasFieldNamed(ClassNode c, String name) {
        return hasField(c, name, null);
    }

    private boolean hasMethod(ClassNode c, String name, String desc) {
        return walk(c, cn -> {
            for (MethodNode m : cn.methods) {
                if (m.name.equals(name) && (desc == null || m.desc.equals(desc))) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * A @Shadow resolves on the target class itself: Mixin looks the member up in the
     * target's own field and method lists and rejects the shadow at apply ("not located in
     * the target class"), taking the whole layer down. A member the target only inherits --
     * from a superclass or an interface (Level.getChunkSource, declared on LevelAccessor) --
     * is not found; the shadow belongs on a mixin of the declaring class.
     */
    private static boolean declaresMethod(ClassNode c, String name, String desc) {
        for (MethodNode m : c.methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }

    private static boolean declaresField(ClassNode c, String name, String desc) {
        for (FieldNode f : c.fields) {
            if (f.name.equals(name) && f.desc.equals(desc)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMethodNamed(ClassNode c, String name) {
        return hasMethod(c, name, null);
    }

    /** older releases' stubs: a cover's ORIGINAL member is looked up there */
    private SourceStubs sources = SourceStubs.none();

    private boolean walk(ClassNode c, java.util.function.Predicate<ClassNode> test) {
        java.util.ArrayDeque<ClassNode> todo = new java.util.ArrayDeque<>();
        java.util.Set<String> seen = new java.util.HashSet<>();
        todo.add(c);
        while (!todo.isEmpty()) {
            ClassNode cn = todo.poll();
            if (!seen.add(cn.name)) {
                continue;
            }
            if (test.test(cn)) {
                return true;
            }
            if (cn.superName != null && jar.containsKey(cn.superName)) {
                todo.add(jar.get(cn.superName));
            }
            if (cn.interfaces != null) {
                for (String i : cn.interfaces) {
                    if (jar.containsKey(i)) {
                        todo.add(jar.get(i));
                    }
                }
            }
        }
        return false;
    }

    private static boolean has(List<AnnotationNode> a, List<AnnotationNode> b, String desc) {
        return find(a, desc) != null || find(b, desc) != null;
    }

    private static AnnotationNode find(List<AnnotationNode> list, String desc) {
        if (list == null) {
            return null;
        }
        for (AnnotationNode a : list) {
            if (a.desc.equals(desc)) {
                return a;
            }
        }
        return null;
    }

    private static Object get(AnnotationNode a, String key) {
        if (a.values == null) {
            return null;
        }
        for (int i = 0; i + 1 < a.values.size(); i += 2) {
            if (key.equals(a.values.get(i))) {
                return a.values.get(i + 1);
            }
        }
        return null;
    }

    private static String str(Object o) {
        return o == null ? null : o.toString();
    }

    private static String short_(String desc) {
        return "@" + desc.substring(desc.lastIndexOf('/') + 1, desc.length() - 1);
    }
}
