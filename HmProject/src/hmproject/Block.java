/***************************************************************
* file: Block.java
* author: Angela Kwok, Kevin Ly, SinHo Cheung
* class: CS 4450 - Computer Graphics
*
* assignment: semester project
* date last modified: 11/23/2020
*
* purpose: object class for a block/cube
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

public class Block {
    private boolean IsActive;
    private BlockType Type;
    private float x,y,z;
    
    public enum BlockType {
        BlockType_Grass(0),
        BlockType_Sand(1),
        BlockType_Water(2),
        BlockType_Dirt(3),
        BlockType_Stone(4),
        BlockType_Bedrock(5),
        BlockType_Default(6),
        BlockType_Rose(7),
        BlockType_Pine(8),
        BlockType_Cactus(9),
        BlockType_Mushroom(10),
        BlockType_Flower(11),
        BlockType_FairyRing(12),
        BlockType_Tree(13),
        BlockType_Sudowoodo(14);
        private int BlockID;
        
        BlockType(int i) {
            BlockID=i;
        }
        public int GetID(){
            return BlockID;
        }
        public void SetID(int i){
            BlockID = i;
        }
    }
    
    //constructor
    public Block(BlockType type){
        Type= type;
    }
    
    //method: setCoords
    //purpose: set coordinates of block
    public void setCoords(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    //method: IsActive
    //purpose: returns boolean
    public boolean IsActive() {
        return IsActive;
    }
    
    //method: SetActive
    //purpose: set IsActive variable
    public void SetActive(boolean active){
        IsActive=active;
    }
    
    //method: GetID
    //purpose: returns int indicating what type of terrain the block is
    public int GetID(){
        return Type.GetID();
    }
}
