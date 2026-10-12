package top.dext.centurybridge.rt.v1_20_2;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;
import net.minecraft.class_2461;
import net.minecraft.class_2487;
import net.minecraft.class_2512;
import net.minecraft.class_2966;
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
        try {
            Class<?> gridClass = Class.forName("net.minecraft.class_3471$class_3474");
            Class<?> simpleGridClass = Class.forName("net.minecraft.class_3471$class_3478");
            MethodHandles.Lookup grid = MethodHandles.privateLookupIn(gridClass, MethodHandles.lookup());
            MethodHandles.Lookup simple = MethodHandles.privateLookupIn(simpleGridClass, MethodHandles.lookup());
            Object mansion = grid.findConstructor(gridClass, MethodType.methodType(void.class, class_5819.class)).invoke(random);
            MethodHandle base = grid.findGetter(gridClass, "field_15440", simpleGridClass);
            MethodHandle third = grid.findGetter(gridClass, "field_15439", simpleGridClass);
            MethodHandle rowsOf = simple.findGetter(simpleGridClass, "field_15453", int.class);
            MethodHandle columnsOf = simple.findGetter(simpleGridClass, "field_15454", int.class);
            MethodHandle get = simple.findVirtual(simpleGridClass, "method_15066", MethodType.methodType(int.class, int.class, int.class));
            for (int floor = 0; floor < 2; floor++) {
                Object g = floor == 0 ? base.invoke(mansion) : third.invoke(mansion);
                int rows = (int) rowsOf.invoke(g);
                int columns = (int) columnsOf.invoke(g);
                for (int y = 0; y < rows; y++) {
                    for (int x = 0; x < columns; x++) {
                        int cell = (int) get.invoke(g, x, y);
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
        } catch (Throwable t) {
            throw CbSuper.rethrow(t);
        }
    }

    private CbDevTools() {
    }
}
