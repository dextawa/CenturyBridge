package top.dext.centurybridge.data;

import top.dext.centurybridge.engine.Segment;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Build-time generator: diff two adjacent intermediary-era versions into a
 * frozen Segment. (The Mojmap-frontier and inventory-diff generators join
 * this class as the datagen tool grows.)
 */
public final class SegmentGen {
    private static final Pattern CLASS_PAT = Pattern.compile("net/minecraft/class_\\d+");
    private static final Pattern METHOD_PAT = Pattern.compile("method_\\d+");
    private static final Pattern FIELD_PAT = Pattern.compile("field_\\d+");

    public static Segment diff(String from, String to, TinyMappings a, TinyMappings b) {
        Segment s = new Segment();
        s.from = from;
        s.to = to;
        s.namespace = "intermediary";
        for (String c : a.classes) {
            if (CLASS_PAT.matcher(c).matches() && !b.classes.contains(c)) {
                s.classesGone.add(c);
            }
        }
        diffMembers(a.methods, b.methods, METHOD_PAT, s.methodsGone, s.methodsDescChanged);
        diffMembers(a.fields, b.fields, FIELD_PAT, s.fieldsGone, s.fieldsDescChanged);
        Collections.sort(s.classesGone);
        Collections.sort(s.methodsGone);
        Collections.sort(s.methodsDescChanged);
        Collections.sort(s.fieldsGone);
        Collections.sort(s.fieldsDescChanged);
        s.index();
        return s;
    }

    private static void diffMembers(Map<String, Set<String>> a, Map<String, Set<String>> b,
                                    Pattern pat, List<String> gone, List<String> descChanged) {
        for (Map.Entry<String, Set<String>> e : a.entrySet()) {
            String name = e.getKey();
            if (!pat.matcher(name).matches()) {
                continue;
            }
            Set<String> newDescs = b.get(name);
            if (newDescs == null) {
                gone.add(name);
            } else if (Collections.disjoint(e.getValue(), newDescs)) {
                descChanged.add(name);
            }
        }
    }
}
