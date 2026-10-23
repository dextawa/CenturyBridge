package top.dext.centurybridge.step.v1_21_2.rt;

import net.minecraft.class_1922;
import net.minecraft.class_1937;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3218;
import net.minecraft.class_3568;
import net.minecraft.class_4076;

/**
 * 1.21.1's fluid and light calls on 1.21.2.
 * <ul>
 * <li>Fluid / FlowingFluid / FluidState ticks and spreading take a ServerLevel since 1.21.2 (and the block state at
 *     the position, which every 1.21.2 caller reads off the level there): 1.21.1 ran them on server levels only
 *     (ServerLevel.tickFluid, randomTick), so a 1.21.1 call hands its level on as the server level it is.</li>
 * <li>LevelLightEngine.lightOnInSection(sectionPos): 1.21.1 asked whether the section's column has light sources
 *     (its storage looked up SectionPos.getZeroNode of the section); 1.21.2 asks lightOnInColumn of that column key.</li>
 * </ul>
 */
public final class CbLevelFluids {

    /** a 1.21.1 fluid call's level, as the server level 1.21.2 takes */
    public static class_3218 server(class_1937 level) {
        if (level instanceof class_3218 server) {
            return server;
        }
        throw new UnsupportedOperationException("1.21.1's fluid tick or spread called on a client level "
            + (level == null ? null : level.getClass().getName()) + ": 1.21.1 ran fluids on server levels, 1.21.2 only can");
    }

    /** the block state at the position, which 1.21.2's callers pass alongside */
    public static class_2680 stateAt(class_1937 level, class_2338 pos) {
        return level.method_8320(pos);
    }

    // ---- light ----

    /** levelLightEngine.lightOnInSection(sectionPos) */
    public static boolean method_51559(class_3568 self, class_4076 section) {
        if (self instanceof LegacyLightOnInSection l) {
            return l.method_51559(section);
        }
        return self.method_62874(class_4076.method_18693(section.method_18694()));
    }

    public interface LegacyLightOnInSection {
        boolean method_51559(class_4076 section);
    }

    public interface SuperLightOnInColumn {
        boolean cb$super$method_62874(long column);
    }

    /** vanilla's lightOnInColumn(column) of a 1.21.1 engine: its lightOnInSection of the column's bottom section */
    public static boolean lightOnInColumn(LegacyLightOnInSection self, long column) {
        return self.method_51559(class_4076.method_18677(column));
    }

    public static boolean superLightOnInSection(SuperLightOnInColumn self, class_4076 section) {
        return self.cb$super$method_62874(class_4076.method_18693(section.method_18694()));
    }

    /** a LightEngine's opacity / occlusion asked with a position 1.21.2 no longer passes: the origin */
    public static class_2338 origin() {
        return class_2338.field_10980;
    }

    private CbLevelFluids() {
    }
}
