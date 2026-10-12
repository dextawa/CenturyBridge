package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 Pack.ResourcesSupplier shape: open(id) opens the pack's resources. 1.20.2's supplier
 * has openPrimary(id) and openFull(id, info) (the latter adding the pack's overlay directories,
 * which 1.20.1 packs did not have). A 1.20.1 lambda or method reference is built as this interface
 * (lambdaAdapters) and turned into a 1.20.2 supplier by CbPacks.resourcesSupplier; a class
 * implementing the 1.20.1 interface gets this interface and the two 1.20.2 methods synthesized.
 */
public interface CbLegacyResourcesSupplier {

    /** open(id) */
    net.minecraft.class_3262 open(String id);
}
