package top.dext.centurybridge.step.v1_21_4.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.compat.fapi.rendering.v1.BuiltinItemRendererRegistry;
import top.dext.centurybridge.step.v1_21_4.mixin.CbItemLayerAccess;
import top.dext.centurybridge.step.v1_21_4.mixin.CbItemStackRenderStateAccess;

/**
 * 1.21.3 BlockEntityWithoutLevelRenderer (class_756, gone in 1.21.4): renderByItem drew a special item (chest, shield,
 * banner, trident, skull, bed, conduit, decorated pot ...) in the model space its caller set up (display transform
 * and centring done). Fabric API first handed the stack to the item's BuiltinItemRendererRegistry renderer. Both on
 * 1.21.4: a registered renderer is called as before; a vanilla special item is drawn by the special model renderer
 * 1.21.4's own item definition gives it, called the way 1.21.4 calls it (in that same model space).
 */
@Environment(EnvType.CLIENT)
public class BlockEntityWithoutLevelRenderer implements net.minecraft.class_4013 {

    /** blockEntityRenderDispatcher */
    public final net.minecraft.class_824 field_27738;
    /** entityModelSet */
    public final net.minecraft.class_5599 field_27739;

    public BlockEntityWithoutLevelRenderer(net.minecraft.class_824 dispatcher, net.minecraft.class_5599 models) {
        this.field_27738 = dispatcher;
        this.field_27739 = models;
    }

    /** onResourceManagerReload: 1.21.3 rebuilt its shield / trident / skull models; 1.21.4's special renderers are baked with the item models */
    @Override
    public void method_14491(net.minecraft.class_3300 resourceManager) {
    }

    /** renderByItem */
    public void method_3166(net.minecraft.class_1799 stack, net.minecraft.class_811 context, net.minecraft.class_4587 pose,
            net.minecraft.class_4597 buffers, int light, int overlay) {
        BuiltinItemRendererRegistry.DynamicItemRenderer fabric = BuiltinItemRendererRegistry.INSTANCE.get(stack.method_7909());
        if (fabric != null) {
            fabric.render(stack, context, pose, buffers, light, overlay);
            return;
        }
        net.minecraft.class_10444 state = new net.minecraft.class_10444();
        net.minecraft.class_310.method_1551().method_65386().method_65598(state, stack, context, false, null, null, 0);
        CbItemStackRenderStateAccess access = (CbItemStackRenderStateAccess) state;
        for (int i = 0; i < access.cb$activeLayerCount(); i++) {
            CbItemLayerAccess layer = (CbItemLayerAccess) access.cb$layers()[i];
            if (layer.cb$special() != null) {
                layer.cb$special().method_65694(layer.cb$specialArgument(), context, pose, buffers, light, overlay, stack.method_7958());
            }
        }
    }
}
