/***************************************************************
* file: CameraController.java
* author: Angela Kwok, Kevin Ly, SinHo Cheung
* class: CS 4450 - Computer Graphics
*
* assignment: semester project
* date last modified: 11/25/2020
*
* purpose: Create 'landscape' of Blocks to be displayed
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
import java.nio.FloatBuffer;
import java.util.Random;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Chunk
{
    //instance fields
    
    static final int CHUNK_SIZE = 60;
    static final int CUBE_LENGTH = 2;
    private Block[][][] Blocks;
    private int VBOVertexHandle;
    private int VBOColorHandle;
    private int StartX, StartY, StartZ;
    private Random r;
    private int VBOTextureHandle;
    private Texture texture;
    
    
    //constructor
    public Chunk(int startX, int startY, int startZ)
    {
        try
        {
            texture = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("terrain.png"));
        }
        catch(Exception e)
        {
            System.out.print("Problem loading texture.");
        }
        r = new Random();
        float height[][] = getHeight(startY);
        int type[][] = getType();
        Blocks = new Block[CHUNK_SIZE][CHUNK_SIZE][CHUNK_SIZE];
        for (int x = 0; x < CHUNK_SIZE; x++)
        {
            for (int z = 0; z < CHUNK_SIZE; z++)
            {
                for (int y = 0; y < CHUNK_SIZE; y++)
                {
                    if(y == height[(int)x][(int)z])
                    {
                        if(type[x][z] == 0)
                        {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Grass);
                            //random flora generated
                            if(r.nextFloat() > 0.8)
                            {
                                height[(int)x][(int)z]++;
                                y++;
                                if(r.nextFloat() > 0.96)
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_FairyRing);
                                }
                                else if(r.nextFloat() > 0.72)
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Rose);
                                }
                                else if(r.nextFloat() > 0.58)
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Mushroom);
                                }
                                else if(r.nextFloat() > 0.44)
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Pine);
                                }
                                else if(r.nextFloat() > 0.30)
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Flower);
                                }
                                else if(r.nextFloat() > 0.16)
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sudowoodo);
                                }
                                else
                                {
                                    Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Tree);
                                }
                            }
                        }
                        else if(type[x][z] == 1)
                        {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Water);
                        }
                        else if(type[x][z] == 2)
                        {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Sand);
                            //random flora generated
                            if(r.nextFloat() > 0.9)
                            {
                                height[(int)x][(int)z]++;
                                y++;
                                Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Cactus);
                            }
                        }
                        else
                        {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Default);
                        }
                    }
                    else if(y < 1)
                    {
                        Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Bedrock);
                    }
                    else
                    {
                        if(r.nextFloat() > 0.75f)
                        {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Stone);
                        }
                        else
                        {
                            Blocks[(int)x][(int)y][(int)z] = new Block(Block.BlockType.BlockType_Dirt);
                        }
                    }
                }
            }
        }
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        StartX = startX;
        StartY = startY;
        StartZ = startZ;
        rebuildMesh(startX, startY, startZ, height);
    }
    
    //method: render
    //purpose: make and show image to be seen
    public void render()
    { 
        glPushMatrix();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glVertexPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glColorPointer(3, GL_FLOAT, 0, 0L);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBindTexture(GL_TEXTURE_2D, 1);
        glTexCoordPointer(2,GL_FLOAT,0,0L);
        glDrawArrays(GL_QUADS, 0, CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 24);
        glPopMatrix();
    }
    
    //method: rebuildMesh
    //purpose: accept start point and create cubes
    public void rebuildMesh(float startX, float startY, float startZ, float[][] height)
    {
        VBOColorHandle = glGenBuffers();
        VBOVertexHandle = glGenBuffers();
        VBOTextureHandle = glGenBuffers();
        FloatBuffer VertexPositionData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexColorData = BufferUtils.createFloatBuffer(CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE * 6 * 12);
        FloatBuffer VertexTextureData = BufferUtils.createFloatBuffer((CHUNK_SIZE * CHUNK_SIZE * CHUNK_SIZE) * 6 * 12);
      
        for(float x = 0; x < CHUNK_SIZE; x += 1)
        {
            for (float z = 0; z < CHUNK_SIZE; z += 1)
            {
                for(float y = 0; y <= height[(int)x][(int)z]; y += 1)
                {
                    VertexPositionData.put(createCube((float)(startX + x * CUBE_LENGTH), 
                            (float)(y * CUBE_LENGTH + (CHUNK_SIZE * 0.8)),
                            (float)(startZ + z * CUBE_LENGTH)));
                    VertexColorData.put(createCubeVertexCol(getCubeColor(Blocks[(int)x][(int)y][(int)z])));
                    VertexTextureData.put(createTexCube((float)0, (float)0,
                            Blocks[(int)(x)][(int)(y)][(int)(z)]));
                }
            }
        }
        
        VertexColorData.flip();
        VertexPositionData.flip();
        VertexTextureData.flip();
        glBindBuffer(GL_ARRAY_BUFFER, VBOVertexHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexPositionData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOColorHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexColorData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindBuffer(GL_ARRAY_BUFFER, VBOTextureHandle);
        glBufferData(GL_ARRAY_BUFFER, VertexTextureData, GL_STATIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }
    
    //method: getHeight
    //purpose: return 2D float array with height of landscape
    private float[][] getHeight(float startY)
    {
        float height;
        int largestFeature = r.nextInt(200) + 100;
        int seed = r.nextInt();
        SimplexNoise noise = new SimplexNoise(largestFeature, 0.1f, seed);
        float[][] array = new float[CHUNK_SIZE][CHUNK_SIZE];
        for(float x = 0; x < CHUNK_SIZE; x += 1)
        {
            for (float z = 0; z < CHUNK_SIZE; z += 1)
            {
                
                height = (float)(startY + (int)(100 * noise.getNoise((int)x, (int)z)));
                if(height < 0)
                {
                    height *= -1;
                }
                if (height == 0)
                {
                    height += 1;
                }
                array[(int)x][(int)z] = height;
            }
        }
        return array;
    }
    
    //method: getType
    //purpose: return 2D int array with type of block for the landscape
    private int[][] getType()
    {
        int type = r.nextInt(3);
        int[][] array = new int[CHUNK_SIZE][CHUNK_SIZE];
        for(int x = 0; x < CHUNK_SIZE; x += 3)
        {
            for (int z = 0; z < CHUNK_SIZE; z += 2)
            {
                if(x > 0)
                {
                    type = array[x][z];
                }
                if(r.nextFloat() > 0.50f)
                {
                    type = r.nextInt(3);
                }
                array[x][z] = type;
                array[x][z + 1] = type;
                array[x + 1][z] = type;
                array[x + 1][z + 1] = type;
                array[x + 2][z] = type;
                array[x + 2][z + 1] = type;
                if(r.nextFloat() > 0.5f && x > 0)
                {
                    array[x][z + 1] = array[x - 1][z + 1];
                }
                if(r.nextFloat() > 0.5f && x > 0)
                {
                    array[x][z] = array[x - 1][z];
                }
            }
        }
        //generate a river
        if(r.nextFloat() > 0.7)
       {
          int x = r.nextInt(56);
          for(int i = x; i < x + 4; i++)
          {
             for(int y = 0; y < CHUNK_SIZE; y++)
             {
                array[i][y] = 1;
             }
          } 
       }
        return array;
    }
    
    //method: createCubeVertexCol
    //purpose: return a float array of cube colors
    private float[] createCubeVertexCol(float[] CubeColorArray)
    {
        float cubeColors[] = new float[CubeColorArray.length * 4 * 6];
        for(int i = 0; i < cubeColors.length; i++)
        {
            cubeColors[i] = CubeColorArray[i % CubeColorArray.length];
        }
        return cubeColors;
    }
    
    //method: createCube
    //purpose: returns a float array containing the vertices of the cube created
    public static float[] createCube(float x, float y, float z)
    {
        int offset = CUBE_LENGTH / 2;
        return new float[]{
        //top
            x + offset, y + offset, z,
            x - offset, y + offset, z,
            x - offset, y + offset, z - CUBE_LENGTH,
            x + offset, y + offset, z - CUBE_LENGTH,
        //bottom
            x + offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x - offset, y - offset, z,
            x + offset, y - offset, z,
        //front
            x + offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
        //back
            x + offset, y - offset, z,
            x - offset, y - offset, z,
            x - offset, y + offset, z,
            x + offset, y + offset, z,
        //left
            x - offset, y + offset, z - CUBE_LENGTH,
            x - offset, y + offset, z,
            x - offset, y - offset, z,
            x - offset, y - offset, z - CUBE_LENGTH,
        //right
            x + offset, y + offset, z,
            x + offset, y + offset, z - CUBE_LENGTH,
            x + offset, y - offset, z - CUBE_LENGTH,
            x + offset, y - offset, z
        };
    }
    
    //method: getCubeColor
    //purpose: return color of cube as float array
    private float[] getCubeColor(Block block)
    {
        /*switch(block.GetID())
        {
            case 1:
                return new float[]{0, 1, 0};
            case 2:
                return new float[]{1, 0.5f, 0};
            case 3:
                return new float[]{0, 0f, 1f};
        }*/
        return new float[]{1, 1, 1};
    }
    
    //method: createTexCube
    //purpose: return float array with information about texture of cubes
    private static float[] createTexCube(float x, float y, Block block)
    {
        float offset = (1024f / 16) / 1024f;
        switch (block.GetID())
        {
            case 0: //grass
                return new float[] {
                // TOP
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // BOTTOM QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // BACK QUAD
                x + offset*3, y + offset*1,
                x + offset*4, y + offset*1,
                x + offset*4, y + offset*0, 
                x + offset*3, y + offset*0,
                // LEFT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1,
                // RIGHT QUAD
                x + offset*3, y + offset*0,
                x + offset*4, y + offset*0,
                x + offset*4, y + offset*1, 
                x + offset*3, y + offset*1};
            case 1: //sand
                return new float[] {
                // TOP!
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // FRONT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // BACK QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // LEFT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // RIGHT QUAD
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2};
            case 2: //water
                return new float[] {
                // TOP!
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // FRONT QUAD
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // BACK QUAD
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // LEFT QUAD
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12,
                // RIGHT QUAD
                x + offset*1, y + offset*11,
                x + offset*2, y + offset*11,
                x + offset*2, y + offset*12,
                x + offset*1, y + offset*12};
            case 3: //dirt
                return new float[] {
                // TOP!
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // BACK QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // LEFT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // RIGHT QUAD
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1};
            case 4: //stone
                return new float[] {
                // TOP!
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // FRONT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // BACK QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // LEFT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2,
                // RIGHT QUAD
                x + offset*0, y + offset*1,
                x + offset*1, y + offset*1,
                x + offset*1, y + offset*2,
                x + offset*0, y + offset*2};
            case 5: //bedrock
            return new float[] {
                // TOP!
                x + offset*12, y + offset*9,
                x + offset*13, y + offset*9,
                x + offset*13, y + offset*10,
                x + offset*12, y + offset*10,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*12, y + offset*9,
                x + offset*13, y + offset*9,
                x + offset*13, y + offset*10,
                x + offset*12, y + offset*10,
                // FRONT QUAD
                x + offset*12, y + offset*9,
                x + offset*13, y + offset*9,
                x + offset*13, y + offset*10,
                x + offset*12, y + offset*10,
                // BACK QUAD
                x + offset*12, y + offset*9,
                x + offset*13, y + offset*9,
                x + offset*13, y + offset*10,
                x + offset*12, y + offset*10,
                // LEFT QUAD
                x + offset*12, y + offset*9,
                x + offset*13, y + offset*9,
                x + offset*13, y + offset*10,
                x + offset*12, y + offset*10,
                // RIGHT QUAD
                x + offset*12, y + offset*9,
                x + offset*13, y + offset*9,
                x + offset*13, y + offset*10,
                x + offset*12, y + offset*10};
            case 7: //rose
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*12, y + offset*0,
                x + offset*13, y + offset*0,
                x + offset*13, y + offset*1,
                x + offset*12, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            case 8: //pine
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*15, y + offset*3,
                x + offset*16, y + offset*3,
                x + offset*16, y + offset*4,
                x + offset*15, y + offset*4,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            case 9: //cactus
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*1,
                x + offset*3, y + offset*1,
                x + offset*3, y + offset*2,
                x + offset*2, y + offset*2,
                // FRONT QUAD
                x + offset*7, y + offset*3,
                x + offset*8, y + offset*3,
                x + offset*8, y + offset*4,
                x + offset*7, y + offset*4,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            case 10: //mushroom
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*13, y + offset*1,
                x + offset*14, y + offset*1,
                x + offset*14, y + offset*2,
                x + offset*13, y + offset*2,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            case 11: //flower
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*13, y + offset*0,
                x + offset*14, y + offset*0,
                x + offset*14, y + offset*1,
                x + offset*13, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            case 12: //fairy ring
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*12, y + offset*1,
                x + offset*13, y + offset*1,
                x + offset*13, y + offset*2,
                x + offset*12, y + offset*2,
                // BACK QUAD
                x + offset*12, y + offset*2,
                x + offset*13, y + offset*2,
                x + offset*13, y + offset*1,
                x + offset*12, y + offset*1,
                // LEFT QUAD
                x + offset*12, y + offset*1,
                x + offset*13, y + offset*1,
                x + offset*13, y + offset*2,
                x + offset*12, y + offset*2,
                // RIGHT QUAD
                x + offset*12, y + offset*1,
                x + offset*13, y + offset*1,
                x + offset*13, y + offset*2,
                x + offset*12, y + offset*2};
            case 13: //tree
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*15, y + offset*0,
                x + offset*16, y + offset*0,
                x + offset*16, y + offset*1,
                x + offset*15, y + offset*1,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            case 14: //sudowoodo
            return new float[] {
                // TOP!
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*1, y + offset*10,
                x + offset*2, y + offset*10,
                x + offset*2, y + offset*9,
                x + offset*1, y + offset*9,
                // FRONT QUAD
                x + offset*14, y + offset*1,
                x + offset*15, y + offset*1,
                x + offset*15, y + offset*2,
                x + offset*14, y + offset*2,
                // BACK QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // LEFT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12,
                // RIGHT QUAD
                x + offset*4, y + offset*11,
                x + offset*5, y + offset*11,
                x + offset*5, y + offset*12,
                x + offset*4, y + offset*12};
            default: 
                return new float[] {
                // TOP!
                x + offset*2, y + offset*4,
                x + offset*3, y + offset*4,
                x + offset*3, y + offset*5,
                x + offset*2, y + offset*5,
                // BOTTOM QUAD(DOWN=+Y)
                x + offset*2, y + offset*0,
                x + offset*3, y + offset*0,
                x + offset*3, y + offset*1,
                x + offset*2, y + offset*1,
                // FRONT QUAD
                x + offset*4, y + offset*4,
                x + offset*5, y + offset*4,
                x + offset*5, y + offset*5,
                x + offset*4, y + offset*5,
                // BACK QUAD
                x + offset*4, y + offset*5,
                x + offset*5, y + offset*5,
                x + offset*5, y + offset*4,
                x + offset*4, y + offset*4,
                // LEFT QUAD
                x + offset*4, y + offset*4,
                x + offset*5, y + offset*4,
                x + offset*5, y + offset*5,
                x + offset*4, y + offset*5,
                // RIGHT QUAD
                x + offset*4, y + offset*4,
                x + offset*5, y + offset*4,
                x + offset*5, y + offset*5,
                x + offset*4, y + offset*5};
        }
    }
}
