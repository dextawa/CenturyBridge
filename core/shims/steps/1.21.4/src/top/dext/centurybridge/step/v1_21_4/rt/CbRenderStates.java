package top.dext.centurybridge.step.v1_21_4.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.3's item fields of the entity render states on 1.21.4.
 * <p>
 * 1.21.3's renderers extracted, for every item an entity shows, the stack (a copy) and the BakedModel the item renderer
 * resolved for it, and layers drew them with ItemRenderer.render(.., model). 1.21.4's states carry an ItemStackRenderState
 * instead. The states get 1.21.3's fields back (the CbState*Mixin classes), and 1.21.4's extraction fills them as 1.21.3's
 * did -- same stacks, models resolved through 1.21.3's getModel / resolveItemModel ({@link CbItemRender}) -- so a 1.21.3
 * layer reading them, and drawing them, sees what it saw.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderStates {
    private CbRenderStates() {
    }

    private static net.minecraft.class_918 itemRenderer() {
        return net.minecraft.class_310.method_1551().method_1480();
    }

    /** ItemRenderer.getModel(stack, level, entity, seed), as 1.21.3's renderers asked it */
    public static net.minecraft.class_1087 model(net.minecraft.class_1799 stack, net.minecraft.class_1937 level, net.minecraft.class_1309 entity, int seed) {
        return CbItemRender.getModel(itemRenderer(), stack, level, entity, seed);
    }

    /** ItemRenderer.resolveItemModel(stack, entity, context) */
    public static net.minecraft.class_1087 resolve(net.minecraft.class_1799 stack, net.minecraft.class_1309 entity, net.minecraft.class_811 context) {
        return CbItemRender.resolveItemModel(itemRenderer(), stack, entity, context);
    }

    /** LivingEntityRenderState's 1.21.3 fields (CbStateLivingMixin) */
    public interface CbLegacyLivingState {
        net.minecraft.class_1799 cb$headItem();

        void cb$setHeadItem(net.minecraft.class_1799 stack);

        void cb$setLegacyItems(net.minecraft.class_1087 headItemModel, net.minecraft.class_1306 mainArm, net.minecraft.class_1799 rightHandItem,
            net.minecraft.class_1799 leftHandItem, net.minecraft.class_1087 rightHandItemModel, net.minecraft.class_1087 leftHandItemModel);

        void cb$setHeadItemModel(net.minecraft.class_1087 model);
    }

    /** LivingEntityRenderState's 1.21.3 hand fields, read by the layer bridges (CbRenderLayers) */
    public interface CbLegacyHands {
        net.minecraft.class_1087 cb$rightHandItemModel();

        net.minecraft.class_1087 cb$leftHandItemModel();

        net.minecraft.class_1799 cb$rightHandItem();

        net.minecraft.class_1799 cb$leftHandItem();
    }

    /** was LivingEntityRenderState.headItem (field_53467, an ItemStack: 1.21.4's field of that name is the head item's render state) */
    public static net.minecraft.class_1799 field_53467(net.minecraft.class_10042 state) {
        return ((CbLegacyLivingState) state).cb$headItem();
    }

    public static void field_53467(net.minecraft.class_10042 state, net.minecraft.class_1799 stack) {
        ((CbLegacyLivingState) state).cb$setHeadItem(stack);
    }

    /** 1.21.3 LivingEntityRenderer.extractRenderState's item part */
    public static void extractLiving(net.minecraft.class_1309 entity, net.minecraft.class_10042 state) {
        CbLegacyLivingState s = (CbLegacyLivingState) state;
        net.minecraft.class_1799 head = entity.method_6118(net.minecraft.class_1304.field_6169);
        s.cb$setHeadItem(head.method_7972());
        net.minecraft.class_1087 headModel = resolve(head, entity, net.minecraft.class_811.field_4316);
        net.minecraft.class_1799 right = entity.method_61420(net.minecraft.class_1306.field_6183);
        net.minecraft.class_1799 left = entity.method_61420(net.minecraft.class_1306.field_6182);
        s.cb$setLegacyItems(headModel, entity.method_6068(), right.method_7972(), left.method_7972(),
            resolve(right, entity, net.minecraft.class_811.field_4320), resolve(left, entity, net.minecraft.class_811.field_4323));
    }

    /** 1.21.3 SnowGolemRenderer.extractRenderState: the head item is a carved pumpkin while the golem wears it */
    public static void extractSnowGolem(net.minecraft.class_1473 golem, net.minecraft.class_10042 state) {
        CbLegacyLivingState s = (CbLegacyLivingState) state;
        net.minecraft.class_1799 head = golem.method_6643() ? new net.minecraft.class_1799(net.minecraft.class_1802.field_17519)
            : net.minecraft.class_1799.field_8037;
        s.cb$setHeadItem(head);
        s.cb$setHeadItemModel(resolve(head, golem, net.minecraft.class_811.field_4316));
    }

    /** a stack's model as 1.21.3's single-item renderers resolved it: none for an empty stack */
    public static net.minecraft.class_1087 modelUnlessEmpty(net.minecraft.class_1799 stack, net.minecraft.class_1937 level, int seed) {
        return stack.method_7960() ? null : model(stack, level, null, seed);
    }

    /** a render state with 1.21.3 item fields its renderer's 1.21.4 extraction fills as 1.21.3's did (the CbState*Mixin classes) */
    public interface CbLegacyExtract {
        void cb$extract(net.minecraft.class_1297 entity);
    }

    // ---- OminousItemSpawnerRenderer, whose state type 1.21.4 changed ----

    /** a 1.21.3 subclass's createRenderState() answers 1.21.3's state, which is the ItemClusterRenderState 1.21.4 asks for */
    public static net.minecraft.class_10428 cbOminousState(top.dext.centurybridge.step.v1_21_4.facade.OminousItemSpawnerRenderState state) {
        return state;
    }

    /** was createRenderState(), called by a mod: the renderer's state is 1.21.3's (CbStateOminousRendererMixin) */
    public static top.dext.centurybridge.step.v1_21_4.facade.OminousItemSpawnerRenderState method_62496(net.minecraft.class_9483 renderer) {
        return (top.dext.centurybridge.step.v1_21_4.facade.OminousItemSpawnerRenderState) renderer.method_62496();
    }

    /** was extractRenderState(spawner, state, partialTick), called by a mod */
    public static void method_62497(net.minecraft.class_9483 renderer, net.minecraft.class_9461 spawner,
        top.dext.centurybridge.step.v1_21_4.facade.OminousItemSpawnerRenderState state, float partialTick) {
        renderer.method_62497(spawner, state, partialTick);
    }

    /** was render(state, pose, buffers, light), called by a mod */
    public static void method_58753(net.minecraft.class_9483 renderer, top.dext.centurybridge.step.v1_21_4.facade.OminousItemSpawnerRenderState state,
        net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light) {
        renderer.method_58753(state, pose, buffers, light);
    }

    /** FireworkRocketRenderState's 1.21.3 item (field_53369, an ItemStack: 1.21.4's field of that name is its render state) */
    public interface CbLegacyFireworkState {
        net.minecraft.class_1799 cb$item();

        void cb$setItem(net.minecraft.class_1799 stack);
    }

    public static net.minecraft.class_1799 field_53369(net.minecraft.class_10025 state) {
        return ((CbLegacyFireworkState) state).cb$item();
    }

    public static void field_53369(net.minecraft.class_10025 state, net.minecraft.class_1799 stack) {
        ((CbLegacyFireworkState) state).cb$setItem(stack);
    }
}
