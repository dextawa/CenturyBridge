package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 shape of StructureRenderer.addBoundingBox(box, pieces, isStart, dimensionType) (1.20.2's takes
 * piece records and the level's key), given to a mod subclass overriding it so that the 1.20.2 shape reaches
 * its override (CbGuiDebug.cbAddBoundingBox).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbGuiLegacyStructureRenderer {

    void method_3871(net.minecraft.class_3341 box, java.util.List<net.minecraft.class_3341> pieces,
                     java.util.List<Boolean> isStart, net.minecraft.class_2874 dimensionType);
}
