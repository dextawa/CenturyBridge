package top.dext.centurybridge.step.v26_1.rt;

import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BabyModelTransform;
import net.minecraft.client.model.geom.builders.MeshTransformer;

/**
 * 1.21.11's feline models on 26.1: 26.1 split FelineModel / CatModel / OcelotModel into adult and baby models; the
 * adult ones (AdultFelineModel, AdultCatModel, AdultOcelotModel) are 1.21.11's -- same parts, mesh, constants and
 * animation -- and take their place (classRenames). 1.21.11 shrank a baby feline with FelineModel.BABY_TRANSFORMER,
 * which 26.1 dropped with its baby models; this is 1.21.11's transformer (head scaled apart, 10 / 4 offsets).
 */
@Environment(EnvType.CLIENT)
public final class CbWallFelineModels {

    private CbWallFelineModels() {
    }

    private static final MeshTransformer BABY_TRANSFORMER = new BabyModelTransform(true, 10.0F, 4.0F, Set.of("head"));

    public static MeshTransformer babyTransformer() {
        return BABY_TRANSFORMER;
    }
}
