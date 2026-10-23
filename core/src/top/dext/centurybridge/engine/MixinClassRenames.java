package top.dext.centurybridge.engine;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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

    /** a class visitor renaming class names in the strings of Mixin's annotations (upstream of the ClassRemapper) */
    static ClassVisitor annotations(ClassVisitor next, Map<String, String> renames) {
        return new ClassVisitor(Opcodes.ASM9, next) {
            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return wrap(super.visitAnnotation(desc, visible), desc, renames);
            }

            @Override
            public MethodVisitor visitMethod(int acc, String name, String desc, String sig, String[] ex) {
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(acc, name, desc, sig, ex)) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return wrap(super.visitAnnotation(d, visible), d, renames);
                    }
                };
            }

            @Override
            public FieldVisitor visitField(int acc, String name, String desc, String sig, Object value) {
                return new FieldVisitor(Opcodes.ASM9, super.visitField(acc, name, desc, sig, value)) {
                    @Override
                    public AnnotationVisitor visitAnnotation(String d, boolean visible) {
                        return wrap(super.visitAnnotation(d, visible), d, renames);
                    }
                };
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

    /** a refmap's values renamed (keys kept); null when nothing changed */
    static byte[] refmap(byte[] json, Map<String, String> renames) {
        JsonObject root;
        try {
            root = GSON.fromJson(new String(json, StandardCharsets.UTF_8), JsonObject.class);
        } catch (RuntimeException ex) {
            return null;
        }
        if (root == null || !values(root, renames)) {
            return null;
        }
        return GSON.toJson(root).getBytes(StandardCharsets.UTF_8);
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
