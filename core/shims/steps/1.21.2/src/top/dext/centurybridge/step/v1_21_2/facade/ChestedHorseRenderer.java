package top.dext.centurybridge.step.v1_21_2.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's ChestedHorseRenderer (net/minecraft/class_883, classRenames): the donkey / mule renderer over a
 * ChestedHorseModel of the given layer, scaled, its texture looked up by the entity's type (donkey or mule texture,
 * none for another type -- a mod's subclass answers its own). 1.21.2's DonkeyRenderer is that renderer with a baby layer
 * and the texture fixed per renderer; this one is built on the given layer, the baby layer being it under the equine
 * BABY_TRANSFORMER (AbstractEquineModel's: 1.21.1's young scaling of the same mesh, CbRenderBabyLayers), and answers
 * 1.21.1's per-entity texture (getTextureLocation(entity), overridable) for the entity the state was extracted from.
 */
@Environment(EnvType.CLIENT)
public class ChestedHorseRenderer<T extends net.minecraft.class_1492> extends net.minecraft.class_9991<T> {

    private static final net.minecraft.class_2960 DONKEY = net.minecraft.class_2960.method_60656("textures/entity/horse/donkey.png");
    private static final net.minecraft.class_2960 MULE = net.minecraft.class_2960.method_60656("textures/entity/horse/mule.png");

    public ChestedHorseRenderer(net.minecraft.class_5617.class_5618 context, float scale, net.minecraft.class_5601 layer) {
        super(context, scale, layer, top.dext.centurybridge.step.v1_21_2.rt.CbRenderBabyLayers.equineBaby(layer), false);
    }

    /** getTextureLocation(entity): 1.21.1's map by entity type */
    public net.minecraft.class_2960 method_3894(T entity) {
        net.minecraft.class_1299<?> type = entity.method_5864();
        return type == net.minecraft.class_1299.field_6067 ? DONKEY : type == net.minecraft.class_1299.field_6057 ? MULE : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public net.minecraft.class_2960 method_62414(net.minecraft.class_10013 state) {
        if (top.dext.centurybridge.step.v1_21_2.rt.CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1492 entity) {
            return this.method_3894((T) entity);
        }
        return super.method_62414(state);
    }
}
