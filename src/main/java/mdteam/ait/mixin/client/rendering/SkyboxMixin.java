package mdteam.ait.mixin.client.rendering;


import com.mojang.blaze3d.systems.RenderSystem;
import mdteam.ait.AITMod;
import mdteam.ait.client.util.SkyboxUtil;
import mdteam.ait.core.AITDimensions;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class SkyboxMixin {

    @Inject(method="renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    public void renderSky(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci) {
        ClientWorld world = MinecraftClient.getInstance().world;
        if(world == null) return;
        if(world.getRegistryKey() == AITDimensions.TARDIS_DIM_WORLD) {
            SkyboxUtil.renderTardisSky(matrices);
            ci.cancel();
        }
    }
}