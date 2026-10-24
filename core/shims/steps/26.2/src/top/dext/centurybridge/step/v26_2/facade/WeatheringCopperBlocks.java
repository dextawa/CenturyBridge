package top.dext.centurybridge.step.v26_2.facade;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.apache.commons.lang3.function.TriFunction;

/**
 * 26.1.2's net.minecraft.world.level.block.WeatheringCopperBlocks, which 26.2 replaced by the generic
 * WeatheringCopperCollection (weathering / waxed, each a ByState of four). The record and every member as 26.1.2
 * had them (javap of 26.1.2's class: create's names and weather states, the orders of asList / forEach / the
 * mappings); of(..) builds one from 26.2's collection, for the 26.1.2 constants (Blocks.COPPER_BARS, ...) answered
 * in rt/CbS26Copper. classRenames in s26.json send a 26.1.2 mod's references here.
 */
public record WeatheringCopperBlocks(Block unaffected, Block exposed, Block weathered, Block oxidized, Block waxed,
                                     Block waxedExposed, Block waxedWeathered, Block waxedOxidized) {

    public static WeatheringCopperBlocks of(WeatheringCopperCollection<Block> c) {
        return new WeatheringCopperBlocks(c.weathering().unaffected(), c.weathering().exposed(), c.weathering().weathered(),
            c.weathering().oxidized(), c.waxed().unaffected(), c.waxed().exposed(), c.waxed().weathered(), c.waxed().oxidized());
    }

    /** 26.1.2: registers the eight blocks through the caller's register function, names prefixed by state */
    public static <WaxedBlock extends Block, WeatheringBlock extends Block & WeatheringCopper> WeatheringCopperBlocks create(
            String name, TriFunction<String, Function<BlockBehaviour.Properties, Block>, BlockBehaviour.Properties, Block> register,
            Function<BlockBehaviour.Properties, WaxedBlock> waxedFactory,
            BiFunction<WeatheringCopper.WeatherState, BlockBehaviour.Properties, WeatheringBlock> weatheringFactory,
            Function<WeatheringCopper.WeatherState, BlockBehaviour.Properties> propertiesSupplier) {
        return new WeatheringCopperBlocks(
            register.apply(name, p -> weatheringFactory.apply(WeatheringCopper.WeatherState.UNAFFECTED, p),
                propertiesSupplier.apply(WeatheringCopper.WeatherState.UNAFFECTED)),
            register.apply("exposed_" + name, p -> weatheringFactory.apply(WeatheringCopper.WeatherState.EXPOSED, p),
                propertiesSupplier.apply(WeatheringCopper.WeatherState.EXPOSED)),
            register.apply("weathered_" + name, p -> weatheringFactory.apply(WeatheringCopper.WeatherState.WEATHERED, p),
                propertiesSupplier.apply(WeatheringCopper.WeatherState.WEATHERED)),
            register.apply("oxidized_" + name, p -> weatheringFactory.apply(WeatheringCopper.WeatherState.OXIDIZED, p),
                propertiesSupplier.apply(WeatheringCopper.WeatherState.OXIDIZED)),
            register.apply("waxed_" + name, waxedFactory::apply, propertiesSupplier.apply(WeatheringCopper.WeatherState.UNAFFECTED)),
            register.apply("waxed_exposed_" + name, waxedFactory::apply, propertiesSupplier.apply(WeatheringCopper.WeatherState.EXPOSED)),
            register.apply("waxed_weathered_" + name, waxedFactory::apply, propertiesSupplier.apply(WeatheringCopper.WeatherState.WEATHERED)),
            register.apply("waxed_oxidized_" + name, waxedFactory::apply, propertiesSupplier.apply(WeatheringCopper.WeatherState.OXIDIZED)));
    }

    public ImmutableBiMap<Block, Block> weatheringMapping() {
        return ImmutableBiMap.of(this.unaffected, this.exposed, this.exposed, this.weathered, this.weathered, this.oxidized);
    }

    public ImmutableBiMap<Block, Block> waxedMapping() {
        return ImmutableBiMap.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered,
            this.oxidized, this.waxedOxidized);
    }

    public ImmutableList<Block> asList() {
        return ImmutableList.of(this.unaffected, this.waxed, this.exposed, this.waxedExposed, this.weathered, this.waxedWeathered,
            this.oxidized, this.waxedOxidized);
    }

    public void forEach(Consumer<Block> action) {
        action.accept(this.unaffected);
        action.accept(this.exposed);
        action.accept(this.weathered);
        action.accept(this.oxidized);
        action.accept(this.waxed);
        action.accept(this.waxedExposed);
        action.accept(this.waxedWeathered);
        action.accept(this.waxedOxidized);
    }
}
