package top.dext.centurybridge.step.v1_20_3.mixin;

import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.class_266;
import net.minecraft.class_267;
import net.minecraft.class_269;
import net.minecraft.class_274;
import net.minecraft.class_9015;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldScoreboardV;
import top.dext.centurybridge.step.v1_20_3.rt.CbWorldScores;
import top.dext.centurybridge.step.v1_20_3.rt.CbVanillaReads;

/**
 * 1.20.2's Scoreboard player-score methods on 1.20.3's (covers, see CbWorldScoreboardV): each with its 1.20.2
 * body over the 1.20.3 ScoreHolder API (CbWorldScores), and forAllObjectives over the objectives of the criteria in
 * the order they were added, as 1.20.2 kept them.
 */
@Mixin(value = class_269.class, priority = CbVanillaReads.PRIORITY)
public abstract class CbWorldScoreboardMixin implements CbWorldScoreboardV {

    @Shadow
    @Final
    private Reference2ObjectMap<class_274, List<class_266>> field_1429;

    @Shadow
    @Final
    private Map<String, ?> field_1431;

    @Shadow
    public abstract void method_1176(class_9015 holder, class_266 objective, class_267 score);

    @Override
    public List<String> cb$playerNames() {
        return new ArrayList<>(this.field_1431.keySet());
    }

    @Override
    public class_267 method_1180(String owner, class_266 objective) {
        return CbWorldScores.getOrCreatePlayerScore((class_269) (Object) this, owner, objective);
    }

    @Override
    public Map<class_266, class_267> method_1166(String owner) {
        return CbWorldScores.getPlayerScores((class_269) (Object) this, owner);
    }

    @Override
    public boolean method_1183(String owner, class_266 objective) {
        return CbWorldScores.hasPlayerScore((class_269) (Object) this, owner, objective);
    }

    @Override
    public void method_1155(String owner, class_266 objective) {
        CbWorldScores.resetPlayerScore((class_269) (Object) this, owner, objective);
    }

    @Override
    public final void method_1162(class_274 criteria, String owner, Consumer<class_267> action) {
        for (class_266 objective : this.field_1429.getOrDefault(criteria, Collections.emptyList())) {
            action.accept(this.method_1180(owner, objective));
        }
    }

    @Override
    public void cb$announce(class_9015 holder, class_266 objective, class_267 score) {
        this.method_1176(holder, objective, score);
    }
}
