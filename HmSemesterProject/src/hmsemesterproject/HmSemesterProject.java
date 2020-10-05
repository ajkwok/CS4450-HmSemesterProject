/***************************************************************
* file: HmSemesterProject.java
* author: Angela Kwok, Kevin Ly, SinHo Cheung
* class: CS 4450 - Computer Graphics
*
* assignment: semester project
* date last modified: 10/04/2020
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

package hmsemesterproject;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import static org.lwjgl.opengl.GL11.*;
import java.util.*;
import java.io.*;

public class HmSemesterProject
{

    public static void main(String[] args)
    {
        // start();
    }
    
    //method:start
    //purpose:accepts list of shapes and setsup/displays the window as specified
    private static void start()
    {
        try{
            createWindow();
            initializeGL();
            render();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    //method: createWindow
    //purpose: create window of appropriate size, not fullscreen
    private static void createWindow() throws Exception 
    {
        Display.setFullscreen(false);
        Display.setDisplayMode(new DisplayMode(640, 480));
        Display.setTitle("Computer Graphics - Semester Project (Hm)");
        Display.create();
    }
    
    //method: initializeGL
    //purpose: set background of window to black, orient window to coordinates
    private static void initializeGL()
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        
        glOrtho(0, 640, 0, 480, 1, -1);
        
        glMatrixMode(GL_MODELVIEW);
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
    }
    
    //method: render
    //purpose: close window when appropriate when esc key is pressed
    private static void render()
    {
        try
        {
            Keyboard.create();
        }
        catch(Exception e)
        {
            System.out.println("Keyboard creation failed.");
            System.exit(0);
        }
        while(!Display.isCloseRequested()&& !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
        {
            try
            {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
                glLoadIdentity();
                
                
                Display.update();
                Display.sync(60);
            } 
            catch(Exception e)
            {
            }
        }
        Display.destroy();
    }
    
}
