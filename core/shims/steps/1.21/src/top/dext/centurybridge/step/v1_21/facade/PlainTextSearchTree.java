package top.dext.centurybridge.step.v1_21.facade;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

/**
 * 1.20.6's PlainTextSearchTree (class_7454, gone in 1.21, whose SearchTree.plainText builds the same suffix
 * array): search(text) over the strings each element gives; empty(); create(contents, filler).
 */
@Environment(EnvType.CLIENT)
@FunctionalInterface
public interface PlainTextSearchTree<T> {

    /** search(text) */
    List<T> search(String text);

    /** empty(): finds nothing */
    static <T> PlainTextSearchTree<T> method_43793() {
        return text -> List.of();
    }

    /** create(contents, filler): 1.21's SearchTree.plainText, the same suffix array over the filler's strings */
    static <T> PlainTextSearchTree<T> method_43796(List<T> contents, Function<T, Stream<String>> filler) {
        net.minecraft.class_1129<T> tree = net.minecraft.class_1129.plainText(contents, filler);
        return tree::method_4810;
    }
}
