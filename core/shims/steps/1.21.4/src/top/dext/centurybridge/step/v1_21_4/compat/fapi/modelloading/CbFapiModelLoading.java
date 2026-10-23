package top.dext.centurybridge.step.v1_21_4.compat.fapi.modelloading;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelModifier;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import top.dext.centurybridge.step.v1_21_4.rt.CbModelLoading;

/**
 * Fabric API 1.21.3's model loading API (fabric-model-loading-api-v1) on 1.21.4's.
 * <ul>
 * <li>One set of modifier events served every model: models loaded by id (resourceId()) and the block state models
 *     (topLevelId(), the state's model location). 1.21.4 serves block state models through events of their own
 *     (modifyBlockModelOnLoad / BeforeBake / AfterBake). A 1.21.3 modifier registered through a plugin context is registered
 *     on both, the block one adapted: it gets the block state model as an UnbakedModel (CbModelLoading's wrappers, which
 *     unwrap again) and a context whose topLevelId() is the state's model location, as 1.21.3's dispatcher gave it.</li>
 * <li>resolveModel(): 1.21.3 asked the resolvers for a model id before loading its file; 1.21.4 has no resolvers. Each plugin
 *     context gets 1.21.3's event, asked first for every model 1.21.4 loads by id (an on-load modifier in a phase before every
 *     other): a resolver's model replaces the file's, a null leaves it.</li>
 * <li>Contexts: resourceId() is the id for a model loaded by id and null for a block state model; topLevelId() the reverse;
 *     textureGetter() is the baker's sprites (what 1.21.3's bakery handed the context).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbFapiModelLoading {
    private CbFapiModelLoading() {
    }

    /** the phase 1.21.3's resolvers run in: before every 1.21.4 on-load modifier */
    public static final net.minecraft.class_2960 LEGACY_RESOLVE_PHASE = net.minecraft.class_2960.method_60655("centurybridge", "legacy_resolve");

    /** 1.21.3's block state models are loaded, baked and modified with rotation X0_Y0 */
    private static net.minecraft.class_3665 topLevelSettings() {
        return net.minecraft.class_1086.field_5350;
    }

    // ---- resolveModel ----

    private static final Map<ModelLoadingPlugin.Context, Event<ModelResolver>> RESOLVERS = new WeakHashMap<>();

    /** was ModelLoadingPlugin.Context.resolveModel() */
    public static synchronized Event<ModelResolver> resolveModel(ModelLoadingPlugin.Context context) {
        Event<ModelResolver> event = RESOLVERS.get(context);
        if (event == null) {
            Event<ModelResolver> resolvers = EventFactory.createArrayBacked(ModelResolver.class, listeners -> ctx -> {
                for (ModelResolver resolver : listeners) {
                    net.minecraft.class_1100 model = resolver.resolveModel(ctx);
                    if (model != null) {
                        return model;
                    }
                }
                return null;
            });
            Event<ModelModifier.OnLoad> onLoad = context.modifyModelOnLoad();
            onLoad.addPhaseOrdering(LEGACY_RESOLVE_PHASE, ModelModifier.OVERRIDE_PHASE);
            onLoad.register(LEGACY_RESOLVE_PHASE, (model, ctx) -> {
                net.minecraft.class_2960 id = ctx.id();
                net.minecraft.class_1100 resolved = resolvers.invoker().resolveModel(() -> id);
                return resolved != null ? resolved : model;
            });
            RESOLVERS.put(context, resolvers);
            event = resolvers;
        }
        return event;
    }

    // ---- modifier events serving both kinds of model ----

    /** an event that registers each listener on two events, the second listener made from the first */
    private static final class BothEvent<T, B> extends Event<T> {
        private final Event<T> models;
        private final Event<B> blocks;
        private final Function<T, B> adapt;

        BothEvent(Event<T> models, Event<B> blocks, Function<T, B> adapt) {
            this.models = models;
            this.blocks = blocks;
            this.adapt = adapt;
            this.invoker = models.invoker();
        }

        @Override
        public void register(T listener) {
            this.register(Event.DEFAULT_PHASE, listener);
        }

        @Override
        public void register(net.minecraft.class_2960 phase, T listener) {
            this.models.register(phase, listener);
            this.blocks.register(phase, this.adapt.apply(listener));
            this.invoker = this.models.invoker();
        }

        @Override
        public void addPhaseOrdering(net.minecraft.class_2960 first, net.minecraft.class_2960 second) {
            this.models.addPhaseOrdering(first, second);
            this.blocks.addPhaseOrdering(first, second);
        }
    }

    /** a 1.21.3 context for a block state model: no resource id, the state's model location */
    public interface LegacyBlockContext {
        net.minecraft.class_1091 cb$topLevelId();
    }

    private record OnLoadBlockContext(net.minecraft.class_1091 location) implements ModelModifier.OnLoad.Context, LegacyBlockContext {
        @Override
        public net.minecraft.class_2960 id() {
            return this.location.comp_2875();
        }

        @Override
        public net.minecraft.class_1091 cb$topLevelId() {
            return this.location;
        }
    }

    private record BeforeBakeBlockContext(net.minecraft.class_1091 location, net.minecraft.class_7775 baker)
        implements ModelModifier.BeforeBake.Context, LegacyBlockContext {
        @Override
        public net.minecraft.class_2960 id() {
            return this.location.comp_2875();
        }

        @Override
        public net.minecraft.class_3665 settings() {
            return topLevelSettings();
        }

        @Override
        public net.minecraft.class_1091 cb$topLevelId() {
            return this.location;
        }
    }

    private record AfterBakeBlockContext(net.minecraft.class_1091 location, net.minecraft.class_1100 sourceModel, net.minecraft.class_7775 baker)
        implements ModelModifier.AfterBake.Context, LegacyBlockContext {
        @Override
        public net.minecraft.class_2960 id() {
            return this.location.comp_2875();
        }

        @Override
        public net.minecraft.class_3665 settings() {
            return topLevelSettings();
        }

        @Override
        public net.minecraft.class_1091 cb$topLevelId() {
            return this.location;
        }
    }

    private static final Map<ModelLoadingPlugin.Context, Event<ModelModifier.OnLoad>> ON_LOAD = new WeakHashMap<>();
    private static final Map<ModelLoadingPlugin.Context, Event<ModelModifier.BeforeBake>> BEFORE_BAKE = new WeakHashMap<>();
    private static final Map<ModelLoadingPlugin.Context, Event<ModelModifier.AfterBake>> AFTER_BAKE = new WeakHashMap<>();

    /** was ModelLoadingPlugin.Context.modifyModelOnLoad(), called by a 1.21.3 plugin */
    public static synchronized Event<ModelModifier.OnLoad> modifyModelOnLoad(ModelLoadingPlugin.Context context) {
        return ON_LOAD.computeIfAbsent(context, c -> new BothEvent<ModelModifier.OnLoad, ModelModifier.OnLoadBlock>(c.modifyModelOnLoad(),
            c.modifyBlockModelOnLoad(), modifier -> (model, ctx) -> {
                net.minecraft.class_1100 unbaked = CbModelLoading.toUnbaked(model);
                net.minecraft.class_1100 result = modifier.modifyModelOnLoad(unbaked, new OnLoadBlockContext(ctx.id()));
                return result == unbaked ? model : CbModelLoading.toStateModel(result);
            }));
    }

    /** was ModelLoadingPlugin.Context.modifyModelBeforeBake(), called by a 1.21.3 plugin */
    public static synchronized Event<ModelModifier.BeforeBake> modifyModelBeforeBake(ModelLoadingPlugin.Context context) {
        return BEFORE_BAKE.computeIfAbsent(context, c -> new BothEvent<ModelModifier.BeforeBake, ModelModifier.BeforeBakeBlock>(c.modifyModelBeforeBake(),
            c.modifyBlockModelBeforeBake(), modifier -> (model, ctx) -> {
                net.minecraft.class_1100 unbaked = CbModelLoading.toUnbaked(model);
                net.minecraft.class_1100 result = modifier.modifyModelBeforeBake(unbaked, new BeforeBakeBlockContext(ctx.id(), ctx.baker()));
                return result == unbaked ? model : CbModelLoading.toStateModel(result);
            }));
    }

    /** was ModelLoadingPlugin.Context.modifyModelAfterBake(), called by a 1.21.3 plugin */
    public static synchronized Event<ModelModifier.AfterBake> modifyModelAfterBake(ModelLoadingPlugin.Context context) {
        return AFTER_BAKE.computeIfAbsent(context, c -> new BothEvent<ModelModifier.AfterBake, ModelModifier.AfterBakeBlock>(c.modifyModelAfterBake(),
            c.modifyBlockModelAfterBake(), modifier -> (model, ctx) -> modifier.modifyModelAfterBake(model,
                new AfterBakeBlockContext(ctx.id(), CbModelLoading.toUnbaked(ctx.sourceModel()), ctx.baker()))));
    }

    // ---- contexts ----

    private static net.minecraft.class_2960 resourceIdOf(Object context, net.minecraft.class_2960 id) {
        return context instanceof LegacyBlockContext ? null : id;
    }

    private static net.minecraft.class_1091 topLevelIdOf(Object context) {
        return context instanceof LegacyBlockContext block ? block.cb$topLevelId() : null;
    }

    /** OnLoad.Context.resourceId() / topLevelId() */
    public static net.minecraft.class_2960 resourceId(ModelModifier.OnLoad.Context context) {
        return resourceIdOf(context, context.id());
    }

    public static net.minecraft.class_1091 topLevelId(ModelModifier.OnLoad.Context context) {
        return topLevelIdOf(context);
    }

    public static net.minecraft.class_2960 resourceId(ModelModifier.BeforeBake.Context context) {
        return resourceIdOf(context, context.id());
    }

    public static net.minecraft.class_1091 topLevelId(ModelModifier.BeforeBake.Context context) {
        return topLevelIdOf(context);
    }

    /** BeforeBake.Context.textureGetter(): the baker's sprites */
    public static Function<net.minecraft.class_4730, net.minecraft.class_1058> textureGetter(ModelModifier.BeforeBake.Context context) {
        return context.baker().method_65732()::method_65741;
    }

    public static net.minecraft.class_2960 resourceId(ModelModifier.AfterBake.Context context) {
        return resourceIdOf(context, context.id());
    }

    public static net.minecraft.class_1091 topLevelId(ModelModifier.AfterBake.Context context) {
        return topLevelIdOf(context);
    }

    public static Function<net.minecraft.class_4730, net.minecraft.class_1058> textureGetter(ModelModifier.AfterBake.Context context) {
        return context.baker().method_65732()::method_65741;
    }

    /** BlockStateResolver.Context.setModel(state, model): 1.21.4 takes a block state model */
    public static net.minecraft.class_9979 cbStateModel(net.minecraft.class_1100 model) {
        return CbModelLoading.toStateModel(model);
    }

    // ---- 1.21.3 implementations of the contexts (tests, wrappers): adapterHooks / abstractFills ----

    /** a 1.21.3 OnLoad.Context implementation (resourceId / topLevelId) */
    public interface CbLegacyIdContext {
        net.minecraft.class_2960 resourceId();

        net.minecraft.class_1091 topLevelId();
    }

    /** its id(): the resource id, else the top level location's */
    public static net.minecraft.class_2960 id(CbLegacyIdContext context) {
        net.minecraft.class_2960 id = context.resourceId();
        return id != null ? id : context.topLevelId().comp_2875();
    }

    /** a 1.21.3 BlockStateResolver.Context implementation (setModel with an UnbakedModel) */
    public interface CbLegacyBlockStateContext {
        void setModel(net.minecraft.class_2680 state, net.minecraft.class_1100 model);
    }

    public static void setModel(CbLegacyBlockStateContext context, net.minecraft.class_2680 state, net.minecraft.class_9979 model) {
        context.setModel(state, CbModelLoading.toUnbaked(model));
    }

    /**
     * a 1.21.3 ModelLoadingPlugin.Context implementation's block model events: 1.21.3's API had none, so such a context hands
     * out events of its own (chained as Fabric API's are) that only whoever drives that context fires
     */
    public static Event<ModelModifier.OnLoadBlock> modifyBlockModelOnLoad(ModelLoadingPlugin.Context context) {
        return EventFactory.createArrayBacked(ModelModifier.OnLoadBlock.class, listeners -> (model, ctx) -> {
            for (ModelModifier.OnLoadBlock l : listeners) {
                model = l.modifyModelOnLoad(model, ctx);
            }
            return model;
        });
    }

    public static Event<ModelModifier.BeforeBakeBlock> modifyBlockModelBeforeBake(ModelLoadingPlugin.Context context) {
        return EventFactory.createArrayBacked(ModelModifier.BeforeBakeBlock.class, listeners -> (model, ctx) -> {
            for (ModelModifier.BeforeBakeBlock l : listeners) {
                model = l.modifyModelBeforeBake(model, ctx);
            }
            return model;
        });
    }

    public static Event<ModelModifier.AfterBakeBlock> modifyBlockModelAfterBake(ModelLoadingPlugin.Context context) {
        return EventFactory.createArrayBacked(ModelModifier.AfterBakeBlock.class, listeners -> (model, ctx) -> {
            for (ModelModifier.AfterBakeBlock l : listeners) {
                model = l.modifyModelAfterBake(model, ctx);
            }
            return model;
        });
    }
}
