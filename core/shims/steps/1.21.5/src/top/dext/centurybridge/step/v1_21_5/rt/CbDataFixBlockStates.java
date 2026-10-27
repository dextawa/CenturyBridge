package top.dext.centurybridge.step.v1_21_5.rt;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import net.minecraft.class_2509;
import net.minecraft.class_2522;
import org.slf4j.Logger;

/**
 * 1.21.4's BlockStateData (class_3580, the 1.13 flattening table) string forms on 1.21.5 (core-util area). 1.21.4 wrote
 * each block state of the table as an SNBT string with single quotes ("{Name:'minecraft:stone'}") and parsed it with
 * parse(String) (method_15598); 1.21.5 builds the same CompoundTag from the block name and a property map
 * (method_68177 / method_68178: a JavaOps map converted to NbtOps) and dropped parse. Here parse is 1.21.4's body
 * (javap -c -p 1.21.4 class_3580.method_15598): quotes ' -> ", the compound parsed, wrapped in a Dynamic over NbtOps;
 * any exception logged as "Parsing {}" and rethrown wrapped in a RuntimeException. 1.21.4's TagParser.parseTag
 * (method_10718, one compound read from the whole string) is 1.21.5's TagParser.parseCompoundFully (method_67315: the
 * 1.21.5 SNBT grammar over NbtOps, answering a compound or throwing).
 */
public final class CbDataFixBlockStates {

    private static final Logger LOGGER = LogUtils.getLogger();

    private CbDataFixBlockStates() {
    }

    /** was BlockStateData.parse(String) (method_15598) */
    public static Dynamic<?> parse(String state) {
        try {
            return new Dynamic<>(class_2509.field_11560, class_2522.method_67315(state.replace('\'', '"')));
        } catch (Exception e) {
            LOGGER.error("Parsing {}", state, e);
            throw new RuntimeException(e);
        }
    }

    /** register(id, String, String...)'s old states, each through {@link #parse} (the register call reshape) */
    public static Dynamic<?>[] parseAll(String[] states) {
        Dynamic<?>[] out = new Dynamic<?>[states.length];
        for (int i = 0; i < states.length; i++) {
            out[i] = parse(states[i]);
        }
        return out;
    }
}
