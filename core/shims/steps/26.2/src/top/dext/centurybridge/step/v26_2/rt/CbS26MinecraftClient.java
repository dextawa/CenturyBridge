package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.blaze3d.pipeline.RenderTarget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.CommandHistory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.WaypointStyleManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.resources.Identifier;

/**
 * 26.1.2 Minecraft members whose state 26.2 moved (javap of both): the screen, the overlay, the toasts and chat
 * opening into the new Gui (minecraft.gui), the HUD-hidden flag and waypoint styles into Hud (gui.hud), the command
 * history into the chat component, the main render target into GameRenderer, the shader-transparency test into
 * GameRenderState. Each answers with the member that now holds that state (the one 26.2's own callers use).
 */
@Environment(EnvType.CLIENT)
public final class CbS26MinecraftClient {
    private CbS26MinecraftClient() {}

    /** 26.1.2 Minecraft.setScreen: 26.2's Gui.setScreen, the same code over the state it moved there */
    public static void setScreen(Minecraft self, Screen screen) {
        self.gui.setScreen(screen);
    }

    /** 26.1.2 Minecraft.screen (read) */
    public static Screen getScreen(Minecraft self) {
        return self.gui.screen();
    }

    /** 26.1.2 Minecraft.screen (write: the field itself, no lifecycle -- as a 26.1.2 assignment was) */
    public static void putScreen(Minecraft self, Screen screen) {
        self.gui.screen = screen;
    }

    /** 26.1.2 Minecraft.getOverlay() */
    public static Overlay getOverlay(Minecraft self) {
        return self.gui.overlay();
    }

    /** 26.1.2 Minecraft.getToastManager() */
    public static ToastManager getToastManager(Minecraft self) {
        return self.gui.toastManager();
    }

    /** 26.1.2 Minecraft.openChatScreen(method): 26.2's Gui.openChatScreen */
    public static void openChatScreen(Minecraft self, ChatComponent.ChatMethod method) {
        self.gui.openChatScreen(method);
    }

    /** 26.1.2 Minecraft.isSingleplayer(), its body: a single-player server that is not published */
    public static boolean isSingleplayer(Minecraft self) {
        IntegratedServer server = self.getSingleplayerServer();
        return server != null && !server.isPublished();
    }

    /** 26.1.2 Minecraft.getMainRenderTarget(): 26.2 keeps the main render target on GameRenderer */
    public static RenderTarget getMainRenderTarget(Minecraft self) {
        return self.gameRenderer.mainRenderTarget();
    }

    /** 26.1.2 static Minecraft.useShaderTransparency(): its body is 26.2's GameRenderState.useShaderTransparency() */
    public static boolean useShaderTransparency() {
        return Minecraft.getInstance().gameRenderer.gameRenderState().useShaderTransparency();
    }

    /** 26.1.2 static Minecraft.renderNames(): !options.hideGui, the flag 26.2 keeps as gui.hud.isHidden() */
    public static boolean renderNames() {
        return !Minecraft.getInstance().gui.hud.isHidden();
    }

    /** 26.1.2 Minecraft.getWaypointStyles(): 26.2's Hud.getWaypointStyles() */
    public static WaypointStyleManager getWaypointStyles(Minecraft self) {
        return self.gui.hud.getWaypointStyles();
    }

    /** 26.1.2 Minecraft.commandHistory(): the one CommandHistory, which 26.2's chat component holds */
    public static CommandHistory commandHistory(Minecraft self) {
        return self.gui.hud.getChat().commandHistory;
    }

    /** 26.1.2 Minecraft.UNIFORM_FONT: Identifier.withDefaultNamespace("uniform") */
    private static final Identifier UNIFORM_FONT = Identifier.withDefaultNamespace("uniform");
    private static final Identifier ALT_FONT = Identifier.withDefaultNamespace("alt");

    public static Identifier UNIFORM_FONT() {
        return UNIFORM_FONT;
    }

    /** 26.1.2 Minecraft.ALT_FONT: Identifier.withDefaultNamespace("alt") */
    public static Identifier ALT_FONT() {
        return ALT_FONT;
    }

    /**
     * 26.1.2 Minecraft.destroy(): "Stopping!", the narrator destroyed, the level left, the screen removed, close();
     * finally the time source back to System::nanoTime and the process exited (status 0) unless a delayed crash was
     * pending. 26.2's exitWorldAndClose is that body without the narrator (26.2's close() destroys it) and without the
     * ending: 26.2's Main shuts the process down after it. 26.2 has no delayed crash (its crash path exits at once), so
     * the layer always exits, as 26.1.2 did with none pending.
     */
    public static void destroy(Minecraft self) {
        try {
            self.exitWorldAndClose();
        } finally {
            net.minecraft.util.Util.setTimeSource(System::nanoTime);
            System.exit(0);
        }
    }
}
