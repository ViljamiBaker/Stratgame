package stratgame.game.ai;

public class AISTATES {
    // ai will have overarching GOAL and enter a SUBGOAL
    public static enum GOAL{
        ATTACK,
        DEFEND,
        IDLE,
        WANDER
    }
}
