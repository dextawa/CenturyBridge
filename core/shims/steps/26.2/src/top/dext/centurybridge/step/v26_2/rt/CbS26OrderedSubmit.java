package top.dext.centurybridge.step.v26_2.rt;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.feature.submit.TranslucentSubmit;
import net.minecraft.client.renderer.gizmos.DrawableGizmoPrimitives;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.state.level.QuadParticleRenderState;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * 26.1.2 classes that IMPLEMENT OrderedSubmitNodeCollector on 26.2 (javap of both interfaces). The calls are
 * {@link CbS26SubmitClient}'s; this is what 26.2 calls on such an implementation.
 * <ul>
 * <li>submitNameTag(.., light, camera): 26.2 dropped the distance argument and computes it when drawing,
 * NameTagFeatureRenderer$Submit.distanceToCameraSq() = TranslucentSubmit.computeDistanceToCameraSq(pose) of the pose
 * SubmitNodeCollection.submitNameTag built: translate(x, y + 0.5, z), then rotation and scale, which leave the
 * translation alone. The old override gets that same value, computed on the pose it is handed.</li>
 * <li>submitBreakingBlockModel(pose, parts, progress): 26.2's LevelRenderer seeds the random with the state's seed and
 * collects the model's parts itself, what 26.1.2's BlockFeatureRenderer did from (model, seed). The old override gets
 * a model whose collectParts adds exactly those parts (the seed no longer chooses anything; 0).</li>
 * <li>submitShapeOutline / submitGizmoPrimitives: new in 26.2, no 26.1.2 counterpart. submitQuadParticleGroup: 26.1.2's
 * submitParticleGroup took a ParticleGroupRenderer (prepare(ParticleBufferCache, translucent) / render(PreparedBuffers,
 * ..)); 26.2 has none of those types (whole-jar check: QuadParticleFeatureRenderer builds layers from the state
 * itself), so an old implementation cannot be handed a 26.2 group it could draw. The 26.1 layer's
 * CbFapiSubmitBlocks$OrderedView forwards all three to the collector it wraps; any other 26.1.2 implementation drops
 * them (KNOWN LOSS, logged once per class). Vanilla's collectors implement them and are never filled.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbS26OrderedSubmit {
    private CbS26OrderedSubmit() {}

    /** the 26.1.2 shape an implementation declares (the adapter the hook adds) */
    public interface LegacyNameTag {
        void submitNameTag(PoseStack pose, Vec3 offset, int y, Component name, boolean seeThrough, int light, double distanceToCameraSq,
                           CameraRenderState camera);
    }

    /** the 26.1.2 shape an implementation declares (the adapter the hook adds) */
    public interface LegacyBreakingBlockModel {
        void submitBreakingBlockModel(PoseStack pose, BlockStateModel model, long seed, int progress);
    }

    /** 26.2's submitNameTag of a 26.1.2 implementation */
    public static void submitNameTag(LegacyNameTag self, PoseStack pose, Vec3 offset, int y, Component name, boolean seeThrough, int light,
                                     CameraRenderState camera) {
        float distance = offset == null ? TranslucentSubmit.computeDistanceToCameraSq(pose.last().pose())
            : TranslucentSubmit.computeDistanceToCameraSq(pose.last().pose(), (float) offset.x, (float) (offset.y + 0.5), (float) offset.z);
        self.submitNameTag(pose, offset, y, name, seeThrough, light, distance, camera);
    }

    /** 26.2's submitBreakingBlockModel of a 26.1.2 implementation */
    public static void submitBreakingBlockModel(LegacyBreakingBlockModel self, PoseStack pose, List<BlockStateModelPart> parts, int progress) {
        self.submitBreakingBlockModel(pose, new PartsModel(parts), 0L, progress);
    }

    /**
     * The parts 26.2 already chose, as a model: collectParts adds them; particle material and flags as 26.2's
     * MultiPartModel answers for its parts (the first one's material, every part's flags or-ed).
     */
    public record PartsModel(List<BlockStateModelPart> parts) implements BlockStateModel {
        @Override
        public void collectParts(RandomSource random, List<BlockStateModelPart> out) {
            out.addAll(this.parts);
        }

        @Override
        public Material.Baked particleMaterial() {
            return this.parts.isEmpty() ? null : this.parts.getFirst().particleMaterial();
        }

        @Override
        public int materialFlags() {
            int flags = 0;
            for (BlockStateModelPart part : this.parts) {
                flags |= part.materialFlags();
            }
            return flags;
        }
    }

    public static void submitShapeOutline(OrderedSubmitNodeCollector self, PoseStack pose, VoxelShape shape, RenderType renderType, int color,
                                          float width, boolean translucent) {
        OrderedSubmitNodeCollector target = OrderedView.target(self);
        if (target != null) {
            target.submitShapeOutline(pose, shape, renderType, color, width, translucent);
        } else {
            knownLoss(self, "submitShapeOutline");
        }
    }

    public static void submitGizmoPrimitives(OrderedSubmitNodeCollector self, DrawableGizmoPrimitives.Group group, CameraRenderState camera,
                                             boolean translucent) {
        OrderedSubmitNodeCollector target = OrderedView.target(self);
        if (target != null) {
            target.submitGizmoPrimitives(group, camera, translucent);
        } else {
            knownLoss(self, "submitGizmoPrimitives");
        }
    }

    public static void submitQuadParticleGroup(OrderedSubmitNodeCollector self, QuadParticleRenderState state) {
        OrderedSubmitNodeCollector target = OrderedView.target(self);
        if (target != null) {
            target.submitQuadParticleGroup(state);
        } else {
            knownLoss(self, "submitQuadParticleGroup");
        }
    }

    /**
     * callReshapes (26.1.2 submitParticleGroup(ParticleGroupRenderer) -> 26.2 submitQuadParticleGroup(state)): a 26.1.2
     * caller's argument as 26.2 takes it. 26.1.2's QuadParticleRenderState was itself a ParticleGroupRenderer and is
     * 26.2's state, passed on; any other renderer (a mod's own, whose prepare/render types 26.2 no longer has) has no
     * 26.2 form: an empty state is submitted in its place (draws nothing; known loss, logged once per class).
     */
    public static QuadParticleRenderState quadParticleGroup(Object renderer) {
        if (renderer instanceof QuadParticleRenderState state) {
            return state;
        }
        String cls = renderer == null ? "null" : renderer.getClass().getName();
        if (LOGGED.add(cls + ".submitParticleGroup")) {
            LogUtils.getLogger().warn("[CenturyBridge] {} is a 26.1.2 ParticleGroupRenderer that is not a QuadParticleRenderState;"
                + " 26.2 has no ParticleGroupRenderer, so its particle group is dropped (known loss)", cls);
        }
        return new QuadParticleRenderState();
    }

    private static final Set<String> LOGGED = ConcurrentHashMap.newKeySet();

    private static void knownLoss(OrderedSubmitNodeCollector self, String what) {
        if (LOGGED.add(self.getClass().getName() + "." + what)) {
            LogUtils.getLogger().warn("[CenturyBridge] {} is a 26.1.2 OrderedSubmitNodeCollector; 26.2's {} has no 26.1.2 counterpart it"
                + " could take, so that submission is dropped for it (known loss)", self.getClass().getName(), what);
        }
    }

    /**
     * The 26.1 layer's CbFapiSubmitBlocks$OrderedView (private nested, so no access widener or accessor reaches it):
     * its {@code private final OrderedSubmitNodeCollector target}, read through a private lookup. The class ships in
     * every runtime this layer is in; a missing class or field is a packaging error and fails loudly.
     */
    private static final class OrderedView {
        private static final String NAME = "top.dext.centurybridge.step.v26_1.rt.CbFapiSubmitBlocks$OrderedView";
        private static final Class<?> CLASS;
        private static final VarHandle TARGET;

        static {
            try {
                CLASS = Class.forName(NAME, false, CbS26OrderedSubmit.class.getClassLoader());
                TARGET = MethodHandles.privateLookupIn(CLASS, MethodHandles.lookup()).findVarHandle(CLASS, "target",
                    OrderedSubmitNodeCollector.class);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("26.1 layer's " + NAME + ".target (OrderedSubmitNodeCollector) is missing", e);
            }
        }

        static OrderedSubmitNodeCollector target(OrderedSubmitNodeCollector self) {
            return self.getClass() == CLASS ? (OrderedSubmitNodeCollector) TARGET.get(self) : null;
        }
    }
}
