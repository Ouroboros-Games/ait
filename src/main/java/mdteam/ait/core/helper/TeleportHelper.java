package mdteam.ait.core.helper;

import com.mojang.logging.LogUtils;
import mdteam.ait.AITMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.UUID;

public class TeleportHelper {
    public final UUID entityUUID;
    public final AbsoluteBlockPos destination;

    public TeleportHelper(UUID uuid, AbsoluteBlockPos destination) {
        this.entityUUID = uuid;
        this.destination = destination;
    }

    public TeleportHelper(UUID uuid, World level, Vec3i destination) {
        this.entityUUID = uuid;
        this.destination = new AbsoluteBlockPos(new BlockPos(destination), level);
    }

    public void teleport(ServerWorld origin) {
        Entity entity = origin.getEntity(entityUUID);
        destination.getDimension().getWorldChunk(destination.toBlockPos());

        if (entity instanceof ServerPlayerEntity player) {
            player.teleport((ServerWorld) destination.getDimension(), destination.getX() + 0.5, destination.getY(), destination.getZ() + 0.5,destination.getDirection().asRotation(), entity.getPitch());
        }
        else if (!(entity instanceof PlayerEntity)) {
            entity.moveToWorld((ServerWorld) destination.getDimension());
        }
        AITMod.LOGGER.info("Teleported " + entity + " to " + destination + " with rotation " +  destination.getDirection());
    }
}
