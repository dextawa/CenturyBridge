package top.dext.centurybridge.data;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import top.dext.centurybridge.engine.SiteMatch;
import top.dext.centurybridge.engine.Wiring;

/**
 * Why each open mixin damage row of a step is open: the triage of mixin_step.sh's open-mixin.tsv
 * by cause, read off V's and W's bytecode and the step's wiring only (no corpus, no guessing: a
 * counterpart is named only when the bytecode singles it out, otherwise the row says "ambiguous").
 *
 * Target causes (the mixin's target member does not stand on W as V had it):
 *   target-wired       the step's wiring has an entry for the member (MixinBridge should carry it)
 *   target-api         public/protected member with no wiring: the API side of the step is still open
 *   target-private-rename   a private/package-private member W keeps under another name: the body
 *                      is the same instruction for instruction (self references normalised)
 *   target-private-reshape  same name, another descriptor (private, unwired)
 *   target-private-similar  one W member of the same kind is singled out by body similarity (not
 *                      identical): a candidate, to be judged
 *   target-private-gone     no counterpart
 *   target-moved-up    W declares it on a supertype (a @Shadow / injector on the subclass cannot see it)
 *   class-renamed / class-gone   the target class
 * Anchor causes (the target method stands, an @At does not, or not as often):
 *   anchor-wired       the anchor member is in the step's wiring (rename/reshape/redirect)
 *   anchor-owner-changed    the same name and descriptor is called through another owner now
 *   anchor-into-lambda / anchor-into-callee / anchor-into-caller   the anchor stands in a lambda the
 *                      W method creates, a method it calls (depth 2), or a same-class method calling it
 *   anchor-private-rename / anchor-reshaped / anchor-api-gone / anchor-member-gone   the anchor's own member
 *   anchor-unused-here the anchor member still exists, nothing near the target uses it (rewritten)
 *   constant-changed   W's body has a new constant of the same type where V had this one (candidate),
 *   constant-gone      or none
 * Count causes (anchor count / ordinal gone): each V occurrence matched to its W counterpart by the
 *   surrounding significant instructions (calls, fields, types, constants):
 *   count-harmless-all-kept   unordinaled, every V site has its W counterpart; W adds sites
 *   count-sites-lost          unordinaled, V sites without a counterpart
 *   ordinal-same-site         the ordinal still names the counterpart of V's site
 *   ordinal-shift             the counterpart is another ordinal (remappable: V#i -> W#j)
 *   ordinal-site-lost / count-ambiguous
 *
 *   mixin-cause <V code jar> <W code jar> <wiring.json> <open-mixin.tsv> <out.tsv> [<index.tsv> <report.md>]
 * out.tsv: the open rows with cause and evidence; stdout: counts per cause (rows and distinct targets).
 * With the isolated probe's index and the converter's report (mixin_step.sh's mixin/), a wired target
 * is split by what MixinBridge / repair / triage said about that very probe member ("wired: <why>").
 */
public final class MixinCause {

    private final Jar v;
    private final Jar w;
    /** every key of every object section of the wiring (member and class keys) */
    private final Set<String> wired = new HashSet<>();
    /** wiring keys without descriptor: "owner.name" -> full keys */
    private final Map<String, List<String>> wiredByName = new HashMap<>();
    /** wiring member keys by name+descriptor without owner: an override family's entry on its base */
    private final Map<String, List<String>> wiredByMember = new HashMap<>();
    private final Map<String, String> classRenames = new HashMap<>();
    /** redirect sections: section -> key -> value */
    private final Map<String, Map<String, String>> redirects = new HashMap<>();
    /** the step's layer (optional): redirect bodies are read there */
    private Jar layer;

    static final class Jar {
        final ZipFile zip;
        final Map<String, ClassNode> cache = new HashMap<>();

        Jar(Path p) throws IOException {
            zip = new ZipFile(p.toFile());
        }

        ClassNode get(String name) {
            if (name == null) return null;
            return cache.computeIfAbsent(name, n -> {
                try {
                    ZipEntry e = zip.getEntry(n + ".class");
                    if (e == null) return null;
                    ClassNode cn = new ClassNode();
                    new ClassReader(zip.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
                    return cn;
                } catch (IOException ex) {
                    return null;
                }
            });
        }

        MethodNode method(String owner, String name, String desc) {
            ClassNode cn = get(owner);
            if (cn == null) return null;
            for (MethodNode m : cn.methods) if (m.name.equals(name) && m.desc.equals(desc)) return m;
            return null;
        }

        FieldNode field(String owner, String name, String desc) {
            ClassNode cn = get(owner);
            if (cn == null) return null;
            for (FieldNode f : cn.fields) if (f.name.equals(name) && (desc == null || f.desc.equals(desc))) return f;
            return null;
        }

        /** declared on owner or a superclass / superinterface */
        boolean resolves(String owner, String name, String desc, boolean field) {
            Set<String> seen = new HashSet<>();
            List<String> todo = new ArrayList<>(List.of(owner));
            while (!todo.isEmpty()) {
                String c = todo.remove(todo.size() - 1);
                if (!seen.add(c)) continue;
                ClassNode cn = get(c);
                if (cn == null) continue;
                if (field ? field(c, name, desc) != null : method(c, name, desc) != null) return true;
                if (cn.superName != null) todo.add(cn.superName);
                todo.addAll(cn.interfaces);
            }
            return false;
        }
    }

    record Row(String[] f, String usage, String owner, String target, String anchor, int ordinal, int vcount, String raw, String now) {}

    record Verdict(String cause, String evidence) {}

    MixinCause(Path vJar, Path wJar, JsonObject wiring) throws IOException {
        v = new Jar(vJar);
        w = new Jar(wJar);
        for (Map.Entry<String, JsonElement> sec : wiring.entrySet()) {
            if (!sec.getValue().isJsonObject() || sec.getKey().startsWith("_") || sec.getKey().equals("ledger") || sec.getKey().equals("mixinLedger")) continue;
            for (String k : sec.getValue().getAsJsonObject().keySet()) {
                wired.add(k);
                if (sec.getKey().endsWith("Redirects") && sec.getValue().getAsJsonObject().get(k).isJsonPrimitive()) {
                    redirects.computeIfAbsent(sec.getKey(), x -> new HashMap<>()).put(k, sec.getValue().getAsJsonObject().get(k).getAsString());
                }
                if (sec.getKey().equals("classRenames") || sec.getKey().equals("superRenames")) {
                    JsonElement to = sec.getValue().getAsJsonObject().get(k);
                    if (to.isJsonPrimitive()) classRenames.put(k, to.getAsString());
                }
                int dot = k.lastIndexOf('.', k.indexOf('(') >= 0 ? k.indexOf('(') : k.length());
                if (dot > 0) wiredByMember.computeIfAbsent(k.substring(dot + 1), x -> new ArrayList<>()).add(k.substring(0, dot));
                int p = k.indexOf('(');
                int c = k.indexOf(':');
                int cut = p >= 0 ? p : c;
                if (cut > 0) wiredByName.computeIfAbsent(k.substring(0, cut), x -> new ArrayList<>()).add(sec.getKey());
            }
        }
    }

    public static void run(String[] args) throws IOException {
        MixinCause mc = new MixinCause(Path.of(args[1]), Path.of(args[2]), Wiring.read(Path.of(args[3])));
        List<String> lines = Files.readAllLines(Path.of(args[4]), StandardCharsets.UTF_8);
        String[] head = lines.get(0).split("\t", -1);
        Map<String, Integer> col = new HashMap<>();
        for (int i = 0; i < head.length; i++) col.put(head[i], i);
        Map<String, String> said = args.length > 7 ? said(Path.of(args[6]), Path.of(args[7])) : Map.of();
        if (args.length > 8) mc.layer = new Jar(Path.of(args[8]));
        Map<String, int[]> counts = new TreeMap<>();
        Map<String, Set<String>> targets = new HashMap<>();
        try (PrintWriter o = new PrintWriter(Files.newBufferedWriter(Path.of(args[5]), StandardCharsets.UTF_8))) {
            o.println(lines.get(0) + "\tcause\tevidence");
            for (String line : lines.subList(1, lines.size())) {
                String[] f = line.split("\t", -1);
                Row r = new Row(f, f[col.get("usage")], f[col.get("owner")], f[col.get("target")], f[col.get("anchor")],
                    Integer.parseInt(f[col.get("ordinal")]), Integer.parseInt(f[col.get("vcount")]), f[col.get("w")], f[col.get("now")]);
                Verdict vd;
                try {
                    vd = mc.judge(r);
                    if (vd.cause().endsWith("-wired") || vd.cause().equals("target-wired-on-super") || vd.cause().equals("target-stands")) {
                        String why = said.get(String.join("\t", r.usage(), r.owner(), r.target(), r.anchor(), String.valueOf(r.ordinal())));
                        if (why != null) vd = new Verdict(vd.cause() + ": " + whyKind(why), why);
                    }
                    if (vd.cause().startsWith("target-private-") || vd.cause().equals("target-api")) {
                        // body follow found V's code split across W methods: not "gone", a judgment for the step
                        String why = said.get(String.join("\t", r.usage(), r.owner(), r.target(), r.anchor(), String.valueOf(r.ordinal())));
                        if (why != null && why.contains("is split across W methods")) vd = new Verdict("target-split", why);
                    }
                    if (vd.cause().startsWith("anchor-into-")) {
                        // what descend-site said about it
                        String why = said.get(String.join("\t", r.usage(), r.owner(), r.target(), r.anchor(), String.valueOf(r.ordinal())));
                        if (why != null && why.contains("is called from")) vd = new Verdict(vd.cause() + ": several call sites", why);
                        else if (why != null && why.contains("cancels")) vd = new Verdict(vd.cause() + ": handler cancels", why);
                        else if (why != null && why.contains("matches in")) vd = new Verdict(vd.cause() + ": ambiguous", why);
                    }
                    if (mc.layer != null && vd.cause().startsWith("target-wired") && !r.target().contains(":")) {
                        String[] d = mc.delegation(r.owner(), r.target());
                        if (d != null) vd = new Verdict(vd.cause() + " / " + d[0], vd.evidence() + " | " + d[1]);
                    }
                } catch (RuntimeException ex) {
                    vd = new Verdict("triage-error", String.valueOf(ex));
                }
                o.println(line + "\t" + vd.cause() + "\t" + vd.evidence());
                counts.computeIfAbsent(vd.cause(), x -> new int[1])[0]++;
                targets.computeIfAbsent(vd.cause(), x -> new HashSet<>()).add(r.owner() + "." + r.target());
            }
        }
        int total = counts.values().stream().mapToInt(a -> a[0]).sum();
        System.out.println("mixin-cause: " + total + " open rows -> " + args[5]);
        counts.entrySet().stream().sorted((a, b) -> b.getValue()[0] - a.getValue()[0]).forEach(e ->
            System.out.printf("  %-28s %7d rows  %6d targets%n", e.getKey(), e.getValue()[0], targets.get(e.getKey()).size()));
    }

    /** per probe row key: the report line about its probe member (most specific first) */
    private static Map<String, String> said(Path index, Path report) throws IOException {
        Map<String, List<String>> bySimple = new HashMap<>();
        for (String l : Files.readAllLines(report, StandardCharsets.UTF_8)) {
            if (!l.startsWith("- mixin not bridged") && !l.startsWith("- disabled injector") && !l.startsWith("- stripped mixin")) continue;
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("([A-Za-z0-9_]+_\\d+)(\\.[A-Za-z0-9_]+)?").matcher(l);
            while (m.find()) bySimple.computeIfAbsent(m.group(1) + (m.group(2) == null ? "" : m.group(2)), x -> new ArrayList<>()).add(l);
        }
        Map<String, String> out = new HashMap<>();
        List<String> lines = Files.readAllLines(index, StandardCharsets.UTF_8);
        for (String l : lines.subList(1, lines.size())) {
            String[] f = l.split("\t", -1);
            String simple = f[0].substring(f[0].lastIndexOf('/') + 1);
            String member = f[1].contains("(") ? f[1].substring(0, f[1].indexOf('(')) : f[1];
            List<String> hit = bySimple.get(simple + "." + member);
            if (hit == null) hit = bySimple.get(simple);
            if (hit == null) continue;
            String best = hit.get(0);
            for (String h : hit) if (h.startsWith("- mixin not bridged")) { best = h; break; }
            out.put(String.join("\t", f[2], f[3], f[4], f[5], f[6]), best);
        }
        return out;
    }

    private static String whyKind(String l) {
        if (l.startsWith("- stripped mixin")) {
            if (l.contains("became an interface")) return "stripped, target became an interface";
            if (l.contains("not declared on")) return "stripped, member not declared on target";
            return "stripped";
        }
        if (l.startsWith("- disabled injector")) return l.contains("target gone") ? "disabled, target gone" : "disabled, anchor gone";
        if (l.contains("is split across W methods")) return "code split across W methods";
        if (l.contains("short of evidence")) return "no W method holds its code";
        if (l.contains("W keeps its code there") && l.contains("no longer stands")) return "follows by code, anchor gone in the new method";
        if (l.contains("W keeps its code there")) return "follows by code, handler cannot take it";
        if (l.contains("in its place W's body has no single")) return "anchor gone, nothing stands in its place";
        if (l.contains("through an adapter")) return "hook reaches another method through an adapter";
        if (l.contains("no longer stands")) return "follows, anchor gone in the new method";
        if (l.contains("does not declare")) return "follows, new method not declared on target";
        if (l.contains("the wiring does not say")) return "hook through adapter, argument map unsaid";
        if (l.contains("shape changed")) return "call shape changed under a call-handling injector";
        if (l.contains("standing") && l.contains("time")) return "new anchor count differs";
        if (l.contains("stripped for other damage")) return "carried, mixin stripped for other damage";
        if (l.contains("override hook through")) return "override hook through an adapter";
        String t = l.replaceAll("net/minecraft/[A-Za-z0-9_/$]+|(method|field|class)_\\d+|\\([^)]*\\)\\S*|\\d+", "");
        return t.length() > 70 ? t.substring(0, 70) : t;
    }

    // ------------------------------------------------------------------ the judgement

    private static boolean anchorFate(String fate) {
        return fate.startsWith("anchor ");
    }

    Verdict judge(Row r) {
        boolean member = r.usage().startsWith("shadow") || r.usage().startsWith("accessor") || r.usage().startsWith("invoker");
        if (member || !anchorFate(r.now()) && !anchorFate(r.raw())) {
            return judgeTarget(r);
        }
        // the target method on W: the same, or where the step's wiring / a private rename put it
        MethodNode vm = v.method(r.owner(), name(r.target()), desc(r.target()));
        MethodNode wm = w.method(r.owner(), name(r.target()), desc(r.target()));
        if (wm == null) {
            Verdict t = judgeTarget(r);
            if (!t.cause().startsWith("target-private-rename")) return t;
            String[] nd = t.evidence().split(" ")[0].split("(?=\\()", 2);
            wm = w.method(r.owner(), nd[0], nd[1]);
            if (wm == null) return t;
        }
        if (vm == null) return new Verdict("triage-no-v-method", r.owner() + "." + r.target());
        Anchor a = Anchor.parse(r.anchor());
        if (a == null) return new Verdict("triage-unparsed-anchor", r.anchor());
        String fate = anchorFate(r.now()) ? r.now() : r.raw();
        int wc = count(wm, a);
        if (wc > 0) return judgeCount(r, a, vm, wm, fate);
        return judgeAnchorGone(r, a, vm, wm);
    }

    // ------------------------------------------------------------------ target

    private Verdict judgeTarget(Row r) {
        String u = r.usage();
        boolean fieldUse = u.startsWith("shadow-field") || u.startsWith("accessor");
        ClassNode vc = v.get(r.owner());
        ClassNode wc = w.get(r.owner());
        if (wc == null) {
            String to = classRenames.get(r.owner());
            if (to != null) {
                return new Verdict(to.startsWith("top/dext/") ? "class-renamed-to-layer" : "class-renamed-vanilla", r.owner() + " -> " + to);
            }
            // intermediary names are stable across releases: a class W lacks is gone; a mojang name may only
            // have been renamed (a classRenames entry the step lacks)
            return new Verdict(r.owner().matches("net/minecraft/class_\\d+(\\$.*)?") ? "class-gone" : "class-missing-unwired", r.owner());
        }
        if (vc == null) return new Verdict("triage-no-v-class", r.owner());
        if (fieldUse) {
            int c = r.target().indexOf(':');
            String n = r.target().substring(0, c), d = r.target().substring(c + 1);
            FieldNode vf = v.field(r.owner(), n, d);
            if (isWired(r.owner() + "." + r.target(), r.owner() + "." + n)) return new Verdict("target-wired", r.owner() + "." + r.target());
            if (vf == null) return new Verdict("triage-no-v-field", r.target());
            if (w.field(r.owner(), n, null) == null && w.resolves(r.owner(), n, d, true)) return new Verdict("target-moved-up", n);
            if (wiredOnSuper(r.owner(), n + ":" + d)) return new Verdict("target-wired-on-super", n + ":" + d);
            if (api(vf.access)) return new Verdict("target-api", access(vf.access) + " " + n);
            return privateField(r.owner(), vf);
        }
        String n = name(r.target()), d = desc(r.target());
        if (isWired(r.owner() + "." + r.target(), r.owner() + "." + n)) return new Verdict("target-wired", r.owner() + "." + r.target());
        MethodNode vm = v.method(r.owner(), n, d);
        if (vm == null) return new Verdict("triage-no-v-method", r.target());
        MethodNode wm = w.method(r.owner(), n, d);
        if (wm != null) {
            // the member stands: what failed is the handler shape / static-ness / the mixin as a whole
            return new Verdict("target-stands", r.now());
        }
        if (!n.startsWith("<") && w.resolves(r.owner(), n, d, false)) {
            // MixinBridge restores the dropped override as a super call: HEAD / RETURN hook there again,
            // an anchor of V's own override body has nothing to stand in
            if (!r.anchor().isEmpty() && !r.anchor().equals("HEAD") && !r.anchor().equals("RETURN")) {
                return new Verdict("target-moved-up-anchor", n + d + ": W dropped the override, its body with it");
            }
            return new Verdict("target-moved-up", n + d);
        }
        if (wiredOnSuper(r.owner(), n + d)) return new Verdict("target-wired-on-super", n + d);
        if (api(vm.access)) return new Verdict("target-api", access(vm.access) + " " + n + d);
        return privateMethod(r.owner(), vm);
    }

    private Verdict privateMethod(String owner, MethodNode vm) {
        ClassNode vc = v.get(owner), wc = w.get(owner);
        List<MethodNode> sameName = new ArrayList<>();
        for (MethodNode m : wc.methods) if (m.name.equals(vm.name)) sameName.add(m);
        if (sameName.size() == 1 && !vm.name.startsWith("<")) return new Verdict("target-private-reshape", vm.name + sameName.get(0).desc);
        // W members V does not have under that name+desc: the candidates
        Set<String> vkeys = new HashSet<>();
        for (MethodNode m : vc.methods) vkeys.add(m.name + m.desc);
        String body = shape(vm, owner, v);
        List<MethodNode> same = new ArrayList<>();
        List<MethodNode> cands = new ArrayList<>();
        for (MethodNode m : wc.methods) {
            if (vkeys.contains(m.name + m.desc) || (m.access & Opcodes.ACC_STATIC) != (vm.access & Opcodes.ACC_STATIC)) continue;
            if (m.name.startsWith("<") != vm.name.startsWith("<")) continue;
            cands.add(m);
            if (shape(m, owner, w).equals(body)) same.add(m);
        }
        if (same.size() == 1) return new Verdict("target-private-rename", same.get(0).name + same.get(0).desc + " (same body)");
        if (same.size() > 1) return new Verdict("target-private-ambiguous", same.size() + " W methods with the same body");
        MethodNode best = null;
        double bs = 0, second = 0;
        List<String> vs = SiteMatch.sig(vm);
        for (MethodNode m : cands) {
            double s = similarity(vs, SiteMatch.sig(m));
            if (s > bs) { second = bs; bs = s; best = m; } else if (s > second) second = s;
        }
        if (best != null && bs >= 0.6 && bs - second >= 0.2) {
            return new Verdict("target-private-similar", best.name + best.desc + String.format(" (%.2f, next %.2f)", bs, second));
        }
        return new Verdict("target-private-gone", access(vm.access) + " " + vm.name + vm.desc);
    }

    private Verdict privateField(String owner, FieldNode vf) {
        ClassNode vc = v.get(owner), wc = w.get(owner);
        Set<String> vnames = new HashSet<>();
        for (FieldNode f : vc.fields) vnames.add(f.name);
        FieldNode sameName = w.field(owner, vf.name, null);
        if (sameName != null) return new Verdict("target-private-reshape", vf.name + ":" + sameName.desc);
        // a field is known by where it is used: the methods of its class touching it, by opcode
        Set<String> vuse = fieldUses(vc, owner, vf.name);
        List<FieldNode> hits = new ArrayList<>();
        for (FieldNode f : wc.fields) {
            if (vnames.contains(f.name) || !f.desc.equals(vf.desc) || (f.access & Opcodes.ACC_STATIC) != (vf.access & Opcodes.ACC_STATIC)) continue;
            Set<String> wuse = fieldUses(wc, owner, f.name);
            if (!vuse.isEmpty() && wuse.equals(vuse)) hits.add(f);
        }
        if (hits.size() == 1) return new Verdict("target-private-rename", hits.get(0).name + ":" + hits.get(0).desc + " (same uses)");
        if (hits.size() > 1) return new Verdict("target-private-ambiguous", hits.size() + " W fields with the same uses");
        return new Verdict("target-private-gone", access(vf.access) + " " + vf.name + ":" + vf.desc);
    }

    private static Set<String> fieldUses(ClassNode cn, String owner, String name) {
        Set<String> out = new HashSet<>();
        for (MethodNode m : cn.methods) {
            if (m.instructions == null || (m.access & Opcodes.ACC_SYNTHETIC) != 0) continue;
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof FieldInsnNode fi && fi.owner.equals(owner) && fi.name.equals(name)) out.add(m.name + m.desc + "#" + fi.getOpcode());
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ anchors

    record Anchor(String kind, String owner, String name, String desc, Object constant) {
        static Anchor parse(String s) {
            int sp = s.indexOf(' ');
            if (sp < 0) return null;
            String kind = s.substring(0, sp), rest = s.substring(sp + 1);
            switch (kind) {
                case "INVOKE" -> {
                    int semi = rest.indexOf(';'), p = rest.indexOf('(');
                    return new Anchor(kind, rest.substring(1, semi), rest.substring(semi + 1, p), rest.substring(p), null);
                }
                case "FIELD" -> {
                    int semi = rest.indexOf(';'), c = rest.indexOf(':');
                    return new Anchor(kind, rest.substring(1, semi), rest.substring(semi + 1, c), rest.substring(c + 1), null);
                }
                case "NEW" -> {
                    return new Anchor(kind, rest, null, null, null);
                }
                case "CONSTANT" -> {
                    int eq = rest.indexOf('=');
                    String k = rest.substring(0, eq), val = rest.substring(eq + 1);
                    Object c = switch (k) {
                        case "intValue" -> Integer.valueOf(val);
                        case "floatValue" -> Float.valueOf(val);
                        case "longValue" -> Long.valueOf(val);
                        case "doubleValue" -> Double.valueOf(val);
                        case "stringValue" -> val;
                        case "classValue" -> Type.getType(val.startsWith("L") || val.startsWith("[") ? val : "L" + val + ";");
                        default -> null;
                    };
                    return c == null ? null : new Anchor(kind, null, null, null, c);
                }
                default -> {
                    return null;
                }
            }
        }

        boolean matches(AbstractInsnNode in) {
            return switch (kind) {
                case "INVOKE" -> in instanceof MethodInsnNode mi && mi.owner.equals(owner) && mi.name.equals(name) && mi.desc.equals(desc);
                case "FIELD" -> in instanceof FieldInsnNode fi && fi.owner.equals(owner) && fi.name.equals(name) && fi.desc.equals(desc);
                case "NEW" -> in instanceof TypeInsnNode ti && ti.getOpcode() == Opcodes.NEW && ti.desc.equals(owner);
                case "CONSTANT" -> constantOf(in) != null && constantOf(in).equals(constant);
                default -> false;
            };
        }

        /** the same name and descriptor through any owner */
        boolean matchesAnyOwner(AbstractInsnNode in) {
            return switch (kind) {
                case "INVOKE" -> in instanceof MethodInsnNode mi && mi.name.equals(name) && mi.desc.equals(desc);
                case "FIELD" -> in instanceof FieldInsnNode fi && fi.name.equals(name) && fi.desc.equals(desc);
                default -> false;
            };
        }
    }

    static Object constantOf(AbstractInsnNode in) {
        return SiteMatch.constantOf(in);
    }

    private static int count(MethodNode m, Anchor a) {
        int c = 0;
        if (m.instructions != null) for (AbstractInsnNode in : m.instructions) if (a.matches(in)) c++;
        return c;
    }

    private Verdict judgeAnchorGone(Row r, Anchor a, MethodNode vm, MethodNode wm) {
        if (a.kind().equals("CONSTANT")) {
            String moved = findIn(r.owner(), wm, a);
            if (moved != null) return new Verdict(moved.split(" ")[0], moved.substring(moved.indexOf(' ') + 1));
            return constantChange(vm, wm, a);
        }
        String key = a.kind().equals("NEW") ? a.owner() : a.owner() + "." + a.name() + (a.kind().equals("FIELD") ? ":" : "") + a.desc();
        if (a.kind().equals("NEW") ? wiredClass(a.owner()) != null || isWired(a.owner() + ".<init>", a.owner() + ".<init>") : isWired(key, a.owner() + "." + a.name())) {
            return new Verdict("anchor-wired", key);
        }
        if (!a.kind().equals("NEW") && wm.instructions != null) {
            for (AbstractInsnNode in : wm.instructions) {
                if (a.matchesAnyOwner(in)) {
                    String o = in instanceof MethodInsnNode mi ? mi.owner : ((FieldInsnNode) in).owner;
                    return new Verdict("anchor-owner-changed", a.owner() + " -> " + o);
                }
            }
        }
        boolean exists = a.kind().equals("NEW") ? w.get(a.owner()) != null || v.get(a.owner()) == null
            : w.resolves(a.owner(), a.name(), a.desc(), a.kind().equals("FIELD")) || !inGame(a.owner()) && v.get(a.owner()) == null;
        if (exists) {
            String moved = findIn(r.owner(), wm, a);
            if (moved != null) return new Verdict(moved.split(" ")[0], moved.substring(moved.indexOf(' ') + 1));
            return new Verdict("anchor-unused-here", key);
        }
        // the anchor's own member is gone from W
        if (a.kind().equals("NEW")) return new Verdict("anchor-member-gone", "class " + a.owner());
        ClassNode avc = v.get(a.owner());
        if (avc == null) return new Verdict("anchor-member-gone", "outside the game: " + key);
        if (a.kind().equals("INVOKE")) {
            MethodNode am = v.method(a.owner(), a.name(), a.desc());
            if (am == null) return new Verdict("anchor-member-gone", "inherited: " + key);
            if (w.get(a.owner()) == null) return new Verdict("anchor-member-gone", "class gone: " + a.owner());
            if (api(am.access)) return new Verdict("anchor-api-gone", key);
            Verdict p = privateMethod(a.owner(), am);
            return new Verdict(p.cause().replace("target-", "anchor-"), p.evidence());
        }
        FieldNode af = v.field(a.owner(), a.name(), a.desc());
        if (af == null) return new Verdict("anchor-member-gone", "inherited: " + key);
        if (w.get(a.owner()) == null) return new Verdict("anchor-member-gone", "class gone: " + a.owner());
        if (api(af.access)) return new Verdict("anchor-api-gone", key);
        Verdict p = privateField(a.owner(), af);
        return new Verdict(p.cause().replace("target-", "anchor-"), p.evidence());
    }

    private static boolean inGame(String owner) {
        return owner.startsWith("net/minecraft/") || owner.startsWith("com/mojang/blaze3d/");
    }

    /** where the anchor stands now near the W method: "cause evidence" or null */
    private String findIn(String owner, MethodNode wm, Anchor a) {
        ClassNode wc = w.get(owner);
        // lambdas the method creates (and their lambdas)
        for (MethodNode l : lambdas(wc, wm, 3)) if (count(l, a) > 0) return "anchor-into-lambda " + l.name + l.desc;
        // calls the method makes, depth 2, any game class
        Set<String> seen = new HashSet<>();
        List<String[]> frontier = new ArrayList<>();
        frontier.add(new String[] {owner, wm.name, wm.desc});
        for (int depth = 1; depth <= 2; depth++) {
            List<String[]> next = new ArrayList<>();
            List<String> hits = new ArrayList<>();
            for (String[] c : frontier) {
                MethodNode m = w.method(c[0], c[1], c[2]);
                if (m == null || m.instructions == null) continue;
                for (AbstractInsnNode in : m.instructions) {
                    if (!(in instanceof MethodInsnNode mi) || !inGame(mi.owner)) continue;
                    String k = mi.owner + "." + mi.name + mi.desc;
                    if (!seen.add(k)) continue;
                    MethodNode cm = resolveMethod(mi.owner, mi.name, mi.desc);
                    if (cm == null) continue;
                    if (count(cm, a) > 0) hits.add(k);
                    next.add(new String[] {mi.owner, mi.name, mi.desc});
                }
            }
            if (hits.size() == 1) return "anchor-into-callee depth " + depth + " " + hits.get(0);
            if (hits.size() > 1) return "anchor-into-callee depth " + depth + " ambiguous: " + hits.size() + " callees, e.g. " + hits.get(0);
            frontier = next;
        }
        // same-class callers of the method
        List<String> callers = new ArrayList<>();
        for (MethodNode m : wc.methods) {
            if (m == wm || m.instructions == null) continue;
            boolean calls = false;
            for (AbstractInsnNode in : m.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(owner) && mi.name.equals(wm.name) && mi.desc.equals(wm.desc)) calls = true;
            }
            if (calls && count(m, a) > 0) callers.add(m.name + m.desc);
        }
        if (callers.size() == 1) return "anchor-into-caller " + callers.get(0);
        if (callers.size() > 1) return "anchor-into-caller ambiguous: " + callers.size() + " callers, e.g. " + callers.get(0);
        return null;
    }

    private MethodNode resolveMethod(String owner, String name, String desc) {
        for (String c = owner; c != null; ) {
            ClassNode cn = w.get(c);
            if (cn == null) return null;
            MethodNode m = w.method(c, name, desc);
            if (m != null) return m;
            c = cn.superName;
        }
        return null;
    }

    private static List<MethodNode> lambdas(ClassNode cn, MethodNode m, int depth) {
        List<MethodNode> out = new ArrayList<>();
        if (depth == 0 || m.instructions == null) return out;
        for (AbstractInsnNode in : m.instructions) {
            if (!(in instanceof InvokeDynamicInsnNode indy)) continue;
            for (Object arg : indy.bsmArgs) {
                if (arg instanceof Handle h && h.getOwner().equals(cn.name)) {
                    for (MethodNode c : cn.methods) {
                        if (c.name.equals(h.getName()) && c.desc.equals(h.getDesc()) && (c.access & Opcodes.ACC_SYNTHETIC) != 0) {
                            out.add(c);
                            out.addAll(lambdas(cn, c, depth - 1));
                        }
                    }
                }
            }
        }
        return out;
    }

    private Verdict constantChange(MethodNode vm, MethodNode wm, Anchor a) {
        Map<Object, Integer> vcs = constants(vm), wcs = constants(wm);
        List<String> fresh = new ArrayList<>();
        for (Map.Entry<Object, Integer> e : wcs.entrySet()) {
            if (e.getKey().getClass() == a.constant().getClass() && !vcs.containsKey(e.getKey())) fresh.add(String.valueOf(e.getKey()));
        }
        if (fresh.size() == 1) return new Verdict("constant-changed", a.constant() + " -> " + fresh.get(0));
        if (fresh.size() > 1) return new Verdict("constant-changed-ambiguous", a.constant() + " -> one of " + fresh);
        return new Verdict("constant-gone", String.valueOf(a.constant()));
    }

    private static Map<Object, Integer> constants(MethodNode m) {
        Map<Object, Integer> out = new LinkedHashMap<>();
        if (m.instructions != null) {
            for (AbstractInsnNode in : m.instructions) {
                Object c = constantOf(in);
                if (c != null) out.merge(c, 1, Integer::sum);
            }
        }
        return out;
    }

    // ------------------------------------------------------------------ counts and ordinals

    private Verdict judgeCount(Row r, Anchor a, MethodNode vm, MethodNode wm, String fate) {
        List<AbstractInsnNode> vo = occurrences(vm, a), wo = occurrences(wm, a);
        int[] map = SiteMatch.match(vm, vo, wm, wo);
        if (r.ordinal() < 0) {
            int kept = 0;
            for (int j : map) if (j >= 0) kept++;
            if (kept == vo.size()) {
                return new Verdict("count-harmless-all-kept", vo.size() + " -> " + wo.size() + ": every V site kept, W adds " + (wo.size() - vo.size()));
            }
            if (wo.size() > vo.size()) {
                // more sites now, yet not every V site singled out by its context
                return new Verdict("count-ambiguous", vo.size() + " -> " + wo.size() + ": " + kept + " V sites matched by context");
            }
            return new Verdict("count-sites-lost", vo.size() + " -> " + wo.size() + ": " + kept + " V sites matched by context");
        }
        int i = r.ordinal();
        if (i >= map.length) return new Verdict("triage-ordinal-out-of-range", i + " of " + map.length);
        int j = map[i];
        if (j < 0) return new Verdict("ordinal-site-lost", "V #" + i + " of " + vo.size() + " has no unique counterpart in W's " + wo.size());
        if (j == i) return new Verdict("ordinal-same-site", "V #" + i + " = W #" + j + " (" + vo.size() + " -> " + wo.size() + ")");
        return new Verdict("ordinal-shift", "V #" + i + " -> W #" + j + " (" + vo.size() + " -> " + wo.size() + ")");
    }

    private static List<AbstractInsnNode> occurrences(MethodNode m, Anchor a) {
        List<AbstractInsnNode> out = new ArrayList<>();
        if (m.instructions != null) for (AbstractInsnNode in : m.instructions) if (a.matches(in)) out.add(in);
        return out;
    }

    private static double similarity(List<String> a, List<String> b) {
        if (a.isEmpty() && b.isEmpty()) return 1;
        int n = a.size(), m = b.size();
        if ((long) n * m > 4_000_000L) return 0;
        int[] prev = new int[m + 1], cur = new int[m + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) cur[j] = a.get(i - 1).equals(b.get(j - 1)) ? prev[j - 1] + 1 : Math.max(prev[j], cur[j - 1]);
            int[] t = prev; prev = cur; cur = t;
        }
        return 2.0 * prev[m] / (n + m);
    }

    /** a body instruction by instruction, references to the class's own non-API members normalised */
    private static String shape(MethodNode m, String owner, Jar jar) {
        StringBuilder b = new StringBuilder();
        if (m.instructions == null) return "";
        for (AbstractInsnNode in : m.instructions) {
            int op = in.getOpcode();
            if (op < 0) continue;
            b.append(op);
            if (in instanceof MethodInsnNode mi) {
                MethodNode t = mi.owner.equals(owner) ? jar.method(owner, mi.name, mi.desc) : null;
                b.append(' ').append(mi.owner).append('.').append(t != null && !api(t.access) ? "#self" : mi.name + mi.desc);
            } else if (in instanceof FieldInsnNode fi) {
                FieldNode t = fi.owner.equals(owner) ? jar.field(owner, fi.name, fi.desc) : null;
                b.append(' ').append(fi.owner).append('.').append(t != null && !api(t.access) ? "#self" : fi.name).append(':').append(fi.desc);
            } else if (in instanceof TypeInsnNode ti) {
                b.append(' ').append(ti.desc);
            } else if (in instanceof VarInsnNode vi) {
                b.append(' ').append(vi.var);
            } else if (in instanceof IincInsnNode ii) {
                b.append(' ').append(ii.var).append(',').append(ii.incr);
            } else if (in instanceof InvokeDynamicInsnNode id) {
                b.append(' ').append(id.name).append(id.desc);
            } else {
                Object c = constantOf(in);
                if (c != null) b.append(' ').append(c);
            }
            b.append(';');
        }
        return b.toString();
    }

    // ------------------------------------------------------------------ wiring

    private boolean isWired(String fullKey, String nameKey) {
        return wired.contains(fullKey) || wiredByName.containsKey(nameKey);
    }

    /**
     * What a redirect of owner.target (on owner or a V supertype) does in the layer: {kind, evidence} --
     * "delegates" (one call, into the game), "cb-code" (calls only the layer), "computes" (several), or
     * null when the member is not redirected.
     */
    String[] delegation(String owner, String target) {
        Set<String> seen = new HashSet<>();
        List<String> todo = new ArrayList<>(List.of(owner));
        while (!todo.isEmpty()) {
            String c = todo.remove(0);
            if (!seen.add(c)) continue;
            for (String sec : List.of("staticRedirects", "instanceRedirects", "interfaceCallRedirects", "superRedirects")) {
                String val = redirects.getOrDefault(sec, Map.of()).get(c + "." + target);
                if (val == null) continue;
                String cls = val.contains("#") ? val.substring(0, val.indexOf('#')) : val;
                String name = val.contains("#") ? val.substring(val.indexOf('#') + 1) : name(target);
                ClassNode cn = layer.get(cls);
                if (cn == null) return new String[] {"redirect class missing", val};
                MethodNode body = null;
                for (MethodNode m : cn.methods) {
                    if (!m.name.equals(name)) continue;
                    boolean exact = m.desc.equals(desc(target)) || m.desc.equals("(L" + c + ";" + desc(target).substring(1));
                    if (exact || body == null) body = m;
                }
                if (body == null || body.instructions == null) return new String[] {"redirect body missing", val};
                List<String> game = new ArrayList<>(), cb = new ArrayList<>(), other = new ArrayList<>();
                for (AbstractInsnNode in : body.instructions) {
                    if (in instanceof MethodInsnNode mi) {
                        String k = mi.owner + "." + mi.name + mi.desc;
                        if (inGame(mi.owner)) game.add(k);
                        else if (mi.owner.startsWith("top/dext/")) cb.add(k);
                        else other.add(k);
                    } else if (in instanceof InvokeDynamicInsnNode) {
                        other.add("indy");
                    }
                }
                String sn = sec.replace("Redirects", "");
                if (game.size() == 1 && cb.isEmpty() && other.isEmpty()) return new String[] {sn + " delegates to one game call", game.get(0)};
                if (game.isEmpty() && other.isEmpty()) return new String[] {sn + " into CB code", cb.size() + " CB calls"};
                return new String[] {sn + " computes", game.size() + " game, " + cb.size() + " CB, " + other.size() + " other calls"};
            }
            ClassNode vc = v.get(c);
            if (vc != null) {
                if (vc.superName != null) todo.add(vc.superName);
                todo.addAll(vc.interfaces);
            }
        }
        return null;
    }

    /** the member (name+desc) wired on a supertype of owner in V: the override family's entry */
    private boolean wiredOnSuper(String owner, String member) {
        List<String> owners = wiredByMember.get(member);
        if (owners == null) return false;
        Set<String> seen = new HashSet<>();
        List<String> todo = new ArrayList<>(List.of(owner));
        while (!todo.isEmpty()) {
            String c = todo.remove(todo.size() - 1);
            if (!seen.add(c)) continue;
            if (owners.contains(c)) return true;
            ClassNode cn = v.get(c);
            if (cn == null) continue;
            if (cn.superName != null) todo.add(cn.superName);
            todo.addAll(cn.interfaces);
        }
        return false;
    }

    private String wiredClass(String cls) {
        return wired.contains(cls) ? "(wired)" : null;
    }

    private static boolean api(int acc) {
        return (acc & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) != 0;
    }

    private static String access(int acc) {
        return (acc & Opcodes.ACC_PUBLIC) != 0 ? "public" : (acc & Opcodes.ACC_PROTECTED) != 0 ? "protected"
            : (acc & Opcodes.ACC_PRIVATE) != 0 ? "private" : "package";
    }

    private static String name(String target) {
        int p = target.indexOf('(');
        return p < 0 ? target : target.substring(0, p);
    }

    private static String desc(String target) {
        int p = target.indexOf('(');
        return p < 0 ? "" : target.substring(p);
    }
}
