package top.dext.centurybridge.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Tiny v2 (official <-> intermediary) mappings, viewed in the intermediary
 * namespace. Published Fabric mods of the intermediary era reference exactly
 * these names, so cross-version identity within that era is name equality.
 * Descriptors in the file are official-namespace and are remapped here.
 */
public final class TinyMappings {
    public final Set<String> classes = new HashSet<>();
    /** intermediary member name -> set of descriptors (intermediary namespace) */
    public final Map<String, Set<String>> methods = new HashMap<>();
    public final Map<String, Set<String>> fields = new HashMap<>();

    public static TinyMappings load(Path tiny) throws IOException {
        TinyMappings m = new TinyMappings();
        Map<String, String> classMap = new HashMap<>();
        List<String[]> members = new ArrayList<>(); // kind, descObf, nameInt
        try (BufferedReader r = Files.newBufferedReader(tiny)) {
            String header = r.readLine();
            if (header == null || !header.startsWith("tiny\t2")) {
                throw new IOException("not a tiny v2 file: " + tiny);
            }
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\t", -1);
                if (p[0].equals("c") && p.length >= 3) {
                    classMap.put(p[1], p[2]);
                    m.classes.add(p[2]);
                } else if (p.length >= 5 && p[0].isEmpty() && (p[1].equals("m") || p[1].equals("f"))) {
                    members.add(new String[] {p[1], p[2], p[4]});
                }
            }
        }
        for (String[] e : members) {
            String descInt = remapDesc(e[1], classMap);
            (e[0].equals("m") ? m.methods : m.fields)
                .computeIfAbsent(e[2], k -> new HashSet<>()).add(descInt);
        }
        return m;
    }

    /** obf-side view for remapping official jars into the intermediary namespace */
    public static final class Obf {
        public final Map<String, String> classes = new HashMap<>();  // obf -> intermediary
        public final Map<String, String> methods = new HashMap<>();  // owner.name+desc (obf) -> intermediary
        public final Map<String, String> fields = new HashMap<>();   // owner.name:desc (obf) -> intermediary
    }

    public static Obf loadObf(Path tiny) throws IOException {
        Obf o = new Obf();
        String currentObf = null;
        try (BufferedReader r = Files.newBufferedReader(tiny)) {
            r.readLine();
            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split("\t", -1);
                if (p[0].equals("c") && p.length >= 3) {
                    currentObf = p[1];
                    o.classes.put(p[1], p[2]);
                } else if (p.length >= 5 && p[0].isEmpty() && currentObf != null) {
                    if (p[1].equals("m")) {
                        o.methods.put(currentObf + "." + p[3] + p[2], p[4]);
                    } else if (p[1].equals("f")) {
                        o.fields.put(currentObf + "." + p[3] + ":" + p[2], p[4]);
                    }
                }
            }
        }
        return o;
    }

    static String remapDesc(String desc, Map<String, String> classMap) {
        StringBuilder out = new StringBuilder(desc.length());
        for (int i = 0; i < desc.length(); ) {
            char ch = desc.charAt(i);
            if (ch == 'L') {
                int j = desc.indexOf(';', i);
                String name = desc.substring(i + 1, j);
                out.append('L').append(classMap.getOrDefault(name, name)).append(';');
                i = j + 1;
            } else {
                out.append(ch);
                i++;
            }
        }
        return out.toString();
    }
}
