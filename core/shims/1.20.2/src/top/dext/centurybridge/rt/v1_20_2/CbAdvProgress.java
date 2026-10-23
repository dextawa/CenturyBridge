package top.dext.centurybridge.rt.v1_20_2;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import top.dext.centurybridge.facade.v1_20_2.class_193;

/**
 * 1.20.1's Advancement constructor, Advancement.Builder's requirement API, AdvancementProgress and
 * CriterionProgress on 1.20.2.
 *
 * Requirements: 1.20.1 kept them as String[][] built by a RequirementsStrategy; 1.20.2 as the
 * AdvancementRequirements record over the same String[][] built by AdvancementRequirements.Strategy.
 * Dates: 1.20.1 kept a criterion's obtain time as a java.util.Date and wrote it with the pattern
 * "yyyy-MM-dd HH:mm:ss Z" (Locale.ROOT); 1.20.2 keeps an Instant and writes it with the same pattern.
 */
public final class CbAdvProgress {

    // ------------------------------------------------------------------ Advancement(..)

    /**
     * new Advancement(id, parent, display, rewards, criteria, requirements, telemetry): 1.20.1's
     * advancement named its id and parent itself; 1.20.2's record names its parent by id and its holder
     * carries its own id. The record is built with the parent's id (the parent's holder, or none), and a
     * holder (id, record) is made at once -- the holder 1.20.2 code finds for it (CbAdvancements), so the
     * advancement keeps the id it was built with. A display adds its title as the chat name (1.20.2's
     * constructor does that itself); the parent link is the tree's: 1.20.1 linked the new advancement
     * into its parent's children here, 1.20.2 does it when a tree is built from holders.
     */
    public static net.minecraft.class_161 newAdvancement(net.minecraft.class_2960 id, net.minecraft.class_161 parent,
            net.minecraft.class_185 display, net.minecraft.class_170 rewards, Map<String, net.minecraft.class_175<?>> criteria,
            String[][] requirements, boolean telemetry) {
        java.util.Optional<net.minecraft.class_2960> parentId = parent == null ? java.util.Optional.empty()
            : java.util.Optional.of(CbAdvNodes.holderOf(parent).comp_1919());
        net.minecraft.class_161 a = new net.minecraft.class_161(parentId, java.util.Optional.ofNullable(display), rewards,
            criteria, new net.minecraft.class_8782(requirements), telemetry);
        new net.minecraft.class_8779(id, a);
        return a;
    }

    /**
     * super(id, parent, display, rewards, criteria, requirements, telemetry) of a 1.20.1 Advancement subclass:
     * the record's constructor takes the parent's id (superCtorReshapes conv) and the requirements as the
     * record ...
     */
    public static java.util.Optional<net.minecraft.class_2960> parentId(net.minecraft.class_161 parent) {
        return parent == null ? java.util.Optional.empty() : java.util.Optional.of(CbAdvNodes.holderOf(parent).comp_1919());
    }

    public static java.util.Optional<net.minecraft.class_185> display(net.minecraft.class_185 display) {
        return java.util.Optional.ofNullable(display);
    }

    public static net.minecraft.class_8782 requirements(String[][] requirements) {
        return new net.minecraft.class_8782(requirements);
    }

    /** ... and once the record is built, the holder naming it with the id the subclass was built with (post) */
    public static void named(net.minecraft.class_161 self, net.minecraft.class_2960 id) {
        new net.minecraft.class_8779(id, self);
    }

    // ------------------------------------------------------------------ Advancement.Builder

    /** Builder.requirements(String[][]) */
    public static net.minecraft.class_161.class_162 method_34884(net.minecraft.class_161.class_162 self, String[][] requirements) {
        return self.method_34884(new net.minecraft.class_8782(requirements));
    }

    /** Builder.requirements(RequirementsStrategy) */
    public static net.minecraft.class_161.class_162 method_704(net.minecraft.class_161.class_162 self, class_193 strategy) {
        return self.method_704(strategyOf(strategy));
    }

    /** a 1.20.1 strategy as 1.20.2's: the same groups, as the record */
    public static net.minecraft.class_8782.class_8797 strategyOf(class_193 strategy) {
        if (strategy == class_193.field_16882) {
            return net.minecraft.class_8782.class_8797.field_16882;
        }
        if (strategy == class_193.field_1257) {
            return net.minecraft.class_8782.class_8797.field_1257;
        }
        return names -> new net.minecraft.class_8782(strategy.createRequirements(names));
    }

    // ------------------------------------------------------------------ AdvancementProgress

    /** AdvancementProgress.update(criteria, requirements): 1.20.2 keeps the criteria by the requirement names */
    public static void method_727(net.minecraft.class_167 self, Map<String, net.minecraft.class_175<?>> criteria, String[][] requirements) {
        self.method_727(new net.minecraft.class_8782(requirements));
    }

    /**
     * AdvancementProgress.getProgressText(): 1.20.1 answered done + "/" + total of the requirement groups
     * (null when there was nothing to count); 1.20.2 answers the translatable "advancements.progress"
     * with the same two numbers -- the string is built from them, independent of the language loaded
     */
    public static String method_728(net.minecraft.class_167 self) {
        net.minecraft.class_2561 t = self.method_728();
        if (t == null) {
            return null;
        }
        if (t.method_10851() instanceof net.minecraft.class_2588 tr && tr.method_11023().length == 2) {
            return tr.method_11023()[0] + "/" + tr.method_11023()[1];
        }
        throw new IllegalStateException("CenturyBridge: unexpected progress text " + t);
    }

    /** AdvancementProgress.getFirstProgressDate(): the earliest obtain time, as a Date */
    public static Date method_741(net.minecraft.class_167 self) {
        Instant i = self.method_741();
        return i == null ? null : Date.from(i);
    }

    /**
     * 1.20.2's update(requirements) on a 1.20.1 subclass overriding update(criteria, requirements): the
     * override gets the groups and the criteria map 1.20.1 handed it, keyed by the requirement names (1.20.1's
     * own update read only the key set; the values are the advancement's criteria, which 1.20.2 does not
     * pass -- each name maps to a criterion without an instance, as a 1.20.1 client's criteria were)
     */
    public static void update(CbAdvLegacy.ProgressUpdate self, net.minecraft.class_8782 requirements) {
        Map<String, net.minecraft.class_175<?>> criteria = new java.util.LinkedHashMap<>();
        for (String name : requirements.method_53678()) {
            criteria.put(name, CbAdvCriteria.cbNewCriterion());
        }
        self.method_727(criteria, requirements.comp_1922());
    }

    /** a 1.20.1 subclass's getProgressText string as the text 1.20.2 draws */
    public static net.minecraft.class_2561 progressText(String s) {
        return s == null ? null : net.minecraft.class_2561.method_43470(s);
    }

    /** a Date a 1.20.1 subclass answered, as 1.20.2's Instant */
    public static Instant instant(Date d) {
        return d == null ? null : d.toInstant();
    }

    // ------------------------------------------------------------------ CriterionProgress

    private static final ThreadLocal<SimpleDateFormat> FORMAT =
        ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.ROOT));

    /** CriterionProgress.getObtained(): as a Date */
    public static Date method_786(net.minecraft.class_178 self) {
        Instant i = self.method_786();
        return i == null ? null : Date.from(i);
    }

    /** CriterionProgress.serializeToJson(): a 1.20.1 subclass's own override answers */
    public static JsonElement method_783(net.minecraft.class_178 self) {
        return (Object) self instanceof CbAdvLegacy.CriterionJson own ? own.method_783() : super_method_783(self);
    }

    /** 1.20.1's serializeToJson(): the obtain time in 1.20.1's pattern, JsonNull when not obtained */
    public static JsonElement super_method_783(net.minecraft.class_178 self) {
        Instant i = self.method_786();
        return i == null ? JsonNull.INSTANCE : new JsonPrimitive(FORMAT.get().format(Date.from(i)));
    }

    /** CriterionProgress.fromJson(string): obtained at that time ("Invalid datetime: s" as 1.20.1 failed) */
    public static net.minecraft.class_178 method_788(String s) {
        try {
            return new net.minecraft.class_178(FORMAT.get().parse(s).toInstant());
        } catch (ParseException e) {
            throw new JsonSyntaxException("Invalid datetime: " + s, e);
        }
    }

    // ------------------------------------------------------------------ AdvancementType

    /** FrameType.getTexture(): 1.20.1's sprite-sheet column offset, 26 pixels per frame (task, challenge, goal) */
    public static int method_832(net.minecraft.class_189 self) {
        return self.ordinal() * 26;
    }

    private CbAdvProgress() {
    }
}
