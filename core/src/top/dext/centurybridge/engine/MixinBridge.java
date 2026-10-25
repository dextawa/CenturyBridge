package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 * Mixins carried across one climb step through that step's own wiring, before {@link MixinRepair}
 * and {@link MixinTriage} judge them.
 *
 * The step's wiring already says what became of most members V's mods name -- a call reshape, a
 * rename, a redirect, an override hook -- and the mod's plain code is rewritten through it. A mixin
 * aimed at the same member got nothing from it: triage stripped it, repair dropped its injector.
 * Here each mixin member is carried by the mechanism the wiring names for its member, never per mod:
 *
 *   @Shadow method / field   its uses become uses of the target's own member (the call or field
 *                            access names the target class instead of the mixin), and the step's call
 *                            and field wiring takes them exactly as it takes the same access in plain
 *                            code: reshaped, renamed, redirected, or reaching a member W moved up the
 *                            hierarchy. A field renamed on the target itself stays a shadow under its
 *                            new name, so @Mutable / @Final keep their meaning.
 *   @Invoker                 renamed on the target: retargeted. Reshaped: the invoker takes W's shape
 *                            under a new name with its target pinned, and the V shape the mod calls is
 *                            put back as a synthetic method converting V's arguments to W's and W's
 *                            answer back with the call reshape's own ops (JarProcessor.reshapeCall).
 *                            In an accessor interface that conversion lives in a static of the jar's
 *                            bridge class: the interface's own method and the copy Mixin merges into
 *                            the target both reach it. Redirected / restored by the layer / inherited:
 *                            the invoker becomes a synthetic method making that call.
 *   @Accessor                a renamed field: retargeted; an instance field getter / setter, field
 *                            redirect or getter, or a field W moved up: a synthetic method doing that.
 *   injector selectors       a target method an override hook, rename or reshape moves: the selector
 *                            follows it; a handler that takes the target's arguments gets a W-shaped
 *                            handler mapping W's arguments to V's through the hook's argument map and
 *                            wrap converters (or the inverse of the reshape's keep ops), forwarding to
 *                            the mod's own body -- what JarProcessor.synthesizeOverride does for an
 *                            override.
 *   @At INVOKE / FIELD       an anchor the step renames or reshapes: retargeted to what stands there
 *                            now, once the new anchor is counted in W's body as often as the old one
 *                            stood in V's.
 *
 * Every bridging is reported ("mixin bridged: ..."); a member the wiring maps but this cannot carry
 * faithfully is reported with the reason ("mixin not bridged: ...") and left to repair and triage,
 * which drop or strip it as before. Refmap entries are rewritten where a value came from one and no
 * other annotation of the mixin shares the key; otherwise the annotation carries the intermediary
 * target itself.
 *
 * Not across the 1.21.11 -> 26.1 namespace wall ({@link #applies}): there the converted jar takes its
 * mojang names after this pass and the refmaps with them, while the wiring keys stay intermediary, so
 * the same lookups would compare two namespaces (see repairPastWall). The 26.x steps after it are
 * mojang-named on both sides and are bridged like any other.
 */
final class MixinBridge {
    private static final Gson GSON = new Gson();
    static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    static final String MUTABLE = "Lorg/spongepowered/asm/mixin/Mutable;";
    static final String OVERWRITE = "Lorg/spongepowered/asm/mixin/Overwrite;";
    static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    static final String CI = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfo;";
    static final String CIR = "Lorg/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable;";
    static final String COERCE = "Lorg/spongepowered/asm/mixin/injection/Coerce;";
    /** MixinExtras' parameter sugar (@Local, @Share, @Cancellable): handler values besides the injector's own */
    static final String SUGAR = "Lcom/llamalad7/mixinextras/sugar/";
    /** Mixin's AccessorInfo.AccessorName pattern: the prefix, then the target name it inflects */
    private static final Pattern ACCESSOR_NAME = Pattern.compile("^(get|is|set|call|invoke|new|create)(([A-Z])(.*?))(_\\$md.*)?$");

    /** what one jar layer's pass did: bridged members, and wired members it could not carry (with why) */
    static final class Result {
        /** one carried member: the mixin class, the injector ("@Kind Mixin.handler", null for others), the line */
        record Line(String mixin, String injector, String text) {}

        final List<Line> bridged = new ArrayList<>();
        final List<String> left = new ArrayList<>();

        /**
         * The report lines once repair and triage have judged the jar. A member carried here counts as
         * bridged only if its mixin still applies and, for an injector, repair did not then disable it
         * (an anchor gone in the method it now targets): otherwise it is reported as not bridged, with
         * what took it.
         */
        List<String> report(Set<String> strippedMixins, List<String> disabledInjectors) {
            Set<String> disabled = new HashSet<>();
            Pattern key = Pattern.compile("@(\\w+) (\\S+?) -> ");
            for (String d : disabledInjectors) {
                Matcher m = key.matcher(d);
                if (m.find()) {
                    disabled.add("@" + m.group(1) + " " + m.group(2));
                }
            }
            List<String> out = new ArrayList<>();
            for (Line l : bridged) {
                if (strippedMixins.contains(l.mixin())) {
                    out.add("mixin not bridged: " + l.text() + " -- carried, but the mixin is stripped for other damage");
                } else if (l.injector() != null && disabled.contains(l.injector())) {
                    out.add("mixin not bridged: " + l.text() + " -- carried, then disabled by repair");
                } else {
                    out.add("mixin bridged: " + l.text());
                }
            }
            for (String l : left) {
                out.add("mixin not bridged: " + l);
            }
            return out;
        }
    }

    private final Chain chain;
    private final Map<String, byte[]> entries;
    private final String helperName;
    private final String step;
    private ClassNode helper;
    private int helperSeq;
    /** the config / side list / mixin package being bridged (restoreOverride lists its mixins there) */
    private String curPkg = "";
    private String curKey = "mixins";
    /** restoring mixins made for the current config, by class name */
    private final Map<String, ClassNode> restorers = new LinkedHashMap<>();

    private MixinBridge(Chain chain, Map<String, byte[]> entries, String helperName) {
        this.chain = chain;
        this.entries = entries;
        this.helperName = helperName;
        this.step = chain.to.replace('.', '_');
    }

    /**
     * Every step but the 1.21.11 -> 26.1 namespace wall: wiring keys and the jar's names are the same
     * namespace there -- intermediary before the wall, mojang names after it (26.x steps' wiring is keyed
     * by the names 26.x declares, as their mods are, and the target code jar is the release's own).
     */
    static boolean applies(Chain chain) {
        int from = Chain.RELEASES.indexOf(chain.from);
        int to = Chain.RELEASES.indexOf(chain.to);
        int wall = Chain.RELEASES.indexOf("1.21.11");
        if (chain.bridgeCode != null) {
            return true; // the wall step, with W's bodies in the analysis namespace (WallInverse)
        }
        return from >= 0 && to >= 0 && !(from <= wall && to > wall) && chain.wallClassFwd.isEmpty();
    }

    /**
     * The target name Mixin inflects from an accessor or invoker method's own name when the
     * annotation names none (AccessorInfo.AccessorName.of(name, true)): get/is/set name a field,
     * call/invoke a method, new/create a constructor; the first letter is lowered unless the rest is
     * all upper case (getCRITERIA -> CRITERIA). Null when the name has no such prefix (Mixin then
     * fails the accessor).
     */
    static String inflect(String methodName) {
        Matcher m = ACCESSOR_NAME.matcher(methodName);
        if (!m.matches()) {
            return null;
        }
        boolean upper = m.group(2).equals(m.group(2).toUpperCase(Locale.ROOT));
        return (upper ? m.group(3) : m.group(3).toLowerCase(Locale.ROOT)) + m.group(4);
    }

    /** the accessor prefix of a method name (get, call, new...), or null */
    static String inflectPrefix(String methodName) {
        Matcher m = ACCESSOR_NAME.matcher(methodName);
        return m.matches() ? m.group(1) : null;
    }

    /** the name part after the prefix, case kept (AccessorName.of(name, false)), or null */
    static String inflectRaw(String methodName) {
        Matcher m = ACCESSOR_NAME.matcher(methodName);
        return m.matches() ? m.group(3) + m.group(4) : null;
    }

    /** rewrites mixin classes, refmaps and the classes that use them in {@code entries} in place */
    static Result run(Map<String, byte[]> entries, Chain chain, String helperName) {
        Result res = new Result();
        byte[] fmjBytes = entries.get("fabric.mod.json");
        if (fmjBytes == null) {
            return res;
        }
        JsonObject fmj;
        try {
            fmj = GSON.fromJson(new String(fmjBytes, StandardCharsets.UTF_8), JsonObject.class);
        } catch (RuntimeException ex) {
            return res;
        }
        if (fmj == null || !fmj.has("mixins") || !fmj.get("mixins").isJsonArray()) {
            return res;
        }
        MixinBridge b = new MixinBridge(chain, entries, helperName);
        for (JsonElement ref : fmj.getAsJsonArray("mixins")) {
            String cfgName;
            JsonObject cfg;
            try {
                cfgName = ref.isJsonObject() ? ref.getAsJsonObject().get("config").getAsString() : ref.getAsString();
                byte[] cfgBytes = entries.get(cfgName);
                if (cfgBytes == null) {
                    continue;
                }
                cfg = GSON.fromJson(new String(cfgBytes, StandardCharsets.UTF_8), JsonObject.class);
            } catch (RuntimeException ex) {
                continue;
            }
            if (cfg == null) {
                continue;
            }
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            String refmapName = cfg.has("refmap") ? cfg.get("refmap").getAsString() : null;
            JsonObject refmapRoot = null;
            if (refmapName != null && entries.get(refmapName) != null) {
                try {
                    refmapRoot = GSON.fromJson(new String(entries.get(refmapName), StandardCharsets.UTF_8), JsonObject.class);
                } catch (RuntimeException ex) {
                    refmapRoot = null;
                }
            }
            JsonObject mappings = refmapRoot != null && refmapRoot.has("mappings") && refmapRoot.get("mappings").isJsonObject()
                ? refmapRoot.getAsJsonObject("mappings") : null;
            boolean refmapDirty = false;
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key) || !cfg.get(key).isJsonArray()) {
                    continue;
                }
                b.curPkg = pkg;
                b.curKey = key;
                for (JsonElement e : cfg.getAsJsonArray(key).deepCopy()) {
                    String cls = (pkg.isEmpty() ? "" : pkg + "/") + e.getAsString().replace('.', '/');
                    byte[] bytes = entries.get(cls + ".class");
                    if (bytes == null) {
                        continue;
                    }
                    JsonObject table = mappings != null && mappings.has(cls) && mappings.get(cls).isJsonObject()
                        ? mappings.getAsJsonObject(cls) : null;
                    refmapDirty |= b.bridgeOne(cls, bytes, table, mappings, res);
                }
            }
            if (refmapDirty) {
                entries.put(refmapName, GSON.toJson(refmapRoot).getBytes(StandardCharsets.UTF_8));
            }
            if (!b.restorers.isEmpty()) {
                // the restoring mixins join the config, on the side of the mixins that needed them
                for (Map.Entry<String, ClassNode> r : b.restorers.entrySet()) {
                    String key = (String) r.getValue().invisibleAnnotations.get(1).values.get(1);
                    r.getValue().invisibleAnnotations.remove(1);
                    String rel = (pkg.isEmpty() ? r.getKey() : r.getKey().substring(pkg.length() + 1)).replace('/', '.');
                    cfg.getAsJsonArray(key).add(rel);
                    entries.put(r.getKey() + ".class", write(r.getValue()));
                }
                entries.put(cfgName, GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
                b.restorers.clear();
            }
        }
        if (b.helper != null) {
            entries.put(helperName + ".class", write(b.helper));
        }
        return res;
    }

    /**
     * One mixin, all or nothing: the class, a working copy of its refmap table, the other classes of
     * the jar whose references it retargets and its bridge-class statics are committed together, or
     * -- an engine error part way -- not at all. Returns whether the refmap changed.
     */
    /** the restoring / guard mixins back to their method counts in before; ones created since are dropped */
    private void rollbackRestorers(Map<String, Integer> before) {
        restorers.entrySet().removeIf(e -> !before.containsKey(e.getKey()));
        restorers.forEach((k, v) -> {
            while (v.methods.size() > before.get(k)) {
                v.methods.remove(v.methods.size() - 1);
            }
        });
    }

    private boolean bridgeOne(String cls, byte[] bytes, JsonObject table, JsonObject mappings, Result res) {
        ClassNode mx = new ClassNode();
        try {
            new ClassReader(bytes).accept(mx, 0);
        } catch (RuntimeException ex) {
            return false;
        }
        Mixin m = new Mixin(mx, table == null ? null : table.deepCopy());
        byte[] out;
        Map<String, byte[]> others = new LinkedHashMap<>();
        // the restoring and guard mixins as they stood: a mixin not committed takes back what it added there
        // (a guarded injector calling a handler its uncommitted mixin never merged would fail at run time)
        Map<String, Integer> before = new HashMap<>();
        restorers.forEach((k, v) -> before.put(k, v.methods.size()));
        try {
            if (!m.bridge()) {
                res.left.addAll(m.left);
                rollbackRestorers(before);
                return false;
            }
            out = write(mx);
            for (Map.Entry<String, ClassNode> o : m.others.entrySet()) {
                others.put(o.getKey(), write(o.getValue()));
            }
        } catch (RuntimeException ex) {
            res.left.add(shortName(cls) + ": not bridged, engine error " + ex);
            rollbackRestorers(before);
            return false;
        }
        entries.put(cls + ".class", out);
        for (Map.Entry<String, byte[]> o : others.entrySet()) {
            entries.put(o.getKey() + ".class", o.getValue());
        }
        for (MethodNode hm : m.helperMethods) {
            helper().methods.add(hm);
        }
        res.bridged.addAll(m.bridged);
        res.left.addAll(m.left);
        if (m.refmapDirty && mappings != null) {
            mappings.add(cls, m.table);
            return true;
        }
        return false;
    }

    /** class name -> the jar's classes whose constant pool names it (built once per jar layer) */
    private Map<String, List<String>> classesNaming;

    private Map<String, List<String>> classesNaming() {
        if (classesNaming == null) {
            classesNaming = new HashMap<>();
            char[] buf = new char[1 << 16];
            for (Map.Entry<String, byte[]> e : entries.entrySet()) {
                String k = e.getKey();
                if (!k.endsWith(".class")) {
                    continue;
                }
                String self = k.substring(0, k.length() - 6);
                try {
                    ClassReader cr = new ClassReader(e.getValue());
                    Set<String> named = new HashSet<>();
                    for (int i = 1; i < cr.getItemCount(); i++) {
                        int off = cr.getItem(i);
                        if (off > 0 && e.getValue()[off - 1] == 7) { // CONSTANT_Class
                            named.add(cr.readUTF8(off, buf));
                        }
                    }
                    for (String n : named) {
                        classesNaming.computeIfAbsent(n, x -> new ArrayList<>()).add(self);
                    }
                } catch (RuntimeException ex) {
                    // unreadable entry: names nothing
                }
            }
        }
        return classesNaming;
    }

    /** the jar layer's bridge class: statics an accessor interface's synthetic methods call */
    private ClassNode helper() {
        if (helper == null) {
            helper = new ClassNode();
            helper.version = Opcodes.V17;
            helper.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC;
            helper.name = helperName;
            helper.superName = "java/lang/Object";
        }
        return helper;
    }

    private static byte[] write(ClassNode cn) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }

    // ------------------------------------------------------------------ what W has

    private Map<String, String> followBack;

    /**
     * The private members of a class a mixin follows into a class W adds ({@link Chain#mixinFollow}):
     * W declares them again under new intermediary ids (AbstractBoat's status field is field_54433, V
     * Boat's field_7702). Read off the two classes' same-named methods: the significant instructions
     * aligned (longest common subsequence), each gap between aligned instructions of the same length on
     * both sides paired in order; a V member of the class declared private, not declared by W's class,
     * maps to the W member of the class (same kind, same descriptor as the converted jar spells it, not
     * declared by V) standing in its place -- every time, and no other V member mapping there. Written
     * into the step's fieldRenames / methodRenames under V's names where the wiring says nothing (only
     * mixins reach a private member, so nothing else reads these keys). Returns the number written.
     */
    static int inferPrivateRenames(Chain chain, String from, String to) {
        MixinRepair w = wcode(chain);
        ClassNode vc = chain.sourceCode == null ? null : chain.sourceCode.target(to); // V's class, named as W's
        ClassNode wc = w == null ? null : w.target(to);
        if (vc == null || wc == null) {
            return 0;
        }
        Map<String, String> back = new HashMap<>();
        chain.mixinFollow().forEach((a, b) -> back.put(b, a));
        org.objectweb.asm.commons.SimpleRemapper toV = new org.objectweb.asm.commons.SimpleRemapper(back);
        Map<String, Set<String>> votes = new HashMap<>();
        for (MethodNode vm : vc.methods) {
            MethodNode wm = method(wc, vm.name, vm.desc);
            if (wm == null || vm.instructions == null || wm.instructions == null) {
                continue;
            }
            List<AbstractInsnNode> vi = memberInsns(vm), wi = memberInsns(wm);
            List<String> vs = new ArrayList<>(), ws = new ArrayList<>();
            vi.forEach(in -> vs.add(SiteMatch.sigOf(in)));
            wi.forEach(in -> ws.add(SiteMatch.sigOf(in)));
            int[] al = SiteMatch.align(vs, ws);
            if (al == null) {
                continue;
            }
            int pi = -1, pj = -1;
            for (int i = 0; i <= vs.size(); i++) {
                if (i < vs.size() && al[i] < 0) {
                    continue;
                }
                int j = i < vs.size() ? al[i] : ws.size();
                if (i - pi == j - pj) {
                    for (int d = 1; d < i - pi; d++) {
                        vote(votes, vi.get(pi + d), wi.get(pj + d), to, vc, wc, chain);
                    }
                }
                pi = i;
                pj = j;
            }
        }
        Map<String, String> into = new HashMap<>();
        Map<String, String> picked = new HashMap<>();
        votes.forEach((vk, ws) -> {
            if (ws.size() == 1) {
                String wn = ws.iterator().next();
                if (into.putIfAbsent(wn, vk) == null) {
                    picked.put(vk, wn);
                } else {
                    picked.remove(into.get(wn));
                }
            }
        });
        int n = 0;
        for (Map.Entry<String, String> e : picked.entrySet()) {
            if (into.get(e.getValue()) == null || !into.get(e.getValue()).equals(e.getKey())) {
                continue;
            }
            boolean field = e.getKey().contains(":");
            String vname = e.getKey().substring(0, e.getKey().indexOf(field ? ':' : '('));
            String vdesc = e.getKey().substring(vname.length() + (field ? 1 : 0));
            String wname = e.getValue().substring(0, e.getValue().indexOf(field ? ':' : '('));
            String key = from + "." + vname + (field ? ":" + toV.mapDesc(vdesc) : toV.mapMethodDesc(vdesc));
            Map<String, String> m = field ? chain.fieldRenames : chain.methodRenames;
            if (m.putIfAbsent(key, wname) == null) {
                System.out.println("  mixin follow, private member read off the bodies: " + key + " -> " + to + "." + wname);
                n++;
            }
        }
        return n;
    }

    private static List<AbstractInsnNode> memberInsns(MethodNode m) {
        List<AbstractInsnNode> out = new ArrayList<>();
        for (AbstractInsnNode in : m.instructions) {
            if (SiteMatch.sigOf(in) != null) {
                out.add(in);
            }
        }
        return out;
    }

    private static void vote(Map<String, Set<String>> votes, AbstractInsnNode v, AbstractInsnNode w, String owner,
                             ClassNode vc, ClassNode wc, Chain chain) {
        String vkey, wkey;
        if (v instanceof FieldInsnNode a && w instanceof FieldInsnNode b && a.getOpcode() == b.getOpcode()
                && a.owner.equals(owner) && b.owner.equals(owner) && !a.name.equals(b.name)
                && chain.renamedDesc(a.desc).equals(b.desc)) {
            FieldNode vf = vc.fields.stream().filter(f -> f.name.equals(a.name) && f.desc.equals(a.desc)).findFirst().orElse(null);
            boolean wHasV = wc.fields.stream().anyMatch(f -> f.name.equals(a.name));
            boolean vHasW = vc.fields.stream().anyMatch(f -> f.name.equals(b.name));
            boolean wDecl = wc.fields.stream().anyMatch(f -> f.name.equals(b.name) && f.desc.equals(b.desc));
            if (vf == null || (vf.access & Opcodes.ACC_PRIVATE) == 0 || wHasV || vHasW || !wDecl) {
                return;
            }
            vkey = a.name + ":" + a.desc;
            wkey = b.name + ":" + b.desc;
        } else if (v instanceof MethodInsnNode a && w instanceof MethodInsnNode b && a.getOpcode() == b.getOpcode()
                && a.owner.equals(owner) && b.owner.equals(owner) && !a.name.equals(b.name) && !a.name.startsWith("<")
                && chain.renamedDesc(a.desc).equals(b.desc)) {
            MethodNode vmn = method(vc, a.name, a.desc);
            if (vmn == null || (vmn.access & Opcodes.ACC_PRIVATE) == 0 || method(wc, a.name, b.desc) != null
                    || vc.methods.stream().anyMatch(x -> x.name.equals(b.name)) || method(wc, b.name, b.desc) == null) {
                return;
            }
            vkey = a.name + a.desc;
            wkey = b.name + b.desc;
        } else {
            return;
        }
        votes.computeIfAbsent(vkey, k -> new HashSet<>()).add(wkey);
    }

    /**
     * The name the step's wiring knows a mixin target by: a target that followed its class rename into
     * a class W adds (Chain.mixinFollow) is V's class to the wiring, whose keys are V's members.
     */
    private String vk(String owner) {
        if (followBack == null) {
            followBack = new HashMap<>();
            chain.mixinFollow().forEach((from, to) -> followBack.put(to, from));
        }
        return followBack.getOrDefault(owner, owner);
    }

    /** W's class with its real bodies (the step's code jar), or null */
    private ClassNode w(String owner) {
        MixinRepair c = wcode(chain);
        return c == null ? null : c.target(owner);
    }

    /** W's bodies as the step converts in: the wall view at the namespace wall, else the code jar */
    static MixinRepair wcode(Chain chain) {
        return chain.bridgeCode != null ? chain.bridgeCode : chain.mixinRepair;
    }

    /** V's class with its real bodies, or null */
    private ClassNode v(String owner) {
        return chain.sourceCode == null ? null : chain.sourceCode.target(owner);
    }

    /** whether W's class itself declares the method; null when W's class is not known */
    private Boolean wDeclares(String owner, String name, String desc) {
        ClassNode c = w(owner);
        if (c != null) {
            return method(c, name, desc) != null;
        }
        Set<String> d = chain.targetDecls.get(owner);
        return d == null ? null : d.contains(name + desc);
    }

    private Boolean wDeclaresField(String owner, String name, String desc) {
        ClassNode c = w(owner);
        if (c != null) {
            return field(c, name, desc) != null;
        }
        Set<String> d = chain.targetDecls.get(owner);
        return d == null ? null : d.contains(name + ":" + desc);
    }

    /** where a reference resolves on W, and the member's access flags there */
    record Found(String owner, int access) {
        boolean isPublic() {
            return (access & Opcodes.ACC_PUBLIC) != 0;
        }

        boolean isStatic() {
            return (access & Opcodes.ACC_STATIC) != 0;
        }
    }

    /**
     * The W class a reference to owner.name(desc), made from code merged into owner, resolves to the
     * JVM's way -- the class, its superclasses, then their superinterfaces -- when the member found
     * there is accessible from owner (private only on owner itself, package-private only within its
     * package). Null when it does not resolve, or W's bodies are not at hand (access cannot be
     * judged from names alone).
     */
    private Found wResolveMethod(String owner, String name, String desc) {
        List<String> up = new ArrayList<>();
        for (String c = owner; c != null; ) {
            ClassNode cn = w(c);
            if (cn == null) {
                break;
            }
            MethodNode m = method(cn, name, desc);
            if (m != null) {
                return accessible(owner, c, m.access) ? new Found(c, m.access) : null;
            }
            up.add(c);
            c = cn.superName;
        }
        ArrayDeque<String> todo = new ArrayDeque<>();
        for (String c : up) {
            todo.addAll(w(c).interfaces);
        }
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String i = todo.poll();
            if (!seen.add(i)) {
                continue;
            }
            ClassNode cn = w(i);
            if (cn == null) {
                continue;
            }
            MethodNode m = method(cn, name, desc);
            if (m != null && (m.access & (Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC)) == 0) {
                return new Found(i, m.access);
            }
            todo.addAll(cn.interfaces);
        }
        return null;
    }

    /** field form of {@link #wResolveMethod}: the superclass chain */
    private Found wResolveField(String owner, String name, String desc) {
        for (String c = owner; c != null; ) {
            ClassNode cn = w(c);
            if (cn == null) {
                return null;
            }
            FieldNode f = field(cn, name, desc);
            if (f != null) {
                return accessible(owner, c, f.access) ? new Found(c, f.access) : null;
            }
            c = cn.superName;
        }
        return null;
    }

    private static boolean accessible(String from, String declarer, int access) {
        if ((access & Opcodes.ACC_PRIVATE) != 0) {
            return declarer.equals(from);
        }
        if ((access & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0) {
            return pkgOf(declarer).equals(pkgOf(from));
        }
        return true;
    }

    private boolean isInterfaceOnW(String owner) {
        ClassNode c = w(owner);
        if (c != null) {
            return (c.access & Opcodes.ACC_INTERFACE) != 0;
        }
        return chain.targetInterfaces.contains(owner);
    }

    /**
     * Whether a W value of type {@code sub} is a {@code sup} on W (its superclasses and interfaces):
     * a parameter W narrowed (1.20.3 Block.getCloneItemStack takes a LevelReader, which is a
     * BlockGetter) hands a V handler a value of the type it declared.
     */
    private boolean wAssignable(Type sub, Type sup) {
        if (sub.getSort() != Type.OBJECT || sup.getSort() != Type.OBJECT) {
            return false;
        }
        if (sup.getInternalName().equals("java/lang/Object")) {
            return true;
        }
        ArrayDeque<String> todo = new ArrayDeque<>(List.of(sub.getInternalName()));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.poll();
            if (!seen.add(c)) {
                continue;
            }
            if (c.equals(sup.getInternalName())) {
                return true;
            }
            ClassNode cn = w(c);
            if (cn == null) {
                continue;
            }
            if (cn.superName != null) {
                todo.add(cn.superName);
            }
            todo.addAll(cn.interfaces);
        }
        return false;
    }

    // ------------------------------------------------------------------ what the step does to an access

    /**
     * What the step's wiring does to a call naming the vanilla class owner -- the lookups
     * JarProcessor makes, in its order (the call pass's reshape first, then cover, redirect,
     * rename) -- and the W member the call then reaches ({@code at}; null for a member the layer
     * restores or a redirect static). A member W moved up the hierarchy needs no wiring: the call
     * resolves there. Null when nothing carries the call; {@code why} then says what was rejected.
     */
    record CallFate(String how, String name, String desc, Chain.CallReshape reshape, String redirect,
                    String home, Found at) {}

    private CallFate callFate(String owner, String name, String desc, boolean isStatic, List<String> why) {
        String nd = name + desc;
        String rh = chain.instanceRedirectHome(chain.callReshapeIndex, owner, nd);
        if (rh != null) {
            Chain.CallReshape r = chain.callReshapes.get(rh + "." + nd);
            Found at = wResolveMethod(owner, name, r.newDesc);
            if (at == null) {
                why.add("the call reshape's " + name + r.newDesc + " does not resolve on " + shortName(owner) + " in " + chain.to);
                return null;
            }
            return new CallFate("call reshape", name, r.newDesc, r, null, null, at);
        }
        if (chain.coveredAnywhere(vk(owner), nd)) {
            return new CallFate("restored by the step layer", name, desc, null, null, null, null);
        }
        if (isStatic) {
            String rt = chain.redirectAnywhere(chain.staticRedirects, vk(owner), nd);
            if (rt != null) {
                return new CallFate("static redirect", name, desc, null, rt, null, null);
            }
        } else {
            String ih = chain.instanceRedirectHome(chain.instanceRedirects, vk(owner), nd);
            if (ih != null) {
                return new CallFate("instance redirect", name, desc, null, chain.instanceRedirects.get(ih + "." + nd), ih, null);
            }
        }
        String mrn = chain.methodRenames.get(vk(owner) + "." + nd);
        if (mrn != null) {
            Found at = wResolveMethod(owner, mrn, desc);
            if (at == null) {
                why.add("renamed to " + mrn + ", which does not resolve on " + shortName(owner) + " in " + chain.to);
                return null;
            }
            return new CallFate("renamed to " + mrn, mrn, desc, null, null, null, at);
        }
        Found at = wResolveMethod(owner, name, desc);
        if (at != null && !at.owner().equals(owner)) {
            return new CallFate("inherited from " + shortName(at.owner()), name, desc, null, null, null, at);
        }
        return null;
    }

    /**
     * Field form of {@link #callFate}, in JarProcessor's field order: cover, static field redirect /
     * getter (reads) and static field setter (writes), instance field getter / setter (each kind of
     * access it is used for), rename, a field W moved up. Returns how, or null ({@code why} says what
     * was rejected).
     */
    private String fieldFate(String owner, String name, String desc, boolean isStatic, Refs refs, List<String> why) {
        String key = vk(owner) + "." + name + ":" + desc;
        if (chain.coveredAnywhere(vk(owner), name + ":" + desc)) {
            return "restored by the step layer";
        }
        if (isStatic) {
            // reads go through a field redirect or getter (GETSTATIC), writes through a static field setter (PUTSTATIC)
            String reads = chain.fieldRedirects.containsKey(key) ? "field redirect"
                : chain.fieldGetters.containsKey(key) ? "field getter" : null;
            boolean writes = chain.staticFieldSetters.containsKey(key);
            if ((reads != null || writes) && (refs.gets == 0 || reads != null) && (refs.puts == 0 || writes)) {
                return reads == null ? "static field setter" : writes && refs.puts > 0 ? reads + " and static field setter" : reads;
            }
            if (reads != null || writes) {
                why.add(reads != null ? "written, and the step wires only its reads" : "read, and the step wires only its writes");
                return null;
            }
        } else {
            boolean g = chain.instanceFieldGetters.containsKey(key);
            boolean s = chain.instanceFieldSetters.containsKey(key);
            if ((g || s) && (refs.gets == 0 || g) && (refs.puts == 0 || s)) {
                return "instance field " + (g && s ? "getter and setter" : g ? "getter" : "setter");
            }
            if (g || s) {
                why.add("the step has a " + (g ? "getter" : "setter") + " for it, the mixin also " + (g ? "writes" : "reads") + " it");
                return null;
            }
        }
        String rn = chain.fieldRenames.get(key);
        if (rn != null) {
            Found at = wResolveField(owner, rn, desc);
            if (at == null) {
                why.add("renamed to " + rn + ", which does not resolve on " + shortName(owner) + " in " + chain.to);
                return null;
            }
            return "renamed to " + rn + (at.owner().equals(owner) ? "" : " on " + shortName(at.owner()));
        }
        Found at = wResolveField(owner, name, desc);
        if (at != null && !at.owner().equals(owner)) {
            return "inherited from " + shortName(at.owner());
        }
        return null;
    }

    /** every reference to one member of the mixin, in the mixin and in the jar's other classes */
    static final class Refs {
        final List<MethodInsnNode> calls = new ArrayList<>();
        final List<FieldInsnNode> fieldInsns = new ArrayList<>();
        /** {container (InvokeDynamicInsnNode / LdcInsnNode), index in bsmArgs or -1} */
        final List<Object[]> handles = new ArrayList<>();
        int gets;
        int puts;
    }

    // ------------------------------------------------------------------ one mixin

    private final class Mixin {
        final ClassNode mx;
        /** this mixin's refmap table (a working copy, committed with the class), or null */
        final JsonObject table;
        final List<String> targets;
        /** the one target; a mixin of several targets is left as it is (each would need its own fate) */
        final String target;
        final boolean itf;
        final String sn;
        /** how many annotation values of this mixin name each refmap key */
        final Map<String, Integer> keyUses = new HashMap<>();
        /** other classes of the jar whose references to the mixin's members were retargeted */
        final Map<String, ClassNode> others = new LinkedHashMap<>();
        final List<MethodNode> helperMethods = new ArrayList<>();
        final List<Result.Line> bridged = new ArrayList<>();
        final List<String> left = new ArrayList<>();
        boolean changed;
        boolean refmapDirty;

        /** a carried shadow, invoker, accessor or overwrite */
        void member(String text) {
            bridged.add(new Result.Line(mx.name, null, text));
        }

        /** a carried injector selector or anchor ({@code key} "@Kind Mixin.handler", as repair names it) */
        void injected(String key, String text) {
            bridged.add(new Result.Line(mx.name, key, text));
        }

        Mixin(ClassNode mx, JsonObject table) {
            this.mx = mx;
            this.table = table;
            this.targets = MixinRepair.mixinTargets(mx, table);
            this.target = targets.size() == 1 ? targets.get(0) : null;
            this.itf = (mx.access & Opcodes.ACC_INTERFACE) != 0;
            this.sn = shortName(mx.name);
        }

        boolean bridge() {
            // a mixin of a class the step renames to one of CenturyBridge's (a facade): the facade only holds what
            // converted mods call; where W vanilla runs the same code, the injectors are also put there (alsoOnW)
            String origin = facadeOrigin();
            if (origin != null) {
                countKeys();
                keysCounted = true;
                for (MethodNode m : new ArrayList<>(mx.methods)) {
                    alsoOnW(m, origin);
                }
            }
            if (target == null || !Chain.minecraftOwned(target)) {
                return changed;
            }
            if (w(target) == null && !chain.targetDecls.containsKey(target)) {
                return changed; // nothing to judge the mixin against
            }
            if (!keysCounted) {
                countKeys();
            }
            for (FieldNode f : new ArrayList<>(mx.fields)) {
                if (has(f.visibleAnnotations, f.invisibleAnnotations, SHADOW)) {
                    shadowField(f);
                }
            }
            for (MethodNode m : new ArrayList<>(mx.methods)) {
                List<AnnotationNode> ans = MixinRepair.annotations(m);
                AnnotationNode sh = find(ans, SHADOW);
                AnnotationNode inv = find(ans, INVOKER);
                AnnotationNode acc = find(ans, ACCESSOR);
                if (sh != null) {
                    shadowMethod(m, sh);
                } else if (inv != null) {
                    invoker(m, inv);
                } else if (acc != null) {
                    accessor(m, acc);
                } else if (find(ans, OVERWRITE) != null) {
                    overwrite(m);
                } else {
                    for (AnnotationNode an : ans) {
                        if (MixinRepair.values(an).containsKey("method")) {
                            injector(m, an);
                            break; // one injector per handler; a synthesized handler took the annotations
                        }
                    }
                }
            }
            return changed;
        }

        // -------------------------------------------------------------- refmap keys

        private void countKeys() {
            if (table == null) {
                return;
            }
            countIn(mx.visibleAnnotations);
            countIn(mx.invisibleAnnotations);
            for (FieldNode f : mx.fields) {
                countIn(f.visibleAnnotations);
                countIn(f.invisibleAnnotations);
            }
            for (MethodNode m : mx.methods) {
                for (AnnotationNode an : MixinRepair.annotations(m)) {
                    countIn(an);
                    // an accessor or invoker naming no target inflects its refmap key from its name
                    if ((an.desc.equals(ACCESSOR) || an.desc.equals(INVOKER)) && str(an, "value", "").isEmpty()) {
                        String k = inflect(m.name);
                        if (k != null && table.has(k)) {
                            keyUses.merge(k, 1, Integer::sum);
                        }
                    }
                }
            }
        }

        private void countIn(Object v) {
            if (v instanceof String s) {
                if (table.has(s)) {
                    keyUses.merge(s, 1, Integer::sum);
                }
            } else if (v instanceof AnnotationNode a) {
                if (a.values != null) {
                    for (int i = 1; i < a.values.size(); i += 2) {
                        countIn(a.values.get(i));
                    }
                }
            } else if (v instanceof List<?> l) {
                for (Object o : l) {
                    countIn(o);
                }
            }
        }

        /** whether this refmap key can be rewritten for one annotation without moving another */
        private boolean ownKey(String key) {
            return table != null && table.has(key) && keyUses.getOrDefault(key, 0) == 1;
        }

        /**
         * Point an accessor / invoker at its W target: through its refmap entry when the value came
         * from one no other annotation shares (the annotation then names that key, so a renamed
         * method still finds it), else in the annotation itself.
         */
        private void pin(AnnotationNode an, String key, String target) {
            if (ownKey(key)) {
                MixinRepair.refmapPut(table, key, target);
                refmapDirty = true;
                setValue(an, "value", key);
            } else {
                setValue(an, "value", target);
            }
            changed = true;
        }

        // -------------------------------------------------------------- references

        /**
         * Every reference to the mixin's own member name+desc (a field when {@code field}): calls or
         * field instructions and method handles, in the mixin and in the jar's other classes that
         * name the mixin (its inner classes). Other classes are parsed once and kept in
         * {@link #others}, to be written back when the mixin commits.
         */
        Refs refs(String name, String desc, boolean field) {
            Refs r = new Refs();
            collect(mx, name, desc, field, r);
            for (Map.Entry<String, ClassNode> e : mentioning().entrySet()) {
                int before = r.calls.size() + r.fieldInsns.size() + r.handles.size();
                collect(e.getValue(), name, desc, field, r);
                if (r.calls.size() + r.fieldInsns.size() + r.handles.size() > before) {
                    others.put(e.getKey(), e.getValue());
                }
            }
            return r;
        }

        /** the jar's other classes whose bytes name the mixin, parsed once per mixin (a jar of thousands of mixins) */
        private Map<String, ClassNode> mentioning;

        private Map<String, ClassNode> mentioning() {
            if (mentioning == null) {
                mentioning = new LinkedHashMap<>();
                for (String k : classesNaming().getOrDefault(mx.name, List.of())) {
                    if (k.equals(mx.name)) {
                        continue;
                    }
                    ClassNode cn = new ClassNode();
                    try {
                        new ClassReader(entries.get(k + ".class")).accept(cn, 0);
                    } catch (RuntimeException ex) {
                        continue;
                    }
                    mentioning.put(k, cn);
                }
            }
            return mentioning;
        }

        private void collect(ClassNode cn, String name, String desc, boolean field, Refs r) {
            for (MethodNode m : cn.methods) {
                if (m.instructions == null) {
                    continue;
                }
                for (AbstractInsnNode in : m.instructions) {
                    if (!field && in instanceof MethodInsnNode mi && mi.owner.equals(mx.name)
                            && mi.name.equals(name) && mi.desc.equals(desc)) {
                        r.calls.add(mi);
                    } else if (field && in instanceof FieldInsnNode fi && fi.owner.equals(mx.name)
                            && fi.name.equals(name) && fi.desc.equals(desc)) {
                        r.fieldInsns.add(fi);
                        if (fi.getOpcode() == Opcodes.PUTFIELD || fi.getOpcode() == Opcodes.PUTSTATIC) {
                            r.puts++;
                        } else {
                            r.gets++;
                        }
                    } else if (in instanceof InvokeDynamicInsnNode id && id.bsmArgs != null) {
                        for (int i = 0; i < id.bsmArgs.length; i++) {
                            if (id.bsmArgs[i] instanceof Handle h && names(h, name, desc, field)) {
                                r.handles.add(new Object[] {id, i});
                            }
                        }
                    } else if (in instanceof LdcInsnNode ldc && ldc.cst instanceof Handle h && names(h, name, desc, field)) {
                        r.handles.add(new Object[] {ldc, -1});
                    }
                }
            }
        }

        private boolean names(Handle h, String name, String desc, boolean field) {
            boolean isField = h.getTag() <= Opcodes.H_PUTSTATIC;
            return isField == field && h.getOwner().equals(mx.name) && h.getName().equals(name) && h.getDesc().equals(desc);
        }

        /** point the collected method handles at owner.name instead (same desc and kind) */
        void retargetHandles(Refs r, String owner, String name, boolean ownerItf) {
            for (Object[] h : r.handles) {
                Handle old = h[0] instanceof InvokeDynamicInsnNode id ? (Handle) id.bsmArgs[(Integer) h[1]]
                    : (Handle) ((LdcInsnNode) h[0]).cst;
                Handle nh = new Handle(old.getTag(), owner, name, old.getDesc(),
                    old.getTag() > Opcodes.H_PUTSTATIC && ownerItf);
                if (h[0] instanceof InvokeDynamicInsnNode id) {
                    id.bsmArgs[(Integer) h[1]] = nh;
                } else {
                    ((LdcInsnNode) h[0]).cst = nh;
                }
            }
        }

        // -------------------------------------------------------------- @Shadow

        /**
         * A shadow method the target no longer declares: Mixin would fail the whole mixin at apply.
         * Its uses become calls naming the target class itself -- what Mixin makes of them when it
         * merges the mixin -- and the shadow goes; the step's call wiring then carries those calls
         * like the same call anywhere in the mod's code (JarProcessor runs over mixin classes too).
         */
        void shadowMethod(MethodNode m, AnnotationNode sh) {
            String prefix = str(sh, "prefix", "shadow$");
            String name = m.name.startsWith(prefix) ? m.name.substring(prefix.length()) : m.name;
            Boolean live = wDeclares(target, name, m.desc);
            if (live == null || live) {
                return;
            }
            for (String alias : MixinRepair.strings(MixinRepair.values(sh).get("aliases"))) {
                if (Boolean.TRUE.equals(wDeclares(target, alias, m.desc))) {
                    return; // Mixin tries the aliases too
                }
            }
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            String what = "shadow " + sn + "." + m.name + m.desc;
            List<String> why = new ArrayList<>();
            CallFate f = callFate(target, name, m.desc, isStatic, why);
            if (f == null) {
                if (!why.isEmpty()) {
                    left.add(what + ": " + why.get(0));
                }
                return;
            }
            if (f.at() != null && f.at().isStatic() != isStatic) {
                left.add(what + ": " + f.how() + " reaches a " + (isStatic ? "non-static" : "static") + " member");
                return;
            }
            Refs r = refs(m.name, m.desc, false);
            if (!r.handles.isEmpty() && (f.reshape() != null || f.how().startsWith("renamed"))) {
                // JarProcessor reshapes and renames call instructions, not method handles
                left.add(what + ": taken as a method reference, and " + f.how() + " applies to calls");
                return;
            }
            boolean tItf = isInterfaceOnW(target);
            for (MethodInsnNode mi : r.calls) {
                mi.owner = target;
                mi.name = name;
                mi.itf = tItf;
                // a private V method called directly; when W's member is not private any more the
                // call dispatches as calls on it do
                if (mi.getOpcode() == Opcodes.INVOKESPECIAL && f.at() != null && (f.at().access() & Opcodes.ACC_PRIVATE) == 0) {
                    mi.setOpcode(tItf ? Opcodes.INVOKEINTERFACE : Opcodes.INVOKEVIRTUAL);
                }
            }
            retargetHandles(r, target, name, tItf);
            mx.methods.remove(m);
            changed = true;
            member(what + " -> calls on " + shortName(target) + " (" + f.how() + ")");
        }

        /**
         * A shadow field the target no longer declares. Renamed on the target itself: the shadow
         * keeps being a shadow under the new name (its @Final / @Mutable keep their meaning).
         * Otherwise its reads and writes become accesses naming the target class, which the step's
         * field wiring carries; a write of a final field (the mixin's @Mutable lifted its finality
         * through the shadow) cannot be carried that way.
         */
        void shadowField(FieldNode f) {
            Boolean live = wDeclaresField(target, f.name, f.desc);
            if (live == null || live) {
                return;
            }
            boolean isStatic = (f.access & Opcodes.ACC_STATIC) != 0;
            String what = "shadow field " + sn + "." + f.name + ":" + f.desc;
            Refs r = refs(f.name, f.desc, true);
            String rn = chain.fieldRenames.get(vk(target) + "." + f.name + ":" + f.desc);
            if (rn != null && Boolean.TRUE.equals(wDeclaresField(target, rn, f.desc))) {
                for (FieldInsnNode fi : r.fieldInsns) {
                    fi.name = rn;
                }
                for (Object[] h : r.handles) {
                    Handle old = h[0] instanceof InvokeDynamicInsnNode id ? (Handle) id.bsmArgs[(Integer) h[1]]
                        : (Handle) ((LdcInsnNode) h[0]).cst;
                    Handle nh = new Handle(old.getTag(), old.getOwner(), rn, old.getDesc(), false);
                    if (h[0] instanceof InvokeDynamicInsnNode id) {
                        id.bsmArgs[(Integer) h[1]] = nh;
                    } else {
                        ((LdcInsnNode) h[0]).cst = nh;
                    }
                }
                String old = f.name;
                f.name = rn;
                changed = true;
                member(what + " -> shadow of " + rn + " (renamed " + old + " -> " + rn + ")");
                return;
            }
            List<String> why = new ArrayList<>();
            String how = fieldFate(target, f.name, f.desc, isStatic, r, why);
            if (how == null) {
                if (!why.isEmpty()) {
                    left.add(what + ": " + why.get(0));
                }
                return;
            }
            if (!r.handles.isEmpty()) {
                left.add(what + ": taken as a field handle");
                return;
            }
            // a write a static field setter takes does not store into W's field: its finality does not matter
            if (r.puts > 0 && !(isStatic && chain.staticFieldSetters.containsKey(vk(target) + "." + f.name + ":" + f.desc))) {
                boolean mutable = has(f.visibleAnnotations, f.invisibleAnnotations, MUTABLE);
                Found at = wResolveField(target, rn != null ? rn : f.name, f.desc);
                if (mutable || at != null && (at.access() & Opcodes.ACC_FINAL) != 0) {
                    left.add(what + ": written, and the field is final on " + chain.to + " (@Mutable lifts that only through the shadow)");
                    return;
                }
            }
            for (FieldInsnNode fi : r.fieldInsns) {
                fi.owner = target;
            }
            mx.fields.remove(f);
            changed = true;
            member(what + " -> accesses of " + shortName(target) + "." + f.name + " (" + how + ")");
        }

        // -------------------------------------------------------------- @Invoker

        /**
         * An invoker of a method the target no longer declares in that shape. Renamed on the target:
         * retargeted. Reshaped there: the invoker takes W's shape under a new name with its target
         * pinned, and the V shape the mod calls comes back as a synthetic method converting the way
         * the call reshape converts the same call in plain code. Redirected: the method makes the
         * redirect call. Restored by the layer or moved up W's hierarchy: the method calls it.
         */
        void invoker(MethodNode m, AnnotationNode an) {
            String value = str(an, "value", "");
            boolean implicit = value.isEmpty();
            String key = implicit ? inflect(m.name) : value;
            if (key == null) {
                return;
            }
            String mapped = MixinRepair.refmapLookup(table, key);
            MixinRepair.Sel s = MixinRepair.Sel.parse(mapped);
            if (s.owner() != null && !s.owner().equals(target)) {
                return;
            }
            String name = s.name();
            String simple = shortName(target);
            String pfx = inflectPrefix(m.name);
            if (name.equals("<init>") || name.equals(simple)
                    || implicit && ("new".equals(pfx) || "create".equals(pfx)) && simple.equals(inflectRaw(m.name))) {
                factoryInvoker(m, an);
                return;
            }
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            Boolean live = wDeclares(target, name, m.desc);
            if (live == null || live) {
                return;
            }
            String what = "invoker " + sn + "." + m.name + m.desc;
            List<String> why = new ArrayList<>();
            CallFate f = callFate(target, name, m.desc, isStatic, why);
            if (f == null) {
                if (!why.isEmpty()) {
                    left.add(what + ": " + why.get(0));
                }
                return;
            }
            if (f.at() != null && f.at().isStatic() != isStatic) {
                left.add(what + ": " + f.how() + " reaches a " + (isStatic ? "non-static" : "static") + " member");
                return;
            }
            if (f.reshape() != null) {
                for (Chain.ReshapeOp op : f.reshape().args.ops) {
                    if (op.param != null) {
                        left.add(what + ": the call reshape takes an argument from the caller's own parameters");
                        return;
                    }
                }
                if (f.at().owner().equals(target)) {
                    reshapeInvoker(m, an, key, mapped, f, what);
                    return;
                }
            }
            if (f.redirect() != null) {
                redirectInvoker(m, an, f, what);
                return;
            }
            if (f.reshape() == null && f.at() != null && f.at().owner().equals(target)) {
                pin(an, key, respell(mapped, f.name(), s.desc()));
                member(what + " -> " + f.name() + " (" + f.how() + ")");
                return;
            }
            // the layer's own member, or one W moved up its hierarchy: no invoker can be generated for
            // it (Mixin looks in the target class itself); the method makes the call. A static method
            // of an accessor interface runs in the interface, where only public members are reachable
            if (itf && isStatic && (f.at() == null || !f.at().isPublic())) {
                left.add(what + ": " + f.how() + ", and a static invoker's body runs in the interface, which cannot reach it");
                return;
            }
            directInvoker(m, an, f, what);
        }

        private void reshapeInvoker(MethodNode m, AnnotationNode an, String key, String mapped, CallFate f, String what) {
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            String vName = m.name;
            String vDesc = m.desc;
            String renamed = unique(vName + "$cb" + step);
            m.name = renamed;
            m.desc = f.desc();
            m.signature = null;
            pin(an, key, respell(mapped, f.name(), f.desc()));
            MethodNode vm = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC | (isStatic ? Opcodes.ACC_STATIC : 0),
                vName, vDesc, null, exceptions(m));
            Type ret = Type.getReturnType(vDesc);
            if (itf) {
                // an accessor interface: the conversion lives in the jar's bridge class, which both the
                // interface's own method and the copy Mixin merges into the target reach
                String hDesc = isStatic ? vDesc : "(Ljava/lang/Object;" + vDesc.substring(1);
                MethodNode hm = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                    "b" + (helperSeq++) + "$" + sn.replace('$', '_') + "$" + vName, hDesc, null, null);
                int slot = 0;
                if (!isStatic) {
                    hm.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    hm.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, mx.name));
                    slot = 1;
                }
                loadArgs(hm.instructions, vDesc, slot);
                MethodInsnNode call = new MethodInsnNode(isStatic ? Opcodes.INVOKESTATIC : Opcodes.INVOKEINTERFACE,
                    mx.name, vName, vDesc, true);
                hm.instructions.add(call);
                hm.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
                hm.maxLocals = argSlots(hDesc, true);
                JarProcessor.reshapeCall(hm, call, f.reshape());
                call.name = renamed;
                helperMethods.add(hm);
                int vs = 0;
                if (!isStatic) {
                    vm.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    vs = 1;
                }
                loadArgs(vm.instructions, vDesc, vs);
                vm.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, helperName, hm.name, hDesc, false));
                vm.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
            } else {
                // a class mixin's own calls run merged into the target: the conversion in place
                int vs = 0;
                if (!isStatic) {
                    vm.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    vs = 1;
                }
                loadArgs(vm.instructions, vDesc, vs);
                int op = isStatic ? Opcodes.INVOKESTATIC
                    : (m.access & Opcodes.ACC_PRIVATE) != 0 ? Opcodes.INVOKESPECIAL : Opcodes.INVOKEVIRTUAL;
                MethodInsnNode call = new MethodInsnNode(op, mx.name, vName, vDesc, false);
                vm.instructions.add(call);
                vm.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
                vm.maxLocals = argSlots(vDesc, isStatic);
                JarProcessor.reshapeCall(vm, call, f.reshape());
                call.name = renamed;
            }
            mx.methods.add(vm);
            changed = true;
            member(what + " -> " + renamed + f.desc() + " invoking " + f.name() + f.desc()
                + " (call reshape; " + vName + vDesc + " converts as the reshape does)");
        }

        private void redirectInvoker(MethodNode m, AnnotationNode an, CallFate f, String what) {
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            InsnList in = concrete(m, an);
            String cdesc = m.desc;
            int slot = 0;
            if (!isStatic) {
                in.add(new VarInsnNode(Opcodes.ALOAD, 0));
                in.add(new TypeInsnNode(Opcodes.CHECKCAST, f.home()));
                cdesc = "(L" + f.home() + ";" + m.desc.substring(1);
                slot = 1;
            }
            loadArgs(in, m.desc, slot);
            String owner = JarProcessor.redirCls(f.redirect());
            String name = JarProcessor.redirName(f.redirect(), f.name());
            in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, owner, name, cdesc, false));
            in.add(new InsnNode(Type.getReturnType(m.desc).getOpcode(Opcodes.IRETURN)));
            changed = true;
            member(what + " -> " + owner + "." + name + " (" + f.how() + ")");
        }

        private void directInvoker(MethodNode m, AnnotationNode an, CallFate f, String what) {
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            InsnList in = concrete(m, an);
            int slot = 0;
            if (!isStatic) {
                in.add(new VarInsnNode(Opcodes.ALOAD, 0));
                in.add(new TypeInsnNode(Opcodes.CHECKCAST, target));
                slot = 1;
            }
            loadArgs(in, m.desc, slot);
            boolean tItf = isInterfaceOnW(target);
            MethodInsnNode call = new MethodInsnNode(isStatic ? Opcodes.INVOKESTATIC : tItf ? Opcodes.INVOKEINTERFACE
                : Opcodes.INVOKEVIRTUAL, target, f.name(), m.desc, tItf);
            in.add(call);
            in.add(new InsnNode(Type.getReturnType(m.desc).getOpcode(Opcodes.IRETURN)));
            m.maxLocals = argSlots(m.desc, isStatic);
            if (f.reshape() != null) {
                JarProcessor.reshapeCall(m, call, f.reshape());
            }
            changed = true;
            member(what + " -> calls " + shortName(target) + "." + f.name() + call.desc + " (" + f.how() + ")");
        }

        /**
         * A constructor invoker (@Invoker("<init>") static Owner create(args)): a NEW site of the target
         * class, carried the way the step carries a NEW site in plain code. Its constructor redirected to
         * a factory: the method calls the factory. Reshaped (or tail-adapted): the invoker takes W's
         * constructor shape under a new name, and the V shape comes back as a synthetic static making
         * the reshape's conversion -- its post static on the new object included.
         */
        private void factoryInvoker(MethodNode m, AnnotationNode an) {
            Type ret = Type.getReturnType(m.desc);
            if ((m.access & Opcodes.ACC_STATIC) == 0 || ret.getSort() != Type.OBJECT) {
                return;
            }
            String vDesc = m.desc;
            String ctor = vDesc.substring(0, vDesc.indexOf(')') + 1) + "V";
            Boolean live = wDeclares(target, "<init>", ctor);
            if (live == null || live) {
                return;
            }
            String what = "constructor invoker " + sn + "." + m.name + m.desc;
            String key = vk(target) + ".<init>" + ctor;
            String factory = chain.ctorRedirects.get(key);
            if (factory != null) {
                InsnList in = concrete(m, an);
                loadArgs(in, vDesc, 0);
                String owner = JarProcessor.redirCls(factory);
                String name = JarProcessor.redirName(factory, "cbNew");
                in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, owner, name, ctor.substring(0, ctor.length() - 1) + "L" + target + ";", false));
                in.add(new InsnNode(Opcodes.ARETURN));
                changed = true;
                member(what + " -> " + owner + "." + name + " (constructor factory)");
                return;
            }
            Chain.CtorReshape cr = chain.superCtorReshapes.get(key);
            String[] adapt = cr == null ? chain.superCtorAdapts.get(key) : null;
            if (cr == null && adapt == null) {
                return;
            }
            if (cr != null) {
                for (Chain.ReshapeOp op : cr.ops) {
                    if (op.param != null) {
                        left.add(what + ": the constructor reshape takes an argument from the caller's own parameters");
                        return;
                    }
                }
            }
            String wCtor = cr != null ? cr.newDesc : adapt[0];
            if (!Boolean.TRUE.equals(wDeclares(target, "<init>", wCtor))) {
                left.add(what + ": " + shortName(target) + " has no constructor " + wCtor + " in " + chain.to);
                return;
            }
            String wDesc = wCtor.substring(0, wCtor.indexOf(')') + 1) + ret.getDescriptor();
            String vName = m.name;
            String renamed = unique(vName + "$cb" + step);
            m.name = renamed;
            m.desc = wDesc;
            m.signature = null;
            setValue(an, "value", "<init>"); // the new name no longer inflects to a constructor
            MethodNode vm = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, vName, vDesc, null,
                exceptions(m));
            // an accessor interface's own static runs in the interface: the conversion lives in the bridge class
            MethodNode body = itf ? new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
                "b" + (helperSeq++) + "$" + sn.replace('$', '_') + "$" + vName, vDesc, null, null) : vm;
            loadArgs(body.instructions, vDesc, 0);
            MethodInsnNode call = new MethodInsnNode(Opcodes.INVOKESTATIC, mx.name, vName, vDesc, itf);
            body.instructions.add(call);
            body.instructions.add(new InsnNode(Opcodes.ARETURN));
            body.maxLocals = argSlots(vDesc, true);
            if (cr != null) {
                int[] slots = JarProcessor.reshapeArgs(body, call, cr);
                if (cr.postName != null) {
                    // the post static takes the constructed object and the old arguments the new constructor has no place for
                    Type[] oa = Type.getArgumentTypes(vDesc);
                    InsnList post = new InsnList();
                    post.add(new InsnNode(Opcodes.DUP));
                    for (int idx : cr.postArgs) {
                        post.add(new VarInsnNode(oa[idx].getOpcode(Opcodes.ILOAD), slots[idx]));
                    }
                    post.add(new MethodInsnNode(Opcodes.INVOKESTATIC, cr.postOwner, cr.postName, cr.postDesc, false));
                    body.instructions.insert(call, post);
                }
            } else {
                body.instructions.insertBefore(call, new MethodInsnNode(Opcodes.INVOKESTATIC, JarProcessor.redirCls(adapt[1]),
                    JarProcessor.redirName(adapt[1], "cbConv"), adapt[2], false));
            }
            call.desc = wDesc;
            call.name = renamed;
            if (itf) {
                helperMethods.add(body);
                loadArgs(vm.instructions, vDesc, 0);
                vm.instructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, helperName, body.name, vDesc, false));
                vm.instructions.add(new InsnNode(Opcodes.ARETURN));
            }
            mx.methods.add(vm);
            changed = true;
            member(what + " -> " + renamed + wDesc + " (constructor " + (cr != null ? "reshape" : "adapter") + "; "
                + vName + vDesc + " converts as it does)");
        }

        /** an accessor / invoker method turned into a plain synthetic one; returns its emptied body */
        private InsnList concrete(MethodNode m, AnnotationNode an) {
            removeAnnotation(m, an);
            m.access = (m.access & ~(Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE)) | Opcodes.ACC_SYNTHETIC;
            m.instructions = new InsnList();
            m.tryCatchBlocks = new ArrayList<>();
            m.localVariables = null;
            m.visibleLocalVariableAnnotations = null;
            m.invisibleLocalVariableAnnotations = null;
            m.maxLocals = argSlots(m.desc, (m.access & Opcodes.ACC_STATIC) != 0);
            return m.instructions;
        }

        // -------------------------------------------------------------- @Accessor

        /**
         * An accessor of a field the target no longer declares. Renamed on the target: retargeted.
         * An instance field getter / setter, a static field redirect, getter or setter: the accessor
         * becomes a synthetic method making that call. A field W moved up the hierarchy, or the layer's own:
         * the method accesses it.
         */
        void accessor(MethodNode m, AnnotationNode an) {
            String value = str(an, "value", "");
            String key = value.isEmpty() ? inflect(m.name) : value;
            if (key == null) {
                return;
            }
            String mapped = MixinRepair.refmapLookup(table, key);
            String spec = mapped;
            if (spec.startsWith("L") && spec.contains(";")) {
                if (!spec.substring(1, spec.indexOf(';')).equals(target)) {
                    return;
                }
                spec = spec.substring(spec.indexOf(';') + 1);
            }
            String name = spec.contains(":") ? spec.substring(0, spec.indexOf(':')) : spec;
            Type[] args = Type.getArgumentTypes(m.desc);
            boolean getter = args.length == 0;
            if (!getter && args.length != 1) {
                return;
            }
            String fdesc = (getter ? Type.getReturnType(m.desc) : args[0]).getDescriptor();
            boolean isStatic = (m.access & Opcodes.ACC_STATIC) != 0;
            Boolean live = wDeclaresField(target, name, fdesc);
            if (live == null || live) {
                return;
            }
            String what = "accessor " + sn + "." + m.name + m.desc;
            String fkey = vk(target) + "." + name + ":" + fdesc;
            String rn = chain.fieldRenames.get(fkey);
            if (rn != null && Boolean.TRUE.equals(wDeclaresField(target, rn, fdesc))) {
                pin(an, key, respell(mapped, rn, spec.contains(":") ? fdesc : null));
                member(what + " -> " + rn + " (renamed " + name + " -> " + rn + ")");
                return;
            }
            InsnList body = new InsnList();
            String how;
            if (!isStatic && getter && chain.instanceFieldGetters.containsKey(fkey)) {
                String v = chain.instanceFieldGetters.get(fkey);
                body.add(new VarInsnNode(Opcodes.ALOAD, 0));
                body.add(new TypeInsnNode(Opcodes.CHECKCAST, target));
                body.add(new MethodInsnNode(Opcodes.INVOKESTATIC, JarProcessor.redirCls(v), JarProcessor.redirName(v, name),
                    "(L" + target + ";)" + fdesc, false));
                how = "instance field getter " + JarProcessor.redirCls(v);
            } else if (!isStatic && !getter && chain.instanceFieldSetters.containsKey(fkey)) {
                String v = chain.instanceFieldSetters.get(fkey);
                body.add(new VarInsnNode(Opcodes.ALOAD, 0));
                body.add(new TypeInsnNode(Opcodes.CHECKCAST, target));
                body.add(new VarInsnNode(args[0].getOpcode(Opcodes.ILOAD), 1));
                body.add(new MethodInsnNode(Opcodes.INVOKESTATIC, JarProcessor.redirCls(v), JarProcessor.redirName(v, name),
                    "(L" + target + ";" + fdesc + ")V", false));
                how = "instance field setter " + JarProcessor.redirCls(v);
            } else if (isStatic && getter && chain.fieldRedirects.containsKey(fkey)) {
                String v = chain.fieldRedirects.get(fkey);
                body.add(new FieldInsnNode(Opcodes.GETSTATIC, JarProcessor.redirCls(v), JarProcessor.redirName(v, name), fdesc));
                how = "field redirect " + JarProcessor.redirCls(v);
            } else if (isStatic && getter && chain.fieldGetters.containsKey(fkey)) {
                String v = chain.fieldGetters.get(fkey);
                body.add(new MethodInsnNode(Opcodes.INVOKESTATIC, JarProcessor.redirCls(v), JarProcessor.redirName(v, name),
                    "()" + fdesc, false));
                how = "field getter " + JarProcessor.redirCls(v);
            } else if (isStatic && !getter && chain.staticFieldSetters.containsKey(fkey)) {
                String v = chain.staticFieldSetters.get(fkey);
                body.add(new VarInsnNode(args[0].getOpcode(Opcodes.ILOAD), 0));
                body.add(new MethodInsnNode(Opcodes.INVOKESTATIC, JarProcessor.redirCls(v), JarProcessor.redirName(v, name),
                    "(" + fdesc + ")V", false));
                how = "static field setter " + JarProcessor.redirCls(v);
            } else {
                String fname = rn != null ? rn : name;
                Found at = wResolveField(target, fname, fdesc);
                boolean covered = rn == null && chain.coveredAnywhere(vk(target), name + ":" + fdesc);
                if (at == null && !covered) {
                    if (rn != null) {
                        left.add(what + ": renamed to " + rn + ", which does not resolve on " + shortName(target) + " in " + chain.to);
                    }
                    return;
                }
                if (at != null && at.isStatic() != isStatic) {
                    return;
                }
                if (itf && isStatic && (at == null || !at.isPublic())) {
                    left.add(what + ": a static accessor's body runs in the interface, which cannot reach the field");
                    return;
                }
                if (!getter && at != null && (at.access() & Opcodes.ACC_FINAL) != 0) {
                    left.add(what + ": a setter of a field that is final in " + chain.to);
                    return;
                }
                if (!isStatic) {
                    body.add(new VarInsnNode(Opcodes.ALOAD, 0));
                    body.add(new TypeInsnNode(Opcodes.CHECKCAST, target));
                }
                if (!getter) {
                    body.add(new VarInsnNode(args[0].getOpcode(Opcodes.ILOAD), isStatic ? 0 : 1));
                }
                int op = isStatic ? (getter ? Opcodes.GETSTATIC : Opcodes.PUTSTATIC) : (getter ? Opcodes.GETFIELD : Opcodes.PUTFIELD);
                body.add(new FieldInsnNode(op, target, fname, fdesc));
                how = covered ? "restored by the step layer"
                    : "on " + shortName(at.owner()) + (rn != null ? ", renamed " + name + " -> " + rn : "");
            }
            body.add(new InsnNode(Type.getReturnType(m.desc).getOpcode(Opcodes.IRETURN)));
            concrete(m, an).add(body);
            changed = true;
            member(what + " -> " + how);
        }

        // -------------------------------------------------------------- @Overwrite

        /** an overwrite of a method the target renamed (same shape): it follows the name */
        void overwrite(MethodNode m) {
            Boolean live = wDeclares(target, m.name, m.desc);
            if (live == null || live) {
                return;
            }
            String what = "overwrite " + sn + "." + m.name + m.desc;
            String rn = chain.methodRenames.get(vk(target) + "." + m.name + m.desc);
            if (rn != null && Boolean.TRUE.equals(wDeclares(target, rn, m.desc))) {
                Refs r = refs(m.name, m.desc, false);
                if (!r.handles.isEmpty()) {
                    left.add(what + ": taken as a method reference");
                    return;
                }
                for (MethodInsnNode mi : r.calls) {
                    mi.name = rn;
                }
                String old = m.name;
                m.name = rn;
                changed = true;
                member(what + " -> " + rn + " (renamed " + old + " -> " + rn + ")");
                return;
            }
            if (!chain.overrideHooksForOld(m.name, m.desc).isEmpty()
                    || chain.instanceRedirectHome(chain.callReshapeIndex, target, m.name + m.desc) != null) {
                left.add(what + ": the method's shape changed, and an overwrite's body is not re-shaped");
            }
        }

        /** a method name not yet used in the mixin */
        private String unique(String base) {
            String n = base;
            while (true) {
                boolean taken = false;
                for (MethodNode m : mx.methods) {
                    taken |= m.name.equals(n);
                }
                if (!taken) {
                    return n;
                }
                n = n + "_";
            }
        }

        // -------------------------------------------------------------- injectors

        /**
         * An injector whose target method the step moved: the selector follows the method to its W
         * shape, and a handler written for V's arguments gets a W-shaped one in front of it. Then
         * its @At anchors, in the methods it now targets.
         */
        void injector(MethodNode h, AnnotationNode an) {
            List<String> sels = MixinRepair.strings(MixinRepair.values(an).get("method"));
            ClassNode wt = w(target);
            if (sels.isEmpty() || wt == null) {
                return; // judged against W's real bodies only
            }
            String kind = kind(an);
            Selection sel = select(h, an, kind, sels, wt, v(target));
            if (sel != null) {
                ats(sel, kind);
            } else {
                spellRestored(h, an, kind);
            }
        }

        /**
         * A selector left on a member the layer restores whose descriptor names a class the step renames (an
         * Enchantment parameter that is the facade's in the layer): spelled as the restored member is declared
         * (Chain.renamedDesc), so MixinRepair and MixinJudge find it -- a mixin aimed at vanilla code keeps V's
         * names in its strings (MixinClassRenames), and the restored member is the only method with this name there.
         */
        private void spellRestored(MethodNode h, AnnotationNode an, String kind) {
            List<String> sels = MixinRepair.strings(MixinRepair.values(an).get("method"));
            for (int idx = 0; idx < sels.size(); idx++) {
                String sel = sels.get(idx);
                if (sel.startsWith("/") || sel.contains("*")) {
                    continue;
                }
                String mapped = MixinRepair.refmapLookup(table, sel);
                MixinRepair.Sel s = MixinRepair.Sel.parse(mapped);
                if (s.desc() == null || s.owner() != null && !s.owner().equals(target)) {
                    continue;
                }
                String rd = chain.renamedDesc(s.desc());
                if (rd.equals(s.desc()) || !chain.layerMembers.match(target, s).isEmpty()) {
                    continue;
                }
                MixinRepair.Sel r = new MixinRepair.Sel(s.owner(), s.name(), rd, s.wildcard());
                if (chain.layerMembers.match(target, r).isEmpty()) {
                    continue;
                }
                String nm = respell(mapped, s.name(), rd);
                if (ownKey(sel)) {
                    MixinRepair.refmapPut(table, sel, nm);
                    refmapDirty = true;
                } else {
                    MixinRepair.setMethodSelector(an, idx, nm);
                }
                changed = true;
                injected("@" + kind + " " + sn + "." + h.name, "@" + kind + " " + sn + "." + h.name + " -> " + shortName(target) + "."
                    + s.name() + s.desc() + " => " + s.name() + rd + " (the layer's restored member, its descriptor as the step renames it)");
            }
        }

        private Selection select(MethodNode h, AnnotationNode an, String kind, List<String> sels, ClassNode wt, ClassNode vt) {
            // any alternative that still selects a method of W: nothing moved under it
            for (String sel : sels) {
                if (sel.startsWith("/") || sel.contains("*")) {
                    return null; // regex / wildcard selectors: MixinRepair's to judge
                }
                MixinRepair.Sel s = MixinRepair.Sel.parse(MixinRepair.refmapLookup(table, sel));
                if (s.owner() != null && !s.owner().equals(target)) {
                    return null;
                }
                List<MethodNode> wm = s.match(wt);
                if (wm.isEmpty() && s.desc() != null && !chain.renamedDesc(s.desc()).equals(s.desc())) {
                    // the descriptor as the ClassRemapper leaves it (classRenames): nothing moved under it
                    wm = new MixinRepair.Sel(s.owner(), s.name(), chain.renamedDesc(s.desc()), s.wildcard()).match(wt);
                }
                if (!wm.isEmpty()) {
                    return new Selection(h, an, vt == null ? List.of() : s.match(vt), wm);
                }
                // a member the layer restores on the W class (LayerMembers) whose vanilla read path the layer
                // routes through it (@CbVanillaReads): the selector names a method that exists at runtime and
                // vanilla calls, nothing moves under it. An unmarked restored member (a delegate onto the code W
                // kept) is not taken here: the selector follows V's code to W below, and only when nothing
                // carries it does it stay on the restored member (MixinJudge: "vanilla path unmixed")
                if (s.desc() != null && java.util.stream.Stream.concat(chain.layerMembers.match(target, s).stream(),
                            chain.layerMembers.match(target, new MixinRepair.Sel(s.owner(), s.name(), chain.renamedDesc(s.desc()), s.wildcard())).stream())
                        .anyMatch(m -> chain.layerMembers.vanillaReads(m) != null)) {
                    injected("@" + kind + " " + sn + "." + h.name, "@" + kind + " " + sn + "." + h.name + " -> "
                        + shortName(target) + "." + s.name() + s.desc() + ": the layer's restored member");
                    return null;
                }
            }
            if (vt == null || itf) {
                return null; // which V method was meant cannot be read / an interface mixin's injectors: not carried
            }
            for (int idx = 0; idx < sels.size(); idx++) {
                String sel = sels.get(idx);
                String mapped = MixinRepair.refmapLookup(table, sel);
                List<MethodNode> vm = MixinRepair.Sel.parse(mapped).match(vt);
                if (vm.size() != 1) {
                    continue; // the fate of exactly one V method can be looked up
                }
                MethodNode vmeth = vm.get(0);
                String where = "@" + kind + " " + sn + "." + h.name + " -> " + shortName(target) + "." + vmeth.name + vmeth.desc;
                List<String> why = new ArrayList<>();
                Fate fate = selectorFate(vk(target), vmeth.name, vmeth.desc, why);
                if (fate == null && method(wt, vmeth.name, vmeth.desc) == null) {
                    // no wiring for it, or wiring for mods' overrides that cannot carry an injector: where W keeps its code
                    List<String> bwhy = new ArrayList<>();
                    fate = bodyFate(vt, wt, vmeth, bwhy);
                    if (fate == null && (why.isEmpty() || !bwhy.isEmpty() && bwhy.get(0).contains("split across"))) {
                        why.addAll(0, bwhy); // a split is the more precise reason than the wiring's
                    }
                }
                if (fate == null) {
                    // an anchor in the body W moved up into a superclass: a guarded injector there
                    Selection guarded = baseGuard(h, an, kind, vmeth, wt, where, why);
                    if (guarded != null) {
                        return guarded;
                    }
                    MethodNode restored = restoreOverride(vmeth, wt, why);
                    if (restored != null) {
                        injected("@" + kind + " " + sn + "." + h.name, where + " => the override " + chain.to
                            + " dropped, restored in the mixin as a call of the inherited one");
                        return new Selection(h, an, List.of(vmeth), List.of(restored));
                    }
                    if (!why.isEmpty()) {
                        left.add(where + ": " + why.get(0));
                    }
                    continue;
                }
                MethodNode wmeth = method(wt, fate.name(), fate.desc());
                if (wmeth == null) {
                    // a W-named member a layer mixin declares on the target (an override of an inherited W method,
                    // say: SmithingTrimRecipe.matches, which 1.21.2 has only as SmithingRecipe's default): at runtime
                    // the target declares it; MixinJudge decides whether vanilla reaches it
                    wmeth = chain.layerMembers.member(target, fate.name(), fate.desc());
                }
                if (wmeth == null) {
                    left.add(where + ": " + fate.how() + " gives " + fate.name() + fate.desc() + ", which "
                        + shortName(target) + " does not declare in " + chain.to);
                    continue;
                }
                if (((wmeth.access ^ vmeth.access) & Opcodes.ACC_STATIC) != 0) {
                    left.add(where + ": static in one release and not in the other");
                    continue;
                }
                String gone = anchorGone(an, h, kind, vmeth, wmeth, wt);
                if (gone != null) {
                    // V's site of the anchor may stand in what the new method calls: descend-site from V's method
                    List<String> dwhy = new ArrayList<>();
                    MixinRepair.Descend d = descendFollowed(an, h, vmeth, wmeth, vt, wt, dwhy);
                    if (d != null) {
                        String nm = respell(mapped, d.callee().name, d.callee().desc);
                        if (ownKey(sel)) {
                            MixinRepair.refmapPut(table, sel, nm);
                            refmapDirty = true;
                        } else {
                            MixinRepair.setMethodSelector(an, idx, nm);
                        }
                        MixinRepair.applyDescend(d, h, an);
                        changed = true;
                        injected("@" + kind + " " + sn + "." + h.name, where + " => " + d.callee().name + d.callee().desc
                            + (d.ordinal() >= 0 ? " #" + d.ordinal() : "") + " (" + fate.how() + " gives " + fate.name() + fate.desc()
                            + "; V's site of the anchor stands in " + d.callee().name + ", " + d.how()
                            + (d.makeStatic() ? "; handler made static" : "") + (d.toCir() ? "; callback as CallbackInfoReturnable" : "") + ")");
                        return new Selection(h, an, List.of(vmeth), List.of(d.callee()));
                    }
                    left.add(where + ": " + fate.how() + " gives " + fate.name() + fate.desc() + ", where its anchor " + gone
                        + " no longer stands" + (dwhy.isEmpty() ? "" : "; " + dwhy.get(0)));
                    continue;
                }
                Plan p = plan(h, an, kind, vmeth, fate, why);
                if (p == null) {
                    left.add(where + ": " + why.get(0));
                    continue;
                }
                String nm = respell(mapped, fate.name(), fate.desc());
                if (ownKey(sel)) {
                    MixinRepair.refmapPut(table, sel, nm);
                    refmapDirty = true;
                } else {
                    MixinRepair.setMethodSelector(an, idx, nm);
                }
                MethodNode handler = p.synth() ? synthesizeInject(h, fate, p) : h;
                if (p.untouched()) {
                    handler.invisibleAnnotations = handler.invisibleAnnotations == null ? new ArrayList<>() : handler.invisibleAnnotations;
                    handler.invisibleAnnotations.add(new AnnotationNode(RETURN_UNTOUCHED));
                }
                changed = true;
                injected("@" + kind + " " + sn + "." + h.name, where + " => " + fate.name() + fate.desc() + " (" + fate.how()
                    + (p.synth() ? "; a " + chain.to + "-shaped handler forwards to the mod's" : "")
                    + (p.rc() != null ? "; the answer converted both ways: " + p.rc().how() : "")
                    + (p.untouched() ? "; return type " + Type.getReturnType(chain.renamedDesc(vmeth.desc)) + " -> "
                        + Type.getReturnType(fate.desc()) + " with no converter: partial, the answer untouched" : "") + ")");
                return new Selection(handler, an, List.of(vmeth), List.of(wmeth));
            }
            return null;
        }

        /**
         * Can the handler take the W method? @Inject: its arguments are the target's (all or none) and
         * a CallbackInfo(Returnable) matching W's return; captured arguments need the wiring to say
         * which W arguments are V's, a handler capturing locals cannot follow. Other injectors take
         * the anchor's operands, which a new target signature does not move -- unless the handler
         * also captures the target's arguments.
         */
        private Plan plan(MethodNode h, AnnotationNode an, String kind, MethodNode vmeth, Fate fate, List<String> why) {
            Type[] va = Type.getArgumentTypes(vmeth.desc);
            Type[] wa = Type.getArgumentTypes(fate.desc());
            Type vr = Type.getReturnType(vmeth.desc);
            Type wr = Type.getReturnType(fate.desc());
            Type[] hp = Type.getArgumentTypes(h.desc);
            boolean sameArgs = Arrays.equals(va, wa);
            if (kind.equals("Inject")) {
                int ci = -1;
                for (int i = 0; i < hp.length && ci < 0; i++) {
                    if (hp[i].getDescriptor().equals(CI) || hp[i].getDescriptor().equals(CIR)) {
                        ci = i;
                    }
                }
                if (ci < 0) {
                    why.add("the handler takes no CallbackInfo");
                    return null;
                }
                if (hp.length > ci + 1) {
                    why.add("the handler captures the target's locals");
                    return null;
                }
                boolean capture = ci > 0;
                if (capture) {
                    if (ci != va.length) {
                        why.add("the handler takes " + ci + " of the target's " + va.length + " arguments");
                        return null;
                    }
                    for (int i = 0; i < ci; i++) {
                        if (!hp[i].equals(va[i]) && !(coerced(h, i) && hp[i].getSort() == Type.OBJECT)) {
                            why.add("handler argument " + i + " is not the target's");
                            return null;
                        }
                    }
                    if ((!sameArgs || fate.converts()) && !fate.argsKnown()) {
                        why.add(fate.how() + ": the wiring does not say which of " + chain.to + "'s arguments are " + chain.from + "'s");
                        return null;
                    }
                }
                boolean wVoid = wr.getSort() == Type.VOID;
                String have = hp[ci].getDescriptor();
                if (have.equals(CIR) && wVoid) {
                    why.add("the target returns nothing in " + chain.to + ", the handler takes CallbackInfoReturnable");
                    return null;
                }
                boolean cbChange = have.equals(CI) && !wVoid;
                if (cbChange && bool(an, "cancellable")) {
                    why.add("cancellable, and the target now returns a value: a cancel would answer null where " + chain.from + "'s caller went on");
                    return null;
                }
                Type vrr = Type.getReturnType(chain.renamedDesc(vmeth.desc));
                RetConv rc = null;
                boolean untouched = false;
                if (have.equals(CIR) && !vrr.equals(wr)) {
                    curNumV = vrr;
                    curNumW = wr;
                    List<String> rwhy = new ArrayList<>();
                    rc = retConv(vmeth, vrr, wr, Type.getArgumentTypes(fate.desc()), rwhy);
                    if (rc == null) {
                        if (touchesReturnValue(h)) {
                            why.add("the handler reads or sets the return value, whose type changed (" + vrr + " -> " + wr
                                + ") and " + rwhy.get(0));
                            return null;
                        }
                        untouched = true;
                    }
                }
                boolean synth = cbChange || capture && (!sameArgs || fate.converts()) || rc != null;
                return new Plan(synth, capture, wVoid ? CI : CIR, ci, rc, untouched);
            }
            if (kind.equals("ModifyReturnValue") && !vr.equals(wr)) {
                why.add("the return type changed");
                return null;
            }
            if (sameArgs) {
                return new Plan(false, false, null, -1);
            }
            if (kind.equals("ModifyVariable") || hasSugar(h, "Local;")) {
                why.add("@" + kind + (kind.equals("ModifyVariable") ? "" : " with @Local") + ": the target's locals move with its new arguments");
                return null;
            }
            int base = anchorBase(h, an, kind, vmeth);
            if (base < 0) {
                why.add("@" + kind + ": its own arguments cannot be told from captured ones");
                return null;
            }
            if (hp.length - sugarCount(h) != base) {
                why.add("the @" + kind + " handler captures the target's arguments");
                return null;
            }
            return new Plan(false, false, null, -1);
        }

        /**
         * The converters between V method vmeth's answer (vr, as the converted jar spells it) and the W target's
         * (wr), in this order: a returnConverters entry on vmeth's class or a V supertype of the mixin's target;
         * numeric primitives (Java's conversion); a callReshapes "ret conv" of the member (W -> V) with an override
         * hook's unwrap (V -> W). Null when none, with why.
         */
        private RetConv retConv(MethodNode vmeth, Type vr, Type wr, Type[] wa, List<String> why) {
            for (String c : vSupers(vk(target))) {
                Chain.ReturnConverter e = chain.returnConverters.get(c + "." + vmeth.name + vmeth.desc);
                if (e == null) {
                    continue;
                }
                Type[] tv = Type.getArgumentTypes(e.toV()[2]);
                Type[] tw = Type.getArgumentTypes(e.toW()[2]);
                boolean ok = tv.length == 1 + e.with().length && tw.length == tv.length
                    && Type.getReturnType(e.toV()[2]).equals(vr) && Type.getReturnType(e.toW()[2]).equals(wr)
                    && tv[0].equals(wr) && tw[0].equals(vr);
                for (int i = 0; ok && i < e.with().length; i++) {
                    int j = e.with()[i];
                    ok = j >= 0 && j < wa.length && tv[i + 1].equals(wa[j]) && tw[i + 1].equals(wa[j]);
                }
                if (!ok) {
                    why.add("returnConverters " + c + "." + vmeth.name + vmeth.desc + " does not fit " + vr + " <-> " + wr);
                    return null;
                }
                return new RetConv(e.toV(), e.toW(), e.with(), false, "returnConverters (" + e.basis() + ")");
            }
            if (samePrecisionFamily(vr, wr)) {
                // float <-> double, int <-> long: the same quantity at another precision. Other numeric pairs are not
                // taken: a byte became a float when its meaning changed too (packed degrees -> degrees)
                return new RetConv(null, null, new int[0], true, "numeric " + vr + " <-> " + wr);
            }
            String rk = chain.instanceRedirectHome(chain.callReshapeIndex, vk(target), vmeth.name + vmeth.desc);
            Chain.CallReshape r = rk == null ? null : chain.callReshapes.get(rk + "." + vmeth.name + vmeth.desc);
            if (r != null && "conv".equals(r.retKind)) {
                for (Chain.OverrideHook hk : chain.overrideHooksForOld(vmeth.name, vmeth.desc)) {
                    if (hk.unwrapName != null) {
                        String[] toV = {r.retOwner, r.retName, r.retDesc};
                        String[] toW = {hk.unwrapOwner, hk.unwrapName, hk.unwrapDesc};
                        if (Type.getReturnType(toV[2]).equals(vr) && Type.getArgumentTypes(toV[2]).length == 1
                                && Type.getArgumentTypes(toV[2])[0].equals(wr) && Type.getReturnType(toW[2]).equals(wr)
                                && Type.getArgumentTypes(toW[2]).length == 1 && Type.getArgumentTypes(toW[2])[0].equals(vr)) {
                            return new RetConv(toV, toW, new int[0], false, "the call reshape's ret conv and the override hook's unwrap");
                        }
                    }
                }
            }
            why.add("no converter between them (returnConverters, a numeric pair, or a ret conv with an override hook's unwrap)");
            return null;
        }

        /** a V class and its V supertypes, nearest first */
        private List<String> vSupers(String c) {
            List<String> out = new ArrayList<>();
            ArrayDeque<String> todo = new ArrayDeque<>(List.of(c));
            while (!todo.isEmpty()) {
                String x = todo.poll();
                if (out.contains(x)) {
                    continue;
                }
                out.add(x);
                ClassNode cn = v(x);
                if (cn != null) {
                    if (cn.superName != null) {
                        todo.add(cn.superName);
                    }
                    todo.addAll(cn.interfaces);
                }
            }
            return out;
        }

        /**
         * A static of the jar's helper class turning an answer as the callback holds it (boxed) into the other
         * side's (boxed): {@code (Object, with..)Object}, a null passing through without a call. toV: W's answer
         * into V's; else V's into W's. Returns {name, desc}.
         */
        private String[] retHelper(RetConv rc, boolean toV, Type[] wa) {
            StringBuilder d = new StringBuilder("(Ljava/lang/Object;");
            for (int j : rc.with()) {
                d.append(wa[j].getDescriptor());
            }
            String desc = d.append(")Ljava/lang/Object;").toString();
            String name = "r" + (helperSeq++) + "$" + sn.replace('$', '_') + (toV ? "$toV" : "$toW");
            MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name, desc, null, null);
            Type from = rc.numeric() ? null : Type.getArgumentTypes((toV ? rc.toV() : rc.toW())[2])[0];
            InsnList in = m.instructions;
            LabelNode go = new LabelNode();
            in.add(new VarInsnNode(Opcodes.ALOAD, 0));
            in.add(new JumpInsnNode(Opcodes.IFNONNULL, go));
            in.add(new InsnNode(Opcodes.ACONST_NULL));
            in.add(new InsnNode(Opcodes.ARETURN));
            in.add(go);
            List<Object> locals = new ArrayList<>(List.of("java/lang/Object"));
            for (int j : rc.with()) {
                locals.add(JarProcessor.frameType(wa[j]));
            }
            in.add(new FrameNode(Opcodes.F_NEW, locals.size(), locals.toArray(), 0, new Object[0]));
            in.add(new VarInsnNode(Opcodes.ALOAD, 0));
            Type to;
            if (rc.numeric()) {
                to = numericTarget(rc, toV);
                in.add(new TypeInsnNode(Opcodes.CHECKCAST, "java/lang/Number"));
                in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/lang/Number", to.getClassName() + "Value", "()" + to.getDescriptor(), false));
            } else {
                String[] conv = toV ? rc.toV() : rc.toW();
                unbox(in, from);
                int slot = 1;
                for (int j : rc.with()) {
                    in.add(new VarInsnNode(wa[j].getOpcode(Opcodes.ILOAD), slot));
                    slot += wa[j].getSize();
                }
                in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, conv[0], conv[1], conv[2], false));
                to = Type.getReturnType(conv[2]);
            }
            box(in, to);
            in.add(new InsnNode(Opcodes.ARETURN));
            int ml = 1;
            for (int j : rc.with()) {
                ml += wa[j].getSize();
            }
            m.maxLocals = ml;
            m.maxStack = 4 + rc.with().length * 2;
            helperMethods.add(m);
            return new String[] {name, desc};
        }

        /** the numeric pair's target type: V's answer for toV, W's for toW (read off the moved target) */
        private Type numericTarget(RetConv rc, boolean toV) {
            return toV ? curNumV : curNumW;
        }

        /**
         * {@code (CallbackInfoReturnable w, CallbackInfoReturnable v, with..)V}: when the mod's handler cancelled V's
         * callback, W's is cancelled too, with V's answer turned into W's when it set one.
         */
        private String[] backHelper(RetConv rc, String[] toW, Type[] wa) {
            String cirT = CIR.substring(1, CIR.length() - 1);
            String ciT = CI.substring(1, CI.length() - 1);
            StringBuilder d = new StringBuilder("(" + CIR + CIR);
            for (int j : rc.with()) {
                d.append(wa[j].getDescriptor());
            }
            String desc = d.append(")V").toString();
            String name = "r" + (helperSeq++) + "$" + sn.replace('$', '_') + "$back";
            MethodNode m = new MethodNode(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name, desc, null, null);
            int n = 2;
            List<Object> locals = new ArrayList<>(List.of(cirT, cirT));
            for (int j : rc.with()) {
                locals.add(JarProcessor.frameType(wa[j]));
                n += wa[j].getSize();
            }
            InsnList in = m.instructions;
            LabelNode end = new LabelNode();
            LabelNode set = new LabelNode();
            in.add(new VarInsnNode(Opcodes.ALOAD, 1));
            in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ciT, "isCancelled", "()Z", false));
            in.add(new JumpInsnNode(Opcodes.IFEQ, end));
            in.add(new VarInsnNode(Opcodes.ALOAD, 1));
            in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, cirT, "getReturnValue", "()Ljava/lang/Object;", false));
            in.add(new VarInsnNode(Opcodes.ASTORE, n));
            in.add(new VarInsnNode(Opcodes.ALOAD, n));
            in.add(new JumpInsnNode(Opcodes.IFNONNULL, set));
            in.add(new VarInsnNode(Opcodes.ALOAD, 0));
            in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ciT, "cancel", "()V", false));
            in.add(new InsnNode(Opcodes.RETURN));
            in.add(set);
            List<Object> withN = new ArrayList<>(locals);
            withN.add("java/lang/Object");
            in.add(new FrameNode(Opcodes.F_NEW, withN.size(), withN.toArray(), 0, new Object[0]));
            in.add(new VarInsnNode(Opcodes.ALOAD, 0));
            in.add(new VarInsnNode(Opcodes.ALOAD, n));
            int slot = 2;
            for (int j : rc.with()) {
                in.add(new VarInsnNode(wa[j].getOpcode(Opcodes.ILOAD), slot));
                slot += wa[j].getSize();
            }
            in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, helperName, toW[0], toW[1], false));
            in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, cirT, "setReturnValue", "(Ljava/lang/Object;)V", false));
            in.add(end);
            in.add(new FrameNode(Opcodes.F_NEW, locals.size(), locals.toArray(), 0, new Object[0]));
            in.add(new InsnNode(Opcodes.RETURN));
            m.maxLocals = n + 1;
            m.maxStack = 4 + rc.with().length * 2;
            helperMethods.add(m);
            return new String[] {name, desc};
        }

        /** how many handler arguments the injector itself defines (the anchor's), -1 when unknown */
        private int anchorBase(MethodNode h, AnnotationNode an, String kind, MethodNode vmeth) {
            switch (kind) {
                case "ModifyExpressionValue", "ModifyReturnValue", "ModifyConstant", "ModifyReceiver", "ModifyArgs" -> {
                    return 1;
                }
                default -> { }
            }
            List<AnnotationNode> ats = new ArrayList<>();
            if (an.values != null) {
                for (int i = 0; i + 1 < an.values.size(); i += 2) {
                    if ("at".equals(an.values.get(i))) {
                        collectAts(an.values.get(i + 1), ats);
                    }
                }
            }
            if (ats.size() != 1 || vmeth.instructions == null) {
                return -1;
            }
            Map<String, Object> av = MixinRepair.values(ats.get(0));
            String value = String.valueOf(av.get("value"));
            MixinRepair.Anchor a = value.equals("INVOKE") || value.equals("INVOKE_ASSIGN") ? MixinRepair.parseAt(av, table) : null;
            if (a == null || a.desc() == null) {
                return -1;
            }
            int op = -1;
            for (AbstractInsnNode in : vmeth.instructions) {
                if (MixinRepair.matchesAnchor(in, a)) {
                    op = in.getOpcode();
                    break;
                }
            }
            if (op < 0) {
                return -1;
            }
            int n = Type.getArgumentTypes(a.desc()).length + (op == Opcodes.INVOKESTATIC ? 0 : 1);
            return switch (kind) {
                case "Redirect", "WrapWithCondition" -> n;
                case "WrapOperation" -> n + 1;
                case "ModifyArg" -> Type.getArgumentTypes(h.desc).length - sugarCount(h) == 1 ? 1 : -1;
                default -> -1;
            };
        }

        /**
         * The W-shaped @Inject handler: what JarProcessor.synthesizeOverride is for an override. It
         * takes W's arguments (when the handler took the target's) and the callback W's return
         * calls for, and forwards to the mod's own handler -- kept under a private name of its own --
         * with V's arguments: W's argument the wiring maps each from, through the hook's wrap
         * converter where the type changed; a widened argument (narrowing hook, keep with a wider
         * type) reaches the handler only when it is V's type, as V only ever passed that.
         */
        private MethodNode synthesizeInject(MethodNode h, Fate fate, Plan p) {
            boolean st = (h.access & Opcodes.ACC_STATIC) != 0;
            Type[] hp = Type.getArgumentTypes(h.desc);
            Type[] wa = Type.getArgumentTypes(fate.desc());
            String oldName = h.name;
            String oldDesc = h.desc;
            String del = unique(oldName + "$cb" + step);
            StringBuilder d = new StringBuilder("(");
            if (p.wArgs()) {
                for (Type t : wa) {
                    d.append(t.getDescriptor());
                }
            }
            d.append(p.cb()).append(")V");
            MethodNode syn = new MethodNode(h.access & ~(Opcodes.ACC_ABSTRACT | Opcodes.ACC_NATIVE | Opcodes.ACC_BRIDGE
                | Opcodes.ACC_VARARGS), oldName, d.toString(), null, exceptions(h));
            syn.visibleAnnotations = h.visibleAnnotations;
            syn.invisibleAnnotations = h.invisibleAnnotations;
            h.visibleAnnotations = null;
            h.invisibleAnnotations = null;
            Refs r = refs(oldName, oldDesc, false);
            for (MethodInsnNode mi : r.calls) {
                mi.name = del;
            }
            retargetHandles(r, mx.name, del, itf);
            h.name = del;
            h.access = (h.access & ~(Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PRIVATE;
            int[] wslot = new int[wa.length];
            int s = st ? 0 : 1;
            for (int j = 0; j < wa.length; j++) {
                wslot[j] = s;
                s += wa[j].getSize();
            }
            int cbSlot = p.wArgs() ? s : (st ? 0 : 1);
            int vcbSlot = cbSlot + 1;
            InsnList in = syn.instructions;
            LabelNode skip = null;
            for (int i = 0; p.capture() && i < p.ci(); i++) {
                if (fate.narrow()[i]) {
                    skip = skip == null ? new LabelNode() : skip;
                    in.add(new VarInsnNode(Opcodes.ALOAD, wslot[fate.from()[i]]));
                    in.add(new TypeInsnNode(Opcodes.INSTANCEOF, hp[i].getInternalName()));
                    in.add(new JumpInsnNode(Opcodes.IFEQ, skip));
                }
            }
            String[] back = null;
            if (p.rc() != null) {
                // V's callback: the W answer so far as V's (toV), handed back as W's when the mod's handler set one
                String cirT = CIR.substring(1, CIR.length() - 1);
                String ciT = CI.substring(1, CI.length() - 1);
                String[] toV = retHelper(p.rc(), true, wa);
                back = backHelper(p.rc(), retHelper(p.rc(), false, wa), wa);
                in.add(new TypeInsnNode(Opcodes.NEW, cirT));
                in.add(new InsnNode(Opcodes.DUP));
                in.add(new VarInsnNode(Opcodes.ALOAD, cbSlot));
                in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ciT, "getId", "()Ljava/lang/String;", false));
                in.add(new VarInsnNode(Opcodes.ALOAD, cbSlot));
                in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, ciT, "isCancellable", "()Z", false));
                in.add(new VarInsnNode(Opcodes.ALOAD, cbSlot));
                in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, cirT, "getReturnValue", "()Ljava/lang/Object;", false));
                for (int j : p.rc().with()) {
                    in.add(new VarInsnNode(wa[j].getOpcode(Opcodes.ILOAD), wslot[j]));
                }
                in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, helperName, toV[0], toV[1], false));
                in.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, cirT, "<init>", "(Ljava/lang/String;ZLjava/lang/Object;)V", false));
                in.add(new VarInsnNode(Opcodes.ASTORE, vcbSlot));
            }
            if (!st) {
                in.add(new VarInsnNode(Opcodes.ALOAD, 0));
            }
            for (int i = 0; p.capture() && i < p.ci(); i++) {
                int j = fate.from()[i];
                in.add(new VarInsnNode(wa[j].getOpcode(Opcodes.ILOAD), wslot[j]));
                String[] wr = fate.wrap()[i];
                if (wr != null) {
                    in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, wr[0], wr[1], wr[2], false));
                }
                Type got = wr != null ? Type.getReturnType(wr[2]) : wa[j];
                if (!got.equals(hp[i]) && hp[i].getSort() >= Type.ARRAY) {
                    in.add(new TypeInsnNode(Opcodes.CHECKCAST, hp[i].getInternalName()));
                }
            }
            in.add(new VarInsnNode(Opcodes.ALOAD, p.rc() != null ? vcbSlot : cbSlot));
            in.add(new MethodInsnNode(st ? Opcodes.INVOKESTATIC : Opcodes.INVOKESPECIAL, mx.name, del, oldDesc, false));
            if (back != null) {
                in.add(new VarInsnNode(Opcodes.ALOAD, cbSlot));
                in.add(new VarInsnNode(Opcodes.ALOAD, vcbSlot));
                for (int j : p.rc().with()) {
                    in.add(new VarInsnNode(wa[j].getOpcode(Opcodes.ILOAD), wslot[j]));
                }
                in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, helperName, back[0], back[1], false));
            }
            in.add(new InsnNode(Opcodes.RETURN));
            if (skip != null) {
                List<Object> locals = new ArrayList<>();
                if (!st) {
                    locals.add(mx.name);
                }
                for (Type t : p.wArgs() ? wa : new Type[0]) {
                    locals.add(JarProcessor.frameType(t));
                }
                locals.add(p.cb().substring(1, p.cb().length() - 1));
                in.add(skip);
                in.add(new FrameNode(Opcodes.F_NEW, locals.size(), locals.toArray(), 0, new Object[0]));
                in.add(new InsnNode(Opcodes.RETURN));
            }
            syn.maxLocals = s + 2;
            syn.maxStack = s + 6;
            mx.methods.add(syn);
            return syn;
        }

        /**
         * The first anchor of the injector that the W method does not have -- neither as written nor as
         * what the wiring says stands there now, counted as often as in the V method (what {@link #ats}
         * then retargets) -- or null. A selector is only carried to a method where its injection can
         * happen: MixinRepair would otherwise disable it there, and the bridging would be claimed for
         * nothing. Anchors that cannot be evaluated statically are not judged (null), as MixinRepair
         * leaves them alone too.
         */
        /**
         * The injector followed vmeth to wmeth, whose body no longer holds its anchor: MixinRepair's
         * descend-site, from V's method to what W's new one calls (one anchor, a handler taking no target
         * arguments -- the callee's are its own). Null when not applicable or refused (why says so).
         */
        private MixinRepair.Descend descendFollowed(AnnotationNode an, MethodNode h, MethodNode vmeth, MethodNode wmeth,
                                                    ClassNode vt, ClassNode wt, List<String> why) {
            MixinRepair code = wcode(chain);
            if (code == null || hasSugar(h, "Local;")) {
                return null;
            }
            List<MixinRepair.Anchor> anchors = code.anchors(an, MixinRepair.values(an), table);
            if (anchors == null || anchors.size() != 1 || MixinRepair.capturesTargetArgs(an, h, anchors)) {
                return null;
            }
            return code.descendBySite(an, h, wt, vt, vmeth, wmeth, anchors.get(0), why);
        }

        private String anchorGone(AnnotationNode an, MethodNode h, String kind, MethodNode vmeth, MethodNode wmeth, ClassNode wt) {
            List<MixinRepair.Anchor> anchors = wcode(chain).anchors(an, MixinRepair.values(an), table);
            if (anchors == null || anchors.isEmpty() || MixinRepair.anchorsIn(wmeth, wt, anchors)) {
                return null;
            }
            for (MixinRepair.Anchor a : anchors) {
                if (a.kind().equals("LOCAL")) {
                    return a.toString(); // a slot in a body whose arguments changed
                }
                if (MixinRepair.anchorIn(wmeth, a)) {
                    continue;
                }
                // what ats() can retarget: the wiring's new anchor, as often in W as the old in V, with the
                // handler able to take it
                AtFate af = a.kind().equals("INVOKE") || a.kind().equals("FIELD") ? atFate(a) : null;
                List<String> cwhy = new ArrayList<>();
                boolean byContext = a.role().equals("main") && counterpartAnchor(kind, h, a, List.of(vmeth), List.of(wmeth), cwhy) != null;
                String gone = a + (cwhy.isEmpty() ? "" : " (in its place " + cwhy.get(0) + ")");
                if (af == null || !af.desc().equals(a.desc()) && !kind.equals("Inject")
                        || kind.equals("Inject") && capturesLocals(h) || hasSugar(h, "Local;")) {
                    if (byContext) {
                        continue; // ats() retargets it to what stands in its place
                    }
                    return gone;
                }
                MixinRepair.Anchor na = new MixinRepair.Anchor(a.kind(), af.owner(), af.name(), af.desc(), null, a.ordinal(), a.role());
                int vc = count(List.of(vmeth), a);
                if (vc == 0 || count(List.of(wmeth), na) != vc) {
                    if (byContext) {
                        continue;
                    }
                    return gone;
                }
            }
            return null;
        }

        /**
         * @At INVOKE / FIELD anchors the step moved, in the methods the injector targets: retargeted
         * to what the wiring says stands there now, once that is counted in W's methods exactly as
         * often as the old anchor stood in V's (an ordinal keeps pointing at the same occurrence).
         * A call whose shape changed carries only a positional @Inject: other injectors take the
         * call's operands.
         */
        private void ats(Selection sel, String kind) {
            if (sel.v().isEmpty() || sel.w().isEmpty()) {
                return;
            }
            for (AnnotationNode at : atNodes(sel.an())) {
                Map<String, Object> av = MixinRepair.values(at);
                String value = String.valueOf(av.get("value"));
                if (value.equals("NEW") || value.equals("CONSTANT")) {
                    // no wiring speaks of allocations or constants: only what W's body has in their place
                    MixinRepair.Anchor a = MixinRepair.parseAt(av, table);
                    if (a == null || a == MixinRepair.POSITIONAL || count(sel.w(), a) > 0 || count(sel.v(), a) == 0) {
                        continue;
                    }
                    counterpartAt(sel, kind, at, value, av.get("target") instanceof String nt ? nt : null, a, null);
                    continue;
                }
                if (!value.equals("INVOKE") && !value.equals("INVOKE_ASSIGN") && !value.equals("FIELD")
                        || !(av.get("target") instanceof String t)) {
                    continue;
                }
                MixinRepair.Anchor a = MixinRepair.parseAt(av, table);
                if (a == null || a.owner() == null || a.desc() == null || count(sel.w(), a) > 0) {
                    continue;
                }
                int vc = count(sel.v(), a);
                if (vc == 0) {
                    continue; // not where the mod wrote it in V either: not this step's doing
                }
                boolean fld = a.kind().equals("FIELD");
                String where = "@" + kind + " " + sn + "." + sel.handler().name + " @At " + value + " " + shortName(a.owner())
                    + "." + a.name() + (fld ? ":" : "") + a.desc();
                AtFate af = atFate(a);
                if (af == null) {
                    if (!value.equals("INVOKE_ASSIGN")) {
                        counterpartAt(sel, kind, at, value, t, a, null);
                    }
                    continue;
                }
                MixinRepair.Anchor na = new MixinRepair.Anchor(a.kind(), af.owner(), af.name(), af.desc(), null, a.ordinal());
                int wc = count(sel.w(), na);
                if (wc != vc) {
                    String wired = af.how() + " gives " + af.name() + af.desc() + ", standing " + wc + " time(s) in "
                        + chain.to + " where the old one stood " + vc + " time(s) in " + chain.from;
                    if (value.equals("INVOKE_ASSIGN") || !counterpartAt(sel, kind, at, value, t, a, wired)) {
                        left.add(where + ": " + wired);
                    }
                    continue;
                }
                if (!af.desc().equals(a.desc()) && !kind.equals("Inject")) {
                    left.add(where + ": the call's shape changed (" + af.how() + "), and @" + kind + " handles the call itself");
                    continue;
                }
                if (RECEIVER_KINDS.contains(kind) && !af.owner().equals(a.owner()) && takesReceiver(sel.v(), a)) {
                    // the handler takes the call's receiver first, typed as V's owner; W's call is on another object
                    // (Minecraft.setScreen -> minecraft.gui's Gui.setScreen), which the handler cannot take
                    left.add(where + ": " + af.how() + " gives " + shortName(af.owner()) + "." + af.name() + af.desc()
                        + ", a call on another receiver than " + shortName(a.owner()) + ", which @" + kind + "'s handler takes");
                    continue;
                }
                if (kind.equals("Inject") && capturesLocals(sel.handler()) || hasSugar(sel.handler(), "Local;")) {
                    // the locals Mixin hands it are read at the anchor, in a body that changed around it
                    left.add(where + ": the handler captures the target's locals at the anchor");
                    continue;
                }
                String nt = "L" + af.owner() + ";" + af.name() + (fld ? ":" : "") + af.desc();
                if (ownKey(t)) {
                    MixinRepair.refmapPut(table, t, nt);
                    refmapDirty = true;
                } else {
                    setValue(at, "target", nt);
                }
                changed = true;
                injected("@" + kind + " " + sn + "." + sel.handler().name, where + " => " + af.name() + (fld ? ":" : "") + af.desc() + " (" + af.how() + ")");
            }
        }

        /**
         * An @At whose anchor W's body no longer holds, with no wiring that puts the same number of
         * anything in its place: retargeted to the instruction W has where each V site stood
         * ({@link SiteMatch#counterpart}: same kind, same significant neighbours, unique on both sides).
         * A positional @Inject, or an injector handling the call or value itself when the stand-in
         * takes exactly the operands its handler is written for ({@link #sameOperands}); never a
         * handler reading the target's locals, which it would read at a site whose surroundings changed. One new
         * anchor must stand at every counterpart; it is pinned by ordinal unless its occurrences in W
         * are exactly the counterparts. True when retargeted; otherwise the reason is reported.
         */
        private boolean counterpartAt(Selection sel, String kind, AnnotationNode at, String value, String t,
                                      MixinRepair.Anchor a, String wired) {
            String where = "@" + kind + " " + sn + "." + sel.handler().name + " @At " + value + " " + a;
            List<String> why = new ArrayList<>();
            MixinRepair.Anchor na = counterpartAnchor(kind, sel.handler(), a, sel.v(), sel.w(), why);
            if (na == null) {
                if (!why.isEmpty()) {
                    left.add(where + ": " + (wired != null ? wired + "; " : "") + "in its place " + why.get(0));
                }
                return false;
            }
            switch (value) {
                case "INVOKE", "FIELD" -> {
                    if (!na.kind().equals(value)) {
                        setValue(at, "value", na.kind()); // a positional @Inject's call re-described as a field read, or back
                    }
                    String nt = "L" + na.owner() + ";" + na.name() + (na.kind().equals("FIELD") ? ":" : "") + na.desc();
                    if (t != null && ownKey(t)) {
                        MixinRepair.refmapPut(table, t, nt);
                        refmapDirty = true;
                    } else {
                        setValue(at, "target", nt);
                    }
                }
                case "NEW" -> {
                    String nt = t != null && t.startsWith("L") ? "L" + na.owner() + ";" : na.owner();
                    if (t != null && ownKey(t)) {
                        MixinRepair.refmapPut(table, t, nt);
                        refmapDirty = true;
                    } else {
                        setValue(at, "target", nt);
                    }
                }
                case "CONSTANT" -> {
                    String key = constantKey(na.constant());
                    List<Object> args = new ArrayList<>();
                    for (Object o : MixinRepair.list(MixinRepair.values(at).get("args"))) {
                        String sa = String.valueOf(o);
                        args.add(sa.matches("\\s*(int|float|long|double|string)Value\\s*=.*") ? key + "=" + na.constant() : sa);
                    }
                    setValue(at, "args", args);
                }
                default -> {
                    return false;
                }
            }
            if (na.ordinal() >= 0) {
                setValue(at, "ordinal", na.ordinal());
            } else if (at.values != null) {
                for (int i = 0; i + 1 < at.values.size(); i += 2) {
                    if ("ordinal".equals(at.values.get(i))) {
                        at.values.remove(i + 1);
                        at.values.remove(i);
                        break;
                    }
                }
            }
            changed = true;
            injected("@" + kind + " " + sn + "." + sel.handler().name, where + " => " + na + " (the instruction standing in its place in "
                + chain.to + ", by context" + (wired != null ? "; the wiring's own count differs" : "") + ")");
            return true;
        }

        /** the anchor standing where V's sites of {@code a} stood (see counterpartAt), or null with why */
        private MixinRepair.Anchor counterpartAnchor(String kind, MethodNode h, MixinRepair.Anchor a, List<MethodNode> vms,
                                                     List<MethodNode> wms, List<String> why) {
            boolean operands = OPERAND_KINDS.contains(kind);
            if (!kind.equals("Inject") && !operands) {
                why.add("@" + kind + " is not carried to a stand-in");
                return null;
            }
            if (capturesLocals(h) || hasSugar(h, "Local;")) {
                why.add("the handler reads the target's locals, in a body that changed around the anchor");
                return null;
            }
            if (vms.size() != 1 || wms.size() != 1) {
                why.add("the injector targets " + vms.size() + " method(s) in " + chain.from + " and " + wms.size() + " in " + chain.to);
                return null;
            }
            MethodNode vm = vms.get(0);
            MethodNode wm = wms.get(0);
            List<AbstractInsnNode> vo = SiteMatch.occurrences(vm, in -> MixinRepair.matchesAnchor(in, a));
            if (vo.isEmpty() || a.ordinal() >= vo.size()) {
                why.add("the anchor does not stand in " + chain.from + "'s body as the mod names it");
                return null;
            }
            List<AbstractInsnNode> sites = a.ordinal() >= 0 ? List.of(vo.get(a.ordinal())) : vo;
            List<AbstractInsnNode> found = new ArrayList<>();
            for (AbstractInsnNode site : sites) {
                // a positional @Inject marks a place: a value read there (a getter call re-described as a field
                // read of a render state) stands as well as a call of the same kind; operand-taking ones need the kind
                AbstractInsnNode c = SiteMatch.counterpart(vm, site, wm, in -> SiteMatch.sameKind(site, in)
                    || !operands && SiteMatch.valueRead(site) && SiteMatch.valueRead(in), !operands);
                if (c == null) {
                    why.add("W's body has no single instruction of its kind between the same neighbours ("
                        + (sites.size() == 1 ? "the site" : found.size() + " of " + sites.size() + " sites found") + ")");
                    return null;
                }
                if (operands && !sameOperands(kind, site, c)) {
                    // the handler takes the call's / value's operands: they must be exactly V's
                    why.add("what stands there (" + anchorOf(c) + ") does not take the operands @" + kind + "'s handler is written for");
                    return null;
                }
                if (operands && !sameMember(site, c) && wLinks(site)) {
                    // W still has V's member and calls another in its place (IntStream.range -> rangeClosed):
                    // another operation, whose operands mean something else to a handler written for V's
                    why.add("what stands there (" + anchorOf(c) + ") is another member, and " + chain.to + " still has "
                        + anchorOf(site) + ": a different operation, not its replacement");
                    return null;
                }
                found.add(c);
            }
            MixinRepair.Anchor na = anchorOf(found.get(0));
            for (AbstractInsnNode c : found) {
                if (!MixinRepair.matchesAnchor(c, na)) {
                    why.add("different instructions stand in place of its " + sites.size() + " sites");
                    return null;
                }
            }
            List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> MixinRepair.matchesAnchor(in, na));
            if (sites.size() == 1 && (a.ordinal() >= 0 || wo.size() > 1)) {
                return withOrdinal(na, wo.indexOf(found.get(0)));
            }
            if (!new HashSet<>(found).equals(new HashSet<>(wo))) {
                why.add(na + " stands in place of all " + sites.size() + " sites, and " + (wo.size() - found.size()) + " time(s) elsewhere");
                return null;
            }
            return na;
        }

        /**
         * V's class overrode the method and held the anchor in its body; W's inherits the method from a
         * superclass whose body holds it (V's site by context, as in any followed target): the injector
         * moves into a mixin of that superclass, guarded by {@code this instanceof} the target -- it
         * runs for exactly the instances V's override ran for. The mod's handler stays in its mixin as a
         * plain public method (merged into the target), which the guarded handler calls; outside the
         * target the guarded handler does nothing ({@code @Inject}) or hands the value back unchanged
         * ({@code @ModifyExpressionValue}, {@code @ModifyArg}, {@code @ModifyConstant} with the one
         * value). Refused when the target's instances are not V's: a V subclass of the target that W
         * moved out from under it, or a class new in W under the superclass but outside the target
         * (which V class it stands for is not read off the hierarchy). Handlers capturing locals or
         * carrying sugar, static handlers, and injectors handling a call itself are not moved.
         */
        private Selection baseGuard(MethodNode h, AnnotationNode an, String kind, MethodNode vmeth, ClassNode wt,
                                    String where, List<String> why) {
            MixinRepair code = wcode(chain);
            if (itf || code == null || chain.sourceCode == null || !GUARD_KINDS.contains(kind)
                    || (h.access & Opcodes.ACC_STATIC) != 0 || (vmeth.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0
                    || vmeth.name.startsWith("<") || capturesLocals(h) || sugarCount(h) > 0) {
                return null;
            }
            if (!kind.equals("Inject") && Type.getArgumentTypes(h.desc).length != 1) {
                return null; // the guard's other branch hands back its one value
            }
            List<MixinRepair.Anchor> anchors = code.anchors(an, MixinRepair.values(an), table);
            if (anchors == null || anchors.isEmpty()) {
                return null; // HEAD / RETURN / TAIL: a restored override carries them
            }
            for (MethodNode m : mx.methods) {
                if (m != h && m.name.equals(vmeth.name) && m.desc.equals(vmeth.desc)) {
                    return null;
                }
            }
            ClassNode decl = null;
            MethodNode inherited = null;
            for (String c = wt.superName; c != null && inherited == null; ) {
                ClassNode cn = w(c);
                if (cn == null) {
                    return null;
                }
                MethodNode m = method(cn, vmeth.name, vmeth.desc);
                if (m != null) {
                    decl = cn;
                    inherited = m;
                }
                c = cn.superName;
            }
            if (inherited == null || (inherited.access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE)) != 0
                    || inherited.instructions == null || !Chain.minecraftOwned(decl.name)) {
                return null;
            }
            // whose instances the guard admits: the target's in W, which must be the target's in V
            Set<String> wSub = new HashSet<>(code.subclasses(target));
            wSub.add(target);
            List<String> movedOut = new ArrayList<>();
            for (String c : chain.sourceCode.subclasses(target)) {
                if (!wSub.contains(c) && w(c) != null) {
                    movedOut.add(c);
                }
            }
            if (!movedOut.isEmpty()) {
                why.add(shortName(decl.name) + "." + vmeth.name + vmeth.desc + " holds the code W moved up, but "
                    + shortName(movedOut.get(0)) + (movedOut.size() > 1 ? " (and " + (movedOut.size() - 1) + " more)" : "")
                    + ", a " + shortName(target) + " in " + chain.from + ", is not one in " + chain.to + ": this instanceof "
                    + shortName(target) + " would miss it");
                return null;
            }
            List<String> newOut = new ArrayList<>();
            for (String c : code.subclasses(decl.name)) {
                if (!wSub.contains(c) && chain.sourceCode.target(c) == null) {
                    newOut.add(c);
                }
            }
            if (!newOut.isEmpty()) {
                why.add(shortName(decl.name) + "." + vmeth.name + vmeth.desc + " holds the code W moved up, but "
                    + shortName(newOut.get(0)) + (newOut.size() > 1 ? " (and " + (newOut.size() - 1) + " more)" : "")
                    + ", new in " + chain.to + " under it, may stand for a " + shortName(target) + " of " + chain.from);
                return null;
            }
            String sel = vmeth.name + vmeth.desc;
            AnnotationNode moved = literalCopy(an, sel);
            String gone = anchorGone(moved, h, kind, vmeth, inherited, decl);
            if (gone != null) {
                why.add(shortName(decl.name) + "." + sel + ", which " + shortName(target) + " now inherits, does not hold its anchor "
                    + gone);
                return null;
            }
            ClassNode vDecl = chain.sourceCode.target(decl.name);
            String sites = guardSites(moved, vmeth, inherited, vDecl == null ? null : method(vDecl, vmeth.name, vmeth.desc));
            if (sites != null) {
                why.add(shortName(decl.name) + "." + sel + ", which " + shortName(target) + " now inherits: " + sites);
                return null;
            }
            // the mod's handler: a plain public method of the target
            String oldName = h.name;
            String oldDesc = h.desc;
            String plain = unique("cb$guarded$" + oldName);
            Refs r = refs(oldName, oldDesc, false);
            for (MethodInsnNode mi : r.calls) {
                mi.name = plain;
            }
            retargetHandles(r, mx.name, plain, itf);
            if (h.visibleAnnotations != null) {
                h.visibleAnnotations.remove(an);
            }
            if (h.invisibleAnnotations != null) {
                h.invisibleAnnotations.remove(an);
            }
            h.name = plain;
            h.access = (h.access & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
            // the guarded injector in the superclass's mixin
            ClassNode gc = guardMixin(decl.name);
            String gname = shortName(mx.name).replaceAll("[^A-Za-z0-9_]", "_") + "$" + oldName;
            for (MethodNode m : gc.methods) {
                if (m.name.equals(gname)) {
                    gname = gname + "_";
                }
            }
            MethodNode g = new MethodNode(Opcodes.ACC_PRIVATE, gname, oldDesc, null, exceptions(h));
            g.visibleAnnotations = new ArrayList<>(List.of(moved));
            Type[] hp = Type.getArgumentTypes(oldDesc);
            Type ret = Type.getReturnType(oldDesc);
            LabelNode other = new LabelNode();
            g.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            g.instructions.add(new TypeInsnNode(Opcodes.INSTANCEOF, target));
            g.instructions.add(new JumpInsnNode(Opcodes.IFEQ, other));
            g.instructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
            g.instructions.add(new TypeInsnNode(Opcodes.CHECKCAST, target));
            int slot = 1;
            for (Type t : hp) {
                g.instructions.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
                slot += t.getSize();
            }
            g.instructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, target, plain, oldDesc, false));
            g.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
            g.instructions.add(other);
            g.instructions.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null));
            if (ret.getSort() == Type.VOID) {
                g.instructions.add(new InsnNode(Opcodes.RETURN));
            } else {
                g.instructions.add(new VarInsnNode(hp[0].getOpcode(Opcodes.ILOAD), 1));
                g.instructions.add(new InsnNode(ret.getOpcode(Opcodes.IRETURN)));
            }
            gc.methods.add(g);
            AnnotationNode mark = new AnnotationNode(GUARDED);
            mark.values = new ArrayList<>(List.of("of", oldName, "mixin", gc.name, "handler", gname));
            h.invisibleAnnotations = h.invisibleAnnotations == null ? new ArrayList<>() : h.invisibleAnnotations;
            h.invisibleAnnotations.add(mark);
            changed = true;
            injected("@" + kind + " " + sn + "." + oldName, where + " => " + shortName(decl.name) + "." + sel
                + " (the override " + chain.to + " dropped held the anchor; W keeps it in the superclass: injected there, guarded by"
                + " this instanceof " + shortName(target) + ", calling the mod's handler)");
            return new Selection(g, moved, List.of(vmeth), List.of(inherited));
        }

        /**
         * The count rule for an injector moved into another class's body: an anchor standing there as often
         * as in V's override keeps its pin (the occurrences correspond in order, as MixinRepair takes them on
         * any target). Otherwise each V site is found by context ({@link SiteMatch#match}, a context of 3
         * or more, no alignment -- another method than V's own) among the occurrences there. A pinned anchor
         * is re-pinned to its site's counterpart; an unpinned one must find every V site, and fires on
         * W's occurrences only if they are exactly those (one V site among more W occurrences is pinned).
         * When the superclass declared the method in V too, its own sites there (found from V's copy of
         * it) are not the override's code: no V site may land on one, and the shortcut by count is off.
         * An anchor W's method does not hold is left to the stand-in (ats). Null when the sites carry,
         * else why not.
         */
        private String guardSites(AnnotationNode moved, MethodNode vmeth, MethodNode wm, MethodNode vOwn) {
            for (Object c : MixinRepair.list(MixinRepair.values(moved).get("constant"))) {
                if (c != null) {
                    return "@Constant anchors are not re-pinned in another class's body";
                }
            }
            for (AnnotationNode at : atNodes(moved)) {
                MixinRepair.Anchor a = MixinRepair.parseAt(MixinRepair.values(at), null);
                if (a == null || a == MixinRepair.POSITIONAL || a.kind().equals("LOCAL")) {
                    continue;
                }
                List<AbstractInsnNode> vo = SiteMatch.occurrences(vmeth, in -> MixinRepair.matchesAnchor(in, a));
                List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> MixinRepair.matchesAnchor(in, a));
                if (vOwn != null && (wo.isEmpty() || vo.isEmpty())) {
                    return "the superclass had the method in " + chain.from + " too, and " + a + " has no site of V's override there";
                }
                // what the superclass's own copy in V already held is not the override's code moved up
                Set<Integer> own = new HashSet<>();
                if (vOwn != null) {
                    List<AbstractInsnNode> oo = SiteMatch.occurrences(vOwn, in -> MixinRepair.matchesAnchor(in, a));
                    for (int j : SiteMatch.match(vOwn, oo, wm, wo)) {
                        if (j >= 0) {
                            own.add(j);
                        }
                    }
                }
                if (wo.isEmpty() || vo.isEmpty() || vOwn == null && wo.size() == vo.size()) {
                    continue; // as often there as in V's override: the occurrences correspond in order, as on any target
                }
                int[] map = SiteMatch.match(vmeth, vo, wm, wo, 3, false);
                for (int i = 0; i < map.length; i++) {
                    if (own.contains(map[i])) {
                        map[i] = -1;
                    }
                }
                if (a.ordinal() >= 0) {
                    if (a.ordinal() >= vo.size() || map[a.ordinal()] < 0) {
                        return "V's site " + a + " has no counterpart there";
                    }
                    setValue(at, "ordinal", map[a.ordinal()]);
                    continue;
                }
                Set<Integer> got = new HashSet<>();
                for (int j : map) {
                    if (j < 0) {
                        return a + ": " + vo.size() + " site(s) in " + chain.from + ", not all of them found there";
                    }
                    got.add(j);
                }
                if (got.size() == wo.size() - own.size() && own.isEmpty()) {
                    continue;
                }
                if (vo.size() == 1) {
                    setValue(at, "ordinal", map[0]);
                    continue;
                }
                return a + " stands there " + wo.size() + " time(s), " + vo.size() + " of them V's sites";
            }
            return null;
        }

        /** the injector with its selector set to sel and every refmap-keyed string spelled out (a new mixin has no refmap) */
        private AnnotationNode literalCopy(AnnotationNode an, String sel) {
            AnnotationNode c = new AnnotationNode(an.desc);
            c.values = new ArrayList<>();
            for (int i = 0; an.values != null && i + 1 < an.values.size(); i += 2) {
                c.values.add(an.values.get(i));
                c.values.add("method".equals(an.values.get(i)) ? new ArrayList<>(List.of(sel)) : literal(an.values.get(i + 1)));
            }
            return c;
        }

        private Object literal(Object v) {
            if (v instanceof AnnotationNode a) {
                AnnotationNode c = new AnnotationNode(a.desc);
                c.values = new ArrayList<>();
                for (int i = 0; a.values != null && i + 1 < a.values.size(); i += 2) {
                    Object x = a.values.get(i + 1);
                    c.values.add(a.values.get(i));
                    c.values.add("target".equals(a.values.get(i)) && x instanceof String t ? MixinRepair.refmapLookup(table, t) : literal(x));
                }
                return c;
            }
            if (v instanceof List<?> l) {
                List<Object> out = new ArrayList<>();
                for (Object x : l) {
                    out.add(literal(x));
                }
                return out;
            }
            return v;
        }

        private boolean keysCounted;

        /** the numeric pair of the @Inject being planned (retHelper reads it) */
        private Type curNumV, curNumW;

        /**
         * V's class when this mixin's target is (or becomes, by the step's class rename) a facade: the class
         * name the mixin was written for. Null for any other mixin, and for interface mixins.
         */
        private String facadeOrigin() {
            if (target == null || itf) {
                return null;
            }
            if (MixinClassRenames.toLayer(target)) {
                String v = vk(target); // a mixin that followed its class rename onto the facade (Chain.mixinFollow)
                return v.equals(target) ? null : v;
            }
            return MixinClassRenames.toLayer(chain.classRenames.get(target)) && Chain.minecraftOwned(target) ? target : null;
        }

        /**
         * follow+W: an injector of a mixin on V class {@code origin}, which the step renames to a facade, also goes
         * onto the W method holding the same code (wCounterpart) -- the facade sees only converted mods' calls, the W
         * method is what vanilla runs. A copy of the handler in a mixin of that W class, its selector the W method,
         * its anchors unchanged (the bodies are the same instructions); the mod's handler is marked
         * {@link #ALSO_ON_W} with where the copy is (MixinJudge judges both). Refused, and said why: no single W
         * method holds the code, static-ness differs, the handler reads {@code this} or the mixin's other members
         * (which stand for the facade's object and state, not the W class's), its anchors are not all in the W
         * method, or the W class is itself renamed (a class literal would follow the rename).
         */
        private void alsoOnW(MethodNode h, String origin) {
            AnnotationNode an = null;
            for (AnnotationNode a : MixinRepair.annotations(h)) {
                if (MixinRepair.values(a).containsKey("method") && !a.desc.equals(SHADOW) && !a.desc.equals(INVOKER)
                        && !a.desc.equals(ACCESSOR)) {
                    an = a;
                    break;
                }
            }
            if (an == null) {
                return;
            }
            String kind = kind(an);
            String where = "@" + kind + " " + sn + "." + h.name + " -> " + shortName(origin);
            List<String> sels = MixinRepair.strings(MixinRepair.values(an).get("method"));
            if (sels.size() != 1) {
                left.add(where + ": facade-only (follow+W: " + sels.size() + " selectors)");
                return;
            }
            MixinRepair.Sel s = MixinRepair.Sel.parse(MixinRepair.refmapLookup(table, sels.get(0)));
            ClassNode vraw = v(origin);
            if (vraw == null || s.desc() == null || s.owner() != null && !s.owner().equals(target) && !s.owner().equals(origin)) {
                left.add(where + ": facade-only (follow+W: " + chain.from + "'s method not read)");
                return;
            }
            ClassNode vc = vanillaView(vraw);
            MethodNode vm = null;
            for (MethodNode m : vraw.methods) {
                if (m.name.equals(s.name()) && (m.desc.equals(s.desc()) || chain.renamedDesc(m.desc).equals(s.desc()))) {
                    vm = method(vc, m.name, vanillaDesc(m.desc));
                }
            }
            if (vm == null) {
                left.add(where + ": facade-only (follow+W: " + s.name() + s.desc() + " not in " + chain.from + "'s class)");
                return;
            }
            List<String> why = new ArrayList<>();
            String[] cp = wCounterpart(origin, vm, why);
            if (cp == null) {
                left.add(where + "." + vm.name + ": facade-only (follow+W: " + why.get(0) + ")");
                return;
            }
            ClassNode yc = w(cp[0]);
            MethodNode wm = yc == null ? null : method(yc, cp[1], cp[2]);
            AnnotationNode moved = literalCopy(an, cp[1] + cp[2]);
            String refuse = null;
            if (wm == null) {
                refuse = "W's method not read";
            } else if (((wm.access ^ vm.access) & Opcodes.ACC_STATIC) != 0) {
                refuse = "static-ness differs";
            } else if ((yc.access & Opcodes.ACC_INTERFACE) != 0) {
                refuse = shortName(cp[0]) + " is an interface";
            } else if (chain.classRenames.containsKey(cp[0])) {
                refuse = shortName(cp[0]) + " is itself renamed by the step";
            } else if ((h.access & Opcodes.ACC_STATIC) == 0 && !MixinRepair.touchesThisFree(h)) {
                refuse = "the handler reads this, which on the facade is the facade's object";
            } else if (usesMixinMembers(h)) {
                refuse = "the handler uses the mixin's other members (the facade's state)";
            } else {
                refuse = selfAnchors(moved, vm, origin, wm);
                List<MixinRepair.Anchor> anchors = refuse != null ? null : wcode(chain).anchors(moved, MixinRepair.values(moved), null);
                if (refuse == null && anchors == null) {
                    refuse = "its anchors are not evaluable";
                } else if (refuse == null && !anchors.isEmpty() && !MixinRepair.anchorsIn(wm, yc, anchors)) {
                    refuse = "its anchors " + anchors + " are not in " + shortName(cp[0]) + "." + cp[1];
                }
            }
            if (refuse != null) {
                left.add(where + "." + vm.name + ": facade-only (follow+W onto " + shortName(cp[0]) + "." + cp[1] + cp[2] + ": " + refuse + ")");
                return;
            }
            ClassNode gc = alsoMixin(cp[0]);
            String gname = shortName(mx.name).replaceAll("[^A-Za-z0-9_]", "_") + "$" + h.name;
            for (MethodNode m : gc.methods) {
                if (m.name.equals(gname)) {
                    gname = gname + "_";
                }
            }
            MethodNode g = new MethodNode(h.access, gname, h.desc, h.signature, exceptions(h));
            h.accept(g);
            g.name = gname;
            g.access = (h.access & Opcodes.ACC_STATIC) | Opcodes.ACC_PRIVATE;
            List<AnnotationNode> vis = new ArrayList<>();
            if (g.visibleAnnotations != null) {
                for (AnnotationNode a : g.visibleAnnotations) {
                    vis.add(a.desc.equals(an.desc) ? moved : a);
                }
            }
            if (!vis.contains(moved)) {
                vis.add(moved);
            }
            g.visibleAnnotations = vis;
            if (g.invisibleAnnotations != null) {
                String ad = an.desc;
                g.invisibleAnnotations.removeIf(a -> a.desc.equals(ad) || a.desc.startsWith("Ltop/dext/centurybridge/"));
                if (g.invisibleAnnotations.isEmpty()) {
                    g.invisibleAnnotations = null;
                }
            }
            gc.methods.add(g);
            AnnotationNode mark = new AnnotationNode(ALSO_ON_W);
            mark.values = new ArrayList<>(List.of("mixin", gc.name, "handler", gname));
            h.invisibleAnnotations = h.invisibleAnnotations == null ? new ArrayList<>() : h.invisibleAnnotations;
            h.invisibleAnnotations.add(mark);
            changed = true;
            injected("@" + kind + " " + sn + "." + h.name, where + "." + vm.name + vm.desc + " => also " + shortName(cp[0]) + "."
                + cp[1] + cp[2] + " (follow+W: the step renames " + shortName(origin) + " to a facade, which only converted mods call;"
                + " W vanilla runs the same code there, " + why.get(0) + ")");
        }

        /**
         * The follow+W copy's anchors on the facade class's own members (FastColor$ARGB32.color calling its own
         * alpha(..)): in W they are the counterpart class's own members, which the identical-instruction test knew
         * only by kind. Each moves to the W instruction at the same place among the significant instructions (the
         * bodies are the same sequence); a pinned one to that occurrence, an unpinned one only when every V site
         * lands on the same W member and W holds it exactly there. Null when done, else why not.
         */
        private String selfAnchors(AnnotationNode moved, MethodNode vm, String origin, MethodNode wm) {
            List<AbstractInsnNode> vs = new ArrayList<>(), ws = new ArrayList<>();
            for (AbstractInsnNode in : vm.instructions) {
                if (SiteMatch.sigOf(in) != null) {
                    vs.add(in);
                }
            }
            for (AbstractInsnNode in : wm.instructions) {
                if (SiteMatch.sigOf(in) != null) {
                    ws.add(in);
                }
            }
            for (AnnotationNode at : atNodes(moved)) {
                MixinRepair.Anchor a = MixinRepair.parseAt(MixinRepair.values(at), null);
                if (a == null || a.owner() == null || !(a.kind().equals("INVOKE") || a.kind().equals("FIELD"))
                        || !a.owner().equals(target) && !a.owner().equals(origin)) {
                    continue;
                }
                if (vs.size() != ws.size()) {
                    return "its anchor " + a + " names the facade's own member, and the bodies do not line up";
                }
                List<Integer> sites = new ArrayList<>();
                for (int i = 0; i < vs.size(); i++) {
                    AbstractInsnNode in = vs.get(i);
                    boolean hit = in instanceof MethodInsnNode mi && mi.owner.equals(origin) && mi.name.equals(a.name())
                        && (a.desc() == null || chain.renamedDesc(mi.desc).equals(a.desc()) || mi.desc.equals(a.desc()))
                        || in instanceof FieldInsnNode fi && fi.owner.equals(origin) && fi.name.equals(a.name())
                        && (a.desc() == null || chain.renamedDesc(fi.desc).equals(a.desc()) || fi.desc.equals(a.desc()));
                    if (hit) {
                        sites.add(i);
                    }
                }
                if (sites.isEmpty() || a.ordinal() >= sites.size()) {
                    return "its anchor " + a + " is not in " + chain.from + "'s body";
                }
                List<Integer> mine = a.ordinal() >= 0 ? List.of(sites.get(a.ordinal())) : sites;
                MixinRepair.Anchor na = anchorOf(ws.get(mine.get(0)));
                for (int i : mine) {
                    if (!MixinRepair.matchesAnchor(ws.get(i), na)) {
                        return "its anchor " + a + " stands for different W members";
                    }
                }
                List<AbstractInsnNode> wo = SiteMatch.occurrences(wm, in -> MixinRepair.matchesAnchor(in, na));
                String nt = "L" + na.owner() + ";" + na.name() + (na.kind().equals("FIELD") ? ":" : "") + na.desc();
                setValue(at, "target", nt);
                if (mine.size() == 1 && (a.ordinal() >= 0 || wo.size() > 1)) {
                    setValue(at, "ordinal", wo.indexOf(ws.get(mine.get(0))));
                } else if (mine.size() != wo.size()) {
                    return "its anchor " + a + ": " + mine.size() + " V sites, " + wo.size() + " W occurrences of " + na;
                }
            }
            return null;
        }
        private boolean usesMixinMembers(MethodNode h) {
            if (h.instructions == null) {
                return false;
            }
            for (AbstractInsnNode in : h.instructions) {
                if (in instanceof MethodInsnNode mi && mi.owner.equals(mx.name) || in instanceof FieldInsnNode fi && fi.owner.equals(mx.name)) {
                    return true;
                }
            }
            return false;
        }

        /** the mixin of a W class that follow+W copies of this config section go into (one per class) */
        private ClassNode alsoMixin(String decl) {
            String name = (curPkg.isEmpty() ? "" : curPkg + "/") + "cb$alsow$" + step + "$" + curKey + "$"
                + decl.replace('/', '_').replace('$', '_');
            ClassNode gc = restorers.get(name);
            if (gc == null) {
                gc = new ClassNode();
                gc.version = mx.version;
                gc.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
                gc.name = name;
                gc.superName = "java/lang/Object";
                AnnotationNode mixin = new AnnotationNode(MIXIN);
                mixin.values = new ArrayList<>(List.of("value", List.of(Type.getObjectType(decl))));
                gc.invisibleAnnotations = new ArrayList<>(List.of(mixin, new AnnotationNode("Lcb/side;")));
                gc.invisibleAnnotations.get(1).values = new ArrayList<>(List.of("key", curKey));
                restorers.put(name, gc);
            }
            return gc;
        }

        /** the mixin of a superclass that guarded injectors of this config section go into (one per class) */
        private ClassNode guardMixin(String decl) {
            String name = (curPkg.isEmpty() ? "" : curPkg + "/") + "cb$guard$" + step + "$" + curKey + "$"
                + decl.replace('/', '_').replace('$', '_');
            ClassNode gc = restorers.get(name);
            if (gc == null) {
                gc = new ClassNode();
                gc.version = mx.version;
                gc.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
                gc.name = name;
                gc.superName = "java/lang/Object";
                AnnotationNode mixin = new AnnotationNode(MIXIN);
                mixin.values = new ArrayList<>(List.of("value", List.of(Type.getObjectType(decl))));
                gc.invisibleAnnotations = new ArrayList<>(List.of(mixin, new AnnotationNode("Lcb/side;")));
                gc.invisibleAnnotations.get(1).values = new ArrayList<>(List.of("key", curKey));
                restorers.put(name, gc);
            }
            return gc;
        }

        /**
         * V's class overrode the method, W's inherits it (the override was dropped): the injector's
         * target method is put back as an override that only calls the inherited one -- Sub.m runs
         * exactly when it did, and does what W does -- merged by this mixin, marked {@link #RESTORED}
         * (repair and the judge take it as the target). Only for an instance method W declares on a
         * superclass, not private, final, static or abstract there, overridable from the target's
         * package; one per mixin and member. Anchors are then judged in it as in any target.
         */
        private MethodNode restoreOverride(MethodNode vmeth, ClassNode wt, List<String> why) {
            if (itf || (vmeth.access & (Opcodes.ACC_STATIC | Opcodes.ACC_PRIVATE | Opcodes.ACC_ABSTRACT)) != 0
                    || vmeth.name.startsWith("<")) {
                return null;
            }
            for (MethodNode m : mx.methods) {
                if (m.name.equals(vmeth.name) && m.desc.equals(vmeth.desc)) {
                    return null; // the mixin itself declares it (an overwrite / its own method): not ours to add
                }
            }
            ClassNode decl = null;
            MethodNode inherited = null;
            for (String c = wt.superName; c != null && inherited == null; ) {
                ClassNode cn = w(c);
                if (cn == null) {
                    return null;
                }
                MethodNode m = method(cn, vmeth.name, vmeth.desc);
                if (m != null) {
                    decl = cn;
                    inherited = m;
                }
                c = cn.superName;
            }
            if (inherited == null) {
                return null;
            }
            int acc = inherited.access;
            if ((acc & (Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_ABSTRACT)) != 0) {
                why.add(shortName(decl.name) + "." + vmeth.name + vmeth.desc + ", which " + shortName(wt.name) + " now inherits, cannot be overridden");
                return null;
            }
            boolean pkgPrivate = (acc & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED)) == 0;
            if (pkgPrivate && !pkg(decl.name).equals(pkg(wt.name))) {
                why.add(shortName(decl.name) + "." + vmeth.name + vmeth.desc + " is package-private in another package");
                return null;
            }
            // super.m(..) as javac writes it: through the direct superclass, the JVM finds the declaration above
            ClassNode rc = restorer(target, wt.superName);
            for (MethodNode m : rc.methods) {
                if (m.name.equals(vmeth.name) && m.desc.equals(vmeth.desc)) {
                    return m;
                }
            }
            MethodNode r = new MethodNode(acc & (Opcodes.ACC_PUBLIC | Opcodes.ACC_PROTECTED | Opcodes.ACC_VARARGS),
                vmeth.name, vmeth.desc, null, inherited.exceptions == null ? null : inherited.exceptions.toArray(new String[0]));
            r.visitAnnotation(RESTORED, false);
            r.visitCode();
            r.visitVarInsn(Opcodes.ALOAD, 0);
            int slot = 1;
            for (Type t : Type.getArgumentTypes(vmeth.desc)) {
                r.visitVarInsn(t.getOpcode(Opcodes.ILOAD), slot);
                slot += t.getSize();
            }
            r.visitMethodInsn(Opcodes.INVOKESPECIAL, wt.superName, vmeth.name, vmeth.desc, false);
            r.visitInsn(Type.getReturnType(vmeth.desc).getOpcode(Opcodes.IRETURN));
            r.visitMaxs(slot + 1, slot);
            r.visitEnd();
            rc.methods.add(r);
            return r;
        }

        /**
         * The jar's restoring mixin for a target on the current config's side: abstract, extending the
         * target's W superclass (its super calls are then Mixin's own super calls), with
         * priority 1 -- merged before any mod's mixins, so whichever converted mod restores a member
         * first, every mod's injectors may target it (Mixin refuses an injector a method merged by a mixin
         * of equal or higher priority); a second restoring of the same member is skipped by Mixin with
         * an overwrite-conflict warning.
         */
        private ClassNode restorer(String target, String sup) {
            String name = (curPkg.isEmpty() ? "" : curPkg + "/") + "cb$restore$" + step + "$" + curKey + "$"
                + target.replace('/', '_').replace('$', '_');
            ClassNode rc = restorers.get(name);
            if (rc == null) {
                rc = new ClassNode();
                rc.version = mx.version;
                rc.access = Opcodes.ACC_PUBLIC | Opcodes.ACC_ABSTRACT | Opcodes.ACC_SUPER;
                rc.name = name;
                rc.superName = sup;
                AnnotationNode mixin = new AnnotationNode(MIXIN);
                mixin.values = new ArrayList<>(List.of("value", List.of(Type.getObjectType(target)), "priority", 1));
                rc.invisibleAnnotations = new ArrayList<>(List.of(mixin, new AnnotationNode("Lcb/side;")));
                rc.invisibleAnnotations.get(1).values = new ArrayList<>(List.of("key", curKey));
                restorers.put(name, rc);
            }
            return rc;
        }
    }

    // ------------------------------------------------------------------ injector fates

    /**
     * What became of a V method an injector selects, as the step's wiring says: W's name and
     * descriptor, and for each V argument the W argument it comes from ({@code from}), through
     * {@code wrap} (W type -> V type) or a type test ({@code narrow}) where the type differs.
     * {@code argsKnown} is false when the wiring gives no such map (an adapter-bridged hook).
     */
    record Fate(String name, String desc, int[] from, String[][] wrap, boolean[] narrow, boolean argsKnown, String how) {
        boolean converts() {
            for (int i = 0; wrap != null && i < wrap.length; i++) {
                if (wrap[i] != null || narrow[i]) {
                    return true;
                }
            }
            return false;
        }
    }

    record Plan(boolean synth, boolean capture, String cb, int ci, RetConv rc, boolean untouched) {
        Plan(boolean synth, boolean capture, String cb, int ci) {
            this(synth, capture, cb, ci, null, false);
        }

        /** the W-shaped handler takes W's arguments: the mod's handler captured V's, or a converter needs some */
        boolean wArgs() {
            return capture || rc != null && rc.with().length > 0;
        }
    }

    /**
     * How the answer of the V method an @Inject was written for and the answer of the W method it now targets
     * turn into each other: toV / toW statics (null with numeric: Java's conversion of numeric primitives),
     * each taking the answer and then the W target's arguments at indices with.
     */
    record RetConv(String[] toV, String[] toW, int[] with, boolean numeric, String how) {}

    /** marks an @Inject handler whose target's return type changed with no converter: it runs, the answer untouched */
    static final String RETURN_UNTOUCHED = "Ltop/dext/centurybridge/ReturnUntouched;";

    record Selection(MethodNode handler, AnnotationNode an, List<MethodNode> v, List<MethodNode> w) {}

    record AtFate(String owner, String name, String desc, String how) {}

    /**
     * Override hook (applicable to the mixin's target class) first: it describes the method itself
     * as overriders see it, argument map included. One that bridges through an adapter carries only
     * handlers that take none of the target's arguments, and only when the method kept its name
     * (an adapter into another method can stand for something wider). Then a rename, a call reshape
     * (the inverse of its keep ops), a constructor reshape or adapter.
     */
    private Fate selectorFate(String owner, String name, String vdesc, List<String> why) {
        Type[] va = Type.getArgumentTypes(vdesc);
        // an area agent's judgment of where the V code lives on W comes before anything inferred
        Chain.MixinTarget mt = chain.mixinTargets.get(owner + "." + name + vdesc);
        if (mt != null) {
            boolean known = mt.args() != null && mt.args().length == va.length;
            int[] from = known ? mt.args().clone() : null;
            for (int i = 0; known && i < from.length; i++) {
                known = from[i] >= 0;
            }
            return new Fate(mt.name(), mt.desc(), known ? from : null, new String[va.length][], new boolean[va.length], known,
                "mixinTargets (" + mt.basis() + ")");
        }
        for (Chain.OverrideHook h : chain.overrideHooksForOld(name, vdesc)) {
            if (h.onlyUnder != null && !chain.sourceSubtype(List.of(owner), h.onlyUnder)
                    || h.notUnder != null && chain.sourceSubtype(List.of(owner), h.notUnder)) {
                continue;
            }
            if (h.bridgeName != null) {
                if (!h.oldName.equals(h.newName)) {
                    why.add("its override hook reaches " + h.newName + h.newDesc + ", another method, through an adapter");
                    return null;
                }
                return new Fate(h.newName, h.newDesc, null, null, null, false, "override hook through " + shortName(h.adapter));
            }
            Type[] na = Type.getArgumentTypes(h.newDesc);
            int[] map = h.args;
            if (map == null) {
                if (na.length != va.length) {
                    why.add("its override hook changes the arity without an argument map");
                    return null;
                }
                map = identity(va.length);
            }
            int[] from = new int[va.length];
            String[][] wrap = new String[va.length][];
            boolean[] narrow = new boolean[va.length];
            boolean known = map.length == va.length;
            for (int i = 0; known && i < va.length; i++) {
                int j = map[i];
                from[i] = j;
                if (j < 0 || j >= na.length) {
                    known = false;
                } else if (!na[j].equals(va[i]) && !wAssignable(na[j], va[i])) {
                    if (h.wrapName != null && Arrays.equals(Type.getArgumentTypes(h.wrapDesc), new Type[] {na[j]})
                            && Type.getReturnType(h.wrapDesc).equals(va[i])) {
                        wrap[i] = new String[] {h.wrapOwner, h.wrapName, h.wrapDesc};
                    } else if (h.narrow && va[i].getSort() >= Type.ARRAY) {
                        narrow[i] = true;
                    } else {
                        known = false;
                    }
                }
            }
            return new Fate(h.newName, h.newDesc, from, wrap, narrow, known,
                "override hook" + (h.wrapName != null ? " (wraps " + h.wrapName + ")" : h.narrow ? " (narrows)" : ""));
        }
        String nd = name + vdesc;
        String rh = chain.instanceRedirectHome(chain.methodRenames, owner, nd);
        if (rh != null) {
            return new Fate(chain.methodRenames.get(rh + "." + nd), vdesc, identity(va.length), new String[va.length][],
                new boolean[va.length], true, "renamed");
        }
        String ch = chain.instanceRedirectHome(chain.callReshapeIndex, owner, nd);
        if (ch != null) {
            Chain.CallReshape r = chain.callReshapes.get(ch + "." + nd);
            return inverse(name, r.args, va, r.newDesc, "call reshape");
        }
        if (name.equals("<init>")) {
            Chain.CtorReshape cr = chain.superCtorReshapes.get(owner + ".<init>" + vdesc);
            if (cr != null) {
                return inverse(name, cr, va, cr.newDesc, "constructor reshape");
            }
            String[] ad = chain.superCtorAdapts.get(owner + ".<init>" + vdesc);
            if (ad != null) {
                // the adapter supplies a new tail argument (no parameters) or converts the old tail
                Type[] na = Type.getArgumentTypes(ad[0]);
                boolean supplies = Type.getArgumentTypes(ad[2]).length == 0 && na.length == va.length + 1;
                int[] from = new int[va.length];
                boolean known = supplies;
                for (int i = 0; i < va.length; i++) {
                    from[i] = supplies && na[i].equals(va[i]) ? i : -1;
                    known &= from[i] >= 0;
                }
                return new Fate(name, ad[0], from, new String[va.length][], new boolean[va.length], known, "constructor adapter");
            }
        }
        return null;
    }

    /**
     * A V method W no longer declares and the step's wiring says nothing of: the W method of the same
     * class that holds its code now (1.20.5 made every packet's write(FriendlyByteBuf) a private
     * write(RegistryFriendlyByteBuf) its stream codec calls, and split each block's use() into
     * useItemOn / useWithoutItem) -- the vanilla code an injector into V's method hooked runs there.
     * Candidates are the methods W declares and V does not (by name and descriptor), not synthetic, of
     * the same static-ness, a constructor for a constructor. Evidence: their significant instructions
     * (SiteMatch.sig) agree with V's body at least 0.75 (2 x common subsequence / both lengths) and
     * every other candidate trails by 0.15 or more; anything less is reported, never picked. V's
     * arguments are W's where each has exactly one W argument of its type (or a W subtype of it) and no
     * two share one: otherwise the arguments are unknown and only a handler taking none follows (plan);
     * a changed return type is plan's to judge as well.
     */
    private Fate bodyFate(ClassNode vt, ClassNode wt, MethodNode vm, List<String> why) {
        if ((vm.access & Opcodes.ACC_SYNTHETIC) != 0 || vm.instructions == null || vm.instructions.size() == 0) {
            return null;
        }
        Set<String> vkeys = new HashSet<>();
        for (MethodNode m : vt.methods) {
            vkeys.add(m.name + m.desc);
        }
        Type[] va = Type.getArgumentTypes(vm.desc);
        List<String> vs = SiteMatch.sigSelf(vm, vt.name);
        MethodNode best = null;
        double bs = -1;
        double second = -1;
        List<MethodNode> cands = new ArrayList<>();
        for (MethodNode m : wt.methods) {
            if (vkeys.contains(m.name + m.desc) || (m.access & Opcodes.ACC_SYNTHETIC) != 0
                    || ((m.access ^ vm.access) & Opcodes.ACC_STATIC) != 0 || m.name.equals("<init>") != vm.name.equals("<init>")
                    || m.name.equals("<clinit>")) {
                continue;
            }
            cands.add(m);
            double sim = SiteMatch.similarity(vs, SiteMatch.sigSelf(m, wt.name));
            if (sim > bs) {
                second = bs;
                bs = sim;
                best = m;
            } else if (sim > second) {
                second = sim;
            }
        }
        if (best == null) {
            return null;
        }
        if (vs.size() < 6 && bs < 1.0) {
            // a getter / delegate of a few instructions looks like any other: no evidence short of identity
            why.add(String.format(Locale.ROOT, "no wiring; the closest W method by code is %s%s at %.2f, but a body of %d significant "
                + "instructions is no evidence short of identity", best.name, best.desc, bs, vs.size()));
            return null;
        }
        if (bs < 0.75 || bs - second < 0.15) {
            why.add(String.format(Locale.ROOT, "no wiring; the closest W method by code is %s%s at %.2f (next %.2f), short of evidence",
                best.name, best.desc, bs, Math.max(second, 0)));
            return null;
        }
        String split = split(vs, best, cands, wt.name);
        if (split != null) {
            why.add("no wiring; its code is split across W methods: " + best.name + best.desc + " and " + split
                + " (an injector cannot follow it into one of them)");
            return null;
        }
        Type[] wa = Type.getArgumentTypes(best.desc);
        int[] from = new int[va.length];
        boolean known = true;
        Set<Integer> used = new HashSet<>();
        for (int i = 0; i < va.length; i++) {
            from[i] = -1;
            for (int j = 0; j < wa.length; j++) {
                if (wa[j].equals(va[i]) || wAssignable(wa[j], va[i])) {
                    from[i] = from[i] == -1 ? j : -2;
                }
            }
            known &= from[i] >= 0 && used.add(from[i]);
        }
        return new Fate(best.name, best.desc, known ? from : null, new String[va.length][], new boolean[va.length], known,
            String.format(Locale.ROOT, "W keeps its code there (similarity %.2f, next %.2f)%s", bs, Math.max(second, 0),
                known ? "" : "; its arguments are not V's one for one"));
    }

    /**
     * Another W method holding a share of V's code the best match does not (1.20.5 split Block.use into
     * useItemOn and useWithoutItem): by the alignment of V's significant instructions against each body,
     * a candidate that aligns at least a sixth of V's instructions the best match leaves unaligned,
     * with at least 4 of them, and is itself mostly V's code (60% of its own instructions aligned).
     * Null when no candidate does.
     */
    private static String split(List<String> vs, MethodNode best, List<MethodNode> cands, String owner) {
        if (vs.size() < 4) {
            return null;
        }
        int[] b = SiteMatch.align(vs, SiteMatch.sigSelf(best, owner));
        if (b == null) {
            return null;
        }
        for (MethodNode c : cands) {
            if (c == best) {
                continue;
            }
            List<String> cs = SiteMatch.sigSelf(c, owner);
            int[] a = SiteMatch.align(vs, cs);
            if (a == null || cs.isEmpty()) {
                continue;
            }
            int extra = 0;
            int own = 0;
            for (int i = 0; i < vs.size(); i++) {
                if (a[i] >= 0) {
                    own++;
                    if (b[i] < 0) {
                        extra++;
                    }
                }
            }
            if (extra >= 4 && extra * 6 >= vs.size() && own * 10 >= cs.size() * 6) {
                return c.name + c.desc;
            }
        }
        return null;
    }

    /** W argument j of a reshape's "keep" ops is V argument i; drops, conversions and supplies give none */
    private static Fate inverse(String name, Chain.CtorReshape r, Type[] va, String newDesc, String how) {
        Type[] na = Type.getArgumentTypes(newDesc);
        int[] from = new int[va.length];
        Arrays.fill(from, -1);
        boolean[] narrow = new boolean[va.length];
        int j = 0;
        for (Chain.ReshapeOp op : r.ops) {
            if (op.drop) {
                continue;
            }
            if (op.param == null && op.owner == null && op.from.length == 1 && j < na.length && from[op.from[0]] < 0) {
                int i = op.from[0];
                if (na[j].equals(va[i])) {
                    from[i] = j;
                } else if (va[i].getSort() >= Type.ARRAY && na[j].getSort() >= Type.ARRAY) {
                    from[i] = j; // "keep:N <wider type>": W's callers may pass any instance of it
                    narrow[i] = true;
                }
            }
            j++;
        }
        boolean known = true;
        for (int f : from) {
            known &= f >= 0;
        }
        return new Fate(name, newDesc, from, new String[va.length][], narrow, known, how);
    }

    /** what the wiring says stands where a V call or field access stood: rename, call reshape, override hook */
    private AtFate atFate(MixinRepair.Anchor a) {
        if (a.kind().equals("FIELD")) {
            for (String o : chain.selfAndSupers(a.owner())) {
                String rn = chain.fieldRenames.get(o + "." + a.name() + ":" + a.desc());
                if (rn != null) {
                    return new AtFate(a.owner(), rn, a.desc(), "field renamed");
                }
            }
            return null;
        }
        String nd = a.name() + a.desc();
        String rh = chain.instanceRedirectHome(chain.methodRenames, a.owner(), nd);
        if (rh != null) {
            return new AtFate(a.owner(), chain.methodRenames.get(rh + "." + nd), a.desc(), "renamed");
        }
        String ch = chain.instanceRedirectHome(chain.callReshapeIndex, a.owner(), nd);
        if (ch != null) {
            return new AtFate(a.owner(), a.name(), chain.callReshapes.get(ch + "." + nd).newDesc, "call reshape");
        }
        for (Chain.OverrideHook h : chain.overrideHooksForOld(a.name(), a.desc())) {
            if (h.onlyUnder != null && !chain.sourceSubtype(List.of(a.owner()), h.onlyUnder)
                    && !chain.sourceSubtype(List.of(h.onlyUnder), a.owner())
                    || h.notUnder != null && chain.sourceSubtype(List.of(a.owner()), h.notUnder)) {
                continue;
            }
            return new AtFate(a.owner(), h.newName, h.newDesc, "override hook");
        }
        return redirectedCall(a);
    }

    private java.util.zip.ZipFile layerZip;
    private final Map<String, ClassNode> layerClasses = new HashMap<>();

    /**
     * A V call the step redirects (static / instance redirect) to a layer body that makes one game call,
     * the rest of the body at most reading record components of that call's answer (1.21.2:
     * getShaderFogColor() is getShaderFog().color()): vanilla W calls that one game call where V called
     * the redirected member, so an @At on it follows there. Needs the layer's bytecode (Chain.layerJar).
     */
    private AtFate redirectedCall(MixinRepair.Anchor a) {
        if (chain.layerJar == null || !a.kind().equals("INVOKE") || chain.bridgeCode != null) {
            // (not at the wall: the layer there is in 26.1's names, the anchors in the analysis namespace)
            return null;
        }
        String nd = a.name() + a.desc();
        String val = chain.redirectAnywhere(chain.staticRedirects, a.owner(), nd);
        boolean instance = false;
        if (val == null) {
            val = chain.redirectAnywhere(chain.instanceRedirects, a.owner(), nd);
            instance = val != null;
        }
        if (val == null) {
            return null;
        }
        String cls = val.contains("#") ? val.substring(0, val.indexOf('#')) : val;
        String name = val.contains("#") ? val.substring(val.indexOf('#') + 1) : a.name();
        if (name.contains("(")) {
            name = name.substring(0, name.indexOf('('));
        }
        ClassNode cn = layerClass(cls);
        if (cn == null) {
            return null;
        }
        String want = instance ? "(L" + a.owner() + ";" + a.desc().substring(1) : a.desc();
        MethodNode body = null;
        for (MethodNode m : cn.methods) {
            if (m.name.equals(name) && (m.desc.equals(want) || m.desc.equals(a.desc()))) {
                body = m;
            }
        }
        if (body == null || body.instructions == null) {
            return null;
        }
        MethodInsnNode first = null;
        for (AbstractInsnNode in : body.instructions) {
            if (in instanceof InvokeDynamicInsnNode) {
                return null;
            }
            if (!(in instanceof MethodInsnNode mi)) {
                continue;
            }
            if (first == null) {
                if (!Chain.minecraftOwned(mi.owner)) {
                    return null;
                }
                first = mi;
                continue;
            }
            // only reads of the answer's components: no-argument calls on the class the first call returns
            Type r = Type.getReturnType(first.desc);
            if (r.getSort() != Type.OBJECT || !mi.owner.equals(r.getInternalName()) || Type.getArgumentTypes(mi.desc).length != 0) {
                return null;
            }
        }
        return first == null ? null : new AtFate(first.owner, first.name, first.desc, "the redirect body's one game call");
    }

    private ClassNode layerClass(String cls) {
        return layerClasses.computeIfAbsent(cls, c -> {
            try {
                if (layerZip == null) {
                    layerZip = new java.util.zip.ZipFile(chain.layerJar.toFile());
                }
                java.util.zip.ZipEntry e = layerZip.getEntry(c + ".class");
                if (e == null) {
                    return null;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(layerZip.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                return cn;
            } catch (java.io.IOException ex) {
                return null;
            }
        });
    }

    private static int count(List<MethodNode> ms, MixinRepair.Anchor a) {
        int n = 0;
        for (MethodNode m : ms) {
            if (m.instructions != null) {
                for (AbstractInsnNode in : m.instructions) {
                    if (MixinRepair.matchesAnchor(in, a)) {
                        n++;
                    }
                }
            }
        }
        return n;
    }

    /** the injector's @At nodes: "at" and its slice's from / to */
    private static List<AnnotationNode> atNodes(AnnotationNode an) {
        List<AnnotationNode> out = new ArrayList<>();
        for (int i = 0; an.values != null && i + 1 < an.values.size(); i += 2) {
            Object k = an.values.get(i);
            Object v = an.values.get(i + 1);
            if ("at".equals(k)) {
                collectAts(v, out);
            } else if ("slice".equals(k)) {
                for (Object sl : v instanceof List<?> l ? l : List.of(v)) {
                    if (sl instanceof AnnotationNode s && s.values != null) {
                        for (int j = 0; j + 1 < s.values.size(); j += 2) {
                            if ("from".equals(s.values.get(j)) || "to".equals(s.values.get(j))) {
                                collectAts(s.values.get(j + 1), out);
                            }
                        }
                    }
                }
            }
        }
        return out;
    }

    private static void collectAts(Object v, List<AnnotationNode> out) {
        if (v instanceof AnnotationNode a) {
            out.add(a);
        } else if (v instanceof List<?> l) {
            for (Object o : l) {
                if (o instanceof AnnotationNode a) {
                    out.add(a);
                }
            }
        }
    }

    private static int[] identity(int n) {
        int[] m = new int[n];
        for (int i = 0; i < n; i++) {
            m[i] = i;
        }
        return m;
    }

    private static boolean coerced(MethodNode h, int i) {
        return h.invisibleParameterAnnotations != null && i < h.invisibleParameterAnnotations.length
            && h.invisibleParameterAnnotations[i] != null && find(h.invisibleParameterAnnotations[i], COERCE) != null;
    }

    /** handler arguments carrying MixinExtras sugar (@Local, @Share...) */
    private static int sugarCount(MethodNode h) {
        int n = 0;
        int params = Type.getArgumentTypes(h.desc).length;
        for (int i = 0; i < params; i++) {
            n += sugarAt(h, i, "") ? 1 : 0;
        }
        return n;
    }

    private static boolean hasSugar(MethodNode h, String suffix) {
        int params = Type.getArgumentTypes(h.desc).length;
        for (int i = 0; i < params; i++) {
            if (sugarAt(h, i, suffix)) {
                return true;
            }
        }
        return false;
    }

    private static boolean sugarAt(MethodNode h, int i, String suffix) {
        return sugarIn(h.visibleParameterAnnotations, i, suffix) || sugarIn(h.invisibleParameterAnnotations, i, suffix);
    }

    private static boolean sugarIn(List<AnnotationNode>[] pa, int i, String suffix) {
        if (pa == null || i >= pa.length || pa[i] == null) {
            return false;
        }
        for (AnnotationNode a : pa[i]) {
            if (a.desc.startsWith(SUGAR) && a.desc.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }

    /** does an @Inject handler take parameters after its CallbackInfo (captured locals) */
    private static boolean capturesLocals(MethodNode h) {
        Type[] hp = Type.getArgumentTypes(h.desc);
        for (int i = 0; i < hp.length; i++) {
            if (hp[i].getDescriptor().equals(CI) || hp[i].getDescriptor().equals(CIR)) {
                return i + 1 < hp.length;
            }
        }
        return false;
    }

    /** does the handler read or set its CallbackInfoReturnable's value */
    private static boolean touchesReturnValue(MethodNode h) {
        if (h.instructions == null) {
            return false;
        }
        String cir = CIR.substring(1, CIR.length() - 1);
        for (AbstractInsnNode in : h.instructions) {
            if (in instanceof MethodInsnNode mi && mi.owner.equals(cir)
                    && (mi.name.startsWith("getReturnValue") || mi.name.equals("setReturnValue"))) {
                return true;
            }
        }
        return false;
    }

    private static boolean contains(byte[] hay, byte[] needle) {
        outer:
        for (int i = 0; i + needle.length <= hay.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (hay[i + j] != needle[j]) {
                    continue outer;
                }
            }
            return true;
        }
        return false;
    }

    /** load a method's parameters onto the stack, from slot {@code slot} on */
    private static void loadArgs(InsnList in, String desc, int slot) {
        for (Type t : Type.getArgumentTypes(desc)) {
            in.add(new VarInsnNode(t.getOpcode(Opcodes.ILOAD), slot));
            slot += t.getSize();
        }
    }

    private static String[] exceptions(MethodNode m) {
        return m.exceptions == null ? null : m.exceptions.toArray(new String[0]);
    }

    // ------------------------------------------------------------------ small helpers

    static String shortName(String internal) {
        int i = internal.lastIndexOf('/');
        return i < 0 ? internal : internal.substring(i + 1);
    }

    private static String pkgOf(String internal) {
        int i = internal.lastIndexOf('/');
        return i < 0 ? "" : internal.substring(0, i);
    }

    static MethodNode method(ClassNode c, String name, String desc) {
        for (MethodNode m : c.methods) {
            if (m.name.equals(name) && m.desc.equals(desc)) {
                return m;
            }
        }
        return null;
    }

    static FieldNode field(ClassNode c, String name, String desc) {
        for (FieldNode f : c.fields) {
            if (f.name.equals(name) && f.desc.equals(desc)) {
                return f;
            }
        }
        return null;
    }

    private static AnnotationNode find(List<AnnotationNode> ans, String desc) {
        for (AnnotationNode a : ans) {
            if (a.desc.equals(desc)) {
                return a;
            }
        }
        return null;
    }

    private static boolean has(List<AnnotationNode> a, List<AnnotationNode> b, String desc) {
        return (a != null && find(a, desc) != null) || (b != null && find(b, desc) != null);
    }

    private static String str(AnnotationNode an, String key, String def) {
        return MixinRepair.values(an).get(key) instanceof String s ? s : def;
    }

    private static boolean bool(AnnotationNode an, String key) {
        return Boolean.TRUE.equals(MixinRepair.values(an).get(key));
    }

    /** set (or add) one annotation value */
    private static void setValue(AnnotationNode an, String key, Object v) {
        if (an.values == null) {
            an.values = new ArrayList<>();
        }
        for (int i = 0; i + 1 < an.values.size(); i += 2) {
            if (key.equals(an.values.get(i))) {
                an.values.set(i + 1, v);
                return;
            }
        }
        an.values.add(key);
        an.values.add(v);
    }

    private static void removeAnnotation(MethodNode m, AnnotationNode an) {
        if (m.visibleAnnotations != null) {
            m.visibleAnnotations.remove(an);
        }
        if (m.invisibleAnnotations != null) {
            m.invisibleAnnotations.remove(an);
        }
    }

    /** the injector's simple name: Inject, Redirect, ModifyArg, WrapOperation... */
    private static String kind(AnnotationNode an) {
        return an.desc.substring(an.desc.lastIndexOf('/') + 1, an.desc.length() - 1);
    }

    private static int argSlots(String desc, boolean isStatic) {
        int s = isStatic ? 0 : 1;
        for (Type t : Type.getArgumentTypes(desc)) {
            s += t.getSize();
        }
        return s;
    }

    /** "Lowner;name(desc)" / "name(desc)" / "owner.name(desc)" with another name or descriptor, prefix kept */
    private static String respell(String mapped, String name, String desc) {
        String prefix = "";
        String rest = mapped;
        if (rest.startsWith("L") && rest.contains(";")) {
            prefix = rest.substring(0, rest.indexOf(';') + 1);
            rest = rest.substring(rest.indexOf(';') + 1);
        }
        boolean field = desc != null && !desc.startsWith("(");
        return prefix + name + (desc == null ? "" : (field ? ":" : "") + desc);
    }

    private static MixinRepair.Anchor anchorOf(AbstractInsnNode in) {
        if (in instanceof MethodInsnNode mi) {
            return new MixinRepair.Anchor("INVOKE", mi.owner, mi.name, mi.desc, null, -1);
        }
        if (in instanceof FieldInsnNode fi) {
            return new MixinRepair.Anchor("FIELD", fi.owner, fi.name, fi.desc, null, -1);
        }
        if (in instanceof TypeInsnNode ti) {
            return new MixinRepair.Anchor("NEW", ti.desc, null, null, null, -1);
        }
        return new MixinRepair.Anchor("CONSTANT", null, null, null, SiteMatch.constantOf(in), -1);
    }

    /**
     * Injectors whose handler takes the anchor's operands (a call's receiver and arguments, its
     * result, a field's value, a constant): carried to a stand-in only when it takes the same ones.
     */
    private static final Set<String> OPERAND_KINDS = Set.of("Redirect", "WrapOperation", "WrapWithCondition",
        "ModifyArg", "ModifyArgs", "ModifyExpressionValue", "ModifyReturnValue", "ModifyConstant");

    /**
     * Does the stand-in c hand the handler what V's site handed it? Same descriptor and same
     * invocation / access shape (static, interface, virtual / special); a handler that takes the
     * receiver ({@code Redirect}, {@code WrapOperation}, {@code WrapWithCondition}) also needs the
     * same owner. A constant: same type (sameKind already demands it). An allocation never: its
     * handler takes the constructor's arguments, which another class's need not match.
     */
    static boolean sameOperands(String kind, AbstractInsnNode site, AbstractInsnNode c) {
        boolean receiver = kind.equals("Redirect") || kind.equals("WrapOperation") || kind.equals("WrapWithCondition");
        if (site instanceof MethodInsnNode s && c instanceof MethodInsnNode m) {
            return !s.name.equals("<init>") && s.desc.equals(m.desc) && s.getOpcode() == m.getOpcode() && s.itf == m.itf
                && (!receiver || s.getOpcode() == Opcodes.INVOKESTATIC || s.owner.equals(m.owner));
        }
        if (site instanceof FieldInsnNode s && c instanceof FieldInsnNode f) {
            boolean stat = s.getOpcode() == Opcodes.GETSTATIC || s.getOpcode() == Opcodes.PUTSTATIC;
            return s.desc.equals(f.desc) && s.getOpcode() == f.getOpcode() && (!receiver || stat || s.owner.equals(f.owner));
        }
        if (site instanceof TypeInsnNode) {
            return false;
        }
        Object a = SiteMatch.constantOf(site);
        Object b = SiteMatch.constantOf(c);
        return a != null && b != null && a.getClass() == b.getClass();
    }

    /** do the two instructions name the same member (constants and allocations: always) */
    static boolean sameMember(AbstractInsnNode a, AbstractInsnNode b) {
        if (a instanceof MethodInsnNode x && b instanceof MethodInsnNode y) {
            return x.owner.equals(y.owner) && x.name.equals(y.name) && x.desc.equals(y.desc);
        }
        if (a instanceof FieldInsnNode x && b instanceof FieldInsnNode y) {
            return x.owner.equals(y.owner) && x.name.equals(y.name) && x.desc.equals(y.desc);
        }
        return true;
    }

    /**
     * Does the member V's site names still link on W (W's code jar, its class and supertypes)? A
     * library owner, or a supertype outside the code jar, counts as linking: nothing says it went.
     */
    private boolean wLinks(AbstractInsnNode site) {
        String owner, name, desc;
        boolean field = site instanceof FieldInsnNode;
        if (site instanceof MethodInsnNode m) {
            owner = m.owner; name = m.name; desc = m.desc;
        } else if (site instanceof FieldInsnNode f) {
            owner = f.owner; name = f.name; desc = f.desc;
        } else {
            return true;
        }
        if (!Chain.minecraftOwned(owner)) {
            return true;
        }
        if (w(owner) == null) {
            return false; // the class itself is gone
        }
        ArrayDeque<String> todo = new ArrayDeque<>(List.of(owner));
        Set<String> seen = new HashSet<>();
        while (!todo.isEmpty()) {
            String c = todo.pop();
            if (!seen.add(c)) {
                continue;
            }
            ClassNode cn = w(c);
            if (cn == null) {
                return true; // a supertype outside the code jar may declare it
            }
            if (field ? cn.fields.stream().anyMatch(f -> f.name.equals(name) && f.desc.equals(desc)) : method(cn, name, desc) != null) {
                return true;
            }
            if (cn.superName != null) {
                todo.push(cn.superName);
            }
            todo.addAll(cn.interfaces);
        }
        return false;
    }

    /** float/double or int/long: one quantity at two precisions */
    private static boolean samePrecisionFamily(Type a, Type b) {
        int x = a.getSort(), y = b.getSort();
        return x != y && (x == Type.FLOAT || x == Type.DOUBLE) && (y == Type.FLOAT || y == Type.DOUBLE)
            || x != y && (x == Type.INT || x == Type.LONG) && (y == Type.INT || y == Type.LONG);
    }

    private static String boxOf(Type t) {
        return switch (t.getSort()) {
            case Type.BOOLEAN -> "java/lang/Boolean";
            case Type.CHAR -> "java/lang/Character";
            case Type.BYTE -> "java/lang/Byte";
            case Type.SHORT -> "java/lang/Short";
            case Type.INT -> "java/lang/Integer";
            case Type.LONG -> "java/lang/Long";
            case Type.FLOAT -> "java/lang/Float";
            case Type.DOUBLE -> "java/lang/Double";
            default -> null;
        };
    }

    /** the boxed value on the stack as t (a primitive unboxed, a reference cast) */
    private static void unbox(InsnList in, Type t) {
        String b = boxOf(t);
        if (b == null) {
            if (!t.getDescriptor().equals("Ljava/lang/Object;")) {
                in.add(new TypeInsnNode(Opcodes.CHECKCAST, t.getSort() == Type.ARRAY ? t.getDescriptor() : t.getInternalName()));
            }
            return;
        }
        in.add(new TypeInsnNode(Opcodes.CHECKCAST, b));
        in.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, b, t.getClassName() + "Value", "()" + t.getDescriptor(), false));
    }

    /** the t on the stack boxed (a reference left as it is) */
    private static void box(InsnList in, Type t) {
        String b = boxOf(t);
        if (b != null) {
            in.add(new MethodInsnNode(Opcodes.INVOKESTATIC, b, "valueOf", "(" + t.getDescriptor() + ")L" + b + ";", false));
        }
    }

    private static MixinRepair.Anchor withOrdinal(MixinRepair.Anchor a, int ordinal) {
        return new MixinRepair.Anchor(a.kind(), a.owner(), a.name(), a.desc(), a.constant(), ordinal, a.role());
    }

    private static String constantKey(Object c) {
        return c instanceof Integer ? "intValue" : c instanceof Float ? "floatValue" : c instanceof Long ? "longValue"
            : c instanceof Double ? "doubleValue" : "stringValue";
    }

    /** injectors whose handler takes the call's receiver (an instance call / field access) as its first argument */
    private static final Set<String> RECEIVER_KINDS = Set.of("Redirect", "WrapOperation", "WrapWithCondition");

    /** does V's site of the anchor act on an object (an instance call or GETFIELD/PUTFIELD), in the V methods */
    private static boolean takesReceiver(List<MethodNode> vms, MixinRepair.Anchor a) {
        for (MethodNode m : vms) {
            for (AbstractInsnNode in : SiteMatch.occurrences(m, x -> MixinRepair.matchesAnchor(x, a))) {
                int op = in.getOpcode();
                return op != Opcodes.INVOKESTATIC && op != Opcodes.GETSTATIC && op != Opcodes.PUTSTATIC;
            }
        }
        return true; // not read: as the anchor's kind suggests, conservatively an instance access
    }

    /** injectors baseGuard moves: positional, or handing back the one value they take */
    private static final Set<String> GUARD_KINDS = Set.of("Inject", "ModifyExpressionValue", "ModifyArg", "ModifyConstant");

    /**
     * marks a mod handler baseGuard turned into a plain method: "of" its name in the mod, "mixin" the
     * guard mixin and "handler" the guarded injector there (the judge follows it)
     */
    static final String GUARDED = "Ltop/dext/centurybridge/BaseGuarded;";

    /**
     * marks a mod handler alsoOnW copied onto the W method holding its facade method's code: "mixin" the copy's
     * mixin class and "handler" the copy (the judge follows it)
     */
    static final String ALSO_ON_W = "Ltop/dext/centurybridge/AlsoOnW;";

    /** the step's class renames into vanilla classes only (a facade rename left out) */
    private Map<String, String> vanillaRenames;

    private Map<String, String> vanillaRenames() {
        if (vanillaRenames == null) {
            vanillaRenames = new HashMap<>();
            chain.classRenames.forEach((f, t) -> {
                if (!MixinClassRenames.toLayer(t)) {
                    vanillaRenames.put(f, t);
                }
            });
        }
        return vanillaRenames;
    }

    /** V's class with the step's vanilla class renames applied (its facade renames not: W never names a facade) */
    private ClassNode vanillaView(ClassNode v) {
        ClassNode out = new ClassNode();
        v.accept(new org.objectweb.asm.commons.ClassRemapper(out, new org.objectweb.asm.commons.SimpleRemapper(vanillaRenames())));
        return out;
    }

    private String vanillaDesc(String desc) {
        return new org.objectweb.asm.commons.SimpleRemapper(vanillaRenames()).mapMethodDesc(desc);
    }

    /** W's methods by body: descriptor and significant instructions (own members by kind) -> owner and name (two at most) */
    private Map<String, List<String[]>> bodyIndex;

    /** does the body reference a game member of another class, a string, or a constant beyond -1..5 / 0, 1, 2 */
    private static boolean distinctive(MethodNode m, String owner) {
        for (AbstractInsnNode in : m.instructions) {
            if (in instanceof MethodInsnNode mi && !mi.owner.equals(owner) && Chain.minecraftOwned(mi.owner)
                    || in instanceof FieldInsnNode fi && !fi.owner.equals(owner) && Chain.minecraftOwned(fi.owner)
                    || in instanceof TypeInsnNode ti && !ti.desc.equals(owner) && Chain.minecraftOwned(ti.desc)) {
                return true;
            }
            Object c = SiteMatch.constantOf(in);
            if (c instanceof String || c instanceof Number n && !(in.getOpcode() >= Opcodes.ICONST_M1 && in.getOpcode() <= Opcodes.DCONST_1)) {
                return true;
            }
        }
        return false;
    }

    private static String bodyKey(MethodNode m, String owner) {
        List<String> sig = SiteMatch.sigSelf(m, owner);
        return sig.size() < 3 ? null : m.desc + "#" + String.join("|", sig);
    }

    /**
     * The W method holding V method vm's code (vm read with the step's vanilla renames, of V class origin): the one
     * W method, of a game class and not synthetic, whose descriptor and significant instructions (the class's own
     * members by kind) are vm's, with three significant instructions at least. Returns {owner, name, desc} with how
     * it was found in why, or null with why not. A facade descriptor (a type W never names) has no counterpart.
     */
    private String[] wCounterpart(String origin, MethodNode vm, List<String> why) {
        MixinRepair code = wcode(chain);
        if (code == null) {
            why.add("no W code at hand");
            return null;
        }
        if (vm.desc.contains("Ltop/dext/")) {
            why.add("its descriptor names a facade");
            return null;
        }
        if (vm.name.equals("<clinit>")) {
            why.add("a static initializer: another class's static state is not the facade's");
            return null;
        }
        String key = bodyKey(vm, origin);
        if (key == null) {
            why.add("its body is too short to single out (fewer than 3 significant instructions)");
            return null;
        }
        if (!distinctive(vm, origin)) {
            // same instructions made only of the JDK, the class's own members and small constants say nothing about
            // which W class carries the code (ChunkTaskPriorityQueueSorter.close and Brain.clearMemories read the same)
            why.add("its body names nothing distinctive (only the JDK, its own members and small constants)");
            return null;
        }
        if (bodyIndex == null) {
            Map<String, List<String[]>> idx = new HashMap<>();
            code.forEachClass(cn -> {
                if (!Chain.minecraftOwned(cn.name)) {
                    return;
                }
                for (MethodNode m : cn.methods) {
                    if ((m.access & (Opcodes.ACC_SYNTHETIC | Opcodes.ACC_ABSTRACT)) != 0 || m.instructions == null) {
                        continue;
                    }
                    String k = bodyKey(m, cn.name);
                    if (k != null) {
                        List<String[]> l = idx.computeIfAbsent(k, x -> new ArrayList<>(2));
                        if (l.size() < 2) {
                            l.add(new String[] {cn.name, m.name});
                        }
                    }
                }
            });
            bodyIndex = idx;
        }
        List<String[]> hits = bodyIndex.get(key);
        if (hits == null) {
            why.add("no W method holds the same instructions");
            return null;
        }
        if (hits.size() > 1) {
            why.add("several W methods hold the same instructions (" + shortName(hits.get(0)[0]) + "." + hits.get(0)[1] + ", "
                + shortName(hits.get(1)[0]) + "." + hits.get(1)[1] + ")");
            return null;
        }
        why.add("the same " + SiteMatch.sigSelf(vm, origin).size() + " significant instructions, the only W method holding them");
        return new String[] {hits.get(0)[0], hits.get(0)[1], vm.desc};
    }

    /** marks a method MixinBridge put back into a mixin as a target override (restoreOverride) */
    static final String RESTORED = "Ltop/dext/centurybridge/RestoredOverride;";

    static boolean restoredMark(MethodNode m) {
        if (m.invisibleAnnotations != null) {
            for (AnnotationNode a : m.invisibleAnnotations) {
                if (a.desc.equals(RESTORED)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String pkg(String internal) {
        int i = internal.lastIndexOf('/');
        return i < 0 ? "" : internal.substring(0, i);
    }
}
