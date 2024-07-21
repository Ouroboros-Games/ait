package loqor.ait.tardis.data;

import loqor.ait.core.entities.RealTardisEntity;
import loqor.ait.tardis.base.KeyedTardisComponent;
import loqor.ait.tardis.data.properties.v2.bool.BoolProperty;
import loqor.ait.tardis.data.properties.v2.bool.BoolValue;
import loqor.ait.tardis.util.TardisUtil;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class RealFlightHandler extends KeyedTardisComponent {

    private static final BoolProperty ACTIVE = new BoolProperty("active");

    private final BoolValue active = ACTIVE.create(this);

    public RealFlightHandler() {
        super(Id.FLIGHT);
    }

    @Override
    public void onLoaded() {
        active.of(this, ACTIVE);
    }

    public boolean isActive() {
        return active.get();
    }

    public void enterFlight(ServerPlayerEntity player) {
        PlayerAbilities abilities = player.getAbilities();
        abilities.invulnerable = true;
        abilities.allowFlying = true;
        abilities.flying = true;

        player.sendAbilitiesUpdate();
        player.setInvisible(true);

        TardisUtil.teleportOutside(this.tardis, player);
        RealTardisEntity.create(player, tardis);

        this.tardis.travel().finishDemat();
        this.active.set(true);
    }

    public void land(PlayerEntity player) {
        PlayerAbilities abilities = player.getAbilities();

        abilities.invulnerable = player.isCreative();
        abilities.allowFlying = player.isCreative();
        abilities.flying = false;

        player.sendAbilitiesUpdate();
        player.setInvisible(false);

        this.tardis.travel().immediatelyLandHere(tardis.travel().position());
        this.tardis.travel().autopilot(false);

        this.active.set(false);
    }
}
