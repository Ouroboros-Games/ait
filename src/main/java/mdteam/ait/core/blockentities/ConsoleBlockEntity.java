package mdteam.ait.core.blockentities;

import mdteam.ait.api.tardis.ILinkable;
import mdteam.ait.client.renderers.consoles.ConsoleEnum;
import mdteam.ait.core.AITBlockEntityTypes;
import mdteam.ait.core.blocks.ConsoleBlock;
import mdteam.ait.core.helper.TardisUtil;
import mdteam.ait.data.AbsoluteBlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.AnimationState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import the.mdteam.ait.Tardis;
import the.mdteam.ait.TardisDesktop;
import the.mdteam.ait.TardisManager;
import the.mdteam.ait.TardisTravel;

import static the.mdteam.ait.TardisTravel.State.*;

public class ConsoleBlockEntity extends BlockEntity implements ILinkable {
    public final AnimationState ANIM_FLIGHT = new AnimationState();
    public int animationTimer = 0;

    private Tardis tardis;

    public ConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.DISPLAY_CONSOLE_BLOCK_ENTITY_TYPE, pos, state);

        this.setTardis(TardisUtil.findTardisByInterior(pos));
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        //this.getAnimation().setAlpha(nbt.getFloat("alpha"));

        if (this.tardis != null) {
            nbt.putUuid("tardis", this.tardis.getUuid());
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        //nbt.putFloat("alpha", this.getAlpha());

        if (nbt.contains("tardis")) {
            TardisManager.getInstance().link(nbt.getUuid("tardis"), this);
        }
    }

    @Override
    public Tardis getTardis() {
        return tardis;
    }

    public ConsoleEnum getConsole() {
        return this.tardis.getConsole().getType();
    }

    @Override
    public void setTardis(Tardis tardis) {
        this.tardis = tardis;

        // force re-link a desktop if it's not null
        this.linkDesktop();
    }

    public void useOn(ServerWorld world, boolean sneaking, PlayerEntity player) {

        if(player == null)
            return;

        if(world != TardisUtil.getTardisDimension())
            return;

        if(sneaking) {
            this.tardis.setLockedTardis(!this.tardis.getLockedTardis());
            String lockedState = this.tardis.getLockedTardis() ? "\uD83D\uDD12" : "\uD83D\uDD13";
            player.sendMessage(Text.literal(lockedState), true);
            world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_BREAK, SoundCategory.BLOCKS, 0.6F, 1F);
        } else if(this.tardis.getTravel().getState() == LANDED) {
            if (!this.tardis.getLockedTardis()) {
                DoorBlockEntity door = TardisUtil.getDoor(this.tardis);
                if(this.tardis.getTravel().getState() == LANDED)
                    if (door != null) {
                        //TardisUtil.getTardisDimension().getChunk(door.getPos()); // force load the chunk

                        door.setLeftDoorRot(door.getLeftDoorRotation() == 0 ? 1.2f : 0f);
                        //door.setRightDoorRot(0);
                    }
                world.playSound(null, pos, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundCategory.BLOCKS, 0.6f, 1f);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_STEP, SoundCategory.BLOCKS, 0.6F, 1F);
                player.sendMessage(Text.literal("\uD83D\uDD12"), true);
            }
        }
    }

    @Override
    public void setDesktop(TardisDesktop desktop) {
        if(this.getWorld() == null)
            return;
        if(this.getWorld() != TardisUtil.getTardisDimension())
            return;
        desktop.setConsolePos(new AbsoluteBlockPos.Directed(
                this.pos, TardisUtil.getTardisDimension(), this.getWorld().getBlockState(this.getPos()).get(ConsoleBlock.FACING))
        );
    }

    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState blockState, T entity) {
        ConsoleBlockEntity console = (ConsoleBlockEntity) entity;

        // idk
        if (world.isClient()) {
            console.checkAnimations();
        }
    }
    public void checkAnimations() {
        // DO NOT RUN THIS ON SERVER!!

        animationTimer++;
        TardisTravel.State state = this.getTardis().getTravel().getState();

        if (!ANIM_FLIGHT.isRunning()) {
            if (state == LANDED) {
                // stop all others and start this one, theres likely a better way to do this. fixme
                ANIM_FLIGHT.start(animationTimer);
            } else if (state == DEMAT) {
                ANIM_FLIGHT.start(animationTimer);
            } else if (state == FLIGHT) {
                ANIM_FLIGHT.start(animationTimer);
            } else if (state == MAT) {
                ANIM_FLIGHT.start(animationTimer);
            }
        }
    }
    private void stopAllAnimations() {
        // DO NOT RUN ON SERVER
        ANIM_FLIGHT.stop();
    }

    public void onBroken() {}
}
