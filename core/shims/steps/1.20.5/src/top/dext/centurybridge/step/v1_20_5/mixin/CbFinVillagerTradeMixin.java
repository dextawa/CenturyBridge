package top.dext.centurybridge.step.v1_20_5.mixin;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List;
import com.mojang.datafixers.util.Pair;
import java.util.function.Function;
import net.minecraft.class_1208;
import net.minecraft.class_1220;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.dext.centurybridge.step.v1_20_5.rt.CbFinDatafix;

/**
 * VillagerTradeFix: 1.20.4's protected fix(typed) -- a NamedEntityFix on minecraft:villager fixing the
 * item ids of every trade in the villager's Offers.Recipes -- back for a subclass's call and super
 * call, and 1.20.4's rule shape for a subclass whose own fix the override hook reaches
 * (CbFinDatafix.cbVillagerTradeRule). 1.20.5 fixes each trade type-wide; fix(typed) walks
 * Offers.Recipes as 1.20.4 did and applies 1.20.5's own per-trade fix (the buy/buyB/sell item ids).
 */
@Mixin(net.minecraft.class_1219.class)
public abstract class CbFinVillagerTradeMixin extends DataFix implements CbFinDatafix.CbVillagerTradeRule {

    private CbFinVillagerTradeMixin(Schema schema, boolean changesType) {
        super(schema, changesType);
    }

    @Shadow
    private Typed<?> method_5187(OpticFinder<Pair<String, String>> idFinder, Typed<?> item) {
        throw new AssertionError();
    }

    @Shadow
    private static Typed<?> method_59638(OpticFinder<?> buy, Function<Typed<?>, Typed<?>> fixItem, OpticFinder<?> buyB,
            OpticFinder<?> sell, Typed<?> trade) {
        throw new AssertionError();
    }

    /** was fix(typed) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Typed<?> method_5105(Typed<?> typed) {
        OpticFinder<?> offers = typed.getType().findField("Offers");
        OpticFinder<?> recipes = offers.type().findField("Recipes");
        Type<?> recipesType = recipes.type();
        if (!(recipesType instanceof List.ListType<?> list)) {
            throw new IllegalStateException("Recipes are expected to be a list.");
        }
        Type<?> trade = list.getElement();
        OpticFinder<?> tradeFinder = DSL.typeFinder(trade);
        OpticFinder<?> buy = trade.findField("buy");
        OpticFinder<?> buyB = trade.findField("buyB");
        OpticFinder<?> sell = trade.findField("sell");
        OpticFinder<Pair<String, String>> id = DSL.fieldFinder("id",
            DSL.named(class_1208.field_5713.typeName(), class_1220.method_28295()));
        Function<Typed<?>, Typed<?>> fixItem = item -> this.method_5187(id, item);
        return typed.updateTyped(offers, o -> o.updateTyped(recipes,
            r -> r.updateTyped(tradeFinder, t -> method_59638(buy, fixItem, buyB, sell, t))));
    }

    /** 1.20.4's NamedEntityFix rule ("Villager trade fix", entity minecraft:villager) over a fix function */
    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public TypeRewriteRule cb$namedEntityRule(Function<Typed<?>, Typed<?>> fix) {
        Schema in = this.getInputSchema();
        OpticFinder<?> villager = DSL.namedChoice("minecraft:villager", in.getChoiceType(class_1208.field_5729, "minecraft:villager"));
        Type<?> outVillager = this.getOutputSchema().getChoiceType(class_1208.field_5729, "minecraft:villager");
        return this.fixTypeEverywhereTyped("Villager trade fix", in.getType(class_1208.field_5729),
            this.getOutputSchema().getType(class_1208.field_5729), t -> t.updateTyped((OpticFinder) villager, (Type) outVillager, (Function) fix));
    }
}
