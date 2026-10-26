package top.dext.centurybridge.rt.v1_20_2;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import net.minecraft.class_2461;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_2966;
import net.minecraft.class_3471;
import net.minecraft.class_3843;
import net.minecraft.class_4525;
import net.minecraft.class_5819;
import net.minecraft.class_7403;
import org.slf4j.Logger;

/**
 * 1.20.1's developer entry points that 1.20.2 removed, with their 1.20.1 bodies (s1misc).
 */
public final class CbDevTools {

    private static final Logger LOGGER = com.mojang.logging.LogUtils.getLogger();

    /**
     * StructureUtils.main(args): bootstraps the game and upgrades every .snbt test structure under
     * the test-structure directory in place (read, update to the current data version, write back).
     */
    public static void method_36106(String[] args) throws IOException {
        class_2966.method_12851();
        try (Stream<Path> files = Files.walk(Paths.get(class_4525.field_20579))) {
            files.filter(p -> p.toString().endsWith(".snbt")).forEach(p -> {
                try {
                    String snbt = Files.readString(p);
                    class_2487 tag = class_2512.method_32260(snbt);
                    class_2487 updated = class_3843.method_32235(p.toString(), tag);
                    class_2461.method_32234(class_7403.field_39439, p, class_2512.method_32271(updated));
                } catch (com.mojang.brigadier.exceptions.CommandSyntaxException | IOException e) {
                    LOGGER.error("Something went wrong upgrading: {}", p, e);
                }
            });
        }
    }

    /**
     * WoodlandMansionPieces.main(args): prints a random seed and the floor plans a mansion grid
     * generates from it. 1.20.2's MansionGrid is package-private and lost its print(); the grid
     * is built and read here as 1.20.1's print() read it (base grid, then third floor; one
     * character per cell).
     */
    public static void method_35471(String[] args) {
        class_5819 random = class_5819.method_43047();
        long seed = random.method_43055();
        System.out.println("Seed: " + seed);
        random.method_43052(seed);
        // MansionGrid / SimpleGrid and the members read here are opened by the layer's access widener and
        // named in bytecode (a reflective name string would not follow the class as the layer climbs)
        {
            class_3471.class_3474 mansion = new class_3471.class_3474(random);
            for (int floor = 0; floor < 2; floor++) {
                class_3471.class_3478 g = floor == 0 ? mansion.field_15440 : mansion.field_15439;
                int rows = g.field_15453;
                int columns = g.field_15454;
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < columns; x++) {
                        int cell = g.method_15066(x, y);
                        System.out.print(switch (cell) {
                            case 1 -> "+";
                            case 4 -> "x";
                            case 2 -> "X";
                            case 3 -> "O";
                            case 5 -> "#";
                            default -> " ";
                        });
                    }
                    System.out.println("");
                }
                System.out.println("");
            }
        }
    }

    private CbDevTools() {
    }
}
