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
        if(cFrame.position.y-GameManager.getMapHeight(cFrame.position)<=0.01){
            hit();
        }
        int colliding = GameManager.getEntitiesInRadius(cFrame.position, radius, team).length;
        if(colliding != 0){
            hit();
        }
    }

    public void hit(){
        Entity entity = GameManager.getColliding(this, true);

        entity.changeHealth(-damage/entity.cFrame.position.sub(cFrame.position,new Vector3f()).length());
        GameManager.requestDeletion(this);
    }
}
