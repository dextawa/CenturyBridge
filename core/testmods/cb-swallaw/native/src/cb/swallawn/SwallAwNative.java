package cb.swallawn;

import net.fabricmc.api.ModInitializer;
import net.minecraft.advancements.predicates.entity.PlayerPredicate;

/** cb-swallaw's native 26.2 control: the same construction, its access widener written for 26.2 directly. */
public class SwallAwNative implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Object p = new PlayerPredicate.AdvancementDonePredicate(true);
            System.out.println("CB-SWALLAWN ok " + p);
        } catch (Throwable t) {
            System.out.println("CB-SWALLAWN FAIL " + t);
        }
    }
}
