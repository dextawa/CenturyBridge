package top.dext.centurybridge.step.v1_21_9.facade;

import java.util.Objects;
import net.minecraft.class_2350;

/**
 * 1.21.8's WorldBorder.DistancePerDirection (class_2784$class_10763): a side of the border and the distance to it.
 * 1.21.9 keeps the same record only on the client's border render state (class_12078$class_10763), which a dedicated
 * server does not have, so the common 1.21.8 record is restored here. A final class, not a record: a facade's members
 * are public under their intermediary names (a mod's access widener may have opened them); equals / hashCode /
 * toString are the record's (toString as 1.21.8's read on an intermediary runtime).
 */
public final class DistancePerDirection {
    public final class_2350 comp_3671;
    public final double comp_3672;

    public DistancePerDirection(class_2350 direction, double distance) {
        this.comp_3671 = direction;
        this.comp_3672 = distance;
    }

    /** direction() */
    public class_2350 comp_3671() {
        return this.comp_3671;
    }

    /** distance() */
    public double comp_3672() {
        return this.comp_3672;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof DistancePerDirection other && this.comp_3671 == other.comp_3671
                && Double.compare(this.comp_3672, other.comp_3672) == 0;
    }

    @Override
    public int hashCode() {
        return 31 * Objects.hashCode(this.comp_3671) + Double.hashCode(this.comp_3672);
    }

    @Override
    public String toString() {
        return "class_10763[comp_3671=" + this.comp_3671 + ", comp_3672=" + this.comp_3672 + "]";
    }
}
