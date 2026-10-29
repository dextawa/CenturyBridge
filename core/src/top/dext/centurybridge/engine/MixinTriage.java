package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * Mixin survival triage, ported from the Python prototype with its
 * hard-earned pits intact: refmap keys may be descriptor-qualified; a
 * stable-named target such as {@code <init>} can die through a dead class in
 * its descriptor; implicit accessors infer their target from the method name.
 *
 * Annotation strings inside published jars are SOURCE-form (Yarn/Mojmap) and
 * translated at runtime via the refmap -- triage must do the same lookup.
 */
public final class MixinTriage {
    private static final Gson GSON = new Gson();
    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";
    private static final String OVERWRITE = "Lorg/spongepowered/asm/mixin/Overwrite;";
    private static final String SHADOW = "Lorg/spongepowered/asm/mixin/Shadow;";
    private static final String ACCESSOR = "Lorg/spongepowered/asm/mixin/gen/Accessor;";
    private static final String INVOKER = "Lorg/spongepowered/asm/mixin/gen/Invoker;";
    private static final Pattern IMPLICIT_ACCESSOR = Pattern.compile("(?:get|is|set)([A-Z].*)");

    public record MixinInfo(String className, String config, List<String> targets, boolean vanilla,
                            boolean loadBearing, Chain.Issue worst, List<String> broken) {

        /**
         * Will this mixin hard-fail at APPLY time? L3 anywhere is fatal; an L2
         * on an inject-target/@At spec is also fatal when the spec carries an
         * explicit descriptor the changed method no longer matches.
         */
        public boolean applyFatal() {
            return applyFatal(false);
        }

        /**
         * @param injectorsHandled MixinRepair ran against real bodies and switched every
         *        injector with a dead target or anchor to require=0 -- those entries no
         *        longer take the whole mixin (and its working injectors) down with them
         */
        public boolean applyFatal(boolean injectorsHandled) {
            for (String b : broken) {
                if (injectorsHandled && (b.contains("inject-target") || b.contains(": at ") || b.contains("at-owner"))) {
                    continue;
                }
                if (b.startsWith("L3")) {
                    return true;
                }
                if (b.startsWith("L2") && (b.contains("inject-target") || b.contains(": at ")
                        || b.contains("at-owner"))) {
                    return true;
                }
                // a @Shadow / @Accessor / @Invoker resolves by name AND descriptor:
                // a changed descriptor is exactly as fatal as a missing member
                if (b.startsWith("L2") && (b.contains("shadow") || b.contains("accessor")
                        || b.contains("invoker"))) {
                    return true;
                }
            }
            return false;
        }
    }

    public record Result(List<MixinInfo> mixins, Map<String, String> configOfClass) {}

    public static Result run(Map<String, byte[]> entries, Chain chain) {
        Map<String, String> configOfClass = new LinkedHashMap<>(); // mixin class -> config entry name
        Map<String, Map<String, String>> refmap = new HashMap<>(); // mixin class -> src -> intermediary
        List<MixinInfo> out = new ArrayList<>();
        if (!readConfigs(entries, configOfClass, refmap)) {
            return new Result(out, configOfClass);
        }

        // the members the jar's own class mixins merge onto their targets (smx1 CASE 13: a climbing layer's @Invoker of
        // a member its own CbRecipeJsonShapedStatics restores on class_1869): declared on the target at runtime
        LayerMembers own = ownMembers(entries);
        for (Map.Entry<String, String> e : configOfClass.entrySet()) {
            byte[] bytes = entries.get(e.getKey() + ".class");
            if (bytes != null) {
                out.add(analyze(bytes, e.getKey(), e.getValue(),
                    refmap.getOrDefault(e.getKey(), Map.of()), chain, own));
            }
        }
        return new Result(out, configOfClass);
    }

    /**
     * K3 (MixinPeel): one listed mixin judged again from other bytes (a peeled copy), with the jar's configs and refmaps
     * ({@code cfgs} / {@code refmaps} from {@link #configs}) and its own merged members
     */
    static MixinInfo reanalyze(byte[] bytes, String className, Map<String, String> cfgs, Map<String, Map<String, String>> refmaps,
                               Chain chain, LayerMembers own) {
        return analyze(bytes, className, cfgs.getOrDefault(className, ""), refmaps.getOrDefault(className, Map.of()), chain, own);
    }

    /** the jar's listed mixin classes -> config, and refmaps, into the two maps (see readConfigs) */
    static void configs(Map<String, byte[]> entries, Map<String, String> cfgs, Map<String, Map<String, String>> refmaps) {
        readConfigs(entries, cfgs, refmaps);
    }

    /** the members the jar's listed class mixins merge onto their targets ({@link LayerMembers#ofJar}) */
    public static LayerMembers ownMembers(Map<String, byte[]> entries) {
        Map<String, String> configOfClass = new LinkedHashMap<>();
        readConfigs(entries, configOfClass, new HashMap<>());
        return LayerMembers.ofJar(entries, configOfClass.keySet());
    }

    /** the jar's mixin configs: listed mixin class -> config entry name, and the refmaps; false without fabric.mod.json */
    private static boolean readConfigs(Map<String, byte[]> entries, Map<String, String> configOfClass,
                                       Map<String, Map<String, String>> refmap) {
        byte[] fmjBytes = entries.get("fabric.mod.json");
        if (fmjBytes == null) {
            return false;
        }
        JsonObject fmj = GSON.fromJson(new String(fmjBytes, StandardCharsets.UTF_8), JsonObject.class);
        for (JsonElement cfgRef : fmj.has("mixins") ? fmj.getAsJsonArray("mixins") : new JsonArray()) {
            String cfgName = cfgRef.isJsonObject()
                ? cfgRef.getAsJsonObject().get("config").getAsString() : cfgRef.getAsString();
            byte[] cfgBytes = entries.get(cfgName);
            if (cfgBytes == null) {
                continue;
            }
            JsonObject cfg = GSON.fromJson(new String(cfgBytes, StandardCharsets.UTF_8), JsonObject.class);
            String pkg = cfg.has("package") ? cfg.get("package").getAsString().replace('.', '/') : "";
            if (cfg.has("refmap")) {
                byte[] rm = entries.get(cfg.get("refmap").getAsString());
                if (rm != null) {
                    JsonObject root = GSON.fromJson(new String(rm, StandardCharsets.UTF_8), JsonObject.class);
                    JsonObject mappings = root.has("mappings") ? root.getAsJsonObject("mappings") : new JsonObject();
                    for (String cls : mappings.keySet()) {
                        Map<String, String> table = refmap.computeIfAbsent(cls, k -> new HashMap<>());
                        JsonObject t = mappings.getAsJsonObject(cls);
                        for (String k : t.keySet()) {
                            table.put(k, t.get(k).getAsString());
                        }
                    }
                }
            }
            for (String key : new String[] {"mixins", "client", "server"}) {
                if (!cfg.has(key)) {
                    continue;
                }
                for (JsonElement e : cfg.getAsJsonArray(key)) {
                    String cls = (pkg.isEmpty() ? "" : pkg + "/") + e.getAsString().replace('.', '/');
                    configOfClass.put(cls, cfgName);
                }
            }
        }
        return true;
    }

    /**
     * whether a mixin at {@code mixinPriority} finds a member the converted jar's own class mixins merge onto the
     * target (kind 'm' or 'f'): as {@link Chain#layerDeclares} for the step layer, with Mixin's priority order -- the
     * merging mixin applies first only at a lower priority (MixinCheck.indexMerged judges gate 3 the same way). The jar
     * is in the analysis namespace like the query, so no wall spelling
     */
    static boolean ownDeclares(LayerMembers own, String owner, char kind, String name, String desc, int mixinPriority) {
        if (desc == null) {
            return false;
        }
        if (kind == 'f') {
            org.objectweb.asm.tree.FieldNode f = own.field(owner, name, desc);
            return f != null && own.fieldPriority(f) < mixinPriority;
        }
        org.objectweb.asm.tree.MethodNode m = own.member(owner, name, desc);
        return m != null && own.priority(m) < mixinPriority;
    }

    // ---------------------------------------------------------------- analysis

    private static MixinInfo analyze(byte[] bytes, String className, String config,
                                     Map<String, String> refmap, Chain chain, LayerMembers own) {
        List<String> targets = new ArrayList<>();
        List<String> broken = new ArrayList<>();
        boolean[] loadBearing = {false};
        Chain.Issue[] worst = {Chain.Issue.OK};
        // the mixin's @Mixin priority (Mixin's default 1000): a member a layer mixin merges is there for this mixin's
        // @Shadow / @Accessor / @Invoker only when the layer's mixin applies first (Chain.layerDeclares)
        int[] priority = {LayerMembers.DEFAULT_PRIORITY};

        java.util.function.BiConsumer<Chain.Issue, String> touch = (issue, what) -> {
            if (issue.level() != Chain.Level.OK) {
                broken.add(issue.level() + "@" + issue.boundary() + ": " + what);
            }
            worst[0] = worst[0].worst(issue);
        };
        // Mixin looks the member up in the target class itself; a target that merely
        // inherits it (the method moved up the hierarchy) fails at APPLY time, so it
        // is as fatal as a dead member even though resolveSpec says the name survives.
        java.util.function.BiConsumer<Spec, String> declCheck = (sp, what) -> {
            List<String> owners = sp.owner != null ? List.of(sp.owner) : targets;
            for (String o : owners) {
                // the descriptor as the jar's ClassRemapper will leave it (the step's classRenames: 1.21.2
                // DirectionProperty -> EnumProperty, a @Shadow DirectionProperty FACING is then exactly W's field)
                String d = chain.renamedDesc(sp.desc);
                boolean declared = sp.kind == 'f'
                    ? chain.fieldDeclaredOnTarget(o, sp.name, sp.desc) || !java.util.Objects.equals(d, sp.desc) && chain.fieldDeclaredOnTarget(o, sp.name, d)
                    : chain.declaredOnTarget(o, sp.name, sp.desc) || !java.util.Objects.equals(d, sp.desc) && chain.declaredOnTarget(o, sp.name, d);
                // or a member the step layer restores on the target (LayerMembers: a restored method or field)
                declared = declared || chain.layerDeclares(o, sp.kind, sp.name, sp.desc, priority[0])
                    || !java.util.Objects.equals(d, sp.desc) && chain.layerDeclares(o, sp.kind, sp.name, d, priority[0])
                    // or one the jar's own class mixins merge onto the target (CASE 13)
                    || ownDeclares(own, o, sp.kind, sp.name, sp.desc, priority[0]);
                if (!declared) {
                    touch.accept(new Chain.Issue(Chain.Level.L3, "target-decl"),
                        what + " not declared on " + o.substring(o.lastIndexOf('/') + 1));
                }
            }
        };
        // L2 says a member of that name changed its descriptor at some boundary (L3 that it is gone, for a
        // descriptor naming a class the step renames). When the target
        // declares exactly the shape the mixin names (a mixin MixinBridge carried to the new shape,
        // or another overload that stayed), Mixin finds it: nothing is broken
        java.util.function.BiFunction<Spec, Chain.Issue, Chain.Issue> exact = (sp, issue) -> {
            // L3 too when the step's classRenames give the member exactly W's shape (the segment judged the
            // V descriptor; the ClassRemapper will write the renamed one)
            // a member the step layer restores (LayerMembers) in exactly this shape on every target: the segment
            // judged V's member dead or changed, but at runtime the target declares it (Item.components on 26.1)
            if (issue.level() != Chain.Level.OK && sp.desc != null && !(sp.owner == null && targets.isEmpty())) {
                boolean all = true;
                for (String o : sp.owner != null ? List.of(sp.owner) : targets) {
                    all &= chain.layerDeclares(o, sp.kind, sp.name, sp.desc, priority[0])
                        || chain.layerDeclares(o, sp.kind, sp.name, chain.renamedDesc(sp.desc), priority[0])
                        || ownDeclares(own, o, sp.kind, sp.name, sp.desc, priority[0])
                        // or the facade the step renames the target to declares it (the mixin applies to the facade)
                        || chain.facadeDeclares(o, sp.kind, sp.name, sp.desc);
                }
                if (all) {
                    return Chain.Issue.OK;
                }
            }
            boolean viaRename = issue.level() == Chain.Level.L3 && sp.desc != null && !sp.desc.equals(chain.renamedDesc(sp.desc));
            if (issue.level() != Chain.Level.L2 && !viaRename || sp.desc == null || chain.targetDecls.isEmpty()) {
                return issue;
            }
            for (String o : sp.owner != null ? List.of(sp.owner) : targets) {
                java.util.Set<String> d = chain.targetDecls.get(o);
                String rd = chain.renamedDesc(sp.desc); // as the ClassRemapper leaves it
                if (d == null || !d.contains(sp.kind == 'f' ? sp.name + ":" + sp.desc : sp.name + sp.desc)
                        && !d.contains(sp.kind == 'f' ? sp.name + ":" + rd : sp.name + rd)) {
                    return issue;
                }
            }
            return Chain.Issue.OK;
        };

        ClassVisitor cv = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public void visit(int v, int acc, String name, String sig, String sup, String[] ifaces) {
                if (ifaces != null && ifaces.length > 0) {
                    loadBearing[0] = true; // interface merge onto the target
                }
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
                if (!desc.equals(MIXIN)) {
                    return null;
                }
                Map<String, Object> values = new LinkedHashMap<>();
                return collector(values, () -> {
                    if (values.get("priority") instanceof Integer p) {
                        priority[0] = p;
                    }
                    for (Object t : asList(values.get("value"))) {
                        if (t instanceof Type type) {
                            targets.add(type.getInternalName());
                        }
                    }
                    for (Object t : asList(values.get("targets"))) {
                        if (t instanceof String s) {
                            String mapped = rm(refmap, s).replace('.', '/');
                            if (mapped.startsWith("L") && mapped.endsWith(";")) {
                                mapped = mapped.substring(1, mapped.length() - 1);
                            }
                            targets.add(mapped);
                        }
                    }
                });
            }

            @Override
            public FieldVisitor visitField(int acc, String fName, String fDesc, String sig, Object val) {
                return new FieldVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
                        if (desc.equals(SHADOW)) {
                            Spec sp = new Spec(null, fName, fDesc, 'f');
                            touch.accept(exact.apply(sp, chain.resolveSpec('f', fName, fDesc)), "shadow-field " + fName);
                            declCheck.accept(sp, "shadow-field " + fName);
                        }
                        return null;
                    }
                };
            }

            @Override
            public MethodVisitor visitMethod(int acc, String mName, String mDesc, String sig, String[] ex) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String desc, boolean vis) {
                        switch (desc) {
                            case OVERWRITE -> {
                                loadBearing[0] = true;
                                Spec sp = new Spec(null, mName, mDesc, 'm');
                                touch.accept(exact.apply(sp, chain.resolveSpec('m', mName, mDesc)), "overwrite " + mName);
                                declCheck.accept(sp, "overwrite " + mName);
                                return null;
                            }
                            case SHADOW -> {
                                // @Shadow(prefix = ...) ("shadow$" by default) is not part of the target's name
                                Map<String, Object> values = new LinkedHashMap<>();
                                return collector(values, () -> {
                                    String prefix = values.get("prefix") instanceof String p ? p : "shadow$";
                                    String n = mName.startsWith(prefix) ? mName.substring(prefix.length()) : mName;
                                    Spec sp = new Spec(null, n, mDesc, 'm');
                                    touch.accept(exact.apply(sp, chain.resolveSpec('m', n, mDesc)), "shadow " + n);
                                    declCheck.accept(sp, "shadow " + n);
                                });
                            }
                            case ACCESSOR, INVOKER -> {
                                loadBearing[0] = true;
                                Map<String, Object> values = new LinkedHashMap<>();
                                boolean invoker = desc.equals(INVOKER);
                                return collector(values, () -> {
                                    String tgt = values.get("value") instanceof String s && !s.isEmpty() ? s : null;
                                    boolean implicit = tgt == null;
                                    if (implicit) {
                                        // Mixin's own inflection (AccessorInfo): get/is/set name a field,
                                        // call/invoke a method, new/create a constructor
                                        String inflected = MixinBridge.inflect(mName);
                                        tgt = inflected != null ? inflected : mName;
                                    }
                                    Spec sp = parseSpec(rm(refmap, tgt));
                                    if (invoker) {
                                        // the target is matched by the invoker's own descriptor; a
                                        // constructor invoker's with its return type made void
                                        String simple = targets.isEmpty() ? "" : targets.get(0).substring(targets.get(0).lastIndexOf('/') + 1);
                                        String pfx = MixinBridge.inflectPrefix(mName);
                                        boolean factory = sp.name.equals("<init>") || sp.name.equals(simple)
                                            || implicit && ("new".equals(pfx) || "create".equals(pfx)) && simple.equals(MixinBridge.inflectRaw(mName));
                                        Spec ms = factory
                                            ? new Spec(sp.owner, "<init>", mDesc.substring(0, mDesc.indexOf(')') + 1) + "V", 'm')
                                            : new Spec(sp.owner, sp.name, mDesc, 'm');
                                        touch.accept(exact.apply(ms, chain.resolveSpec('m', ms.name, ms.desc)), "accessor " + tgt);
                                        declCheck.accept(ms, "invoker " + tgt);
                                    } else {
                                        // the field's descriptor is the accessor's return type
                                        // (getter) or its single parameter (setter)
                                        Type[] args = Type.getArgumentTypes(mDesc);
                                        String fd = args.length == 1 ? args[0].getDescriptor()
                                            : Type.getReturnType(mDesc).getDescriptor();
                                        Spec fs = new Spec(sp.owner, sp.name, fd, 'f');
                                        touch.accept(exact.apply(fs, chain.resolveSpec('f', fs.name, fs.desc)), "accessor " + tgt);
                                        declCheck.accept(fs, "accessor " + tgt);
                                    }
                                });
                            }
                            default -> {
                                Map<String, Object> values = new LinkedHashMap<>();
                                return collector(values, () -> {
                                    if (!(values.containsKey("method") || values.containsKey("at")
                                          || values.containsKey("constant") || values.containsKey("target"))) {
                                        return; // not an injector-shaped annotation
                                    }
                                    for (Object o : asList(values.get("method"))) {
                                        if (o instanceof String s && !s.equals("*") && !s.startsWith("/")) {
                                            Spec sp = parseSpec(rm(refmap, s));
                                            touch.accept(exact.apply(sp, chain.resolveSpec(sp.kind, sp.name, sp.desc)),
                                                "inject-target " + sp.name);
                                            if (sp.kind == 'm') {
                                                declCheck.accept(sp, "inject-target " + sp.name);
                                            }
                                        }
                                    }
                                    for (Object o : asList(values.get("at"))) {
                                        if (o instanceof Map<?, ?> at && at.get("target") instanceof String s) {
                                            Spec sp = parseSpec(rm(refmap, s));
                                            if (sp.owner != null) {
                                                touch.accept(chain.resolveClass(sp.owner), "at-owner " + sp.owner);
                                            }
                                            touch.accept(sp.owner != null ? exact.apply(sp, chain.resolveSpec(sp.kind, sp.name, sp.desc))
                                                : chain.resolveSpec(sp.kind, sp.name, sp.desc), "at " + sp.name);
                                        }
                                    }
                                });
                            }
                        }
                    }
                };
            }
        };
        new ClassReader(bytes).accept(cv,
            ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);

        // a game class: the packages the stub index reads (StubDiff.index), blaze3d and realmsclient included -- past the
        // wall (and for blaze3d already in 1.21.x intermediary) those are game classes too, and a mixin on one that
        // cannot apply must be stripped like any other (s121f: CbRenderBufferBuilderMixin kept on 26.2's BufferBuilder)
        boolean vanilla = targets.stream().anyMatch(t -> t.startsWith("net/minecraft/") || t.startsWith("com/mojang/blaze3d/")
            || t.startsWith("com/mojang/realmsclient/"));
        String shortName = className.substring(className.lastIndexOf('/') + 1);
        return new MixinInfo(className, config, targets, vanilla, loadBearing[0], worst[0],
            broken.size() > 12 ? broken.subList(0, 12) : broken);
    }

    // ---------------------------------------------------------------- helpers

    private record Spec(String owner, String name, String desc, char kind) {}

    private static Spec parseSpec(String spec) {
        String owner = null;
        if (spec.startsWith("L") && spec.contains(";")) {
            int i = spec.indexOf(';');
            owner = spec.substring(1, i);
            spec = spec.substring(i + 1);
        }
        int p = spec.indexOf('(');
        if (p >= 0) {
            return new Spec(owner, spec.substring(0, p), spec.substring(p), 'm');
        }
        int c = spec.indexOf(':');
        if (c >= 0) {
            return new Spec(owner, spec.substring(0, c), spec.substring(c + 1), 'f');
        }
        return new Spec(owner, spec, null, 'm');
    }

    /** refmap lookup; keys may be descriptor-qualified ("name:Ldesc;" / "name(args)V") */
    private static String rm(Map<String, String> refmap, String s) {
        String direct = refmap.get(s);
        if (direct != null) {
            return direct;
        }
        String base = base(s);
        for (Map.Entry<String, String> e : refmap.entrySet()) {
            if (base(e.getKey()).equals(base)) {
                return e.getValue();
            }
        }
        return s;
    }

    private static String base(String s) {
        int p = s.indexOf('(');
        if (p >= 0) {
            s = s.substring(0, p);
        }
        int c = s.indexOf(':');
        if (c >= 0) {
            s = s.substring(0, c);
        }
        return s;
    }

    @SuppressWarnings("unchecked")
    private static List<Object> asList(Object o) {
        if (o == null) {
            return List.of();
        }
        return o instanceof List ? (List<Object>) o : List.of(o);
    }

    /** generic annotation-value collector; runs onEnd when the annotation closes */
    private static AnnotationVisitor collector(Object container, Runnable onEnd) {
        return new AnnotationVisitor(Opcodes.ASM9) {
            @SuppressWarnings("unchecked")
            private void put(String name, Object value) {
                if (container instanceof Map) {
                    ((Map<String, Object>) container).put(name, value);
                } else {
                    ((List<Object>) container).add(value);
                }
            }

            @Override
            public void visit(String name, Object value) {
                put(name, value);
            }

            @Override
            public void visitEnum(String name, String desc, String value) {
                put(name, value);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String desc) {
                Map<String, Object> sub = new LinkedHashMap<>();
                put(name, sub);
                return collector(sub, null);
            }

            @Override
            public AnnotationVisitor visitArray(String name) {
                List<Object> list = new ArrayList<>();
                put(name, list);
                return collector(list, null);
            }

            @Override
            public void visitEnd() {
                if (onEnd != null) {
                    onEnd.run();
                }
            }
        };
    }
}
