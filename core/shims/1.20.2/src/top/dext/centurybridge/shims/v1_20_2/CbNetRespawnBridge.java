package top.dext.centurybridge.shims.v1_20_2;

import java.util.Optional;
import net.minecraft.class_1934;
import net.minecraft.class_1937;
import net.minecraft.class_2724;
import net.minecraft.class_2874;
import net.minecraft.class_4208;
import net.minecraft.class_5321;
import org.spongepowered.asm.mixin.Mixin;

/**
 * PlayerRespawnS2CPacket: a class of ten fields in 1.20.1; 1.20.2 keeps the flags byte and moves the
 * rest into a CommonPlayerSpawnInfo record. The 1.20.1 getters answer from it, for converted calls
 * and a subclass's super calls (the class is extendable again); vanilla 1.20.2 reads the record.
 */
@Mixin(class_2724.class)
public abstract class CbNetRespawnBridge {

    /** was getDimensionType() */
    public class_5321<class_2874> method_29445() {
        return ((class_2724) (Object) this).comp_1728().comp_1553();
    }

    /** was getDimension() */
    public class_5321<class_1937> method_11779() {
        return ((class_2724) (Object) this).comp_1728().comp_1554();
    }

    /** was getSha256Seed() */
    public long method_22425() {
        return ((class_2724) (Object) this).comp_1728().comp_1555();
    }

    /** was getGameMode() */
    public class_1934 method_11780() {
        return ((class_2724) (Object) this).comp_1728().comp_1556();
    }

    /** was getPreviousGameMode() */
    public class_1934 method_30117() {
        return ((class_2724) (Object) this).comp_1728().comp_1557();
    }

    /** was isDebugWorld() */
    public boolean method_28120() {
        return ((class_2724) (Object) this).comp_1728().comp_1558();
    }

    /** was isFlatWorld() */
    public boolean method_28121() {
        return ((class_2724) (Object) this).comp_1728().comp_1559();
    }

    /** was getLastDeathPos() */
    public Optional<class_4208> method_44326() {
        return ((class_2724) (Object) this).comp_1728().comp_1560();
    }

    /** was getPortalCooldown() */
    public int method_51892() {
        return ((class_2724) (Object) this).comp_1728().comp_1561();
    }
}
