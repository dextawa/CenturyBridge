package top.dext.centurybridge.facade.v1_20_2;

/**
 * 1.20.1 GoalSelectorDebugRenderer.DebugGoal (net/minecraft/class_4205$class_4206): one goal of a mob's goal
 * selector and the position its line is drawn above. 1.20.2 carries a goal in GoalDebugPayload.DebugGoal, a
 * record without a position (addGoalSelector takes one position for all of a mob's goals); CbGuiDebug
 * converts between the two. Reached through classRenames.
 */
@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class class_4205$class_4206 {

    /** pos */
    public final net.minecraft.class_2338 field_18782;
    /** priority */
    public final int field_18783;
    /** name */
    public final String field_18784;
    /** isRunning */
    public final boolean field_18785;

    public class_4205$class_4206(net.minecraft.class_2338 pos, int priority, String name, boolean isRunning) {
        this.field_18782 = pos;
        this.field_18783 = priority;
        this.field_18784 = name;
        this.field_18785 = isRunning;
    }
}
