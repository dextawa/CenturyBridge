package top.dext.centurybridge.step.v26_2.rt;

import java.util.Optional;
import java.util.WeakHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.TeamColor;

/**
 * 26.1.2's PlayerTeam colour and option shapes on 26.2 (javap of both).
 *
 * <ul>
 * <li>packOptions / unpackOptions: an int in 26.1.2, a byte in 26.2; the same two bits (friendly fire 1, see
 *     friendly invisibles 2), so the value converts as it is.</li>
 * <li>The colour: 26.1.2 kept any ChatFormatting (RESET when unset); 26.2 keeps an Optional of TeamColor, the
 *     sixteen colours under the same names. RESET is the empty Optional; a colour is the TeamColor of its name.
 *     26.1.2 also accepted a format (BOLD, ITALIC, ...) as a team colour and styled the team's names with it: 26.2
 *     cannot hold one, so it is kept here per team, answered by getColor and applied where 26.2 colours the
 *     team's names (CbS26TeamColorMixin, PlayerTeam.applyColor), the team's TeamColor left empty. 26.2's team
 *     packet carries a TeamColor only, so clients see such a team uncoloured (verified: ClientboundSetPlayerTeamPacket
 *     $Parameters in 26.2 is (displayName, prefix, suffix, visibility, collision, Optional of TeamColor, options)).</li>
 * </ul>
 */
public final class CbS26Team {
    private CbS26Team() {}

    private static final WeakHashMap<PlayerTeam, ChatFormatting> FORMAT = new WeakHashMap<>();

    public static int packOptions(PlayerTeam team) {
        return team.packOptions();
    }

    public static void unpackOptions(PlayerTeam team, int options) {
        team.unpackOptions((byte) options);
    }

    /** hook unwrap: a 26.1.2 override's packOptions() int answering 26.2's byte shape (bits 0 and 1) */
    public static byte optionsByte(int options) {
        return (byte) options;
    }

    /** hook wrap: 26.2's byte options handed to a 26.1.2 override of unpackOptions(int) */
    public static int optionsInt(byte options) {
        return options;
    }

    public static Optional<TeamColor> toTeamColor(ChatFormatting f) {
        if (f == null || f == ChatFormatting.RESET || CbS26ChatFormatting.isFormat(f)) {
            return Optional.empty();
        }
        return Optional.of(TeamColor.valueOf(f.name()));
    }

    public static ChatFormatting toFormatting(Optional<TeamColor> c) {
        return c.map(t -> ChatFormatting.valueOf(t.name())).orElse(ChatFormatting.RESET);
    }

    public static void setColor(PlayerTeam team, ChatFormatting f) {
        synchronized (FORMAT) {
            if (f != null && CbS26ChatFormatting.isFormat(f)) {
                FORMAT.put(team, f);
            } else {
                FORMAT.remove(team);
            }
        }
        team.setColor(toTeamColor(f));
    }

    public static ChatFormatting getColor(PlayerTeam team) {
        ChatFormatting f;
        synchronized (FORMAT) {
            f = FORMAT.get(team);
        }
        return f != null ? f : toFormatting(team.getColor());
    }

    /** hook wrap: 26.2's setColor(Optional) handed to a 26.1.2 override of setColor(ChatFormatting) */
    public static ChatFormatting wrapColor(Optional<TeamColor> c) {
        return toFormatting(c);
    }

    /** hook unwrap: a 26.1.2 override of getColor() answering 26.2's getColor() */
    public static Optional<TeamColor> unwrapColor(ChatFormatting f) {
        return toTeamColor(f);
    }

    /** CbS26TeamColorMixin: 26.1.2 styled a team's names with a format kept as its colour */
    public static MutableComponent applyFormat(PlayerTeam team, MutableComponent text) {
        ChatFormatting f;
        synchronized (FORMAT) {
            f = FORMAT.get(team);
        }
        return f == null ? text : text.withStyle(f);
    }
}
