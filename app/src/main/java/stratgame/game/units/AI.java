package stratgame.game.units;

import java.util.Arrays;

import org.joml.Vector3f;

import stratgame.game.GameManager;
import stratgame.game.util.Pathfinder;
import stratgame.rendering.RenderManager;

public class AI {
    public BaseUnit p;

    public Vector3f targetPos;
    
    public int[][] path;

    public int pathIndex;

    public AI(BaseUnit p){
        this.p = p;
    }

    public void update(){
        pathfind();
    }

    private void findpath(){
        targetPos = new Vector3f((float)Math.random()*GameManager.mapSpeeds.length*(float)GameManager.mapGridSize);
        path = Pathfinder.findPath(p.cFrame.position, targetPos);
        p.moveTo = targetPos;
    }

    private void pathfind(){
        if(path == null){
            findpath();
        }
        pathIndex = -1;
        for (int i = 0; i<path.length; i++) {
            if(path[i][0] == (int)(p.cFrame.position.x/GameManager.mapGridSize) && path[i][1] == (int)(p.cFrame.position.z/GameManager.mapGridSize)){
                pathIndex = i;
                break;
            }
        }
        if(pathIndex == -1){
            findpath();
            return;
        };
        if(pathIndex == path.length-1){
            findpath();
            return;
        };
        p.moveTo = GameManager.convertFrMap(new Vector3f(path[pathIndex+1][0], targetPos.y, path[pathIndex+1][1])).add(new Vector3f((float)GameManager.mapGridSize/2));
        //RenderManager.drawLine(GameManager.convertFrMap(new Vector3f(path[0][0], 0, path[0][1])), GameManager.convertFrMap(new Vector3f(path[0+1][0], 0, path[0+1][1])), new Vector3f(1.0f));
        for (int i = 0; i < path.length-1; i++) {
            RenderManager.drawLine(GameManager.convertFrMap(new Vector3f(path[i][0], 20, path[i][1])), GameManager.convertFrMap(new Vector3f(path[i+1][0], 20, path[i+1][1])), new Vector3f(1.0f));
        }
    }
}
