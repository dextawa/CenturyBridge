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
        return av == null || !mixinAnnotation(desc) ? av : strings(av, renames);
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
                return n == null ? null : strings(n, renames);
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
