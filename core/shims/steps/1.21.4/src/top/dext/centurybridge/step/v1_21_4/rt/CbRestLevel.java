package top.dext.centurybridge.step.v1_21_4.rt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1508;
import net.minecraft.class_1510;
import net.minecraft.class_1937;
import top.dext.centurybridge.step.v1_21_4.mixin.CbRestLevelInvoker;

/**
 * Level.dragonParts(), abstract since 1.21.4 (abstractFills): 1.21.4's getEntities(..) adds the dragon parts
 * from it, where 1.21.3's added each ender dragon's parts as it met the dragon. A 1.21.3 level subclass (a
 * mod's fake or preview level) gets the parts of the ender dragons it holds, the parts 1.21.3's lookup could
 * find there.
 */
public final class CbRestLevel {

    private CbRestLevel() {
    }

    public static Collection<class_1508> dragonParts(class_1937 level) {
        List<class_1508> parts = new ArrayList<>();
        for (class_1297 e : ((CbRestLevelInvoker) level).cb$entities().method_31803()) {
            if (e instanceof class_1510 dragon) {
                for (class_1508 part : dragon.method_5690()) {
                    parts.add(part);
                }
            }
        }
        return parts;
    }
}
