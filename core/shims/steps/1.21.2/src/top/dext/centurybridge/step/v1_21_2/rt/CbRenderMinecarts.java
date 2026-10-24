package top.dext.centurybridge.step.v1_21_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.1's AbstractMinecartRenderer.renderMinecartContents(minecart, partialTick, blockState, poseStack, buffers, light)
 * and TntMinecartRenderer.renderMinecartContents(tntMinecart, partialTick, ...) over 1.21.2's, which take the render
 * state (MinecartRenderState / MinecartTntRenderState) in place of the entity and the partial tick.
 *
 * <p>A mod renderer's 1.21.1 override answers 1.21.2's calls (overrideHooks, bridges {@link #contents} / {@link #tnt})
 * with the minecart the state was extracted from and that frame's partial tick. Its super call, and a call on a
 * renderer that does not override, get 1.21.1's bodies (javap both: 1.21.2's are the same draws on the state's copies
 * of the entity's values): the base one drew the block state through the block renderer with no overlay; the TNT one
 * scaled the block up over the last ten ticks of the fuse and drew it flashing white every five ticks.
 */
@Environment(EnvType.CLIENT)
public final class CbRenderMinecarts {

    /** AbstractMinecartRenderer's 1.21.1 renderMinecartContents, as a mod renderer overrides it */
    public interface Legacy {
        void method_4064(net.minecraft.class_1688 minecart, float partialTick, net.minecraft.class_2680 blockState,
                         net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light);
    }

    /** TntMinecartRenderer's 1.21.1 renderMinecartContents, as a mod renderer overrides it */
    public interface LegacyTnt {
        void method_4137(net.minecraft.class_1701 minecart, float partialTick, net.minecraft.class_2680 blockState,
                         net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light);
    }

    private CbRenderMinecarts() {
    }

    private static net.minecraft.class_776 blockRenderer() {
        return net.minecraft.class_310.method_1551().method_1541();
    }

    /** the synthesized 1.21.2 renderMinecartContents of a mod renderer overriding 1.21.1's (overrideHooks bridge) */
    public static void contents(Legacy renderer, net.minecraft.class_10045 state, net.minecraft.class_2680 blockState,
                                net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light) {
        if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1688 minecart) {
            renderer.method_4064(minecart, CbRenderStateEntity.partialTickOf(state), blockState, poseStack, buffers, light);
        } else {
            // a state no minecart was extracted into (never by 1.21.2's own renderers): the draw 1.21.1's base body made
            superContents(null, null, 0.0F, blockState, poseStack, buffers, light);
        }
    }

    /** the synthesized 1.21.2 TNT renderMinecartContents of a mod renderer overriding 1.21.1's (overrideHooks bridge) */
    public static void tnt(LegacyTnt renderer, net.minecraft.class_10046 state, net.minecraft.class_2680 blockState,
                           net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers, int light) {
        if (CbRenderStateEntity.entityOf(state) instanceof net.minecraft.class_1701 minecart) {
            renderer.method_4137(minecart, CbRenderStateEntity.partialTickOf(state), blockState, poseStack, buffers, light);
        } else {
            // a state no TNT minecart was extracted into: 1.21.1's TNT body cannot run without its fuse; the block as the base body drew it
            superContents(null, null, 0.0F, blockState, poseStack, buffers, light);
        }
    }

    /** 1.21.1 AbstractMinecartRenderer.renderMinecartContents's body (superRedirects) */
    public static void superContents(net.minecraft.class_925<?, ?> renderer, net.minecraft.class_1688 minecart, float partialTick,
                                     net.minecraft.class_2680 blockState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                     int light) {
        blockRenderer().method_3353(blockState, poseStack, buffers, light, net.minecraft.class_4608.field_21444);
    }

    /** 1.21.1 TntMinecartRenderer.renderMinecartContents's body (superRedirects) */
    public static void superTnt(net.minecraft.class_957 renderer, net.minecraft.class_1701 minecart, float partialTick,
                                net.minecraft.class_2680 blockState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                int light) {
        int fuse = minecart.method_7577();
        if (fuse > -1 && (float) fuse - partialTick + 1.0F < 10.0F) {
            float f = 1.0F - ((float) fuse - partialTick + 1.0F) / 10.0F;
            f = net.minecraft.class_3532.method_15363(f, 0.0F, 1.0F);
            f *= f;
            f *= f;
            float scale = 1.0F + f * 0.3F;
            poseStack.method_22905(scale, scale, scale);
        }
        net.minecraft.class_957.method_23190(blockRenderer(), blockState, poseStack, buffers, light, fuse > -1 && fuse / 5 % 2 == 0);
    }

    /** a mod's this.renderMinecartContents(minecart, partialTick, ..) (instanceRedirects): its override, else 1.21.1's body */
    public static void method_4064(net.minecraft.class_925<?, ?> renderer, net.minecraft.class_1688 minecart, float partialTick,
                                   net.minecraft.class_2680 blockState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light) {
        if (renderer instanceof Legacy legacy) {
            legacy.method_4064(minecart, partialTick, blockState, poseStack, buffers, light);
        } else if (renderer instanceof net.minecraft.class_957 tnt && minecart instanceof net.minecraft.class_1701 tntMinecart) {
            // 1.21.1 TntMinecartRenderer.renderMinecartContents(AbstractMinecart, ..) handed the TNT minecart to its own
            method_4137(tnt, tntMinecart, partialTick, blockState, poseStack, buffers, light);
        } else {
            superContents(renderer, minecart, partialTick, blockState, poseStack, buffers, light);
        }
    }

    /** the same call resolved on TntMinecartRenderer, whose 1.21.1 renderMinecartContents(AbstractMinecart, ..) declared the bridge */
    public static void method_4064(net.minecraft.class_957 renderer, net.minecraft.class_1688 minecart, float partialTick,
                                   net.minecraft.class_2680 blockState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light) {
        method_4064((net.minecraft.class_925<?, ?>) renderer, minecart, partialTick, blockState, poseStack, buffers, light);
    }

    /**
     * a TNT renderer subclass's super.renderMinecartContents(minecart, ..) (superRedirects): 1.21.1's TntMinecartRenderer
     * body there, handing the TNT minecart to renderMinecartContents(MinecartTNT, ..) of this renderer
     */
    public static void superTntContents(net.minecraft.class_957 renderer, net.minecraft.class_1688 minecart, float partialTick,
                                        net.minecraft.class_2680 blockState, net.minecraft.class_4587 poseStack,
                                        net.minecraft.class_4597 buffers, int light) {
        method_4137(renderer, (net.minecraft.class_1701) minecart, partialTick, blockState, poseStack, buffers, light);
    }

    /** a mod's this.renderMinecartContents(tntMinecart, partialTick, ..) on a TNT renderer (instanceRedirects) */
    public static void method_4137(net.minecraft.class_957 renderer, net.minecraft.class_1701 minecart, float partialTick,
                                   net.minecraft.class_2680 blockState, net.minecraft.class_4587 poseStack, net.minecraft.class_4597 buffers,
                                   int light) {
        if (renderer instanceof LegacyTnt legacy) {
            legacy.method_4137(minecart, partialTick, blockState, poseStack, buffers, light);
        } else {
            superTnt(renderer, minecart, partialTick, blockState, poseStack, buffers, light);
        }
    }

    /** 1.21.1 AbstractMinecartRenderer.model (instanceFieldGetters): an EntityModel there, the MinecartModel it holds in 1.21.2 */
    @SuppressWarnings("rawtypes")
    public static net.minecraft.class_583 field_4747(net.minecraft.class_925<?, ?> renderer) {
        return renderer.field_4747;
    }
}
