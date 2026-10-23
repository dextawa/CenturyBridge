package top.dext.centurybridge.step.v1_21.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's BufferBuilder.SortState (class_287$class_5594, gone in 1.21): what a builder needs to re-sort the
 * quads it has already uploaded -- the mode, the vertex count, the quads' centroids and the sorting.
 * getSortState() takes it, restoreSortState() makes the next end() an index-only buffer of the quads re-sorted
 * (CbRenderBufferBuilderMixin). Members 1.20.6 kept package-private are public here: a mod's access widener may
 * name them, and wideners never reach a CenturyBridge class.
 */
@Environment(EnvType.CLIENT)
public class BufferBuilderSortState {

    /** mode */
    public final net.minecraft.class_293.class_5596 field_27358;

    /** vertices */
    public final int field_27359;

    /** sortingPoints */
    public final org.joml.Vector3f[] field_27360;

    /** sorting */
    public final net.minecraft.class_8251 field_43359;

    public BufferBuilderSortState(net.minecraft.class_293.class_5596 mode, int vertices, org.joml.Vector3f[] sortingPoints,
                                  net.minecraft.class_8251 sorting) {
        this.field_27358 = mode;
        this.field_27359 = vertices;
        this.field_27360 = sortingPoints;
        this.field_43359 = sorting;
    }
}
