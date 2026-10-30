package top.dext.centurybridge.step.v1_21_9.rt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_11677;
import net.minecraft.class_5601;
import net.minecraft.class_5602;
import net.minecraft.class_5605;
import net.minecraft.class_5609;

/**
 * 1.21.8's inner / outer armor model layers, given back on 1.21.9's armor model sets.
 *
 * <p>1.21.8 gave every armored humanoid two layers, {@code <model>#inner_armor} and {@code <model>#outer_armor}: the
 * whole armor mesh at the inner and at the outer cube deformation (javap -c 1.21.8 ModelLayers: registerInnerArmor /
 * registerOuterArmor; LayerDefinitions.createRoots puts one definition under each). 1.21.9 replaced the pair with an
 * ArmorModelSet of four layers, {@code helmet / chestplate / leggings / boots} (ModelLayers.registerArmorSet), each
 * built by HumanoidModel.createArmorMeshSet(meshOf, inner, outer) from {@code meshOf(deformation)} and then cut to that
 * slot's parts (javap -c 1.21.9 HumanoidModel.method_72853: retainExactParts / retainPartsAndChildren), so no 1.21.9
 * layer carries the whole mesh any more.
 *
 * <p>The 1.21.8 layers are rebuilt from the same function: where createArmorMeshSet returns, the whole mesh at each
 * deformation is built once more ({@code meshOf.apply(inner)}, {@code meshOf.apply(outer)}) and kept beside the set;
 * every {@code ArmorModelSet.map} 1.21.9 then applies to the set (baby scaling, the layer definition) is applied to
 * the two whole meshes as well; and where LayerDefinitions puts a vanilla set's definitions into the roots
 * ({@code ArmorModelSet.putFrom}) the two land under the 1.21.8 locations. A 1.21.8 mod's
 * {@code bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)} thus bakes 1.21.9's player armor mesh, whole, at the inner
 * deformation. The 1.21.8 locations are not added to ModelLayers' known set: they are extra roots, never required.
 */
@Environment(EnvType.CLIENT)
public final class CbArmorLayers {
    private CbArmorLayers() {
    }

    /** a tracked armor set -> {whole inner, whole outer}, keyed by identity, held as long as the set is */
    private static final Map<Object, Object[]> WHOLE = new MapMaker().weakKeys().makeMap();

    /** the vanilla location sets the 1.21.8 inner / outer fields stood for (ModelLayers, by model name) */
    private static final Set<Object> VANILLA = Collections.newSetFromMap(new MapMaker().weakKeys().makeMap());

    static {
        Collections.addAll(VANILLA, class_5602.field_61719, class_5602.field_61720, class_5602.field_61684,
            class_5602.field_61687, class_5602.field_61686, class_5602.field_61696, class_5602.field_61695,
            class_5602.field_61694, class_5602.field_61700, class_5602.field_61698, class_5602.field_61699,
            class_5602.field_61701, class_5602.field_61697, class_5602.field_61708, class_5602.field_61709,
            class_5602.field_61712, class_5602.field_61714, class_5602.field_61713, class_5602.field_61718,
            class_5602.field_61717, class_5602.field_61711, class_5602.field_61710);
    }

    /** 1.21.8's {@code <model>#inner_armor} of the model a 1.21.9 location set is for */
    public static class_5601 inner(class_11677<class_5601> set) {
        return new class_5601(set.comp_4542().comp_2995(), "inner_armor");
    }

    /** 1.21.8's {@code <model>#outer_armor} of the model a 1.21.9 location set is for */
    public static class_5601 outer(class_11677<class_5601> set) {
        return new class_5601(set.comp_4542().comp_2995(), "outer_armor");
    }

    /** HumanoidModel.createArmorMeshSet returned {@code set}: build the whole mesh at both deformations beside it */
    public static void built(class_11677<class_5609> set, Function<class_5605, class_5609> meshOf, class_5605 inner,
                             class_5605 outer) {
        if (set != null) {
            WHOLE.put(set, new Object[]{meshOf.apply(inner), meshOf.apply(outer)});
        }
    }

    /** ArmorModelSet.map(fn) turned {@code from} into {@code to}: the whole meshes go through {@code fn} as well */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void mapped(Object from, Object to, Function fn) {
        Object[] whole = WHOLE.get(from);
        if (whole != null && to != null) {
            WHOLE.put(to, new Object[]{fn.apply(whole[0]), fn.apply(whole[1])});
        }
    }

    /**
     * ArmorModelSet.putFrom(definitions, roots) on location set {@code locations}: a vanilla set's whole definitions
     * also go in under 1.21.8's inner / outer locations
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void put(class_11677<class_5601> locations, Object definitions, ImmutableMap.Builder roots) {
        if (!VANILLA.contains(locations)) {
            return;
        }
        Object[] whole = WHOLE.get(definitions);
        if (whole != null) {
            roots.put(inner(locations), whole[0]);
            roots.put(outer(locations), whole[1]);
        }
    }
}
