package top.dext.centurybridge.shims.v1_20_2;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * DimensionDataStorage.readTagFromDisk(id, dataVersion), 1.20.1's shape, back on 1.20.2's class
 * (which takes a DataFixTypes as well). The 1.20.1 body is ported: the file read as compressed or
 * plain NBT exactly as both releases read it. 1.20.1 then ran the result through the one
 * DataFixTypes.SAVED_DATA ("saved_data") of its data fixer; 1.20.2 has no such type -- its fixer
 * knows saved data only per vanilla file (raids, map data, scoreboard...) and has no fix for any
 * other file -- so the tag is returned as read, which is what passing no fix type means in 1.20.2
 * (Fabric API's PersistentStateManagerMixin answers a null type the same way).
 */
@Mixin(net.minecraft.class_26.class)
public abstract class CbSavedDataReadCover {

    @Shadow
    private File method_17922(String id) {
        throw new AssertionError();
    }

    @Shadow
    private boolean method_17921(PushbackInputStream in) throws IOException {
        throw new AssertionError();
    }

    public net.minecraft.class_2487 method_17923(String id, int dataVersion) throws IOException {
        File file = this.method_17922(id);
        try (FileInputStream fileIn = new FileInputStream(file);
             PushbackInputStream in = new PushbackInputStream(fileIn, 2)) {
            if (this.method_17921(in)) {
                return net.minecraft.class_2507.method_10629(in);
            }
            try (DataInputStream data = new DataInputStream(in)) {
                return net.minecraft.class_2507.method_10627(data);
            }
        }
    }
}
