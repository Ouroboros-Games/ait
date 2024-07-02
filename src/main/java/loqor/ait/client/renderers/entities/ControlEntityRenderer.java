package loqor.ait.client.renderers.entities;

import loqor.ait.AITMod;
import loqor.ait.client.models.consoles.ControlModel;
import loqor.ait.core.entities.ConsoleControlEntity;
import loqor.ait.core.item.SonicItem;
import loqor.ait.tardis.control.impl.RefuelerControl;
import loqor.ait.tardis.data.FuelData;
import loqor.ait.tardis.data.SonicHandler;
import loqor.ait.tardis.data.loyalty.Loyalty;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

@Environment(value = EnvType.CLIENT)
public class ControlEntityRenderer
		extends LivingEntityRenderer<ConsoleControlEntity, ControlModel> {

	private static final Identifier TEXTURE = new Identifier(AITMod.MOD_ID, "textures/entity/control/sequenced.png");

	ControlModel model = new ControlModel(ControlModel.getTexturedModelData().createModel());

	public ControlEntityRenderer(EntityRendererFactory.Context context) {
		super(context, new ControlModel(ControlModel.getNotModelData().createModel()), 0f);
	}

	@Override
	public void render(ConsoleControlEntity livingEntity, float yaw, float tickDelta, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light) {
		super.render(livingEntity, yaw, tickDelta, matrixStack, vertexConsumerProvider, light);

		if (isPlayerHoldingScanningSonic()) {
				renderOutline(livingEntity, matrixStack, vertexConsumerProvider);
		}
	}

	@Override
	protected void renderLabelIfPresent(ConsoleControlEntity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
		double d = this.dispatcher.getSquaredDistanceToCamera(entity);
		if (d > 4096.0) {
			return;
		}
		float f = entity.getNameLabelHeight() - 0.3f;
		matrices.push();
		matrices.translate(0.0f, f, 0.0f);
		matrices.multiply(this.dispatcher.getRotation());
		matrices.scale(-0.0075f, -0.0075f, 0.0075f);
		Matrix4f matrix4f = matrices.peek().getPositionMatrix();
		TextRenderer textRenderer = this.getTextRenderer();
		float h = (float) -textRenderer.getWidth(text) / 2;
		HitResult hitresult = MinecraftClient.getInstance().crosshairTarget;
		if (hitresult != null) {
			boolean isPlayerLookingWithSonic = isPlayerLookingAtControlWithSonic(hitresult, entity);
			OrderedText orderedText = Text.of(text.getString().toUpperCase().replace("_", " ")).asOrderedText();

			if (isPlayerLookingWithSonic) {
				textRenderer.drawWithOutline(orderedText, h, (float) text.getString().length(), 0xF0F0F0, 0x000000, matrix4f, vertexConsumers, 0xFF);

				// TODO so this is like not very well received so er im removing it :))))
				/*if (entity.getControl() instanceof RefuelerControl || entity.getIdentity().equals("RefuelerControl")) {
					Text fuelLevel = Text.literal((int) ((entity.getTardis().getFuel() / FuelData.TARDIS_MAX_FUEL) * 100) + "%");
					textRenderer.drawWithOutline(fuelLevel.asOrderedText(), h / 2, (float) fuelLevel.getString().length(), 0xF0F0F0, 0x000000, matrix4f, vertexConsumers, 0xFF);
				}*/
			}
		}
		matrices.pop();
		if (hitresult != null) {
			boolean isPlayerHoldingScanningSonic = isScanningSonicInConsole(entity);
			PlayerEntity player = MinecraftClient.getInstance().player;
			if (isPlayerHoldingScanningSonic && entity.getTardis().loyalty().get(player).isOf(Loyalty.Type.PILOT)) {
				if (entity.isPartOfSequence()) {
					matrices.push();
					matrices.scale(0.4f, 0.4f, 0.4f);
					matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(180f));
					matrices.translate(0, (-2 - entity.getControlHeight() / 2) + entity.getWorld().random.nextFloat() * 0.02, 0);
					matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(MinecraftClient.getInstance().getTickDelta() % 180));
					this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(TEXTURE)), light, OverlayTexture.DEFAULT_UV,
							!entity.wasSequenced() ? 0.95f - (entity.getSequenceColor() >= 1 ? entity.getSequenceColor() + 1 : entity.getSequenceColor() * 0.35f) : 0.0f,
							!entity.wasSequenced() ? 0.3f + (entity.getSequenceColor() >= 1 ? entity.getSequenceColor() + 1 : entity.getSequenceColor() * 0.35f) : 0.9f,
							!entity.wasSequenced() ? 0.3f + (entity.getSequenceColor() >= 1 ? entity.getSequenceColor() + 1 : entity.getSequenceColor() * 0.35f) : 0.1f, entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
					this.model.render(matrices, vertexConsumers.getBuffer(RenderLayer.getEyes(TEXTURE)), 0xFF00F0, OverlayTexture.DEFAULT_UV,
							!entity.wasSequenced() ? 0.95f - (entity.getSequenceColor() >= 1 ? entity.getSequenceColor() + 1 : entity.getSequenceColor() * 0.35f) : 0.5f,
							!entity.wasSequenced() ? 0.3f + (entity.getSequenceColor() >= 1 ? entity.getSequenceColor() + 1 : entity.getSequenceColor() * 0.35f) : 0.9f,
							!entity.wasSequenced() ? 0.3f + (entity.getSequenceColor() >= 1 ? entity.getSequenceColor() + 1 : entity.getSequenceColor() * 0.35f) : 0.5f, entity.getWorld().random.nextInt(32) != 6 ? 0.4f : 0.05f);
					matrices.pop();
				}
			}
		}
	}

	private static void renderOutline(LivingEntity entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
		VertexConsumer vertices = vertexConsumers.getBuffer(RenderLayer.LINES);

		Box box = entity.getBoundingBox().offset(-entity.getX(), -entity.getY(), -entity.getZ());
		WorldRenderer.drawBox(matrices, vertices, box, 0.0f, 0.8f, 1.0f, 1.0f);
	}

	private static boolean isPlayerHoldingScanningSonic() {
		PlayerEntity player = MinecraftClient.getInstance().player;

		if (player == null) return false;

		boolean mainhand;

		if (player.getMainHandStack().getItem() instanceof SonicItem) {
			mainhand = true;
		} else if (player.getOffHandStack().getItem() instanceof SonicItem) {
			mainhand = false;
		} else {
			return false;
		}

		ItemStack stack = mainhand ? player.getMainHandStack() : player.getOffHandStack();

		boolean current = SonicItem.findMode(stack) == SonicItem.Mode.SCANNING;
		boolean prev = SonicItem.findPreviousMode(stack) == SonicItem.Mode.SCANNING;

		return current || prev;
	}

	public static boolean isPlayerLookingAtControlWithSonic(HitResult hitResult, ConsoleControlEntity entity) {
		if (entity.getWorld() == null || !entity.getWorld().isClient())
			return false;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			if (player.getMainHandStack().getItem() instanceof SonicItem) {
				ItemStack sonic = player.getMainHandStack();
				NbtCompound nbt = sonic.getOrCreateNbt();
				if (hitResult.getType() == HitResult.Type.ENTITY) {
					Entity hitEntity = ((EntityHitResult) hitResult).getEntity();
					return hitEntity != null && hitEntity.equals(entity) && (nbt.getInt(SonicItem.PREV_MODE_KEY) == 3 || nbt.getInt(SonicItem.MODE_KEY) == 3);
				}
			} else if (player.getOffHandStack().getItem() instanceof SonicItem) {
				ItemStack sonic = player.getOffHandStack();
				if(sonic == null) return false;
				NbtCompound nbt = sonic.getOrCreateNbt();
				if (hitResult.getType() == HitResult.Type.ENTITY) {
					Entity hitEntity = ((EntityHitResult) hitResult).getEntity();
					return hitEntity != null && hitEntity.equals(entity) && (nbt.getInt(SonicItem.PREV_MODE_KEY) == 3 || nbt.getInt(SonicItem.MODE_KEY) == 3);
				}
			}
		}
		return false;
	}

	public static boolean isPlayerLookingAtControl(HitResult hitResult, ConsoleControlEntity entity) {
		if (entity.getWorld() == null || !entity.getWorld().isClient())
			return false;
		PlayerEntity player = MinecraftClient.getInstance().player;
		if (player != null) {
			if (hitResult.getType() == HitResult.Type.ENTITY) {
				Entity hitEntity = ((EntityHitResult) hitResult).getEntity();
				return hitEntity != null && hitEntity.equals(entity);
			}
		}
		return false;
	}

	public static boolean isScanningSonicInConsole(ConsoleControlEntity entity) {
		if (entity.getWorld() == null || !entity.getWorld().isClient())
			return false;
		if (entity.getTardis() == null) return false;
		if (!entity.getTardis().sonic().hasSonic(SonicHandler.HAS_CONSOLE_SONIC)) return false;
		ItemStack sonic = entity.getTardis().sonic().get(SonicHandler.HAS_CONSOLE_SONIC);
		if(sonic == null) return false;
		NbtCompound nbt = sonic.getOrCreateNbt();
		return nbt.contains(SonicItem.MODE_KEY) && (nbt.getInt(SonicItem.PREV_MODE_KEY) == 3 || nbt.getInt(SonicItem.MODE_KEY) == 3);
	}

	@Override
	public Identifier getTexture(ConsoleControlEntity controlEntity) {
		return TEXTURE;
	}

	@Override
	protected void setupTransforms(ConsoleControlEntity controlEntity, MatrixStack matrixStack, float f, float g, float h) {

	}
}
