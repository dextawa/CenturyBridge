package top.dext.centurybridge.step.v1_20_5.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * Minecraft.is64Bit(): 1.20.4 read it once at startup from the JVM's data model / bitmode / os.arch (any of
 * them containing "64"), for the 32-bit Realms warning and the options' render-distance default; 1.20.5 runs
 * only on 64-bit JVMs and dropped the check. The same reading, as 1.20.4's constructor did it.
 */
@Environment(EnvType.CLIENT)
@Mixin(net.minecraft.class_310.class)
public abstract class CbClientMinecraftMixin {

    @Unique
    private static final boolean CB$IS_64_BIT = cb$checkIs64Bit();

    @Unique
    private static boolean cb$checkIs64Bit() {
        for (String key : new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"}) {
            String value = System.getProperty(key);
            if (value != null && value.contains("64")) {
                return true;
            }
        }
        return false;
    }

    /** 1.20.4 Minecraft.is64Bit() */
    public boolean method_1540() {
        return CB$IS_64_BIT;
    }
}
