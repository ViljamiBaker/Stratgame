package stratgame.game.units;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.util.CFrame;
import stratgame.game.util.GUTILVB;

public class Entity {
    public CFrame cFrame;
    public Vector3f velocity;
    public double health;
    public double radius;
    public double radiusSquared;
    public float drag = 0.99f;

    protected Vector3f temp = new Vector3f();

    public Entity(CFrame cFrame, Vector3f velocity, double health, double radius){
        this.cFrame = cFrame;
        this.velocity = velocity;
        this.health = health;
        this.radius = radius;
        this.radiusSquared = radius*radius;
    }

    public void update(){
        physics();
    }

    public void physics(){
        double mapHeight = GameManager.getMapHeight(cFrame.position);
        GUTILVB.mulAdd(velocity, MainGame.gravity, MainGame.deltaTime);
        if(cFrame.position.y<=mapHeight){
            cFrame.position.y = (float)mapHeight;
            velocity.y = 0;
        }else{
        }
        velocity.mul(drag);
        cFrame.position.add(velocity);
    }
}
