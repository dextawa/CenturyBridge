package cb.swallaw;

import net.fabricmc.api.ModInitializer;
import net.minecraft.class_4553;

/**
 * A 1.21.11-era mod whose access widener opens PlayerPredicate.AdvancementDonePredicate (a package-private record
 * with a private constructor) and constructs it. 26.2 moved the class to advancements/predicates/entity
 * (26.1.2 -> 26.2 classRenames): unless the access widener climbs with the class, the line widens nothing on 26.2
 * and the construction is an IllegalAccessError.
 */
public class SwallAwMod implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Object p = new class_4553.class_4555(true);
            System.out.println("CB-SWALLAW ok " + p);
        } catch (Throwable t) {
            System.out.println("CB-SWALLAW FAIL " + t);
        }
    }
}
