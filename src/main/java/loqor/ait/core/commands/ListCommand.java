package loqor.ait.core.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import loqor.ait.AITMod;
import loqor.ait.core.util.TextUtil;
import loqor.ait.tardis.wrapper.server.ServerTardis;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;

public class ListCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID)
                .then(literal("list").requires(source -> source.hasPermissionLevel(2)).executes(ListCommand::execute)));
    }

    public static int execute(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        source.sendMessage(Text.literal("TARDIS':"));

        ServerTardisManager.getInstance().forEach(tardis -> sendTardis(source, tardis));
        return Command.SINGLE_SUCCESS;
    }

    private static void sendTardis(ServerCommandSource source, ServerTardis tardis) {
        source.sendMessage(Text.literal("  - ").append(TextUtil.forTardis(tardis)));
    }
}