package top.dext.centurybridge.rt.v1_20_2;

/**
 * The 1.20.1 shape of GoalSelectorDebugRenderer.addGoalSelector(mobId, goals) (1.20.2's takes the mob's
 * position and goal records), given to a mod subclass overriding it so that the 1.20.2 shape reaches its
 * override (CbGuiDebug.cbAddGoalSelector).
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public interface CbGuiLegacyGoalRenderer {

    void method_19430(int mobId, java.util.List<top.dext.centurybridge.facade.v1_20_2.class_4205$class_4206> goals);
}
