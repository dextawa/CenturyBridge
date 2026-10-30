package top.dext.centurybridge.step.v1_21_11.mixin;

import net.minecraft.class_12206;
import net.minecraft.class_243;
import net.minecraft.class_638;
import org.spongepowered.asm.mixin.Mixin;
import top.dext.centurybridge.step.v1_21_11.rt.CbVanillaReads;

/**
 * 1.21.10 ClientLevel.getSkyColor(Vec3, float) / getCloudColor(float) (class_638.method_23777 / method_23785), gone
 * from 1.21.11 (javap -c -p both). 1.21.10 computed them in ClientLevel from the biome sky colour (sampled around the
 * position), the time of day, rain, thunder and the lightning flash. 1.21.11 moved the same inputs into the environment
 * attributes SKY_COLOR / CLOUD_COLOR (EnvironmentAttributes field_63746 / field_64126): biome values, ClientLevel's own
 * time-based modifier (method_76541) and the weather modifiers, read by SkyRenderer / LevelRenderer. Given back as the
 * level's attribute value: the sky colour at the position, the cloud colour position-free (1.21.10's had no position).
 * The partial tick is not consulted: the attribute system samples the current tick (a sub-tick difference in the
 * time-of-day blend). 1.21.11 never calls these.
 */
@Mixin(value = class_638.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbClientLevelColorsRestoredMixin {

    /** ClientLevel.getSkyColor(pos, partialTick) */
    public int method_23777(class_243 pos, float partialTick) {
        return ((class_638) (Object) this).method_75728().method_75695(class_12206.field_63746, pos);
    }

    /** ClientLevel.getCloudColor(partialTick) */
    public int method_23785(float partialTick) {
        return ((class_638) (Object) this).method_75728().method_75694(class_12206.field_64126);
    }
}
