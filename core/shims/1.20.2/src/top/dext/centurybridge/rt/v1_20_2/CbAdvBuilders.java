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
     * -Dcenturybridge.legacyJsonRoute=false (the switch of the recipe readers' route too): the restored 1.20.1
     * Builder.fromJson is never consulted
     */
    public static final boolean ROUTE = CbRecipeJson.ROUTE;

    /**
     * the advancement files ServerAdvancementManager.apply (method_20724) is reading on this thread: its Map argument,
     * taken at HEAD by its erased type (CbAdvJsonVanillaMixin) and cleared at its return. Up to 1.21.1 its values are
     * the files' JSON; from 1.21.2 the loader hands decoded advancements, and {@link #consultCarried} finds no JSON.
     */
    private static final ThreadLocal<java.util.Map<?, ?>> CURRENT_FILES = new ThreadLocal<>();

    public static void carryFiles(java.util.Map<?, ?> files) {
        if (files == null) {
            CURRENT_FILES.remove();
        } else {
            CURRENT_FILES.set(files);
        }
    }

    /**
     * {@link #consult} for the advancement {@code id} the manager is about to hold, with that file's carried JSON;
     * {@code read} unchanged when no JSON is carried for it (none carried, or a release whose loader decodes first)
     */
    public static net.minecraft.class_161 consultCarried(net.minecraft.class_2960 id, net.minecraft.class_161 read,
                                                         net.minecraft.class_60 lootData) {
        java.util.Map<?, ?> files = CURRENT_FILES.get();
        Object json = files == null || id == null ? null : files.get(id);
        return json instanceof com.google.gson.JsonElement element ? consult(read, id, element, lootData) : read;
    }

    /**
     * ServerAdvancementManager has read {@code read} with 1.20.2's Advancement.fromJson. The advancement vanilla keeps:
     * {@code read}, unless the restored 1.20.1 Builder.fromJson (which a converted mod's mixin may change) answers
     * otherwise than its unmixed copy {@link #goldenFromJson} -- then what that builder builds (or its exception), as
     * 1.20.1's manager built the builders Builder.fromJson answered. Two answers are the same when both throw the same
     * exception or both build advancements of the same JSON form (Advancement.serializeToJson).
     */
    public static net.minecraft.class_161 consult(net.minecraft.class_161 read, net.minecraft.class_2960 id,
                                                  com.google.gson.JsonElement element, net.minecraft.class_60 lootData) {
        if (!ROUTE || read == null || !CbAdvPredicateJson.mixed()) {
            return read;
        }
        JsonObject json = net.minecraft.class_3518.method_15295(element, "advancement");
        Object mixed;
        try {
            mixed = top.dext.centurybridge.shims.v1_20_2.CbAdvBuilderAccess.cb$fromJson(json, new net.minecraft.class_5257(id, lootData));
        } catch (RuntimeException e) {
            mixed = e;
        }
        Object golden;
        try {
            golden = goldenFromJson(json, new net.minecraft.class_5257(id, lootData));
        } catch (RuntimeException e) {
            golden = e;
        }
        if (mixed instanceof RuntimeException a || golden instanceof RuntimeException) {
            if (mixed instanceof RuntimeException a && golden instanceof RuntimeException b && a.getClass() == b.getClass()
                    && java.util.Objects.equals(a.getMessage(), b.getMessage())) {
                return read;
            }
            if (mixed instanceof RuntimeException a) {
                throw a;
            }
            return ((net.minecraft.class_161.class_162) mixed).method_695(id).comp_1920();
        }
        net.minecraft.class_161 m = ((net.minecraft.class_161.class_162) mixed).method_695(id).comp_1920();
        net.minecraft.class_161 g = ((net.minecraft.class_161.class_162) golden).method_695(id).comp_1920();
        return m.method_53621().equals(g.method_53621()) ? read : m;
    }

    /** 1.20.1's private Builder(parent, display, rewards, criteria, requirements, sendsTelemetryEvent), as 1.20.2's builder */
    public static net.minecraft.class_161.class_162 builderOf(net.minecraft.class_2960 parent, net.minecraft.class_185 display,
            net.minecraft.class_170 rewards, Map<String, net.minecraft.class_175<?>> criteria, String[][] requirements, boolean telemetry) {
        net.minecraft.class_161.class_162 b = net.minecraft.class_161.class_162.method_51698();
        if (parent != null) {
            b.method_708(parent);
        }
        if (display != null) {
            b.method_693(display);
        }
        b.method_706(rewards);
        criteria.forEach(b::method_705);
        b.method_34884(new net.minecraft.class_8782(requirements));
        if (telemetry) {
            b.method_53634();
        }
        return b;
    }

    /** the unmixable copy of the restored 1.20.1 Builder.fromJson (CbAdvBuilderJson.method_692): the same body */
    public static net.minecraft.class_161.class_162 goldenFromJson(JsonObject json, net.minecraft.class_5257 context) {
        net.minecraft.class_2960 parent = json.has("parent") ? new net.minecraft.class_2960(net.minecraft.class_3518.method_15265(json, "parent")) : null;
        net.minecraft.class_185 display = json.has("display") ? net.minecraft.class_185.method_809(net.minecraft.class_3518.method_15296(json, "display")) : null;
        net.minecraft.class_170 rewards = json.has("rewards") ? net.minecraft.class_170.method_27912(net.minecraft.class_3518.method_15296(json, "rewards"))
            : net.minecraft.class_170.field_1167;
        Map<String, net.minecraft.class_175<?>> criteria = net.minecraft.class_175.method_772(net.minecraft.class_3518.method_15296(json, "criteria"), context);
        if (criteria.isEmpty()) {
            throw new com.google.gson.JsonSyntaxException("Advancement criteria cannot be empty");
        }
        com.google.gson.JsonArray groups = net.minecraft.class_3518.method_15292(json, "requirements", new com.google.gson.JsonArray());
        String[][] requirements = new String[groups.size()][];
        for (int i = 0; i < groups.size(); ++i) {
            com.google.gson.JsonArray group = net.minecraft.class_3518.method_15252(groups.get(i), "requirements[" + i + "]");
            requirements[i] = new String[group.size()];
            for (int j = 0; j < group.size(); ++j) {
                requirements[i][j] = net.minecraft.class_3518.method_15287(group.get(j), "requirements[" + i + "][" + j + "]");
            }
        }
        if (requirements.length == 0) {
            requirements = new String[criteria.size()][];
            int k = 0;
            for (String name : criteria.keySet()) {
                requirements[k++] = new String[] {name};
            }
        }
        for (String[] group : requirements) {
            if (group.length == 0 && criteria.isEmpty()) {
                throw new com.google.gson.JsonSyntaxException("Requirement entry cannot be empty");
            }
            for (String name : group) {
                if (!criteria.containsKey(name)) {
                    throw new com.google.gson.JsonSyntaxException("Unknown required criterion '" + name + "'");
                }
            }
        }
        for (String name : criteria.keySet()) {
            boolean required = false;
            for (String[] group : requirements) {
                if (org.apache.commons.lang3.ArrayUtils.contains(group, name)) {
                    required = true;
                    break;
                }
            }
            if (!required) {
                throw new com.google.gson.JsonSyntaxException("Criterion '" + name
                    + "' isn't a requirement for completion. This isn't supported behaviour, all criteria must be required.");
            }
        }
        boolean telemetry = net.minecraft.class_3518.method_15258(json, "sends_telemetry_event", false);
        return builderOf(parent, display, rewards, criteria, requirements, telemetry);
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
