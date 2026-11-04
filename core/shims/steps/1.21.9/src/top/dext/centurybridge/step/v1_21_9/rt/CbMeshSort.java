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

    /**
     * 1.21.8 SortState.centroids() (comp_2840, Vector3f[]): 1.21.9's record keeps the same centroids packed in a
     * CompactVectorArray (x, y, z per entry, javap of get(i, dest)). A converted call (and an access-widened read of the
     * record field) gets them unpacked into a fresh array, entry i = get(i, new Vector3f()); the reverse of
     * {@link #sortState}, so a 1.21.8 array round-trips to equal vectors. Writes to the array do not reach the record:
     * 1.21.8's record field was final and its array was only read (buildSortedIndexBuffer), so nothing wrote it back.
     */
    public static Vector3f[] comp_2840(net.minecraft.class_9801.class_9802 self) {
        net.minecraft.class_11899 packed = self.comp_2840();
        int n = packed.method_74170();
        Vector3f[] out = new Vector3f[n];
        for (int i = 0; i < n; i++) {
            out[i] = packed.method_74173(i, new Vector3f());
        }
        return out;
    }
}
