package top.dext.centurybridge.step.v1_20_3.rt;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.class_267;

/**
 * 1.20.2's OperationArgument.Operation, apply(target Score, source Score): the converter builds a 1.20.2 lambda
 * or method reference as this interface (same SAM name and descriptor) and hands it to
 * CbWorldScores.operation for the 1.20.3 Operation, apply(ScoreAccess, ScoreAccess).
 */
@FunctionalInterface
public interface CbWorldScoreOperation {
    void apply(class_267 target, class_267 source) throws CommandSyntaxException;
}
