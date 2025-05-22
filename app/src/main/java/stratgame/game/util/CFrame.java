package stratgame.game.util;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class CFrame {
    public Vector3f position;
    public Quaternionf rotation;
    public float scale;

    public CFrame(){
        position = new Vector3f();
        rotation = new Quaternionf().identity();
        scale = 1.0f;
    }

    public CFrame(CFrame cloneFrom){
        position = new Vector3f(cloneFrom.position);
        rotation = new Quaternionf(cloneFrom.rotation);
        scale = cloneFrom.scale;
    }

    public CFrame(Vector3f position, float pitch, float yaw, float roll, float scale){
        this.position = position;
        this.rotation = new Quaternionf().rotateXYZ(pitch, yaw, roll);
        this.scale = scale;
    }

    public CFrame(Vector3f position, Vector3f rotation, float scale){
        this.position = new Vector3f(position);
        this.rotation = new Quaternionf().rotateXYZ(rotation.x ,rotation.y, rotation.z);
        this.scale = scale;
    }

    public CFrame(Vector3f position, Quaternionf rotation, float scale){
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Matrix4f getAsMat4(){
        return new Matrix4f().translate(position).rotate(rotation).scale(scale);
    }

    public void setRotation(Quaternionf rotation){
        this.rotation = rotation;
    }

    public void setRotation(Vector3f rotation){
        this.rotation = new Quaternionf().rotateXYZ(rotation.x ,rotation.y, rotation.z);
    }

    public void rotate(float x, float y, float z) {
        //Create a quaternion with the delta rotation values
        Quaternionf rotationDelta = new Quaternionf();
        rotationDelta.rotationXYZ(x, y, z);

        //Multiply this transform by the rotation delta quaternion and its inverse
        rotation.mul(rotationDelta);
    }
    public Vector3f getRotation(){
        return rotation.getEulerAnglesXYZ(new Vector3f());
    }
}
