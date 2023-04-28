package inf112.skeleton.app.Screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.List;

import inf112.skeleton.app.Entities.AbstractGameObject;
import inf112.skeleton.app.Entities.Enemies.BlueEnemy;
import inf112.skeleton.app.Entities.Enemies.MonsterInterface;

import inf112.skeleton.app.Entities.Player.PlayerInterface;
import inf112.skeleton.app.Entities.Projectiles.ProjectileInterface;
import inf112.skeleton.app.Mapfolder.MapInterface;
import inf112.skeleton.app.Zelda;
import inf112.skeleton.app.Controller.Controller;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class View implements Screen {

    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private PlayerInterface playerI;
    private BitmapFont lifeText = new BitmapFont();
    private BitmapFont pauseText = new BitmapFont();
    private Zelda game;
    private MonsterInterface monsterI;
    private boolean paused = false;
    private Controller controller;
    public HashMap<AbstractGameObject, Rectangle> enemies = new HashMap<>();
    
    
    //MapInterface mapI = new Level1Mini(123,76);
    MapInterface mapI;
    OrthogonalTiledMapRenderer nyRend;
    TiledMap nyMap;
    SpriteBatch batch;
    
    public View(Zelda game, Controller controller, PlayerInterface playerI) {
        this.game = game;
        this.controller = controller;    
        this.playerI = playerI;
        this.mapI=playerI.returnMap();
        playerI.spawn(mapI.getPlayerSpawnX()*16,mapI.getPlayerSpawnY()*16);
        
    }   
    public View(Zelda game, Controller controller, PlayerInterface playerI, float x,float y){
        this.game = game;
        this.controller = controller;    
        this.playerI = playerI;
        this.mapI=playerI.returnMap();

    }
    @Override
    public void show() {
        
        map = mapI.getMap();
        //renderer = mapI.getRenderer();
        renderer = new OrthogonalTiledMapRenderer(map);
        monsterI = new BlueEnemy(mapI);
        camera = new OrthographicCamera();        
        batch = new SpriteBatch();

        pauseText.getData().setScale(5,5);
        pauseText.setColor(Color.BLUE);
        lifeText.getData().setScale(0.7f);
        lifeText.setColor(Color.RED);

        
    }

    @Override
    public void render(float delta) {
        playerI.getRect().setSize(playerI.getWidth(), playerI.getHeight());
        if(controller.isPaused()){pause();}
        if(!controller.isPaused()){resume();}
        if (paused) {
            batch.begin();
            pauseText.draw(batch, "PAUSED", playerI.getPosition().x, playerI.getPosition().y);
            batch.end();
            return;
        }
        
        
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
    
        
        camera.position.set(playerI.getPosition().x+playerI.getWidth() / 2, playerI.getPosition().y+playerI.getHeight()/2,0);
        
        
        //Update   
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        
        playerI.update(delta);

        if (playerI.onPortal()){
            this.mapI=playerI.nextMap();
            
            renderer.setMap(mapI.getMap());
            playerI.setOffPortal();
        }
        
        

        //render map
        renderer.setView(camera);
        renderer.render();
   
        
        //render player / enemies / projectiles
        batch.begin();

        //draw player
        playerI.getSprite().draw(batch);

        //draw projectiles and check if they hit enemy.
        for (ProjectileInterface projectile : playerI.getArrows()){
            projectile.getSprite().draw(batch);
            for (MonsterInterface monsterI : mapI.getMonsterList()) {
                if (projectile.getRect().overlaps(monsterI.getRect())) { monsterI.takeDamage(projectile.getDamage()); }
            }
        }
        CopyOnWriteArrayList<MonsterInterface> deadMonsterList = new CopyOnWriteArrayList<>();
            
        //draw monsters
        for (MonsterInterface monsterI : mapI.getMonsterList()){
            
            monsterI.update(delta);
            monsterI.getSprite().draw(batch);   
            lifeText.draw(batch,"HP:"+monsterI.getCurrentHitpoints(),monsterI.getPosition().x,monsterI.getPosition().y);
            hpText.draw(batch,"HP:"+monsterI.getCurrentHitpoints(),monsterI.getPosition().x,monsterI.getPosition().y);
            monsterI.followPlayer(playerI.getPosition().x, playerI.getPosition().y);
            
            //check if monsterhp is less than or equal to zero
            if (monsterI.isDead()){
                deadMonsterList.add(monsterI);
                playerI.getExp();
            }
                
                

            //check if monster and player is colliding. if so, player takes damage
            if (monsterI.getRect().overlaps(playerI.getRect())) {
                playerI.takeDamage(monsterI.getDamage());
                break;
            }
        }   
        //remove dead monsters
        mapI.getMonsterList().removeAll(deadMonsterList);
        deadMonsterList.clear();
        
        
        System.out.println(playerI.getHealthAbilityLevel());

        //open store (bound to K)
        if(controller.isShop()){
            game.setScreen(new Shop(game,controller,playerI));
             

        }
        lifeText.draw(batch, "Lives: " + playerI.getLives(), playerI.getPosition().x - 12, playerI.getPosition().y + playerI.getHeight() + 30);
        lifeText.draw(batch, "HP: " + playerI.getCurrentHitpoints(), playerI.getPosition().x - 12, playerI.getPosition().y + playerI.getHeight() + 15);
        lifeText.draw(batch,".",playerI.getPosition().x+11,playerI.getPosition().y+18);
        lifeText.draw(batch, "Level: " + playerI.getLevel(), playerI.getPosition().x - 12, playerI.getPosition().y - playerI.getHeight() + 15);
        
        batch.end();

    }

        
    




    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width/3f;
        camera.viewportHeight = height/3f;
        
    }


    @Override
    public void pause() {
        paused = true;
    }

    /* */
    @Override
    public void resume() {
        paused = false;
    }


    @Override
    public void hide() {
    }


    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
       
    }
}
