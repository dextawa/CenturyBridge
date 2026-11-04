package top.dext.centurybridge.engine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import top.dext.centurybridge.data.TinyMappings;

/**
 * smixin20 (Kilt loss 6): the local variable table the game jar carries, as Mixin reads it. Mojang's obfuscated
 * client jars keep a LocalVariableTable on every method, and Mixin's LocalVariableDiscriminator (MixinExtras @Local,
 * LocalCapture) types each local by that table's declared type -- not by the verifier's type -- matching with
 * Type.equals. The intermediary code jars the engine analyses are written without debug attributes, so their locals
 * carry only the verifier's types (1.20.1 DispenseItemBehavior$10: slot 3 is ServerLevel by the verifier, Level by
 * the table, and Kilt's @Local Level binds there). This reads the table from the obfuscated jar: the method is the
 * one of the class (through the intermediary tiny's class names) whose descriptor, remapped, is the analysed
 * method's and whose instructions have the same opcodes in the same order -- the same compiled body; anything else
 * answers nothing (the caller keeps the verifier's types).
 */
public final class GameLvt {
    private final ZipFile obf;
    private final Map<String, String> toObf = new HashMap<>();
    private final Map<String, String> toInter;
    private final Map<String, ClassNode> classes = new HashMap<>();
    private final Map<MethodNode, Table> tables = java.util.Collections.synchronizedMap(new java.util.IdentityHashMap<>());
    private static final Table NONE = new Table(List.of());

    private GameLvt(ZipFile obf, Map<String, String> toInter) {
        this.obf = obf;
        this.toInter = toInter;
        toInter.forEach((o, i) -> toObf.put(i, o));
    }

    /**
     * the table source for an intermediary code jar data/jars/client-V-intermediary-code.jar: data/jars/client-V-obf.jar
     * with data/mappings/intermediary-V.tiny; null for any other jar (a 26.x jar keeps its own table) or when absent
     */
    public static GameLvt forCodeJar(Path codeJar) {
        String n = codeJar.getFileName().toString();
        if (!n.startsWith("client-") || !n.endsWith("-intermediary-code.jar") || codeJar.getParent() == null
                || codeJar.getParent().getParent() == null) {
            return null;
        }
        String v = n.substring("client-".length(), n.length() - "-intermediary-code.jar".length());
        Path data = codeJar.getParent().getParent();
        // the obfuscated jar: data/jars, or the release's runtime (bootstrap_client.py keeps it there);
        // -Dcb.gameObf.<V> / -Dcb.gameTiny.<V> name them explicitly
        List<Path> obfs = new ArrayList<>();
        List<Path> tinies = new ArrayList<>();
        if (System.getProperty("cb.gameObf." + v) != null) {
            obfs.add(Path.of(System.getProperty("cb.gameObf." + v)));
        }
        if (System.getProperty("cb.gameTiny." + v) != null) {
            tinies.add(Path.of(System.getProperty("cb.gameTiny." + v)));
        }
        obfs.add(codeJar.getParent().resolve("client-" + v + "-obf.jar"));
        obfs.add(data.resolve("runtime/client-" + v + "/client-" + v + "-obf.jar"));
        String dir = System.getProperty("cb.gameTinyDir", System.getenv("CB_GAME_TINY_DIR"));
        if (dir != null) {
            tinies.add(Path.of(dir).resolve("intermediary-" + v + ".tiny"));
        }
        tinies.add(data.resolve("mappings/intermediary-" + v + ".tiny"));
        Path obf = obfs.stream().filter(Files::exists).findFirst().orElse(null);
        Path tiny = tinies.stream().filter(Files::exists).findFirst().orElse(null);
        if (obf == null || tiny == null) {
            System.out.println("game LVT " + v + ": none (obf " + obf + ", tiny " + tiny + "): @Local judged on verifier types");
            return null;
        }
        try {
            return open(obf, tiny);
        } catch (IOException ex) {
            return null;
        }
    }

    /** the release's table source, or null when its obfuscated jar or intermediary tiny is absent */
    public static GameLvt open(Path obfJar, Path tiny) throws IOException {
        if (!Files.exists(obfJar) || !Files.exists(tiny)) {
            return null;
        }
        return new GameLvt(new ZipFile(obfJar.toFile()), TinyMappings.loadObf(tiny).classes);
    }

    /** the declared locals of one method: each entry's type in intermediary names, its range in real-opcode ordinals */
    record Entry(int slot, Type type, int from, int to) {
    }

    record Table(List<Entry> entries) {
        /** the type the table declares for slot at the real instruction of ordinal ord, or null */
        Type at(int slot, int ord) {
            for (Entry e : entries) {
                if (e.slot() == slot && e.from() <= ord && ord < e.to()) {
                    return e.type();
                }
            }
            return null;
        }
    }

    /** the table of m (a method of the intermediary class owner, as the code jar has it), or null */
    Table of(String owner, MethodNode m) {
        Table t = tables.computeIfAbsent(m, k -> build(owner, m));
        return t == NONE ? null : t;
    }

    private synchronized ClassNode obfClass(String o) {
        return classes.computeIfAbsent(o, x -> {
            try {
                ZipEntry e = obf.getEntry(x + ".class");
                if (e == null) {
                    return null;
                }
                ClassNode cn = new ClassNode();
                new ClassReader(obf.getInputStream(e).readAllBytes()).accept(cn, ClassReader.SKIP_FRAMES);
                return cn;
            } catch (IOException ex) {
                return null;
            }
        });
    }

    private Table build(String owner, MethodNode m) {
        String o = toObf.get(owner);
        ClassNode cn = o == null ? null : obfClass(o);
        if (cn == null || m.instructions == null) {
            return NONE;
        }
        List<Integer> ops = opcodes(m);
        MethodNode hit = null;
        for (MethodNode om : cn.methods) {
            if ((om.access & Opcodes.ACC_STATIC) != (m.access & Opcodes.ACC_STATIC) || !remap(om.desc).equals(m.desc)
                    || !opcodes(om).equals(ops)) {
                continue;
            }
            if (hit != null) {
                return NONE; // two bodies alike: not told apart
            }
            hit = om;
        }
        if (hit == null || hit.localVariables == null || hit.localVariables.isEmpty()) {
            return NONE;
        }
        List<Entry> out = new ArrayList<>();
        for (LocalVariableNode lv : hit.localVariables) {
            out.add(new Entry(lv.index, Type.getType(remap(lv.desc)), ordinal(hit, hit.instructions.indexOf(lv.start)),
                ordinal(hit, hit.instructions.indexOf(lv.end))));
        }
        return new Table(out);
    }

    /** the number of real instructions before index i of m's instruction list (the ordinal of the next one) */
    static int ordinal(MethodNode m, int i) {
        int n = 0;
        AbstractInsnNode[] a = m.instructions.toArray();
        for (int k = 0; k < i && k < a.length; k++) {
            if (a[k].getOpcode() >= 0) {
                n++;
            }
        }
        return n;
    }

    private static List<Integer> opcodes(MethodNode m) {
        List<Integer> out = new ArrayList<>();
        if (m.instructions != null) {
            for (AbstractInsnNode n : m.instructions) {
                if (n.getOpcode() >= 0) {
                    out.add(n.getOpcode());
                }
            }
        }
        return out;
    }

    private String remap(String desc) {
        StringBuilder out = new StringBuilder(desc.length());
        for (int i = 0; i < desc.length(); ) {
            char ch = desc.charAt(i);
            if (ch == 'L') {
                int j = desc.indexOf(';', i);
                String name = desc.substring(i + 1, j);
                out.append('L').append(toInter.getOrDefault(name, name)).append(';');
                i = j + 1;
            } else {
                out.append(ch);
                i++;
            }
        }
        return out.toString();
    }
}
