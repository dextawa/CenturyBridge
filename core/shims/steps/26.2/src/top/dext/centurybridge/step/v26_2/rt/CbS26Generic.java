package top.dext.centurybridge.step.v26_2.rt;

import com.mojang.serialization.Codec;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.scores.TeamColor;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

/**
 * Same-descriptor values whose type arguments 26.2 changed (generic drift), each converted where a 26.1.2 mod hands
 * it in or takes it out; every converter tests what it is given and passes anything else through, so the two
 * directions undo each other.
 */
public final class CbS26Generic {
    private CbS26Generic() {}

    // ---- PlayerTeam.Packed.color: Optional<ChatFormatting> (26.1.2) / Optional<TeamColor> (26.2) ----

    /** in: a 26.1.2 Optional<ChatFormatting> as 26.2's Optional<TeamColor> (a format or RESET is no colour) */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional toTeamColors(Optional o) {
        if (o == null || o.isEmpty() || !(o.get() instanceof ChatFormatting f)) {
            return o;
        }
        return CbS26Team.toTeamColor(f);
    }

    /** out: 26.2's Optional<TeamColor> as 26.1.2's Optional<ChatFormatting> */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Optional toFormattings(Optional o) {
        if (o == null || o.isEmpty() || !(o.get() instanceof TeamColor c)) {
            return o;
        }
        return Optional.of(ChatFormatting.valueOf(c.name()));
    }

    // ---- LayeringTransform: Consumer<Matrix4fStack> (26.1.2) / Consumer<Matrix4f> (26.2) ----

    /**
     * in: a 26.1.2 modifier, written against the model-view Matrix4fStack, as 26.2's modifier of a Matrix4f: it runs on
     * a stack holding the matrix (one level, as 26.1.2's RenderType setup applied it), whose result is written back
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Consumer toMatrixModifier(Consumer modifier) {
        if (modifier == null || modifier instanceof StackModifier) {
            return modifier;
        }
        return new StackModifier(modifier);
    }

    /**
     * out: 26.2's modifier as 26.1.2's modifier of a Matrix4fStack: a stack is a Matrix4f, so 26.2's modifier takes it
     * as it is; one this layer wrapped hands back the 26.1.2 modifier it wraps
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Consumer toStackModifier(Consumer modifier) {
        return modifier instanceof StackModifier s ? s.modifier() : modifier;
    }

    private record StackModifier(Consumer<Matrix4fStack> modifier) implements Consumer<Matrix4f> {
        @Override
        public void accept(Matrix4f m) {
            if (m instanceof Matrix4fStack s) {
                modifier.accept(s);
                return;
            }
            Matrix4fStack s = new Matrix4fStack(2);
            s.set(m);
            modifier.accept(s);
            m.set(s);
        }
    }

    // ---- ProfileCollector.getChartedPaths: Set<apache Pair> (26.1.2) / Set<DataFixerUpper Pair> (26.2) ----

    /** out: 26.2's set of com.mojang.datafixers.util.Pair as 26.1.2's set of org.apache.commons.lang3.tuple.Pair */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set toApachePairs(Set set) {
        if (set == null || set.isEmpty() || !(set.iterator().next() instanceof com.mojang.datafixers.util.Pair)) {
            return set;
        }
        Set out = new LinkedHashSet();
        for (Object o : set) {
            out.add(o instanceof com.mojang.datafixers.util.Pair p ? org.apache.commons.lang3.tuple.Pair.of(p.getFirst(), p.getSecond()) : o);
        }
        return out;
    }

    /** in: a 26.1.2 set of apache Pairs (a 26.1.2 collector's answer) as 26.2's set of DataFixerUpper Pairs */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Set toDfuPairs(Set set) {
        if (set == null || set.isEmpty() || !(set.iterator().next() instanceof org.apache.commons.lang3.tuple.Pair)) {
            return set;
        }
        Set out = new LinkedHashSet();
        for (Object o : set) {
            out.add(o instanceof org.apache.commons.lang3.tuple.Pair p ? com.mojang.datafixers.util.Pair.of(p.getLeft(), p.getRight()) : o);
        }
        return out;
    }

    // ---- DensityFunction codecs ----

    /** 26.1.2 DensityFunction.CODEC: RegistryFileCodec.create(DENSITY_FUNCTION, DensityFunctions.DIRECT_CODEC), as 26.1.2 built it */
    private static final Codec<Holder<DensityFunction>> DENSITY_HOLDER_CODEC =
        RegistryFileCodec.create(Registries.DENSITY_FUNCTION, DensityFunctions.DIRECT_CODEC);

    public static Codec<Holder<DensityFunction>> densityFunctionCodec() {
        return DENSITY_HOLDER_CODEC;
    }

    /** a value 26.1.2 wrapped in Optional.of itself where 26.2 takes the Optional (PickedUpItemTrigger) */
    public static Optional<Object> some(Object value) {
        return Optional.of(value);
    }
}
