package top.dext.centurybridge.step.v1_21_2.rt;

import java.io.File;
import java.nio.file.Path;

/**
 * DimensionDataStorage (class_26) at 1.21.2: the data directory and its files are Paths. 1.21.1's private
 * getDataFile(id) answered {@code new File(dataFolder, id + ".dat")}; 1.21.2's getDataFile(id) answers
 * {@code dataFolder.resolve(id + ".dat")} (javap of both), the same file. A 1.21.1 mixin's shadow / invoker of it reads
 * it back as the File it expects (s212.json callReshapes).
 */
public final class CbLevelSavedData {

    private CbLevelSavedData() {
    }

    /** 1.21.2's getDataFile answer as 1.21.1's File */
    public static File file(Path path) {
        return path == null ? null : path.toFile();
    }
}
