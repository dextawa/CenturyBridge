package top.dext.centurybridge.data;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import org.objectweb.asm.Opcodes;

/**
 * Access wideners and class tweakers, read and applied the way Fabric Loader 0.19 does it
 * at runtime (its bundled class-tweaker: ClassTweakerReaderImpl, AccessWidenerImpl,
 * AccessUtils, AccessWidenerClassVisitor, MinecraftGameProvider.getBuiltinTransforms --
 * each rule below was read off the loader's bytecode):
 *
 *  - every loaded mod's file applies to the whole game, "transitive-" or not: Fabric API's
 *    modules', CenturyBridge's, each mod's own;
 *  - only game classes are tweaked: net.minecraft.*, com.mojang.blaze3d.*,
 *    com.mojang.minecraft.*, com.mojang.rubydung.*, the default package. An entry naming
 *    anything else -- com.mojang.realmsclient, com.mojang.math -- is read and never applied;
 *  - accessible: class/field public; method public, and final too if it was a private
 *    instance method (so it cannot start overriding anything). extendable: class public and
 *    not final, its PermittedSubclasses dropped; method protected unless public, not final.
 *    Both on a method: public, not final. mutable: field not final (a static interface
 *    field excepted);
 *  - a member made accessible makes its class accessible; a method made extendable makes
 *    its class extendable; an accessible record class gets a public canonical constructor;
 *  - inject-interface (class tweaker v1+) adds the interface to the class;
 *  - a file that does not parse, or names another namespace than the runtime's, stops the
 *    game at boot ("Failed to read classTweaker file from mod X").
 */
public final class AwRules {

    public static final int ACCESSIBLE = 1;
    public static final int EXTENDABLE = 2;
    public static final int MUTABLE = 4;

    private static final Pattern TOKENS = Pattern.compile("[ \\t]+");

    private final Map<String, Integer> classes = new HashMap<>();
    /** owner -> name + desc -> bits */
    private final Map<String, Map<String, Integer>> methods = new HashMap<>();
    /** owner -> name + ":" + desc -> bits */
    private final Map<String, Map<String, Integer>> fields = new HashMap<>();
    /** target -> interfaces injected into it */
    private final Map<String, Set<String>> injected = new HashMap<>();

    /** the classes the loader runs its class tweaks over (MinecraftGameProvider.getBuiltinTransforms) */
    public static boolean tweakable(String internalName) {
        return internalName.startsWith("net/minecraft/") || internalName.startsWith("com/mojang/blaze3d/")
            || internalName.startsWith("com/mojang/minecraft/") || internalName.startsWith("com/mojang/rubydung/")
            || internalName.indexOf('/') < 0;
    }

    public static List<String> lines(String text) {
        return new BufferedReader(new StringReader(text)).lines().toList();
    }

    /**
     * Reads one file as ClassTweakerReaderImpl does: null if the loader accepts it, else the
     * error it stops the game with. {@code runtimeNamespace} null skips the namespace check.
     * Lines before an error are kept (the game does not start either way).
     */
    public String read(List<String> lines, String runtimeNamespace) {
        if (lines.isEmpty()) {
            return "empty file (no header)";
        }
        String[] h = lines.get(0).split("\\s+");
        if (h.length != 3 || !(h[0].equals("accessWidener") || h[0].equals("classTweaker"))) {
            return "line 1: Invalid access widener file header. Expected: 'classTweaker <version> <namespace>'";
        }
        int version = h[1].equals("v1") ? 1 : h[1].equals("v2") ? 2 : 0;
        if (version == 0) {
            return "line 1: Unsupported " + (h[0].equals("accessWidener") ? "access widener" : "class tweaker") + " format: " + h[1];
        }
        if (h[0].equals("classTweaker")) {
            version += 2; // the reader numbers class tweaker v1/v2 as 3/4
        }
        if (runtimeNamespace != null && !h[2].equals(runtimeNamespace)) {
            return "line 1: Namespace (" + h[2] + ") does not match current runtime namespace (" + runtimeNamespace + ")";
        }
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int hash = line.indexOf('#');
            if (hash >= 0) {
                line = line.substring(0, hash);
                if (version <= 1) {
                    line = line.trim();
                }
            }
            if (line.isEmpty()) {
                continue;
            }
            String at = "line " + (i + 1) + ": ";
            if (Character.isWhitespace(line.codePointAt(0))) {
                return at + "Leading whitespace is not allowed";
            }
            List<String> t = Arrays.asList(TOKENS.split(line));
            String kw = t.get(0);
            if (version >= 3 && (kw.equals("inject-interface") || kw.equals("transitive-inject-interface"))) {
                if (t.size() != 3) {
                    return at + "Expected (inject-interface <className> <interfaceName>) got (" + line + ")";
                }
                String itf = t.get(2);
                injected.computeIfAbsent(t.get(1), k -> new LinkedHashSet<>())
                    .add(itf.contains("<") ? itf.substring(0, itf.indexOf('<')) : itf);
                continue;
            }
            if (version >= 4 && (kw.equals("extend-enum") || kw.equals("transitive-extend-enum"))) {
                if (t.size() != 3) {
                    return at + "Expected (extend-enum <className> <constantName>) got (" + line + ")";
                }
                continue; // adds an enum constant: no access consequence
            }
            if (version >= 2 && kw.startsWith("transitive-")) {
                kw = kw.substring("transitive-".length());
            }
            int bit = switch (kw) {
                case "accessible" -> ACCESSIBLE;
                case "extendable" -> EXTENDABLE;
                case "mutable" -> MUTABLE;
                default -> 0;
            };
            if (bit == 0) {
                return at + "Unknown access type: " + kw;
            }
            if (t.size() < 2) {
                return at + "Expected <class|field|method> following " + t.get(0);
            }
            String type = t.get(1);
            int want = type.equals("class") ? 3 : 5;
            if (!type.equals("class") && !type.equals("field") && !type.equals("method")) {
                return at + "Unsupported type: '" + type + "'";
            }
            if (t.size() != want) {
                return at + "Expected (<access> " + type + " <className>" + (want == 5 ? " <name> <desc>" : "") + ") got (" + line + ")";
            }
            String owner = t.get(2);
            if (owner.contains(".")) {
                return at + "Class-names must be specified as a/b/C, not a.b.C, but found: " + owner;
            }
            switch (type) {
                case "class" -> {
                    if (bit == MUTABLE) {
                        return at + "Classes cannot be made mutable";
                    }
                    classes.merge(owner, bit, (a, b) -> a | b);
                }
                case "field" -> {
                    if (bit == EXTENDABLE) {
                        return at + "Fields cannot be made extendable";
                    }
                    fields.computeIfAbsent(owner, k -> new HashMap<>()).merge(t.get(3) + ":" + t.get(4), bit, (a, b) -> a | b);
                    if (bit == ACCESSIBLE) {
                        classes.merge(owner, ACCESSIBLE, (a, b) -> a | b);
                    }
                }
                default -> {
                    if (bit == MUTABLE) {
                        return at + "Methods cannot be made mutable";
                    }
                    methods.computeIfAbsent(owner, k -> new HashMap<>()).merge(t.get(3) + t.get(4), bit, (a, b) -> a | b);
                    classes.merge(owner, bit, (a, b) -> a | b); // accessible -> accessible, extendable -> extendable
                }
            }
        }
        return null;
    }

    /** classes named by the files read that the loader never tweaks: those entries do nothing */
    public Set<String> untweakable() {
        Set<String> out = new TreeSet<>();
        for (String c : classes.keySet()) {
            if (!tweakable(c)) {
                out.add(c);
            }
        }
        for (String c : injected.keySet()) {
            if (!tweakable(c)) {
                out.add(c);
            }
        }
        return out;
    }

    public boolean touches(String cls) {
        return tweakable(cls) && (classes.containsKey(cls) || injected.containsKey(cls));
    }

    public int classAccess(String cls, int acc) {
        int bits = tweakable(cls) ? classes.getOrDefault(cls, 0) : 0;
        if ((bits & EXTENDABLE) != 0) {
            return makePublic(removeFinal(acc));
        }
        return (bits & ACCESSIBLE) != 0 ? makePublic(acc) : acc;
    }

    /** an extendable class loses its PermittedSubclasses: it is no longer sealed */
    public boolean unsealed(String cls) {
        return tweakable(cls) && (classes.getOrDefault(cls, 0) & EXTENDABLE) != 0;
    }

    public Set<String> injectedInterfaces(String cls) {
        return tweakable(cls) ? injected.getOrDefault(cls, Set.of()) : Set.of();
    }

    /** {@code ownerAccess}: the declaring class's flags as read, before any widening */
    public int methodAccess(String owner, String name, String desc, int acc, int ownerAccess) {
        if (!tweakable(owner)) {
            return acc;
        }
        int bits = methods.getOrDefault(owner, Map.of()).getOrDefault(name + desc, 0);
        return switch (bits & (ACCESSIBLE | EXTENDABLE)) {
            case ACCESSIBLE -> makePublic(makeFinalIfPrivate(acc, name, ownerAccess));
            case EXTENDABLE -> makeProtected(removeFinal(acc));
            case ACCESSIBLE | EXTENDABLE -> makePublic(removeFinal(acc));
            default -> acc;
        };
    }

    /** a record class made accessible: its canonical constructor is made accessible first */
    public int canonicalConstructorAccess(String owner, int acc) {
        return tweakable(owner) && (classes.getOrDefault(owner, 0) & ACCESSIBLE) != 0 ? makePublic(acc) : acc;
    }

    public int fieldAccess(String owner, String name, String desc, int acc, int ownerAccess) {
        if (!tweakable(owner)) {
            return acc;
        }
        int bits = fields.getOrDefault(owner, Map.of()).getOrDefault(name + ":" + desc, 0);
        boolean interfaceStatic = (ownerAccess & Opcodes.ACC_INTERFACE) != 0 && (acc & Opcodes.ACC_STATIC) != 0;
        return switch (bits & (ACCESSIBLE | MUTABLE)) {
            case ACCESSIBLE -> makePublic(acc);
            case MUTABLE -> interfaceStatic ? acc : removeFinal(acc);
            case ACCESSIBLE | MUTABLE -> interfaceStatic ? makePublic(acc) : makePublic(removeFinal(acc));
            default -> acc;
        };
    }

    // AccessUtils

    static int makePublic(int acc) {
        return (acc & ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED)) | Opcodes.ACC_PUBLIC;
    }

    static int makeProtected(int acc) {
        return (acc & Opcodes.ACC_PUBLIC) != 0 ? acc : (acc & ~Opcodes.ACC_PRIVATE) | Opcodes.ACC_PROTECTED;
    }

    static int makeFinalIfPrivate(int acc, String name, int ownerAccess) {
        if (name.equals("<init>") || (ownerAccess & Opcodes.ACC_INTERFACE) != 0 || (acc & Opcodes.ACC_STATIC) != 0) {
            return acc;
        }
        return (acc & Opcodes.ACC_PRIVATE) != 0 ? acc | Opcodes.ACC_FINAL : acc;
    }

    static int removeFinal(int acc) {
        return acc & ~Opcodes.ACC_FINAL;
    }
}
