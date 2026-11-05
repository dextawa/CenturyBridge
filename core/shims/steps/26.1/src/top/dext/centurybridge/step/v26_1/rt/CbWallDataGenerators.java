package top.dext.centurybridge.step.v26_1.rt;

import java.nio.file.Path;
import java.util.Collection;
import net.minecraft.WorldVersion;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.Main;

/**
 * The data generator across the namespace wall (javap of both jars).
 * <p>
 * 1.21.11's DataGenerator (class_2403) was one concrete class built from (output, version, alwaysGenerate) that ran its
 * providers through a HashCache. 26.1 made DataGenerator abstract over the output folder alone; DataGenerator.Cached
 * (output, version, alwaysGenerate) is 1.21.11's class -- the same fields, the same HashCache construction and the same
 * run() body instruction for instruction -- and Uncached is new. A 1.21.11 {@code new DataGenerator(..)} is that Cached
 * generator; a 1.21.11 subclass extends Cached (superRenames), so its super(output, version, alwaysGenerate) and its
 * super.run() land on the same body.
 * <p>
 * 1.21.11's data.Main.addServerProviders(generator, inputs, server, dev, reports) (method_4968) is split in 26.1 into
 * addServerConverters(generator, inputs, server, dev) -- SNBT-to-NBT when server, NBT-to-SNBT when dev -- and
 * addServerDefinitionProviders(generator, server, reports) -- every other provider, each gated by the same flag as in
 * 1.21.11's body (provider by provider, javap of both bodies and their lambdas). 26.1's main() passes them the same
 * flags 1.21.11's main() passed method_4968. The converters are added first: 1.21.11 added SNBT-to-NBT first and
 * NBT-to-SNBT among the dev providers; providers write their own outputs and none reads another's, so only the log
 * order of NBT-to-SNBT differs. The providers 26.1 adds (potion, villager trade, feature tags, trade rebalance trade
 * tags) are 26.1's own vanilla data.
 */
public final class CbWallDataGenerators {

    private CbWallDataGenerators() {
    }

    /** was {@code new DataGenerator(output, version, alwaysGenerate)} (class_2403.&lt;init&gt;) */
    public static DataGenerator dataGenerator(Path output, WorldVersion version, boolean alwaysGenerate) {
        return new DataGenerator.Cached(output, version, alwaysGenerate);
    }

    /** was data.Main.addServerProviders(generator, inputs, server, dev, reports) (method_4968) */
    public static void method_4968(DataGenerator generator, Collection<Path> inputs, boolean server, boolean dev,
            boolean reports) {
        Main.addServerConverters(generator, inputs, server, dev);
        Main.addServerDefinitionProviders(generator, server, reports);
    }
}
