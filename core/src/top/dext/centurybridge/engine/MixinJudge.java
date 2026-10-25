package top.dext.centurybridge.engine;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * The mixin probe's verdict on one release: every probe member (Main mixin-probe) judged statically
 * the way Mixin judges it at apply, against that release's real method bodies -- the mixin side of
 * what link-check is for the API probe. Raw on W it is the step's mixin damage table; raw on V it is
 * the control (it must be empty, or the probe or the judge is wrong); the probe converted one step by
 * the engine, judged on W with the step's layer, it says which damage rows the step's bridging carries.
 *
 * Per probe member, found by name in its probe mixin (a member the engine reshaped keeps its name on
 * the shape the mod calls):
 *   the mixin        still listed in the mixin config (triage strips a mixin it cannot keep)
 *   @Shadow          the target class itself declares the member, same static-ness (Mixin looks in
 *                    the target's own member table)
 *   @Accessor        the target declares the field with the accessor's type, same static-ness
 *   @Invoker         the target declares the method (a constructor invoker: the constructor)
 *   injectors        the selector names a method the target declares, static-ness and callback as
 *                    the handler takes them; every @At anchor stands in it (MixinRepair's evaluation:
 *                    owner, name and descriptor of a call or field access, a NEW's class, a constant,
 *                    the ordinal-th occurrence), and as often as in V (vcount: an injector at an
 *                    anchor that W's body holds a different number of times injects elsewhere or
 *                    more or less often than the mod was written for)
 *   anything else    a member without such an annotation (what the engine put in place of one) is
 *                    judged by its body: every call and field access resolves -- on the target and
 *                    its supertypes, the layers, the jar's own classes -- and a reference to another
 *                    member of the mixin is judged as that member
 * An injector repair drops leaves the handler without its annotation: judged as "disabled".
 *
 * With --source (V's code; the converted probe only, never the raw damage judging) an anchor pinned to
 * an ordinal that W holds another number of times than V passes when its occurrence is the counterpart
 * of V's (SiteMatch: matched by surrounding instructions), and fails as shifted / site lost otherwise.
 *
 *   mixin-judge <code jar> <probe jar> <index.tsv> <out.tsv> [--layer <jar>]... [--keys <damage.tsv>] [--source <V code jar>]
 * out.tsv: usage owner target anchor ordinal vcount fate -- one row per member that does not survive
 * (with --keys only the members whose key is in that table, i.e. the step's open damage rows).
 */
public final class MixinJudge {
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    private static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    private static final String CIR = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";

    private final MixinRepair code;
    /** classes of the layers and of the judged jar itself (bridge classes): declared members only */
    private final Map<String, ClassNode> extra = new HashMap<>();
    private final Map<String, byte[]> jar = new HashMap<>();
    private final Set<String> listed = new HashSet<>();
    /**
     * each listed mixin's refmap table: the names its annotation strings stand for (identity in the probe as
     * generated; past the 1.21.11 -> 26.1 wall the converter renamed the refmap, not the strings)
     */
    private final Map<String, com.google.gson.JsonObject> refmaps = new HashMap<>();

    private com.google.gson.JsonObject refmap(ClassNode mx) {
        return refmaps.get(mx.name);
    }

    /** an accessor / invoker name through the refmap: "name:desc" or "name(desc)" gives the name */
    private static String refName(com.google.gson.JsonObject rm, String s) {
        String m = MixinRepair.refmapLookup(rm, s);
        int i = m.indexOf(':') >= 0 ? m.indexOf(':') : m.indexOf('(');
        return i > 0 ? m.substring(0, i) : m;
    }

    /** the converted jar's restored overrides (MixinBridge.restoreOverride), by target: built on first use */
    private Map<String, List<MethodNode>> restoredByTarget;

    /** V's code (--source): an ordinal-pinned anchor is judged by whether it names V's site's counterpart */
    private MixinRepair source;
    /** the probe row being judged, as V had it (index columns owner, target, anchor, ordinal) */
    private final Map<String, String[]> sourceRows = new HashMap<>();
    private String rowOwner;
    private String rowTarget;
    private String rowAnchor;
    private int rowOrdinal = -1;

    private MixinJudge(Path codeJar) throws IOException {
        this.code = new MixinRepair(codeJar);
    }

    public static void run(String[] args) throws IOException {
        MixinJudge j = new MixinJudge(Path.of(args[1]));
        Path probe = Path.of(args[2]);
        Path index = Path.of(args[3]);
        Path out = Path.of(args[4]);
        Set<String> keys = null;
        for (int i = 5; i < args.length; i++) {
            if (args[i].equals("--layer")) {
                j.loadExtra(Path.of(args[++i]));
            } else if (args[i].equals("--source")) {
                j.source = new MixinRepair(Path.of(args[++i]));
            } else if (args[i].equals("--source-index")) {
                // the index in the source code's names (past the wall: wall_index.py), row for row
                List<String> si = Files.readAllLines(Path.of(args[++i]), StandardCharsets.UTF_8);
                for (int k = 1; k < si.size(); k++) {
                    String[] r = si.get(k).split("	", -1);
                    j.sourceRows.put(r[0] + "	" + r[1], r);
                }
            } else if (args[i].equals("--keys")) {
                keys = new HashSet<>();
                List<String> lines = Files.readAllLines(Path.of(args[++i]), StandardCharsets.UTF_8);
                for (int k = 1; k < lines.size(); k++) {
                    keys.add(key(lines.get(k).split("\t", -1)));
                }
            }
        }
        j.loadJar(probe);
        // rows grouped by probe class: each converted class is parsed once
        Map<String, List<String[]>> byClass = new LinkedHashMap<>();
        List<String> lines = Files.readAllLines(index, StandardCharsets.UTF_8);
        for (int k = 1; k < lines.size(); k++) {
            String[] r = lines.get(k).split("\t", -1);
            byClass.computeIfAbsent(r[0], c -> new ArrayList<>()).add(r);
        }
        int judged = 0;
        int open = 0;
        Map<String, Integer> byUsage = new TreeMap<>();
        Map<String, Integer> layerCounts = new java.util.TreeMap<>();
        Map<String, Integer> openByUsage = new TreeMap<>();
        // closed rows the layer holds, by category: what an audit of the layer-held rule samples from
        Path layerOut = out.resolveSibling(out.getFileName().toString().replaceFirst("[.]tsv$", "") + "-layer.tsv");
        try (PrintWriter w = new PrintWriter(Files.newBufferedWriter(out, StandardCharsets.UTF_8));
             PrintWriter lw = new PrintWriter(Files.newBufferedWriter(layerOut, StandardCharsets.UTF_8))) {
            w.println("usage\towner\ttarget\tanchor\tordinal\tvcount\tfate");
            lw.println("usage\towner\ttarget\tanchor\tordinal\tvcount\tlayer");
            for (Map.Entry<String, List<String[]>> e : byClass.entrySet()) {
                ClassNode mx = j.mixin(e.getKey());
                for (String[] r : e.getValue()) {
                    // index: probeClass member usage owner target anchor ordinal vcount
                    String[] k5 = {r[2], r[3], r[4], r[5], r[6]};
                    if (keys != null && !keys.contains(key(k5))) {
                        continue;
                    }
                    judged++;
                    byUsage.merge(r[2], 1, Integer::sum);
                    String why;
                    if (mx == null) {
                        why = j.jar.containsKey(e.getKey() + ".class") ? "mixin stripped" : "mixin gone";
                    } else {
                        String name = r[1].substring(0, r[1].indexOf('('));
                        // the mixin's own target as it stands now (a class rename moves it), else V's owner
                        List<String> ts = MixinRepair.mixinTargets(mx, j.refmap(mx));
                        String[] sr = j.sourceRows.getOrDefault(r[0] + "	" + r[1], r);
                        j.rowOwner = sr[3];
                        j.rowTarget = sr[4];
                        j.rowAnchor = sr[5];
                        j.rowOrdinal = Integer.parseInt(r[6]);
                        j.rowLayer = null;
                        why = j.judgeMember(mx, ts.size() == 1 ? ts.get(0) : r[3], name, Integer.parseInt(r[7]), new HashSet<>());
                        if (why == null && j.rowLayer == null && ts.stream().anyMatch(t -> t.startsWith("top/dext/"))) {
                            // a shadow / accessor / invoker of a mixin aimed at a facade: it reads and calls the facade's
                            // own members, which stand for V's class only on the instances mods hold (as their own calls
                            // of the facade do); not a vanilla path. Never counted closed: open under its own cause,
                            // reported on its own line (C7; main's decision)
                            j.rowLayer = "facade-access";
                            why = "facade-access: the facade's own member, not vanilla's";
                        }
                    }
                    if (j.rowLayer != null) {
                        layerCounts.merge(j.rowLayer, 1, Integer::sum);
                        if (why == null) {
                            lw.println(String.join("\t", r[2], r[3], r[4], r[5], r[6], r[7], j.rowLayer));
                        }
                    }
                    if (why != null) {
                        open++;
                        openByUsage.merge(r[2], 1, Integer::sum);
                        w.println(String.join("\t", r[2], r[3], r[4], r[5], r[6], r[7], why));
                    }
                }
            }
        }
        System.out.println("mixin-judge: " + open + " of " + judged + " probe members do not survive on " + Path.of(args[1]).getFileName()
            + " -> " + out);
        for (Map.Entry<String, Integer> e : byUsage.entrySet()) {
            System.out.printf("  %-18s %7d / %7d%n", e.getKey(), openByUsage.getOrDefault(e.getKey(), 0), e.getValue());
        }
        System.out.println("  layer-held rows (facade class or restored member; MixinJudge.layerHeld): follow+W "
            + layerCounts.getOrDefault("follow+W", 0) + ", facade-only-unreached " + layerCounts.getOrDefault("facade-only-unreached", 0)
            + ", facade-only-open " + layerCounts.getOrDefault("facade-only-open", 0) + "; restored vanilla-reads "
            + layerCounts.getOrDefault("restored-vanilla-reads", 0) + ", restored-unreached " + layerCounts.getOrDefault("restored-unreached", 0)
            + ", restored-partial " + layerCounts.getOrDefault("restored-partial", 0)
            + ", restored-open " + layerCounts.getOrDefault("restored-open", 0)
            + ", restored-override " + layerCounts.getOrDefault("restored-override", 0)
            + ", restored-priority " + layerCounts.getOrDefault("restored-priority", 0)
            + "; facade-access (shadow/accessor/invoker on a facade) " + layerCounts.getOrDefault("facade-access", 0)
            + "; partial, return untouched "
            + layerCounts.getOrDefault("partial-return", 0));
    }

    static String key(String[] r) {
        return r[0] + "\t" + r[1] + "\t" + r[2] + "\t" + r[3] + "\t" + r[4];
    }

    // ------------------------------------------------------------------ loading

    /** members the layers' mixins add to game classes (their non-shadow members): "name+desc" / "name:desc" */
    private final Map<String, Set<String>> folded = new HashMap<>();

    /** the layers' jars: a mixin aimed at a facade is judged against the facade's own code */
    private final List<Path> layers = new ArrayList<>();
    private final Map<String, ClassNode> facades = new HashMap<>();

    /**
     * the class a mixin is aimed at, with its code: W's, or -- a mixin the converter moved onto one of
     * CenturyBridge's classes with a class rename (a facade) -- the layer's
     */
    private ClassNode targetClass(String name) {
        ClassNode c = code.target(name);
        if (c != null || !name.startsWith("top/dext/")) {
            return c;
        }
        return facades.computeIfAbsent(name, n -> {
            for (Path p : layers) {
                try (ZipFile z = new ZipFile(p.toFile())) {
                    ZipEntry e = z.getEntry(n + ".class");
                    if (e != null) {
                        ClassNode cn = new ClassNode();
                        new ClassReader(z.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                        return cn;
                    }
                } catch (IOException ex) {
                    // an unreadable layer holds no facade
                }
            }
            return null;
        });
    }

    /** the members each layer's mixins restore on game classes (LayerMembers) */
    private final List<LayerMembers> restoredLayers = new ArrayList<>();

    private void loadExtra(Path p) throws IOException {
        layers.add(p);
        restoredLayers.add(LayerMembers.read(p));
        try (ZipFile z = new ZipFile(p.toFile())) {
            for (ZipEntry e : java.util.Collections.list(z.entries())) {
                if (e.getName().endsWith(".class")) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(z.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_CODE);
                    extra.putIfAbsent(cn.name, cn);
                    fold(cn);
                }
            }
        }
    }

    /** a layer mixin's own members exist on its targets at runtime (a cover the converter leaves a call to) */
    private void fold(ClassNode cn) {
        AnnotationNode mx = null;
        for (List<AnnotationNode> l : java.util.Arrays.asList(cn.invisibleAnnotations, cn.visibleAnnotations)) {
            for (AnnotationNode a : l == null ? List.<AnnotationNode>of() : l) {
                if (a.desc.equals("Lorg/spongepowered/asm/mixin/Mixin;")) {
                    mx = a;
                }
            }
        }
        if (mx == null) {
            return;
        }
        List<String> targets = new ArrayList<>();
        for (Object o : MixinRepair.list(MixinRepair.values(mx).get("value"))) {
            if (o instanceof Type t) {
                targets.add(t.getInternalName());
            }
        }
        for (Object o : MixinRepair.list(MixinRepair.values(mx).get("targets"))) {
            if (o instanceof String t) {
                targets.add(t.replace('.', '/'));
            }
        }
        for (String t : targets) {
            Set<String> into = folded.computeIfAbsent(t, k -> new HashSet<>());
            for (MethodNode m : cn.methods) {
                if (!m.name.startsWith("<") && MixinRepair.annotations(m).stream().noneMatch(a -> a.desc.equals(SHADOW))) {
                    into.add(m.name + m.desc);
                }
            }
            for (FieldNode f : cn.fields) {
                if (!has(f, SHADOW)) {
                    into.add(f.name + ":" + f.desc);
                }
            }
        }
    }

    private void loadJar(Path p) throws IOException {
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(p))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.isDirectory()) {
                    jar.put(e.getName(), zin.readAllBytes());
                }
            }
        }
        com.google.gson.JsonObject fmj = com.google.gson.JsonParser.parseString(
            new String(jar.get("fabric.mod.json"), StandardCharsets.UTF_8)).getAsJsonObject();
        for (com.google.gson.JsonElement c : fmj.getAsJsonArray("mixins")) {
            String name = c.isJsonObject() ? c.getAsJsonObject().get("config").getAsString() : c.getAsString();
            com.google.gson.JsonObject cfg = com.google.gson.JsonParser.parseString(
                new String(jar.get(name), StandardCharsets.UTF_8)).getAsJsonObject();
            String pkg = cfg.get("package").getAsString().replace('.', '/');
            com.google.gson.JsonObject mappings = null;
            if (cfg.has("refmap") && jar.get(cfg.get("refmap").getAsString()) != null) {
                com.google.gson.JsonObject rm = com.google.gson.JsonParser.parseString(
                    new String(jar.get(cfg.get("refmap").getAsString()), StandardCharsets.UTF_8)).getAsJsonObject();
                mappings = rm.has("mappings") ? rm.getAsJsonObject("mappings") : null;
            }
            for (String k : new String[] {"mixins", "client", "server"}) {
                if (cfg.has(k)) {
                    for (com.google.gson.JsonElement m : cfg.getAsJsonArray(k)) {
                        String mn = pkg + "/" + m.getAsString().replace('.', '/');
                        listed.add(mn);
                        if (mappings != null && mappings.has(mn)) {
                            refmaps.put(mn, mappings.getAsJsonObject(mn));
                        }
                    }
                }
            }
        }
        // the jar's own non-probe classes (the engine's bridge classes) resolve like a layer's
        for (Map.Entry<String, byte[]> en : jar.entrySet()) {
            if (en.getKey().endsWith(".class") && !en.getKey().startsWith("cbmprobe/")) {
                ClassNode cn = new ClassNode();
                new ClassReader(en.getValue()).accept(cn, 0);
                extra.put(cn.name, cn);
            }
        }
    }

    /** a listed probe mixin, parsed with its code, or null when gone or stripped */
    private ClassNode mixin(String name) {
        byte[] b = jar.get(name + ".class");
        if (b == null || !listed.contains(name)) {
            return null;
        }
        ClassNode cn = new ClassNode();
        new ClassReader(b).accept(cn, ClassReader.SKIP_FRAMES);
        return cn;
    }

    // ------------------------------------------------------------------ one member

    private String judgeMember(ClassNode mx, String target, String name, int vcount, Set<String> seen) {
        MethodNode m = null;
        for (MethodNode c : mx.methods) {
            if (c.name.equals(name)) {
                m = c;
                break;
            }
        }
        if (m == null) {
            for (FieldNode f : mx.fields) {
                if (f.name.equals(name)) {
                    return judgeShadowField(f, target);
                }
            }
            String guarded = judgeGuarded(mx, name, vcount);
            if (guarded != null) {
                return guarded.isEmpty() ? null : guarded;
            }
            return "member gone from the mixin";
        }
        return judgeMethod(mx, target, m, vcount, seen);
    }

    /**
     * A handler MixinBridge.baseGuard moved: its injector now sits in a guard mixin of the superclass
     * W keeps the code in, judged there. "" when it fits, the reason when not, null when the member
     * was not moved.
     */
    private String judgeGuarded(ClassNode mx, String name, int vcount) {
        for (MethodNode c : mx.methods) {
            for (AnnotationNode a : MixinRepair.annotations(c)) {
                if (!a.desc.equals(MixinBridge.GUARDED)) {
                    continue;
                }
                Map<String, Object> v = MixinRepair.values(a);
                if (!name.equals(v.get("of"))) {
                    continue;
                }
                ClassNode gm = mixin(String.valueOf(v.get("mixin")));
                if (gm == null) {
                    return "guard mixin gone";
                }
                List<String> ts = MixinRepair.mixinTargets(gm, null);
                ClassNode tc = ts.size() == 1 ? targetClass(ts.get(0)) : null;
                if (tc == null) {
                    return "guard mixin's target gone";
                }
                for (MethodNode h : gm.methods) {
                    if (h.name.equals(v.get("handler"))) {
                        for (AnnotationNode an : MixinRepair.annotations(h)) {
                            if (MixinRepair.values(an).containsKey("method")) {
                                String why = judgeInjector(gm, tc, h, an, vcount, null);
                                return why == null ? "" : why;
                            }
                        }
                        return "guarded injector disabled";
                    }
                }
                return "guarded handler gone";
            }
        }
        return null;
    }

    private String judgeMethod(ClassNode mx, String target, MethodNode m, int vcount, Set<String> seen) {
        if (!seen.add(mx.name + "." + m.name + m.desc)) {
            return null;
        }
        ClassNode tc = targetClass(target);
        if (tc == null) {
            return "target class gone";
        }
        boolean st = (m.access & Opcodes.ACC_STATIC) != 0;
        List<AnnotationNode> ans = MixinRepair.annotations(m);
        for (AnnotationNode an : ans) {
            switch (an.desc) {
                case SHADOW -> {
                    return declared(tc, m.name, m.desc, st) ? null : methodFate(tc, m.name, m.desc, st, "shadow");
                }
                case ACCESSOR -> {
                    String fname = refName(refmap(mx), str(an));
                    Type[] a = Type.getArgumentTypes(m.desc);
                    String fdesc = a.length == 1 ? a[0].getDescriptor() : Type.getReturnType(m.desc).getDescriptor();
                    return declaredField(tc, fname, fdesc, st) ? null : fieldFate(tc, fname, fdesc, st);
                }
                case INVOKER -> {
                    String iname = refName(refmap(mx), str(an));
                    if ("<init>".equals(iname)) {
                        String d = m.desc.substring(0, m.desc.indexOf(')') + 1) + "V";
                        return declared(tc, "<init>", d, false) ? null : methodFate(tc, "<init>", d, false, "constructor");
                    }
                    return declared(tc, iname, m.desc, st) ? null : methodFate(tc, iname, m.desc, st, "method");
                }
                default -> {
                    if (MixinRepair.values(an).containsKey("method")) {
                        return judgeInjector(mx, tc, m, an, vcount, refmap(mx));
                    }
                }
            }
        }
        if (m.name.matches("h[0-9]+")) {
            return "injector disabled"; // a probe handler whose injector annotation repair dropped
        }
        // a plain member: what the engine put in place of an annotated one, or a shadow's user
        return judgeBody(mx, target, m, seen);
    }

    private String judgeInjector(ClassNode mx, ClassNode tc, MethodNode h, AnnotationNode an, int vcount, com.google.gson.JsonObject rm) {
        Map<String, Object> v = MixinRepair.values(an);
        List<String> sels = MixinRepair.strings(v.get("method"));
        boolean st = (h.access & Opcodes.ACC_STATIC) != 0;
        List<MixinRepair.Anchor> anchors = code.anchors(an, v, rm);
        String first = null;
        for (String sel : sels) {
            MixinRepair.Sel s = MixinRepair.Sel.parse(MixinRepair.refmapLookup(rm, sel));
            ClassNode oc = s.owner() != null ? targetClass(s.owner()) : tc;
            if (oc == null) {
                first = first != null ? first : "target class gone";
                continue;
            }
            List<MethodNode> cands = s.match(oc);
            if (cands.isEmpty() && oc == tc) {
                if (restoredByTarget == null) {
                    restoredByTarget = MixinRepair.restoredOverrides(jar);
                }
                cands = MixinRepair.restored(restoredByTarget, tc.name, s); // an override the converter put back (a super call)
            }
            LayerMembers restoring = null;
            for (int li = 0; cands.isEmpty() && li < restoredLayers.size(); li++) {
                cands = restoredLayers.get(li).match(oc.name, s); // a member a layer restores on the W class
                restoring = cands.isEmpty() ? null : restoredLayers.get(li);
            }
            if (cands.isEmpty()) {
                first = first != null ? first : methodFate(oc, s.name(), s.desc(), st, "target");
                continue;
            }
            for (MethodNode wm : cands) {
                String why = fits(h, an, wm, st, anchors, vcount, oc);
                int mp = MixinRepair.values(MixinRepair.mixinAnnotation(mx)).get("priority") instanceof Integer p ? p : LayerMembers.DEFAULT_PRIORITY;
                if (why == null && restoring != null && !restoring.injectable(wm, mp, MixinRepair.values(an))) {
                    // Mixin refuses: the layer mixin merged the member at a priority not below this mixin's, and the
                    // injection point is not HEAD / RETURN / TAIL (s7item's server log: "cannot inject into ...
                    // merged by ... with priority 1000")
                    rowLayer = "restored-priority";
                    why = "restored-member: merged at priority " + restoring.priority(wm) + ", not below the mod's " + mp;
                }
                if (why == null && (restoring != null || oc.name.startsWith("top/dext/"))) {
                    why = layerHeld(h, an, wm, restoring, vcount, oc);
                }
                if (why == null && MixinRepair.annotations(h).stream().anyMatch(a -> a.desc.equals(MixinBridge.RETURN_UNTOUCHED))) {
                    // the target's answer changed type with no converter: the handler runs, but a callback reading or
                    // setting the answer would not work (MixinBridge refuses those); never counted fully bridged
                    rowLayer = "partial-return";
                    why = "partial: return untouched only";
                }
                if (why == null) {
                    return null;
                }
                first = first != null ? first : why;
            }
        }
        return first != null ? first : "no selector";
    }

    /** the layer-held category of the row being judged (layerHeld), tallied per row; null: none */
    private String rowLayer;

    /**
     * One rule for an injector that applies to a member the layer holds -- a member a layer mixin restores on a W
     * class (LayerMembers), or a method of a facade (a class the step renames to one of CenturyBridge's). The member
     * holds V's code for converted mods' calls; whether the mod's mixin changes what vanilla does is the question.
     * Bridged when
     * <ul><li>a layer route sends vanilla's W read path through it (@CbVanillaReads naming a layer mixin: restored
     * members), or
     * <li>the injector also applies to the W method holding the same code (MixinBridge.alsoOnW's copy, marked
     * AlsoOnW, judged on its own target: facades), or
     * <li>V's own code never called the member (by name, in V's code; not past the wall, where V's names are not
     * W's): the mixin changed only mods' calls on V too, as it does now.</ul>
     * Otherwise open: "facade-only: vanilla path unmixed" / "restored-member: vanilla path unmixed".
     */
    private String layerHeld(MethodNode h, AnnotationNode an, MethodNode wm, LayerMembers restoring, int vcount, ClassNode oc) {
        boolean facade = restoring == null;
        if (!facade && (wm.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) == 0 && overridesW(oc, wm.name, wm.desc)) {
            // the restored member overrides a method of the W class's supertypes: vanilla's virtual calls of that
            // method on this class run it (SmithingTrimRecipe.matches over SmithingRecipe's default)
            rowLayer = "restored-override";
            return null;
        }
        if (!facade && restoring.vanillaReads(wm) != null) {
            if (restoring.carries(wm) != null && !LayerMembers.carried(restoring.carries(wm), an, MixinRepair.values(an))) {
                // vanilla's read path reaches the restored member only for what the marker names (its answer at
                // HEAD/RETURN, say), not the body this injector acts inside or on (swall's "carries")
                rowLayer = "restored-partial";
                return "restored-member: vanilla path partial";
            }
            rowLayer = "restored-vanilla-reads";
            return null;
        }
        if (facade) {
            for (AnnotationNode a : MixinRepair.annotations(h)) {
                if (!a.desc.equals(MixinBridge.ALSO_ON_W)) {
                    continue;
                }
                Map<String, Object> v = MixinRepair.values(a);
                String why = judgeCopy(String.valueOf(v.get("mixin")), String.valueOf(v.get("handler")), vcount);
                rowLayer = why == null ? "follow+W" : "facade-only-open";
                return why == null ? null : "facade-only: the W copy " + why;
            }
        }
        boolean unreached = source != null && sourceRows.isEmpty() && !source.invokes(wm.name);
        if (unreached) {
            rowLayer = facade ? "facade-only-unreached" : "restored-unreached";
            return null;
        }
        rowLayer = facade ? "facade-only-open" : "restored-open";
        return facade ? "facade-only: vanilla path unmixed" : "restored-member: vanilla path unmixed";
    }

    /** whether a supertype of the W class (W's code, not the layer's) declares an overridable name+desc */
    private boolean overridesW(ClassNode c, String name, String desc) {
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>();
        Set<String> seen = new HashSet<>();
        if (c.superName != null) todo.add(c.superName);
        if (c.interfaces != null) todo.addAll(c.interfaces);
        while (!todo.isEmpty()) {
            String n = todo.poll();
            if (!seen.add(n) || n.startsWith("java/")) {
                continue;
            }
            ClassNode s = targetClass(n);
            if (s == null || s.name.startsWith("top/dext/")) {
                continue;
            }
            for (MethodNode m : s.methods) {
                if (m.name.equals(name) && m.desc.equals(desc)
                        && (m.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL)) == 0) {
                    return true;
                }
            }
            if (s.superName != null) todo.add(s.superName);
            if (s.interfaces != null) todo.addAll(s.interfaces);
        }
        return false;
    }

    /** the follow+W copy of a handler, judged on its own mixin's target: null when it applies */
    private String judgeCopy(String mixinName, String handler, int vcount) {
        ClassNode gm = mixin(mixinName);
        if (gm == null) {
            return "gone (its mixin is not in the jar)";
        }
        List<String> ts = MixinRepair.mixinTargets(gm, null);
        ClassNode tc = ts.size() == 1 ? targetClass(ts.get(0)) : null;
        if (tc == null) {
            return "has no target class";
        }
        for (MethodNode g : gm.methods) {
            if (g.name.equals(handler)) {
                for (AnnotationNode an : MixinRepair.annotations(g)) {
                    if (MixinRepair.values(an).containsKey("method")) {
                        return judgeInjector(gm, tc, g, an, vcount, null);
                    }
                }
                return "was disabled";
            }
        }
        return "is gone";
    }

    private String fits(MethodNode h, AnnotationNode an, MethodNode wm, boolean st, List<MixinRepair.Anchor> anchors, int vcount,
                        ClassNode oc) {
        if (((wm.access & Opcodes.ACC_STATIC) != 0) != st) {
            return "target static-ness changed";
        }
        if (an.desc.endsWith("/Inject;")) {
            Type[] hp = Type.getArgumentTypes(h.desc);
            int ci = -1;
            for (int i = 0; i < hp.length; i++) {
                if (hp[i].getDescriptor().equals(CI) || hp[i].getDescriptor().equals(CIR)) {
                    ci = i;
                    break;
                }
            }
            if (ci < 0) {
                return "handler takes no callback";
            }
            boolean wVoid = Type.getReturnType(wm.desc).getSort() == Type.VOID;
            if (hp[ci].getDescriptor().equals(CIR) == wVoid) {
                return "target return changed (callback)";
            }
            Type[] wa = Type.getArgumentTypes(wm.desc);
            if (ci > 0) {
                if (ci != wa.length) {
                    return "target arguments changed";
                }
                for (int i = 0; i < ci; i++) {
                    if (!hp[i].equals(wa[i])) {
                        return "target argument " + i + " changed";
                    }
                }
            }
        }
        if (anchors == null) {
            return null; // not evaluable statically: not judged (MixinRepair leaves it alone too)
        }
        if (source != null && anchors.size() > 1 && rowOrdinal < 0) {
            String split = sites(anchors, wm);
            if (split != null) {
                return split.isEmpty() ? null : split;
            }
        }
        for (MixinRepair.Anchor a : anchors) {
            if (a.kind().equals("LOCAL")) {
                continue;
            }
            int c = 0;
            if (wm.instructions != null) {
                for (AbstractInsnNode in : wm.instructions) {
                    if (MixinRepair.matchesAnchor(in, a)) {
                        c++;
                    }
                }
            }
            if (c == 0) {
                return "anchor gone";
            }
            if (source != null && a.ordinal() >= 0 && a.ordinal() < c && anchors.size() == 1 && vcount > 0 && c != vcount) {
                String pinned = pinned(a, wm, c, an.desc.equals("Lorg/spongepowered/asm/mixin/injection/Inject;"));
                if ("".equals(pinned)) {
                    continue;
                }
                if (pinned != null) {
                    return pinned;
                }
            }
            if (c <= a.ordinal()) {
                return "anchor ordinal gone (" + vcount + " -> " + c + ")";
            }
            if (vcount > 0 && c != vcount) {
                return "anchor count " + vcount + " -> " + c;
            }
        }
        return null;
    }

    /**
     * An anchor pinned to occurrence #a.ordinal of W's body, which holds it another number of times than
     * V held the row's anchor: "" when that occurrence stands where the row's V site stood (the same
     * anchor: its counterpart by SiteMatch.match; another one, retargeted: the instruction standing in
     * the V site's place, SiteMatch.counterpart), a fate when it stands elsewhere, null when the row's
     * V site is not one site to compare (left to the count rule).
     */
    private String pinned(MixinRepair.Anchor a, MethodNode wm, int c, boolean positional) {
        ClassNode vc = source.target(rowOwner);
        int p = rowTarget.indexOf('(');
        MethodNode vm = null;
        if (vc != null && p > 0) {
            for (MethodNode m : vc.methods) {
                if (m.name.equals(rowTarget.substring(0, p)) && m.desc.equals(rowTarget.substring(p))) {
                    vm = m;
                }
            }
        }
        MixinRepair.Anchor va = indexAnchor(rowAnchor);
        if (vm == null || va == null) {
            return null;
        }
        List<AbstractInsnNode> vo = SiteMatch.occurrences(vm, in -> MixinRepair.matchesAnchor(in, va));
        if (rowOrdinal < 0 ? vo.size() != 1 : rowOrdinal >= vo.size()) {
            return null;
        }
        int vi = Math.max(rowOrdinal, 0);
        AbstractInsnNode site = vo.get(vi);
        List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> MixinRepair.matchesAnchor(in, a));
        if (wo.size() > 0 && MixinRepair.matchesAnchor(wo.get(0), va)) {
            int j = SiteMatch.match(vm, vo, wm, wo)[vi];
            if (j == a.ordinal()) {
                return "";
            }
            return j < 0 ? "anchor ordinal site lost (V #" + vi + " of " + vo.size() + ", W " + c + ")"
                : "anchor ordinal shifted (V #" + vi + " is W #" + j + ", pinned to #" + a.ordinal() + ")";
        }
        // what MixinBridge.counterpartAnchor takes as standing there: for a positional @Inject also a value read
        // of another kind, or the keyed consumer a construction now runs inside (SiteMatch.counterpart)
        AbstractInsnNode cp = SiteMatch.counterpart(vm, site, wm, in -> SiteMatch.sameKind(site, in)
            || positional && SiteMatch.valueRead(site) && SiteMatch.valueRead(in), positional);
        return cp != null && wo.indexOf(cp) == a.ordinal() ? "" : null;
    }

    /**
     * One anchor named once per site (MixinRepair sites-split: one @At per ordinal): "" when the ordinals
     * are exactly the W counterparts of the row's V occurrences (SiteMatch.match), a fate when they are
     * not, null when these are not one anchor at several ordinals standing for the row's anchor.
     */
    private String sites(List<MixinRepair.Anchor> anchors, MethodNode wm) {
        MixinRepair.Anchor a = anchors.get(0);
        Set<Integer> ords = new HashSet<>();
        for (MixinRepair.Anchor x : anchors) {
            if (!x.role().equals("main") || x.ordinal() < 0 || !x.kind().equals(a.kind()) || !java.util.Objects.equals(x.owner(), a.owner())
                    || !java.util.Objects.equals(x.name(), a.name()) || !java.util.Objects.equals(x.desc(), a.desc())
                    || !java.util.Objects.equals(x.constant(), a.constant())) {
                return null;
            }
            ords.add(x.ordinal());
        }
        MixinRepair.Anchor va = indexAnchor(rowAnchor);
        ClassNode vc = source.target(rowOwner);
        int p = rowTarget.indexOf('(');
        if (va == null || vc == null || p < 0 || !va.kind().equals(a.kind()) || !java.util.Objects.equals(va.owner(), a.owner())
                || !java.util.Objects.equals(va.name(), a.name()) || !java.util.Objects.equals(va.desc(), a.desc())
                || !java.util.Objects.equals(va.constant(), a.constant())) {
            return null;
        }
        MethodNode vm = null;
        for (MethodNode m : vc.methods) {
            if (m.name.equals(rowTarget.substring(0, p)) && m.desc.equals(rowTarget.substring(p))) {
                vm = m;
            }
        }
        if (vm == null) {
            return null;
        }
        List<AbstractInsnNode> vo = SiteMatch.occurrences(vm, in -> MixinRepair.matchesAnchor(in, va));
        List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> MixinRepair.matchesAnchor(in, a));
        Set<Integer> want = new HashSet<>();
        for (int j : SiteMatch.match(vm, vo, wm, wo)) {
            if (j < 0) {
                return "anchor sites: a V occurrence has no counterpart in W";
            }
            want.add(j);
        }
        return want.equals(ords) ? "" : "anchor sites differ (pinned " + ords + ", V's counterparts " + want + ")";
    }

    /** an anchor as the probe index writes it: INVOKE Lo;n(d)r, FIELD Lo;n:d, NEW o, CONSTANT xValue=v */
    static MixinRepair.Anchor indexAnchor(String s) {
        int sp = s.indexOf(' ');
        if (sp < 0) {
            return null;
        }
        String kind = s.substring(0, sp);
        String rest = s.substring(sp + 1);
        try {
            switch (kind) {
                case "INVOKE" -> {
                    int semi = rest.indexOf(';');
                    int par = rest.indexOf('(');
                    return new MixinRepair.Anchor(kind, rest.substring(1, semi), rest.substring(semi + 1, par), rest.substring(par), null, -1);
                }
                case "FIELD" -> {
                    int semi = rest.indexOf(';');
                    int col = rest.indexOf(':', semi);
                    return new MixinRepair.Anchor(kind, rest.substring(1, semi), rest.substring(semi + 1, col), rest.substring(col + 1), null, -1);
                }
                case "NEW" -> {
                    return new MixinRepair.Anchor(kind, rest, null, null, null, -1);
                }
                case "CONSTANT" -> {
                    int eq = rest.indexOf('=');
                    String k = rest.substring(0, eq);
                    String v = rest.substring(eq + 1);
                    Object c = switch (k) {
                        case "intValue" -> Integer.valueOf(v);
                        case "floatValue" -> Float.valueOf(v);
                        case "longValue" -> Long.valueOf(v);
                        case "doubleValue" -> Double.valueOf(v);
                        case "stringValue" -> v;
                        default -> null;
                    };
                    return c == null ? null : new MixinRepair.Anchor(kind, null, null, null, c, -1);
                }
                default -> {
                    return null;
                }
            }
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private String judgeShadowField(FieldNode f, String target) {
        ClassNode tc = targetClass(target);
        if (tc == null) {
            return "target class gone";
        }
        boolean st = (f.access & Opcodes.ACC_STATIC) != 0;
        return declaredField(tc, f.name, f.desc, st) ? null : fieldFate(tc, f.name, f.desc, st);
    }

    /** every call and field access of a plain member resolves; the mixin's own members are judged as such */
    private String judgeBody(ClassNode mx, String target, MethodNode m, Set<String> seen) {
        if (m.instructions == null) {
            return null;
        }
        for (AbstractInsnNode in : m.instructions) {
            if (in instanceof MethodInsnNode mi) {
                if (mi.owner.equals(mx.name)) {
                    MethodNode own = method(mx, mi.name, mi.desc);
                    if (own == null) {
                        return "calls " + mi.name + mi.desc + ", which the mixin lacks";
                    }
                    String why = judgeMethod(mx, target, own, 0, seen);
                    if (why != null) {
                        return why;
                    }
                } else if (!resolvesMethod(mi.owner, mi.name, mi.desc)) {
                    return "calls " + shortName(mi.owner) + "." + mi.name + mi.desc + ", unresolved";
                } else if (extra.containsKey(mi.owner) && !mi.owner.startsWith("net/minecraft/")) {
                    ClassNode hc = extra.get(mi.owner);
                    MethodNode hm = method(hc, mi.name, mi.desc);
                    if (hm != null && hm.instructions != null && hm.instructions.size() > 0 && seen.add(hc.name + "." + hm.name + hm.desc)) {
                        // a bridge class's static: its own references resolve too
                        for (AbstractInsnNode x : hm.instructions) {
                            if (!(x instanceof MethodInsnNode y)) {
                                continue;
                            }
                            if (y.owner.equals(mx.name)) {
                                MethodNode own = method(mx, y.name, y.desc);
                                String why = own == null ? "calls " + y.name + y.desc + ", which the mixin lacks"
                                    : judgeMethod(mx, target, own, 0, seen);
                                if (why != null) {
                                    return why;
                                }
                            } else if (!resolvesMethod(y.owner, y.name, y.desc)) {
                                return "calls " + shortName(y.owner) + "." + y.name + y.desc + ", unresolved";
                            }
                        }
                    }
                }
            } else if (in instanceof FieldInsnNode fi) {
                if (fi.owner.equals(mx.name)) {
                    FieldNode own = null;
                    for (FieldNode f : mx.fields) {
                        if (f.name.equals(fi.name) && f.desc.equals(fi.desc)) {
                            own = f;
                        }
                    }
                    if (own == null) {
                        return "uses field " + fi.name + ", which the mixin lacks";
                    }
                    if (has(own, SHADOW)) {
                        String why = judgeShadowField(own, target);
                        if (why != null) {
                            return why;
                        }
                    }
                } else if (!resolvesField(fi.owner, fi.name, fi.desc)) {
                    return "uses " + shortName(fi.owner) + "." + fi.name + ":" + fi.desc + ", unresolved";
                }
            } else if (in instanceof TypeInsnNode ti && ti.getOpcode() == Opcodes.NEW && judgedOwner(ti.desc) && cls(ti.desc) == null) {
                return "creates " + shortName(ti.desc) + ", gone";
            }
        }
        return null;
    }

    private static boolean has(FieldNode f, String desc) {
        for (List<AnnotationNode> l : List.of(f.visibleAnnotations == null ? List.<AnnotationNode>of() : f.visibleAnnotations,
                f.invisibleAnnotations == null ? List.<AnnotationNode>of() : f.invisibleAnnotations)) {
            for (AnnotationNode a : l) {
                if (a.desc.equals(desc)) {
                    return true;
                }
            }
        }
        return false;
    }

    // ------------------------------------------------------------------ resolution

    /** owners the judge holds classes for: the game, CenturyBridge's layers and bridge classes */
    private static boolean judgedOwner(String owner) {
        return Chain.minecraftOwned(owner) || owner.startsWith("top/dext/") || owner.startsWith("centurybridge/");
    }

    private ClassNode cls(String name) {
        ClassNode c = code.target(name);
        return c != null ? c : extra.get(name);
    }

    private boolean resolvesMethod(String owner, String name, String desc) {
        if (owner.startsWith("centurybridge/gen/Tombstones")) {
            return false; // links, and throws the moment it is reached: not bridged
        }
        if (owner.startsWith("[") || !judgedOwner(owner)) {
            return true;
        }
        return walk(owner, c -> method(c, name, desc) != null || folded.getOrDefault(c.name, Set.of()).contains(name + desc));
    }

    private boolean resolvesField(String owner, String name, String desc) {
        if (!judgedOwner(owner)) {
            return true;
        }
        return walk(owner, c -> {
            if (folded.getOrDefault(c.name, Set.of()).contains(name + ":" + desc)) {
                return true;
            }
            for (FieldNode f : c.fields) {
                if (f.name.equals(name) && f.desc.equals(desc)) {
                    return true;
                }
            }
            return false;
        });
    }

    /** the class and its supertypes; an ancestor outside what the judge holds (the JDK, a library) counts as resolving */
    private boolean walk(String start, java.util.function.Predicate<ClassNode> test) {
        return walk(start, test, true);
    }

    /** {@code lenient}: an ancestor outside what the judge holds counts as resolving (link judging, not fates) */
    private boolean walk(String start, java.util.function.Predicate<ClassNode> test, boolean lenient) {
        java.util.ArrayDeque<String> todo = new java.util.ArrayDeque<>(List.of(start));
        Set<String> seen = new HashSet<>();
        boolean unknownAncestor = false;
        boolean first = true;
        while (!todo.isEmpty()) {
            String n = todo.poll();
            if (!seen.add(n)) {
                continue;
            }
            ClassNode c = cls(n);
            if (c == null) {
                if (first) {
                    return false;
                }
                unknownAncestor |= !judgedOwner(n);
                continue;
            }
            first = false;
            if (test.test(c)) {
                return true;
            }
            if (c.superName != null) {
                todo.add(c.superName);
            }
            todo.addAll(c.interfaces);
        }
        return lenient && unknownAncestor;
    }

    private static boolean declared(ClassNode c, String name, String desc, boolean st) {
        MethodNode m = method(c, name, desc);
        return m != null && ((m.access & Opcodes.ACC_STATIC) != 0) == st;
    }

    private static boolean declaredField(ClassNode c, String name, String desc, boolean st) {
        for (FieldNode f : c.fields) {
            if (f.name.equals(name) && f.desc.equals(desc)) {
                return ((f.access & Opcodes.ACC_STATIC) != 0) == st;
            }
        }
        return false;
    }

    /** what the release has in place of a method a mixin names: its fate, for the damage table */
    private String methodFate(ClassNode c, String name, String desc, boolean st, String what) {
        if (desc == null) {
            return what + " gone";
        }
        MethodNode same = method(c, name, desc);
        if (same != null) {
            return what + " static-ness changed";
        }
        List<String> other = new ArrayList<>();
        for (MethodNode m : c.methods) {
            if (m.name.equals(name)) {
                other.add(m.desc);
            }
        }
        if (!other.isEmpty()) {
            return what + " desc changed: " + String.join(" | ", other.subList(0, Math.min(3, other.size())));
        }
        if (!name.startsWith("<") && walk(c.name, k -> k != c && method(k, name, desc) != null, false)) {
            return what + " moved to a supertype";
        }
        return what + " gone";
    }

    private String fieldFate(ClassNode c, String name, String desc, boolean st) {
        for (FieldNode f : c.fields) {
            if (f.name.equals(name)) {
                return f.desc.equals(desc) ? "field static-ness changed" : "field type changed: " + f.desc;
            }
        }
        if (walk(c.name, k -> k != c && k.fields.stream().anyMatch(f -> f.name.equals(name) && f.desc.equals(desc)), false)) {
            return "field moved to a supertype";
        }
        return "field gone";
    }

    static MethodNode method(ClassNode c, String name, String desc) {
        for (MethodNode m : c.methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return m;
            }
        }
        return null;
    }

    private static String str(AnnotationNode an) {
        Object v = MixinRepair.values(an).get("value");
        return v instanceof String s ? s : null;
    }

    private static String shortName(String internal) {
        return internal.substring(internal.lastIndexOf('/') + 1);
    }
}
