package top.dext.centurybridge.step.v1_21_6.facade.blaze3d;

/**
 * 1.21.5's com.mojang.blaze3d.buffers.BufferType, which 1.21.6 removed: a GPU buffer's binding target. 1.21.6 states a
 * buffer's roles as GpuBuffer usage bits instead (rt/CbBuffers maps between the two). The same seven constants in the
 * same order (javap).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public enum BufferType {
    VERTICES,
    INDICES,
    PIXEL_PACK,
    COPY_READ,
    COPY_WRITE,
    PIXEL_UNPACK,
    UNIFORM
}
