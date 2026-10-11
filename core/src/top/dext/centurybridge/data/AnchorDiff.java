package top.dext.centurybridge.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;

/**
 * The mixin side of a segment's full diff: what a mod's injectors can target in release V,
 * and whether it is still there in W. A mixin names a method of a vanilla class and a point
 * in its body (HEAD/RETURN/TAIL, a call, a field access, a NEW, a constant); when W changed
 * the method's descriptor, moved it, or rewrote the body, the point is not found and the
 * injection is lost. For every method with code in V -- private ones and lambda bodies too,
 * mixins target those -- every injection point is checked against the same method in W:
 *
 *   method:  same | desc-changed (same owner and name, one overload) | moved (the name lives in
 *            another class now) | gone
 *   anchor:  kept | desc-changed (same owner and name) | owner-changed (same name and desc,
 *            another owner: moved up or down the hierarchy) | relocated (gone from this method,
 *            present in another method of the class) | gone
 *
 * Intermediary names are stable across releases, so a surviving member keeps its name; across
 * the namespace wall the V side is translated first (--wall inter-to-mojang.tsv). Nothing here
 * looks at a corpus.
 *
 *   anchor-diff <V code jar> <W code jar> <out.tsv> [--wall <inter-to-mojang.tsv>]
 */
public final class AnchorDiff {

    record Anchor(String kind, String owner, String name, String desc) {
        String key() {
            return kind + " " + owner + "." + name + desc;
        }
    }

    /** an anonymous (or local) class somewhere in the name: Outer$1, Outer$1$Inner */
    private static final java.util.regex.Pattern ANONYMOUS = java.util.regex.Pattern.compile("[$][0-9]");

    private final Map<String, String> classMap = new HashMap<>();
    private final Map<String, String> memberMap = new HashMap<>(); // "owner.name" -> new name
    /** intermediary member names are unique across classes: the fallback for a reference through a subclass owner */
    private final Map<String, String> nameOnly = new HashMap<>();

    public static void run(String[] args) throws IOException {
        AnchorDiff d = new AnchorDiff();
        for (int i = 4; i < args.length; i++) {
            if (args[i].equals("--wall")) {
                d.loadWall(Path.of(args[++i]));
            }
        }
        Map<String, ClassNode> v = load(Path.of(args[1]));
        Map<String, ClassNode> w = load(Path.of(args[2]));
        d.compare(v, w, Path.of(args[3]));
    }

    private void loadWall(Path tsv) throws IOException {
        for (String line : Files.readAllLines(tsv, StandardCharsets.UTF_8)) {
            String[] f = line.split("\t");
            if (f.length < 3) {
                continue;
            }
            if (f[0].equals("C")) {
                classMap.put(f[1], f[2]);
            } else {
                int a = f[1].lastIndexOf('.');
                int b = f[2].lastIndexOf('.');
                if (a > 0 && b > 0) {
                    String to = f[2].substring(b + 1);
                    memberMap.put(f[1], to);
                    nameOnly.merge(f[1].substring(a + 1), to, (x, y) -> x.equals(y) ? x : "");
                }
            }
        }
    }

    private static Map<String, ClassNode> load(Path jar) throws IOException {
        Map<String, ClassNode> out = new LinkedHashMap<>();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (ZipEntry e : java.util.Collections.list(z.entries())) {
                String n = e.getName();
                if (!n.endsWith(".class") || !(n.startsWith("net/minecraft/") || n.startsWith("com/mojang/"))) {
                    continue;
                }
                try (InputStream in = z.getInputStream(e)) {
                    ClassNode cn = new ClassNode();
                    new ClassReader(in).accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
                    out.put(cn.name, cn);
                }
            }
        }
        return out;
    }

    // ---------------------------------------------------------------- V names into W's namespace

    private String cls(String internal) {
        if (classMap.isEmpty()) {
            return internal;
        }
        String m = classMap.get(internal);
        if (m != null) {
            return m;
        }
        int d = internal.lastIndexOf('$');
        return d > 0 ? cls(internal.substring(0, d)) + internal.substring(d) : internal;
    }

    private String desc(String d) {
        if (classMap.isEmpty()) {
            return d;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < d.length(); i++) {
            char c = d.charAt(i);
            sb.append(c);
            if (c == 'L') {
                int end = d.indexOf(';', i);
                sb.append(cls(d.substring(i + 1, end))).append(';');
                i = end;
            }
        }
        return sb.toString();
    }

    private String member(String owner, String name) {
        if (memberMap.isEmpty()) {
            return name;
        }
        if (name.startsWith("lambda$")) {
            // lambda$<enclosing method>$<n>: the enclosing method's name is translated too
            int end = name.lastIndexOf('$');
            if (end > 7) {
                return "lambda$" + member(owner, name.substring(7, end)) + name.substring(end);
            }
            return name;
        }
        String m = memberMap.get(owner + "." + name);
        if (m == null) {
            m = nameOnly.get(name);
        }
        return m != null && !m.isEmpty() ? m : name;
    }

    private String type(String t) {
        return t.startsWith("[") ? desc(t) : cls(t);
    }

    // ---------------------------------------------------------------- anchors

    private List<Anchor> anchors(MethodNode m, boolean translate) {
        List<Anchor> out = new ArrayList<>();
        for (AbstractInsnNode in = m.instructions.getFirst(); in != null; in = in.getNext()) {
            if (in instanceof MethodInsnNode mi) {
                String o = translate ? cls(mi.owner) : mi.owner;
                out.add(new Anchor("INVOKE", o, translate ? member(mi.owner, mi.name) : mi.name,
                    translate ? desc(mi.desc) : mi.desc));
            } else if (in instanceof FieldInsnNode fi) {
                String o = translate ? cls(fi.owner) : fi.owner;
                String k = switch (fi.getOpcode()) {
                    case Opcodes.GETFIELD, Opcodes.GETSTATIC -> "FIELD-GET";
                    default -> "FIELD-PUT";
                };
                out.add(new Anchor(k, o, translate ? member(fi.owner, fi.name) : fi.name, ":" + (translate ? desc(fi.desc) : fi.desc)));
            } else if (in instanceof TypeInsnNode ti && ti.getOpcode() == Opcodes.NEW) {
                out.add(new Anchor("NEW", translate ? type(ti.desc) : ti.desc, "", ""));
            } else if (in instanceof LdcInsnNode li) {
                Object c = li.cst;
                String v = c instanceof Type t ? "class " + (translate ? type(t.getInternalName()) : t.getInternalName())
                    : c.getClass().getSimpleName() + " " + c;
                out.add(new Anchor("CONSTANT", v, "", ""));
            } else if (in instanceof IntInsnNode ii && (ii.getOpcode() == Opcodes.BIPUSH || ii.getOpcode() == Opcodes.SIPUSH)) {
                out.add(new Anchor("CONSTANT", "Integer " + ii.operand, "", ""));
            } else if (in.getOpcode() >= Opcodes.IRETURN && in.getOpcode() <= Opcodes.RETURN) {
                out.add(new Anchor("RETURN", "", "", ""));
            }
        }
        return out;
    }

    private static Map<String, Integer> counts(List<Anchor> as) {
        Map<String, Integer> c = new HashMap<>();
        for (Anchor a : as) {
            c.merge(a.key(), 1, Integer::sum);
        }
        return c;
    }

    // ---------------------------------------------------------------- compare

    private void compare(Map<String, ClassNode> v, Map<String, ClassNode> w, Path out) throws IOException {
        boolean wall = !classMap.isEmpty();
        // W: method name -> owners declaring it (moved detection); per class: method key -> node
        Map<String, List<String>> nameHomes = new HashMap<>();
        for (ClassNode c : w.values()) {
            for (MethodNode m : c.methods) {
                nameHomes.computeIfAbsent(m.name + (wall ? m.desc : ""), k -> new ArrayList<>()).add(c.name);
            }
        }
        Map<String, Integer> methodFate = new TreeMap<>();
        Map<String, Integer> anchorFate = new TreeMap<>();
        long methodsWithCode = 0;
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(out, StandardCharsets.UTF_8))) {
            pw.println("owner\tmethod\tdesc\tmethodFate\twMethod\tanchors\tkept\tdescChanged\townerChanged\trelocated\tgone\tlost(sample)");
            for (ClassNode vc : v.values()) {
                String wOwner = cls(vc.name);
                ClassNode wc = w.get(wOwner);
                for (MethodNode vm : vc.methods) {
                    if (vm.instructions == null || vm.instructions.size() == 0) {
                        continue;
                    }
                    methodsWithCode++;
                    String wName = vm.name.startsWith("<") ? vm.name : member(vc.name, vm.name);
                    String wDesc = desc(vm.desc);
                    MethodNode wm = null;
                    String fate;
                    if (wc != null) {
                        for (MethodNode m : wc.methods) {
                            if (m.name.equals(wName) && m.desc.equals(wDesc)) {
                                wm = m;
                                break;
                            }
                        }
                    }
                    if (wm != null) {
                        fate = "same";
                    } else {
                        List<MethodNode> sameName = new ArrayList<>();
                        if (wc != null && !vm.name.startsWith("<")) {
                            for (MethodNode m : wc.methods) {
                                if (m.name.equals(wName)) {
                                    sameName.add(m);
                                }
                            }
                        }
                        if (sameName.size() == 1 && !vm.name.startsWith("lambda$")) {
                            wm = sameName.get(0);
                            fate = "desc-changed";
                        } else if (wc == null && !vm.name.startsWith("<") && !vm.name.startsWith("lambda$")
                                && nameHomes.containsKey(wName + (wall ? wDesc : ""))) {
                            fate = "moved";
                        } else {
                            fate = wc == null ? "class-gone" : "gone";
                        }
                    }
                    // anonymous classes and lambda bodies are renumbered by every release: their own
                    // bucket, so they neither hide nor inflate the named methods' fates
                    String bucket = ANONYMOUS.matcher(vc.name).find() ? "anonymous:" : vm.name.startsWith("lambda$") ? "lambda:" : "";
                    fate = bucket + fate;
                    methodFate.merge(fate, 1, Integer::sum);
                    List<Anchor> va = anchors(vm, wall);
                    int kept = 0, dChanged = 0, oChanged = 0, relocated = 0, gone = 0;
                    List<String> lost = new ArrayList<>();
                    if (wm != null) {
                        List<Anchor> wa = anchors(wm, false);
                        Map<String, Integer> have = counts(wa);
                        Map<String, Integer> byOwnerName = new HashMap<>();
                        Map<String, Integer> byNameDesc = new HashMap<>();
                        for (Anchor a : wa) {
                            byOwnerName.merge(a.kind() + " " + a.owner() + "." + a.name(), 1, Integer::sum);
                            byNameDesc.merge(a.kind() + " " + a.name() + a.desc(), 1, Integer::sum);
                        }
                        Map<String, Integer> elsewhere = null;
                        for (Anchor a : va) {
                            String k = a.key();
                            Integer n = have.get(k);
                            if (n != null && n > 0) {
                                have.put(k, n - 1);
                                kept++;
                                continue;
                            }
                            if (!a.name().isEmpty() && byOwnerName.getOrDefault(a.kind() + " " + a.owner() + "." + a.name(), 0) > 0) {
                                dChanged++;
                                continue;
                            }
                            if (!a.name().isEmpty() && byNameDesc.getOrDefault(a.kind() + " " + a.name() + a.desc(), 0) > 0) {
                                oChanged++;
                                continue;
                            }
                            if (elsewhere == null) {
                                elsewhere = new HashMap<>();
                                for (MethodNode m : wc.methods) {
                                    if (m != wm && m.instructions != null) {
                                        for (Anchor x : anchors(m, false)) {
                                            elsewhere.merge(x.key(), 1, Integer::sum);
                                        }
                                    }
                                }
                            }
                            if (elsewhere.containsKey(k)) {
                                relocated++;
                            } else {
                                gone++;
                                if (lost.size() < 4) {
                                    lost.add(k);
                                }
                            }
                        }
                    } else {
                        gone = va.size();
                    }
                    anchorFate.merge(bucket + "kept", kept, Integer::sum);
                    anchorFate.merge(bucket + "desc-changed", dChanged, Integer::sum);
                    anchorFate.merge(bucket + "owner-changed", oChanged, Integer::sum);
                    anchorFate.merge(bucket + "relocated", relocated, Integer::sum);
                    anchorFate.merge(bucket + "gone", gone, Integer::sum);
                    if (fate.equals("same") && dChanged + oChanged + relocated + gone == 0) {
                        continue; // nothing a mixin could lose here
                    }
                    pw.println(String.join("\t", vc.name, vm.name, vm.desc, fate, wm == null ? "" : wm.name + wm.desc,
                        String.valueOf(va.size()), String.valueOf(kept), String.valueOf(dChanged), String.valueOf(oChanged),
                        String.valueOf(relocated), String.valueOf(gone), String.join(" | ", lost)));
                }
            }
        }
        System.out.println("anchor-diff: " + methodsWithCode + " methods with code; methods " + methodFate + "; anchors " + anchorFate
            + " -> " + out);
    }

    private AnchorDiff() {
    }
}
