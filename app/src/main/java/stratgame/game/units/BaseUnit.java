package stratgame.game.units;

import java.util.Arrays;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.util.CFrame;
import stratgame.game.util.Pathfinder;

public class BaseUnit extends Entity{

    public int[][] path;

    public int pathIndex;

    public Vector3f targetPos;

    public Vector3f moveTo = new Vector3f(cFrame.position);

    public double speed;

    public BaseUnit(Vector3f pos, double health, double radius, double speed){
        super(new CFrame(pos, new Vector3f(), (float)radius), new Vector3f(), health, radius);
        this.speed = speed;
    }

    public void update(){
        super.update();
        pathfind();
        temp.set(0);
        Vector3f diff = moveTo.sub(cFrame.position, temp);
        diff.y = 0;
        diff.normalize();
        cFrame.position.add(diff.mul((float)speed*MainGame.deltaTime));
        System.out.println(cFrame.position.y);
    }
    private void pathfind(){
        if(path == null){
            targetPos = new Vector3f((float)Math.random()*(float)GameManager.mapGridSize);
            path = Pathfinder.findPath(cFrame.position, targetPos);
            for (int[] is : path) {
                System.out.println(Arrays.toString(is));
            }
            pathIndex = 0;
        }
        pathIndex = -1;
        for (int i = 0; i<path.length; i++) {
            if(path[i][0] == (int)cFrame.position.x && path[i][1] == (int)cFrame.position.z){
                pathIndex = i;
                break;
            }
        }
        if(pathIndex == -1){
            path = Pathfinder.findPath(cFrame.position, targetPos);
            targetPos = new Vector3f((float)Math.random()*(float)GameManager.mapGridSize);
            moveTo = cFrame.position;
            return;
        };
        if(pathIndex == path.length-1){
            path = Pathfinder.findPath(cFrame.position, targetPos);
            moveTo = cFrame.position;
            targetPos = new Vector3f((float)Math.random()*(float)GameManager.mapGridSize);
            return;
        };
        moveTo = new Vector3f(path[pathIndex+1][0]*(float)GameManager.mapGridSize+(float)GameManager.mapGridSize/2,targetPos.y,path[pathIndex+1][1]*(float)GameManager.mapGridSize+(float)GameManager.mapGridSize/2);
    }
}
