package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import top.dext.centurybridge.step.v1_21_4.facade.ItemColor;
import top.dext.centurybridge.step.v1_21_4.facade.ItemColors;
import top.dext.centurybridge.step.v1_21_4.mixin.CbItemLayerAccess;
import top.dext.centurybridge.step.v1_21_4.mixin.CbItemStackRenderStateAccess;

/**
 * 1.21.3 ItemRenderer's model-based API on 1.21.4, which draws an item through its item model definition into an
 * ItemStackRenderState instead.
 *
 * getModel(stack, level, entity, seed) answered the BakedModel the stack would draw (its overrides resolved); it
 * answers a {@link StackModel}: a BakedModel whose quads, transforms and flags are those of the model 1.21.4 resolves
 * for the stack (its first layer), and which carries the stack, so that render(.., model) draws it exactly as 1.21.4
 * draws that stack -- through the definition, for the display context and hand render passes. A model of a mod's own
 * render(.., model) draws as 1.21.3 did: its transform for the context, centred, its quads with the item's render
 * type, foil, and the colours of the renderer's ItemColors (the item colour providers of {@link CbItemColors}).
 */
@Environment(EnvType.CLIENT)
public final class CbItemRender {

    private CbItemRender() {
    }

    /** the ItemColors a mod built its own ItemRenderer with (1.21.4's renderer takes none) */
    private static final Map<net.minecraft.class_918, ItemColors> COLORS = new WeakHashMap<>();

    /** new ItemRenderer(modelManager, itemColors, blockEntityWithoutLevelRenderer): 1.21.4 takes a model resolver */
    public static net.minecraft.class_10442 cbResolver(net.minecraft.class_1092 modelManager) {
        return new net.minecraft.class_10442(modelManager);
    }

    /** the game's item model resolver (Minecraft.getItemModelResolver) */
    public static net.minecraft.class_10442 cbGameResolver() {
        return net.minecraft.class_310.method_1551().method_65386();
    }

    /** the constructed renderer keeps the ItemColors it was given, for models of a mod's own */
    public static void cbConstructed(net.minecraft.class_918 renderer, ItemColors colors) {
        synchronized (COLORS) {
            COLORS.put(renderer, colors);
        }
    }

    // ------------------------------------------------------------------ the stack-carrying model

    /** the BakedModel getModel answers: 1.21.4's resolved model for the stack, which render draws as 1.21.4 draws the stack */
    public static final class StackModel implements net.minecraft.class_1087 {
        public final net.minecraft.class_1799 stack;
        public final net.minecraft.class_1937 level;
        public final net.minecraft.class_1309 entity;
        public final int seed;
        private net.minecraft.class_1087 base;

        StackModel(net.minecraft.class_1799 stack, net.minecraft.class_1937 level, net.minecraft.class_1309 entity, int seed) {
            this.stack = stack;
            this.level = level;
            this.entity = entity;
            this.seed = seed;
        }

        /** the first layer's baked model 1.21.4 resolves for the stack, or the missing model */
        public net.minecraft.class_1087 base() {
            if (this.base == null) {
                net.minecraft.class_10444 state = resolve(this, net.minecraft.class_811.field_4315, false);
                CbItemStackRenderStateAccess access = (CbItemStackRenderStateAccess) state;
                net.minecraft.class_1087 m = access.cb$activeLayerCount() > 0 ? ((CbItemLayerAccess) access.cb$layers()[0]).cb$model() : null;
                this.base = m != null ? m : net.minecraft.class_310.method_1551().method_1554().method_4744();
            }
            return this.base;
        }

        @Override
        public List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
            return this.base().method_4707(state, side, random);
        }

        @Override
        public boolean method_4708() {
            return this.base().method_4708();
        }

        @Override
        public boolean method_4712() {
            return this.base().method_4712();
        }

        @Override
        public boolean method_24304() {
            return this.base().method_24304();
        }

        @Override
        public net.minecraft.class_1058 method_4711() {
            return this.base().method_4711();
        }

        @Override
        public net.minecraft.class_809 method_4709() {
            return this.base().method_4709();
        }
    }

    static net.minecraft.class_10444 resolve(StackModel m, net.minecraft.class_811 context, boolean leftHand) {
        net.minecraft.class_10444 state = new net.minecraft.class_10444();
        net.minecraft.class_310.method_1551().method_65386().method_65598(state, m.stack, context, leftHand, m.level, m.entity, m.seed);
        return state;
    }

    /** was getModel(stack, level, entity, seed) (method_4019) */
    public static net.minecraft.class_1087 getModel(net.minecraft.class_918 renderer, net.minecraft.class_1799 stack, net.minecraft.class_1937 level,
            net.minecraft.class_1309 entity, int seed) {
        return new StackModel(stack, level, entity, seed);
    }

    /** was resolveItemModel(stack, entity, context) (method_62475): null for an empty stack, the seed entity id + context */
    public static net.minecraft.class_1087 resolveItemModel(net.minecraft.class_918 renderer, net.minecraft.class_1799 stack, net.minecraft.class_1309 entity,
            net.minecraft.class_811 context) {
        if (stack.method_7960()) {
            return null;
        }
        return getModel(renderer, stack, entity.method_37908(), entity, entity.method_5628() + context.ordinal());
    }

    // ------------------------------------------------------------------ render

    /** was render(stack, context, leftHand, pose, buffers, light, overlay, model) (method_23179) */
    public static void render(net.minecraft.class_918 renderer, net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand,
            net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, int overlay, net.minecraft.class_1087 model) {
        if (stack.method_7960()) {
            return;
        }
        if (model instanceof StackModel m) {
            resolve(m, context, leftHand).method_65604(pose, buffers, light, overlay);
            return;
        }
        boolean flat = context == net.minecraft.class_811.field_4317 || context == net.minecraft.class_811.field_4318 || context == net.minecraft.class_811.field_4319;
        if (flat && (stack.method_31574(net.minecraft.class_1802.field_8547) || stack.method_31574(net.minecraft.class_1802.field_27070))) {
            // 1.21.3 swapped in the trident's / spyglass's inventory model here; 1.21.4 bakes no such model -- its
            // definition picks the flat model for these contexts itself
            resolve(new StackModel(stack, null, null, 0), context, leftHand).method_65604(pose, buffers, light, overlay);
            return;
        }
        pose.method_22903();
        model.method_4709().method_3503(context).method_23075(leftHand, pose);
        pose.method_46416(-0.5F, -0.5F, -0.5F);
        net.minecraft.class_10444.class_10445 foil = !stack.method_7958() ? net.minecraft.class_10444.class_10445.field_55341
            : stack.method_31573(net.minecraft.class_3489.field_38699) || stack.method_31574(net.minecraft.class_1802.field_8557)
                ? net.minecraft.class_10444.class_10445.field_55343 : net.minecraft.class_10444.class_10445.field_55342;
        net.minecraft.class_918.method_62476(context, pose, buffers, light, overlay, tints(renderer, stack, model), model,
            net.minecraft.class_4696.method_23678(stack), foil);
        pose.method_22909();
    }

    /** was render(.., model, level, entity, seed) (method_62477): an open bundle draws its two layers, as its 1.21.4 definition does */
    public static void render(net.minecraft.class_918 renderer, net.minecraft.class_1799 stack, net.minecraft.class_811 context, boolean leftHand,
            net.minecraft.class_4587 pose, net.minecraft.class_4597 buffers, int light, int overlay, net.minecraft.class_1087 model,
            net.minecraft.class_1937 level, net.minecraft.class_1309 entity, int seed) {
        if (stack.method_7909() instanceof net.minecraft.class_5537 && net.minecraft.class_5537.method_61642(stack)) {
            if (!stack.method_7960()) {
                resolve(new StackModel(stack, level, entity, seed), context, leftHand).method_65604(pose, buffers, light, overlay);
            }
            return;
        }
        render(renderer, stack, context, leftHand, pose, buffers, light, overlay, model);
    }

    /** the colours of a mod's own model's tint indices: the renderer's ItemColors, else the item colour providers */
    private static int[] tints(net.minecraft.class_918 renderer, net.minecraft.class_1799 stack, net.minecraft.class_1087 model) {
        int max = -1;
        net.minecraft.class_5819 random = net.minecraft.class_5819.method_43047();
        for (net.minecraft.class_2350 side : net.minecraft.class_2350.values()) {
            random.method_43052(42L);
            for (net.minecraft.class_777 q : model.method_4707(null, side, random)) {
                max = Math.max(max, q.method_3359());
            }
        }
        random.method_43052(42L);
        for (net.minecraft.class_777 q : model.method_4707(null, null, random)) {
            max = Math.max(max, q.method_3359());
        }
        int[] tints = new int[max + 1];
        ItemColors own;
        synchronized (COLORS) {
            own = COLORS.get(renderer);
        }
        ItemColor provider = own == null ? CbItemColors.get(stack.method_7909()) : null;
        for (int i = 0; i <= max; i++) {
            tints[i] = own != null ? own.method_1704(stack, i) : provider != null ? provider.getColor(stack, i) : -1;
        }
        return tints;
    }

    /** ItemRenderer.TRIDENT_MODEL: minecraft:trident#inventory */
    public static net.minecraft.class_1091 field_40532() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("trident"), "inventory");
    }

    /** ItemRenderer.SPYGLASS_MODEL: minecraft:spyglass#inventory */
    public static net.minecraft.class_1091 field_40533() {
        return new net.minecraft.class_1091(net.minecraft.class_2960.method_60656("spyglass"), "inventory");
    }
}
