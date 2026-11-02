package top.dext.centurybridge.step.v1_21.rt;

import java.util.function.BiFunction;
import java.util.function.Function;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1058;
import net.minecraft.class_1087;
import net.minecraft.class_1088;
import net.minecraft.class_1091;
import net.minecraft.class_1100;
import net.minecraft.class_2350;
import net.minecraft.class_2960;
import net.minecraft.class_3665;
import net.minecraft.class_4730;
import net.minecraft.class_7654;
import net.minecraft.class_7775;
import net.minecraft.class_783;
import net.minecraft.class_787;
import net.minecraft.class_793;

/**
 * 1.20.6's model baking API on 1.21. 1.21 dropped the model id 1.20.6 handed UnbakedModel.bake, BlockModel.bake and
 * FaceBakery (vanilla 1.20.6 used it only in the UV-lock warning, which 1.21 no longer prints; a 1.20.6 mod's mixin on
 * bakeQuad reads it: {@link #baking}, CbRenderFaceBakeryRouteMixin), made ModelResourceLocation a
 * record that is no ResourceLocation, keys baking by ModelResourceLocation, and made BlockElementFace a record.
 * Wired from s6fin-client.json.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderModelBake {

    private CbRenderModelBake() {
    }

    // ------------------------------------------------------------------------------------------------ the model id

    /** 1.21's model baker, telling which model it is baking (CbRenderModelBakerMixin) */
    public interface ModelIdSource {
        class_2960 cb$currentModelId();
    }

    /**
     * The id 1.20.6 handed a model's bake: the location the baker was asked to bake (ModelBaker.bake(id, state)) for a
     * nested model, the baker's own model location for a top-level one. 1.20.6's top-level id was that
     * ModelResourceLocation itself, a ResourceLocation then; 1.21's is a record no ResourceLocation can stand for, so
     * a top-level model gets its location's id (namespace and path; the variant has no place in a ResourceLocation).
     * Null for a baker that is not 1.21's (a mod's own), which knows no model.
     */
    public static class_2960 idOf(class_7775 baker) {
        return baker instanceof ModelIdSource source ? source.cb$currentModelId() : null;
    }

    /** the restored 1.20.6 FaceBakery.bakeQuad(.., id), as a CbMixedMembers key */
    public static final String BAKE_QUAD = "net/minecraft/class_796.method_3468(Lorg/joml/Vector3f;Lorg/joml/Vector3f;Lnet/minecraft/class_783;"
        + "Lnet/minecraft/class_1058;Lnet/minecraft/class_2350;Lnet/minecraft/class_3665;Lnet/minecraft/class_789;ZLnet/minecraft/class_2960;)"
        + "Lnet/minecraft/class_777;";

    /** 1.21's FaceBakery with the restored 1.20.6 bakeQuad(.., id) (CbRenderFaceBakeryMixin) */
    public interface LegacyFaceBakery {
        net.minecraft.class_777 method_3468(org.joml.Vector3f from, org.joml.Vector3f to, class_783 face, class_1058 sprite, class_2350 direction,
                                            class_3665 state, net.minecraft.class_789 rotation, boolean shade, class_2960 id);
    }

    /**
     * The id of the model whose bake runs on this thread, set by {@link #baking} around 1.21's model bakes while a converted
     * mod mixes the restored bakeQuad: 1.20.6 handed it down UnbakedModel.bake -> BlockModel.bake -> bakeFace -> bakeQuad,
     * 1.21 drops it on the way (CbRenderFaceBakeryRouteMixin reads it at bakeFace's bakeQuad call).
     */
    private static final ThreadLocal<class_2960> BAKING_ID = new ThreadLocal<>();

    /** the id of the model being baked on this thread (null outside a bake {@link #baking} marks) */
    public static class_2960 bakingId() {
        return BAKING_ID.get();
    }

    /**
     * Runs a model bake with {@code id} as the id of the model being baked, when a converted mod mixes the restored
     * bakeQuad (CbMixedMembers); the previous id comes back afterwards (a nested bake ends inside the outer one).
     */
    public static <T> T baking(class_2960 id, java.util.function.Supplier<T> bake) {
        if (!CbMixedMembers.mixed(BAKE_QUAD)) {
            return bake.get();
        }
        class_2960 was = BAKING_ID.get();
        BAKING_ID.set(id);
        try {
            return bake.get();
        } finally {
            BAKING_ID.set(was);
        }
    }

    // ------------------------------------------------------------------------------------------------ UnbakedModel.bake

    /** a 1.20.6 model class's own bake(baker, sprites, state, id) */
    public interface LegacyUnbaked {
        class_1087 method_4753(class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state, class_2960 id);
    }

    /** a 1.20.6 BlockModel subclass's own bake(baker, parent, sprites, state, id, gui3d) */
    public interface LegacyBlockModel {
        class_1087 method_3446(class_7775 baker, class_793 parent, Function<class_4730, class_1058> sprites, class_3665 state, class_2960 id,
                               boolean gui3d);
    }

    /** the super accessor a 1.20.6 model subclass gets for its super.bake(baker, sprites, state, id) */
    public interface SuperModel {
        class_1087 cb$super$method_4753(class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state);
    }

    /** the super accessor a 1.20.6 BlockModel subclass gets for its super.bake(baker, parent, sprites, state, id, gui3d) */
    public interface SuperBlockModel {
        class_1087 cb$super$method_3446(class_7775 baker, class_793 parent, Function<class_4730, class_1058> sprites, class_3665 state,
                                        boolean gui3d);
    }

    /** model.bake(baker, sprites, state, id) of 1.20.6: a 1.20.6 class's own method with the caller's id, else 1.21's */
    public static class_1087 method_4753(class_1100 model, class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state, class_2960 id) {
        if (model instanceof LegacyUnbaked legacy) {
            return legacy.method_4753(baker, sprites, state, id);
        }
        return baking(id, () -> model.method_4753(baker, sprites, state));
    }

    /** the same, the call made on a BlockModel */
    public static class_1087 method_4753(class_793 model, class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state, class_2960 id) {
        return method_4753((class_1100) model, baker, sprites, state, id);
    }

    /** the same, the call made on a MultiPart */
    public static class_1087 method_4753(net.minecraft.class_819 model, class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state,
                                         class_2960 id) {
        return method_4753((class_1100) model, baker, sprites, state, id);
    }

    /** the same, the call made on Fabric API's DelegatingUnbakedModel (final: never a 1.20.6 class's own) */
    public static class_1087 method_4753(net.fabricmc.fabric.api.client.model.loading.v1.DelegatingUnbakedModel model, class_7775 baker,
                                         Function<class_4730, class_1058> sprites, class_3665 state, class_2960 id) {
        return model.method_4753(baker, sprites, state);
    }

    /** 1.21's bake(baker, sprites, state) of a 1.20.6 model class: its own bake with the id of the model being baked */
    public static class_1087 bake(LegacyUnbaked self, class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state) {
        return self.method_4753(baker, sprites, state, idOf(baker));
    }

    /** super.bake(baker, sprites, state, id) of a 1.20.6 subclass: super's 1.21 bake, the id handed on to bakeQuad ({@link #baking}) */
    public static class_1087 superBake(SuperModel self, class_7775 baker, Function<class_4730, class_1058> sprites, class_3665 state, class_2960 id) {
        return baking(id, () -> self.cb$super$method_4753(baker, sprites, state));
    }

    /** blockModel.bake(baker, parent, sprites, state, id, gui3d) of 1.20.6: a 1.20.6 subclass's own, else 1.21's */
    public static class_1087 method_3446(class_793 model, class_7775 baker, class_793 parent, Function<class_4730, class_1058> sprites, class_3665 state,
                                         class_2960 id, boolean gui3d) {
        if (model instanceof LegacyBlockModel legacy) {
            return legacy.method_3446(baker, parent, sprites, state, id, gui3d);
        }
        return baking(id, () -> model.method_3446(baker, parent, sprites, state, gui3d));
    }

    /** 1.21's BlockModel.bake(baker, parent, sprites, state, gui3d) of a 1.20.6 subclass: its own with the model's id */
    public static class_1087 blockBake(LegacyBlockModel self, class_7775 baker, class_793 parent, Function<class_4730, class_1058> sprites,
                                       class_3665 state, boolean gui3d) {
        return self.method_3446(baker, parent, sprites, state, idOf(baker), gui3d);
    }

    /** super.bake(baker, parent, sprites, state, id, gui3d) of a 1.20.6 subclass: super's 1.21 bake, the id handed on to bakeQuad */
    public static class_1087 superBlockBake(SuperBlockModel self, class_7775 baker, class_793 parent, Function<class_4730, class_1058> sprites,
                                            class_3665 state, class_2960 id, boolean gui3d) {
        return baking(id, () -> self.cb$super$method_3446(baker, parent, sprites, state, gui3d));
    }

    // ------------------------------------------------------------------------------------------------ FaceBakery

    /** 1.20.6 FaceBakery.recomputeUVs(uv, direction, transform, id): 1.21's (the id only named the UV-lock warning) */
    public static class_787 method_3454(class_787 uv, class_2350 direction, net.minecraft.class_4590 transform, class_2960 id) {
        return net.minecraft.class_796.method_3454(uv, direction, transform);
    }

    // ------------------------------------------------------------------------------------------------ BlockModel

    /** a FaceBakery of the layer's own: 1.21's has no instance state (javap 1.21 class_796: static fields only) */
    private static final net.minecraft.class_796 FACE_BAKERY = new net.minecraft.class_796();

    /**
     * 1.20.6 BlockModel.bakeFace(element, face, sprite, direction, state, id) (class_793.method_3447, private static,
     * reached through a mod's own access widener: 7 corpus mods, damage-aw aw-call-static). 1.20.6's body (javap):
     * FACE_BAKERY.bakeQuad(element.from, element.to, face, sprite, direction, state, element.rotation, element.shade, id).
     * 1.21 dropped the id from both; this is 1.20.6's body on the restored bakeQuad(.., id) (CbRenderFaceBakeryMixin),
     * so the id reaches a converted mod's bakeQuad mixin as it did.
     */
    public static net.minecraft.class_777 method_3447(net.minecraft.class_785 element, class_783 face, class_1058 sprite,
                                                     class_2350 direction, class_3665 state, class_2960 id) {
        return ((LegacyFaceBakery) (Object) FACE_BAKERY).method_3468(element.field_4228, element.field_4231, face, sprite, direction,
            state, element.field_4232, element.field_4229, id);
    }

    // ------------------------------------------------------------------------------------------------ ModelBakery

    /** 1.20.6 ModelBakery.MISSING_MODEL_LOCATION (builtin/missing#missing): 1.21's MISSING_MODEL_VARIANT, the same location */
    public static class_1091 field_5374() {
        return class_1088.field_52276;
    }

    /** 1.20.6 ModelBakery.BLOCKSTATE_LISTER (json files under blockstates/): 1.21 keeps it in BlockStateModelLoader */
    public static class_7654 field_40569() {
        return net.minecraft.class_9824.field_52260;
    }

    /**
     * The 1.21 texture getter for a 1.20.6 sprite function (model location, material) -> sprite: the model's location
     * as 1.20.6 named it -- its id; 1.20.6's location was the ModelResourceLocation itself, a ResourceLocation then.
     */
    public static class_1088.class_9826 textureGetter(BiFunction<class_2960, class_4730, class_1058> sprites) {
        return (location, material) -> sprites.apply(location.comp_2875(), material);
    }

    /**
     * The 1.20.6 sprite function a 1.20.6 bakeModels override is handed for 1.21's texture getter: the location it
     * passes, as the model location 1.21 asks with (no variant: 1.21's getter reads the location only to name a
     * missing texture in its warning).
     */
    public static BiFunction<class_2960, class_4730, class_1058> spriteFunction(class_1088.class_9826 getter) {
        return (location, material) -> getter.get(new class_1091(location, ""), material);
    }

    /** the super accessor a 1.20.6 ModelBakery subclass gets for its super.bakeModels */
    public interface SuperBakery {
        void cb$super$method_45876(class_1088.class_9826 getter);
    }

    /** super.bakeModels(sprites) of a 1.20.6 subclass: super's 1.21 bakeModels with the matching texture getter */
    public static void superBakeModels(SuperBakery self, BiFunction<class_2960, class_4730, class_1058> sprites) {
        self.cb$super$method_45876(textureGetter(sprites));
    }

    // ------------------------------------------------------------------------------------------------ BlockElementFace

    /** 1.20.6 face.cullForDirection: 1.21's record component */
    public static class_2350 field_4225(class_783 face) {
        return face.comp_2867();
    }

    /** 1.20.6 face.tintIndex */
    public static int field_4226(class_783 face) {
        return face.comp_2868();
    }

    /** 1.20.6 face.texture */
    public static String field_4224(class_783 face) {
        return face.comp_2869();
    }

    /** 1.20.6 face.uv */
    public static class_787 field_4227(class_783 face) {
        return face.comp_2870();
    }

    // ------------------------------------------------------------------------------------------------ language, sounds

    /**
     * The reload callback a 1.20.6 LanguageManager gets: 1.21's manager hands each reloaded language to one (the
     * game's refreshes the player's search trees); 1.20.6's manager had none, so nothing more happens.
     */
    public static java.util.function.Consumer<net.minecraft.class_1078> noLanguageCallback() {
        return language -> {
        };
    }

    // ------------------------------------------------------------------------------------------------ CommonListenerCookie

    /**
     * A 1.20.6 CommonListenerCookie carries neither custom report details nor server links (1.21 added both): none
     * of either, what 1.21's own login handshake starts the configuration phase with (Map.of(), ServerLinks.EMPTY).
     */
    public static java.util.Map<String, String> noReportDetails() {
        return java.util.Map.of();
    }

    public static net.minecraft.class_9782 noServerLinks() {
        return net.minecraft.class_9782.field_51977;
    }
}
