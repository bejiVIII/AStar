package application;

import javafx.scene.layout.Pane;
<<<<<<< HEAD

public class Cell extends Pane {
	
	private boolean isSelected = false;
	
	
=======
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

public class Cell extends Pane {

	private boolean isWall = false;
	private boolean isStartCell;
	private boolean isEndCell;
	private Cell[] neighbors;
	
	public Cell()
	{
		
	}
	
	public boolean isStartCell()
	{
		return isStartCell;
	}
	
	public boolean isEndCell()
	{
		return isEndCell;
	}
	
	public void setStartCell(boolean isStartCell)
	{
		this.isStartCell = isStartCell; 
		updateFill();
	}
	
	public void setEndCell(boolean isEndCell)
	{
		this.isEndCell = isEndCell; 
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

        } else if (isEndCell) {
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
        } else {
        	getChildren().clear();
        }
    }
>>>>>>> 8798023 (All the user interface is done. User can place walls manually, place a source node and a destination node, can create random walls using the 'Generate random walls' button, same goes for the 'maze'. User can also see the grid lines.)
}