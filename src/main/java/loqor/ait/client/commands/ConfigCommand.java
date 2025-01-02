package loqor.ait.client.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import dev.pavatus.config.AITConfig;
import loqor.ait.AITMod;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;


public class ConfigCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal(AITMod.MOD_ID + "-config").executes(context -> {
            MinecraftClient client = MinecraftClient.getInstance();

            Screen screen = AutoConfig.getConfigScreen(AITConfig.class, client.currentScreen).get();
            client.send(() -> client.setScreen(screen));
            return Command.SINGLE_SUCCESS;
        }));
    }
}
