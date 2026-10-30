package top.dext.centurybridge.step.v1_21_9.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_1542;
import net.minecraft.class_1799;
import net.minecraft.class_1935;
import net.minecraft.class_3218;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_9.rt.CbVanillaReads;

/**
 * Entity members 1.21.9 dropped (javap: absent from 1.21.9's class_1297 and every later 1.21.x), 1.21.8's bodies over
 * calls 1.21.9 keeps: spawnAtLocation(level, item, yOffset) is spawnAtLocation(level, new ItemStack(item), yOffset)
 * (method_5699, still there); getServer() is level().getServer() (level() is method_73183 since 1.21.9's renumbering,
 * Level.getServer method_8503). 1.21.9 never calls them.
 */
@Mixin(value = class_1297.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbEntityRestoredMixin {

    /** Entity.spawnAtLocation(level, item, yOffset) */
    public class_1542 method_5870(class_3218 level, class_1935 item, int yOffset) {
        return ((class_1297) (Object) this).method_5699(level, new class_1799(item), (float) yOffset);
    }

    /** Entity.getServer() */
    public MinecraftServer method_5682() {
        return ((class_1297) (Object) this).method_73183().method_8503();
    }
}
