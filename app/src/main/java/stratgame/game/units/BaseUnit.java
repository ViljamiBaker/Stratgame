package stratgame.game.units;
import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.ai.AI;
import stratgame.game.util.CFrame;
import stratgame.game.weapons.Weapon;
import stratgame.rendering.RenderManager;

public class BaseUnit extends Entity{

    private Vector3f moveTo = new Vector3f(cFrame.position);

    private double speed;

    private AI ai;

    private Weapon weapon;

    public BaseUnit(Vector3f pos, double health, double radius, double speed, int team, Weapon weapon){
        super(new CFrame(pos, new Vector3f(), (float)radius), new Vector3f(), health, radius, team, 0.99);
        this.speed = speed;
        this.weapon = weapon;
        weapon.p = this;
        ai = new AI(this);
    }

    public void moveTo(Vector3f to){
        moveTo = to;
    }
    @Override
    public void update(){
        weapon.update();
        ai.update();
        Vector3f diff = moveTo.sub(cFrame.position, temp);
        diff.y = 0;
        if(diff.lengthSquared()!=0){
            if(diff.lengthSquared()<=(speed*MainGame.deltaTime*speed*MainGame.deltaTime)){
                cFrame.position.add(diff);
            }else{
                diff.normalize();
                cFrame.position.add(diff.mul((float)speed*MainGame.deltaTime));
            }
        }
        RenderManager.drawLine(cFrame.position, moveTo, new Vector3f(1.0f));
    }

    public void attack(Vector3f position){
        weapon.attack(new Vector3f(position));
    }

    public boolean atTarget(){
        return Math.abs(moveTo.x-cFrame.position.x)+Math.abs(moveTo.z-cFrame.position.z)<=1;
    }

    public double getRange() {
        return weapon.range;
    }
}
