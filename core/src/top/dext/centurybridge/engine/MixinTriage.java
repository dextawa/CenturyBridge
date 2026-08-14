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
            for (String b : broken) {
                if (b.startsWith("L3")) {
                    return true;
                }
                if (b.startsWith("L2") && (b.contains("inject-target") || b.contains(": at ")
                        || b.contains("at-owner"))) {
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

        byte[] fmjBytes = entries.get("fabric.mod.json");
        List<MixinInfo> out = new ArrayList<>();
        if (fmjBytes == null) {
            return new Result(out, configOfClass);
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

        for (Map.Entry<String, String> e : configOfClass.entrySet()) {
            byte[] bytes = entries.get(e.getKey() + ".class");
            if (bytes != null) {
                out.add(analyze(bytes, e.getKey(), e.getValue(),
                    refmap.getOrDefault(e.getKey(), Map.of()), chain));
            }
        }
        return new Result(out, configOfClass);
    }

    // ---------------------------------------------------------------- analysis

    private static MixinInfo analyze(byte[] bytes, String className, String config,
                                     Map<String, String> refmap, Chain chain) {
        List<String> targets = new ArrayList<>();
        List<String> broken = new ArrayList<>();
        boolean[] loadBearing = {false};
        Chain.Issue[] worst = {Chain.Issue.OK};

        java.util.function.BiConsumer<Chain.Issue, String> touch = (issue, what) -> {
            if (issue.level() != Chain.Level.OK) {
                broken.add(issue.level() + "@" + issue.boundary() + ": " + what);
            }
            worst[0] = worst[0].worst(issue);
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
                            touch.accept(chain.resolveSpec('f', fName, fDesc), "shadow-field " + fName);
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
                                touch.accept(chain.resolveSpec('m', mName, mDesc), "overwrite " + mName);
                                return null;
                            }
                            case SHADOW -> {
                                touch.accept(chain.resolveSpec('m', mName, mDesc), "shadow " + mName);
                                return null;
                            }
                            case ACCESSOR, INVOKER -> {
                                loadBearing[0] = true;
                                Map<String, Object> values = new LinkedHashMap<>();
                                boolean invoker = desc.equals(INVOKER);
                                return collector(values, () -> {
                                    String tgt = values.get("value") instanceof String s && !s.isEmpty() ? s : null;
                                    if (tgt == null) {
                                        Matcher m = IMPLICIT_ACCESSOR.matcher(mName);
                                        tgt = m.matches()
                                            ? Character.toLowerCase(m.group(1).charAt(0)) + m.group(1).substring(1)
                                            : mName;
                                    }
                                    Spec sp = parseSpec(rm(refmap, tgt));
                                    touch.accept(chain.resolveSpec(invoker ? 'm' : 'f', sp.name, sp.desc),
                                        "accessor " + tgt);
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
                                            touch.accept(chain.resolveSpec(sp.kind, sp.name, sp.desc),
                                                "inject-target " + sp.name);
                                        }
                                    }
                                    for (Object o : asList(values.get("at"))) {
                                        if (o instanceof Map<?, ?> at && at.get("target") instanceof String s) {
                                            Spec sp = parseSpec(rm(refmap, s));
                                            if (sp.owner != null) {
                                                touch.accept(chain.resolveClass(sp.owner), "at-owner " + sp.owner);
                                            }
                                            touch.accept(chain.resolveSpec(sp.kind, sp.name, sp.desc), "at " + sp.name);
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

        boolean vanilla = targets.stream().anyMatch(t -> t.startsWith("net/minecraft/"));
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
