package top.dext.centurybridge.shims.v1_20_2;

import net.minecraft.class_1923;
import net.minecraft.class_2672;
import net.minecraft.class_2818;
import net.minecraft.class_3193;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_3898;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * 1.20.1's ChunkMap.updateChunkTracking(player, pos, packetCache, wasLoaded, load) back (s1misc):
 * when a chunk comes into a player's view (load and not wasLoaded) it is sent to the player, when it
 * leaves (wasLoaded and not load) the player drops it -- done the 1.20.2 way, through the player's
 * chunk sender (markChunkPendingToSend / dropChunk), which also sends the chunk's debug POI
 * packets that 1.20.1 sent here. The packet cache 1.20.1 filled is not used: 1.20.2 builds chunk
 * packets when it sends them. 1.20.2 tracks views as a whole and does not call this; a chunk map is
 * created by the server level alone, so a subclass override has no way in either.
 */
@Mixin(value = class_3898.class, priority = top.dext.centurybridge.rt.v1_20_2.CbVanillaReads.PRIORITY)
public abstract class CbChunkMapLegacy {

    @Shadow
    @Final
    class_3218 field_17214;

    @Shadow
    protected abstract class_3193 method_17216(long pos);

    @Shadow
    private static void method_52348(class_3222 player, class_2818 chunk) {
        throw new AssertionError();
    }

    @Shadow
    private static void method_52354(class_3222 player, class_1923 pos) {
        throw new AssertionError();
    }

    public void method_17241(class_3222 player, class_1923 pos, MutableObject<class_2672> packetCache, boolean wasLoaded, boolean load) {
        if (player.method_37908() != this.field_17214) {
            return;
        }
        if (load && !wasLoaded) {
            class_3193 holder = this.method_17216(pos.method_8324());
            if (holder != null) {
                class_2818 chunk = holder.method_16144();
                if (chunk != null) {
                    method_52348(player, chunk);
                }
            }
        }
        if (!load && wasLoaded) {
            method_52354(player, pos);
        }
    }
}
