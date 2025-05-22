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
import static org.lwjgl.opengl.GL11.GL_LINES;
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

import java.util.ArrayList;
import java.util.HashMap;

import org.joml.Matrix4f;
import org.joml.Vector2f;
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
	
	private static ArrayList<Float> lineData = new ArrayList<>();

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

        float[] mapVerticies = new float[30*GameManager.getMH().length*GameManager.getMH()[0].length];
        for (int x = 0; x < GameManager.getMH().length-1; x++) {
            for (int y = 0; y < GameManager.getMH()[0].length-1; y++) {
                for(int i = 0; i<6; i++){
                    mapVerticies[x*GameManager.getMH()[0].length*30 + y*30 + i*5] = (float)GameManager.getMGS()*(x+mapOffsets[i][0]);
                    mapVerticies[x*GameManager.getMH()[0].length*30 + y*30 + i*5+1] = (float)GameManager.getMH()[(x+mapOffsets[i][0])][(y+mapOffsets[i][1])];
                    mapVerticies[x*GameManager.getMH()[0].length*30 + y*30 + i*5+2] = (float)GameManager.getMGS()*(y+mapOffsets[i][1]);
                    mapVerticies[x*GameManager.getMH()[0].length*30 + y*30 + i*5+3] = mapOffsets[i][0];
                    mapVerticies[x*GameManager.getMH()[0].length*30 + y*30 + i*5+4] = mapOffsets[i][1];
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

            //GameManager.getEntities().get(0).cFrame.position.add(new Vector3f(0,5,0), MainGame.camera.position);

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


			for (Entity e : GameManager.getEntities()) {
                glActiveTexture(GL_TEXTURE0);
			    defaultTexture.bind();
				model = e.cFrame.getAsMat4().translate(0, (float)e.getRadius(), 0); 
				pureShader.setUniform("model", model);
				glBindVertexArray(vertexArray);
				glDrawArrays(GL_TRIANGLES, 0, 6);
			}

            glActiveTexture(GL_TEXTURE0);
			defaultTexture.bind();
			pureShader.setUniform("model", model.identity());
			glBindVertexArray(mapVertexArray);
			glDrawArrays(GL_TRIANGLES, 0, 6*GameManager.getMH().length*GameManager.getMH()[0].length);
    
			drawLinesFlush();

			glfwSwapBuffers(window);
			glfwPollEvents();
    }

    private static int[][] mapOffsets = {{0,0},{1,0},{1,1},{0,0},{0,1},{1,1}};

	private static int lineVAO, lineVBO;

	public static void drawLine(Vector3f p1, Vector3f p2, Vector3f color)
	{
		// point 1
		lineData.add(p1.x);
		lineData.add(p1.y);
		lineData.add(p1.z);

		// color
		lineData.add(color.x);
		lineData.add(color.y);
		lineData.add(color.z);

		// point 2
		lineData.add(p2.x);
		lineData.add(p2.y);
		lineData.add(p2.z);

		// color
		lineData.add(color.x);
		lineData.add(color.y);
		lineData.add(color.z);
	}
	public static void drawLine2d(Vector2f p1, Vector2f p2, Vector3f color)
	{
		drawLine(new Vector3f(p1, 0), new Vector3f(p2, 0), color);
	}

	private static boolean createdLineVAO = false;

	private static void drawLinesFlush()
	{
		lineShader.use();

		lineShader.setUniform("projection", projection);

		lineShader.setUniform("view", view);

		float[] arr = new float[lineData.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = lineData.get(i);
		}

		if (!createdLineVAO)
		{
			createdLineVAO = true;

			lineVAO = glGenVertexArrays();

			lineVBO = glGenBuffers();
			glBindBuffer(GL_ARRAY_BUFFER, lineVBO);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);

			glBindVertexArray(lineVAO);

			glVertexAttribPointer(0, 3, GL_FLOAT, false, Float.BYTES*6,0);
			glEnableVertexAttribArray(0);

			glVertexAttribPointer(1, 3, GL_FLOAT, false, Float.BYTES*6,Float.BYTES*3);
			glEnableVertexAttribArray(1);
		}
		else
		{
			glBindBuffer(GL_ARRAY_BUFFER, lineVBO);
			glBufferData(GL_ARRAY_BUFFER, arr, GL_STATIC_DRAW);
		}

		// 6 floats make up a vertex (3 position 3 color)
		// divide by that to get number of vertices to draw
		int count = lineData.size()/6;

		glBindVertexArray(lineVAO);
		glDrawArrays(GL_LINES, 0, count);

		lineData.clear();
	}

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
