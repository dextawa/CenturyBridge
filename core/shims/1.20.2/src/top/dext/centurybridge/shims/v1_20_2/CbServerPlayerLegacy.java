package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1923;
import net.minecraft.class_2561;
import net.minecraft.class_2596;
import net.minecraft.class_2666;
import net.minecraft.class_2720;
import net.minecraft.class_2803;
import net.minecraft.class_3222;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.rt.v1_20_2.CbServerPlayers;

/**
 * 1.20.1 ServerPlayer methods 1.20.2 removed or reshaped, back with their 1.20.1 meaning (s1misc):
 * <ul>
 * <li>updateOptions(packet): the client information the packet carries, applied by 1.20.2's
 *     updateOptions(information) -- ServerPlayer's own, so a converted override that super-calls
 *     this does not come back into itself;</li>
 * <li>sendTexturePack(url, hash, required, prompt): the resource-pack push packet, sent to the
 *     player (1.20.2 sends the server's pack during configuration, before a player exists, so an
 *     override of this is not consulted there);</li>
 * <li>trackChunk(pos, packet): the chunk packet, sent to the player;</li>
 * <li>untrackChunk(pos): what 1.20.2's chunk sender does when a chunk leaves a player's view --
 *     forget it if it is still waiting to be sent, else tell a living player's client to drop
 *     it -- so a mod calling this keeps the sender's view consistent.</li>
 * </ul>
 * A converted player overriding trackChunk / untrackChunk is sent chunks through the override
 * again (CbPlayerChunkSenderLegacy).
 */
@Mixin(class_3222.class)
public abstract class CbServerPlayerLegacy {

    private class_3222 cb$self() {
        return (class_3222) (Object) this;
    }

    public void method_14213(class_2803 packet) {
        CbServerPlayers.updateOptions(this.cb$self(), packet.comp_1963());
    }

    public void method_14255(String url, String hash, boolean required, class_2561 prompt) {
        this.cb$self().field_13987.method_14364(new class_2720(url, hash, required, prompt));
    }

    public void method_14205(class_1923 pos, class_2596<?> packet) {
        this.cb$self().field_13987.method_14364(packet);
    }

    public void method_14246(class_1923 pos) {
        class_3222 self = this.cb$self();
        boolean pending = ((CbPlayerChunkSenderAccess) self.field_13987.field_45026).cb$pending().remove(pos.method_8324());
        if (!pending && self.method_5805()) {
            self.field_13987.method_14364(new class_2666(pos));
        }
    }
}
