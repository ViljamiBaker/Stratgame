package stratgame.rendering;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glPolygonMode;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glGenBuffers;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.GameManager;
import stratgame.game.units.Entity;
import stratgame.rendering.model.Texture;
import stratgame.rendering.things.Camera;
import stratgame.rendering.things.Shader;
import stratgame.rendering.util.LUTILVB;

// Handles the rendering of the game with info provided from GameManager
public class RenderManager {
	public static Camera camera;
	public static long window;
	public static HashMap<String, Texture> textures = new HashMap<>();
	
    private static boolean wireframe = false;
	private static boolean pDownLast = false;
	private static boolean lockMouse = true;
	private static boolean lDownLast = false;

	private static Shader pureShader;
	private static Shader lineShader;

    private static int vertexArray;
    private static int vertexBuffer;

    private static int mapVertexArray;
    private static int mapVertexBuffer;

    private static Texture defaultTexture;

    public static void init(){
        LUTILVB.init();
        window = LUTILVB.createWindow(800, 600, "Colors");
		glEnable(GL_DEPTH_TEST);  
		glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
		LUTILVB.enableDebug();

		camera = MainGame.camera;
        camera.window = window;
		camera.enableCameraMovement();

		vertexArray = glGenVertexArrays();
		vertexBuffer = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, LUTILVB.faceVertices, GL_STATIC_DRAW);

		glBindVertexArray(vertexArray);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*5, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.BYTES*5, Float.BYTES*3);
		glEnableVertexAttribArray(1);

		pureShader = new Shader("shaderVertex", "shaderFragTexture");
		lineShader = new Shader("shaderLineVertex", "shaderLineFrag");

		Camera staticCamera = new Camera(window);
		staticCamera.position = new Vector3f(0,0,1);

        defaultTexture = new Texture(LUTILVB.defaultTexture);

        float[] mapVerticies = new float[30*GameManager.mapHeights.length*GameManager.mapHeights[0].length];
        for (int x = 0; x < GameManager.mapHeights.length-1; x++) {
            for (int y = 0; y < GameManager.mapHeights[0].length-1; y++) {
                for(int i = 0; i<6; i++){
                    mapVerticies[x*GameManager.mapHeights[0].length*30 + y*30 + i*5] = (float)GameManager.mapGridSize*(x+mapOffsets[i][0]);
                    mapVerticies[x*GameManager.mapHeights[0].length*30 + y*30 + i*5+1] = (float)GameManager.mapHeights[(x+mapOffsets[i][0])][(y+mapOffsets[i][1])];
                    mapVerticies[x*GameManager.mapHeights[0].length*30 + y*30 + i*5+2] = (float)GameManager.mapGridSize*(y+mapOffsets[i][1]);
                    mapVerticies[x*GameManager.mapHeights[0].length*30 + y*30 + i*5+3] = mapOffsets[i][0];
                    mapVerticies[x*GameManager.mapHeights[0].length*30 + y*30 + i*5+4] = mapOffsets[i][1];
                }
            }
        }
        mapVertexArray = glGenVertexArrays();
		mapVertexBuffer = glGenBuffers();

		glBindBuffer(GL_ARRAY_BUFFER, mapVertexBuffer);
        glBufferData(GL_ARRAY_BUFFER, mapVerticies, GL_STATIC_DRAW);

		glBindVertexArray(mapVertexArray);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*5, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, Float.BYTES*5, Float.BYTES*3);
		glEnableVertexAttribArray(1);
    }
    private static Matrix4f model;
    private static Matrix4f projection;
    private static Matrix4f view;
    // called once per frame
    public static void render(){
			// input
			processInput(window);

			//render code

            GameManager.entities.get(0).cFrame.position.add(new Vector3f(0,5,0), MainGame.camera.position);

            glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

			pureShader.use();
			projection = camera.getProjection();
			pureShader.setUniform("projection", projection);
			
			view = camera.getVeiw();

			pureShader.setUniform("view", view);

			// be sure to activate shader when setting uniforms/drawing objects
			pureShader.use();		

			pureShader.setInt("material.diffuse", 0);
			pureShader.setInt("material.specular", 1);
			pureShader.setFloat("material.shininess", 32.0f);

			pureShader.setUniform("objectColor", 0.5f, 0.5f, 0.31f);
            //glBindBuffer(GL_ARRAY_BUFFER, modelVBO);
			//glBufferData(GL_ARRAY_BUFFER, largeVerticies, GL_STATIC_DRAW);


			for (Entity e : GameManager.entities) {
                glActiveTexture(GL_TEXTURE0);
			    defaultTexture.bind();
				model = e.cFrame.getAsMat4(); 
				pureShader.setUniform("model", model);
				glBindVertexArray(vertexArray);
				glDrawArrays(GL_TRIANGLES, 0, 6);
			}

            glActiveTexture(GL_TEXTURE0);
			defaultTexture.bind();
			pureShader.setUniform("model", model.identity());
			glBindVertexArray(mapVertexArray);
			glDrawArrays(GL_TRIANGLES, 0, 6*GameManager.mapHeights.length*GameManager.mapHeights[0].length);
            
			glfwSwapBuffers(window);
			glfwPollEvents();
    }

    private static int[][] mapOffsets = {{0,0},{1,0},{1,1},{0,0},{0,1},{1,1}};

    public static void processInput(long window)
	{
		if(glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS)
			glfwSetWindowShouldClose(window, true);
		
		if(glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS&&!pDownLast){
			if(!wireframe){
				glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			}else{
				glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			}
			wireframe = !wireframe;
		}
		if(glfwGetKey(window, GLFW_KEY_L) == GLFW_PRESS&&!lDownLast){
			if(lockMouse){
				lockMouse = false;
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
			}else{
				lockMouse = true;
				glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
			}
			camera.setMouseMoveEnabled(lockMouse);
		}
		lDownLast = glfwGetKey(window, GLFW_KEY_L) == GLFW_PRESS;
		pDownLast = glfwGetKey(window, GLFW_KEY_P) == GLFW_PRESS;
        MainGame.camera.processInput();
	}
}
