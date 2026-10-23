package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3's baking API on 1.21.4's.
 * <ul>
 * <li>UnbakedModel.bake(baker, spriteGetter, state): 1.21.4 bakes through bake(textureSlots, baker, state, ambientOcclusion,
 *     usesBlockLight, transforms) with the values of the model's parent chain, and the baker carries the sprites. A 1.21.3
 *     model's own bake is reached from 1.21.4's (an override hook, {@link #bake(CbLegacyUnbakedModel, net.minecraft.class_10419,
 *     net.minecraft.class_7775, net.minecraft.class_3665, boolean, boolean, net.minecraft.class_809)}), with the baker's sprites
 *     as the sprite getter -- what 1.21.3's model bakery handed every model it baked (its texture getter for the model being
 *     baked). A mod's call of the 1.21.3 shape bakes a 1.21.4 model with the sprites the mod gave
 *     ({@link #method_4753(net.minecraft.class_1100, net.minecraft.class_7775, Function, net.minecraft.class_3665)}).</li>
 * <li>ModelBaker: 1.21.4 added sprites() and rootName(); a 1.21.3 mod's baker answers them as 1.21.3 resolved a model's
 *     materials (the atlas sprite, the missing texture for a reference no texture answers).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbModelBaking {
    private CbModelBaking() {
    }

    /** 1.21.3 BlockModel.MISSING_MATERIAL: the missing texture on the block atlas, what 1.21.3 answered for a missing reference */
    public static net.minecraft.class_4730 missingMaterial() {
        return new net.minecraft.class_4730(net.minecraft.class_1059.field_5275, net.minecraft.class_1047.method_4539());
    }

    /** the same material, made once */
    public static final net.minecraft.class_4730 MISSING = missingMaterial();

    /** an UnbakedModel with 1.21.3's bake(baker, spriteGetter, state): a mod's model, and every BlockModel (CbModelBlockModelMixin) */
    public interface CbLegacyUnbakedModel {
        net.minecraft.class_1087 method_4753(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state);
    }

    /** 1.21.4's bake on a 1.21.3 model: its own bake, the baker's sprites as its sprite getter */
    public static net.minecraft.class_1087 bake(CbLegacyUnbakedModel model, net.minecraft.class_10419 slots, net.minecraft.class_7775 baker,
        net.minecraft.class_3665 state, boolean ambientOcclusion, boolean usesBlockLight, net.minecraft.class_809 transforms) {
        net.minecraft.class_10527 sprites = baker.method_65732();
        return model.method_4753(baker, sprites::method_65741, state);
    }

    /** was UnbakedModel.bake(baker, spriteGetter, state) (method_4753), called by a mod */
    public static net.minecraft.class_1087 method_4753(net.minecraft.class_1100 model, net.minecraft.class_7775 baker,
        Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites, net.minecraft.class_3665 state) {
        if (model instanceof CbLegacyUnbakedModel legacy) {
            return legacy.method_4753(baker, sprites, state);
        }
        // a 1.21.4 model: baked as 1.21.4 bakes a top model (its parent chain's values), its sprites from the mod's getter
        return net.minecraft.class_1100.method_65765(model, new SpritesBaker(baker, sprites), state);
    }

    /** a baker whose sprites are a 1.21.3 sprite getter; everything else is the given baker's */
    public static final class SpritesBaker implements net.minecraft.class_7775 {
        private final net.minecraft.class_7775 baker;
        private final Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites;

        public SpritesBaker(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites) {
            this.baker = baker;
            this.sprites = sprites;
        }

        @Override
        public net.minecraft.class_1087 method_45873(net.minecraft.class_2960 id, net.minecraft.class_3665 state) {
            return this.baker.method_45873(id, state);
        }

        @Override
        public net.minecraft.class_10527 method_65732() {
            net.minecraft.class_10527 own = this.baker.method_65732();
            Function<net.minecraft.class_4730, net.minecraft.class_1058> fn = this.sprites;
            return new net.minecraft.class_10527() {
                @Override
                public net.minecraft.class_1058 method_65741(net.minecraft.class_4730 material) {
                    return fn.apply(material);
                }

                @Override
                public net.minecraft.class_1058 method_65742(String name) {
                    return own.method_65742(name);
                }
            };
        }

        @Override
        public net.minecraft.class_10525 method_65733() {
            return this.baker.method_65733();
        }
    }

    /** 1.21.3's sprite resolution for a baker that has no sprites of its own: the atlas sprite, the missing texture for a missing reference */
    public static final net.minecraft.class_10527 LEGACY_SPRITES = new net.minecraft.class_10527() {
        @Override
        public net.minecraft.class_1058 method_65741(net.minecraft.class_4730 material) {
            return material.method_24148();
        }

        @Override
        public net.minecraft.class_1058 method_65742(String name) {
            return MISSING.method_24148();
        }
    };

    /** ModelBaker.sprites() of a 1.21.3 mod's baker (abstractFills) */
    public static net.minecraft.class_10527 sprites(net.minecraft.class_7775 baker) {
        return LEGACY_SPRITES;
    }

    /** ModelBaker.rootName() of a 1.21.3 mod's baker (abstractFills): only log messages read it */
    public static net.minecraft.class_10525 rootName(net.minecraft.class_7775 baker) {
        String name = baker.getClass().getName();
        return () -> name;
    }

    /** a 1.21.3 ModelBaker lambda: ModelBaker was a functional interface (bake(id, state)) */
    @FunctionalInterface
    public interface CbLegacyModelBaker {
        net.minecraft.class_1087 method_45873(net.minecraft.class_2960 id, net.minecraft.class_3665 state);
    }

    /** a 1.21.3 ModelBaker lambda as a 1.21.4 baker (lambdaAdapters) */
    public static net.minecraft.class_7775 adaptModelBaker(CbLegacyModelBaker lambda) {
        return new net.minecraft.class_7775() {
            @Override
            public net.minecraft.class_1087 method_45873(net.minecraft.class_2960 id, net.minecraft.class_3665 state) {
                return lambda.method_45873(id, state);
            }

            @Override
            public net.minecraft.class_10527 method_65732() {
                return LEGACY_SPRITES;
            }

            @Override
            public net.minecraft.class_10525 method_65733() {
                String name = lambda.getClass().getName();
                return () -> name;
            }
        };
    }
}
