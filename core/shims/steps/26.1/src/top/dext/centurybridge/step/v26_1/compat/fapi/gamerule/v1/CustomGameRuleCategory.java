package top.dext.centurybridge.step.v26_1.compat.fapi.gamerule.v1;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.gamerules.GameRule;
import top.dext.centurybridge.step.v26_1.rt.CbFapiGameRules;

/**
 * 1.21.11's net.fabricmc.fabric.api.gamerule.v1.CustomGameRuleCategory, which 26.1's Fabric API removed
 * (26.1 vanilla categories take any id: GameRuleCategory.register(Identifier)). Same surface and the same
 * bodies as 1.21.11 (an id and a display name, equal by id); a rule's custom category is what
 * GameRuleBuilder.category(CustomGameRuleCategory) gave it (rt/CbFapiGameRules).
 */
public final class CustomGameRuleCategory {
    private final Identifier id;
    private final Component name;

    public CustomGameRuleCategory(Identifier id, Component name) {
        this.id = id;
        this.name = name;
    }

    public Identifier getId() {
        return this.id;
    }

    public Component getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        return this.id.equals(((CustomGameRuleCategory) o).id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public static <T> Optional<CustomGameRuleCategory> getCategory(GameRule<T> rule) {
        return Optional.ofNullable(CbFapiGameRules.customCategoryOf(rule));
    }
}
