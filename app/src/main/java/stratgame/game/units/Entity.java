package stratgame.game.units;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.util.CFrame;
import stratgame.game.util.GUTILVB;

public class Entity {
    public static long CURRENTENTITYID = 0;

    public CFrame cFrame;
    protected Vector3f velocity;
    protected double radius;
    protected double radiusSquared;
    protected float drag = 0.99f;
    protected int team;
    protected double health;
    protected double dragCooef;

    private long entityID;

    protected Vector3f temp = new Vector3f();

    public Entity(CFrame cFrame, Vector3f velocity, double health, double radius, int team, double dragCooef){
        this.cFrame = cFrame;
        this.velocity = velocity;
        this.health = health;
        this.radius = radius;
        this.radiusSquared = radius*radius;
        this.team = team;
        this.dragCooef = dragCooef;
        this.entityID = CURRENTENTITYID;
        CURRENTENTITYID++;
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
        GUTILVB.mulAdd(cFrame.position, velocity, MainGame.deltaTime);
        //cFrame.position.add(velocity.mul(MainGame.deltaTime,new Vector3f()));
    }

    public void changeHealth(double value){
        health += value;
        if(health<=0){
            GameManager.requestDeletion(this);
        }
    }

    public double getRadius() {
        return radius;
    }

    public int getTeam() {
        return team;
    }
    
    public double getRadiusSquared() {
        return radiusSquared;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public float getDrag() {
        return drag;
    }

    public long getEntityID() {
        return entityID;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Entity)){
            return false;
        }
        Entity e = (Entity)o;
        return e.entityID == this.entityID;
    }
}
