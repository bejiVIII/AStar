package application;

import java.util.ArrayList;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Cell extends Pane {
	
	private boolean isWall;
	private boolean isStartCell;
	private boolean isGoalCell;
	private boolean isInOpenSet;
	private boolean isInClosedSet;
	//Parent node: reference to the node that was expanded to generate the current node.
	private Cell parentNode;
	private double x;
	private double y;
	private int i;
	private int j;
	//G-Cost: the cost of getting from the start node to this node.
	public double gCost;
	//H-Cost(Heuristics cost): the cost of getting from this node to the goal node.
	public double hCost;
	//F-Cost: the total cost, which is the sum of the G-Cost and the H-Cost.
	public double fCost;
	
	
	private ArrayList<Cell> neighbors = new ArrayList<Cell>();
	
	public Cell(int i, int j)
	{
		this.i = i;
		this.j = j;
		
		x = getLayoutX();
		y = getLayoutY();
		
		gCost = 0.0;
		hCost = 0.0;
		fCost = 0.0;
		parentNode = null;
	}
	
	public void addNeighbor(Cell c)
	{
		neighbors.add(c);
	}
	
	public ArrayList<Cell> getNeighbors()
	{
		return neighbors;
	}
	
	public void calculateFCost()
	{
		fCost = gCost + hCost;
	}
	
	public int getI()
	{
		return i;
	}
	
	public int getJ()
	{
		return j;
	}
	
	public double getX()
	{
		return x;
	}
	
	public double getY()
	{
		return y;
	}

	public Cell getParentNode()
	{
		return parentNode;
	}
	
	public boolean isStartCell()
	{
		return isStartCell;
	}
		
	public boolean isGoalCell()
	{
		return isGoalCell;
	}
	
	public void isInOpenSet(boolean value)
	{
		isInOpenSet = value;
		updateFill();
	}
	
	public boolean isInOpenSet()
	{
		return isInOpenSet;
	}
	
	public void isInClosedSet(boolean value)
	{
		isInClosedSet = value;
		updateFill();
	}
	
	public boolean isInClosedSet()
	{
		return isInClosedSet;
	}
	
	public void setStartCell(boolean isStartCell)
	{
		this.isStartCell = isStartCell; 
		updateFill();
	}
		
	public void setGoalCell(boolean isGoalCell)
	{
		this.isGoalCell = isGoalCell; 
		updateFill();
	}
		
	public boolean isWall()
	{
		return isWall;
	}
		
	public void isWall(boolean value)
	{
		isWall = value;
		updateFill();
	}
		
	private void updateFill() {
	//Rectangle border = (Rectangle)getChildren().get(0);
	
		if (isStartCell) {
			//setColor("green");
			Circle c = new Circle();
			c.setRadius(7);
			
			c.setCenterX(13);
			c.setCenterY(9.5);
			c.setFill(Color.GREEN);
			getChildren().add(c);
	
	        } else if (isGoalCell) {
				//setColor("red");
				Circle c = new Circle();
				c.setRadius(7);
				
				c.setCenterX(13);
				c.setCenterY(9.5);
				c.setFill(Color.RED);
				getChildren().add(c);
	
				
	        } else if (isWall){
	        	Circle c = new Circle();
				c.setRadius(7);
				
				c.setCenterX(13);
				c.setCenterY(9.5);
				c.setFill(Color.BLACK);
				
				getChildren().add(c);
				// or any other color for regular cells
	        } else if(isInOpenSet) {
	        	setStyle("-fx-background-color: green");
			} else if(isInClosedSet){ 
	        	setStyle("-fx-background-color: red");
			} else {
	        	getChildren().clear();
	        }
    }
}
