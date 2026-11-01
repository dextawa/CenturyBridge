package top.dext.centurybridge.step.v1_21_5.rt;

import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.21.4 BakedModel (class_1087: getQuads(state, side, random), useAmbientOcclusion, isGui3d, usesBlockLight,
 * getParticleIcon, getTransforms) on 1.21.5, where class_1087 is BlockStateModel (collectParts(random, parts),
 * particleIcon) and the quads live in BlockModelParts (class_10889: getQuads(side), useAmbientOcclusion, particleIcon).
 * <ul>
 * <li>A 1.21.4 implementation gets 1.21.5's two methods through the step's adapter hook ({@link LegacyBakedModel}):
 *     collectParts adds one part holding the model's quads for every side. 1.21.4 renderers reseeded the random to the
 *     same seed before each side (ModelBlockRenderer.tesselate*, renderModel, ItemRenderer.renderModelLists: javap
 *     1.21.4, setSeed before every getQuads); 1.21.5 hands collectParts one random and asks the parts for sides later
 *     without one. So the part takes one long from that random and gives every side a random created from it: every side
 *     sees the same random state, as in 1.21.4. state: 1.21.5's collectParts carries no block state, so getQuads gets
 *     null, which 1.21.4 itself passed for items (ItemRenderer.renderModelLists, ModelBlockRenderer.renderModel's
 *     callers with no state); a model whose quads depend on the state answers its stateless quads (known loss).</li>
 * <li>A 1.21.4 call on any 1.21.5 model is answered from its parts (getQuads) or from what 1.21.5 moved elsewhere.</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbBakedModels {

    private CbBakedModels() {
    }

    /** a 1.21.4 BakedModel implementation: the old shapes the adapter reaches */
    public interface LegacyBakedModel extends net.minecraft.class_1087 {
        List<net.minecraft.class_777> method_4707(net.minecraft.class_2680 state, net.minecraft.class_2350 side, net.minecraft.class_5819 random);

        boolean method_4708();

        net.minecraft.class_1058 method_4711();
    }

    /** the one part a 1.21.4 model contributes: its quads per side (index 6: unculled), its ao flag and particle */
    public static final class LegacyPart implements net.minecraft.class_10889 {
        private final List<net.minecraft.class_777>[] quads;
        private final boolean ao;
        private final net.minecraft.class_1058 particle;

        LegacyPart(List<net.minecraft.class_777>[] quads, boolean ao, net.minecraft.class_1058 particle) {
            this.quads = quads;
            this.ao = ao;
            this.particle = particle;
        }

        @Override
        public List<net.minecraft.class_777> method_68509(net.minecraft.class_2350 side) {
            return this.quads[side == null ? 6 : side.ordinal()];
        }

        @Override
        public boolean comp_3751() {
            return this.ao;
        }

        @Override
        public net.minecraft.class_1058 comp_3752() {
            return this.particle;
        }
    }

    /** collectParts(random, parts) of a 1.21.4 model */
    @SuppressWarnings("unchecked")
    public static void collectParts(LegacyBakedModel model, net.minecraft.class_5819 random, List<net.minecraft.class_10889> parts) {
        long seed = random.method_43055();
        List<net.minecraft.class_777>[] quads = new List[7];
        net.minecraft.class_2350[] sides = net.minecraft.class_2350.values();
        for (int i = 0; i < 7; i++) {
            List<net.minecraft.class_777> q = model.method_4707(null, i < 6 ? sides[i] : null, net.minecraft.class_5819.method_43049(seed));
            quads[i] = q == null ? List.of() : q;
        }
        parts.add(new LegacyPart(quads, model.method_4708(), model.method_4711()));
    }

    /** particleIcon() of a 1.21.4 model: its getParticleIcon() */
    public static net.minecraft.class_1058 particleIcon(LegacyBakedModel model) {
        return model.method_4711();
    }

    // ---- 1.21.4 calls on a model ----

    /** getQuads(state, side, random): a 1.21.4 model answers itself; a 1.21.5 one the given side of the parts it collects */
    public static List<net.minecraft.class_777> getQuads(net.minecraft.class_1087 model, net.minecraft.class_2680 state,
            net.minecraft.class_2350 side, net.minecraft.class_5819 random) {
        if (model instanceof LegacyBakedModel legacy) {
            return legacy.method_4707(state, side, random);
        }
        List<net.minecraft.class_10889> parts = new ArrayList<>();
        model.method_68513(random, parts);
        if (parts.size() == 1) {
            return parts.get(0).method_68509(side);
        }
        List<net.minecraft.class_777> out = new ArrayList<>();
        for (net.minecraft.class_10889 p : parts) {
            out.addAll(p.method_68509(side));
        }
        return out;
    }

    /** useAmbientOcclusion(): 1.21.5's ModelBlockRenderer.tesselateBlock takes it from the first collected part (seed 42) */
    public static boolean useAmbientOcclusion(net.minecraft.class_1087 model) {
        if (model instanceof LegacyBakedModel legacy) {
            return legacy.method_4708();
        }
        List<net.minecraft.class_10889> parts = model.method_68512(net.minecraft.class_5819.method_43049(42L));
        return !parts.isEmpty() && parts.get(0).comp_3751();
    }

    /** getParticleIcon(): 1.21.5's particleIcon() */
    public static net.minecraft.class_1058 getParticleIcon(net.minecraft.class_1087 model) {
        return model.method_68511();
    }
}
