package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Runs one jar through a chain of segments: mixin triage (dead mixins are
 * stripped from their configs, load-bearing ones with a warning), reference
 * verification with boundary attribution, fabric.mod.json patching.
 * Verdicts follow the tombstone philosophy: missing symbols degrade lazily.
 */
public final class JarProcessor {
    private static final Gson GSON = new Gson();

    public static final class Report {
        public String file;
        public String modId;
        public String verdict;
        public List<String> issues = new ArrayList<>();        // server-runtime relevant
        public List<String> clientIssues = new ArrayList<>();  // client-side residuals, tracked separately
        public int datagenSkipped;                             // dev-time-only refs, never execute in play
        public int ledgered;                                   // examined damage classified in the quirk ledger
        public List<String> strippedMixins = new ArrayList<>();
        public List<String> notes = new ArrayList<>();
    }

    public static Report process(Path in, Path outDir, Chain chain) throws IOException {
        Report rpt = new Report();
        rpt.file = in.getFileName().toString();

        Map<String, byte[]> entries = readAll(Files.readAllBytes(in));
        Set<String> issues = new TreeSet<>();
        processEntries(entries, chain, rpt, issues, "");

        // ---- metadata patch ----
        byte[] fmj = entries.get("fabric.mod.json");
        if (fmj != null) {
            entries.put("fabric.mod.json", patchFmj(fmj, chain, rpt));
        }

        // ---- emit ----
        String base = rpt.file.endsWith(".jar") ? rpt.file.substring(0, rpt.file.length() - 4) : rpt.file;
        Path out = outDir.resolve(base + "+" + chain.to + ".jar");
        Files.createDirectories(outDir);
        try (ZipOutputStream zout = new ZipOutputStream(Files.newOutputStream(out))) {
            for (Map.Entry<String, byte[]> e : entries.entrySet()) {
                zout.putNextEntry(new ZipEntry(e.getKey()));
                zout.write(e.getValue());
                zout.closeEntry();
            }
        }

        rpt.issues = new ArrayList<>(issues);
        rpt.verdict = !rpt.strippedMixins.isEmpty() ? "ok_degraded"
            : issues.isEmpty() ? "ok_direct" : "ok_partial";
        if (!issues.isEmpty() && !rpt.strippedMixins.isEmpty()) {
            rpt.verdict = "ok_degraded_partial";
        }
        rpt.notes.add("output: " + out.getFileName());
        return rpt;
    }

    // ---------------------------------------------------------------- pieces

    /**
     * Triage + strip + verify one jar's entry map, recursing into JIJ-nested
     * jars (bundled libraries carry their own mixins -- a nested mixin whose
     * target died crashes the whole game just as hard as a top-level one).
     */
    private static void processEntries(Map<String, byte[]> entries, Chain chain,
                                       Report rpt, Set<String> issues, String tag) throws IOException {
        MixinTriage.Result triage = MixinTriage.run(entries, chain);
        Set<String> deadMixins = new HashSet<>();
        for (MixinTriage.MixinInfo mx : triage.mixins()) {
            if (mx.vanilla() && mx.applyFatal()) {
                deadMixins.add(mx.className());
                String lb = mx.loadBearing() ? " [load-bearing! may be unstable]" : "";
                rpt.strippedMixins.add(tag + shortName(mx.className()) + lb + " -- " + String.join("; ", mx.broken()));
            } else if (mx.vanilla() && mx.worst().level() != Chain.Level.OK) {
                // triage decided to KEEP this mixin (no explicit-desc spec to fail at
                // apply); an informational note, not an unaccounted residual
                rpt.notes.add(tag + "kept L2 mixin (target sig changed@" + mx.worst().boundary()
                    + "): " + shortName(mx.className()));
            }
        }
        stripConfigs(entries, triage, deadMixins);

        // tombstone stub class must be unique per jar layer (all layers share the runtime classpath)
        String tombClass = "centurybridge/gen/Tombstones" + (tag.isEmpty() ? "" : "$" + tag.chars().filter(c -> c == '(').count());
        Tombstones reg = new Tombstones(tombClass, chain.to);

        for (Map.Entry<String, byte[]> e : entries.entrySet()) {
            String name = e.getKey();
            if (name.endsWith(".class")) {
                String cls = name.substring(0, name.length() - 6);
                if (!triage.configOfClass().containsKey(cls)) {
                    // fabric convention: the mod's own datagen providers live under
                    // .../datagen/ or .../data/ and never execute in a live game
                    boolean datagenCaller = cls.contains("datagen") || cls.contains("/data/");
                    Set<String> sink = datagenCaller ? new TreeSet<>() : issues;
                    byte[] rewritten = verifyAndRewrite(e.getValue(), chain, sink, tag, reg, rpt);
                    if (datagenCaller) {
                        rpt.datagenSkipped += sink.size();
                    }
                    if (rewritten != null) {
                        e.setValue(rewritten);
                    }
                }
            } else if (name.startsWith("META-INF/jars/") && name.endsWith(".jar")) {
                Map<String, byte[]> nested = readAll(e.getValue());
                processEntries(nested, chain, rpt, issues, tag + "(bundled) ");
                e.setValue(writeJar(nested));
            }
        }
        if (!reg.stubs.isEmpty()) {
            entries.put(tombClass + ".class", reg.synthesize());
        }
    }

    /** per-jar-layer registry of lazy-fail stubs, synthesized as one class */
    private static final class Tombstones {
        record Stub(String name, String staticDesc, String message) {}
        final String clsName;
        final String targetVer;
        final Map<String, Stub> stubs = new LinkedHashMap<>();

        Tombstones(String clsName, String targetVer) {
            this.clsName = clsName;
            this.targetVer = targetVer;
        }

        Stub get(String owner, String name, String desc, boolean isStatic, String boundary) {
            return stubs.computeIfAbsent(owner + "." + name + desc + isStatic, k -> {
                String staticDesc = isStatic ? desc : "(L" + owner + ";" + desc.substring(1);
                String msg = "CenturyBridge tombstone: " + owner.replace('/', '.') + "." + name
                    + " no longer exists in " + targetVer + " (died @" + boundary + ")";
                return new Stub("t" + stubs.size(), staticDesc, msg);
            });
        }

        byte[] synthesize() {
            org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(org.objectweb.asm.ClassWriter.COMPUTE_MAXS);
            cw.visit(Opcodes.V17, Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNTHETIC, clsName, null, "java/lang/Object", null);
            for (Stub s : stubs.values()) {
                MethodVisitor mv = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, s.name(), s.staticDesc(), null, null);
                mv.visitCode();
                mv.visitTypeInsn(Opcodes.NEW, "java/lang/UnsupportedOperationException");
                mv.visitInsn(Opcodes.DUP);
                mv.visitLdcInsn(s.message());
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/UnsupportedOperationException",
                    "<init>", "(Ljava/lang/String;)V", false);
                mv.visitInsn(Opcodes.ATHROW);
                mv.visitMaxs(0, 0);
                mv.visitEnd();
            }
            cw.visitEnd();
            return cw.toByteArray();
        }
    }

    private static byte[] writeJar(Map<String, byte[]> entries) throws IOException {
        var buf = new java.io.ByteArrayOutputStream();
        try (ZipOutputStream zout = new ZipOutputStream(buf)) {
            for (Map.Entry<String, byte[]> e : entries.entrySet()) {
                zout.putNextEntry(new ZipEntry(e.getKey()));
                zout.write(e.getValue());
                zout.closeEntry();
            }
        }
        return buf.toByteArray();
    }

    private static Map<String, byte[]> readAll(byte[] jar) throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        try (ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.isDirectory()) {
                    entries.put(e.getName(), zin.readAllBytes());
                }
            }
        }
        return entries;
    }

    private static void stripConfigs(Map<String, byte[]> entries, MixinTriage.Result triage, Set<String> dead) {
        if (dead.isEmpty()) {
            return;
        }
        Set<String> configs = new HashSet<>(triage.configOfClass().values());
        for (String cfgName : configs) {
            byte[] data = entries.get(cfgName);
            if (data == null) {
                continue;
            }
            JsonObject cfg = GSON.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) {
                    continue;
                }
                JsonArray kept = new JsonArray();
                for (JsonElement e : cfg.getAsJsonArray(key)) {
                    String cls = (pkg.isEmpty() ? "" : pkg + "/") + e.getAsString().replace('.', '/');
                    if (!dead.contains(cls)) {
                        kept.add(e);
                    }
                }
                cfg.add(key, kept);
            }
            entries.put(cfgName, GSON.toJson(cfg).getBytes(StandardCharsets.UTF_8));
        }
    }

    /**
     * Analysis pass collects issues and marks call sites that can be repaired
     * (static redirects into the runtime bridge; dead-method tombstones). When
     * repairs exist, a second ASM pass rewrites them -- redirected calls stop
     * being issues, tombstoned calls fail lazily with a descriptive message.
     * Returns the rewritten class bytes, or null when unchanged.
     */
    private static byte[] verifyAndRewrite(byte[] bytes, Chain chain, Set<String> issues,
                                           String tag, Tombstones reg, Report rpt) {
        Map<String, String> redirects = new HashMap<>();     // static: owner.name+desc -> runtime class
        Map<String, String> instRedirects = new HashMap<>(); // instance: receiver becomes arg 0
        Map<String, String> fldRedirects = new HashMap<>();  // GETSTATIC: owner.name:desc -> runtime class
        Map<String, String> fldRenames = new HashMap<>();    // instance fields renamed in place
        Map<String, String> mthRenames = new HashMap<>();    // methods renamed in place (relocated to super)
        Map<String, String> tombstone = new HashMap<>();     // owner.name+desc -> boundary
        Set<String> clientTombstones = new HashSet<>();      // subset of tombstone keys on client-only owners
        Set<String> silentTombstones = new HashSet<>();      // ledgered damage: rewritten but not reported

        // dead-class facade renames touch descriptors/signatures everywhere, so the
        // trigger is a raw byte scan rather than per-ref analysis
        boolean needClassRename = false;
        if (!chain.classRenames.isEmpty()) {
            String raw = new String(bytes, java.nio.charset.StandardCharsets.ISO_8859_1);
            for (String dead : chain.classRenames.keySet()) {
                if (raw.contains(dead)) {
                    needClassRename = true;
                    rpt.notes.add(tag + "facade-renamed: " + dead + " -> " + chain.classRenames.get(dead));
                    break;
                }
            }
        }
        ClassVisitor analysis = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(int ver, int acc, String name, String sig, String superName, String[] ifaces) {
                checkClass(superName);
                if (ifaces != null) {
                    for (String i : ifaces) {
                        checkClass(i);
                    }
                }
            }

            @Override
            public MethodVisitor visitMethod(int acc, String mName, String mDesc, String sig, String[] ex) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        checkClass(owner);
                        if (!owner.startsWith("net/minecraft/")) {
                            return; // mod-owned members (incl. overrides of dead interface methods) still exist
                        }
                        if (chain.classRenames.containsKey(owner)) {
                            return; // owner facade-renamed; the member lives on the facade
                        }
                        String key = owner + "." + name + desc;
                        if (chain.shimCovers.contains(key)) {
                            return; // restored at runtime by a shim mixin
                        }
                        String rt = chain.staticRedirects.get(key);
                        if (rt != null && op == Opcodes.INVOKESTATIC) {
                            redirects.put(key, rt);
                            return;
                        }
                        String irt = chain.instanceRedirects.get(key);
                        if (irt != null && (op == Opcodes.INVOKEVIRTUAL || op == Opcodes.INVOKEINTERFACE || op == Opcodes.INVOKESPECIAL) && !name.equals("<init>")) {
                            instRedirects.put(key, irt);
                            return;
                        }
                        String mrn = chain.methodRenames.get(key);
                        if (mrn != null) {
                            mthRenames.put(key, mrn);
                            return;
                        }
                        Chain.Issue r = chain.resolveMember('m', name);
                        if (r.level() == Chain.Level.OK) {
                            return;
                        }
                        String side = chain.sideOf(owner);
                        if (side.equals("datagen")) {
                            rpt.datagenSkipped++; // dev-time bytecode, never executes in play
                            return;
                        }
                        if (chain.ledgerReason(owner, name) != null) {
                            rpt.ledgered++;
                            if (r.level() == Chain.Level.L3 && !name.equals("<init>")
                                    && chain.resolveClass(owner).level() == Chain.Level.OK) {
                                tombstone.put(key, r.boundary()); // still fail lazily with a message
                                silentTombstones.add(key);        // but never as an issue line
                            }
                            return;
                        }
                        if (r.level() == Chain.Level.L3 && !name.equals("<init>")
                                && chain.resolveClass(owner).level() == Chain.Level.OK) {
                            tombstone.put(key, r.boundary());
                            if (side.equals("client")) {
                                clientTombstones.add(key);
                            }
                        } else {
                            String line = tag + r.level() + " method @" + r.boundary() + ": " + shortName(owner) + "." + name;
                            if (side.equals("client")) {
                                rpt.clientIssues.add(tag + "[client] " + line);
                            } else {
                                issues.add(line);
                            }
                        }
                    }

                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        checkClass(owner);
                        if (!owner.startsWith("net/minecraft/")) {
                            return;
                        }
                        if (chain.classRenames.containsKey(owner)) {
                            return;
                        }
                        String fkey = owner + "." + name + ":" + desc;
                        String frt = chain.fieldRedirects.get(fkey);
                        if (frt != null && op == Opcodes.GETSTATIC) {
                            fldRedirects.put(fkey, frt);
                            return;
                        }
                        String rename = chain.fieldRenames.get(fkey);
                        if (rename != null) {
                            fldRenames.put(fkey, rename);
                            return;
                        }
                        Chain.Issue r = chain.resolveMember('f', name);
                        if (r.level() == Chain.Level.OK) {
                            return;
                        }
                        String side = chain.sideOf(owner);
                        if (side.equals("datagen")) {
                            rpt.datagenSkipped++;
                            return;
                        }
                        if (chain.ledgerReason(owner, name) != null) {
                            rpt.ledgered++;
                            return;
                        }
                        String line = tag + r.level() + " field @" + r.boundary() + ": " + shortName(owner) + "." + name;
                        if (side.equals("client")) {
                            rpt.clientIssues.add(tag + "[client] " + line);
                        } else {
                            issues.add(line);
                        }
                    }

                    @Override
                    public void visitTypeInsn(int op, String type) {
                        checkClass(type);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String desc,
                            org.objectweb.asm.Handle bsm, Object... bsmArgs) {
                        // method references to dead/redirected methods hide inside
                        // indy bootstrap arguments -- treat each handle like a call site
                        for (Object arg : bsmArgs) {
                            if (arg instanceof org.objectweb.asm.Handle h
                                    && h.getOwner().startsWith("net/minecraft/")
                                    && !chain.classRenames.containsKey(h.getOwner())) {
                                String key = h.getOwner() + "." + h.getName() + h.getDesc();
                                if (chain.shimCovers.contains(key)) {
                                    continue;
                                }
                                String rt = chain.staticRedirects.get(key);
                                String irt = chain.instanceRedirects.get(key);
                                if (rt != null || irt != null) {
                                    redirects.putAll(rt != null ? Map.of(key, rt) : Map.of());
                                    if (irt != null) {
                                        instRedirects.put(key, irt);
                                    }
                                    continue;
                                }
                                Chain.Issue r = chain.resolveMember('m', h.getName());
                                if (r.level() == Chain.Level.L3 && !h.getName().equals("<init>")
                                        && chain.resolveClass(h.getOwner()).level() == Chain.Level.OK) {
                                    tombstone.put(key, r.boundary());
                                    if (chain.ledgerReason(h.getOwner(), h.getName()) != null) {
                                        rpt.ledgered++;
                                        silentTombstones.add(key);
                                    }
                                }
                            }
                        }
                    }
                };
            }

            private void checkClass(String internal) {
                if (internal == null) {
                    return;
                }
                String t = internal;
                while (t.startsWith("[")) {
                    t = t.substring(1);
                }
                if (t.startsWith("L") && t.endsWith(";")) {
                    t = t.substring(1, t.length() - 1);
                }
                if (chain.classRenames.containsKey(t)) {
                    return; // facade-renamed, repaired by the rewrite pass
                }
                Chain.Issue r = chain.resolveClass(t);
                if (r.level() != Chain.Level.OK) {
                    String side = chain.sideOf(t);
                    if (chain.ledgerReason(t, null) != null) {
                        rpt.ledgered++;
                    } else if (side.equals("datagen")) {
                        rpt.datagenSkipped++;
                    } else if (side.equals("client")) {
                        rpt.clientIssues.add(tag + "[client] " + r.level() + " class @" + r.boundary() + ": " + t);
                    } else {
                        issues.add(tag + r.level() + " class @" + r.boundary() + ": " + t);
                    }
                }
            }
        };
        new ClassReader(bytes).accept(analysis, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        if (redirects.isEmpty() && instRedirects.isEmpty() && fldRedirects.isEmpty()
                && fldRenames.isEmpty() && mthRenames.isEmpty() && tombstone.isEmpty() && !needClassRename) {
            return null;
        }
        for (String k : redirects.keySet()) {
            rpt.notes.add(tag + "static-redirected: " + k + " -> " + redirects.get(k));
        }
        for (String k : instRedirects.keySet()) {
            rpt.notes.add(tag + "instance-redirected: " + k + " -> " + instRedirects.get(k));
        }
        for (String k : fldRedirects.keySet()) {
            rpt.notes.add(tag + "field-redirected: " + k + " -> " + fldRedirects.get(k));
        }
        for (Map.Entry<String, String> t : tombstone.entrySet()) {
            if (silentTombstones.contains(t.getKey())) {
                continue; // ledgered: rewritten to a descriptive lazy-fail, accounted elsewhere
            }
            String line = tag + "tombstoned (lazy-fail) @" + t.getValue() + ": " + t.getKey();
            if (clientTombstones.contains(t.getKey())) {
                rpt.clientIssues.add(tag + "[client] " + line);
            } else {
                issues.add(line);
            }
        }

        ClassReader cr = new ClassReader(bytes);
        org.objectweb.asm.ClassWriter cw = new org.objectweb.asm.ClassWriter(0);
        ClassVisitor sink = needClassRename
            ? new org.objectweb.asm.commons.ClassRemapper(cw,
                new org.objectweb.asm.commons.SimpleRemapper(chain.classRenames))
            : cw;
        cr.accept(new ClassVisitor(Opcodes.ASM9, sink) {
            @Override
            public MethodVisitor visitMethod(int acc, String mName, String mDesc, String sig, String[] ex) {
                MethodVisitor mv = super.visitMethod(acc, mName, mDesc, sig, ex);
                return new MethodVisitor(Opcodes.ASM9, mv) {
                    @Override
                    public void visitMethodInsn(int op, String owner, String name, String desc, boolean itf) {
                        String key = owner + "." + name + desc;
                        String rt = redirects.get(key);
                        if (rt != null && op == Opcodes.INVOKESTATIC) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, rt, name, desc, false);
                            return;
                        }
                        String irt = instRedirects.get(key);
                        if (irt != null && (op == Opcodes.INVOKEVIRTUAL || op == Opcodes.INVOKEINTERFACE || op == Opcodes.INVOKESPECIAL) && !name.equals("<init>")) {
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, irt, name,
                                "(L" + owner + ";" + desc.substring(1), false);
                            return;
                        }
                        String mrn = mthRenames.get(key);
                        if (mrn != null) {
                            super.visitMethodInsn(op, owner, mrn, desc, itf);
                            return;
                        }
                        String boundary = tombstone.get(key);
                        if (boundary != null) {
                            Tombstones.Stub s = reg.get(owner, name, desc, op == Opcodes.INVOKESTATIC, boundary);
                            super.visitMethodInsn(Opcodes.INVOKESTATIC, reg.clsName, s.name(), s.staticDesc(), false);
                            return;
                        }
                        super.visitMethodInsn(op, owner, name, desc, itf);
                    }

                    @Override
                    public void visitFieldInsn(int op, String owner, String name, String desc) {
                        String fkey = owner + "." + name + ":" + desc;
                        if (op == Opcodes.GETSTATIC && fldRedirects.containsKey(fkey)) {
                            super.visitFieldInsn(Opcodes.GETSTATIC, fldRedirects.get(fkey), name, desc);
                            return;
                        }
                        String renamed = fldRenames.get(fkey);
                        if (renamed != null) {
                            super.visitFieldInsn(op, owner, renamed, desc);
                            return;
                        }
                        super.visitFieldInsn(op, owner, name, desc);
                    }

                    @Override
                    public void visitInvokeDynamicInsn(String name, String desc,
                            org.objectweb.asm.Handle bsm, Object... bsmArgs) {
                        Object[] rewritten = bsmArgs.clone();
                        for (int i = 0; i < rewritten.length; i++) {
                            if (!(rewritten[i] instanceof org.objectweb.asm.Handle h)) {
                                continue;
                            }
                            String key = h.getOwner() + "." + h.getName() + h.getDesc();
                            String rt = redirects.get(key);
                            if (rt != null && h.getTag() == Opcodes.H_INVOKESTATIC) {
                                rewritten[i] = new org.objectweb.asm.Handle(
                                    Opcodes.H_INVOKESTATIC, rt, h.getName(), h.getDesc(), false);
                                continue;
                            }
                            String irt = instRedirects.get(key);
                            if (irt != null && (h.getTag() == Opcodes.H_INVOKEVIRTUAL
                                    || h.getTag() == Opcodes.H_INVOKEINTERFACE)) {
                                rewritten[i] = new org.objectweb.asm.Handle(Opcodes.H_INVOKESTATIC, irt,
                                    h.getName(), "(L" + h.getOwner() + ";" + h.getDesc().substring(1), false);
                                continue;
                            }
                            String boundary = tombstone.get(key);
                            if (boundary != null) {
                                boolean isStatic = h.getTag() == Opcodes.H_INVOKESTATIC;
                                Tombstones.Stub s = reg.get(h.getOwner(), h.getName(), h.getDesc(), isStatic, boundary);
                                rewritten[i] = new org.objectweb.asm.Handle(
                                    Opcodes.H_INVOKESTATIC, reg.clsName, s.name(), s.staticDesc(), false);
                            }
                        }
                        super.visitInvokeDynamicInsn(name, desc, bsm, rewritten);
                    }
                };
            }
        }, 0);
        return cw.toByteArray();
    }

    private static byte[] patchFmj(byte[] data, Chain chain, Report rpt) {
        JsonObject fmj = GSON.fromJson(new String(data, StandardCharsets.UTF_8), JsonObject.class);
        if (fmj.has("id")) {
            rpt.modId = fmj.get("id").getAsString();
        }
        if (fmj.has("depends")) {
            JsonObject dep = fmj.getAsJsonObject("depends");
            if (dep.has("minecraft")) {
                dep.add("minecraft", new JsonPrimitive(chain.to));
                rpt.notes.add("minecraft dep -> " + chain.to);
            }
            for (Segment s : chain.segments) {
                for (Map.Entry<String, String> e : s.depRenames.entrySet()) {
                    if (dep.has(e.getKey())) {
                        JsonElement v = dep.remove(e.getKey());
                        dep.add(e.getValue(), v);
                        rpt.notes.add("dep " + e.getKey() + " -> " + e.getValue());
                    }
                }
            }
        }
        return GSON.toJson(fmj).getBytes(StandardCharsets.UTF_8);
    }

    private static String shortName(String internal) {
        int i = internal.lastIndexOf('/');
        return i < 0 ? internal : internal.substring(i + 1);
    }
}
