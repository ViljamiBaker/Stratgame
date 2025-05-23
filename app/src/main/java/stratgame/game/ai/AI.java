package stratgame.game.ai;

import java.util.ArrayList;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.ai.AISTATES.GOAL;
import stratgame.game.units.BaseUnit;
import stratgame.game.units.Entity;
import stratgame.game.util.Pathfinder;
import stratgame.rendering.RenderManager;

public class AI {
    private Vector3f temp = new Vector3f();

    private BaseUnit p;

    private Entity target;

    private Vector3f targetPos;

    private Vector3f localTargetPos;
    
    private int[][] path;

    private int pathIndex;

    private GOAL goal = GOAL.IDLE;
    private double goalTime;

    public AI(BaseUnit p){
        this.p = p;
        targetPos = new Vector3f(p.cFrame.position);
        localTargetPos = new Vector3f(p.cFrame.position);
    }
    // switch case for each combo of goal subgoal and status
    public void update(){
        routene();
        goalTime += MainGame.deltaTime;
    }

    private void routene(){
        switch (goal) {
            case ATTACK:
                if(target == null){
                    target = generateTarget();
                    if(target == null){
                        setGoal(GOAL.IDLE);
                        break;
                    }
                }
                Vector3f newTargetPos = genTargetPos();
                if(p.cFrame.position.distance(newTargetPos)>1){
                    targetPos = newTargetPos;
                    if(!GameManager.isAtMapPosition(newTargetPos, path[path.length-1][0], path[path.length-1][1])){
                        createPath();
                    }
                    pathfind();
                    moveAlongPath();
                }else{
                    p.moveTo(p.cFrame.position);
                    p.attack(target.cFrame.position);
                }
                break;
            case DEFEND:
            case IDLE:
                if(goalTime>10){
                    target = generateTarget();
                    if(target != null){
                        setGoal(GOAL.ATTACK);
                        break;
                    }
                    wander();
                    setGoal(GOAL.WANDER);
                }
                p.moveTo(p.cFrame.position);
            case WANDER:
                if(atTarget()){
                    wander();
                }
                pathfind();
                moveAlongPath();
                if(goalTime>10){
                    setGoal(GOAL.IDLE);
                }
                break;
        }
    }

    public void setGoal(GOAL goal){
        this.goal = goal;
        goalTime = 0;
        System.out.println(goal);
    }

    private void createPath(){
        path = Pathfinder.findPath(p.cFrame.position, targetPos);
        pathIndex = 0;
        p.moveTo(targetPos);
    }

    private void wander(){
        targetPos = new Vector3f((float)MainGame.r.nextDouble(GameManager.getMX()*GameManager.getMGS()),0.0f,
            (float)MainGame.r.nextDouble(GameManager.getMY()*GameManager.getMGS()));
        createPath();
    }

    private void moveAlongPath(){
        if(pathIndex == path.length-1){
            p.moveTo(targetPos);
            return;
        }
        localTargetPos.set(GameManager.convertFrMap(new Vector3f(path[pathIndex+1][0]+0.5f, 20, path[pathIndex+1][1]+0.5f)));
        p.moveTo(localTargetPos);
    }

    private void pathfind(){
        for (int i = 0; i < path.length-1; i++) {
            RenderManager.drawLine(GameManager.convertFrMap(new Vector3f(path[i][0]+0.5f, 20, path[i][1]+0.5f)), 
                GameManager.convertFrMap(new Vector3f(path[i+1][0]+0.5f, 20, path[i+1][1]+0.5f)), new Vector3f(1.0f));
        }
        pathIndex = -1;
        for (int i = 0; i<path.length; i++) {
            if(path[i][0] == (int)(p.cFrame.position.x/GameManager.getMGS()) && path[i][1] == (int)(p.cFrame.position.z/GameManager.getMGS())){
                pathIndex = i;
                break;
            }
        }
        if(pathIndex == -1){
            createPath();
            return;
        }
        if(pathIndex == path.length-1){
            return;
        }
    }

    public boolean atTarget(){
        return Math.abs(targetPos.x-p.cFrame.position.x)+Math.abs(targetPos.z-p.cFrame.position.z)<=1;
    }

    protected Entity generateTarget(){
        ArrayList<Entity> entities = GameManager.getEntities();
        double highestScore = -2.0;
        int highestScoreIndex = -1;
        for (int i = 0; i < entities.size(); i++) {
            double score = evaluateTarget(entities.get(i));
            if(score>highestScore){
                highestScore = score;
                highestScoreIndex = i;
            }
        }
        if(highestScore <= 0.0){
            return null;
        }
        return entities.get(highestScoreIndex);
    }

    protected Vector3f genTargetPos(){
        Vector3f diff = p.cFrame.position.sub(target.cFrame.position,new Vector3f());
        if(diff.length()>p.getRange()*0.8){
            diff.normalize((float)p.getRange()*0.8f);
        }
        Vector3f newTargetPos = diff.add(target.cFrame.position);
        return newTargetPos;
    }

    protected double evaluateTarget(Entity e){
        if(e.equals(p)||p.getTeam()==e.getTeam()){
            return -1.0;
        }
        return e.cFrame.position.distanceSquared(p.cFrame.position);
    } 
}
