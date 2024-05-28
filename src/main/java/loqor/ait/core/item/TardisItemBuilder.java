package loqor.ait.core.item;

import loqor.ait.AITMod;
import loqor.ait.core.blockentities.ConsoleBlockEntity;
import loqor.ait.core.data.AbsoluteBlockPos;
import loqor.ait.core.data.schema.exterior.ExteriorCategorySchema;
import loqor.ait.registry.impl.CategoryRegistry;
import loqor.ait.registry.impl.DesktopRegistry;
import loqor.ait.registry.impl.exterior.ExteriorVariantRegistry;
import loqor.ait.tardis.manager.TardisBuilder;
import loqor.ait.tardis.TardisTravel;
import loqor.ait.tardis.base.TardisComponent;
import loqor.ait.tardis.control.impl.DirectionControl;
import loqor.ait.tardis.data.StatsData;
import loqor.ait.tardis.exterior.category.CapsuleCategory;
import loqor.ait.tardis.wrapper.server.manager.ServerTardisManager;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;

public class TardisItemBuilder extends Item {

	public static final Identifier DEFAULT_INTERIOR = new Identifier(AITMod.MOD_ID, "meridian");
	public static final Identifier DEFAULT_EXTERIOR = CapsuleCategory.REFERENCE;

	private final Identifier exterior;
	private final Identifier desktop;

	public TardisItemBuilder(Settings settings, Identifier exterior, Identifier desktopId) {
		super(settings);

		this.exterior = exterior;
		this.desktop = desktopId;
	}

	public TardisItemBuilder(Settings settings, Identifier exterior) {
		this(settings, exterior, DEFAULT_INTERIOR);
	}

	public TardisItemBuilder(Settings settings) {
		this(settings, DEFAULT_EXTERIOR);
	}

	@Override
	public ActionResult useOnBlock(ItemUsageContext context) {
		World world = context.getWorld();
		PlayerEntity player = context.getPlayer();

		if (world.isClient() || player == null)
			return ActionResult.PASS;

		if (context.getHand() != Hand.MAIN_HAND)
			return ActionResult.SUCCESS;

		AbsoluteBlockPos.Directed pos = new AbsoluteBlockPos.Directed(context.getBlockPos().up(), world, DirectionControl.getGeneralizedRotation(RotationPropertyHelper.fromYaw(player.getBodyYaw())));
		BlockEntity entity = world.getBlockEntity(context.getBlockPos());

		if (entity instanceof ConsoleBlockEntity consoleBlock) {
			if (consoleBlock.findTardis().isEmpty())
				return ActionResult.FAIL;

			TardisTravel.State state = consoleBlock.findTardis().get().travel().getState();

			if (!(state == TardisTravel.State.LANDED || state == TardisTravel.State.FLIGHT))
				return ActionResult.PASS;

			consoleBlock.killControls();
			world.removeBlock(context.getBlockPos(), false);
			world.removeBlockEntity(context.getBlockPos());
			return ActionResult.SUCCESS;
		}

		ExteriorCategorySchema category = CategoryRegistry.getInstance().get(this.exterior);

		ServerTardisManager.getInstance().create(
				new TardisBuilder()
						.at(pos).desktop(DesktopRegistry.getInstance().get(this.desktop))
						.exterior(ExteriorVariantRegistry.getInstance().pickRandomWithParent(category))
						.<StatsData>with(TardisComponent.Id.STATS, stats -> {
							stats.setPlayerCreatorName(player.getName().getString());
							stats.markPlayerCreatorName();
						})
		);

		context.getStack().decrement(1);

		return ActionResult.SUCCESS;
	}
}