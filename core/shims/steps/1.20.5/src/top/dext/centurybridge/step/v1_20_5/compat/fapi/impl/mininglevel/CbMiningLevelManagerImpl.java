package top.dext.centurybridge.step.v1_20_5.compat.fapi.impl.mininglevel;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.event.lifecycle.v1.CommonLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1.20.4's fabric-mining-level-api-v1 MiningLevelManagerImpl on 1.20.5 (Fabric API, Apache-2.0): a state's
 * required level is the highest of its #fabric:needs_tool_level_N tags and the vanilla needs_stone (1) /
 * needs_iron (2) / needs_diamond (3) tags, cached per thread until tags reload.
 */
public final class CbMiningLevelManagerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger("centurybridge/fabric-mining-level-api-v1");
    private static final String TOOL_TAG_NAMESPACE = "fabric";
    private static final Pattern TOOL_TAG_PATTERN = Pattern.compile("^needs_tool_level_([0-9]+)$");
    private static final ThreadLocal<Reference2IntMap<net.minecraft.class_2680>> CACHE = ThreadLocal.withInitial(Reference2IntOpenHashMap::new);

    static {
        CommonLifecycleEvents.TAGS_LOADED.register((registries, client) -> CACHE.get().clear());
    }

    private CbMiningLevelManagerImpl() {
    }

    public static int getRequiredMiningLevel(net.minecraft.class_2680 state) {
        return CACHE.get().computeIfAbsent(state, s -> {
            int miningLevel = 0; // MiningLevels.HAND
            for (net.minecraft.class_6862<net.minecraft.class_2248> tagId : state.method_40144().toList()) {
                if (!tagId.comp_327().method_12836().equals(TOOL_TAG_NAMESPACE)) {
                    continue;
                }
                Matcher matcher = TOOL_TAG_PATTERN.matcher(tagId.comp_327().method_12832());
                if (matcher.matches()) {
                    try {
                        miningLevel = Math.max(miningLevel, Integer.parseInt(matcher.group(1)));
                    } catch (NumberFormatException e) {
                        LOGGER.error("Could not read mining level from tag #{}", tagId, e);
                    }
                }
            }
            if (state.method_26164(net.minecraft.class_3481.field_33717)) {
                miningLevel = Math.max(miningLevel, 3); // MiningLevels.DIAMOND
            } else if (state.method_26164(net.minecraft.class_3481.field_33718)) {
                miningLevel = Math.max(miningLevel, 2); // MiningLevels.IRON
            } else if (state.method_26164(net.minecraft.class_3481.field_33719)) {
                miningLevel = Math.max(miningLevel, 1); // MiningLevels.STONE
            }
            return miningLevel;
        });
    }
}
