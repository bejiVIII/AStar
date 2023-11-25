package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class MainController implements Initializable
{
	
	@FXML
	private Button executeAlgoButton;
	
	@FXML
	private Button executeRandomButton;
	
	@FXML
	private Button clearButton;
	
	@FXML
	private Slider speedSlider;
	
	@FXML
	private Label sliderValueLabel;
	
	@FXML
	private AnchorPane mainAnchor;
	
	@FXML
	private ComboBox<String> tilePickerComboBox;
	
	@FXML
	private GridPane grid = new GridPane();
	
	@FXML
	private CheckBox showGridLines;
	
	private Cell startCell;
	
	private Cell goalCell;
	
	private static final int ROWS = 40;
    private static final int COLS = 60;
	
	private Cell[][] cells;
	
	private Random r = new Random();
	
	private boolean bShowGridLines = false;
	
	private Tooltip tt = new Tooltip();
	
	private ArrayList<Cell> openSet = new ArrayList<Cell>();
	//openSet stores all the nodes that still need to be evaluated.

	private ArrayList<Cell> closedSet = new ArrayList<Cell>();
	//closedSet stores all the nodes that finished being evaluated.
	private Timeline timeline = new Timeline();

	private int pointTimeline = 0;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1)
	{
		grid.addEventHandler(MouseEvent.MOUSE_CLICKED, hnd);		
		grid.addEventHandler(MouseEvent.MOUSE_DRAGGED, hndDrag);
		grid.setGridLinesVisible(bShowGridLines);
		
		tilePickerComboBox.getItems().add("Wall");
		tilePickerComboBox.getItems().add("Source node");
		tilePickerComboBox.getItems().add("Destination node");
		
		//TODO: maybe tooltipsuru for each cellsuru
		
		//https://stackoverflow.com/questions/22780369/make-a-label-update-while-dragging-a-slider
		speedSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(
				ObservableValue<? extends Number> obsValue, Number oldValue, Number newValue) {
				//System.out.println(obsValue);			
				sliderValueLabel.textProperty().setValue(String.valueOf(newValue.intValue() + "%"));				
				tt.setText(newValue.intValue() + "%");
			}
					
		});		
		
		tt.setText("100%");
		speedSlider.setTooltip(tt);
		
		cells = new Cell[ROWS][COLS];
		

		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{	
				Cell cell;
				cell = new Cell(i, j);
				
				cells[i][j] = cell;
				grid.add(cell, j, i);
			}
		}
		
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{	
				if(i < ROWS - 1)
				{
					cells[i][j].addNeighbor(cells[i + 1][j]);
				}
				if(i > 0)
				{
					cells[i][j].addNeighbor(cells[i - 1][j]);
				}
				if(j < COLS - 1)
				{
					cells[i][j].addNeighbor(cells[i][j + 1]);
				}
				if(j > 0)
				{
					cells[i][j].addNeighbor(cells[i][j - 1]);
				}	
			}
		}
		
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{
				System.out.printf("Node (%d, %d): [", i, j);
				for(Cell c : cells[i][j].getNeighbors())
				{
					System.out.print(" (" + c.getI() + ", " + c.getJ() + ") ");
				}
				System.out.println("]");
			}
		}
	}
	
	
	public double calculateManhattanDistance(Cell sourceCell, Cell destCell)
	{
		return Math.abs(sourceCell.getX() - destCell.getX()) + Math.abs(sourceCell.getY() - destCell.getY()); 
	}
	
	public void run()
	{
		aStar();
		timeline.play();
	}
	
	public void aStar()
	{

		//actually use background colors for the path
		for(int i = 0; i < ROWS ; i++)
		{
			for(int j = 0; j < COLS ; j++)
			{
				if(cells[i][j].isStartCell())
				{
					startCell = cells[i][j];
				}
				if(cells[i][j].isGoalCell())
				{
					goalCell = cells[i][j];
				}

				//System.out.printf("Cells(i: %d, j: %d): \n", i, j);
				//System.out.printf("layout x: %.2f\n",cells[i][j].getLayoutX());
				//System.out.printf("layout y: %.2f\n",cells[i][j].getLayoutY());
					
			}
		}
		
		if(startCell != null && goalCell != null)
		{
			//System.out.println(startCell);
			//System.out.println(goalCell);	

			openSet.add(startCell);
			KeyFrame kf = new KeyFrame(Duration.millis(pointTimeline * 10), e -> {
				startCell.isInOpenSet(true);
			});
			pointTimeline++;
			
			timeline.getKeyFrames().add(kf);
			//startCell.isInOpenSet(true);
			
			while(!openSet.isEmpty())
			{
				
				int winner = 0;
				for(int i = 0; i < openSet.size(); i++)
				{
					if(openSet.get(i).fCost < openSet.get(winner).fCost)
					{
						winner = i;
					}
				}
				
				var current = openSet.get(winner);
				
				if(current == goalCell)
				{
					System.out.println("DONE! UwU");
				}
				
				openSet.removeIf(c -> c.equals(current));
				
				kf = new KeyFrame(Duration.millis(pointTimeline * 10), e -> {
					current.isInOpenSet(false);
				});
				pointTimeline++;
				
				timeline.getKeyFrames().add(kf);
				//current.isInOpenSet(false);
				
				//check if it removes the right thing
				closedSet.add(current);
				
				kf = new KeyFrame(Duration.millis(pointTimeline * 10), e -> {
					current.isInClosedSet(true);
				});
				pointTimeline++;
				
				timeline.getKeyFrames().add(kf);
				//current.isInClosedSet(true);
				
				ArrayList<Cell> neighbors = current.getNeighbors();
				
				for(int i = 0; i < neighbors.size(); i ++)
				{
					Cell neighbor = neighbors.get(i);
					
					if(!closedSet.contains(neighbor))
					{
						double tempG = current.gCost + 1;
						
						//current.gCost = calculateManhattanDistance(startCell, current);
						if(openSet.contains(neighbor))
						{
							if(tempG < neighbor.gCost)
							{
								neighbor.gCost = tempG;
							}
						}
						else 
						{
							neighbor.gCost = tempG;
							
							kf = new KeyFrame(Duration.millis(pointTimeline * 10), e -> {
								neighbor.isInOpenSet(true);
							});
							pointTimeline++;
							
							timeline.getKeyFrames().add(kf);
							//neighbor.isInOpenSet(true);
							openSet.add(neighbor);
						}
					}
					
					neighbor.hCost = calculateManhattanDistance(neighbor, goalCell);
					neighbor.fCost = neighbor.gCost + neighbor.hCost;
				}
				
			}
		}
	}
	
	public void showGridLines()
	{
		bShowGridLines = !bShowGridLines;
		grid.setGridLinesVisible(bShowGridLines);
	}
	
	public void generateRandomWalls()
	{
		//TODO: animate this bullshit
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{ 
				if(!cells[i][j].isStartCell() && !cells[i][j].isGoalCell())
				{
					if(r.nextFloat(1) < 0.3)
					{
						cells[i][j].isWall(true);
					}
					else
					{
						cells[i][j].isWall(false);
					}
				}
				else
				{
					//System.out.println(cells[i][j]);
					//System.out.println("is start cell or end cell");
				}
			}
		}
 	
	}
	
	public void generateMaze()
	{
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{ 
				if(!cells[i][j].isStartCell() && !cells[i][j].isStartCell() &&
				   !cells[i][j].isGoalCell() && !cells[i][j].isGoalCell())
				{
					cells[i][j].isWall(false);
				}
			}
		}
		
		for(int i = 0; i < ROWS; i++)
		{
			int n = r.nextInt(4);
			
			if(n < 2)
			{	
				if(!cells[i][0].isStartCell() && !cells[i][0].isGoalCell())
				cells[i][0].isWall(true);

			}
			
			n = r.nextInt(4);
			if(n < 2)
			{
				if(!cells[i][COLS - 1].isStartCell() && !cells[i][COLS - 1].isGoalCell())
				{
					cells[i][COLS - 1].isWall(true);
				}
			}
		}
		
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j = j + 2)
			{ 
				if(j > 0 && i < ROWS - 1)
				{
					if(r.nextInt(2) == 0)
					{
						if(!cells[i][j - 1].isStartCell() && !cells[i][j - 1].isGoalCell())
						{
							cells[i][j - 1].isWall(true);
						}
					}
					else
					{
						if(!cells[i + 1][j].isStartCell() && !cells[i + 1][j].isGoalCell())
						{
							cells[i + 1][j].isWall(true);
						}
					}
				}
			}
		}
	}
	
	public void clearGrid()
	{
		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{
				cells[i][j].isWall(false);
				cells[i][j].setStartCell(false);
				cells[i][j].setGoalCell(false);
			}
		} 
	}

	EventHandler<MouseEvent> hndDrag = new EventHandler<MouseEvent>()
			{
				@Override
				public void handle(MouseEvent ev) {
					//System.out.println(ev.getTarget());
					
					if(ev.getButton() == MouseButton.PRIMARY)
					{
						if(tilePickerComboBox.getValue() == null || tilePickerComboBox.getValue() == "Wall")
						{
							Node intNode = ev.getPickResult().getIntersectedNode();
							try {
								if(intNode.toString().contains("Circle") || intNode.toString().contains("Grid"))
								{
									return;
								}
								else
								{
									Cell cell = (Cell)intNode;
									
									if(!cell.isWall())
									{
										cell.isWall(true);
									}
								}
							}
							catch(Exception e)
							{
								return;
							}
							
						}
					}
					if(ev.getButton() == MouseButton.SECONDARY)
					{
						try {
							if(ev.getPickResult().getIntersectedNode().toString().contains("Circle"))
							{
								Circle c = (Circle)ev.getPickResult().getIntersectedNode();
								Cell parentCell = (Cell)c.getParent();
								parentCell.isWall(false);
								parentCell.setStartCell(false);
								parentCell.setGoalCell(false);
							}
							if(ev.getPickResult().getIntersectedNode().toString().contains("Cell"))
							{
								Cell cell = (Cell)ev.getPickResult().getIntersectedNode();
								cell.isWall(false);
								cell.setStartCell(false);
								cell.setGoalCell(false);
							}
						}
						catch(Exception e)
						{
							return;
						}
						
					}
				}
		
			};

	EventHandler<MouseEvent> hnd = new EventHandler<MouseEvent>()
	{
		@Override
		public void handle(MouseEvent ev) {
			EventTarget et = ev.getTarget();

			if(et.toString().contains("Grid"))
			{
				return;
			}
			
			Cell cell = null;
			
			if(!et.toString().contains("Circle"))
			{
				cell = (Cell)et;
			}
			
			if(ev.getButton() == MouseButton.PRIMARY)
			{
				if(tilePickerComboBox.getValue() == null || tilePickerComboBox.getValue() == "Wall")
				{	
					if(!cell.isWall())
					{
						cell.isWall(true);
					}
				}
				else if(tilePickerComboBox.getValue() == "Source node") {
					
					for(int i = 0; i < ROWS; i ++)
					{
						for(int j = 0; j < COLS; j++)
						{
							if(cells[i][j].isStartCell())
							{
								cells[i][j].setStartCell(false);
							}
						}
					}
					
					Cell c = (Cell)et;
					c.setStartCell(true);
					
				} else {
					for(int i = 0; i < ROWS; i ++)
					{
						for(int j = 0; j < COLS; j++)
						{
							if(cells[i][j].isGoalCell())
							{
								cells[i][j].setGoalCell(false);
							}
						}
					}
					
					Cell c = (Cell)et;
					c.setGoalCell(true);
				}
			}
			if(ev.getButton() == MouseButton.SECONDARY) //DELETUS TYPE OF NODUS
			{
				if(et.toString().contains("Circle"))
				{
					Circle c = (Circle)et;
					Cell parentCell = (Cell)c.getParent();
					parentCell.isWall(false);
					parentCell.setStartCell(false);
					parentCell.setGoalCell(false);
				}
				if(et.toString().contains("Cell"))
				{
					cell.isWall(false);
					cell.setStartCell(false);
					cell.setGoalCell(false);
				}
			}
		}	
	};
	
}
