import java.util.Random;
import java.util.ArrayList;
import java.lang.Math;
import java.util.Scanner;
import java.util.Date;


public class maze
{
    private int size; //size of maze
    private Cell[][] myMaze; //storage of all Cells

    public maze(int N,int p) //create new Maze
    {
        this.size = N; //set size
        this.myMaze = new Cell[N][N]; //create new NxN list
		Random rand = new Random();
        for(int i=0;i<N;i++) // coordinates X
        {
            for(int j=0;j<N;j++) // coordinates Y
            {
                int  r = rand.nextInt(10)+1; //r for obstacle/free space
                if(r<=p) //if r lesser than probability of obstacle
                {
					myMaze[i][j] = new Cell(i,j); //create cell object
                    myMaze[i][j].putValue(-1); //put -1 (<0 so obstacle)
                }
                else
                {
					myMaze[i][j] = new Cell(i,j); //create cell object
                    myMaze[i][j].putValue(rand.nextInt(4)+1); //put value [0,3] + 1 -> [1,4]
                }
				if(i>0){ //if there is neighbor to the left horizontally
					if(j>0){ //if there is neighbor to the left vertically
						myMaze[i][j].addNeighbor(myMaze[i-1][j-1]); //add up right neighbor
						myMaze[i][j].addNeighbor(myMaze[i-1][j]); //add right neighbor
						myMaze[i][j].addNeighbor(myMaze[i][j-1]); //add up neighbor
						myMaze[i-1][j-1].addNeighbor(myMaze[i][j]); //up right neighbor adds you
						myMaze[i-1][j].addNeighbor(myMaze[i][j]); //right neighbor adds you
						myMaze[i][j-1].addNeighbor(myMaze[i][j]); //up neighbor adds you
					}else{
						myMaze[i][j].addNeighbor(myMaze[i-1][j]); //add right neighbor
						myMaze[i-1][j].addNeighbor(myMaze[i][j]); //right neighbor adds you
					}
				}else{
					if(j>0){
						myMaze[i][j].addNeighbor(myMaze[i][j-1]); //add up neighbor
						myMaze[i][j-1].addNeighbor(myMaze[i][j]); //up neighbor adds you
					}
				}
            }
        }    
    }
	public Cell cell(int x, int y){
		return this.myMaze[x][y];
	}
	public int val(int row,int column){ //return value of node
        return myMaze[row][column].getValue();
    }
	public double move(Cell c1, Cell c2) //calculate cost of movement
	{
		int x1 = c1.getX();
		int y1 = c1.getY();
		int x2 = c2.getX();
		int y2 = c2.getY();
		if(val(x1,y1)<0 || val(x2,y2)<0){ //if obstacle return 0 (since each move costs >0.5
			return 0;
		}
		double D = Math.abs(val(x1,y1) - val(x2,y2)); //calculate D cost
		if(x1==x2){ //if horizontal return d + 1
			return D + 1;
		}
		if(y1==y2){ //if vertical return d + 1
			return D + 1;
		}
		return D + 0.5; //if diagonal return d + 0.5
	}
	public ArrayList<Cell> ucs(Cell S,Cell G1,Cell G2)
	{
		ArrayList<Cell> openedList = new ArrayList<Cell>(); // a list that contains the nodes that are possible to be selected
		ArrayList<Cell> closedList = new ArrayList<Cell>(); // a list that contained the nodes that have already been selected
		ArrayList<Cell> path = new ArrayList<Cell>();  // contains the optimal path 
		openedList.add(S); // we add the starting node S to the opened List
		int found = 0;
		while(true){
			if(openedList.isEmpty()){
				if(found > 0){
					if(G1.getParent() != null){ // if G1 has a parent
						Cell parent = G1;  // G1 now is the parent
						path.add(parent); // we add to the path
						while(parent != null){  // when we reach parent = null, that means tha we are in the starting Node S, so we have the full path we needed.
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
					else if(G2.getParent() != null){
						Cell parent = G2;  // same as above for G1
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
				}
				return null;
			}
			Cell selected = openedList.get(0); // selected is the node with the minimum value from the openedList
			double mincost = selected.getCost();	 // We hold the cost from the selected node
			for(int i=0;i<openedList.size();i++)  // go through the openedList
			{
				if(openedList.get(i).getCost() < mincost ) // if we found less value than mincost
				{
					mincost = openedList.get(i).getCost();  // update the mincost of selected node 
					selected = openedList.get(i);			// update the selected node
				}
			}
			openedList.remove(selected);  // we picked selected so we remove it from the openedList
			if(selected == G1 || selected == G2){ // if we are in a goal state
				if(G1 == G2){
					found+=2;
				}else{
					found++;
					closedList.add(selected);
				}
				if(found==2){
					if(G1.getCost() < G2.getCost()){ // we take the smallest cost from G1 or G2 and then we calculate the path, same as above
						Cell parent = G1;   
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
					else if(G2.getParent() != null){
						Cell parent = G2;  // if G2 has the smallest cost then we do tha same for G2
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
				}
			}
			closedList.add(selected); // we checked the selected node, so we added to the closedList
			int counter = selected.hasNeighbors(); // We hold the number of the neighbors that selected has
			if(counter>0){ // if we have neighbors
				for(int i=0; i<counter; i++){
					Cell child = selected.getNeighbor(i); // take each child 
					double temp = selected.getCost() + move(selected, child); // hold temporarily the cost that the node has and the cost from the move that was made
					if(!closedList.contains(child)){ // if the child is not on closedList
						if(!openedList.contains(child)){ // and if the child is not on openedList
							child.setParent(selected); // We set the parent for the child to be the selected node
							child.setCost(temp); // we set the cost for the child
							openedList.add(child); // we add it to the openedList
						}
						else{
							if(child.getCost() > temp){ // if the child is in the openedList and the corresponding child has bigger cost then we do the same thing as above
								child.setParent(selected); 
								child.setCost(temp);
								openedList.add(child);
							}
						}
					}	
				}
			}
		}
	}
	public double h(Cell current,Cell goal1,Cell goal2)
	{
		int dx1 = Math.abs(current.getX() - goal1.getX()); // calculate coordinate x for G1
		int dy1 = Math.abs(current.getY() - goal1.getY()); // calculate coordinate y for G2
		int D = 1; // vertical or horizontal distance between nodes (=1)
		double D2 = 0.5; // diagonal distance between nodes (=0.5)
		double h1 = D*(dx1+dy1)+(D2 - (2*D))*Math.min(dx1,dy1); // calculate h1 for G1
		int dx2 = Math.abs(current.getX() - goal2.getX()); // calculate coordinate x
		int dy2 = Math.abs(current.getY() - goal2.getY()); // calculate coordinate y
		double h2 = D*(dx2+dy2)+(D2 - (2*D))*Math.min(dx2,dy2); // calculate h2 for G2
		if(h1<h2){ // if h1 is bigger than h2 we choose that
			return h1;
		}
		return h2;
	}
	public void resetMaze() // this method resets the parents and the costs after the ucs algorithm, so we can execute A* algorithm
	{
		for(int i=0;i<this.size;i++) 
        {
            for(int j=0;j<this.size;j++)
			{
				this.myMaze[i][j].setParent(null); // we set the parent to the null
				this.myMaze[i][j].setCost(0);	  // we set the cost to the zero
			}
		}	
	}
	/* A* algorithm code is the same as ucs, the only change is that we add the h function to the mincost of the selected node and we take some more checks */
	public ArrayList<Cell> a_star_search(Cell S,Cell G1,Cell G2) 
	{
		ArrayList<Cell> openedList = new ArrayList<Cell>();
		ArrayList<Cell> closedList = new ArrayList<Cell>();
		ArrayList<Cell> path = new ArrayList<Cell>();
		openedList.add(S);
		int found = 0;
		while(true){
			if(openedList.isEmpty()){
				if(found > 0){
					if(G1.getParent() != null){
						Cell parent = G1;
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
					else{
						Cell parent = G2;
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
				}
				return null;
			}
			Cell selected = openedList.get(0);
			double mincost = selected.getCost() + h(selected,G1,G2); // we add the the result of h function to the mincost
			double mincostTemp;	
			for(int i=0;i<openedList.size();i++)
			{
				mincostTemp = openedList.get(i).getCost() + h(openedList.get(i),G1,G2); // the temporary cost is the cost from the node + the return value from h function
				if(mincostTemp < mincost )  // if mincostTemp if less that mincost, we found a smaller value
				{
					mincost = mincostTemp; // update mincost
					selected = openedList.get(i); // update selected node
				}
			}
			openedList.remove(selected);
			if(selected == G1 || selected == G2){
				if(G1 == G2){ //same goal practicly having only 1 so go end algorithm
					found+=2;
				}else{
					if(!closedList.contains(selected)){ //do not add to found twice from same goal
						found++;
						closedList.add(selected);
					}
				}
				if(found==2){
					if(G1.getCost() < G2.getCost()){
						Cell parent = G1;
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
					else{
						Cell parent = G2;
						path.add(parent);
						while(parent != null){
							parent = parent.getParent();
							path.add(parent);
						}
						return path;
					}
				}
			}
			closedList.add(selected);
			int counter = selected.hasNeighbors();
			if(counter>0){
				for(int i=0; i<counter; i++){
					Cell child = selected.getNeighbor(i);
					double temp = selected.getCost() + move(selected, child);
					if(!closedList.contains(child)){
						if(!openedList.contains(child)){
							child.setParent(selected);
							child.setCost(temp);
							openedList.add(child);
						}
						else{
							if(child.getCost() > temp){
								child.setParent(selected);
								child.setCost(temp);
							}
						}
					}	
				}
			}
		}
	}

	
	public static void main(String args[])
	{
		Scanner sc = new Scanner(System.in); // we use scanner so the user can give us the values for node S,G1,G2
		ArrayList<Cell> path = new ArrayList<Cell>(); // a list that holds the path
		System.out.println("Give the size of the maze(N)"); 
		int N = sc.nextInt(); //size of maze
		System.out.println("Give the probability(p) of the maze (from [0,1])"); 
		int p = (int)(sc.nextDouble()*10); //probability p*10 (p from exercise's introduction) 
		maze mylabyrinth = new maze(N,p); // we make a maze
		System.out.println("\nMaze cells: "); // we print the cells of the maze
		for(int i=0;i<N;i++)
		{
			System.out.println();
			for(int j=0;j<N;j++)
			{
				System.out.print("("+i+","+j+") : "+mylabyrinth.val(i,j)+" | "); //print maze
			}
		}
		// here we have some prints so the user can give us the coordinates
		System.out.println();
		System.out.println("\nChoose cells with values > 0(value = -1 means blocked cell)\n");
		System.out.print("Give coordinate x for starting node S: ");
		int Sx = sc.nextInt();
		System.out.print("Give coordinate y for starting node S: ");
		int Sy = sc.nextInt();
		Cell S = mylabyrinth.cell(Sx,Sy);
		System.out.print("Give coordinate x for possible ending node G1: ");
		int G1x = sc.nextInt();
		System.out.print("Give coordinate y for possible ending node G1: ");
		int G1y = sc.nextInt();		
		Cell G1 = mylabyrinth.cell(G1x,G1y);
		System.out.print("Give coordinate x for possible ending node G2: ");
		int G2x = sc.nextInt();
		System.out.print("Give coordinate y for possible ending node G2: ");
		int G2y = sc.nextInt();	
		Cell G2 = mylabyrinth.cell(G2x,G2y);
		System.out.println("\nWe have: S("+Sx+","+Sy+") G1("+G1x+","+G1y+") G2("+G2x+","+G2y+") \n");
		System.out.println("Executing ucs algorith.."); // first we execute the ucs algorithm
		long startTime = new Date().getTime(); // we need to count the time, so we have the starting time 
		path = mylabyrinth.ucs(S,G1,G2); // run the algorithm
		long endTime = new Date().getTime(); // hold the end time
		long result1 = endTime - startTime; // find the time 
		System.out.println("ucs algorithm time in milliseconds: " + result1+" ms"); // print the time in milliseconds
		System.out.println("\nOptimal path is: "); // print the path we found
		if(path == null){
			System.out.println("\nNo exit was found."); //since both exits are covered by obstacles and are unable to reach exit.
		}else{
			for(int j=0;j<path.size()-1;j++)
			{
				System.out.print("("+path.get(j).getX()+","+path.get(j).getY()+") : "+path.get(j).getValue()+" | "); //print maze
			}
			System.out.println();
			System.out.println("\nFinal cost (for ucs) is: "+path.get(0).getCost()); // print the final cost of the ucs algorithm
			System.out.println("\n");
		}
		mylabyrinth.resetMaze(); // we reset the parents and costs for the maze so we can execute A* algorithm
		System.out.println("Executing A* algorith..");
		long startTime2 = new Date().getTime(); // same as above to count time for A* algorithm
		path = mylabyrinth.a_star_search(S,G1,G2); // run the A* algorithm
		long endTime2 = new Date().getTime();
		long result2 = endTime2 - startTime2;
		System.out.println("A* algorithm time in milliseconds: " + result2+" ms"); // print the time in milliseconds
		System.out.println("\nOptimal path is: "); // print the path we found
		if(path == null){
			System.out.println("\nNo exit was found."); //since both exits are covered by obstacles and are unable to reach exit.
		}else{
			for(int j=0;j<path.size()-1;j++)
			{
				System.out.print("("+path.get(j).getX()+","+path.get(j).getY()+") : "+path.get(j).getValue()+" | "); //print maze
			}
			System.out.println();
			System.out.println("\nFinal cost (for A*) is: "+path.get(0).getCost()); // print the final cost
		}
	}
}
