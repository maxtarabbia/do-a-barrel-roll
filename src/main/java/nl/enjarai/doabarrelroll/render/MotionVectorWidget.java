package nl.enjarai.doabarrelroll.render;

import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import nl.enjarai.doabarrelroll.DoABarrelRoll;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector2d;
import org.joml.Vector3f;

import nl.enjarai.doabarrelroll.ModMath;
import nl.enjarai.doabarrelroll.math.MagicNumbers;

public class MotionVectorWidget extends RenderHelper {
    public static void render(DrawContext context, int scaledWidth, int scaledHeight, Vec3d motionVector, Vec3d lookVec, double roll) {
        int centerX = scaledWidth / 2 - 1;
        int centerY = scaledHeight / 2 - 1;

        Vec3d progradeVector = motionVector.normalize();

        int markerX=0;
        int markerY=0;

        double rollRadians = -roll * MagicNumbers.TORAD;

        int fov = MinecraftClient.getInstance().options.getFov().getValue();

        double offsetY=lookVec.y - progradeVector.y;
        Vec3d flatVec = new Vec3d(progradeVector.x, 0, progradeVector.z).normalize();
        Vec3d flatLookVec = new Vec3d(lookVec.x, 0, lookVec.z).normalize();
        double offsetX = flatVec.crossProduct(flatLookVec).y;

        //rotate offsets based on rollRadians
        double cos = Math.cos(rollRadians);
        double sin = Math.sin(rollRadians);
        double temp = offsetX * cos - offsetY * sin;
        offsetY = offsetX * sin + offsetY * cos;
        offsetX = temp;
        double markerSize = 10.0;
        markerX = centerX + (int) (offsetX * Math.abs(offsetX) * scaledWidth*(50.0/fov));
        markerY = centerY + (int) (offsetY * Math.abs(offsetY) * scaledHeight*(100.0/fov));



        // Draw the prograde marker "< >"
        // This is a simple approximation. A more precise implementation would use texture or more complex drawing.
        // Left part of the marker
        ModMath.forBresenhamLine(
                markerX, markerY,
                markerX - (int) markerSize, markerY - (int) (markerSize / 2),
                blankPixel(context)
        );
        ModMath.forBresenhamLine(
                markerX, markerY,
                markerX - (int) markerSize, markerY + (int) (markerSize / 2),
                blankPixel(context)
        );

        // Right part of the marker
        ModMath.forBresenhamLine(
                markerX, markerY,
                markerX + (int) markerSize, markerY - (int) (markerSize / 2),
                blankPixel(context)
        );
        ModMath.forBresenhamLine(
                markerX, markerY,
                markerX + (int) markerSize, markerY + (int) (markerSize / 2),
                blankPixel(context)
        );
    }
}