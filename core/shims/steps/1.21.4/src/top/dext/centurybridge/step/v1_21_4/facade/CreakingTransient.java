package top.dext.centurybridge.step.v1_21_4.facade;

import net.minecraft.class_10275;
import net.minecraft.class_10319;
import net.minecraft.class_1282;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1937;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2388;
import net.minecraft.class_238;
import net.minecraft.class_2398;
import net.minecraft.class_243;
import net.minecraft.class_3218;
import top.dext.centurybridge.step.v1_21_4.rt.CbRestKillScore;

/**
 * 1.21.3's CreakingTransient (net/minecraft/class_10281, classRenames): the creaking a creaking heart summons.
 * 1.21.4 folded it into Creaking, which behaves as 1.21.3's transient creaking whenever it has a home heart
 * (isHeartBound); this class is a Creaking with 1.21.3's own members:
 * <ul>
 * <li>bindToCreakingHeart(pos): 1.21.3's body set the home heart position (1.21.4's setHomePos);</li>
 * <li>tearDown(source): 1.21.3's body -- pale oak wood and active heart crumble particles, the death sound, the
 *     kill awarded to the source's entity with the creaking's deathScore (when not negative), then removal;</li>
 * <li>INVULNERABILITY_ANIMATION_DURATION: 8, 1.21.3's value.</li>
 * </ul>
 * Its other 1.21.3 overrides (hurt, tick, aiStep, entity events, navigation, ...) are 1.21.4 Creaking's own
 * heart-bound behaviour, which a subclass's super call reaches.
 */
public class CreakingTransient extends class_10275 {

    /** INVULNERABILITY_ANIMATION_DURATION */
    public static final int field_54587 = 8;

    public CreakingTransient(class_1299<? extends class_10275> type, class_1937 level) {
        super(type, level);
    }

    /** bindToCreakingHeart(pos) */
    public void method_64643(class_2338 pos) {
        this.method_65901(pos);
    }

    /** tearDown(source) */
    public void method_64642(class_1282 source) {
        if (this.method_37908() instanceof class_3218 level) {
            class_238 box = this.method_5829();
            class_243 center = box.method_1005();
            double dx = box.method_17939() * 0.3;
            double dy = box.method_17940() * 0.3;
            double dz = box.method_17941() * 0.3;
            level.method_65096(new class_2388(class_2398.field_54870, class_2246.field_54734.method_9564()),
                center.field_1352, center.field_1351, center.field_1350, 100, dx, dy, dz, 0.0);
            level.method_65096(new class_2388(class_2398.field_54870,
                    class_2246.field_54717.method_9564().method_11657(class_10319.field_55064, Boolean.TRUE)),
                center.field_1352, center.field_1351, center.field_1350, 10, dx, dy, dz, 0.0);
        }
        this.method_56078(this.method_6002());
        int score = CbRestKillScore.field_6232(this);
        if (score >= 0 && source != null && source.method_5529() instanceof class_1309 killer) {
            CbRestKillScore.method_5716((class_1297) killer, this, score, source);
        }
        this.method_5650(class_1297.class_5529.field_26999);
    }
}
