package top.dext.centurybridge.step.v26_2.rt;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.pathfinder.PathType;

/**
 * 26.1.2 path navigation members 26.2 dropped (mixin/CbS26GroundNavigationMixin, CbS26StriderNavigationMixin).
 */
public final class CbS26Navigation {
    private CbS26Navigation() {}

    private static final String STRIDER = "net.minecraft.world.entity.monster.Strider$StriderPathNavigation";

    /** super.hasValidPathType(pathType) from Strider$StriderPathNavigation: GroundPathNavigation's restored member, called non-virtually */
    private static final MethodHandle GROUND_HAS_VALID_PATH_TYPE;

    static {
        try {
            Class<?> strider = Class.forName(STRIDER, false, CbS26Navigation.class.getClassLoader());
            GROUND_HAS_VALID_PATH_TYPE = MethodHandles.privateLookupIn(strider, MethodHandles.lookup())
                .findSpecial(GroundPathNavigation.class, "hasValidPathType", MethodType.methodType(boolean.class, PathType.class), strider);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("26.1.2 GroundPathNavigation.hasValidPathType is not restored", e);
        }
    }

    /** 26.1.2 Strider$StriderPathNavigation.hasValidPathType's super call */
    public static boolean superHasValidPathType(GroundPathNavigation strider, PathType pathType) {
        try {
            return (boolean) GROUND_HAS_VALID_PATH_TYPE.invoke(strider, pathType);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable t) {
            throw new IllegalStateException(t);
        }
    }
}
