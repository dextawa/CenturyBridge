package top.dext.centurybridge.step.v26_1.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v26_1.rt.CbVanillaReads;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.21.11's public ChatComponent.addMessage(message, signature, tag), given back (cover): 26.1 sorts messages by
 * source as well -- a signed message is a player's, an unsigned one a mod adds is the client's own (26.1's
 * addClientSystemMessage source) -- and keeps the tag given, as 1.21.11 did.
 */
@Environment(EnvType.CLIENT)
@Mixin(value = ChatComponent.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWallChatComponentMixin {

    @Shadow
    private void addMessage(Component message, MessageSignature signature, GuiMessageSource source, GuiMessageTag tag) {
        throw new AssertionError();
    }

    public void addMessage(Component message, MessageSignature signature, GuiMessageTag tag) {
        this.addMessage(message, signature, signature != null ? GuiMessageSource.PLAYER : GuiMessageSource.SYSTEM_CLIENT, tag);
    }
}
