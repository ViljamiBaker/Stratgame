package stratgame.game.weapons;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.units.BaseUnit;

public class Weapon {
    public BaseUnit p;

    public double range;
    public double maxCooldown;
    public double cooldown;

    public double velocity;
    public double damage;
    public double radius;

    public Weapon(double range, double maxCooldown, double velocity, double damage, double radius){
        this.range = range;
        this.maxCooldown = maxCooldown;
        this.velocity = velocity;
        this.damage = damage;
        this.radius = radius;
    }

    public void update(){
        cooldown -= MainGame.deltaTime;
    }

    public void attack(Vector3f pos){
        if(cooldown>0){
            //return;
        }
        cooldown = maxCooldown;
        GameManager.requestAddition(new Bullet(new Vector3f(p.cFrame.position), pos.sub(p.cFrame.position, new Vector3f()).normalize((float)velocity), damage, radius, p.getTeam()));
    }
}
