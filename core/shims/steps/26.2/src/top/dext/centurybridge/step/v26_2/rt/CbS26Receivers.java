package top.dext.centurybridge.step.v26_2.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.renderer.GameRenderer;

/**
 * mixinReceivers converters (s26b-receivers.json): 26.2 makes on an object 26.1.2's receiver holds the call 26.1.2
 * made on the receiver itself (26.1.2 minecraft.setScreen(s), 26.2 minecraft.gui.setScreen(s); getToastManager,
 * getChatListener -> Gui; getMainRenderTarget -> GameRenderer; 26.1.2 Gui's moved members -> Hud). toW is the held
 * object (what the step's delegate bodies read: Minecraft.gui, Minecraft.gameRenderer, Gui.hud); toV is the object
 * holding it, read off the W receiver's own back reference (Gui.minecraft, GameRenderer.minecraft, Hud.minecraft.gui;
 * opened by the layer's access widener). Each pair undoes the other: in 26.2 these are final fields set once in the
 * holder's constructor with the held object it built, so toV(toW(v)) == v and toW(toV(w)) == w; a receiver that does
 * not satisfy it (a W object built outside its holder) is refused rather than paired with another instance.
 */
@Environment(EnvType.CLIENT)
public final class CbS26Receivers {
    private CbS26Receivers() {}

    /** Minecraft -> Gui: minecraft.gui */
    public static Gui minecraftToGui(Minecraft minecraft) {
        return minecraft.gui;
    }

    /** Gui -> Minecraft: gui.minecraft, the Minecraft whose gui this is */
    public static Minecraft guiToMinecraft(Gui gui) {
        Minecraft minecraft = gui.minecraft;
        if (minecraft.gui != gui) {
            throw new IllegalStateException("a Gui that is not its Minecraft's gui has no 26.1.2 Minecraft receiver");
        }
        return minecraft;
    }

    /** Minecraft -> GameRenderer: minecraft.gameRenderer */
    public static GameRenderer minecraftToGameRenderer(Minecraft minecraft) {
        return minecraft.gameRenderer;
    }

    /** GameRenderer -> Minecraft: gameRenderer.minecraft, the Minecraft whose gameRenderer this is */
    public static Minecraft gameRendererToMinecraft(GameRenderer gameRenderer) {
        Minecraft minecraft = gameRenderer.minecraft;
        if (minecraft.gameRenderer != gameRenderer) {
            throw new IllegalStateException("a GameRenderer that is not its Minecraft's has no 26.1.2 Minecraft receiver");
        }
        return minecraft;
    }

    /** Gui -> Hud: gui.hud */
    public static Hud guiToHud(Gui gui) {
        return gui.hud;
    }

    /** Hud -> Gui: hud.minecraft.gui, the Gui holding this Hud */
    public static Gui hudToGui(Hud hud) {
        Gui gui = hud.minecraft.gui;
        if (gui.hud != hud) {
            throw new IllegalStateException("a Hud that is not its Gui's hud has no 26.1.2 Gui receiver");
        }
        return gui;
    }
}
