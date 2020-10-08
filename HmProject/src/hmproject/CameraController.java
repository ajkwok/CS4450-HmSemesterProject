/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmproject;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.vector.Vector3f;
/**
 *
 * @author kevin
 */
public class CameraController {
     private Vector3f position = null;
    private Vector3f lookPosition = null;
    
    
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    
  
    public CameraController(float x, float y, float z){
        position = new Vector3f(x, y, z);
        lookPosition = new Vector3f(x, y, z);
        lookPosition.x = 0f;
        lookPosition.y = 15f;
        lookPosition.z = 0f;
    }
    
    public void yaw(float amount){
        yaw += amount;
    }
    
    public void pitch(float amount){
        pitch -= amount;
    }
    
    public void moveForward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x -= xOffset;
        position.z += zOffset;
    }
     public void moveBackward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        position.x += xOffset;
        position.z -= zOffset;
    }
    
    public void moveUp(float distance){
        position.y -= distance;
    }
    
    public void moveDown(float distance){
        position.y += distance;
    }
    
    public void strafeLeft(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw - 90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    public void strafeRight(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        position.x -= xOffset;
        position.z += zOffset;
    }
    
    public void lookThrough(){
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y, position.z);
    }
    
    
    private void render() {
        try {
            glBegin(GL_QUADS);
            
                glColor3f(1f, 0f, 0f); //red color
                glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
                glVertex3f( -1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
                glVertex3f( -1.0f, 1.0f, 1.0f ); // Bottom Left Of The Quad (Top)
                glVertex3f( 1.0f, 1.0f, 1.0f ); // Bottom Right Of The Quad (Top)

                glColor3f( 0f, 1f, 0f ); //green color
                glVertex3f( 1.0f, -1.0f, 1.0f ); // Top Right Of The Quad
                glVertex3f( -1.0f, -1.0f, 1.0f ); // Top Left Of The Quad
                glVertex3f( -1.0f, -1.0f, -1.0f ); // Bottom Left Of The Quad
                glVertex3f( 1.0f, -1.0f, -1.0f ); // Bottom Right Of The Quad 

                glColor3f( 0f, 0f, 1f ); //blue color
                glVertex3f( 1.0f, 1.0f, 1.0f ); // Top Right Of The Quad (Front)
                glVertex3f( -1.0f, 1.0f, 1.0f ); // Top Left Of The Quad (Front)
                glVertex3f( -1.0f, -1.0f, 1.0f ); // Bottom Left Of The Quad
                glVertex3f( 1.0f, -1.0f, 1.0f ); // Bottom Right Of The Quad 

                glColor3f( 1f, 1f, 0f ); //yellow (red + green)
                glVertex3f( 1.0f, -1.0f, -1.0f ); // Bottom Left Of The Quad
                glVertex3f( -1.0f, -1.0f, -1.0f ); // Bottom Right Of The Quad
                glVertex3f( -1.0f, 1.0f, -1.0f ); // Top Right Of The Quad (Back)
                glVertex3f( 1.0f, 1.0f, -1.0f ); // Top Left Of The Quad (Back)

                glColor3f( 1f, 0f, 1f ); //purple (red + green)
                glVertex3f( -1.0f, 1.0f, 1.0f ); // Top Right Of The Quad (Left)
                glVertex3f( -1.0f, 1.0f, -1.0f ); // Top Left Of The Quad (Left)
                glVertex3f( -1.0f, -1.0f, -1.0f ); // Bottom Left Of The Quad
                glVertex3f( -1.0f, -1.0f, 1.0f ); // Bottom Right Of The Quad 

                glColor3f( 0f, 1f, 1f ); //sky blue (blue +green)
                glVertex3f( 1.0f, 1.0f, -1.0f ); // Top Right Of The Quad (Right)
                glVertex3f( 1.0f, 1.0f, 1.0f ); // Top Left Of The Quad
                glVertex3f( 1.0f, -1.0f, 1.0f ); // Bottom Left Of The Quad
                glVertex3f( 1.0f, -1.0f, -1.0f ); // Bottom Right Of The Quad
            glEnd();
        }
        catch(Exception e){
            
        }
    }
    
    public void gameLoop()
    {
        CameraController camera = new CameraController(0,0,0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;
        float lastTime = 0.0f;
        long time = 0;
        
        float mouseSensitivity = 0.09f;
        float moveSpeed = 0.35f;
        
        //hides mouse
        Mouse.setGrabbed(true);
        
        while(!Display.isCloseRequested()&& !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {

            time = Sys.getTime();
            lastTime = time;
            dx = Mouse.getDX();
            dy = Mouse.getDY();

            camera.yaw(dx * mouseSensitivity);
            camera.yaw(dy * mouseSensitivity);

            //Moves Camera Position using the WASD keys
            if(Keyboard.isKeyDown(Keyboard.KEY_W)){
                camera.moveForward(moveSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S)){
                camera.moveBackward(moveSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A)){
                camera.strafeLeft(moveSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D)){
                camera.strafeRight(moveSpeed);
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(moveSpeed);
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_E)){
                camera.moveDown(moveSpeed);
            }
            
            glLoadIdentity();
            
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            
            render();
            
            Display.update();
            Display.sync(60);
  
        }
        Display.destroy();
    }
}
