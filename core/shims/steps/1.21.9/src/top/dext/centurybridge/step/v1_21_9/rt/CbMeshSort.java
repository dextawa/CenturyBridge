package top.dext.centurybridge.step.v1_21_9.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.joml.Vector3f;

/**
 * 1.21.8 MeshData.SortState(Vector3f[] centroids, IndexType) (javap: record class_9801$class_9802 over the array):
 * 1.21.9 keeps the same record over a CompactVectorArray (class_11899: new CompactVectorArray(size), set(i, v) per
 * entry), read by the same buildSortedIndexBuffer. A converted call packs the array entry by entry and makes 1.21.9's
 * record (ctorRedirects, s9g-meshsort.json).
 */
@Environment(EnvType.CLIENT)
public final class CbMeshSort {
    private CbMeshSort() {
    }

    /** new MeshData.SortState(centroids, indexType) */
    public static net.minecraft.class_9801.class_9802 sortState(Vector3f[] centroids,
            com.mojang.blaze3d.vertex.VertexFormat.class_5595 indexType) {
        net.minecraft.class_11899 packed = new net.minecraft.class_11899(centroids.length);
        for (int i = 0; i < centroids.length; i++) {
            packed.method_74174(i, centroids[i]);
        }
        return new net.minecraft.class_9801.class_9802(packed, indexType);
    }
}
