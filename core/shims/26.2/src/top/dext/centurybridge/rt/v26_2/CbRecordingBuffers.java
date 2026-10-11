package top.dext.centurybridge.rt.v26_2;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderType;
import top.dext.centurybridge.facade.v26_2.class_4597;

/**
 * The MultiBufferSource a 1.20.1 renderer draws into, over 26.x's submit pipeline. The old
 * code is immediate -- it asks for a buffer per RenderType and writes vertices into it, in
 * between pose-stack changes -- while 26.x collects submissions and draws them later. So
 * each RenderType gets a recorder; once the old method returns, every recording is
 * submitted as custom geometry of that type and replayed into the real buffer at draw time.
 * The old code transformed its vertices itself (through the pose stack it was handed), so
 * the replay submits under an identity pose: same vertices, same order, same buffers.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbRecordingBuffers implements class_4597 {

    private static final PoseStack IDENTITY = new PoseStack();

    private final Map<RenderType, CbRecordingConsumer> recordings = new LinkedHashMap<>();

    @Override
    public VertexConsumer getBuffer(RenderType renderType) {
        return recordings.computeIfAbsent(renderType, k -> new CbRecordingConsumer());
    }

    /** hand everything recorded to the collector, one custom-geometry node per RenderType */
    public void submitTo(SubmitNodeCollector collector) {
        for (Map.Entry<RenderType, CbRecordingConsumer> e : recordings.entrySet()) {
            CbRecordingConsumer rec = e.getValue();
            if (rec.isEmpty()) {
                continue;
            }
            collector.submitCustomGeometry(IDENTITY, e.getKey(), (pose, consumer) -> rec.replay(consumer));
        }
        recordings.clear();
    }
}
