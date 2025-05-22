package stratgame.game.util;

import org.joml.Math;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class GUTILVB {
    public static Quaternionf eulerToQuaternion(Vector3f in, Quaternionf out){
        return eulerToQuaternion(in.x, in.y, in.z, out);
    }

    public static Quaternionf eulerToQuaternion(float roll, float pitch, float yaw, Quaternionf out){
        float cr = Math.cos(roll * 0.5f);
        float sr = Math.sin(roll * 0.5f);
        float cp = Math.cos(pitch * 0.5f);
        float sp = Math.sin(pitch * 0.5f);
        float cy = Math.cos(yaw * 0.5f);
        float sy = Math.sin(yaw * 0.5f);

        float w = cr * cp * cy + sr * sp * sy;
        float x = sr * cp * cy - cr * sp * sy;
        float y = cr * sp * cy + sr * cp * sy;
        float z = cr * cp * sy - sr * sp * cy;
        return out.set(x, y, z, w);
    }

    public static Vector3f mulAdd(Vector3f in, Vector3f add, double mul){
        return in.set(in.x+add.x*mul,in.y+add.y*mul,in.z+add.z*mul);
    }

    public static Vector3f rotateXY(Vector3f in){
        float temp = in.x;
        in.x = in.z;
        in.z = temp;
        return in;
    }

    public static Vector3f eulerAngToVector3(Vector3f rotation, Vector3f out){
        return eulerAngToVector3(rotation.y, rotation.z, out);
    }

    public static Vector3f eulerAngToVector3(float pitch, float yaw, Vector3f out){
        float x = Math.cos(yaw) * Math.cos(pitch);
		float y = Math.sin(pitch);
		float z = Math.sin(yaw) * Math.cos(pitch);

        return out.set(x,y,z);
    }

    public static float[] vector3toAngles(Vector3f in, float[] out){
        float pitch = Math.asin(in.y);
        float yaw = Math.acos(in.x/Math.cos(pitch)) * Math.signum(-in.z);
        out[0] = 0;
        out[1] = pitch;
        out[2] = yaw;
        return out;
    }

    private static Quaternionf temp;

    public static Quaternionf rotateQuatByEuler(Quaternionf quat, float pitch, float yaw, float roll){
        return quat.add(eulerToQuaternion(pitch, yaw, roll, temp));
    }

    public static Quaternionf rotateQuatByEuler(Quaternionf quat, float pitch, float yaw, float roll, Quaternionf dest){
        return quat.add(eulerToQuaternion(pitch, yaw, roll, temp), dest);
    }

    public static double lerp(double d1, double d2, double t){
        return d1*(1-t)+d2*t;
    }
}