package top.dext.centurybridge.step.v26_1.rt;

import com.mojang.blaze3d.TracyFrameCapture;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.opengl.GlBackend;
import com.mojang.blaze3d.opengl.GlCommandEncoder;
import com.mojang.blaze3d.opengl.GlDevice;
import com.mojang.blaze3d.platform.BackendOptions;
import com.mojang.blaze3d.platform.DisplayData;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.ScreenManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.platform.WindowEventHandler;
import com.mojang.blaze3d.shaders.GpuDebugOptions;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.BackendCreationException;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.FramerateLimiter;
import net.minecraft.client.Minecraft;
import net.minecraft.util.TimeSource;

/**
 * The 1.21.11 GPU backend and bootstrap API on 26.1.
 * <p>
 * 1.21.11's OpenGL classes were the API: GlDevice implemented GpuDevice, GlCommandEncoder implemented
 * CommandEncoder and handed out GlRenderPass, which was the RenderPass. 26.1 split each into a public API class
 * (GpuDevice, CommandEncoder, RenderPass: validation, debug groups) over a backend interface the GL classes now
 * implement (GpuDeviceBackend, CommandEncoderBackend, RenderPassBackend). So a 1.21.11 caller holding a GL object
 * gets the API object over it, as 26.1's GpuDevice.createCommandEncoder builds it (new CommandEncoder(device,
 * backend)): createCommandEncoder, createRenderPass, mapBuffer and writeToTexture answer what 1.21.11's GL classes
 * answered. GlDevice's debug settings (verbosity, synchronous logs, labels) are 26.1's GpuDebugOptions; a render
 * pass takes its device beside its encoder; a multi-draw's Draw has a base vertex, which 1.21.11 always drew with 0.
 * <p>
 * Bootstrap: 26.1 creates the window and device through a GpuBackend (GlBackend for OpenGL) and passes backend
 * options (exclusive fullscreen, read from the game options as 26.1's Minecraft does) to GLFW's initialisation.
 * 1.21.11's flipFrame(window, tracy) polled events, swapped the window's buffers and polled again; 26.1's
 * flipFrame(tracy) presents through the device (which owns the window) and leaves event polling to the caller.
 * Window.updateDisplay(tracy) flipped the frame and applied a pending fullscreen change (26.1:
 * updateFullscreenIfChanged). limitDisplayFPS moved to FramerateLimiter. 1.21.11's Window took the ScreenManager
 * its caller built (vanilla: one over Monitor::new); 26.1's Window builds that same manager itself, so a caller's is
 * not used.
 */
@Environment(EnvType.CLIENT)
public final class CbWallGpu {

    private CbWallGpu() {
    }

    // ---- GL backend ----

    public static GpuDebugOptions debugOptions(int verbosity, boolean synchronousLogs, boolean labels) {
        return new GpuDebugOptions(verbosity, synchronousLogs, labels);
    }

    public static GlDevice device(GlCommandEncoder encoder) {
        return encoder.device;
    }

    private static CommandEncoder api(GlCommandEncoder encoder) {
        return new CommandEncoder(encoder.device, encoder);
    }

    public static CommandEncoder createCommandEncoder(GlDevice device) {
        return new CommandEncoder(device, device.createCommandEncoder());
    }

    public static RenderPass createRenderPass(GlCommandEncoder encoder, Supplier<String> label, GpuTextureView color,
                                              OptionalInt clearColor) {
        return api(encoder).createRenderPass(label, color, clearColor);
    }

    public static RenderPass createRenderPass(GlCommandEncoder encoder, Supplier<String> label, GpuTextureView color,
                                              OptionalInt clearColor, GpuTextureView depth, OptionalDouble clearDepth) {
        return api(encoder).createRenderPass(label, color, clearColor, depth, clearDepth);
    }

    public static GpuBuffer.MappedView mapBuffer(GlCommandEncoder encoder, GpuBuffer buffer, boolean read, boolean write) {
        return api(encoder).mapBuffer(buffer, read, write);
    }

    public static void writeToTexture(GlCommandEncoder encoder, GpuTexture texture, NativeImage image) {
        api(encoder).writeToTexture(texture, image);
    }

    /** RenderPass.Draw's base vertex: 1.21.11's multi-draws always drew from vertex 0 */
    public static int zeroBaseVertex() {
        return 0;
    }

    // ---- bootstrap ----

    private static BackendOptions backendOptions() {
        Minecraft minecraft = Minecraft.getInstance();
        boolean exclusive = minecraft != null && minecraft.options != null && minecraft.options.exclusiveFullscreen().get();
        return new BackendOptions(exclusive);
    }

    public static LongSupplier initGlfw() {
        return GLX._initGlfw(backendOptions());
    }

    public static TimeSource.NanoTimeSource initBackendSystem() {
        return RenderSystem.initBackendSystem(backendOptions());
    }

    public static void initRenderer(long window, int verbosity, boolean synchronousLogs, ShaderSource shaders, boolean labels) {
        RenderSystem.initRenderer(new GlBackend().createDevice(window, shaders, debugOptions(verbosity, synchronousLogs, labels)));
    }

    /** GLX.make(value, initializer): the initializer runs on the value, which is answered */
    public static <T> T make(T value, Consumer<T> initializer) {
        initializer.accept(value);
        return value;
    }

    public static Window newWindow(WindowEventHandler handler, ScreenManager screens, DisplayData display, String videoMode,
                                   String title) throws BackendCreationException {
        return new Window(handler, display, videoMode, title, new GlBackend());
    }

    public static void flipFrame(Window window, TracyFrameCapture tracy) {
        RenderSystem.pollEvents();
        RenderSystem.flipFrame(tracy);
        RenderSystem.pollEvents();
    }

    public static void updateDisplay(Window window, TracyFrameCapture tracy) {
        flipFrame(window, tracy);
        window.updateFullscreenIfChanged();
    }

    public static void limitDisplayFPS(int fps) {
        FramerateLimiter.limitDisplayFPS(fps);
    }
}
