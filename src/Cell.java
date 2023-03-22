public class Cell
{
    private int x; //coordinate X
    private int y; //coordinate Y
    private int value; //cost of cell [0,4] or -1 if obstacle
    private double cost; //cost from start point
    private Cell parent; //parent of child in cost
    private Cell[] neighbors; //free neighboring cells (max is 8)
    
    
    public Cell(int x, int y) 
    {
        this.x = x;
        this.y = y;
        this.neighbors = new Cell[8];
    }
    public void putValue(int val){
        this.value = val;
    }
    public int getValue(){
        return this.value;
    }
    public int getX() //return coordinate X
    {
        return this.x;
    }
    public int getY() //return coordinate Y
    {
        return this.y;
    }
    public Cell getNeighbor(int i){
        return this.neighbors[i];
    }
    public double getCost()
    {
        return cost;
    }
	public void setCost(double cost){
		this.cost = cost;
	}
    public Cell getParent()
    {
        return parent;
    }
    public void setParent(Cell p)
    {
        this.parent = p;
    }    
    public int hasNeighbors(){
        int count = 0;
        for(int i=0;i<8;i++){
            if(this.neighbors[i]!=null){
                count++;
            }
        }
        return count;
    }
    
    public void addNeighbor(Cell cell){
        if(this.value < 0){
            return;
        }
        int i=0;
        while(this.neighbors[i] != null){
            i++;
        }
        if(cell.getValue()>0){
            this.neighbors[i] = cell;
        }
        return;
    }
}