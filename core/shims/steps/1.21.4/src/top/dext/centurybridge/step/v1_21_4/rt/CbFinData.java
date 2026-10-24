package top.dext.centurybridge.step.v1_21_4.rt;

import java.nio.file.Path;
import java.util.Collection;
import net.minecraft.class_2403;
import net.minecraft.class_2461;
import net.minecraft.class_6489;

/**
 * 1.21.3's data generator entry on 1.21.4 (area s9fin).
 * <p>
 * data.Main.createStandardGenerator(output, inputs, client, server, dev, reports, validate, version, alwaysGenerate) built the
 * DataGenerator with every standard provider: client providers (models, equipment assets) when client, SNBT-to-NBT when client or
 * server, server providers when server (dev and reports gating their own groups), and the experimental packs (trade_rebalance,
 * redstone_experiments, minecart_improvements, winter_drop); validate was not read. 1.21.4 split that body into
 * client.data.Main.addClientProviders(generator, client) and data.Main.addServerProviders(generator, inputs, server, dev, reports)
 * over a generator the caller makes. The bridge makes 1.21.3's generator and calls both, adding SNBT-to-NBT for a client-only run
 * as 1.21.3 did (1.21.4 gates it on server alone). The Winter Drop is vanilla in 1.21.4, so its data is part of the vanilla pack
 * addServerProviders writes, the way FeatureFlags.WINTER_DROP is the vanilla flag on this step (CbRestConstants); the other
 * experimental packs are 1.21.4's own.
 */
public final class CbFinData {

    private CbFinData() {
    }

    /** was data.Main.createStandardGenerator(...) (method_4968) */
    public static class_2403 method_4968(Path output, Collection<Path> inputs, boolean client, boolean server, boolean dev,
            boolean reports, boolean validate, class_6489 version, boolean alwaysGenerate) {
        class_2403 generator = new class_2403(output, version, alwaysGenerate);
        if (client) {
            CbFinDataClient.addClientProviders(generator);
            if (!server) {
                generator.method_46564(true).method_46566(o -> new class_2461(o, inputs));
            }
        }
        net.minecraft.data.Main.method_4968(generator, inputs, server, dev, reports);
        return generator;
    }
}
