package top.dext.centurybridge.step.v26_1.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/**
 * 1.21.11's Minecraft.ChatStatus (net/minecraft/class_310$class_5859), which 26.1 replaced by ChatAbilities:
 * the same four constants in the same order (name(), values(), valueOf and ordinal as 1.21.11), the same
 * messages and isChatAllowed answers; Minecraft.getChatStatus() (rt/CbWallGameRenderer) decides it from
 * the same three sources 1.21.11 read. The constants also answer to their intermediary field names.
 */
@Environment(EnvType.CLIENT)
public enum ChatStatus {
    ENABLED(CommonComponents.EMPTY) {
        @Override
        public boolean method_33886(boolean isLocalServer) {
            return true;
        }
    },
    DISABLED_BY_OPTIONS(Component.translatable("chat.disabled.options").withStyle(ChatFormatting.RED)) {
        @Override
        public boolean method_33886(boolean isLocalServer) {
            return false;
        }
    },
    DISABLED_BY_LAUNCHER(Component.translatable("chat.disabled.launcher").withStyle(ChatFormatting.RED)) {
        @Override
        public boolean method_33886(boolean isLocalServer) {
            return isLocalServer;
        }
    },
    DISABLED_BY_PROFILE(Component.translatable("chat.disabled.profile",
            Component.keybind(Minecraft.getInstance().options.keyChat.getName())).withStyle(ChatFormatting.RED)) {
        @Override
        public boolean method_33886(boolean isLocalServer) {
            return isLocalServer;
        }
    };

    public static final ChatStatus field_28940 = ENABLED;
    public static final ChatStatus field_28941 = DISABLED_BY_OPTIONS;
    public static final ChatStatus field_28942 = DISABLED_BY_LAUNCHER;
    public static final ChatStatus field_28943 = DISABLED_BY_PROFILE;
    /** INFO_DISABLED_BY_PROFILE */
    public static final Component field_39456 = Component.translatable("chat.disabled.profile.moreInfo");

    private final Component message;

    ChatStatus(Component message) {
        this.message = message;
    }

    /** getMessage() */
    public Component method_33885() {
        return message;
    }

    /** isChatAllowed(isLocalServer) */
    public abstract boolean method_33886(boolean isLocalServer);
}
