package top.dext.centurybridge.step.v1_21_4.facade;

import java.util.List;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.rt.CbItemRender;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelBaking;

/**
 * 1.21.3 ItemModel (class_10202, gone in 1.21.4): the unbaked model of an item, naming its model by id. It resolved that
 * model and the models of its item overrides, and baked to the model, wrapped with its baked overrides when it had any.
 * 1.21.4 resolves items through their item definitions; this is 1.21.3's class, body for body, as an UnbakedModel of
 * 1.21.4 (its 1.21.3 bake reached from 1.21.4's, see {@link CbModelBaking}).
 */
@Environment(EnvType.CLIENT)
public class ItemModel implements net.minecraft.class_1100, CbModelBaking.CbLegacyUnbakedModel {
    /** id */
    public final net.minecraft.class_2960 field_54191;
    /** overrides */
    public List<ItemOverride> field_54192 = List.of();

    public ItemModel(net.minecraft.class_2960 id) {
        this.field_54191 = id;
    }

    /** resolveDependencies: the model, and when it is a BlockModel its overrides and their models */
    @Override
    public void method_62326(net.minecraft.class_10526.class_10103 resolver) {
        net.minecraft.class_1100 model = resolver.method_62642(this.field_54191);
        if (model instanceof net.minecraft.class_793 bm) {
            this.field_54192 = ((top.dext.centurybridge.step.v1_21_4.rt.CbModelBlockModel.CbLegacyBlockModel) bm).method_3434();
            this.field_54192.forEach(o -> method_64089(resolver, o));
        }
    }

    private static void method_64089(net.minecraft.class_10526.class_10103 resolver, ItemOverride override) {
        resolver.method_62642(override.comp_3193());
    }

    /** bake(baker, spriteGetter, state): the model, wrapped with its baked overrides when it has any */
    @Override
    public net.minecraft.class_1087 method_4753(net.minecraft.class_7775 baker, Function<net.minecraft.class_4730, net.minecraft.class_1058> sprites,
            net.minecraft.class_3665 state) {
        net.minecraft.class_1087 baked = baker.method_45873(this.field_54191, state);
        if (this.field_54192.isEmpty()) {
            return baked;
        }
        return new BakedModelWithOverrides(baked, new BakedOverrides(baker, this.field_54192));
    }

    /** 1.21.4's bake: 1.21.3's, the baker's sprites as the sprite getter */
    @Override
    public net.minecraft.class_1087 method_4753(net.minecraft.class_10419 slots, net.minecraft.class_7775 baker, net.minecraft.class_3665 state,
            boolean ambientOcclusion, boolean usesBlockLight, net.minecraft.class_809 transforms) {
        return CbModelBaking.bake(this, slots, baker, state, ambientOcclusion, usesBlockLight, transforms);
    }

    /** ItemModel.BakedModelWithOverrides: the model, with the overrides it answers */
    public static class BakedModelWithOverrides implements net.minecraft.class_1087, CbItemRender.CbLegacyOverrides {
        private final net.minecraft.class_1087 parent;
        private final BakedOverrides overrides;

        public BakedModelWithOverrides(net.minecraft.class_1087 parent, BakedOverrides overrides) {
            this.parent = parent;
            this.overrides = overrides;
        }

        @Override
        public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
            return this.parent.method_4707(state, side, random);
        }

        @Override
        public boolean method_4708() {
            return this.parent.method_4708();
        }

        @Override
        public boolean method_4712() {
            return this.parent.method_4712();
        }

        @Override
        public boolean method_24304() {
            return this.parent.method_24304();
        }

        @Override
        public net.minecraft.class_1058 method_4711() {
            return this.parent.method_4711();
        }

        @Override
        public net.minecraft.class_809 method_4709() {
            return this.parent.method_4709();
        }

        @Override
        public BakedOverrides cb$getOverrides() {
            return this.overrides;
        }
    }
}
