package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * Values 1.21.6's RenderPass calls take that 1.21.5's did not: drawIndexed's base vertex and instance count. 1.21.5's
 * drawIndexed(firstIndex, indexCount) drew once with glDrawElements (javap of GlCommandEncoder.drawFromBuffers), i.e. a
 * base vertex of 0 and one instance in 1.21.6's glDrawElementsInstancedBaseVertex.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbRenderPasses {

    private CbRenderPasses() {
    }

    /** the base vertex of a 1.21.5 indexed draw */
    public static int noBaseVertex() {
        return 0;
    }

    /** the instance count of a 1.21.5 draw */
    public static int oneInstance() {
        return 1;
    }
}
