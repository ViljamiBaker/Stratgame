package stratgame.game;
// Updates the gamestate when told to

import java.util.ArrayList;

import org.joml.Vector3f;

import stratgame.game.units.BaseUnit;
import stratgame.game.units.Entity;
import stratgame.game.util.GUTILVB;

public class GameManager {
    private static ArrayList<Entity> entities = new ArrayList<>();
    private static ArrayList<Entity> entitiesToRemove = new ArrayList<>();
    private static ArrayList<Entity> entitiesToAdd = new ArrayList<>();

    private static double[][] mapHeights = new double[10][10];
    private static double[][] mapSpeeds = new double[10][10];

    private static double mapGridSize = 10;

    // called once at the start of the game
    public static void init(){
        for (int x = 0; x < mapHeights.length-1; x++) {
            for (int y = 0; y < mapHeights[0].length-1; y++) {
                mapHeights[x][y] = Math.random()*10-1;
                mapSpeeds[x][y] = Math.random()*10-1;
            }
        }
        entities.add(new BaseUnit(new Vector3f(), 0, 1,5));
    }

    // called once per frame
    public static void tick(){
        for (Entity e : entities) {
            e.update();
        }
        entities.removeAll(entitiesToRemove);
        entities.addAll(entitiesToAdd);
        entitiesToRemove.clear();
        entitiesToAdd.clear();
    }

    private static double gMHA(int x, int z){
        if(x>=mapHeights.length||x<0){
            return 0;
        }
        if(z>=mapHeights[0].length||z<0){
            return 0;
        }
        return mapHeights[x][z];
    }

    public static Vector3f convertToMap(Vector3f in){
        return in.set(in.x/mapGridSize,in.y,in.z/mapGridSize);
    }

    public static Vector3f convertFrMap(Vector3f in){
        return in.set(in.x*mapGridSize,in.y,in.z*mapGridSize);
    }

    public static double getMapHeight(Vector3f position){
        double x = (position.x/mapGridSize);
        double z = (position.z/mapGridSize);
        if(x>=mapHeights.length||x<0){
            return 0;
        }
        if(z>=mapHeights[0].length||z<0){
            return 0;
        }
        //return gMHA((int)x, (int)z);
        int ix = (int)x;
        int iz = (int)z;
        double s = x%1.0;
        double t = z%1.0;

        double u0 = GUTILVB.lerp(gMHA(ix, iz),gMHA(ix+1, iz),s);
        double u1 = GUTILVB.lerp(gMHA(ix, iz+1),gMHA(ix+1, iz+1),s);
        double avgH = GUTILVB.lerp(u0, u1, t);
        return avgH;
    }

    public static Entity[] getEntitiesInRadius(Vector3f position, double radius){
        ArrayList<Entity> eninrad = new ArrayList<>();
        double radiusSquared = radius*radius;
        Vector3f temp = new Vector3f();
        for (Entity entity : eninrad) {
            if(entity.cFrame.position.sub(position, temp).lengthSquared()<=radiusSquared){
                eninrad.add(entity);
            }
        }
        return eninrad.toArray(new Entity[eninrad.size()]);
    }

    public static void requestDeletion(Entity e){
        entitiesToRemove.add(e);
    }

    public static void requestAddition(Entity e){
        entitiesToAdd.add(e);
    }

    public static double[][] getMH() {
        return mapHeights;
    }

    public static double[][] getMS() {
        return mapSpeeds;
    }

    public static int getMX() {
        return mapSpeeds.length;
    }

    public static int getMY() {
        return mapSpeeds[0].length;
    }

    public static double getMGS() {
        return mapGridSize;
    }

    public static ArrayList<Entity> getEntities() {
        return entities;
    }
}
