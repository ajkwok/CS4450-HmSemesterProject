/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hmproject;


public class Block {
    private boolean isActive;
    private BlockType type;
    private float x, y, z;
    public enum BlockType {
        BLOCKTYPE_DEFAULT(0),
        BLOCKTYPE_GRASS(1),
        BLOCKTYPE_WATER(2),
        BLOCKTYPE_STONE(3),
        BLOCKTYPE_SAND(4),
        BLOCKTYPE_DIRT(5),
        BLOCKTYPE_WOOD(6),
        BLOCKTYPE_NUMTYPE(7);
           
        
        private int blockID;
        
        BlockType(int i){
            blockID = i;
        }
      
        public int getID(){
            return blockID;
        }

        
    }
    public Block(BlockType type){
        this.type = type;
    }
    
    public void setCoords(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
        
    public void isActive(boolean active) {
        isActive = active;
    }
  
    public int getID() {
        return type.getID();
    }
}
