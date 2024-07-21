package loqor.ait.client.renderers.monitors;

import loqor.ait.AITMod;
import loqor.ait.client.models.decoration.PlaqueModel;
import loqor.ait.core.blockentities.WallMonitorBlockEntity;
import loqor.ait.core.blocks.PlaqueBlock;
import loqor.ait.core.data.DirectedGlobalPos;
import loqor.ait.core.util.WorldUtil;
import loqor.ait.tardis.Tardis;
import loqor.ait.tardis.control.impl.DirectionControl;
import loqor.ait.tardis.data.FuelData;
import loqor.ait.tardis.data.travel.TravelHandler;
import loqor.ait.tardis.data.travel.TravelHandlerBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class WallMonitorRenderer<T extends WallMonitorBlockEntity> implements BlockEntityRenderer<T> {

    public static final Identifier PLAQUE_TEXTURE = new Identifier(AITMod.MOD_ID, ("textures/blockentities/monitors/wall_monitor.png"));
    private final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
    private final PlaqueModel plaqueModel;

    public WallMonitorRenderer(BlockEntityRendererFactory.Context ctx) {
        this.plaqueModel = new PlaqueModel(PlaqueModel.getTexturedModelData().createModel());
    }

    @Override
    public void render(WallMonitorBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        BlockState blockState = entity.getCachedState();

        Direction k = blockState.get(PlaqueBlock.FACING);

        matrices.push();
        matrices.translate(0.5f, 1.5f, 0.5f);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(k.asRotation()));
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));

        this.plaqueModel.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(PLAQUE_TEXTURE)), light, overlay, 1.0F, 1.0F, 1.0F, 1.0F);
        matrices.pop();

        if (entity.tardis().isEmpty())
            return;

        Tardis tardis = entity.tardis().get();

        if (!tardis.engine().hasPower())
            return;

        matrices.push();
        matrices.translate(0.5, 0.75, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(k.asRotation()));
        matrices.scale(0.01f, 0.01f, 0.01f);
        float xVal = 0f;
        matrices.translate(xVal, -35f, 35f);

        TravelHandler travel = tardis.travel();
        DirectedGlobalPos.Cached abpp = travel.getState() != TravelHandlerBase.State.LANDED
                ? travel.getProgress() : travel.position();

        BlockPos abppPos = abpp.getPos();

        DirectedGlobalPos.Cached abpd = tardis.travel().destination();
        BlockPos abpdPos = abpd.getPos();

        String positionPosText = abppPos.getX() + ", " + abppPos.getY() + ", " + abppPos.getZ();
        Text positionDimensionText = WorldUtil.worldText(abpp.getDimension());

        String fuelText = Math.round((tardis.getFuel() / FuelData.TARDIS_MAX_FUEL) * 100) + "%";

        String destinationPosText = abpdPos.getX() + ", " + abpdPos.getY() + ", " + abpdPos.getZ();
        Text destinationDimensionText = WorldUtil.worldText(abpd.getDimension());

        float v = -20f;

        String arrow = DirectionControl.rotationForArrow(abpp.getRotation());
        String arrow2 = DirectionControl.rotationForArrow(abpd.getRotation());

        this.textRenderer.drawWithOutline(Text.of(positionPosText).asOrderedText(), (v - xVal) - ((float) this.textRenderer.getWidth(positionPosText) / 2), 35, 0xFFFFFF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(positionDimensionText.asOrderedText(), (v - xVal) - ((float) this.textRenderer.getWidth(positionDimensionText) / 2), 46, 0xFFFFFF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(arrow).asOrderedText(), (18 - xVal) - ((float) this.textRenderer.getWidth(arrow) / 2), 42, 0xFFFFFF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of("-===========-").asOrderedText(), (v - xVal) - ((float) this.textRenderer.getWidth("-===========-") / 2), 55, 0x00F0FF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(destinationPosText).asOrderedText(), (v - xVal) - ((float) this.textRenderer.getWidth(destinationPosText) / 2), 67, 0xFFFFFF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(destinationDimensionText.asOrderedText(), (v - xVal) - ((float) this.textRenderer.getWidth(positionDimensionText) / 2), 78, 0xFFFFFF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(arrow2).asOrderedText(), (18 - xVal) - ((float) this.textRenderer.getWidth(arrow2) / 2), 75, 0xFFFFFF,0x000000, matrices.peek().getPositionMatrix(),vertexConsumers, 0xF000F0);

        this.textRenderer.drawWithOutline(Text.of("⛽").asOrderedText(), (53 - xVal) - ((float) this.textRenderer.getWidth("⛽") / 2), 40, 0xFAF000, 0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(fuelText).asOrderedText(), (53 - xVal) - ((float) this.textRenderer.getWidth(fuelText) / 2), 48, 0xFFFFFF, 0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        String flightTimeText = travel.getState() == TravelHandlerBase.State.LANDED ? "0%" : tardis.travel().getDurationAsPercentage() + "%";

        this.textRenderer.drawWithOutline(Text.of("⏳").asOrderedText(), (53 - xVal) - ((float) this.textRenderer.getWidth("⏳") / 2), 60, 0x00FF0F, 0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);
        this.textRenderer.drawWithOutline(Text.of(flightTimeText).asOrderedText(), (53 - xVal) - ((float) this.textRenderer.getWidth(flightTimeText) / 2), 68, 0xFFFFFF, 0x000000, matrices.peek().getPositionMatrix(), vertexConsumers, 0xF000F0);

        matrices.pop();
    }
}