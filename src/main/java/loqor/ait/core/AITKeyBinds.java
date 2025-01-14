package loqor.ait.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.lwjgl.glfw.GLFW;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;

import loqor.ait.client.util.ClientTardisUtil;
import loqor.ait.core.bind.KeyBind;
import loqor.ait.core.item.KeyItem;
import loqor.ait.core.tardis.Tardis;

public class AITKeyBinds {

    private static final List<KeyBind> BINDS = new ArrayList<>();

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            for (KeyBind bind : BINDS)
                bind.tick(client);
        });

        register(new KeyBind.Held("snap", "main", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, client -> {
            ClientPlayerEntity player = client.player;

            if (player == null)
                return;

            Collection<ItemStack> keys = KeyItem.getKeysInInventory(player);

            for (ItemStack stack : keys) {
                if (stack.getItem() instanceof KeyItem key && key.hasProtocol(KeyItem.Protocols.SNAP)) {
                    Tardis tardis = KeyItem.getTardis(player.getWorld(), stack);

                    if (tardis == null)
                        return;

                    ClientTardisUtil.snapToOpenDoors(tardis);
                }
            }
        }));
    }

    private static void register(KeyBind bind) {
        bind.register();
        BINDS.add(bind);
    }
}
