package stratgame.game.ai;

public class AISTATES {
    // ai will have overarching GOAL and enter a SUBGOAL to decide its STATUS
    public static enum GOAL{
        ATTACK,
        DEFEND,
        IDLE
    }
    public static enum SUBGOAL{
        WANDER,
        IDLE,
        GOTO
    }
    public static enum STATUS{
        MOVING,
        STILL,
        ATTACK
    }
}
