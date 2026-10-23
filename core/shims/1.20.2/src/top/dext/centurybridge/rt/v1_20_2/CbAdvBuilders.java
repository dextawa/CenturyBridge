package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonObject;
import java.util.Map;
import java.util.function.Function;
import top.dext.centurybridge.shims.v1_20_2.CbAdvStateOfBuilder;

/**
 * 1.20.1's Advancement.Builder methods that 1.20.2 dropped, on 1.20.2's builder: the network form
 * (serializeToNetwork / fromNetwork), the JSON form (fromJson), canBuild(lookup), and the advancement tree's
 * 1.20.1 entry point AdvancementList.add(builders).
 *
 * 1.20.1's builder held the parent as an id (and, once canBuild had found it, as the advancement); 1.20.2's
 * holds the parent id only. canBuild answered whether the parent was known (none needed, or the lookup
 * found it); building needs the parent to have been found in 1.20.1 -- in 1.20.2 the id is enough.
 */
public final class CbAdvBuilders {

    private static CbAdvStateOfBuilder state(net.minecraft.class_161.class_162 b) {
        return (CbAdvStateOfBuilder) (Object) b;
    }

    /** the requirements 1.20.1's builder would write: its own, else its strategy over the criteria names */
    private static net.minecraft.class_8782 requirements(net.minecraft.class_161.class_162 b) {
        CbAdvStateOfBuilder s = state(b);
        return s.cb$requirements().orElseGet(() -> s.cb$strategy().create(
            ((top.dext.centurybridge.shims.v1_20_2.CbAdvancementBuilderCriteria) (Object) b).cbCriteria().build().keySet()));
    }

    /** Builder.canBuild(lookup): true with no parent; else whether the lookup knows the parent id */
    public static boolean method_700(net.minecraft.class_161.class_162 self, Function<net.minecraft.class_2960, net.minecraft.class_161> lookup) {
        java.util.Optional<net.minecraft.class_2960> parent = state(self).cb$parent();
        return parent.isEmpty() || lookup.apply(parent.get()) != null;
    }

    /**
     * Builder.serializeToNetwork(buf), 1.20.1's packet form: parent id (nullable), display (nullable), the
     * criteria (names only, Criterion.serializeToNetwork), the requirement groups, the telemetry flag
     */
    public static void method_699(net.minecraft.class_161.class_162 self, net.minecraft.class_2540 buf) {
        CbAdvStateOfBuilder s = state(self);
        buf.method_43826(s.cb$parent().orElse(null), net.minecraft.class_2540::method_10812);
        buf.method_43826(s.cb$display().orElse(null), (b, d) -> d.method_813(b));
        Map<String, net.minecraft.class_175<?>> criteria =
            ((top.dext.centurybridge.shims.v1_20_2.CbAdvancementBuilderCriteria) (Object) self).cbCriteria().build();
        CbAdvCriteria.method_775(criteria, buf);
        String[][] req = requirements(self).comp_1922();
        buf.method_10804(req.length);
        for (String[] group : req) {
            buf.method_10804(group.length);
            for (String name : group) {
                buf.method_10814(name);
            }
        }
        buf.writeBoolean(s.cb$telemetry());
    }

    /** Builder.fromNetwork(buf): the form serializeToNetwork writes, rewards empty (the network carries none) */
    public static net.minecraft.class_161.class_162 method_696(net.minecraft.class_2540 buf) {
        net.minecraft.class_2960 parent = buf.method_43827(net.minecraft.class_2540::method_10810);
        net.minecraft.class_185 display = buf.method_43827(net.minecraft.class_185::method_820);
        Map<String, net.minecraft.class_175<?>> criteria = CbAdvCriteria.method_768(buf);
        String[][] req = new String[buf.method_10816()][];
        for (int i = 0; i < req.length; i++) {
            req[i] = new String[buf.method_10816()];
            for (int j = 0; j < req[i].length; j++) {
                req[i][j] = buf.method_19772();
            }
        }
        boolean telemetry = buf.readBoolean();
        net.minecraft.class_161.class_162 b = telemetry ? net.minecraft.class_161.class_162.method_51698()
            : net.minecraft.class_161.class_162.method_707();
        if (parent != null) {
            b.method_708(parent);
        }
        if (display != null) {
            b.method_693(display);
        }
        criteria.forEach(b::method_705);
        return b.method_34884(new net.minecraft.class_8782(req));
    }

    /**
     * Builder.fromJson(json, context): 1.20.2 reads the same object (parent, display, rewards, criteria,
     * requirements, sends_telemetry_event, with 1.20.1's validation) as an Advancement; the builder is
     * rebuilt from it
     */
    public static net.minecraft.class_161.class_162 method_692(JsonObject json, net.minecraft.class_5257 context) {
        return CbRecipeDatagen.builderOf(net.minecraft.class_161.method_53626(json, context));
    }

    /**
     * AdvancementList.add(builders): 1.20.1's tree took builders by id and resolved parents itself
     * (repeating while any could be placed, then logging the orphans); 1.20.2's takes holders and does the
     * same. Every builder that 1.20.1 would place (its parent present among the builders or already in the
     * tree) is built under its id and handed over.
     */
    public static void method_711(net.minecraft.class_163 self, Map<net.minecraft.class_2960, net.minecraft.class_161.class_162> builders) {
        java.util.List<net.minecraft.class_8779> holders = new java.util.ArrayList<>();
        for (Map.Entry<net.minecraft.class_2960, net.minecraft.class_161.class_162> e : builders.entrySet()) {
            holders.add(e.getValue().method_695(e.getKey()));
        }
        self.method_53691(holders);
    }

    /**
     * 1.20.2's addAll(holders) on a 1.20.1 subclass overriding add(builders): the override gets each holder
     * by its id as a builder of its advancement (1.20.1's loader handed it the builders it read)
     */
    public static void add(CbAdvLegacy.TreeAdd self, java.util.Collection<net.minecraft.class_8779> holders) {
        Map<net.minecraft.class_2960, net.minecraft.class_161.class_162> builders = new java.util.LinkedHashMap<>();
        for (net.minecraft.class_8779 h : holders) {
            builders.put(h.comp_1919(), CbRecipeDatagen.builderOf(h.comp_1920()));
        }
        self.method_711(builders);
    }

    private CbAdvBuilders() {
    }
}
