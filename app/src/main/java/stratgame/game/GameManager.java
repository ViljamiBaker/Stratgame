package stratgame.game;
// Updates the gamestate when told to

import java.util.ArrayList;

import org.joml.Vector3f;

import stratgame.MainGame;
import stratgame.game.units.BaseUnit;
import stratgame.game.units.Entity;
import stratgame.game.util.GUTILVB;
import stratgame.game.weapons.Weapon;

public class GameManager {
    private static ArrayList<Entity> entities = new ArrayList<>();
    private static ArrayList<Entity> entitiesToRemove = new ArrayList<>();
    private static ArrayList<Entity> entitiesToAdd = new ArrayList<>();

    private static double[][] mapHeights = new double[50][50];
    private static double[][] mapSpeeds = new double[50][50];

    private static double mapGridSize = 2;

    // called once at the start of the game
    public static void init(){
        for (int x = 0; x < mapHeights.length-1; x++) {
            for (int y = 0; y < mapHeights[0].length-1; y++) {
                mapHeights[x][y] = MainGame.r.nextInt(2);
                mapSpeeds[x][y] = MainGame.r.nextDouble(5);
            }
        }
        entities.add(new BaseUnit(new Vector3f( 0, 0, 0), 10, 1,5,1, new Weapon(100, 1, 50, 1, 0.2)));
        entities.add(new BaseUnit(new Vector3f(50,50,50), 10, 1,5,2, new Weapon(100, 1, 50, 1, 0.2)));
    }

    // called once per frame
    public static void update(){
        for (Entity e : entities) {
            e.fullupdate();
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

    public static Entity[] getEntitiesInRadius(Vector3f position, double radius, long ignoreTeam){
        ArrayList<Entity> eninrad = new ArrayList<>();
        double radiusSquared = radius*radius;
        Vector3f temp = new Vector3f();
        for (Entity entity : entities) {
            if(ignoreTeam!=-1&&entity.getTeam()==ignoreTeam)continue;
            if(entity.cFrame.position.sub(position, temp).lengthSquared()<=radiusSquared+entity.getRadiusSquared()){
                eninrad.add(entity);
            }
        }
        return eninrad.toArray(new Entity[eninrad.size()]);
    }

    public static Entity getColliding(Entity e, boolean ignoreTeam){
        for (Entity entity : entities) {
            if(entity.equals(e)||(ignoreTeam&&entity.getTeam()==e.getTeam())){
                continue;
            }
            if(e.getRadiusSquared()+entity.getRadiusSquared()>=e.cFrame.position.distanceSquared(entity.cFrame.position)){
                return entity;
            }
        }
        return null;
    }

    public static boolean isColliding(Entity e, boolean ignoreTeam){
        return getColliding(e,ignoreTeam) != null;
    }

    public static boolean isAtMapPosition(Vector3f pos, int x, int y){
        return ((int)pos.x/mapGridSize==x)&&((int)pos.z/mapGridSize==y);
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
