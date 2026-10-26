package top.dext.centurybridge.step.v1_21_6.facade.blaze3d;

/**
 * 1.21.5's com.mojang.blaze3d.buffers.BufferUsage, which 1.21.6 removed: whether the CPU reads a buffer back
 * (readBuffer) and writes into it after creation (writeToBuffer), with GL's static / dynamic / stream hint. The same
 * nine constants, in the same order, with the same flags (javap of its static initializer); rt/CbBuffers maps them to
 * 1.21.6's usage bits.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public enum BufferUsage {
    DYNAMIC_WRITE(false, true),
    STATIC_WRITE(false, true),
    STREAM_WRITE(false, true),
    STATIC_READ(true, false),
    DYNAMIC_READ(true, false),
    STREAM_READ(true, false),
    DYNAMIC_COPY(false, false),
    STATIC_COPY(false, false),
    STREAM_COPY(false, false);

    public final boolean readable;
    public final boolean writable;

    BufferUsage(boolean readable, boolean writable) {
        this.readable = readable;
        this.writable = writable;
    }

    public boolean isReadable() {
        return this.readable;
    }

    public boolean isWritable() {
        return this.writable;
    }
}
