package inf112.skeleton.app.Mapfolder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import inf112.skeleton.app.Entities.MonsterFactory;
import inf112.skeleton.app.Entities.MonsterInterface;
import inf112.skeleton.app.Entities.Enemies.BlueEnemy;

public class Level2fromcave extends TiledMap implements MapInterface {

    private float PlayerSpawnX = 155;
    private float PlayerSpawnY = 66;
    private int EnemyBoundsfromX = 200;
    private int EnemyBoundsToX = 800;
    private int EnemyBoundsFromY = 200;
    private int EnemyBoundsToY = 800;

    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private ArrayList<MonsterInterface> monsterList = new ArrayList<>();
    private Map<String, MonsterFactory> monsterFactories = new HashMap<>();
    private ArrayList<String> enemyList;

    public Level2fromcave(){
        tiledMap = new TmxMapLoader().load(Maps.Level2.source);
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
    }

    public void setup() {
        MonsterFactory blueEnemyFactory = BlueEnemy.getFactory();
        monsterFactories.put(blueEnemyFactory.name(), blueEnemyFactory);
        enemyList = new ArrayList<>(Arrays.asList("BlueEnemy", "BlueEnemy"));
        setup();
        this.spawn(enemyList);
    }
    
    public void spawn(ArrayList<String> enemyList) {

        for (int i=0; i < enemyList.size(); i++){
            MonsterFactory monsterFactory = monsterFactories.get(enemyList.get(i));
            MonsterInterface monster = monsterFactory.create(this);
            monsterList.add(monster);
        }
    }

    @Override
    public TiledMap getMap() {
        return tiledMap;
    }

    @Override
    public OrthogonalTiledMapRenderer getRenderer() {
        return renderer;
    }

    @Override
    public float getPlayerSpawnX() {
        return PlayerSpawnX;
    }

    @Override
    public float getPlayerSpawnY() {
        return PlayerSpawnY;
    }

    @Override
    public int getEnemyBoundsFromX() {
        return EnemyBoundsfromX;
    }

    @Override
    public int getEnemyBoundsToX() {
        return EnemyBoundsToX;
    }

    @Override
    public int getEnemyBoundsFromY() {
        return EnemyBoundsFromY;
    }

    @Override
    public int getEnemyBoundsToY() {
        return EnemyBoundsToY;
    }
    
    @Override
    public ArrayList<MonsterInterface> getMonsters() {
        return monsterList;
    }
    
}
