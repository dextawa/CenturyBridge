package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Owner-qualified stub-to-stub API diff.
 *
 * SegmentGen records only bare member NAMES -- enough for the chain's
 * name-equality identity check, far too coarse to drive bridging: it cannot
 * separate a deleted method from one whose descriptor merely moved, and it
 * never says which class the damage is on. This reads both stub jars with ASM
 * in a single pass (no per-class javap round-trips) and emits every damaged
 * symbol fully qualified, with its fate on the new side.
 *
 * Fates: CLASS_GONE (owner itself deleted), DESC_CHANGED (same owner+name,
 * different descriptor -- shim-overload candidate), MOVED (same name+desc on
 * another owner -- redirect candidate), GONE (owner survives, member does not).
 */
public final class StubDiff {

    /** owner -> name -> descriptors, plus name+desc -> owners for MOVED lookups. */
    static final class Index {
        final Set<String> classes = new HashSet<>();
        final Map<String, Map<String, Set<String>>> methods = new HashMap<>();
        final Map<String, Map<String, Set<String>>> fields = new HashMap<>();
        final Map<String, Set<String>> methodHomes = new HashMap<>();
        final Map<String, Set<String>> fieldHomes = new HashMap<>();

        void addMethod(String owner, String name, String desc) {
            methods.computeIfAbsent(owner, k -> new HashMap<>())
                   .computeIfAbsent(name, k -> new HashSet<>()).add(desc);
            methodHomes.computeIfAbsent(name + desc, k -> new HashSet<>()).add(owner);
        }

        void addField(String owner, String name, String desc) {
            fields.computeIfAbsent(owner, k -> new HashMap<>())
                  .computeIfAbsent(name, k -> new HashSet<>()).add(desc);
            fieldHomes.computeIfAbsent(name + ":" + desc, k -> new HashSet<>()).add(owner);
        }
    }

    public record Damage(String kind, String owner, String name, String desc,
                         String fate, String detail) {}

    public static void run(Path oldJar, Path newJar, Path outTsv) throws IOException {
        Index oldIdx = index(oldJar);
        Index newIdx = index(newJar);
        List<Damage> out = new ArrayList<>();

        for (String owner : oldIdx.classes) {
            boolean ownerGone = !newIdx.classes.contains(owner);
            collect(out, "method", owner, ownerGone,
                oldIdx.methods.getOrDefault(owner, Map.of()),
                ownerGone ? Map.<String, Set<String>>of() : newIdx.methods.getOrDefault(owner, Map.of()),
                newIdx.methodHomes, false);
            collect(out, "field", owner, ownerGone,
                oldIdx.fields.getOrDefault(owner, Map.of()),
                ownerGone ? Map.<String, Set<String>>of() : newIdx.fields.getOrDefault(owner, Map.of()),
                newIdx.fieldHomes, true);
        }

        out.sort((a, b) -> {
            int c = a.owner().compareTo(b.owner());
            return c != 0 ? c : a.name().compareTo(b.name());
        });

        List<String> lines = new ArrayList<>();
        lines.add("kind\tfate\tsymbol\tdesc\tdetail");
        for (Damage d : out) {
            lines.add(d.kind() + "\t" + d.fate() + "\t" + d.owner() + "." + d.name()
                + "\t" + d.desc() + "\t" + d.detail());
        }
        Files.write(outTsv, String.join("\n", lines).getBytes(StandardCharsets.UTF_8));

        Map<String, Integer> byFate = new TreeMap<>();
        for (Damage d : out) {
            byFate.merge(d.fate(), 1, Integer::sum);
        }
        long gone = oldIdx.classes.stream().filter(c -> !newIdx.classes.contains(c)).count();
        System.out.println("stub diff " + oldJar.getFileName() + " -> " + newJar.getFileName());
        System.out.println("  classes " + oldIdx.classes.size() + " -> " + newIdx.classes.size()
            + " (gone " + gone + ")");
        System.out.println("  damaged symbols " + out.size() + " " + byFate);
        System.out.println("  -> " + outTsv);
    }

    private static void collect(List<Damage> out, String kind, String owner, boolean ownerGone,
                                Map<String, Set<String>> oldMembers,
                                Map<String, Set<String>> newMembers,
                                Map<String, Set<String>> newHomes, boolean isField) {
        for (Map.Entry<String, Set<String>> e : oldMembers.entrySet()) {
            String name = e.getKey();
            Set<String> newDescs = newMembers.getOrDefault(name, Set.of());
            for (String desc : e.getValue()) {
                if (newDescs.contains(desc)) {
                    continue; // survives untouched
                }
                if (ownerGone) {
                    out.add(new Damage(kind, owner, name, desc, "CLASS_GONE", ""));
                } else if (!newDescs.isEmpty()) {
                    out.add(new Damage(kind, owner, name, desc, "DESC_CHANGED",
                        String.join(" ; ", new TreeSet<>(newDescs))));
                } else {
                    String homeKey = isField ? name + ":" + desc : name + desc;
                    Set<String> homes = newHomes.getOrDefault(homeKey, Set.of());
                    if (!homes.isEmpty()) {
                        out.add(new Damage(kind, owner, name, desc, "MOVED",
                            String.join(" ; ", new TreeSet<>(homes))));
                    } else {
                        out.add(new Damage(kind, owner, name, desc, "GONE", ""));
                    }
                }
            }
        }
    }

    private static Index index(Path jar) throws IOException {
        Index idx = new Index();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class") || !e.getName().startsWith("net/minecraft/")) {
                    continue;
                }
                new ClassReader(zin.readAllBytes()).accept(new ClassVisitor(Opcodes.ASM9) {
                    String owner;

                    @Override
                    public void visit(int v, int acc, String name, String sig, String sup, String[] itf) {
                        owner = name;
                        idx.classes.add(name);
                    }

                    @Override
                    public MethodVisitor visitMethod(int acc, String name, String desc,
                                                     String sig, String[] ex) {
                        // private members are invisible to mods; <clinit> is never a call target
                        if ((acc & Opcodes.ACC_PRIVATE) == 0 && !name.equals("<clinit>")) {
                            idx.addMethod(owner, name, desc);
                        }
                        return null;
                    }

                    @Override
                    public FieldVisitor visitField(int acc, String name, String desc,
                                                   String sig, Object val) {
                        if ((acc & Opcodes.ACC_PRIVATE) == 0) {
                            idx.addField(owner, name, desc);
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }
        return idx;
    }

    private StubDiff() {
    }
}
