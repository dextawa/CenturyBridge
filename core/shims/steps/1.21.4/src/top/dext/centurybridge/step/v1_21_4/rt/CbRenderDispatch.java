package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3's renderer dispatcher and provider-context constructors on 1.21.4's.
 * <ul>
 * <li>BlockEntityRenderDispatcher took the block, item and entity renderers as Suppliers and asked them only when it reloaded
 *     (building its renderers' context); 1.21.4 takes the values, and the item model resolver, and the entity models as a
 *     Supplier. A 1.21.3 dispatcher keeps its Suppliers and asks them where 1.21.3 did (CbRenderDispatchMixin); the resolver is
 *     the game's, asked at the same moment.</li>
 * <li>EntityRenderDispatcher / the two provider contexts take the item model resolver where 1.21.3 took the item renderer (the
 *     game's resolver, which renders items through the same model manager); the entity context keeps the item renderer it was
 *     given for getItemRenderer().</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbRenderDispatch {
    private CbRenderDispatch() {
    }

    /** BlockEntityRenderDispatcher's 1.21.3 renderer Suppliers (CbRenderDispatchMixin) */
    public interface CbLegacyBlockEntityDispatcher {
        void cb$setSuppliers(Supplier<net.minecraft.class_776> blockRenderer, Supplier<net.minecraft.class_918> itemRenderer,
            Supplier<net.minecraft.class_898> entityRenderer);
    }

    /** the entity models 1.21.3 handed as a value, as the Supplier 1.21.4 takes */
    public static Supplier<net.minecraft.class_5599> cbModelSupplier(net.minecraft.class_5599 models) {
        return () -> models;
    }

    /** a renderer 1.21.4 takes at construction that 1.21.3 asked its Supplier for at reload: set then (CbRenderDispatchMixin) */
    public static net.minecraft.class_776 cbLaterBlockRenderer() {
        return null;
    }

    public static net.minecraft.class_10442 cbLaterResolver() {
        return null;
    }

    public static net.minecraft.class_918 cbLaterItemRenderer() {
        return null;
    }

    public static net.minecraft.class_898 cbLaterEntityRenderer() {
        return null;
    }

    /** new EntityModelSet(): 1.21.3's was empty until its first reload (CbRenderEntityModelSetMixin) */
    public static Map<net.minecraft.class_5601, net.minecraft.class_5607> cbEmptyModels() {
        return com.google.common.collect.ImmutableMap.of();
    }

    /** after new BlockEntityRenderDispatcher(..): the dispatcher keeps 1.21.3's Suppliers */
    public static void cbBlockEntityDispatcherConstructed(net.minecraft.class_824 dispatcher, Supplier<net.minecraft.class_776> blockRenderer,
        Supplier<net.minecraft.class_918> itemRenderer, Supplier<net.minecraft.class_898> entityRenderer) {
        ((CbLegacyBlockEntityDispatcher) dispatcher).cb$setSuppliers(blockRenderer, itemRenderer, entityRenderer);
    }

    /** the item model resolver 1.21.4 takes where 1.21.3 took the item renderer: the game's */
    public static net.minecraft.class_10442 cbResolver(net.minecraft.class_918 itemRenderer) {
        return CbItemRender.cbGameResolver();
    }

    /** the item model resolver a 1.21.4 constructor takes in addition: the game's */
    public static net.minecraft.class_10442 cbGameResolver() {
        return CbItemRender.cbGameResolver();
    }

    // ---- EntityRendererProvider.Context.getItemRenderer() ----

    private static final Map<net.minecraft.class_5617.class_5618, net.minecraft.class_918> ITEM_RENDERERS = new WeakHashMap<>();

    /** after new EntityRendererProvider.Context(..): the context keeps the item renderer it was given */
    public static void cbEntityContextConstructed(net.minecraft.class_5617.class_5618 context, net.minecraft.class_918 itemRenderer) {
        synchronized (ITEM_RENDERERS) {
            ITEM_RENDERERS.put(context, itemRenderer);
        }
    }

    /** a 1.21.3 Context subclass overriding getItemRenderer (renamed onto this, overrideRenames) */
    public interface CbLegacyContextItemRenderer {
        net.minecraft.class_918 cb$getItemRenderer();
    }

    /** was EntityRendererProvider.Context.getItemRenderer() (method_32168): a subclass's own answer, else the renderer it was made with */
    public static net.minecraft.class_918 method_32168(net.minecraft.class_5617.class_5618 context) {
        if (context instanceof CbLegacyContextItemRenderer own) {
            return own.cb$getItemRenderer();
        }
        return superGetItemRenderer(context);
    }

    /** a subclass's super.getItemRenderer(): the renderer the context was made with, else the game's */
    public static net.minecraft.class_918 superGetItemRenderer(net.minecraft.class_5617.class_5618 context) {
        net.minecraft.class_918 own;
        synchronized (ITEM_RENDERERS) {
            own = ITEM_RENDERERS.get(context);
        }
        return own != null ? own : net.minecraft.class_310.method_1551().method_1480();
    }
}
