/***************************************************************
* file: HmSemesterProject.java
* author: Angela Kwok, Kevin Ly, SinHo Cheung
* class: CS 4450 - Computer Graphics
*
* assignment: semester project
* date last modified: 10/07/2020
*
* purpose: Use the LWJGL library to draw a window of 640x480 in the center of
* the screen. 
* 
* Checkpoint 1:
*  - Display a cube in 3D space, with a different color on each face
*  - Should be able to manipulate camera with mouse
*  - Program should use the input.Keyboard class to have the 
*    escape key quit your application.
*  - Should be controllable by arrow keys and w,a,s,d to control camera, as well
*    as space for up and left shift for down
*
****************************************************************/ 
package hmproject;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import org.lwjgl.Sys;
import org.lwjgl.util.glu.GLU;

public class HmProject {
     private CameraController cc = new CameraController(0f, 0f, 0f);
    private DisplayMode displayMode;
    
    public static void main(String[] args)
    {
        HmProject hmproject = new  HmProject();
        hmproject.start();
    }
    
    //method:start
    //purpose:creates window and initializes GL
    public void start()
    {
        
        try{
            createWindow();
            initializeGL();
            cc.gameLoop();
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose: create window of appropriate size, not fullscreen
    private void createWindow() throws Exception 
    {
        Display.setFullscreen(false);
        DisplayMode d[] = Display.getAvailableDisplayModes();
        for(int i = 0; i < d.length; i++){
            if(d[i].getWidth() == 640 && d[i].getHeight() == 480 && d[i].getBitsPerPixel() == 32){
                displayMode = d[i];
                break;
            }
        }
        Display.setDisplayMode(displayMode);
        Display.setTitle("Camera Simulation");
        Display.create();
    }
    
    //method: initializeGL
    //purpose: set background of window to black, orient window to coordinates
    private void initializeGL()
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        GLU.gluPerspective(100.0f, (float)displayMode.getWidth()/(float)displayMode.getHeight(), 0.1f, 300.0f);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
}
