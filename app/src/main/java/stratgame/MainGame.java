package stratgame;
import static org.lwjgl.glfw.GLFW.glfwGetTime;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;

import java.util.Random;

import org.joml.Vector3f;

import stratgame.game.GameManager;
import stratgame.rendering.RenderManager;
import stratgame.rendering.things.Camera;

// Handles the main loop
// Holds variables for things rendering and game use e.g: camera
public class MainGame {
    public static final Vector3f gravity = new Vector3f(0,-1,0);

    public static Camera camera = new Camera(0);

    public static Random r = new Random();

	public static float deltaTime = 0.0f;	// Time between current frame and last frame
	private static float lastFrame = 0.0f; // Time of last frame

    // called once at startup
    public static void init(){
        GameManager.init();
        RenderManager.init();
        loop();
    }

    // loops until window should die
    public static void loop(){
        while(!glfwWindowShouldClose(RenderManager.window))
		{
            float currentFrame = (float)glfwGetTime();
			deltaTime = currentFrame - lastFrame;
			lastFrame = currentFrame;
            GameManager.tick();
            RenderManager.render();
		}
		glfwTerminate();
    }
    public static void main(String[] args) {
        MainGame.init();
    }
}
