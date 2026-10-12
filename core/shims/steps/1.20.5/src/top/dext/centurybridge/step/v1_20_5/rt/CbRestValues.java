package top.dext.centurybridge.step.v1_20_5.rt;

/**
 * 1.20.4 values 1.20.5 dropped or renamed, as 1.20.4 had them: the mobs' temptation ingredients (1.20.5
 * tests a predicate or an item tag of the same items), the tags 1.20.5 renamed or merged (the same
 * contents under 1.20.5's id), the tools tag 1.20.5 removed (its 1.20.4 definition ships in this layer's
 * data, data/minecraft/tags/items/tools.json), the wind burst sound 1.20.5 renamed.
 */
public final class CbRestValues {

    /** AxolotlAi.getTemptations(): the axolotl tempt items, which 1.20.5 names axolotl_food (same items) */
    public static net.minecraft.class_1856 method_33241() {
        return net.minecraft.class_1856.method_8106(net.minecraft.class_3489.field_49957);
    }

    /** CamelAi.getTemptations(): Camel.TEMPTATION_ITEM */
    public static net.minecraft.class_1856 method_45368() {
        return field_40131();
    }

    /** FrogAi.getTemptations(): Frog.TEMPTATION_ITEM */
    public static net.minecraft.class_1856 method_41366() {
        return field_37448();
    }

    /** GoatAi.getTemptations(): wheat */
    public static net.minecraft.class_1856 method_35181() {
        return net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_8861);
    }

    /** SnifferAi.getTemptations(): torchflower seeds */
    public static net.minecraft.class_1856 method_51159() {
        return net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_42711);
    }

    private static net.minecraft.class_1856 camel;
    private static net.minecraft.class_1856 frog;
    private static net.minecraft.class_1856 turtle;

    /** Camel.TEMPTATION_ITEM: cactus */
    public static synchronized net.minecraft.class_1856 field_40131() {
        if (camel == null) {
            camel = net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_17520);
        }
        return camel;
    }

    /** Frog.TEMPTATION_ITEM: slime balls */
    public static synchronized net.minecraft.class_1856 field_37448() {
        if (frog == null) {
            frog = net.minecraft.class_1856.method_8091(net.minecraft.class_1802.field_8777);
        }
        return frog;
    }

    /** Turtle.FOOD_ITEMS: seagrass */
    public static synchronized net.minecraft.class_1856 field_28406() {
        if (turtle == null) {
            turtle = net.minecraft.class_1856.method_8091(net.minecraft.class_2246.field_10376.method_8389());
        }
        return turtle;
    }

    /** ItemTags.AXOLOTL_TEMPT_ITEMS: 1.20.5's axolotl_food, the same items under a new id */
    public static net.minecraft.class_6862<net.minecraft.class_1792> field_28299() {
        return net.minecraft.class_3489.field_49957;
    }

    /** ItemTags.TOOLS: minecraft:tools, 1.20.4's tag (its definition is this layer's data) */
    public static net.minecraft.class_6862<net.minecraft.class_1792> field_42616() {
        return net.minecraft.class_6862.method_40092(net.minecraft.class_7924.field_41197, new net.minecraft.class_2960("tools"));
    }

    /** EntityTypeTags.DEFLECTS_ARROWS: 1.20.5 merged it and DEFLECTS_TRIDENTS into deflects_projectiles (both: the breeze) */
    public static net.minecraft.class_6862<net.minecraft.class_1299<?>> field_47215() {
        return net.minecraft.class_3483.field_48286;
    }

    /** EntityTypeTags.DEFLECTS_TRIDENTS: see DEFLECTS_ARROWS */
    public static net.minecraft.class_6862<net.minecraft.class_1299<?>> field_47216() {
        return net.minecraft.class_3483.field_48286;
    }

    /** SoundEvents.WIND_BURST (entity.generic.wind_burst): 1.20.5's entity.wind_charge.wind_burst, the same sounds */
    public static net.minecraft.class_3414 field_47191() {
        return net.minecraft.class_3417.field_49044.comp_349();
    }

    private CbRestValues() {
    }
}
