package top.dext.centurybridge.shims.v26_2;

import org.spongepowered.asm.mixin.Mixin;

/**
 * ChatHud kept its message-history accessor across 1.20.2 but changed the
 * return type from List to net.minecraft.util.ArrayListDeque, which IS an AbstractList -- so the old
 * descriptor is satisfied by simply returning the new value.
 *
 * Four models called this unbridgeable across six retry rounds and I accepted
 * that verdict; re-reading the stub by hand showed the delegation is one line.
 */
@Mixin(net.minecraft.client.gui.components.ChatComponent.class)
public abstract class ChatHudBridge {

    public java.util.List<String> getRecentChat() {
        return ((net.minecraft.client.gui.components.ChatComponent) (Object) this).getRecentChat();
    }
}
