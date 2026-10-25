package top.dext.centurybridge.step.v26_2.facade;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 26.1.2's com.mojang.blaze3d.vertex.Tesselator, which 26.2 deleted: one ByteBufferBuilder (786432 bytes by default)
 * handing out a BufferBuilder per begin, the game's instance made by init() and cleared at the end of every frame.
 * Restored as it was (javap of 26.1.2's class; VertexFormat.Mode is 26.2's PrimitiveTopology, a step class rename).
 * 26.1.2's Minecraft constructor called init() just before it built its RenderBuffers and RenderSystem.flipFrame
 * cleared the instance before presenting the frame; the layer does both at the same places of 26.2
 * (mixin/CbS26TesselatorMixin). classRenames send a 26.1.2 mod's references here.
 */
@Environment(EnvType.CLIENT)
public class Tesselator {
    private static final int MAX_BYTES = 786432;
    private final ByteBufferBuilder buffer;
    private static Tesselator instance;

    public static void init() {
        if (instance != null) {
            throw new IllegalStateException("Tesselator has already been initialized");
        }
        instance = new Tesselator();
    }

    public static Tesselator getInstance() {
        if (instance == null) {
            throw new IllegalStateException("Tesselator has not been initialized");
        }
        return instance;
    }

    public Tesselator(int capacity) {
        this.buffer = new ByteBufferBuilder(capacity);
    }

    public Tesselator() {
        this(MAX_BYTES);
    }

    public BufferBuilder begin(PrimitiveTopology mode, VertexFormat format) {
        return new BufferBuilder(this.buffer, mode, format);
    }

    public void clear() {
        this.buffer.clear();
    }

    /** mixin/CbS26TesselatorMixin: 26.1.2 RenderSystem.flipFrame's clear, when the game's instance exists */
    public static void clearGameInstance() {
        if (instance != null) {
            instance.clear();
        }
    }
}
