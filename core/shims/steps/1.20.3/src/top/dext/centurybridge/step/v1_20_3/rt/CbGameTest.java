package top.dext.centurybridge.step.v1_20_3.rt;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * The 1.20.2 game-test structure placement, gone in 1.20.3 (StructureUtils.spawnStructure became
 * prepareTestStructure(GameTestInfo, ..), clearSpaceForStructure lost its floor argument, and both
 * changed what they build: 1.20.3 puts the structure block one lower and fills cleared space with
 * stone where 1.20.2 used the flat world's default layers). Each method is the 1.20.2 body on
 * 1.20.3's API, the 1.20.2 private helpers it used included, so a 1.20.2 caller gets 1.20.2's
 * placement.
 */
public final class CbGameTest {

    /** GameTestRunner.PADDING_AROUND_EACH_STRUCTURE (1.20.2's constant) */
    public static final int field_33148 = 2;

    /** the 1.20.2 GameTestInfo.spawnStructure(pos, padding) a 1.20.2 subclass may override (bridge hook adapter) */
    public interface LegacyTestInfo {
        void method_22166(net.minecraft.class_2338 pos, int padding);
    }

    /** StructureBlockEntity's author, which 1.20.2's loadStructure set and 1.20.3 opens no setter for */
    public interface StructureAuthor {
        void cb$setAuthor(String author);
    }

    /**
     * GameTestInfo.prepareTestStructure(pos) of a 1.20.2 subclass overriding spawnStructure(pos,
     * padding): 1.20.2's test runner called spawnStructure(pos, 2) (GameTestRunner.runTest and
     * ReportGameListener's retry both passed 2) where 1.20.3's calls prepareTestStructure(pos).
     */
    public static void prepareTestStructure(LegacyTestInfo test, net.minecraft.class_2338 pos) {
        test.method_22166(pos, field_33148);
    }

    /**
     * StructureUtils.spawnStructure(name, pos, rotation, padding, level, unlessResized), as 1.20.2:
     * the template's box at pos, the structure block at the rotated corner, chunks around pos
     * force-loaded, the space cleared down to pos's height (1.20.2 never read the padding
     * argument), the structure loaded, the area's pending ticks and block events dropped.
     */
    public static net.minecraft.class_2633 spawnStructure(String name, net.minecraft.class_2338 pos,
            net.minecraft.class_2470 rotation, int padding, net.minecraft.class_3218 level, boolean unlessResized) {
        net.minecraft.class_2382 size = structureTemplate(name, level).method_15160();
        net.minecraft.class_3341 box = net.minecraft.class_4525.method_29409(pos, size, rotation);
        net.minecraft.class_2338 corner;
        if (rotation == net.minecraft.class_2470.field_11467) {
            corner = pos;
        } else if (rotation == net.minecraft.class_2470.field_11463) {
            corner = pos.method_10069(size.method_10260() - 1, 0, 0);
        } else if (rotation == net.minecraft.class_2470.field_11464) {
            corner = pos.method_10069(size.method_10263() - 1, 0, size.method_10260() - 1);
        } else if (rotation == net.minecraft.class_2470.field_11465) {
            corner = pos.method_10069(0, 0, size.method_10263() - 1);
        } else {
            throw new IllegalArgumentException("Invalid rotation: " + rotation);
        }
        forceLoadChunks(pos, level);
        clearSpaceForStructure(box, pos.method_10264(), level);
        net.minecraft.class_2633 block = createStructureBlock(name, corner, rotation, level, unlessResized);
        level.method_14196().method_39380(box);
        level.method_23658(box);
        return block;
    }

    /** 1.20.2 StructureUtils.forceLoadChunks(pos, level) (private): the chunks -1..3 around pos's chunk */
    private static void forceLoadChunks(net.minecraft.class_2338 pos, net.minecraft.class_3218 level) {
        net.minecraft.class_1923 chunk = new net.minecraft.class_1923(pos);
        for (int dx = -1; dx < 4; dx++) {
            for (int dz = -1; dz < 4; dz++) {
                level.method_17988(chunk.field_9181 + dx, chunk.field_9180 + dz, true);
            }
        }
    }

    /**
     * StructureUtils.clearSpaceForStructure(box, floor, level), as 1.20.2: the box grown by 2-3 blocks
     * (20 up) filled block by block -- below {@code floor} with the flat world's default layer for
     * that depth, air above -- its ticks and block events dropped, every non-player entity in it
     * discarded.
     */
    public static void clearSpaceForStructure(net.minecraft.class_3341 box, int floor, net.minecraft.class_3218 level) {
        net.minecraft.class_3341 area = new net.minecraft.class_3341(box.method_35415() - 2, box.method_35416() - 3,
            box.method_35417() - 3, box.method_35418() + 3, box.method_35419() + 20, box.method_35420() + 3);
        net.minecraft.class_2338.method_23627(area).forEach(p -> clearBlock(floor, p, level));
        level.method_14196().method_39380(area);
        level.method_23658(area);
        net.minecraft.class_238 aabb = new net.minecraft.class_238(area.method_35415(), area.method_35416(),
            area.method_35417(), area.method_35418(), area.method_35419(), area.method_35420());
        List<net.minecraft.class_1297> entities = level.method_8390(net.minecraft.class_1297.class, aabb,
            e -> !(e instanceof net.minecraft.class_1657));
        entities.forEach(net.minecraft.class_1297::method_31472);
    }

    /** 1.20.2 StructureUtils.clearBlock(floor, pos, level) (private) */
    private static void clearBlock(int floor, net.minecraft.class_2338 pos, net.minecraft.class_3218 level) {
        net.minecraft.class_2680 state = null;
        net.minecraft.class_5455 registries = level.method_30349();
        net.minecraft.class_3232 flat = net.minecraft.class_3232.method_14309(
            registries.method_46762(net.minecraft.class_7924.field_41236),
            registries.method_46762(net.minecraft.class_7924.field_41248),
            registries.method_46762(net.minecraft.class_7924.field_41245));
        List<net.minecraft.class_2680> layers = flat.method_14312();
        int depth = pos.method_10264() - level.method_31607();
        if (pos.method_10264() < floor && depth > 0 && depth <= layers.size()) {
            state = layers.get(depth - 1);
        }
        if (state == null) {
            state = net.minecraft.class_2246.field_10124.method_9564();
        }
        net.minecraft.class_2247 input = new net.minecraft.class_2247(state, java.util.Collections.emptySet(), null);
        input.method_9495(level, pos, 2);
        level.method_8408(pos, state.method_26204());
    }

    /**
     * 1.20.2 StructureUtils.createStructureBlock(name, pos, rotation, level, unlessResized) (private): a
     * structure block in LOAD mode loading the structure -- the world's own templates first, the
     * test structures directory's snbt file else.
     */
    private static net.minecraft.class_2633 createStructureBlock(String name, net.minecraft.class_2338 pos,
            net.minecraft.class_2470 rotation, net.minecraft.class_3218 level, boolean unlessResized) {
        level.method_8501(pos, net.minecraft.class_2246.field_10465.method_9564());
        net.minecraft.class_2633 block = (net.minecraft.class_2633) level.method_8321(pos);
        block.method_11381(net.minecraft.class_2776.field_12697);
        block.method_11385(rotation);
        block.method_11352(false);
        block.method_11344(new net.minecraft.class_2960(name));
        loadStructure(block, level, unlessResized);
        if (block.method_11349() != net.minecraft.class_2382.field_11176) {
            return block;
        }
        loadStructure(block, level, unlessResized, structureTemplate(name, level));
        if (block.method_11349() == net.minecraft.class_2382.field_11176) {
            throw new RuntimeException("Failed to load structure " + name);
        }
        return block;
    }

    /** 1.20.2 StructureUtils.getStructureTemplate(name, level) (private) */
    private static net.minecraft.class_3499 structureTemplate(String name, net.minecraft.class_3218 level) {
        net.minecraft.class_3485 manager = level.method_14183();
        Optional<net.minecraft.class_3499> template = manager.method_15094(new net.minecraft.class_2960(name));
        if (template.isPresent()) {
            return template.get();
        }
        Path path = Paths.get(net.minecraft.class_4525.field_20579, name + ".snbt");
        net.minecraft.class_2487 tag = tryLoadStructure(path);
        if (tag == null) {
            throw new RuntimeException("Could not find structure file " + path
                + ", and the structure is not available in the world structures either.");
        }
        return manager.method_21891(tag);
    }

    /** 1.20.2 StructureUtils.tryLoadStructure(path) (private) */
    private static net.minecraft.class_2487 tryLoadStructure(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            return net.minecraft.class_2512.method_32260(org.apache.commons.io.IOUtils.toString(reader));
        } catch (IOException e) {
            return null;
        } catch (com.mojang.brigadier.exceptions.CommandSyntaxException e) {
            throw new RuntimeException("Error while trying to load structure " + path, e);
        }
    }

    /** 1.20.2 StructureBlockEntity.loadStructure(level, unlessResized): the named structure from the world's templates */
    private static boolean loadStructure(net.minecraft.class_2633 block, net.minecraft.class_3218 level, boolean unlessResized) {
        if (block.method_11374() != net.minecraft.class_2776.field_12697 || !block.method_11384()) {
            return false;
        }
        Optional<net.minecraft.class_3499> template;
        try {
            template = level.method_14183().method_15094(new net.minecraft.class_2960(block.method_11362()));
        } catch (net.minecraft.class_151 e) {
            return false;
        }
        return template.isPresent() && loadStructure(block, level, unlessResized, template.get());
    }

    /**
     * 1.20.2 StructureBlockEntity.loadStructure(level, unlessResized, template): the template's author
     * and size taken over (a size change re-sent to clients), then -- unless asked to stop at a size
     * change and it changed -- the template placed at the block's offset with its mirror, rotation,
     * entity flag and integrity.
     */
    private static boolean loadStructure(net.minecraft.class_2633 block, net.minecraft.class_3218 level, boolean unlessResized,
            net.minecraft.class_3499 template) {
        net.minecraft.class_2338 pos = block.method_11016();
        if (!net.minecraft.class_3544.method_15438(template.method_15181())) {
            ((StructureAuthor) block).cb$setAuthor(template.method_15181());
        }
        net.minecraft.class_2382 size = template.method_15160();
        boolean sameSize = block.method_11349().equals(size);
        if (!sameSize) {
            block.method_11377(size);
            block.method_5431();
            net.minecraft.class_2680 state = level.method_8320(pos);
            level.method_8413(pos, state, state, 3);
        }
        if (unlessResized && !sameSize) {
            return false;
        }
        net.minecraft.class_3492 settings = new net.minecraft.class_3492().method_15125(block.method_11345())
            .method_15123(block.method_11353()).method_15133(block.method_11367());
        if (block.method_11346() < 1.0f) {
            settings.method_16183().method_16184(new net.minecraft.class_3488(net.minecraft.class_3532.method_15363(block.method_11346(), 0.0f, 1.0f)))
                .method_15112(net.minecraft.class_2633.method_20048(block.method_11371()));
        }
        net.minecraft.class_2338 origin = pos.method_10081(block.method_11359());
        template.method_15172(level, origin, origin, settings, net.minecraft.class_2633.method_20048(block.method_11371()), 2);
        return true;
    }

    private CbGameTest() {
    }
}
