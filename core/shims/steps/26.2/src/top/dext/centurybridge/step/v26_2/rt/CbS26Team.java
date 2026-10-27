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

    /**
     * DisplaySlot.teamColorToSlot (26.1.2, gone in 26.2): the sixteen colours answer their TEAM_ slot, a format or
     * RESET answers null; a null argument threw (26.1.2 switched on its ordinal). 26.2 keeps the slot on the
     * TeamColor of the same name (TeamColor.displaySlot()).
     */
    public static net.minecraft.world.scores.DisplaySlot teamColorToSlot(ChatFormatting f) {
        f.ordinal();
        return toTeamColor(f).map(TeamColor::displaySlot).orElse(null);
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

    /**
     * 26.1.2 Team.getColor() (abstract) on a Team-typed receiver: a PlayerTeam answers as above (a kept format, else
     * its TeamColor's formatting); any other Team answers its 26.2 getColor() as the ChatFormatting of the same name,
     * RESET for none (26.1.2's PlayerTeam default colour). A 26.1.2 Team subclass's own getColor() answers 26.2's
     * through the overrideHooks unwrap, so a colour comes back here unchanged; a format it answers (BOLD ...) has no
     * TeamColor and reads back as RESET.
     */
    public static ChatFormatting getColor(net.minecraft.world.scores.Team team) {
        if (team instanceof PlayerTeam playerTeam) {
            return getColor(playerTeam);
        }
        return toFormatting(team.getColor());
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

    // ---- ClientboundSetPlayerTeamPacket.Parameters, a record on 26.2 ----

    /** 26.1.2 new Parameters(buf): the parameters read from the buffer, 26.2's STREAM_CODEC (the same wire format) */
    public static net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters cbNewParameters(
            net.minecraft.network.RegistryFriendlyByteBuf buf) {
        return net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters.STREAM_CODEC.decode(buf);
    }

    /** 26.1.2 Parameters.write(buf) */
    public static void writeParameters(net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters p,
            net.minecraft.network.RegistryFriendlyByteBuf buf) {
        net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters.STREAM_CODEC.encode(buf, p);
    }

    /** 26.1.2 Parameters.getOptions(): the option bits as an int (26.1.2 read the same byte) */
    public static int parametersOptions(net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters p) {
        return p.options();
    }

    /** 26.1.2 Parameters.getColor(): the colour, RESET for none (26.1.2's packet carried RESET for a team without one) */
    public static ChatFormatting parametersColor(net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters p) {
        return toFormatting(p.color());
    }

    /** 26.1.2 Parameters.getNametagVisibility() */
    public static net.minecraft.world.scores.Team.Visibility parametersNametagVisibility(
            net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket.Parameters p) {
        return p.nameTagVisibility();
    }

    // ---- ColorArgument (26.2: TeamColorArgument, answering TeamColor) ----

    /** 26.1.2's ColorArgument.getColor: the parsed argument (26.2's parser hands a TeamColor) as its ChatFormatting */
    public static ChatFormatting getColor(com.mojang.brigadier.context.CommandContext<?> context, String name) {
        Object v = context.getArgument(name, Object.class);
        return v instanceof TeamColor c ? ChatFormatting.valueOf(c.name()) : (ChatFormatting) v;
    }

    /** 26.1.2's ColorArgument.parse: any ChatFormatting that is not a format (the colours and RESET) */
    public static ChatFormatting parse(net.minecraft.commands.arguments.TeamColorArgument argument, com.mojang.brigadier.StringReader reader)
            throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        String s = reader.readUnquotedString();
        ChatFormatting f = CbS26ChatFormatting.getByName(s);
        if (f != null && !CbS26ChatFormatting.isFormat(f)) {
            return f;
        }
        throw net.minecraft.commands.arguments.TeamColorArgument.ERROR_INVALID_VALUE.createWithContext(reader, s);
    }

    /**
     * 26.1.2 ReadOnlyScoreInfo.safeFormatValue(info, format), dropped by 26.2 with the same parts kept: the score's
     * formatValue(format) when there is a score, format.format(0) when there is none (javap of 26.1.2).
     */
    public static MutableComponent safeFormatValue(net.minecraft.world.scores.ReadOnlyScoreInfo info,
            net.minecraft.network.chat.numbers.NumberFormat format) {
        return info != null ? info.formatValue(format) : format.format(0);
    }
}
