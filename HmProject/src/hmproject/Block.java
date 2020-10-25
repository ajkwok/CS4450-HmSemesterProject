/***************************************************************
* file: Block.java
* author: Angela Kwok, Kevin Ly, SinHo Cheung
* class: CS 4450 - Computer Graphics
*
* assignment: semester project
* date last modified: 10/24/2020
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
        BlockType_Default(6);
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
