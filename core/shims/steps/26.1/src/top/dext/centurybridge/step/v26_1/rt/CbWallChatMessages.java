package top.dext.centurybridge.step.v26_1.rt;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;

/**
 * 1.21.11's chat records (javap class_303 / class_303$class_7590) on 26.1's (multiplayer/chat/GuiMessage):
 * <ul>
 * <li>GuiMessage(addedTime, content, signature, tag): 26.1 adds the message's source, read only by ChatAbilities'
 * visible-message filter (javap: SYSTEM_CLIENT always shown, PLAYER while player messages may be received,
 * SYSTEM_SERVER while system messages may be). 1.21.11 showed every message it held. A message with a signature is a
 * signed player message, so it is PLAYER, as 26.1's addPlayerMessage makes it; any other message a 1.21.11 mod builds
 * is made on the client: SYSTEM_CLIENT, which 26.1 always shows, as 1.21.11 did.</li>
 * <li>GuiMessage.Line(addedTime, content, tag, endOfEntry): 26.1's Line holds its parent message instead of the time
 * and tag (Line.addedTime() / tag() answer the parent's). The parent carries 1.21.11's time and tag, no signature,
 * SYSTEM_CLIENT; its content is empty: 26.1 reads nothing else of a line's parent (javap of every
 * GuiMessage$Line.parent reader: none outside Line).</li>
 * </ul>
 */
@Environment(EnvType.CLIENT)
public final class CbWallChatMessages {
    private CbWallChatMessages() {
    }

    /** new GuiMessage(addedTime, content, signature, tag) */
    public static GuiMessage message(int addedTime, Component content, MessageSignature signature, GuiMessageTag tag) {
        return new GuiMessage(addedTime, content, signature, signature != null ? GuiMessageSource.PLAYER : GuiMessageSource.SYSTEM_CLIENT, tag);
    }

    /** new GuiMessage.Line(addedTime, content, tag, endOfEntry) */
    public static GuiMessage.Line line(int addedTime, FormattedCharSequence content, GuiMessageTag tag, boolean endOfEntry) {
        GuiMessage parent = new GuiMessage(addedTime, CommonComponents.EMPTY, null, GuiMessageSource.SYSTEM_CLIENT, tag);
        return new GuiMessage.Line(parent, content, endOfEntry);
    }
}
