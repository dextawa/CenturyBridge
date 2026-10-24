package top.dext.centurybridge.step.v26_1.rt;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.monster.piglin.AbstractPiglinModel;

/**
 * 1.21.11's AbstractPiglinModel.leftSleeve / rightSleeve / leftPants / rightPants / jacket. 1.21.11 built every
 * piglin from the player mesh and kept these overlay parts in fields (the sleeves are children of the arms, the
 * pants of the legs, the jacket of the body). 26.1 dropped the fields; its adult piglins (AdultPiglinModel,
 * AdultZombifiedPiglinModel) are still built from PlayerModel.createMesh and carry the same parts at the same
 * places, so a field reads that part. 26.1's baby piglins have a mesh of their own without overlay parts (1.21.11 had
 * no separate baby mesh): there the field is an empty part of the model's own, with nothing to draw -- the mesh has
 * no such geometry to show or hide.
 * <p>
 * 26.1 also made the ears' resting angle an abstract getDefaultEarAngleInDegrees() (adult 30, baby 5; package-private,
 * opened by the layer's access widener): a 1.21.11 subclass, which has none, gets 1.21.11's angle -- 30 degrees
 * (0.5235988 rad), the only one 1.21.11 had (abstractFills).
 */
@Environment(EnvType.CLIENT)
public final class CbWallPiglinParts {

    private CbWallPiglinParts() {
    }

    private static final Map<AbstractPiglinModel<?>, Map<String, ModelPart>> EMPTY = new WeakHashMap<>();

    private static ModelPart part(AbstractPiglinModel<?> model, ModelPart parent, String name) {
        if (parent.hasChild(name)) {
            return parent.getChild(name);
        }
        synchronized (EMPTY) {
            return EMPTY.computeIfAbsent(model, m -> new java.util.HashMap<>())
                    .computeIfAbsent(name, n -> new ModelPart(List.of(), Map.of()));
        }
    }

    public static ModelPart leftSleeve(AbstractPiglinModel<?> model) {
        return part(model, model.leftArm, "left_sleeve");
    }

    public static ModelPart rightSleeve(AbstractPiglinModel<?> model) {
        return part(model, model.rightArm, "right_sleeve");
    }

    public static ModelPart leftPants(AbstractPiglinModel<?> model) {
        return part(model, model.leftLeg, "left_pants");
    }

    public static ModelPart rightPants(AbstractPiglinModel<?> model) {
        return part(model, model.rightLeg, "right_pants");
    }

    public static ModelPart jacket(AbstractPiglinModel<?> model) {
        return part(model, model.body, "jacket");
    }

    public static float defaultEarAngle(AbstractPiglinModel<?> model) {
        return 30.0F;
    }
}
