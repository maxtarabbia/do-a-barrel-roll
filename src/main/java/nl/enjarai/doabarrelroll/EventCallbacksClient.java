package nl.enjarai.doabarrelroll;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.Vec3d;
import nl.enjarai.doabarrelroll.api.RollEntity;
import nl.enjarai.doabarrelroll.api.RollMouse;
import nl.enjarai.doabarrelroll.config.ModConfig;
import nl.enjarai.doabarrelroll.impl.key.InputContextImpl;
import nl.enjarai.doabarrelroll.render.HorizonLineWidget;
import nl.enjarai.doabarrelroll.render.MotionVectorWidget;
import nl.enjarai.doabarrelroll.render.MomentumCrosshairWidget;
import nl.enjarai.doabarrelroll.util.StarFoxUtil;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class EventCallbacksClient {

    public static Vec3d lastVec = new Vec3d(0,0,0);
    public static Vec3d prevVec = new Vec3d(0,0,0);
    public static float lastDelta = 0;
    public static void clientTick(MinecraftClient client) {
        InputContextImpl.getContexts().forEach(InputContextImpl::tick);

        if (!DoABarrelRollClient.isFallFlying()) {
            DoABarrelRollClient.clearValues();
        }

        ModKeybindings.clientTick(client);

        StarFoxUtil.clientTick(client);
    }

    public static Vector2i onRenderCrosshair(DrawContext context, RenderTickCounter tickCounter, int scaledWidth, int scaledHeight) {
        if (!DoABarrelRollClient.isFallFlying()) return new Vector2i(0, 0);
        var tickDelta = tickCounter.getFixedDeltaTicks();

        var entity = MinecraftClient.getInstance().getCameraEntity();
        var rollEntity = ((RollEntity) entity);
        if (entity != null) {
            if (ModConfig.INSTANCE.getShowHorizon()) {
                HorizonLineWidget.render(context, scaledWidth, scaledHeight,
                        rollEntity.doABarrelRoll$getRoll(tickDelta), entity.getPitch(tickDelta));
            }
            if(ModConfig.INSTANCE.getShowMotionVector()){
                Vec3d lookvec = entity.getRotationVec(tickDelta);
                if(lastDelta < tickDelta){
                    prevVec=lastVec;
                    lastVec=entity.getVelocity();
                }
                lastDelta=tickDelta;
                Vec3d motionVec = prevVec.lerp(lastVec,tickDelta);
                MotionVectorWidget.render(context, scaledWidth, scaledHeight,
                        motionVec, lookvec, rollEntity.doABarrelRoll$getRoll(tickDelta));
            }

            if (ModConfig.INSTANCE.getMomentumBasedMouse() && ModConfig.INSTANCE.getShowMomentumWidget()) {
                var rollMouse = (RollMouse) MinecraftClient.getInstance().mouse;

                return MomentumCrosshairWidget.render(context, scaledWidth, scaledHeight, new Vector2d(rollMouse.doABarrelRoll$getMouseTurnVec()));
            }
        }

        return new Vector2i(0, 0);
    }
}
