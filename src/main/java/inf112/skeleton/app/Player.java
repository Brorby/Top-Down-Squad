package inf112.skeleton.app;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;

public class Player extends GameObject {

    private Controller controller;
    private Collision collision;
    private String  lastPlayerPics;
    public int lives;
    
    private BitmapFont lifeText = new BitmapFont();
    private Animation playerAnimation;
    private float timer = 0;
    private float shootTimer = 0;
    private boolean visible;
    private Projectile projectile;
    public ArrayList<Projectile> projectiles;
    float projVelX = 0;
    float projVelY = 3;
    


    public Player(Sprite sprite, float x, float y, ID id, Controller controller, TiledMap map, View view, String lastPLayerPics) {
        super(x, y, id, sprite, map, view);
        this.controller = controller;
        this.lastPlayerPics = lastPLayerPics;
        collision = new Collision(map, this, view); 
        this.lives = 3;
        this.maxHitPoints = 100;
        this.currentHitPoints = 100;
        visible = true;
        //projectile = new Projectile(x, y, x, y, id, sprite, map, view);
        projectiles = new ArrayList<Projectile>();

        
        setSize(12, 17);

    }

    @Override
    public void draw(Batch batch) {
        update(Gdx.graphics.getDeltaTime());
        super.draw(batch);

    }

    public void setmap(TiledMap tileMap){
        this.map = tileMap;
        collision = new Collision(tileMap, this, view);
    }

    private void update(float deltaTime) {
        
        x += velX;
        y += velY;

        
        //save recent position
        oldX = getX();
        oldY = getY();


        //////////// Controll handling

        if (controller.isUp()){ 
            projVelX=0;
            projVelY=3;
            velY = speed;
            animate(PlayerAnimation.UP.animation, deltaTime);
                    
            // lastPlayerPics = PlayerPics.UP.source;
           
        }
        else if (!controller.isDown()) {
            velY = 0;
        }
        

        if (controller.isDown()) {
            projVelX=0;
            projVelY=-3;
            velY = - speed;
            animate(PlayerAnimation.DOWN.animation, deltaTime);

            // lastPlayerPics = PlayerPics.DOWN.source;
            
        }
        else if (!controller.isUp()) {
            velY = 0;
        }

        if (controller.isRight()) {
            projVelX=3;
            projVelY=0;
            velX = speed;
            animate(PlayerAnimation.RIGHT.animation, deltaTime);

            // lastPlayerPics = PlayerPics.RIGHT.source;
        }
        else if (!controller.isLeft()) {
            velX = 0;
            
        }

        if (controller.isLeft()) {
            projVelX=-3;
            projVelY=0;
            velX = -speed;
            animate(PlayerAnimation.LEFT.animation, deltaTime);
            // lastPlayerPics = PlayerPics.LEFT.source;
        }
        else if (!controller.isRight()) {
            velX = 0;
        }

        if (controller.isFast()) {
            speed = 2;
            try {
            if (controller.isUp()) animate(PlayerAnimation.RUNUP.animation, deltaTime);
            else if (controller.isDown()) animate(PlayerAnimation.RUNDOWN.animation, deltaTime);
            if (controller.isLeft()) animate(PlayerAnimation.RUNLEFT.animation, deltaTime);
            else if (controller.isRight()) animate(PlayerAnimation.RUNRIGHT.animation, deltaTime);
            // playerAnimation.setCycleTime(0.1f);
        } catch (Exception e) {}
        
        }
        else if (!controller.isFast()) {
            speed = 1;
    }

    if (controller.isAttack()) {
        
        if (timer < 1) {
        if(playerAnimation == PlayerAnimation.DOWN.animation || playerAnimation == PlayerAnimation.RUNDOWN.animation){
            animate(PlayerAnimation.ATTACKDOWN.animation, deltaTime);
            // setRegion(new Texture(PlayerPics.ATTACKDOWN.source));
            setScale(2.5f, 2);
            playerAnimation.setCycleTime((1));
             
        }
        if(playerAnimation == PlayerAnimation.UP.animation || playerAnimation == PlayerAnimation.RUNUP.animation){
            setRegion(new Texture(PlayerPics.ATTACKUP.source));
            setScale((float) 1.3,( float) 1.3); 
        }
        if(playerAnimation == PlayerAnimation.LEFT.animation || playerAnimation == PlayerAnimation.RUNLEFT.animation){
            animate(PlayerAnimation.ATTACKLEFT.animation, deltaTime);
            
            // setScale((float) 1.8,(float) 1.8); 
        }
        if(playerAnimation == PlayerAnimation.RIGHT.animation || playerAnimation == PlayerAnimation.RUNRIGHT.animation){
            setRegion(new Texture(PlayerPics.ATTACKRIGHT.source));
            setScale((float) 1.8,(float) 1.8); 
        }
        timer += deltaTime;
    }
    else {
        controller.setAttack(false);
        timer = 0;
    }
        
    }
    
    if (!controller.isAttack()) {
        setScale(1); 
        try {
            setRegion(playerAnimation.getFrame());
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

        //////////// Controll handling ^^^^^^^^^
        

        ////////////// Collision detection 
        setX(getX() + velX * deltaTime);

        if (collision.chechXDirection(velX, oldX)) { 
             x = oldX;
            velX = 0;
        }

        setY(getY() + velY * deltaTime);

        if (collision.chechYDirection(velY, oldY)) {
            y = oldY;
            velY = 0;
        }

        
        if (controller.isAttack()) {
            if(lastPlayerPics==PlayerPics.DOWN.source){
                setTexture(new Texture(PlayerPics.ATTACKDOWN.source));
                setScale((float) 1.8,(float) 1.8); 
            }
            if(lastPlayerPics==PlayerPics.UP.source){
                setTexture(new Texture(PlayerPics.ATTACKUP.source));
                setScale((float) 1.3,( float) 1.3); 
            }
            if(lastPlayerPics==PlayerPics.LEFT.source){
                setTexture(new Texture(PlayerPics.ATTACKLEFT.source));
                setScale((float) 1.8,(float) 1.8); 
            }
            if(lastPlayerPics==PlayerPics.RIGHT.source){
                setTexture(new Texture(PlayerPics.ATTACKRIGHT.source));
                setScale((float) 1.8,(float) 1.8); 
            }
            
        
            
        }
        if (controller.isShoot()){
            if (shootTimer<=0){
                Texture texture = new Texture(PlayerPics.UPARROW.source);
                if (projVelX>0 && projVelY==0){texture = new Texture(PlayerPics.RIGHTARROW.source);}
                if (projVelX<0 && projVelY ==0){texture = new Texture(PlayerPics.LEFTARROW.source);}
                if (projVelX==0 && projVelY >0){texture = new Texture(PlayerPics.UPARROW.source);}
                if (projVelX==0 && projVelY <0){texture = new Texture(PlayerPics.DOWNARROW.source);}
                
                
                Sprite sprite = new Sprite(texture);
                Projectile proj = new Projectile(projVelX,projVelY,this.getX(),this.getY(), id, sprite, map, view);
                view.projectileList.add(proj);
                shootTimer+=0.5;
            } 
            }
           if (shootTimer>0){shootTimer-=deltaTime;
    }


        // TODO må skrive en funskjon som holder følge på hvilke retning spilleren sist beveget seg
        
    }
        
    

    


    //trenger ikke disse?

    public float getOldX() {
        return oldX;
    }
    public float getOldY() {
        return oldY;
    }

    @Override
    public void setOldXNdY(float oldX, float oldY) {
        this.oldX = oldX;
        this.oldY = oldY;
    }
    
    public int getLives() {
        return this.lives;
    }
    

    public void setLives(int newLives) {
        if (newLives < 0) {
            this.lives = 0;
        }
        else {
            this.lives = newLives;
            this.setCurrentHitPoints(this.maxHitPoints);
        }
    }

    @Override
    public void takeDamage(int damage) {
        this.setCurrentHitPoints(this.currentHitPoints - damage);
        if (this.isDead()) {
            this.setLives(this.getLives() - 1);
        }
    }

    public ID getId() {
        return id;
    }

    // changes the textureregion and initialises the animation, also updates the frame.
    private void animate(Animation animation, float dt) {
        playerAnimation = animation;
        setRegion(playerAnimation.getFrame());
        playerAnimation.update(dt); 
    }


    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
}
