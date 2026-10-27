package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * SectionRenderDispatcher.CompiledSection and RenderSection's per-RenderType members of 1.21.5 on 1.21.6 (javap of both):
 * <ul>
 * <li>1.21.5 kept a section's compile result in CompiledSection (UNCOMPILED / EMPTY constants, a set of the chunk render
 *     types with geometry, the renderable block entities, the visibility set) and its GPU buffers in RenderSection, one
 *     SectionBuffers per chunk RenderType. 1.21.6 keeps both in the section's SectionMesh (CompiledSectionMesh: the
 *     buffers per ChunkSectionLayer, the block entities, the visibility set; CompiledSectionMesh.UNCOMPILED / EMPTY are
 *     SectionMesh constants), which RenderSection.sectionMesh (1.21.5's compiled reference) holds. The step renames
 *     CompiledSection to SectionMesh.</li>
 * <li>The chunk RenderTypes solid / cutoutMipped / cutout / translucent / tripwire became ChunkSectionLayer, each layer
 *     drawing with the pipeline the render type drew with ({@link #layer}). A RenderType that is no chunk layer was never
 *     in 1.21.5's set or map: it answers "empty" and no buffers, as there.</li>
 * </ul>
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbSectionMeshes {

    private CbSectionMeshes() {
    }

    /** the chunk layer drawing with the pipeline a 1.21.5 chunk render type drew with; null for any other render type */
    public static net.minecraft.class_11515 layer(net.minecraft.class_1921 type) {
        if (!(type instanceof net.minecraft.class_1921.class_4687 composite)) {
            return null;
        }
        for (net.minecraft.class_11515 layer : net.minecraft.class_11515.values()) {
            if (layer.method_72020() == composite.field_56922) {
                return layer;
            }
        }
        return null;
    }

    /** CompiledSection.UNCOMPILED: 1.21.6's CompiledSectionMesh.UNCOMPILED, what an uncompiled section holds */
    public static net.minecraft.class_11517 field_4451() {
        return net.minecraft.class_11516.field_60935;
    }

    /** CompiledSection.EMPTY: 1.21.6's CompiledSectionMesh.EMPTY, what a section with nothing to draw holds */
    public static net.minecraft.class_11517 field_52172() {
        return net.minecraft.class_11516.field_60936;
    }

    /**
     * new CompiledSection(): 1.21.5's empty compile result (no render types, no block entities, a fresh VisibilitySet,
     * no sort state); 1.21.6's CompiledSectionMesh over fresh SectionCompiler.Results (empty block entities and layers,
     * a fresh VisibilitySet, no sort state) and no point of view -- what 1.21.6's compile task builds for a section with
     * nothing in it.
     */
    public static net.minecraft.class_11517 newCompiledSection() {
        return new net.minecraft.class_11516(null, new net.minecraft.class_9810.class_9811());
    }

    /** CompiledSection.isEmpty(RenderType): no geometry of that type (1.21.6: no buffers of that layer) */
    public static boolean isEmpty(net.minecraft.class_11517 mesh, net.minecraft.class_1921 type) {
        net.minecraft.class_11515 layer = layer(type);
        return layer == null || mesh.method_72028(layer);
    }

    /** RenderSection.getBuffers(RenderType): the section's buffers of that type, null when it has none */
    public static net.minecraft.class_10896 getBuffers(net.minecraft.class_846.class_851 section, net.minecraft.class_1921 type) {
        net.minecraft.class_11515 layer = layer(type);
        return layer == null ? null : section.method_72052().method_72034(layer);
    }

    /**
     * RenderSection.uploadSectionLayer(RenderType, MeshData): 1.21.5 queued the upload of that type's buffers into the
     * section; 1.21.6's uploadSectionLayer(Map&lt;layer, MeshData&gt;, CompiledSectionMesh) uploads into the mesh,
     * which is where 1.21.6 draws a section's buffers from. Into a section not compiled yet (UNCOMPILED / EMPTY) 1.21.5's
     * upload drew nothing either -- its draw asked the compiled section's render types, empty there, and the next compile
     * replaced the buffers --, so the data is released as 1.21.5 released it for a closed dispatcher.
     */
    public static java.util.concurrent.CompletableFuture<Void> uploadSectionLayer(net.minecraft.class_846.class_851 section,
            net.minecraft.class_1921 type, net.minecraft.class_9801 data) {
        net.minecraft.class_11515 layer = layer(type);
        if (layer != null && section.method_72052() instanceof net.minecraft.class_11516 compiled) {
            java.util.Map<net.minecraft.class_11515, net.minecraft.class_9801> one = new java.util.EnumMap<>(net.minecraft.class_11515.class);
            one.put(layer, data);
            return section.method_72050(one, compiled);
        }
        data.close();
        return java.util.concurrent.CompletableFuture.completedFuture(null);
    }

    /** RenderSection.uploadSectionIndexBuffer(ByteBufferBuilder.Result, RenderType): the same, for the sorted index buffer */
    public static java.util.concurrent.CompletableFuture<Void> uploadSectionIndexBuffer(net.minecraft.class_846.class_851 section,
            net.minecraft.class_9799.class_9800 indices, net.minecraft.class_1921 type) {
        net.minecraft.class_11515 layer = layer(type);
        if (layer != null && section.method_72052() instanceof net.minecraft.class_11516 compiled) {
            return section.method_68531(compiled, indices, layer);
        }
        indices.close();
        return java.util.concurrent.CompletableFuture.completedFuture(null);
    }

    /**
     * RenderSection.rebuildSectionAsync(SectionRenderDispatcher, RenderRegionCache): 1.21.5 made the compile task and
     * scheduled it on the dispatcher it was given (javap: createCompileTask(cache), dispatcher.schedule(task)); 1.21.6's
     * one-argument form schedules on the section's own dispatcher. The same two calls, on the given dispatcher.
     */
    public static void rebuildSectionAsync(net.minecraft.class_846.class_851 section, net.minecraft.class_846 dispatcher,
            net.minecraft.class_6850 cache) {
        dispatcher.method_22756(section.method_3674(cache));
    }
}
