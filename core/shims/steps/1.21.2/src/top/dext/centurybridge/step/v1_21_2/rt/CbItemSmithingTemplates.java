package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_124;
import net.minecraft.class_156;
import net.minecraft.class_1792;
import net.minecraft.class_2561;
import net.minecraft.class_2960;
import net.minecraft.class_5321;
import net.minecraft.class_7696;
import net.minecraft.class_8052;
import net.minecraft.class_8056;

/**
 * 1.21.1's SmithingTemplateItem on 1.21.2. 1.21.1's constructor took the upgrade description (the tooltip's first
 * line: the trim pattern's or the netherite upgrade's name) and the item's required feature flags; 1.21.2's takes
 * the Item.Properties and puts a fixed "Smithing Template" line first (the pattern became the item's name). A
 * 1.21.1-built template keeps its upgrade description and shows it there again (CbItemSmithingTemplateMixin); the
 * flags become the properties' required features, as 1.21.1's constructor made them. The factories are 1.21.2's
 * with 1.21.1's upgrade description: "trim_pattern.&lt;ns&gt;.&lt;path&gt;" / "upgrade.minecraft.netherite_upgrade",
 * gray.
 */
public final class CbItemSmithingTemplates {
    private CbItemSmithingTemplates() {
    }

    /** a template's 1.21.1 upgrade description (CbItemSmithingTemplateMixin) */
    public interface Upgrade {
        void cb$setUpgrade(class_2561 upgrade);
    }

    /** the properties 1.21.1's constructor built: new Properties().requiredFeatures(flags) */
    public static class_1792.class_1793 properties(class_7696[] flags) {
        return new class_1792.class_1793().method_45434(flags);
    }

    /** the constructor's upgrade description, kept on the template */
    public static void keepUpgrade(class_8052 template, class_2561 upgrade) {
        ((Upgrade) template).cb$setUpgrade(upgrade);
    }

    /** createArmorTrimTemplate(pattern key, flags) */
    public static class_8052 createArmorTrimTemplate(class_5321<class_8056> pattern, class_7696... flags) {
        return createArmorTrimTemplate(pattern.method_29177(), flags);
    }

    /** createArmorTrimTemplate(pattern id, flags) */
    public static class_8052 createArmorTrimTemplate(class_2960 pattern, class_7696... flags) {
        class_8052 template = class_8052.method_48418(properties(flags));
        keepUpgrade(template, class_2561.method_43471(class_156.method_646("trim_pattern", pattern)).method_27692(class_124.field_1080));
        return template;
    }

    /** createNetheriteUpgradeTemplate() */
    public static class_8052 createNetheriteUpgradeTemplate() {
        class_8052 template = class_8052.method_48420(properties(new class_7696[0]));
        keepUpgrade(template, class_2561.method_43471(class_156.method_646("upgrade", class_2960.method_60656("netherite_upgrade")))
            .method_27692(class_124.field_1080));
        return template;
    }
}
