package top.dext.centurybridge.step.v1_21_6.rt;

/**
 * 1.21.5's RenderSystem shader lights on 1.21.6 (javap of both). 1.21.5 kept the two light directions in
 * RenderSystem.shaderLightDirections (a Vector3f[2]; setShaderLights(first, second) stored them after a render-thread
 * check, getShaderLights() answered the array, and the shaders read it). 1.21.6 keeps them in uniform buffers: Lighting
 * writes each of its fixed setups (level, flat / 3D items, entity in UI, ...) into its own slot once (Std140 vec3 +
 * vec3, Lighting.UBO_SIZE bytes) and RenderSystem.setShaderLights(GpuBufferSlice) selects one. The layer holds
 * 1.21.5's array and one slot of its own: 1.21.5's setShaderLights stores the directions, writes them to that slot as
 * Lighting writes its own, and selects it, so the next draws light as in 1.21.5. getShaderLights() and the climbed
 * accessors of the private field (fieldGetters) answer the array.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public final class CbShaderLights {

    private static final org.joml.Vector3f[] LIGHTS = {new org.joml.Vector3f(), new org.joml.Vector3f()};
    private static com.mojang.blaze3d.buffers.GpuBuffer buffer;

    private CbShaderLights() {
    }

    /** RenderSystem.shaderLightDirections (1.21.5's private static, read by the climbed layers' accessor) */
    public static org.joml.Vector3f[] shaderLightDirections() {
        return LIGHTS;
    }

    /** RenderSystem.getShaderLights() */
    public static org.joml.Vector3f[] getShaderLights() {
        return LIGHTS;
    }

    /** RenderSystem.setShaderLights(first, second) */
    public static void setShaderLights(org.joml.Vector3f first, org.joml.Vector3f second) {
        com.mojang.blaze3d.systems.RenderSystem.assertOnRenderThread();
        LIGHTS[0] = first;
        LIGHTS[1] = second;
        upload();
    }

    /**
     * the 1.21 layer's CbRenderSystem._setShaderLights (1.20.6's thread-check-free setter, climbed): the same store
     * and upload without the render-thread check
     */
    public static void _setShaderLights(org.joml.Vector3f first, org.joml.Vector3f second) {
        LIGHTS[0] = first;
        LIGHTS[1] = second;
        upload();
    }

    /** writes the array's directions to the layer's slot (as Lighting writes its setups) and selects it */
    public static void upload() {
        int size = net.minecraft.class_308.field_60022;
        if (buffer == null) {
            // 136 = the usage Lighting creates its buffer with (javap)
            buffer = com.mojang.blaze3d.systems.RenderSystem.getDevice().createBuffer(() -> "CenturyBridge 1.21.5 shader lights", 136, size);
        }
        com.mojang.blaze3d.buffers.GpuBufferSlice slice = buffer.slice(0, size);
        try (org.lwjgl.system.MemoryStack stack = org.lwjgl.system.MemoryStack.stackPush()) {
            java.nio.ByteBuffer data = com.mojang.blaze3d.buffers.Std140Builder.onStack(stack, size)
                .putVec3(LIGHTS[0]).putVec3(LIGHTS[1]).get();
            com.mojang.blaze3d.systems.RenderSystem.getDevice().createCommandEncoder().writeToBuffer(slice, data);
        }
        com.mojang.blaze3d.systems.RenderSystem.setShaderLights(slice);
    }
}
