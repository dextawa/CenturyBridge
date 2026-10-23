package top.dext.centurybridge.step.v1_20_5.facade;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.4's StatsUpdateListener (classRenames): a screen told when the server's statistics arrive. 1.20.5 tells
 * only its StatsScreen; the layer has StatsScreen implement this interface again (mixin/CbClientStatsScreenMixin)
 * and tells any other screen implementing it, as 1.20.4's handleAwardStats did (mixin/CbClientAwardStatsMixin).
 */
@Environment(EnvType.CLIENT)
public interface StatsUpdateListener {

    /** LOADING_SYMBOLS */
    String[] field_2668 = new String[] {"oooooo", "Oooooo", "oOoooo", "ooOooo", "oooOoo", "ooooOo", "oooooO"};

    /** onStatsUpdated() */
    void method_2300();
}
