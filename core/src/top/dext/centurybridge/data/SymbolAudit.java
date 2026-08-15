package top.dext.centurybridge.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Bulk archaeology: for every damaged symbol in a residual report, print the
 * old signature and its fate in the new stub (gone / descriptor changed /
 * relocated) -- one table instead of one javap round-trip per symbol.
 */
public final class SymbolAudit {

    record Member(String owner, String desc) {}

    static final class Index {
        final Map<String, Map<String, List<String>>> byOwner = new HashMap<>();
        final Map<String, List<Member>> byName = new HashMap<>();

        void add(String owner, String name, String desc) {
            byOwner.computeIfAbsent(owner, k -> new HashMap<>())
                   .computeIfAbsent(name, k -> new ArrayList<>()).add(desc);
            byName.computeIfAbsent(name, k -> new ArrayList<>()).add(new Member(owner, desc));
        }
    }

    public static void run(Path oldStub, Path newStub, Path report, Path out) throws IOException {
        Index oldIdx = index(oldStub);
        Index newIdx = index(newStub);

        Set<String> symbols = new LinkedHashSet<>();
        Pattern full = Pattern.compile("net/minecraft/([\\w$/]+)\\.((?:method|field)_\\d+)");
        Pattern shortp = Pattern.compile("(class_\\d+(?:\\$class_\\d+)?)\\.((?:method|field)_\\d+)");
        for (String line : Files.readAllLines(report)) {
            if (!line.startsWith("- ")) {
                continue;
            }
            Matcher m = full.matcher(line);
            if (m.find()) {
                symbols.add("net/minecraft/" + m.group(1) + "|" + m.group(2));
                continue;
            }
            m = shortp.matcher(line);
            if (m.find()) {
                symbols.add("net/minecraft/" + m.group(1) + "|" + m.group(2));
            }
        }

        List<String> lines = new ArrayList<>();
        lines.add("| symbol | old | new fate |");
        lines.add("|---|---|---|");
        for (String sym : symbols) {
            String[] p = sym.split("\\|");
            String owner = p[0];
            String name = p[1];
            List<String> olds = oldIdx.byOwner.getOrDefault(owner, Map.of()).get(name);
            String oldStr = olds == null ? "(not declared here)" : String.join(" ; ", olds);
            List<String> news = newIdx.byOwner.getOrDefault(owner, Map.of()).get(name);
            String fate;
            if (news != null) {
                fate = "DESC-CHANGED: " + String.join(" ; ", news);
            } else {
                List<Member> global = newIdx.byName.get(name);
                if (global != null && !global.isEmpty()) {
                    Member g = global.get(0);
                    fate = "MOVED: " + g.owner() + " " + g.desc();
                } else {
                    fate = "GONE";
                }
            }
            lines.add("| `" + owner.substring(owner.lastIndexOf('/') + 1) + "." + name + "` | `" + oldStr + "` | " + fate + " |");
        }
        Files.write(out, String.join("\n", lines).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        System.out.println("audited " + symbols.size() + " symbols -> " + out);
    }

    /**
     * Enrich a full-audit worklist with each symbol's fate in the new stub:
     * SAME-DESC (exists as-is: resolution failure was hierarchy/side related),
     * DESC-CHANGED (same owner+name, new descriptor -> shim-overload candidate),
     * MOVED (same name on another owner -> redirect candidate), GONE
     * (needs reimplementation or a justified tombstone).
     */
    public static void classify(Path newStub, Path tsv, Path out) throws IOException {
        Index newIdx = index(newStub);
        List<String> lines = new ArrayList<>();
        for (String line : Files.readAllLines(tsv)) {
            if (line.startsWith("count\t")) {
                lines.add(line + "\tnewFate");
                continue;
            }
            String[] cols = line.split("\t", 3);
            if (cols.length < 3) {
                continue;
            }
            String sym = cols[2];
            String fate;
            int dot = sym.indexOf('.');
            if (dot < 0) {
                // bare class reference (internal names never contain '.')
                fate = newIdx.byOwner.containsKey(sym) ? "CLASS-EXISTS" : "CLASS-GONE";
            } else {
                String owner = sym.substring(0, dot);
                String rest = sym.substring(dot + 1);
                String name;
                String desc;
                int colon = rest.indexOf(':');
                int paren = rest.indexOf('(');
                if (paren >= 0) {
                    name = rest.substring(0, paren);
                    desc = rest.substring(paren);
                } else if (colon >= 0) {
                    name = rest.substring(0, colon);
                    desc = rest.substring(colon + 1);
                } else {
                    name = rest;
                    desc = "";
                }
                List<String> here = newIdx.byOwner.getOrDefault(owner, Map.of()).get(name);
                if (here != null && here.contains(desc)) {
                    fate = "SAME-DESC";
                } else if (here != null) {
                    fate = "DESC-CHANGED: " + String.join(" ; ", here);
                } else {
                    List<Member> global = newIdx.byName.get(name);
                    if (global != null && !global.isEmpty()) {
                        fate = "MOVED: " + global.get(0).owner() + " " + global.get(0).desc();
                    } else {
                        fate = "GONE";
                    }
                }
            }
            lines.add(line + "\t" + fate);
        }
        Files.write(out, String.join("\n", lines).getBytes(java.nio.charset.StandardCharsets.UTF_8));
        System.out.println("classified " + (lines.size() - 1) + " rows -> " + out);
    }

    private static Index index(Path stub) throws IOException {
        Index idx = new Index();
        try (ZipInputStream zin = new ZipInputStream(Files.newInputStream(stub))) {
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (!e.getName().endsWith(".class")) {
                    continue;
                }
                new ClassReader(zin.readAllBytes()).accept(new ClassVisitor(Opcodes.ASM9) {
                    String owner;

                    @Override
                    public void visit(int v, int a, String name, String s, String sup, String[] i) {
                        owner = name;
                    }

                    @Override
                    public MethodVisitor visitMethod(int a, String name, String desc, String s, String[] ex) {
                        idx.add(owner, name, desc);
                        return null;
                    }

                    @Override
                    public FieldVisitor visitField(int a, String name, String desc, String s, Object v) {
                        idx.add(owner, name, desc);
                        return null;
                    }
                }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
            }
        }
        return idx;
    }
}
