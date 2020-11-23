/***************************************************************
* file: CameraController.java
* author: Angela Kwok, Kevin Ly, SinHo Cheung
* class: CS 4450 - Computer Graphics
*
* assignment: semester project
* date last modified: 11/23/2020
*
* purpose: Allow user to control camera and show cube 
* 
* Checkpoint 1:
*  - Display a cube in 3D space, with a different color on each face
*  - Should be able to manipulate camera with mouse
*  - Program should use the input.Keyboard class to have the 
*    escape key quit your application.
*  - Should be controllable by arrow keys and w,a,s,d to control camera, as well
*    as space for up and left shift for down
* Checkpoint 2:
*  - Draw multiple cubes using chunks method
*  - Create a world at least 30 cubes by 30 cubes
*     - Cubes should be textured; Minimum of six(6) cube types
*        - Grass, Sand, Water, Dirt, Stone, and Bedrock
*     - Cubes should be randomly placed using simplex noise classes
*        - Smooth rise and fall; No sudden mountains or valleys
* Checkpoint 3:
*  - layer the types of terrain
*     - top layer: grass, water, sand, and default
*     - middle layer: dirt, stone
*     - bottom layer: bedrock
*  - light source (half world brightly lit, other half dimly illuminated)
*  Final Checkpoint:
*  - Three Additional features 
*     1. random river generation/ terrain grouping
*        - only one river may be generated
*        - river is straight, no curving
*     2. prevent player from moving out of bounds
*        - the player will be repositioned to the center
*          if they move out of x and z boundaries
*     3. floral generation
*        - randomly generated
*        - cactus only in sand
*        - everything else only on grass (mushrooms, flowers, trees, etc.)
*        - added transparency/blending
*
****************************************************************/ 
package hmproject;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;

public class CameraController {
    private Vector3f position = null;
    private Vector3f lookPosition = null;
    static final int CHUNK_SIZE = 60;
    private Chunk chunk;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    
    //method: CameraController
    //purpose: constructor for Camera Controller
    public CameraController(float x, float y, float z){
        position = new Vector3f(x, y, z);
        lookPosition = new Vector3f(x, y, z);
        lookPosition.x = 0f;
        lookPosition.y = 15f;
        lookPosition.z = 0f;
    }
    
    
    //method:yaw
    //purpose: change the camera yaw rotation
    public void yaw(float amount){
        yaw += amount;
    }
    //method:pitch
    //purpose: change camera pitch rotation
    public void pitch(float amount){
        pitch -= amount;
    }
    //method:moveForward
    //purpose:moves camera forward relative to yaw rotation
    public void moveForward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x-=xOffset).put(
        lookPosition.y).put(lookPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        
        if(position.x >= (-1*(CHUNK_SIZE)) && position.x <= CHUNK_SIZE && position.z >= (-1 *(CHUNK_SIZE)) && position.z <= CHUNK_SIZE){
            position.x -= xOffset;
            position.z += zOffset;
        }
        else {
            position.x = 0;
            position.z = 0;
        }
        
       
    
    }
    
    
    //method:moveBackward
    //purpose: moves camera backwards relative to yaw rotation 
     public void moveBackward(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw));
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x-=xOffset).put(
        lookPosition.y).put(lookPosition.z+=zOffset).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        
        if(position.x >= (-1*(CHUNK_SIZE)) && position.x <= CHUNK_SIZE && position.z >= (-1 *(CHUNK_SIZE)) && position.z <= CHUNK_SIZE){
            position.x += xOffset;
            position.z -= zOffset;
        }
        else {
            position.x = 0;
            position.z = 0;
        }
         
     
             
        
    }
    //method:moveUp
     //purpose: moves camera up from it's current position
    public void moveUp(float distance){  
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x).put(
        lookPosition.y).put(lookPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        position.y -= distance;
    }
    //method: moveDown
    //purpose: moves camera down from it's current position
    public void moveDown(float distance){    
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x).put(
        lookPosition.y).put(lookPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
        position.y += distance;
    }
    //method:strafeLeft
    //purpose: strafes camera left relative to it's current rotation
    public void strafeLeft(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw - 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw - 90));
        
        if(position.x >= (-1*(CHUNK_SIZE)) && position.x <= CHUNK_SIZE && position.z >= (-1 *(CHUNK_SIZE)) && position.z <= CHUNK_SIZE){
            position.x -= xOffset;
            position.z += zOffset;
        }
        else {
            position.x = 0;
            position.z = 0;
        }
    }
    //method:strafeRight
    //purpose: strafes camera right relative to it's current rotation
    public void strafeRight(float distance){
        float xOffset = distance * (float)Math.sin(Math.toRadians(yaw + 90));
        float zOffset = distance * (float)Math.cos(Math.toRadians(yaw + 90));
        
        
        if(position.x >= (-1*(CHUNK_SIZE)) && position.x <= CHUNK_SIZE && position.z >= (-1 *(CHUNK_SIZE)) && position.z <= CHUNK_SIZE){
            position.x -= xOffset;
            position.z += zOffset;
        }
        else {
            position.x = 0;
            position.z = 0;
        }
    }
    //method:lookThrough
    //purpose:translates and rotates the matrix so it looks through the camera
    public void lookThrough(){
        glRotatef(pitch, 1.0f, 0.0f, 0.0f);
        glRotatef(yaw, 0.0f, 1.0f, 0.0f);
        glTranslatef(position.x, position.y - 110.0f, position.z);      
        
        FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
        lightPosition.put(lookPosition.x).put(
        lookPosition.y).put(lookPosition.z).put(1.0f).flip();
        glLight(GL_LIGHT0, GL_POSITION, lightPosition);
    }
    
    //method:render
    //purpose:draw cube
    private void render() {
        try {
            
            glEnable(GL_DEPTH_TEST);
            glDepthFunc(GL_LESS);
            glBegin(GL_QUADS);
            
                glColor3f(1f, 0f, 0f); //red color
                glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
                glVertex3f( -1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
                glVertex3f( -1.0f, 1.0f, 1.0f ); // Bottom Left Of The Quad (Top)
                glVertex3f( 1.0f, 1.0f, 1.0f ); // Bottom Right Of The Quad (Top)

                glColor3f( 0.5f, 1f, 0.5f ); //green color (yellow-green)
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

                glColor3f( 0.5f, 0f, 1f ); //purple (red + blue)
                glVertex3f( -1.0f, 1.0f, 1.0f ); // Top Right Of The Quad (Left)
                glVertex3f( -1.0f, 1.0f, -1.0f ); // Top Left Of The Quad (Left)
                glVertex3f( -1.0f, -1.0f, -1.0f ); // Bottom Left Of The Quad
                glVertex3f( -1.0f, -1.0f, 1.0f ); // Bottom Right Of The Quad 

                glColor3f( 0f, 0.9f, 0.9f ); //sky blue (blue + green)
                glVertex3f( 1.0f, 1.0f, -1.0f ); // Top Right Of The Quad (Right)
                glVertex3f( 1.0f, 1.0f, 1.0f ); // Top Left Of The Quad
                glVertex3f( 1.0f, -1.0f, 1.0f ); // Bottom Left Of The Quad
                glVertex3f( 1.0f, -1.0f, -1.0f ); // Bottom Right Of The Quad
            glEnd();
            
        }
        catch(Exception e){
            
        }
    }
   
    //method:gameLoop
    //purpose:set up controls and render image to be seen
    public void gameLoop()
    {
        CameraController camera = new CameraController(-60, 0, 0);
        float dx = 0.0f;
        float dy = 0.0f;
        float dt = 0.0f;
        float lastTime = 0.0f;
        long time = 0;
        chunk = new Chunk(-60, 0, -60);
        
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
            camera.pitch(dy * mouseSensitivity);
            
            
            //Moves Camera Position using the WASD keys
            if(Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_UP)){
                camera.moveForward(moveSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
                camera.moveBackward(moveSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
                camera.strafeLeft(moveSpeed);
            }
            if(Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
                camera.strafeRight(moveSpeed);
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)){
                camera.moveUp(moveSpeed);
            }

            if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)){
                camera.moveDown(moveSpeed);
            }
            
            glLoadIdentity();
            
          
            camera.lookThrough();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
          
            //render();
            chunk.render();
            
            Display.update();
            Display.sync(60);
  
        }
        Display.destroy();
    }
}
