package top.dext.centurybridge.step.v1_21_2.facade;

import java.util.Objects;
import java.util.Set;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * WorldGenerationInfo (class_4413) of 1.21.1: the seed, level type, structure flag and experiments a Realms
 * world was to be regenerated with. 1.21.2 removed regenerating a Realms world from a seed with it. 1.21.1's
 * record, as a class (the layer climbs on and the class was not final before 1.20.3): the components, the
 * canonical constructor, record equality and string form. What used it to drive the Realms service is
 * CbRestRealms.
 */
@Environment(EnvType.CLIENT)
public class WorldGenerationInfo {

    public final String comp_1976;
    public final net.minecraft.class_5672 comp_1977;
    public final boolean comp_1978;
    public final Set<String> comp_1991;

    public WorldGenerationInfo(String seed, net.minecraft.class_5672 levelType, boolean generateStructures, Set<String> experiments) {
        this.comp_1976 = seed;
        this.comp_1977 = levelType;
        this.comp_1978 = generateStructures;
        this.comp_1991 = experiments;
    }

    /** seed() */
    public String comp_1976() {
        return this.comp_1976;
    }

    /** levelType() */
    public net.minecraft.class_5672 comp_1977() {
        return this.comp_1977;
    }

    /** generateStructures() */
    public boolean comp_1978() {
        return this.comp_1978;
    }

    /** experiments() */
    public Set<String> comp_1991() {
        return this.comp_1991;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof WorldGenerationInfo other && this.comp_1978 == other.comp_1978
            && Objects.equals(this.comp_1976, other.comp_1976) && Objects.equals(this.comp_1977, other.comp_1977)
            && Objects.equals(this.comp_1991, other.comp_1991);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.comp_1976, this.comp_1977, this.comp_1978, this.comp_1991);
    }

    /** the record's string form as 1.21.1 printed it at runtime (the record class's simple name under intermediary) */
    @Override
    public String toString() {
        return "class_4413[seed=" + this.comp_1976 + ", levelType=" + this.comp_1977 + ", generateStructures="
            + this.comp_1978 + ", experiments=" + this.comp_1991 + "]";
    }
}
