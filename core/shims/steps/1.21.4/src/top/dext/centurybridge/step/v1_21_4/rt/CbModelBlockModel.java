package top.dext.centurybridge.step.v1_21_4.rt;

import com.mojang.datafixers.util.Either;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.dext.centurybridge.step.v1_21_4.facade.ItemOverride;

/**
 * 1.21.3 BlockModel (class_793) on 1.21.4's.
 * <p>
 * 1.21.4 keeps the class and its parsed state (elements, ambient occlusion, gui light, transforms, parent) but answers
 * only its own values; the parent chain is walked by UnbakedModel's statics and baking moved to bake(textureSlots, ..).
 * 1.21.3 answered every property through the chain itself and baked from it. Every BlockModel gets 1.21.3's members
 * back ({@link CbLegacyBlockModel}, implemented by CbModelBlockModelMixin), with 1.21.3's bodies over 1.21.4's state:
 * <ul>
 * <li>the parent: 1.21.3's was always a BlockModel (resolveDependencies refused anything else); 1.21.4's may be another
 *     UnbakedModel (the item model generator behind builtin/generated). Where 1.21.3 asked the parent BlockModel
 *     virtually it still does; another parent answers through 1.21.4's chain statics, which walk on from it;</li>
 * <li>the texture map: 1.21.3's mutable Map of slot to material-or-reference, 1.21.4's TextureSlots.Data. The map is made
 *     from the data the first time it is asked for, and from then on is the model's texture slots (the mixin answers
 *     textureSlots() from it), so a write through it reaches baking as in 1.21.3;</li>
 * <li>the overrides (item model overrides, gone from 1.21.4's BlockModel): kept with the model, from the constructor or
 *     the model file;</li>
 * <li>getElements / getGuiLight, which 1.21.4 still declares with its own-value bodies (getElements package-private),
 *     are reached through statics here: a mod's calls get the chain's answer, its overrides are kept where 1.21.3 asked
 *     them.</li>
 * </ul>
 * A mod's subclass of BlockModel bakes as 1.21.3 baked (its own bake, or 1.21.3's bake through its overridden members),
 * a vanilla model as 1.21.4 bakes.
 */
@Environment(EnvType.CLIENT)
public final class CbModelBlockModel {
    private CbModelBlockModel() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("CenturyBridge/BlockModel");

    /** 1.21.3 BlockModel.PARTICLE_TEXTURE_REFERENCE */
    public static final String PARTICLE = "particle";

    /** BlockModel's 1.21.3 members (CbModelBlockModelMixin); a mod's subclass overrides them under the same names */
    public interface CbLegacyBlockModel extends CbModelBaking.CbLegacyUnbakedModel {
        /** was getElements() (method_3433): renamed, 1.21.4 declares a package-private method of that shape */
        List<net.minecraft.class_785> cb$getElements();

        boolean method_3444();

        boolean method_35789();

        List<ItemOverride> method_3434();

        net.minecraft.class_1087 method_3446(Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state, boolean guiLight3d);

        boolean method_3432(String name);

        net.minecraft.class_4730 method_24077(String name);

        net.minecraft.class_793 method_3431();

        net.minecraft.class_809 method_3443();

        /** the model's own state */
        List<net.minecraft.class_785> cb$ownElements();

        net.minecraft.class_1100.class_4751 cb$ownGuiLight();

        net.minecraft.class_1100 cb$parent();

        void cb$setParent(net.minecraft.class_1100 parent);

        net.minecraft.class_2960 cb$parentLocation();

        Map<String, Either<net.minecraft.class_4730, String>> cb$textureMap();

        void cb$setTextureMap(Map<String, Either<net.minecraft.class_4730, String>> map);

        void cb$setOverrides(List<ItemOverride> overrides);

        /** the name field (field_4252) */
        String cb$name();

        void cb$setName(String name);
    }

    private static CbLegacyBlockModel legacy(net.minecraft.class_793 model) {
        return (CbLegacyBlockModel) model;
    }

    // ---- the texture map <-> TextureSlots.Data ----

    /** TextureSlots.Value / Reference, reached through accessor mixins (package-private records) */
    public interface CbSlotValue {
        net.minecraft.class_4730 cb$material();
    }

    public interface CbSlotReference {
        String cb$target();
    }

    public static Map<String, Either<net.minecraft.class_4730, String>> toMap(net.minecraft.class_10419.class_10420 data) {
        Map<String, Either<net.minecraft.class_4730, String>> map = new LinkedHashMap<>();
        if (data != null) {
            data.comp_3376().forEach((slot, contents) -> {
                Either<net.minecraft.class_4730, String> e = toEither(contents);
                if (e != null) {
                    map.put(slot, e);
                }
            });
        }
        return map;
    }

    private static Either<net.minecraft.class_4730, String> toEither(Object contents) {
        if (contents instanceof CbSlotValue v) {
            return Either.left(v.cb$material());
        }
        if (contents instanceof CbSlotReference r) {
            return Either.right(r.cb$target());
        }
        return null;
    }

    public static net.minecraft.class_10419.class_10420 toData(Map<String, Either<net.minecraft.class_4730, String>> map) {
        net.minecraft.class_10419.class_10420.class_10421 builder = new net.minecraft.class_10419.class_10420.class_10421();
        map.forEach((slot, e) -> e.ifLeft(material -> builder.method_65548(slot, material)).ifRight(target -> builder.method_65549(slot, target)));
        return builder.method_65547();
    }

    // ---- constructors ----

    /** was new BlockModel(parentLocation, elements, textureMap, ambientOcclusion, guiLight, transforms, overrides) */
    public static net.minecraft.class_793 create(net.minecraft.class_2960 parentLocation, List<net.minecraft.class_785> elements,
        Map<String, Either<net.minecraft.class_4730, String>> textureMap, Boolean ambientOcclusion, net.minecraft.class_1100.class_4751 guiLight,
        net.minecraft.class_809 transforms, List<ItemOverride> overrides) {
        net.minecraft.class_793 model = new net.minecraft.class_793(parentLocation, elements, toData(textureMap), ambientOcclusion, guiLight, transforms);
        constructed(model, textureMap, overrides);
        return model;
    }

    /** a mod subclass's super(..): the texture map the 1.21.4 constructor takes as data */
    public static net.minecraft.class_10419.class_10420 cbData(Map<String, Either<net.minecraft.class_4730, String>> textureMap) {
        return toData(textureMap);
    }

    /** after the 1.21.4 constructor: the model keeps the very map (1.21.3's textureMap field was the map passed) and its overrides */
    public static void constructed(net.minecraft.class_793 model, Map<String, Either<net.minecraft.class_4730, String>> textureMap,
        List<ItemOverride> overrides) {
        legacy(model).cb$setTextureMap(textureMap);
        legacy(model).cb$setOverrides(overrides == null ? List.of() : overrides);
    }

    // ---- fields ----

    /** was textureMap (field_4251) */
    public static Map<String, Either<net.minecraft.class_4730, String>> field_4251(net.minecraft.class_793 model) {
        return legacy(model).cb$textureMap();
    }

    /** was parent (field_4253): 1.21.3's parent was always a BlockModel */
    public static net.minecraft.class_793 field_4253(net.minecraft.class_793 model) {
        return legacy(model).cb$parent() instanceof net.minecraft.class_793 parent ? parent : null;
    }

    /** a write of parent (field_4253) */
    public static void field_4253(net.minecraft.class_793 model, net.minecraft.class_793 parent) {
        legacy(model).cb$setParent(parent);
    }

    /** was BlockModel.MISSING_MATERIAL (field_54858) */
    public static net.minecraft.class_4730 field_54858() {
        return CbModelBaking.MISSING;
    }

    /** was BlockModel.PARTICLE_TEXTURE_REFERENCE (field_32792) */
    public static String field_32792() {
        return PARTICLE;
    }

    // ---- members 1.21.4 still declares with other bodies ----

    /** was getElements() (method_3433), called by a mod: the own elements, else the parent's */
    public static List<net.minecraft.class_785> method_3433(net.minecraft.class_793 model) {
        return legacy(model).cb$getElements();
    }

    /** 1.21.3's getElements body */
    public static List<net.minecraft.class_785> elements(CbLegacyBlockModel model) {
        List<net.minecraft.class_785> own = model.cb$ownElements();
        if (own.isEmpty() && model.cb$parent() instanceof net.minecraft.class_793 parent) {
            return legacy(parent).cb$getElements();
        }
        return own;
    }

    /** was getGuiLight() (method_24298), called by a mod: the model's answer (its own override, or its own value), else the chain's */
    public static net.minecraft.class_1100.class_4751 method_24298(net.minecraft.class_793 model) {
        net.minecraft.class_1100.class_4751 own = model.method_24298();
        return own != null ? own : parentGuiLight(legacy(model));
    }

    /** a mod's super.getGuiLight(): 1.21.3's body */
    public static net.minecraft.class_1100.class_4751 superGetGuiLight(net.minecraft.class_793 model) {
        net.minecraft.class_1100.class_4751 own = legacy(model).cb$ownGuiLight();
        return own != null ? own : parentGuiLight(legacy(model));
    }

    private static net.minecraft.class_1100.class_4751 parentGuiLight(CbLegacyBlockModel model) {
        net.minecraft.class_1100 parent = model.cb$parent();
        if (parent instanceof net.minecraft.class_793 bm) {
            return method_24298(bm);
        }
        if (parent != null) {
            return net.minecraft.class_1100.method_65767(parent);
        }
        return net.minecraft.class_1100.class_4751.field_21859;
    }

    // ---- 1.21.3 bodies of the members 1.21.4 dropped ----

    /** hasAmbientOcclusion() */
    public static boolean hasAmbientOcclusion(CbLegacyBlockModel model, Boolean own) {
        if (own != null) {
            return own;
        }
        net.minecraft.class_1100 parent = model.cb$parent();
        if (parent instanceof net.minecraft.class_793 bm) {
            return legacy(bm).method_3444();
        }
        return parent == null || net.minecraft.class_1100.method_65763(parent);
    }

    /** isResolved() */
    public static boolean isResolved(CbLegacyBlockModel model) {
        if (model.cb$parentLocation() == null) {
            return true;
        }
        net.minecraft.class_1100 parent = model.cb$parent();
        if (parent instanceof net.minecraft.class_793 bm) {
            return legacy(bm).method_35789();
        }
        return parent != null;
    }

    /**
     * getRootModel(): the parent's root, else the model itself; a chain 1.21.4 ends in its builtin/generated model (the item
     * model generator) has 1.21.3's root, GENERATED_MARKER
     */
    public static net.minecraft.class_793 rootModel(net.minecraft.class_793 model) {
        net.minecraft.class_1100 parent = legacy(model).cb$parent();
        if (parent instanceof net.minecraft.class_793 bm) {
            return legacy(bm).method_3431();
        }
        if (parent instanceof net.minecraft.class_801) {
            return CbModelSpecial.generatedMarker();
        }
        return model;
    }

    /** the last BlockModel of the model's chain of BlockModel parents */
    public static net.minecraft.class_793 lastBlockModel(net.minecraft.class_793 model) {
        net.minecraft.class_793 m = model;
        while (legacy(m).cb$parent() instanceof net.minecraft.class_793 parent) {
            m = parent;
        }
        return m;
    }

    /** hasTexture(name) */
    public static boolean hasTexture(CbLegacyBlockModel model, String name) {
        return !net.minecraft.class_1047.method_4539().equals(model.method_24077(name).method_24147());
    }

    /** getMaterial(name): references followed through the model and its parents, the missing material for a loop or a gap */
    public static net.minecraft.class_4730 material(net.minecraft.class_793 model, String name, String modelName) {
        if (isReference(name)) {
            name = name.substring(1);
        }
        List<String> seen = new ArrayList<>();
        while (true) {
            Either<net.minecraft.class_4730, String> entry = findEntry(model, name);
            java.util.Optional<net.minecraft.class_4730> material = entry.left();
            if (material.isPresent()) {
                return material.get();
            }
            name = entry.right().get();
            if (seen.contains(name)) {
                LOGGER.warn("Unable to resolve texture due to reference chain {}->{} in {}", String.join("->", seen), name, modelName);
                return CbModelBaking.MISSING;
            }
            seen.add(name);
        }
    }

    static boolean isReference(String name) {
        return name.charAt(0) == '#';
    }

    /** 1.21.3 findTextureEntry: the model's own entry, else its parent's, else the missing material */
    private static Either<net.minecraft.class_4730, String> findEntry(net.minecraft.class_1100 model, String name) {
        for (net.minecraft.class_1100 m = model; m != null; ) {
            Either<net.minecraft.class_4730, String> own;
            net.minecraft.class_1100 next;
            if (m instanceof net.minecraft.class_793 bm) {
                own = legacy(bm).cb$textureMap().get(name);
                next = legacy(bm).cb$parent();
            } else {
                net.minecraft.class_10419.class_10420 data = m.method_65539();
                own = data == null ? null : toEither(data.comp_3376().get(name));
                next = m.method_65538();
            }
            if (own != null) {
                return own;
            }
            m = next;
        }
        return Either.left(CbModelBaking.MISSING);
    }

    /** getTransforms(): per display context, the own transform where it has one, else the parent's */
    public static net.minecraft.class_809 transforms(net.minecraft.class_793 model) {
        return new net.minecraft.class_809(
            transform(model, net.minecraft.class_811.field_4323), transform(model, net.minecraft.class_811.field_4320),
            transform(model, net.minecraft.class_811.field_4321), transform(model, net.minecraft.class_811.field_4322),
            transform(model, net.minecraft.class_811.field_4316), transform(model, net.minecraft.class_811.field_4317),
            transform(model, net.minecraft.class_811.field_4318), transform(model, net.minecraft.class_811.field_4319));
    }

    private static net.minecraft.class_804 transform(net.minecraft.class_793 model, net.minecraft.class_811 context) {
        net.minecraft.class_809 own = model.method_65540();
        net.minecraft.class_1100 parent = legacy(model).cb$parent();
        if (parent != null && (own == null || !CbModelTransforms.method_3501(own, context))) {
            if (parent instanceof net.minecraft.class_793 bm) {
                return transform(bm, context);
            }
            return net.minecraft.class_1100.method_65764(parent, context);
        }
        return own == null ? net.minecraft.class_804.field_4284 : own.method_3503(context);
    }

    /**
     * bake(spriteGetter, state, guiLight3d): 1.21.3's body -- the builtin/entity marker's chain is a BuiltInModel, any other a
     * SimpleBakedModel of the chain's elements, every member asked through the model (a subclass's overrides count)
     */
    public static net.minecraft.class_1087 bake(net.minecraft.class_793 model, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
        net.minecraft.class_3665 state, boolean guiLight3d) {
        CbLegacyBlockModel m = legacy(model);
        net.minecraft.class_1058 particle = sprites.apply(m.method_24077(PARTICLE));
        if (m.method_3431() == CbModelSpecial.blockEntityMarker()) {
            return new top.dext.centurybridge.step.v1_21_4.facade.BuiltInModel(m.method_3443(), particle, method_24298(model).method_24299());
        }
        net.minecraft.class_1093.class_1094 builder = new net.minecraft.class_1093.class_1094(m.method_3444(), method_24298(model).method_24299(),
            guiLight3d, m.method_3443()).method_4747(particle);
        for (net.minecraft.class_785 element : m.cb$getElements()) {
            for (net.minecraft.class_2350 direction : element.field_4230.keySet()) {
                net.minecraft.class_783 face = element.field_4230.get(direction);
                net.minecraft.class_1058 sprite = sprites.apply(m.method_24077(face.comp_2869()));
                if (face.comp_2867() == null) {
                    builder.method_4748(bakeFace(element, face, sprite, direction, state));
                } else {
                    builder.method_4745(net.minecraft.class_2350.method_23225(state.method_3509().method_22936(), face.comp_2867()),
                        bakeFace(element, face, sprite, direction, state));
                }
            }
        }
        return builder.method_4746();
    }

    /** 1.21.3 BlockModel.bakeFace (its static FaceBakery's bakeQuad) */
    private static net.minecraft.class_777 bakeFace(net.minecraft.class_785 element, net.minecraft.class_783 face, net.minecraft.class_1058 sprite,
        net.minecraft.class_2350 direction, net.minecraft.class_3665 state) {
        return net.minecraft.class_796.method_3468(element.field_4228, element.field_4231, face, sprite, direction, state, element.field_4232,
            element.field_4229, element.field_53159);
    }

    /** SimpleBakedModel.Builder(model, guiLight3d) and its subclasses' super(..): the model's 1.21.3 answers */
    public static net.minecraft.class_1093.class_1094 builder(net.minecraft.class_793 model, boolean guiLight3d) {
        return new net.minecraft.class_1093.class_1094(builderAmbientOcclusion(model), builderBlockLight(model), guiLight3d,
            builderTransforms(model));
    }

    public static boolean builderAmbientOcclusion(net.minecraft.class_793 model) {
        return legacy(model).method_3444();
    }

    public static boolean builderBlockLight(net.minecraft.class_793 model) {
        return method_24298(model).method_24299();
    }

    public static net.minecraft.class_809 builderTransforms(net.minecraft.class_793 model) {
        return legacy(model).method_3443();
    }

    // ---- the model file's overrides ----

    /** 1.21.3 BlockModel.Deserializer.getOverrides(context, json) (ItemOverride.Deserializer per element) */
    public static List<ItemOverride> overrides(com.google.gson.JsonObject json) {
        List<ItemOverride> list = new ArrayList<>();
        if (json.has("overrides")) {
            for (com.google.gson.JsonElement element : net.minecraft.class_3518.method_15261(json, "overrides")) {
                com.google.gson.JsonObject o = element.getAsJsonObject();
                net.minecraft.class_2960 model = net.minecraft.class_2960.method_60654(net.minecraft.class_3518.method_15265(o, "model"));
                Map<net.minecraft.class_2960, Float> predicates = new LinkedHashMap<>();
                for (Map.Entry<String, com.google.gson.JsonElement> e : net.minecraft.class_3518.method_15296(o, "predicate").entrySet()) {
                    predicates.put(net.minecraft.class_2960.method_60654(e.getKey()), net.minecraft.class_3518.method_15269(e.getValue(), e.getKey()));
                }
                List<ItemOverride.Predicate> list2 = new ArrayList<>();
                predicates.forEach((k, v) -> list2.add(new ItemOverride.Predicate(k, v)));
                list.add(new ItemOverride(model, com.google.common.collect.ImmutableList.copyOf(list2)));
            }
        }
        return list;
    }
}
