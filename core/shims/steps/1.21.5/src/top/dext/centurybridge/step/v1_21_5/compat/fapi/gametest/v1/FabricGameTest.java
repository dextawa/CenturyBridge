package top.dext.centurybridge.step.v1_21_5.compat.fapi.gametest.v1;

import java.lang.reflect.Method;
import net.minecraft.class_4516;
import top.dext.centurybridge.step.v1_21_5.rt.CbLegacyGameTests;

/**
 * fabric-gametest-api-v1 2.0.26's net.fabricmc.fabric.api.gametest.v1.FabricGameTest (the 1.21.4 module), which
 * 3.1.3 (1.21.5) dropped: converted references name this interface (classRenames). EMPTY_STRUCTURE is 2.0.26's
 * constant (3.1.3 still ships data/fabric-gametest-api-v1/gametest/structure/empty.snbt); invokeTestMethod's default
 * is 2.0.26's FabricGameTestHelper.invokeTestMethod (rt/CbLegacyGameTests).
 */
public interface FabricGameTest {
    String EMPTY_STRUCTURE = "fabric-gametest-api-v1:empty";

    default void invokeTestMethod(class_4516 context, Method method) {
        CbLegacyGameTests.invokeTestMethod(context, method, this);
    }
}
