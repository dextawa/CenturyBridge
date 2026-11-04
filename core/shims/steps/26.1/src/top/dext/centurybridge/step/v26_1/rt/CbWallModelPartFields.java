package top.dext.centurybridge.step.v26_1.rt;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.animal.bee.AdultBeeModel;
import net.minecraft.client.model.animal.bee.BeeModel;
import net.minecraft.client.model.animal.turtle.AdultTurtleModel;
import net.minecraft.client.model.animal.turtle.TurtleModel;
import net.minecraft.client.model.animal.wolf.AdultWolfModel;
import net.minecraft.client.model.animal.wolf.WolfModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.strider.AdultStriderModel;
import net.minecraft.client.model.monster.strider.StriderModel;

/**
 * 1.21.11 model part fields (opened by access wideners) 26.1 moved from the model class into its Adult* subclass
 * (javap 26.1: BeeModel / WolfModel / TurtleModel / StriderModel are abstract, AdultBeeModel.leftAntenna / rightAntenna,
 * AdultWolfModel.realHead / realTail / upperBody, AdultTurtleModel.eggBelly, AdultStriderModel's six bristles hold the
 * same parts under the same names). An adult model answers its own part.
 * <p>
 * 1.21.11's baby was the adult mesh scaled (MeshTransformer), so a baby model had these parts too; 26.1's Baby* models
 * are new geometry without them (BabyStriderModel has bristle0..2, the others none). A baby model answers a detached
 * part with no cubes, one per model and field, so reads and pose writes work but draw nothing (a recorded loss).
 */
@Environment(EnvType.CLIENT)
public final class CbWallModelPartFields {
    private CbWallModelPartFields() {
    }

    private static final Map<Object, Map<String, ModelPart>> DETACHED = Collections.synchronizedMap(new WeakHashMap<>());

    private static ModelPart detached(Object model, String field) {
        return DETACHED.computeIfAbsent(model, m -> Collections.synchronizedMap(new java.util.HashMap<>()))
            .computeIfAbsent(field, f -> new ModelPart(List.of(), Map.of()));
    }

    public static ModelPart beeLeftAntenna(BeeModel m) {
        return m instanceof AdultBeeModel a ? a.leftAntenna : detached(m, "leftAntenna");
    }

    public static ModelPart beeRightAntenna(BeeModel m) {
        return m instanceof AdultBeeModel a ? a.rightAntenna : detached(m, "rightAntenna");
    }

    public static ModelPart wolfRealHead(WolfModel m) {
        return m instanceof AdultWolfModel a ? a.realHead : detached(m, "realHead");
    }

    public static ModelPart wolfRealTail(WolfModel m) {
        return m instanceof AdultWolfModel a ? a.realTail : detached(m, "realTail");
    }

    public static ModelPart wolfUpperBody(WolfModel m) {
        return m instanceof AdultWolfModel a ? a.upperBody : detached(m, "upperBody");
    }

    public static ModelPart turtleEggBelly(TurtleModel m) {
        return m instanceof AdultTurtleModel a ? a.eggBelly : detached(m, "eggBelly");
    }

    public static ModelPart striderRightBottomBristle(StriderModel m) {
        return m instanceof AdultStriderModel a ? a.rightBottomBristle : detached(m, "rightBottomBristle");
    }

    public static ModelPart striderRightMiddleBristle(StriderModel m) {
        return m instanceof AdultStriderModel a ? a.rightMiddleBristle : detached(m, "rightMiddleBristle");
    }

    public static ModelPart striderRightTopBristle(StriderModel m) {
        return m instanceof AdultStriderModel a ? a.rightTopBristle : detached(m, "rightTopBristle");
    }

    public static ModelPart striderLeftTopBristle(StriderModel m) {
        return m instanceof AdultStriderModel a ? a.leftTopBristle : detached(m, "leftTopBristle");
    }

    public static ModelPart striderLeftMiddleBristle(StriderModel m) {
        return m instanceof AdultStriderModel a ? a.leftMiddleBristle : detached(m, "leftMiddleBristle");
    }

    public static ModelPart striderLeftBottomBristle(StriderModel m) {
        return m instanceof AdultStriderModel a ? a.leftBottomBristle : detached(m, "leftBottomBristle");
    }
}
