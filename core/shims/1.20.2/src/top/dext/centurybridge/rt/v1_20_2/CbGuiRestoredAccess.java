package top.dext.centurybridge.rt.v1_20_2;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * What the client layer mixins that restore 1.20.1 members put on vanilla classes for the injectors that go with
 * them. A restoring mixin runs at {@link CbVanillaReads#PRIORITY} so a mod's 1.20.1 injectors can anchor inside the
 * restored bodies; its injectors into 1.20.2's own methods are a separate mixin (the {@code ...Hooks} next to it) of
 * default priority, which reaches the restored members and their state through these interfaces, implemented by
 * the restoring mixin. 1.20.1 names where the member is the 1.20.1 one (so a converted subclass's override answers);
 * {@code cb$} trampolines where the 1.20.1 member is protected (an interface call cannot select a protected override).
 */
@Environment(EnvType.CLIENT)
public final class CbGuiRestoredAccess {

    /** AbstractClientPlayer (CbGuiClientPlayerSkinBridge): the 1.20.1 skin getters */
    public interface SkinGetters {

        boolean method_3125();

        boolean method_3127();

        net.minecraft.class_2960 method_3117();

        net.minecraft.class_2960 method_3119();

        boolean method_3126();

        net.minecraft.class_2960 method_3122();

        String method_3121();
    }

    /** Minecraft (CbGuiMinecraftBridge): 1.20.1's frameTimer field */
    public interface FrameTimer {

        top.dext.centurybridge.facade.v1_20_2.class_3517 cb$frameTimer();
    }

    /** AbstractSelectionList (CbGuiSelectionListBridge): 1.20.1's renderSelection / renderTopAndBottom and renderBackground */
    public interface SelectionList {

        boolean cb$renderSelection();

        boolean cb$renderTopAndBottom();

        /** this.renderBackground(graphics), called virtually */
        void cb$renderBackground(net.minecraft.class_332 graphics);
    }

    /** SkinManager (CbGuiSkinManagerBridge): what 1.20.1's members use of the SkinManager's constructor arguments */
    public interface SkinManager {

        void cb$keepLegacy(net.minecraft.class_1060 textures, java.nio.file.Path dir,
                     com.mojang.authlib.minecraft.MinecraftSessionService sessions);
    }

    /** StateSwitchingButton (CbGuiStateSwitchingButtonBridge): 1.20.1's texture sheet fields */
    public interface StateSwitchingSheet {

        /** resourceLocation, null for a button given 1.20.2 sprites */
        net.minecraft.class_2960 cb$sheet();

        int cb$xTexStart();

        int cb$yTexStart();

        int cb$xDiffTex();

        int cb$yDiffTex();
    }

    /** TabButton (CbGuiTabButtonBridge) */
    public interface TabButton {

        /** this.getTextureY() (protected in 1.20.1), called virtually */
        int cb$textureY();
    }

    /** User (CbGuiUserBridge) */
    public interface User {

        /** 1.20.1 getUuid() */
        String method_1673();

        /** the UUID string the 1.20.1 constructor was given (null for a User built by 1.20.2) */
        void cb$setUuidString(String uuid);
    }

    private CbGuiRestoredAccess() {
    }
}
