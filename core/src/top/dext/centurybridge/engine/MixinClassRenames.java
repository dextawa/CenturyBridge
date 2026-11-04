package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * A step's classRenames carried into the strings a mixin names classes by. The class rewrite renames every
 * class a mod's bytecode references (ASM's ClassRemapper), but a mixin also names its targets in strings
 * the remapper never reads: injector selectors ("Lowner;name(desc)" or "name(desc)"), @At targets, @Mixin
 * targets="...", @Desc owners, and -- for a mod built with refmaps -- the refmap's values, which Mixin reads
 * in place of the annotation strings. Left as they were, a mixin on a class the step renames (26.2's package
 * moves, a native return, a facade) aims at a name W does not have: an owner-qualified selector or @At
 * target no longer matches and the injector fails.
 *
 * Renamed: every "L<class>;" token in a string, and a string that is a class name itself (internal or
 * dotted). Only Mixin's and MixinExtras' annotations are read, and only a refmap's values (its keys are
 * the mod's own development names).
 *
 * Not renamed: a class the step renames to one of CenturyBridge's own (a facade) while W still declares a
 * class of that name, in the injector and member strings of a mixin aimed at vanilla code. Those strings
 * name what vanilla's bytecode references (an @At call on W's RecipeBookComponent, a selector's parameter
 * type), and vanilla never references a facade -- only converted mods do. A mixin aimed at the facade
 * itself keeps every rename.
 */
final class MixinClassRenames {
    private static final Gson GSON = new Gson();
    private static final Pattern CLASS_REF = Pattern.compile("L([\\w/$]+);");

    private MixinClassRenames() {
    }

    static String rename(String s, Map<String, String> renames) {
        String whole = renames.get(s.replace('.', '/'));
        if (whole != null) {
            return s.indexOf('.') >= 0 ? whole.replace('/', '.') : whole;
        }
        if (s.indexOf(';') < 0) {
            return s;
        }
        Matcher m = CLASS_REF.matcher(s);
        StringBuilder sb = null;
        int last = 0;
        while (m.find()) {
            String to = renames.get(m.group(1));
            if (to == null) {
                continue;
            }
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append(s, last, m.start(1)).append(to);
            last = m.end(1);
        }
        return sb == null ? s : sb.append(s, last, s.length()).toString();
    }

    private static boolean mixinAnnotation(String desc) {
        return desc.startsWith("Lorg/spongepowered/asm/mixin/") || desc.startsWith("Lcom/llamalad7/mixinextras/");
    }

    private static final String MIXIN = "Lorg/spongepowered/asm/mixin/Mixin;";

    /** a rename into CenturyBridge's own classes (a facade) */
    static boolean toLayer(String to) {
        return to != null && to.startsWith("top/dext/");
    }

    /** the renames that apply to strings naming vanilla code: no facade rename of a class W still declares */
    static Map<String, String> forVanilla(Map<String, String> renames, Predicate<String> wDeclares) {
        Map<String, String> out = new HashMap<>();
        renames.forEach((from, to) -> {
            if (!toLayer(to) || !wDeclares.test(from)) {
                out.put(from, to);
            }
        });
        return out;
    }

    /**
     * marks a method MixinBridge wrote spelled in W's own types (a W-shaped @Inject front of a target whose descriptor
     * names a class the step renames onto a layer facade while W still declares it under that name): its descriptor,
     * frames and calls into W code take {@link #forVanilla} only; the positions naming the mod's handler types were
     * written already renamed (Chain.renamedDesc)
     */
    static final String W_TYPED = "Ltop/dext/centurybridge/WTyped;";

    /**
     * The class rewrite's remapper: every method by {@code renames}, except a {@link #W_TYPED} one, which takes
     * {@link #forVanilla} -- classRenames are meant for converted code, never for what names W's code (Mixin matches a
     * handler's descriptor against the target's)
     */
    static ClassVisitor remapper(ClassVisitor next, Map<String, String> renames, Predicate<String> wDeclares, byte[] bytes) {
        java.util.Set<String> wTyped = wTypedMethods(bytes);
        org.objectweb.asm.commons.Remapper full = new org.objectweb.asm.commons.SimpleRemapper(renames);
        if (wTyped.isEmpty()) {
            return new org.objectweb.asm.commons.ClassRemapper(next, full);
        }
        org.objectweb.asm.commons.Remapper w = new org.objectweb.asm.commons.SimpleRemapper(forVanilla(renames, wDeclares));
        return new org.objectweb.asm.commons.ClassRemapper(Opcodes.ASM9, next, full) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String sig, String[] ex) {
                if (!wTyped.contains(name + desc)) {
                    return super.visitMethod(access, name, desc, sig, ex);
                }
                MethodVisitor mv = cv.visitMethod(access, name, w.mapMethodDesc(desc), w.mapSignature(sig, false),
                    ex == null ? null : w.mapTypes(ex));
                return mv == null ? null : new org.objectweb.asm.commons.MethodRemapper(Opcodes.ASM9, mv, w) { };
            }
        };
    }

    /** name+desc of the class's {@link #W_TYPED} methods */
    static java.util.Set<String> wTypedMethods(byte[] bytes) {
        java.util.Set<String> out = new java.util.HashSet<>();
        if (!new String(bytes, StandardCharsets.ISO_8859_1).contains(W_TYPED)) {
            return out;
        }
        org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
        new org.objectweb.asm.ClassReader(bytes).accept(cn, org.objectweb.asm.ClassReader.SKIP_CODE);
        for (org.objectweb.asm.tree.MethodNode m : cn.methods) {
            if (m.invisibleAnnotations != null && m.invisibleAnnotations.stream().anyMatch(a -> a.desc.equals(W_TYPED))) {
                out.add(m.name + m.desc);
            }
        }
        return out;
    }

    /** does a mixin aimed at these classes (as written, or already renamed) aim at a facade */
    static boolean facadeMixin(Iterable<String> targets, Map<String, String> renames) {
        for (String t : targets) {
            String c = t.replace('.', '/');
            if (toLayer(c) || toLayer(renames.get(c))) {
                return true;
            }
        }
        return false;
    }

    /**
     * a class visitor renaming class names in the strings of Mixin's annotations (upstream of the
     * ClassRemapper); a mixin aimed at vanilla code (read off its @Mixin, which ASM visits before the
     * members) gets {@link #forVanilla} in its members' annotations
     */
    static ClassVisitor annotations(ClassVisitor next, Map<String, String> renames, Predicate<String> wDeclares) {
        return new ClassVisitor(Opcodes.ASM9, next) {
            final java.util.List<String> targets = new java.util.ArrayList<>();
            Map<String, String> members;

            Map<String, String> members() {
                if (members == null) {
                    members = targets.isEmpty() || facadeMixin(targets, renames) ? renames : forVanilla(renames, wDeclares);
                }
                return members;
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationVisitor av = wrap(super.visitAnnotation(desc, visible), desc, renames);
                return MIXIN.equals(desc) ? targets(av, targets) : av;
            }

            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                Map<String, String> r = members();
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(acc, name, desc, sig, ex)) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return wrap(super.visitAnnotation(d, visible), d, r);
                    }
                };
            }

            @Override
            public FieldVisitor visitField(int acc, String name, String desc, String sig, Object value) {
                Map<String, String> r = members();
                return new FieldVisitor(Opcodes.ASM9, super.visitField(acc, name, desc, sig, value)) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return wrap(super.visitAnnotation(d, visible), d, r);
                    }
                };
            }
        };
    }

    /**
     * Mixins whose target the step's class renames move into a class W adds in its place
     * ({@link Chain#mixinFollow}): their @Mixin target and their annotations' and refmap tables' class
     * names renamed that way before MixinBridge, repair and triage judge them, so they are judged on the
     * class the converted mixin lands on. Only the annotations and refmap values: the code keeps V's
     * names for the class rewrite, whose wiring is keyed on them. Returns the mixins moved.
     */
    static java.util.List<String> followTargets(Map<String, byte[]> entries, Map<String, String> follow, Map<String, String> all) {
        java.util.List<String> moved = new java.util.ArrayList<>();
        if (follow.isEmpty()) {
            return moved;
        }
        java.util.Set<String> movedSet = new java.util.HashSet<>();
        java.util.Set<String> facadeSet = new java.util.HashSet<>();
        for (Map.Entry<String, byte[]> e : new java.util.ArrayList<>(entries.entrySet())) {
            if (!e.getKey().endsWith(".class")) {
                continue;
            }
            org.objectweb.asm.tree.ClassNode cn = new org.objectweb.asm.tree.ClassNode();
            try {
                new org.objectweb.asm.ClassReader(e.getValue()).accept(cn, org.objectweb.asm.ClassReader.SKIP_CODE);
            } catch (RuntimeException ex) {
                continue;
            }
            java.util.List<String> ts = MixinRepair.mixinTargets(cn, null);
            if (ts.isEmpty() || ts.stream().noneMatch(t -> follow.containsKey(t.replace('.', '/')))) {
                continue;
            }
            org.objectweb.asm.ClassReader r = new org.objectweb.asm.ClassReader(e.getValue());
            org.objectweb.asm.ClassWriter w = new org.objectweb.asm.ClassWriter(0);
            // onto a facade: every name as the class rewrite will spell it (the facade's code names facades);
            // onto a vanilla class: the followed renames, as V's code is read (MixinRepair.aliases)
            boolean facade = facadeMixin(ts, follow);
            r.accept(annotations(mixinTypes(w, follow), facade ? all : follow, c -> false), 0);
            entries.put(e.getKey(), w.toByteArray());
            String cls = e.getKey().substring(0, e.getKey().length() - ".class".length());
            moved.add(cls);
            movedSet.add(cls);
            if (facade) {
                facadeSet.add(cls);
            }
        }
        if (moved.isEmpty()) {
            return moved;
        }
        for (Map.Entry<String, byte[]> e : new java.util.ArrayList<>(entries.entrySet())) {
            if (e.getKey().endsWith(".json") && e.getKey().contains("refmap")) {
                byte[] rew = refmap(e.getValue(), Map.of(), m -> facadeSet.contains(m) ? all : movedSet.contains(m) ? follow : Map.of());
                if (rew != null) {
                    entries.put(e.getKey(), rew);
                }
            }
        }
        return moved;
    }

    /** the class literals of a class's @Mixin renamed (the rest of the class passed on as it is) */
    private static ClassVisitor mixinTypes(ClassVisitor next, Map<String, String> renames) {
        return new ClassVisitor(Opcodes.ASM9, next) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                AnnotationVisitor av = super.visitAnnotation(desc, visible);
                return av == null || !MIXIN.equals(desc) ? av : types(av, renames);
            }
        };
    }

    private static AnnotationVisitor types(AnnotationVisitor av, Map<String, String> renames) {
        return new AnnotationVisitor(Opcodes.ASM9, av) {
            @Override
            public void visit(String name, Object value) {
                if (value instanceof Type t && t.getSort() == Type.OBJECT && renames.containsKey(t.getInternalName())) {
                    value = Type.getObjectType(renames.get(t.getInternalName()));
                }
                super.visit(name, value);
            }

            @Override
            public AnnotationVisitor visitArray(String name) {
                AnnotationVisitor n = super.visitArray(name);
                return n == null ? null : types(n, renames);
            }
        };
    }

    /** reads a @Mixin's value classes and targets strings into out, passing everything on */
    private static AnnotationVisitor targets(AnnotationVisitor av, java.util.List<String> out) {
        return new AnnotationVisitor(Opcodes.ASM9, av) {
            @Override
            public AnnotationVisitor visitArray(String name) {
                AnnotationVisitor n = super.visitArray(name);
                if (!"value".equals(name) && !"targets".equals(name)) {
                    return n;
                }
                return new AnnotationVisitor(Opcodes.ASM9, n) {
                    @Override
                    public void visit(String nm, Object value) {
                        if (value instanceof Type t) {
                            out.add(t.getInternalName());
                        } else if (value instanceof String s) {
                            out.add(s);
                        }
                        super.visit(nm, value);
                    }
                };
            }

            @Override
            public void visit(String name, Object value) {
                if (value instanceof Type t) {
                    out.add(t.getInternalName());
                } else if (("targets".equals(name) || "value".equals(name)) && value instanceof String s) {
                    out.add(s);
                }
                super.visit(name, value);
            }
        };
    }

    private static AnnotationVisitor wrap(AnnotationVisitor av, String desc, Map<String, String> renames) {
        return av == null || !mixinAnnotation(desc) ? av : strings(av, expression(desc) ? vanillaOnly(renames) : renames);
    }

    /** MixinExtras' @Expression / @Definition(s): what they name is what vanilla's body references */
    static boolean expression(String desc) {
        return desc.startsWith("Lcom/llamalad7/mixinextras/expression/");
    }

    /**
     * the renames a MixinExtras @Definition takes: a definition names a member or type vanilla's body references, and
     * vanilla never references CenturyBridge's classes, so no rename into a facade or layer class -- whether or not W
     * still declares the class (an @At / selector keeps a facade rename of a class W lost: the mod's own code calls it)
     */
    static Map<String, String> vanillaOnly(Map<String, String> renames) {
        Map<String, String> out = new HashMap<>();
        renames.forEach((from, to) -> {
            if (!toLayer(to)) {
                out.put(from, to);
            }
        });
        return out;
    }

    /** every string value of an annotation, its arrays and nested annotations (@At, @Slice, @Desc) */
    private static AnnotationVisitor strings(AnnotationVisitor av, Map<String, String> renames) {
        return new AnnotationVisitor(Opcodes.ASM9, av) {
            @Override
            public void visit(String name, Object value) {
                super.visit(name, value instanceof String s ? rename(s, renames) : value);
            }

            @Override
            public AnnotationVisitor visitAnnotation(String name, String desc) {
                AnnotationVisitor n = super.visitAnnotation(name, desc);
                return n == null ? null : strings(n, expression(desc) ? vanillaOnly(renames) : renames);
            }

            @Override
            public AnnotationVisitor visitArray(String name) {
                AnnotationVisitor n = super.visitArray(name);
                return n == null ? null : strings(n, renames);
            }
        };
    }

    /**
     * a refmap's values renamed (keys kept); null when nothing changed. Each mixin's table (under
     * "mappings" and under every "data" environment) takes perMixin's renames for that mixin class.
     */
    static byte[] refmap(byte[] json, Map<String, String> renames, Function<String, Map<String, String>> perMixin) {
        JsonObject root;
        try {
            root = GSON.fromJson(new String(json, StandardCharsets.UTF_8), JsonObject.class);
        } catch (RuntimeException ex) {
            return null;
        }
        if (root == null) {
            return null;
        }
        boolean changed = false;
        for (Map.Entry<String, JsonElement> e : root.entrySet()) {
            if (!e.getValue().isJsonObject()) {
                continue;
            }
            if (e.getKey().equals("mappings")) {
                changed |= tables(e.getValue().getAsJsonObject(), perMixin);
            } else if (e.getKey().equals("data")) {
                for (Map.Entry<String, JsonElement> env : e.getValue().getAsJsonObject().entrySet()) {
                    if (env.getValue().isJsonObject()) {
                        changed |= tables(env.getValue().getAsJsonObject(), perMixin);
                    }
                }
            } else {
                changed |= values(e.getValue().getAsJsonObject(), renames);
            }
        }
        return changed ? GSON.toJson(root).getBytes(StandardCharsets.UTF_8) : null;
    }

    private static boolean tables(JsonObject byMixin, Function<String, Map<String, String>> perMixin) {
        boolean changed = false;
        for (Map.Entry<String, JsonElement> t : byMixin.entrySet()) {
            if (t.getValue().isJsonObject()) {
                changed |= values(t.getValue().getAsJsonObject(), perMixin.apply(t.getKey()));
            }
        }
        return changed;
    }

    private static boolean values(JsonObject o, Map<String, String> renames) {
        boolean changed = false;
        for (Map.Entry<String, JsonElement> e : o.entrySet()) {
            JsonElement v = e.getValue();
            if (v.isJsonObject()) {
                changed |= values(v.getAsJsonObject(), renames);
            } else if (v.isJsonPrimitive() && v.getAsJsonPrimitive().isString()) {
                String s = v.getAsString();
                String r = rename(s, renames);
                if (!r.equals(s)) {
                    e.setValue(new JsonPrimitive(r));
                    changed = true;
                }
            }
        }
        return changed;
    }
}
