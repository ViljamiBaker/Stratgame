package stratgame.game.units;
import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.util.CFrame;
import stratgame.rendering.RenderManager;

public class BaseUnit extends Entity{

    public Vector3f moveTo = new Vector3f(cFrame.position);

    public double speed;

    public AI ai;

    public BaseUnit(Vector3f pos, double health, double radius, double speed){
        super(new CFrame(pos, new Vector3f(), (float)radius), new Vector3f(), health, radius);
        this.speed = speed;
        ai = new AI(this);
    }

    public void update(){
        ai.update();
        temp.set(0);
        Vector3f diff = moveTo.sub(cFrame.position, temp);
        diff.y = 0;
        diff.normalize();
        cFrame.position.add(diff.mul((float)speed*MainGame.deltaTime));
        RenderManager.drawLine(cFrame.position, moveTo, new Vector3f(1.0f));
        super.update();
    }
    
}
