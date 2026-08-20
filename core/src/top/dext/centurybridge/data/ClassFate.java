package top.dext.centurybridge.data;

import java.nio.file.*;
import java.util.*;
import java.util.zip.*;
import org.objectweb.asm.*;

/**
 * Did these classes really vanish, or did they move / merge / split?
 *
 * A name-only comparison calls all four "CLASS_GONE", which is wrong in the
 * ways that matter: a relocated class can be bridged with a facade, a truly
 * deleted one cannot. Intermediary member names are stable across versions, so
 * a class's member-name set is a usable fingerprint -- if most of it turns up
 * together on some new class, that class is where the old one went.
 */
public final class ClassFate {
    record Info(Set<String> members, String superName) {}

    public static void run(Path oldJar, Path newJar, Path out) throws Exception {
        String[] a = {oldJar.toString(), newJar.toString(), out.toString()};
        Map<String, Info> oldIdx = index(Path.of(a[0]));
        Map<String, Info> newIdx = index(Path.of(a[1]));

        List<String> gone = new ArrayList<>();
        for (String c : oldIdx.keySet()) {
            if (!newIdx.containsKey(c)) {
                gone.add(c);
            }
        }
        Collections.sort(gone);

        Map<String, List<String>> homes = new HashMap<>();
        for (var e : newIdx.entrySet()) {
            for (String m : e.getValue().members()) {
                homes.computeIfAbsent(m, k -> new ArrayList<>()).add(e.getKey());
            }
        }

        int synthetic = 0, unmapped = 0, relocated = 0, merged = 0, trulyGone = 0, withdrawn = 0;
        List<String> report = new ArrayList<>();

        for (String c : gone) {
            String simple = c.substring(c.lastIndexOf('/') + 1);
            // anonymous inner classes are renumbered every build; never API
            if (simple.matches(".*[$][0-9]+$")) {
                synthetic++;
                continue;
            }
            Set<String> mapped = new TreeSet<>();
            for (String m : oldIdx.get(c).members()) {
                if (m.startsWith("method_") || m.startsWith("field_") || m.startsWith("comp_")) {
                    mapped.add(m);
                }
            }
            if (mapped.isEmpty()) {
                unmapped++; // obf-only members: a mod could never have referenced them
                continue;
            }

            Map<String, Integer> votes = new HashMap<>();
            for (String m : mapped) {
                for (String home : homes.getOrDefault(m, List.of())) {
                    votes.merge(home, 1, Integer::sum);
                }
            }
            String best = null;
            int bestN = 0;
            for (var e : votes.entrySet()) {
                if (e.getValue() > bestN) {
                    best = e.getKey();
                    bestN = e.getValue();
                }
            }
            double cover = bestN / (double) mapped.size();

            if (best != null && cover >= 0.6) {
                relocated++;
                report.add(String.format("RELOCATED\t%s\t%s\t%d/%d\t%.0f%%",
                    c, best, bestN, mapped.size(), cover * 100));
            } else if (best != null && cover >= 0.25) {
                merged++;
                report.add(String.format("SPLIT\t%s\t%s\t%d/%d\t%.0f%%",
                    c, best, bestN, mapped.size(), cover * 100));
            } else {
                // Separate "moved somewhere we could not pin down" from "this API
                // was withdrawn". A rewrite that relocates code carries the member
                // names with it -- intermediary names follow the member, not the
                // class -- so if not one mapped member survives ANYWHERE in the new
                // jar, the API really was removed rather than moved.
                int survivors = 0;
                for (String m : mapped) {
                    if (homes.containsKey(m)) {
                        survivors++;
                    }
                }
                if (survivors == 0) {
                    withdrawn++;
                    report.add(String.format("WITHDRAWN\t%s\t-\t0/%d\tno member survives anywhere",
                        c, mapped.size()));
                } else {
                    trulyGone++;
                    report.add(String.format("SCATTERED\t%s\t%s\t%d/%d\t%d members survive elsewhere",
                        c, best == null ? "-" : best, bestN, mapped.size(), survivors));
                }
            }
        }

        System.out.println("classes gone by name: " + gone.size());
        System.out.println("  anonymous/synthetic (renumbered, never API): " + synthetic);
        System.out.println("  obf-only members (unreferenceable by mods):  " + unmapped);
        System.out.println("  RELOCATED  (>=60% of members found together): " + relocated);
        System.out.println("  SPLIT      (25-60%):                          " + merged);
        System.out.println("  SCATTERED  (<25%, members live elsewhere):    " + trulyGone);
        System.out.println("  WITHDRAWN  (no member survives anywhere):     " + withdrawn);
        System.out.println();
        for (String s : report) {
            if (s.startsWith("RELOCATED")) {
                System.out.println("  " + s);
            }
        }
        System.out.println();
        for (String s : report) {
            if (s.startsWith("SPLIT")) {
                System.out.println("  " + s);
            }
        }
        Files.write(Path.of(a[2]), report);
        System.out.println("\n-> " + a[2]);
    }

    static Map<String, Info> index(Path jar) throws Exception {
        Map<String, Info> idx = new HashMap<>();
        try (ZipInputStream z = new ZipInputStream(Files.newInputStream(jar))) {
            ZipEntry e;
            while ((e = z.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class") || !e.getName().startsWith("net/minecraft/")) {
                    continue;
                }
                Set<String> mem = new HashSet<>();
                new ClassReader(z.readAllBytes()).accept(new ClassVisitor(Opcodes.ASM9) {
                    @Override
                    public void visit(int v, int acc, String n, String s, String su, String[] i) {
                        idx.put(n, new Info(mem, su));
                    }

                    @Override
                    public MethodVisitor visitMethod(int acc, String n, String d, String s, String[] x) {
                        if ((acc & Opcodes.ACC_PRIVATE) == 0) {
                            mem.add(n);
                        }
                        return null;
                    }

                    @Override
                    public FieldVisitor visitField(int acc, String n, String d, String s, Object v) {
                        if ((acc & Opcodes.ACC_PRIVATE) == 0) {
                            mem.add(n);
                        }
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }
        return idx;
    }
}
