package stratgame.game.weapons;

import org.joml.Vector3f;

import stratgame.game.GameManager;
import stratgame.game.units.Entity;
import stratgame.game.util.CFrame;
import stratgame.game.util.GUTILVB;

public class Bullet extends Entity{
    public double damage;
    public double radius;

    public Bullet(Vector3f pos, Vector3f vel, double damage, double radius, int team){
        super(new CFrame(pos, GUTILVB.vector3toAngles(vel.normalize(new Vector3f())), (float)radius), vel, 1, 0.2, team, 0.999);
        this.damage = damage;
        this.radius = radius;
    }

    public void update(){
        super.update();
        Entity e = GameManager.getColliding(this, true);
        if(e!=null){
            hit(e);
        }
    }

    @Override
    public void hitGround(){
        GameManager.requestDeletion(this);
    }

    public void hit(Entity e){
        if(e == null){
            return;
        }
        e.changeHealth(-damage*velocity.length());
        GameManager.requestDeletion(this);
    }
}
