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
