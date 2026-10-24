package top.dext.centurybridge.step.v1_21_2.rt;

import java.util.Collection;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's model discovery over 1.21.2's.
 *
 * 1.21.1: an UnbakedModel named the models it needed (getDependencies()), the bakery loaded them, and then linked the
 * model to them through a getter (resolveParents(Function&lt;ResourceLocation, UnbakedModel&gt;)); while baking, the
 * ModelBaker answered any discovered model (getModel(id)). 1.21.2: one call, resolveDependencies(Resolver), in which
 * the model asks the resolver for each model it needs -- the resolver records the dependency and answers the model --
 * and the baker no longer hands out unbaked models publicly (ModelBakery's baker keeps getModel(id) privately; the
 * layer's access widener opens it, CbRenderBakeBakerMixin exposes it on the interface).
 *
 * A 1.21.1 model class gets 1.21.2's resolveDependencies synthesized (overrideHooks on its resolveParents, {@link #resolveDependencies}):
 * every dependency it names is resolved (discovered), then its resolveParents runs with the resolver as its getter --
 * what 1.21.1's bakery did in two passes. A 1.21.2 model asked 1.21.1's resolveParents (CbRenderBakeUnbakedMixin) runs
 * its resolveDependencies with the getter as resolver.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderBake {

    /** UnbakedModel's 1.21.1 methods (CbRenderBakeUnbakedMixin makes UnbakedModel extend this) */
    public interface Legacy {
        Collection<net.minecraft.class_2960> method_4755();

        void method_45785(Function<net.minecraft.class_2960, net.minecraft.class_1100> getter);
    }

    /** ModelBaker's 1.21.1 getModel(id) (CbRenderBakeBakerMixin makes ModelBaker extend this) */
    public interface Baker {
        net.minecraft.class_1100 method_45872(net.minecraft.class_2960 id);
    }

    private CbRenderBake() {
    }

    // ---- 1.21.1 ModelBakery constants (fieldGetters), each answered with 1.21.2's counterpart ----

    /** MISSING_MODEL_LOCATION (builtin/missing): MissingBlockModel.LOCATION */
    public static net.minecraft.class_2960 field_5374() {
        return net.minecraft.class_10096.field_53660;
    }

    /** MISSING_MODEL_VARIANT (builtin/missing#missing): MissingBlockModel.VARIANT */
    public static net.minecraft.class_1091 field_52276() {
        return net.minecraft.class_10096.field_53661;
    }

    private static final net.minecraft.class_7654 MODEL_LISTER = net.minecraft.class_7654.method_45114("models");

    /** MODEL_LISTER: FileToIdConverter.json("models"), as 1.21.1 built it */
    public static net.minecraft.class_7654 field_40570() {
        return MODEL_LISTER;
    }

    private static final String MISSING_MODEL_MESH;

    static {
        String m = net.minecraft.class_1047.method_4539().method_12832();
        MISSING_MODEL_MESH = ("{    'textures': {       'particle': '" + m + "',       'missingno': '" + m + "'    },    'elements': [         {  'from': [ 0, 0, 0 ],"
            + "            'to': [ 16, 16, 16 ],            'faces': {"
            + "                'down':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'down',  'texture': '#missingno' },"
            + "                'up':    { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'up',    'texture': '#missingno' },"
            + "                'north': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'north', 'texture': '#missingno' },"
            + "                'south': { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'south', 'texture': '#missingno' },"
            + "                'west':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'west',  'texture': '#missingno' },"
            + "                'east':  { 'uv': [ 0, 0, 16, 16 ], 'cullface': 'east',  'texture': '#missingno' }            }        }    ]}")
            .replace('\'', '"');
    }

    /** MISSING_MODEL_MESH: 1.21.1's json text of the missing model (its literal, with the missing texture's path) */
    public static String field_5371() {
        return MISSING_MODEL_MESH;
    }

    /** GENERATION_MARKER: SpecialModels.GENERATED_MARKER, the BlockModel "builtin/generated" resolves to in 1.21.2 */
    public static net.minecraft.class_793 field_5400() {
        return (net.minecraft.class_793) net.minecraft.class_10101.field_53679;
    }

    /** BLOCK_ENTITY_MARKER: SpecialModels.BLOCK_ENTITY_MARKER, the BlockModel "builtin/entity" resolves to in 1.21.2 */
    public static net.minecraft.class_793 field_5389() {
        return (net.minecraft.class_793) net.minecraft.class_10101.field_53680;
    }

    /**
     * 1.21.1 ItemOverrides(baker, model, overrides) baked each override's model unless it was the owning model itself
     * (class_806.method_33695: Objects.equals(baker.getModel(id), model) -> null, which resolve answered as the base
     * model). 1.21.2's BakedOverrides(baker, overrides) bakes every one, and answers null for a null model (the caller
     * then keeps the base). The baker handed over answers null for the owning model, as 1.21.1 did; every other bake
     * goes to the real baker.
     */
    public static net.minecraft.class_7775 selfSkipping(net.minecraft.class_7775 baker, net.minecraft.class_793 model) {
        return new net.minecraft.class_7775() {
            @Override
            public net.minecraft.class_1087 method_45873(net.minecraft.class_2960 id, net.minecraft.class_3665 state) {
                if (java.util.Objects.equals(((Baker) baker).method_45872(id), model)) {
                    return null;
                }
                return baker.method_45873(id, state);
            }
        };
    }

    /** 1.21.1 new ItemOverrides(baker, model, overrides) (ctorRedirects) */
    public static net.minecraft.class_806 overrides(net.minecraft.class_7775 baker, net.minecraft.class_793 model, java.util.List<net.minecraft.class_799> list) {
        return new net.minecraft.class_806(selfSkipping(baker, model), list);
    }

    /**
     * 1.21.1 ItemOverrides.resolve(base, stack, level, entity, seed) (instanceRedirects): the matching override's model,
     * the base model when none matches or the match has no model; 1.21.2's answers null for both of those.
     */
    public static net.minecraft.class_1087 method_3495(net.minecraft.class_806 overrides, net.minecraft.class_1087 base, net.minecraft.class_1799 stack,
                                                       net.minecraft.class_638 level, net.minecraft.class_1309 entity, int seed) {
        net.minecraft.class_1087 m = overrides.method_64063(stack, level, entity, seed);
        return m != null ? m : base;
    }

    /**
     * 1.21.1 WeightedBakedModel(List of weighted models) (ctorRedirects / superCtorReshapes conv): 1.21.2 takes the same
     * entries as a SimpleWeightedRandomList (same weights, same order).
     */
    public static net.minecraft.class_6005<net.minecraft.class_1087> weighted(java.util.List<net.minecraft.class_6008.class_6010<net.minecraft.class_1087>> list) {
        net.minecraft.class_6005.class_6006<net.minecraft.class_1087> b = net.minecraft.class_6005.method_34971();
        for (net.minecraft.class_6008.class_6010<net.minecraft.class_1087> e : list) {
            b.method_34975(e.comp_2542(), e.comp_2543().method_34976());
        }
        return b.method_34974();
    }

    /** 1.21.1 new WeightedBakedModel(list) (ctorRedirects) */
    public static net.minecraft.class_1097 weightedModel(java.util.List<net.minecraft.class_6008.class_6010<net.minecraft.class_1087>> list) {
        return new net.minecraft.class_1097(weighted(list));
    }

    /** 1.21.1 BlockModel.fromString(json) (staticRedirects): fromStream(new StringReader(json)), as 1.21.1's */
    public static net.minecraft.class_793 method_3430(String json) {
        return net.minecraft.class_793.method_3437(new java.io.StringReader(json));
    }

    /**
     * 1.21.1 BlockModel.bake(baker, owner, spriteGetter, state, usesBlockLight) (instanceRedirects): 1.21.1 baked the
     * model with the item overrides of the owning model built in (ItemOverrides(baker, owner, owner.getOverrides()) --
     * none when it has none); 1.21.2's bake(spriteGetter, state, usesBlockLight) bakes the same model without them. The
     * model 1.21.2 bakes, answering 1.21.1's overrides from getOverrides().
     */
    public static net.minecraft.class_1087 method_3446(net.minecraft.class_793 model, net.minecraft.class_7775 baker, net.minecraft.class_793 owner,
                                                       java.util.function.Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
                                                       net.minecraft.class_3665 state, boolean usesBlockLight) {
        net.minecraft.class_1087 baked = model.method_3446(sprites, state, usesBlockLight);
        java.util.List<net.minecraft.class_799> list = owner.method_3434();
        if (list.isEmpty()) {
            return baked;
        }
        net.minecraft.class_806 overrides = overrides(baker, owner, list);
        return new net.minecraft.class_10200(baked) {
            @Override
            public net.minecraft.class_806 method_4710() {
                return overrides;
            }
        };
    }

    /** 1.21.1 ItemOverride.Predicate.getProperty() (instanceRedirects) */
    public static net.minecraft.class_2960 method_33692(net.minecraft.class_799.class_5826 predicate) {
        return predicate.comp_3195();
    }

    /** 1.21.1 ItemOverride.Predicate.getValue() (instanceRedirects) */
    public static float method_33693(net.minecraft.class_799.class_5826 predicate) {
        return predicate.comp_3196();
    }

    /** 1.21.2 UnbakedModel.resolveDependencies(resolver) of a 1.21.1 model (overrideHooks, on its resolveParents) */
    public static void resolveDependencies(Legacy legacy, net.minecraft.class_1100.class_10103 resolver) {
        for (net.minecraft.class_2960 id : legacy.method_4755()) {
            resolver.method_62642(id);
        }
        legacy.method_45785(resolver::method_62642);
    }

    /** 1.21.1 ItemOverride.getModel() (instanceRedirects): the record's model component */
    public static net.minecraft.class_2960 method_3472(net.minecraft.class_799 override) {
        return override.comp_3193();
    }

    /** 1.21.1 ItemOverride.getPredicates() (instanceRedirects) */
    public static java.util.stream.Stream<net.minecraft.class_799.class_5826> method_33690(net.minecraft.class_799 override) {
        return override.comp_3194().stream();
    }

    // ---- 1.21.1 MultiPartBakedModel's protected fields (instanceFieldGetters) ----
    // 1.21.1's constructor copied them from the first selector's model; 1.21.2's MultiPartBakedModel answers the same
    // properties from that model (DelegateBakedModel's delegate, method_64095's pick): each read is the delegate's
    // property, not a virtual call on the model (a subclass's override did not change 1.21.1's fields).

    public static boolean field_5430(net.minecraft.class_1095 model) {
        return model.field_54188.method_4708();
    }

    public static boolean field_5429(net.minecraft.class_1095 model) {
        return model.field_54188.method_4712();
    }

    public static boolean field_21863(net.minecraft.class_1095 model) {
        return model.field_54188.method_24304();
    }

    public static net.minecraft.class_1058 field_5425(net.minecraft.class_1095 model) {
        return model.field_54188.method_4711();
    }

    public static net.minecraft.class_809 field_5426(net.minecraft.class_1095 model) {
        return model.field_54188.method_4709();
    }

    public static net.minecraft.class_806 field_5428(net.minecraft.class_1095 model) {
        return model.field_54188.method_4710();
    }
}
