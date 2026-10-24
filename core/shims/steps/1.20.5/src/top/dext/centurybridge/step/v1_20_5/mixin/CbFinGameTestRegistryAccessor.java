package top.dext.centurybridge.step.v1_20_5.mixin;

import java.util.Set;
import net.minecraft.class_4519;
import net.minecraft.class_4529;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** GameTestRegistry's last failed tests (1.20.4's getLastFailedTests() answered the live collection; rt/CbFinGameTests) */
@Mixin(class_4519.class)
public interface CbFinGameTestRegistryAccessor {

    @Accessor("field_25302")
    static Set<class_4529> cb$lastFailedTests() {
        throw new AssertionError();
    }
}
