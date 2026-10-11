package top.dext.centurybridge.data;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Interpreter;

/**
 * The registry ids Mojang renamed, read out of the game's own DataFixers: every rename
 * fix (BlockRenameFix, ItemRenameFix, ...) is registered in DataFixers.addFixers with
 * the data version of its schema and a renamer built from literal "old" -> "new"
 * strings. World data is fixed by the game; data packs and resource packs are not --
 * a mod's tag or worldgen file naming minecraft:grass after 1.20.3 names nothing, and a
 * worldgen registry with an unbound reference stops the server. The table this writes
 * is what the resource translation renames by.
 *
 * Read from an unobfuscated (26.x) client jar: names are mojang's, and the newest jar
 * carries every older fix.
 */
public final class IdRenames {

    /** a symbolic value on the analysed stack: a constant, a map, a renamer, a schema */
    static final class V extends BasicValue {
        final Object c;

        V(Type t, Object c) {
            super(t);
            this.c = c;
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof V v && java.util.Objects.equals(v.c, c) && java.util.Objects.equals(v.getType(), getType());
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(getType(), c);
        }
    }

    record Schema(int version) {
    }

    record Renamer(Map<String, String> map) {
    }

    record TypeRef(String name) {
    }

    static final Type OBJ = Type.getObjectType("java/lang/Object");

    static final class Interp extends Interpreter<V> {
        /** another datafix class's static constants (a rename table kept on the fix class) */
        final java.util.function.BiFunction<String, String, Object> statics;
        /** PUTSTATIC values seen while analysing a static initializer */
        final Map<String, Object> putStatics = new LinkedHashMap<>();

        Interp(java.util.function.BiFunction<String, String, Object> statics) {
            super(Opcodes.ASM9);
            this.statics = statics;
        }

        @Override
        public V newValue(Type type) {
            if (type == null) {
                return new V(null, null);
            }
            if (type == Type.VOID_TYPE) {
                return null;
            }
            return new V(type.getSize() == 2 ? Type.LONG_TYPE : OBJ, null);
        }

        @Override
        public V newOperation(AbstractInsnNode insn) {
            switch (insn.getOpcode()) {
                case Opcodes.ICONST_M1, Opcodes.ICONST_0, Opcodes.ICONST_1, Opcodes.ICONST_2, Opcodes.ICONST_3,
                     Opcodes.ICONST_4, Opcodes.ICONST_5:
                    return new V(Type.INT_TYPE, insn.getOpcode() - Opcodes.ICONST_0);
                case Opcodes.BIPUSH, Opcodes.SIPUSH:
                    return new V(Type.INT_TYPE, ((org.objectweb.asm.tree.IntInsnNode) insn).operand);
                case Opcodes.LDC: {
                    Object c = ((LdcInsnNode) insn).cst;
                    if (c instanceof Long || c instanceof Double) {
                        return new V(Type.LONG_TYPE, null);
                    }
                    return new V(OBJ, c);
                }
                case Opcodes.GETSTATIC: {
                    var f = (org.objectweb.asm.tree.FieldInsnNode) insn;
                    Type t = Type.getType(f.desc);
                    if (f.owner.endsWith("/References")) {
                        return new V(OBJ, new TypeRef(f.name));
                    }
                    if (f.owner.startsWith("net/minecraft/util/datafix/") && statics != null) {
                        return new V(t.getSize() == 2 ? Type.LONG_TYPE : OBJ, statics.apply(f.owner, f.name));
                    }
                    return new V(t.getSize() == 2 ? Type.LONG_TYPE : OBJ, null);
                }
                case Opcodes.LCONST_0, Opcodes.LCONST_1, Opcodes.DCONST_0, Opcodes.DCONST_1:
                    return new V(Type.LONG_TYPE, null);
                default:
                    return new V(OBJ, null);
            }
        }

        @Override
        public V copyOperation(AbstractInsnNode insn, V value) {
            return value;
        }

        @Override
        public V unaryOperation(AbstractInsnNode insn, V value) {
            int op = insn.getOpcode();
            if (op == Opcodes.I2L || op == Opcodes.I2D || op == Opcodes.F2L || op == Opcodes.F2D
                    || op == Opcodes.LNEG || op == Opcodes.DNEG || op == Opcodes.L2D || op == Opcodes.D2L) {
                return new V(Type.LONG_TYPE, null);
            }
            if (op == Opcodes.CHECKCAST) {
                return value;
            }
            if (op == Opcodes.GETFIELD) {
                Type t = Type.getType(((org.objectweb.asm.tree.FieldInsnNode) insn).desc);
                return new V(t.getSize() == 2 ? Type.LONG_TYPE : OBJ, null);
            }
            if (op == Opcodes.PUTSTATIC) {
                putStatics.put(((org.objectweb.asm.tree.FieldInsnNode) insn).name, value.c);
                return null;
            }
            if (op == Opcodes.IFEQ || op == Opcodes.IFNE || op == Opcodes.IFLT || op == Opcodes.IFGE
                    || op == Opcodes.IFGT || op == Opcodes.IFLE || op == Opcodes.IFNULL || op == Opcodes.IFNONNULL
                    || op == Opcodes.TABLESWITCH || op == Opcodes.LOOKUPSWITCH || op == Opcodes.PUTSTATIC
                    || op == Opcodes.ATHROW || op == Opcodes.MONITORENTER || op == Opcodes.MONITOREXIT
                    || op >= Opcodes.IRETURN && op <= Opcodes.ARETURN) {
                return null;
            }
            return new V(OBJ, null);
        }

        @Override
        public V binaryOperation(AbstractInsnNode insn, V v1, V v2) {
            int op = insn.getOpcode();
            if (op == Opcodes.LADD || op == Opcodes.LSUB || op == Opcodes.LMUL || op == Opcodes.LDIV
                    || op == Opcodes.DADD || op == Opcodes.DSUB || op == Opcodes.DMUL || op == Opcodes.DDIV
                    || op == Opcodes.LAND || op == Opcodes.LOR || op == Opcodes.LXOR || op == Opcodes.LSHL
                    || op == Opcodes.LSHR || op == Opcodes.LUSHR || op == Opcodes.LREM || op == Opcodes.DREM
                    || op == Opcodes.LALOAD || op == Opcodes.DALOAD) {
                return new V(Type.LONG_TYPE, null);
            }
            if (op >= Opcodes.IF_ICMPEQ && op <= Opcodes.IF_ACMPNE || op == Opcodes.PUTFIELD) {
                return null;
            }
            return new V(OBJ, null);
        }

        @Override
        public V ternaryOperation(AbstractInsnNode insn, V v1, V v2, V v3) {
            return null;
        }

        @Override
        @SuppressWarnings("unchecked")
        public V naryOperation(AbstractInsnNode insn, List<? extends V> values) {
            if (insn instanceof MethodInsnNode m) {
                Type ret = Type.getReturnType(m.desc);
                if (ret == Type.VOID_TYPE) {
                    return null;
                }
                List<Object> args = new ArrayList<>();
                int first = m.getOpcode() == Opcodes.INVOKESTATIC ? 0 : 1;
                for (int i = first; i < values.size(); i++) {
                    args.add(values.get(i).c);
                }
                Object receiver = first == 1 ? values.get(0).c : null;
                Object c = null;
                if ((m.owner.equals("java/util/Map") || m.owner.equals("com/google/common/collect/ImmutableMap"))
                        && m.name.equals("of")) {
                    Map<String, String> map = new LinkedHashMap<>();
                    for (int i = 0; i + 1 < args.size(); i += 2) {
                        if (args.get(i) instanceof String k && args.get(i + 1) instanceof String v) {
                            map.put(k, v);
                        }
                    }
                    c = map;
                } else if (m.owner.equals("com/google/common/collect/ImmutableMap") && m.name.equals("builder")) {
                    c = new LinkedHashMap<String, String>();
                } else if (m.owner.equals("com/google/common/collect/ImmutableMap$Builder")
                        && m.name.equals("put") && receiver instanceof Map<?, ?> b) {
                    Map<String, String> map = new LinkedHashMap<>((Map<String, String>) b);
                    if (args.size() == 2 && args.get(0) instanceof String k && args.get(1) instanceof String v) {
                        map.put(k, v);
                    }
                    c = map;
                } else if (m.owner.equals("com/google/common/collect/ImmutableMap$Builder")
                        && (m.name.equals("build") || m.name.equals("buildOrThrow")) && receiver instanceof Map<?, ?> b) {
                    c = b;
                } else if (m.name.startsWith("createRenamer")) {
                    if (args.size() == 1 && args.get(0) instanceof Map<?, ?> map) {
                        c = new Renamer((Map<String, String>) map);
                    } else if (args.size() == 2 && args.get(0) instanceof String a && args.get(1) instanceof String b2) {
                        c = new Renamer(Map.of(a, b2));
                    }
                } else if (m.name.equals("addSchema") && !args.isEmpty() && args.get(0) instanceof Integer v) {
                    c = new Schema(v);
                }
                return new V(ret.getSize() == 2 ? Type.LONG_TYPE : OBJ, c);
            }
            if (insn instanceof InvokeDynamicInsnNode indy) {
                // a renamer lambda capturing a map (lambda$addFixers$N(Map, String))
                for (V v : values) {
                    if (v.c instanceof Map<?, ?> map) {
                        return new V(OBJ, new Renamer((Map<String, String>) map));
                    }
                }
                return new V(OBJ, null);
            }
            if (insn.getOpcode() == Opcodes.MULTIANEWARRAY) {
                return new V(OBJ, null);
            }
            return new V(OBJ, null);
        }

        @Override
        public void returnOperation(AbstractInsnNode insn, V value, V expected) {
        }

        @Override
        public V merge(V a, V b) {
            if (a == null || b == null) {
                return a == null ? b : a;
            }
            return java.util.Objects.equals(a.c, b.c) && java.util.Objects.equals(a.getType(), b.getType())
                ? a : new V(a.getSize() == 2 ? Type.LONG_TYPE : OBJ, null);
        }
    }

    public static void run(Path jar, Path out) throws IOException, AnalyzerException {
        ClassNode cn = new ClassNode();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            new ClassReader(z.getInputStream(z.getEntry("net/minecraft/util/datafix/DataFixers.class")).readAllBytes())
                .accept(cn, 0);
        }
        MethodNode add = cn.methods.stream().filter(m -> m.name.equals("addFixers")).findFirst().orElseThrow();
        Map<String, Map<String, Object>> staticCache = new java.util.HashMap<>();
        java.util.function.BiFunction<String, String, Object> statics = (owner, field) ->
            staticCache.computeIfAbsent(owner, o -> clinitStatics(jar, o)).get(field);
        Frame<V>[] frames = new Analyzer<>(new Interp(statics)).analyze(cn.name, add);
        JsonArray fixes = new JsonArray();
        for (int i = 0; i < add.instructions.size(); i++) {
            AbstractInsnNode insn = add.instructions.get(i);
            Frame<V> f = frames[i];
            if (f == null || !(insn instanceof MethodInsnNode m) || !m.owner.startsWith("net/minecraft/util/datafix/fixes/")) {
                continue;
            }
            int nargs = Type.getArgumentTypes(m.desc).length;
            int base = f.getStackSize() - nargs;
            Schema schema = null;
            Renamer renamer = null;
            String desc = null;
            String typeRef = null;
            for (int a = 0; a < nargs; a++) {
                Object c = f.getStack(base + a).c;
                if (c instanceof Schema s) {
                    schema = s;
                } else if (c instanceof Renamer r) {
                    renamer = r;
                } else if (c instanceof String s && desc == null) {
                    desc = s;
                } else if (c instanceof TypeRef t) {
                    typeRef = t.name();
                } else if (c instanceof Map<?, ?> map && renamer == null) {
                    @SuppressWarnings("unchecked")
                    Map<String, String> mm = (Map<String, String>) map;
                    renamer = new Renamer(mm); // a fix handed its rename table directly
                }
            }
            if (renamer == null || schema == null) {
                continue;
            }
            JsonObject fix = new JsonObject();
            fix.addProperty("dataVersion", schema.version());
            fix.addProperty("fix", m.owner.substring(m.owner.lastIndexOf('/') + 1));
            if (typeRef != null) {
                fix.addProperty("type", typeRef);
            }
            if (desc != null) {
                fix.addProperty("name", desc);
            }
            JsonObject r = new JsonObject();
            renamer.map().forEach(r::addProperty);
            fix.add("renames", r);
            fixes.add(fix);
        }
        JsonObject root = new JsonObject();
        root.addProperty("source", jar.getFileName().toString());
        root.add("fixes", fixes);
        Files.writeString(out, new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(root) + "\n",
            StandardCharsets.UTF_8);
        System.out.println("id-renames: " + fixes.size() + " rename fixes -> " + out);
    }

    /**
     * The table the resource translation reads: the id renames since the oldest
     * release the chain starts from, and the vanilla tags that vanished (a mod tag
     * still naming one fails to load whole -- required references must resolve),
     * from the vanilla data each release's client jar ships.
     */
    public static void writeTable(Path renamesJson, Path jarsDir, Path javaOut) throws IOException {
        var root = com.google.gson.JsonParser.parseString(Files.readString(renamesJson)).getAsJsonObject();
        int oldest = top.dext.centurybridge.res.Release.DATA_VERSIONS.get(0);
        StringBuilder b = new StringBuilder();
        b.append("package top.dext.centurybridge.res;\n\n");
        b.append("/**\n * GENERATED by `Main gen-renames` from the game's DataFixers and the vanilla data each\n");
        b.append(" * release ships -- do not edit; regenerate.\n */\n");
        b.append("final class IdRenameTable {\n\n");
        b.append("    /** data version, fix, old id, new id: every id rename after ").append(oldest).append(" */\n");
        b.append("    static final String[][] RENAMES = {\n");
        for (var f : root.getAsJsonArray("fixes")) {
            JsonObject o = f.getAsJsonObject();
            int dv = o.get("dataVersion").getAsInt();
            if (dv <= oldest) {
                continue;
            }
            String kind = o.get("fix").getAsString() + (o.has("type") ? "/" + o.get("type").getAsString() : "");
            for (var r : o.getAsJsonObject("renames").entrySet()) {
                if (!r.getKey().contains(":")) {
                    continue; // NBT field renames, not registry ids
                }
                b.append("        {\"").append(dv).append("\", \"").append(kind).append("\", \"").append(r.getKey())
                    .append("\", \"").append(r.getValue().getAsString()).append("\"},\n");
            }
        }
        b.append("    };\n\n");
        List<String> rel = top.dext.centurybridge.res.Release.ALL;
        Map<String, char[]> presence = new java.util.TreeMap<>();
        Map<String, char[]> entryPresence = new java.util.TreeMap<>();
        for (int i = 0; i < rel.size(); i++) {
            String v = rel.get(i);
            Path j = jarsDir.resolve("client-" + v + "-obf.jar");
            if (!Files.exists(j)) {
                j = jarsDir.resolve("client-" + v + ".jar");
            }
            if (!Files.exists(j)) {
                throw new IOException("no client jar for " + v + " in " + jarsDir + ": the tag history needs every release");
            }
            for (String t : vanillaTags(j)) {
                char[] bits = presence.computeIfAbsent(t, k -> "0".repeat(rel.size()).toCharArray());
                bits[i] = '1';
            }
            for (String t : vanillaEntries(j)) {
                char[] bits = entryPresence.computeIfAbsent(t, k -> "0".repeat(rel.size()).toCharArray());
                bits[i] = '1';
            }
        }
        b.append("    /**\n     * data-driven vanilla registry entries (\"worldgen/biome:minecraft:plains\") -> one 0/1 per\n");
        b.append("     * release of Release.ALL: whether that release ships it\n     */\n");
        b.append("    static final String[][] ENTRY_PRESENCE = {\n");
        for (var e : entryPresence.entrySet()) {
            b.append("        {\"").append(e.getKey()).append("\", \"").append(new String(e.getValue())).append("\"},\n");
        }
        b.append("    };\n\n");
        b.append("    /**\n     * vanilla tags not present in every release (\"block:minecraft:tall_flowers\", registry\n");
        b.append("     * names singular) -> one 0/1 per release of Release.ALL: whether that release ships it\n     */\n");
        b.append("    static final String[][] TAG_PRESENCE = {\n");
        for (var e : presence.entrySet()) {
            String bits = new String(e.getValue());
            if (bits.contains("0")) {
                b.append("        {\"").append(e.getKey()).append("\", \"").append(bits).append("\"},\n");
            }
        }
        b.append("    };\n\n    private IdRenameTable() {\n    }\n}\n");
        Files.writeString(javaOut, b.toString(), StandardCharsets.UTF_8);
        System.out.println("id rename table -> " + javaOut);
    }

    /** "block:minecraft:mineable/pickaxe" for every vanilla tag a client jar ships (registry names singular) */
    private static java.util.Set<String> vanillaTags(Path jar) throws IOException {
        java.util.Set<String> out = new java.util.TreeSet<>();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (var en : java.util.Collections.list(z.entries())) {
                String n = en.getName();
                if (!n.startsWith("data/minecraft/tags/") || !n.endsWith(".json")) {
                    continue;
                }
                String rest = n.substring("data/minecraft/tags/".length(), n.length() - 5);
                int slash = rest.indexOf('/');
                String reg = rest.substring(0, slash);
                String path = rest.substring(slash + 1);
                if (reg.equals("worldgen")) {
                    int s2 = path.indexOf('/');
                    reg = "worldgen/" + path.substring(0, s2);
                    path = path.substring(s2 + 1);
                }
                out.add(singular(reg) + ":minecraft:" + path);
            }
        }
        return out;
    }

    /** the vanilla biomes and structures a client jar ships, by their data files */
    private static java.util.Set<String> vanillaEntries(Path jar) throws IOException {
        java.util.Set<String> out = new java.util.TreeSet<>();
        try (ZipFile z = new ZipFile(jar.toFile())) {
            for (var en : java.util.Collections.list(z.entries())) {
                String n = en.getName();
                for (String reg : new String[] {"worldgen/biome", "worldgen/structure"}) {
                    String dir = "data/minecraft/" + reg + "/";
                    if (n.startsWith(dir) && n.endsWith(".json")) {
                        out.add(reg + ":minecraft:" + n.substring(dir.length(), n.length() - 5));
                    }
                }
            }
        }
        return out;
    }

    private static String singular(String reg) {
        return switch (reg) {
            case "blocks" -> "block";
            case "items" -> "item";
            case "entity_types" -> "entity_type";
            case "fluids" -> "fluid";
            case "game_events" -> "game_event";
            case "functions" -> "function";
            default -> reg;
        };
    }

    /** the static fields a class's initializer sets, as far as they are constant maps and strings */
    private static Map<String, Object> clinitStatics(Path jar, String owner) {
        try (ZipFile z = new ZipFile(jar.toFile())) {
            var e = z.getEntry(owner + ".class");
            if (e == null) {
                return Map.of();
            }
            ClassNode cn = new ClassNode();
            new ClassReader(z.getInputStream(e).readAllBytes()).accept(cn, 0);
            for (MethodNode m : cn.methods) {
                if (m.name.equals("<clinit>")) {
                    Interp in = new Interp(null);
                    new Analyzer<>(in).analyze(cn.name, m);
                    return in.putStatics;
                }
            }
        } catch (IOException | AnalyzerException ex) {
            System.out.println("  (could not read " + owner + ": " + ex + ")");
        }
        return Map.of();
    }

    private IdRenames() {
    }
}
