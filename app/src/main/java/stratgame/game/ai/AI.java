package stratgame.game.ai;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.ai.AISTATES.GOAL;
import stratgame.game.ai.AISTATES.STATUS;
import stratgame.game.ai.AISTATES.SUBGOAL;
import stratgame.game.units.BaseUnit;
import stratgame.game.units.Entity;
import stratgame.game.util.Pathfinder;
import stratgame.rendering.RenderManager;

public class AI {
    private Vector3f temp = new Vector3f();

    private BaseUnit p;

    private Entity target;

    private Vector3f targetPos = new Vector3f();

    private Vector3f localTargetPos = new Vector3f();
    
    private int[][] path;

    private int pathIndex;

    private GOAL goal = GOAL.IDLE;
    private double goalTime;

    private SUBGOAL subgoal = SUBGOAL.WANDER;
    private double subgoalTime;

    private STATUS status = STATUS.MOVING;
    private double statusTime;

    public AI(BaseUnit p){
        this.p = p;
    }
    // switch case for each combo of goal subgoal and status
    public void update(){
        routene();
        goalTime += MainGame.deltaTime;
        subgoalTime += MainGame.deltaTime;
        statusTime += MainGame.deltaTime;
    }

    private void routene(){
        switch (goal) {
            case ATTACK:
                switch (subgoal) {
                    case WANDER:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                    case GOTO:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                    case IDLE:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                }
                break;
            case DEFEND:
                switch (subgoal) {
                    case WANDER:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                    case GOTO:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                    case IDLE:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                }
                break;
            case IDLE:
                switch (subgoal) {
                    case WANDER:
                        switch (status) {
                            case MOVING:
                                if(atTarget()){
                                    wander();
                                }
                                pathfind();
                                moveAlongPath();
                                if(statusTime>10){
                                    setStatus(STATUS.STILL);
                                }
                                break;
                            case ATTACK:
                                setStatus(STATUS.MOVING);
                                break;
                            case STILL:
                                if(statusTime>10){
                                    wander();
                                    setStatus(STATUS.MOVING);
                                }
                                p.moveTo(p.cFrame.position);
                                break;
                        }
                        break;
                    case GOTO:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                    case IDLE:
                        switch (status) {
                            case MOVING:

                                break;
                            case ATTACK:

                                break;
                            case STILL:

                                break;
                        }
                        break;
                }
                break;
        }
    }

    public void setGoal(GOAL goal){
        this.goal = goal;
        goalTime = 0;
    }

    public void setSubgoal(SUBGOAL subgoal) {
        this.subgoal = subgoal;
        subgoalTime = 0;
    }

    public void setStatus(STATUS status) {
        this.status = status;
        statusTime = 0;
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
}
