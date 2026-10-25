package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.21.3's model loading pipeline (ModelDiscovery, BlockStateModelLoader, ModelBakery and its TextureGetter,
 * ModelResourceLocation.inventory) on 1.21.4's.
 * <ul>
 * <li>Block state models: 1.21.3's were UnbakedModels (UnbakedBlockStateModel extended UnbakedModel); 1.21.4's are their own
 *     type, baked with a baker alone. A 1.21.3 model handed where 1.21.4 wants a block state model is baked as 1.21.3's model
 *     bakery baked a top model (its parent chain's values, rotation X0_Y0); a 1.21.4 block state model handed back where 1.21.3
 *     had an UnbakedModel bakes through its own bake. Each wrapper unwraps the other.</li>
 * <li>TextureGetter: 1.21.3 asked it per model location, 1.21.4 per debug name -- the model location's toString for a block
 *     state, "id#inventory" for an item, the very strings of 1.21.3's model locations; a debug name is read back into one.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbModelLoading {
    private CbModelLoading() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("CenturyBridge/ModelDiscovery");

    // ---- ModelResourceLocation ----

    /** was ModelResourceLocation.INVENTORY_VARIANT (field_52277) */
    public static String field_52277() {
        return "inventory";
    }

    /** was ModelResourceLocation.inventory(id) (method_61078) */
    public static net.minecraft.class_1091 method_61078(net.minecraft.class_2960 id) {
        return new net.minecraft.class_1091(id, "inventory");
    }

    // ---- block state models <-> unbaked models ----

    /** a 1.21.3 model where 1.21.4 wants a block state model */
    public static final class LegacyStateModel implements net.minecraft.class_9979 {
        final net.minecraft.class_1100 model;

        LegacyStateModel(net.minecraft.class_1100 model) {
            this.model = model;
        }

        @Override
        public net.minecraft.class_1087 method_65542(net.minecraft.class_7775 baker) {
            return net.minecraft.class_1100.method_65765(this.model, baker, net.minecraft.class_1086.field_5350);
        }

        @Override
        public Object method_62332(net.minecraft.class_2680 state) {
            return this.model instanceof net.minecraft.class_9979 s ? s.method_62332(state) : this;
        }

        @Override
        public void method_62326(net.minecraft.class_10526.class_10103 resolver) {
            this.model.method_62326(resolver);
        }
    }

    /** a 1.21.4 block state model where 1.21.3 had an UnbakedModel */
    public static final class StateUnbakedModel implements net.minecraft.class_1100 {
        final net.minecraft.class_9979 model;

        StateUnbakedModel(net.minecraft.class_9979 model) {
            this.model = model;
        }

        @Override
        public void method_62326(net.minecraft.class_10526.class_10103 resolver) {
            this.model.method_62326(resolver);
        }

        @Override
        public net.minecraft.class_1087 method_4753(net.minecraft.class_10419 slots, net.minecraft.class_7775 baker, net.minecraft.class_3665 state,
            boolean ambientOcclusion, boolean usesBlockLight, net.minecraft.class_809 transforms) {
            return this.model.method_65542(baker);
        }
    }

    public static net.minecraft.class_9979 toStateModel(net.minecraft.class_1100 model) {
        if (model == null) {
            return null;
        }
        if (model instanceof StateUnbakedModel s) {
            return s.model;
        }
        if (model instanceof net.minecraft.class_9979 s) {
            return s; // a 1.21.3 mod's block state model: it bakes as one (override hook)
        }
        return new LegacyStateModel(model);
    }

    public static net.minecraft.class_1100 toUnbaked(net.minecraft.class_9979 model) {
        if (model == null) {
            return null;
        }
        if (model instanceof LegacyStateModel l) {
            return l.model;
        }
        if (model instanceof net.minecraft.class_1100 u) {
            return u;
        }
        return new StateUnbakedModel(model);
    }

    /** 1.21.4's bake(baker) of a 1.21.3 mod's UnbakedBlockStateModel: its own bake, rotation X0_Y0 (1.21.3's top models) */
    public static net.minecraft.class_1087 bakeState(CbModelBaking.CbLegacyUnbakedModel model, net.minecraft.class_7775 baker) {
        net.minecraft.class_10527 sprites = baker.method_65732();
        return model.method_4753(baker, sprites::method_65741, net.minecraft.class_1086.field_5350);
    }

    /** new BlockStateModelLoader.LoadedModel(state, model): the model as a block state model */
    public static net.minecraft.class_9979 cbStateModel(net.minecraft.class_1100 model) {
        return toStateModel(model);
    }

    /** was LoadedModel.model() (comp_2871) */
    public static net.minecraft.class_1100 comp_2871(net.minecraft.class_9824.class_9825 loaded) {
        return toUnbaked(loaded.comp_2871());
    }

    // ---- ModelDiscovery ----

    /** was ModelDiscovery.ITEM_MODEL_PREFIX (field_54194) */
    public static String field_54194() {
        return "item/";
    }

    /** ModelDiscovery's 1.21.3 members (CbModelDiscoveryMixin) */
    public interface CbLegacyDiscovery {
        void cb$addTop(net.minecraft.class_1091 location, net.minecraft.class_1100 model);

        Map<net.minecraft.class_1091, net.minecraft.class_1100> method_62637();

        Map<net.minecraft.class_2960, net.minecraft.class_1100> cb$inputModels();

        Map<net.minecraft.class_2960, net.minecraft.class_1100> cb$specialModels();
    }

    /** was ModelDiscovery.registerStandardModels(loadedModels) (method_62632): 1.21.3's body */
    public static void registerStandardModels(CbLegacyDiscovery discovery, net.minecraft.class_9824.class_10095 loaded) {
        discovery.cb$specialModels().put(top.dext.centurybridge.step.v1_21_4.facade.SpecialModels.field_53677,
            top.dext.centurybridge.step.v1_21_4.facade.SpecialModels.field_53679);
        discovery.cb$specialModels().put(top.dext.centurybridge.step.v1_21_4.facade.SpecialModels.field_53678,
            top.dext.centurybridge.step.v1_21_4.facade.SpecialModels.field_53680);
        Set<net.minecraft.class_1091> mandatory = mandatoryModels();
        loaded.comp_3063().forEach((location, model) -> {
            discovery.cb$addTop(location, comp_2871(model));
            mandatory.remove(location);
        });
        discovery.cb$inputModels().keySet().forEach(id -> {
            if (id.method_12832().startsWith("item/")) {
                net.minecraft.class_1091 location = method_61078(id.method_45134(p -> p.substring("item/".length())));
                discovery.cb$addTop(location, new top.dext.centurybridge.step.v1_21_4.facade.ItemModel(id));
                mandatory.remove(location);
            }
        });
        if (!mandatory.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            mandatory.forEach(l -> sb.append("\n\t").append(l));
            LOGGER.warn("Missing mandatory models: {}", sb);
        }
    }

    /** 1.21.3 ModelDiscovery.listMandatoryModels: every item's item model (inventory), and the trident / spyglass in-hand models */
    private static Set<net.minecraft.class_1091> mandatoryModels() {
        Set<net.minecraft.class_1091> set = new java.util.HashSet<>();
        net.minecraft.class_7923.field_41178.method_42017().forEach(holder -> {
            net.minecraft.class_2960 model = holder.comp_349().method_57347().method_57829(net.minecraft.class_9334.field_54199);
            if (model != null) {
                set.add(method_61078(model));
            }
        });
        set.add(CbItemRender.field_40532());
        set.add(CbItemRender.field_40533());
        return set;
    }

    // ---- BlockStateModelLoader ----

    /** BlockStateModelLoader's 1.21.3 state (CbModelStateLoaderMixin) */
    public interface CbLegacyStateLoader {
        void cb$setMissingModel(net.minecraft.class_1100 missingModel);

        /** 1.21.3's loadBlockStateDefinitions(id, stateDefinition, definitions), restored (CbModelStateLoaderMixin) */
        net.minecraft.class_9824.class_10095 method_62627(net.minecraft.class_2960 id,
                net.minecraft.class_2689<net.minecraft.class_2248, net.minecraft.class_2680> definition, java.util.List<?> definitions);
    }

    /** after new BlockStateModelLoader(missingModel): the loader keeps the model 1.21.3 loaded definitions with */
    public static void cbStateLoaderConstructed(net.minecraft.class_9824 loader, net.minecraft.class_1100 missingModel) {
        ((CbLegacyStateLoader) loader).cb$setMissingModel(missingModel);
    }

    // ---- ModelBakery ----

    /** ModelBakery's 1.21.3 members (CbModelBakeryMixin) */
    public interface CbLegacyBakery {
        void cb$bakeModels(net.minecraft.class_1088.class_9826 textures);

        Map<net.minecraft.class_1091, net.minecraft.class_1087> method_4734();
    }

    /** new ModelBakery(topModels, unbakedModels, missingModel): 1.21.4 takes the entity models, block state and item models apart */
    public static net.minecraft.class_5599 cbEntityModels() {
        return net.minecraft.class_310.method_1551().method_31974();
    }

    public static Map<net.minecraft.class_1091, net.minecraft.class_9979> cbStateModels(Map<net.minecraft.class_1091, net.minecraft.class_1100> topModels) {
        Map<net.minecraft.class_1091, net.minecraft.class_9979> map = new LinkedHashMap<>();
        topModels.forEach((location, model) -> map.put(location, toStateModel(model)));
        return map;
    }

    public static Map<net.minecraft.class_2960, net.minecraft.class_10434> cbClientItems() {
        return Map.of();
    }

    /** was bakeModels(textureGetter) (method_45876, void), called by a mod */
    public static void bakeModels(net.minecraft.class_1088 bakery, net.minecraft.class_1088.class_9826 textures) {
        ((CbLegacyBakery) bakery).cb$bakeModels(textures);
    }

    /** the 1.21.3 body (CbModelBakeryMixin): 1.21.4's bake, the baked top models kept for getBakedTopLevelModels */
    public static Map<net.minecraft.class_1091, net.minecraft.class_1087> bakeTop(net.minecraft.class_1088 bakery, net.minecraft.class_1088.class_9826 textures) {
        return new LinkedHashMap<>(bakery.method_45876(textures).comp_3430());
    }

    // ---- TextureGetter ----

    /** a model location as the debug name 1.21.4 bakes it under (its toString), the location kept */
    public record LocationName(net.minecraft.class_1091 location) implements net.minecraft.class_10525 {
        @Override
        public String get() {
            return this.location.toString();
        }
    }

    /** the 1.21.3 model location of a 1.21.4 debug name ("ns:path#variant"; 1.21.4's missing model is "missing") */
    public static net.minecraft.class_1091 location(net.minecraft.class_10525 name) {
        if (name instanceof LocationName l) {
            return l.location();
        }
        String s = name.get();
        int hash = s.lastIndexOf('#');
        net.minecraft.class_2960 id = hash < 0 ? null : net.minecraft.class_2960.method_12829(s.substring(0, hash));
        if (id == null) {
            return net.minecraft.class_10096.field_53661;
        }
        return new net.minecraft.class_1091(id, s.substring(hash + 1));
    }

    /** a 1.21.3 TextureGetter (implementing get(location, material)) */
    public interface CbLegacyTextureGetter {
        net.minecraft.class_1058 get(net.minecraft.class_1091 location, net.minecraft.class_4730 material);
    }

    /** was TextureGetter.get(location, material), called by a mod */
    public static net.minecraft.class_1058 get(net.minecraft.class_1088.class_9826 getter, net.minecraft.class_1091 location, net.minecraft.class_4730 material) {
        return getter.method_65739(new LocationName(location), material);
    }

    /** 1.21.4's get(name, material) on a 1.21.3 getter (override hook) */
    public static net.minecraft.class_1058 bridgeGet(CbLegacyTextureGetter getter, net.minecraft.class_10525 name, net.minecraft.class_4730 material) {
        return getter.get(location(name), material);
    }

    /**
     * 1.21.4's reportMissingReference(name, reference) on a 1.21.3 getter (abstractFills): 1.21.3 answered a reference no
     * texture resolved with the missing material, which the model then asked its getter for
     */
    public static net.minecraft.class_1058 missingReference(net.minecraft.class_1088.class_9826 getter, net.minecraft.class_10525 name, String reference) {
        return getter.method_65739(name, CbModelBaking.MISSING);
    }

    /** a 1.21.3 TextureGetter lambda as 1.21.4's (lambdaAdapters) */
    public static net.minecraft.class_1088.class_9826 adaptTextureGetter(CbLegacyTextureGetter lambda) {
        return new net.minecraft.class_1088.class_9826() {
            @Override
            public net.minecraft.class_1058 method_65739(net.minecraft.class_10525 name, net.minecraft.class_4730 material) {
                return lambda.get(location(name), material);
            }

            @Override
            public net.minecraft.class_1058 method_65740(net.minecraft.class_10525 name, String reference) {
                return lambda.get(location(name), CbModelBaking.MISSING);
            }
        };
    }
}
