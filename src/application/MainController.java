package application;

import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import javafx.animation.Animation;
import javafx.animation.Animation.Status;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
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
	
	private static final int ROWS = 41;
    private static final int COLS = 61;
	
	private Cell[][] cells;
	
	private Random r = new Random();
	
	private boolean bShowGridLines = false;
	
	private Tooltip tt = new Tooltip();
	
	private ArrayList<Cell> openSet = new ArrayList<Cell>();
	//openSet stores all the nodes that still need to be evaluated.

	private ArrayList<Cell> closedSet = new ArrayList<Cell>();
	//closedSet stores all the nodes that finished being evaluated.
	
	private ArrayList<Cell> path = new ArrayList<Cell>();
	
	private Timeline evaluationTimeline = new Timeline();
	
	private Timeline pathTimeline = new Timeline();

	private double animationSpeed = 5.0;
		
	private int pointTimeline = 0;
	
	private boolean goalAchieved = false;
	
	
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
				sliderValueLabel.textProperty().setValue(String.valueOf(newValue.intValue() + "%"));				
				tt.setText(newValue.intValue() + "%");

				animationSpeed = mapValue(newValue.doubleValue(), 1, 100, 5, 130);
				//animationSpeed = newValue.intValue();
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
		
		for(int i = 0; i < ROWS - 1; i++)
		{
			for(int j = 0; j < COLS - 1; j++)
			{	
				if(i > 0)
				{
					cells[i][j].addNeighbor(cells[i - 1][j]);
				}
				if(j > 0)
				{
					cells[i][j].addNeighbor(cells[i][j - 1]);
				}
				if(j < COLS - 1)
				{
					cells[i][j].addNeighbor(cells[i][j + 1]);
				}
				
				if(i < ROWS - 1)
				{
					cells[i][j].addNeighbor(cells[i + 1][j]);
				}
				
				//DIAGONAL NEIGHBOR
				if(i > 0 && j > 0)
				{
					cells[i][j].addNeighbor(cells[i-1][j-1]);
				}
				if(i > 0 && j < COLS - 1)
				{
					cells[i][j].addNeighbor(cells[i-1][j+1]);
				}
				if(i < ROWS - 1 && j > 0)
				{
					cells[i][j].addNeighbor(cells[i+1][j-1]);
				}
				if(i < ROWS - 1 && j < COLS - 1)
				{
					cells[i][j].addNeighbor(cells[i+1][j+1]);
				}
			}
		}
		
	}
	
	private double mapValue(double value, double fromMin, double fromMax, double toMin, double toMax) {
		return toMax + ((value - fromMin) / (fromMax - fromMin)) * (toMin - toMax);
	}
	
	public double heuristics(Cell sourceCell, Cell destCell)
	{
		//manhattan distance
		return Math.abs(sourceCell.getLayoutX() - destCell.getLayoutX()) + Math.abs(sourceCell.getLayoutY() - destCell.getLayoutY());
		//euclidian distance
		//return (destCell.getLayoutX() - sourceCell.getLayoutX()) * 2 + (destCell.getLayoutY() - sourceCell.getLayoutY()) * 2;
	}
	
	public void run()
	{
		//System.out.println("speed: " + animationSpeed);
		
		if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
		{
			for(int i = 0; i < ROWS; i++)
			{
				for(int j = 0; j < COLS; j++)
				{
					cells[i][j].isInOpenSet(false);
					cells[i][j].isInClosedSet(false);
					evaluationTimeline.stop();
					evaluationTimeline.getKeyFrames().clear();
					pathTimeline.stop();
					pathTimeline.getKeyFrames().clear();
					openSet.clear();
					closedSet.clear();
					path.clear();
					pointTimeline = 0;
				}
			}
		}

		aStar();
		
		evaluationTimeline.play();
		
		evaluationTimeline.setOnFinished(event -> {
			//System.out.println("DONE!!! UwU");
			System.out.println("Showing path..");

				animatePath();
		});
		
	}
	
	
	
	public void aStar()
	{

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
				
			}
		}
		
		if(startCell != null && goalCell != null)
		{

			openSet.add(startCell);
			KeyFrame kf = new KeyFrame(Duration.millis(pointTimeline * animationSpeed), e -> {
				startCell.setStyle("-fx-background-color: green;");
			});
			pointTimeline++;
			
			evaluationTimeline.getKeyFrames().add(kf);
			startCell.isInOpenSet(true);
			
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
					path.clear();
					Cell temp = current;

					path.add(temp);
					
					while(temp.previous != null)
					{
						path.add(temp.previous);
						temp = temp.previous;
					}
					
					return;
				}
			
				openSet.removeIf(c -> c.equals(current));
				
				kf = new KeyFrame(Duration.millis(pointTimeline * animationSpeed), e -> {
					current.setStyle("-fx-background-color: pink;");
				});
				pointTimeline++;
				
				evaluationTimeline.getKeyFrames().add(kf);
				current.isInOpenSet(false);
				
				closedSet.add(current);
				
				kf = new KeyFrame(Duration.millis(pointTimeline * animationSpeed), e -> {
					current.setStyle("-fx-background-color: lightblue;");
				});
				pointTimeline++;
				
				evaluationTimeline.getKeyFrames().add(kf);
				
				current.isInClosedSet(true);
				
				ArrayList<Cell> neighbors = current.getNeighbors();
				
				for(int i = 0; i < neighbors.size(); i ++)
				{
					Cell neighbor = neighbors.get(i);
					boolean newPath = false;

					if(!closedSet.contains(neighbor) && !neighbor.isWall())
					{
						double tempG = current.gCost + 1;
						
						//current.gCost = calculateManhattanDistance(startCell, current);
						if(openSet.contains(neighbor))
						{
							if(tempG < neighbor.gCost)
							{
								neighbor.gCost = tempG;
								newPath = true;
							}
						}
						else 
						{
							neighbor.gCost = tempG;
							
							kf = new KeyFrame(Duration.millis(pointTimeline * animationSpeed), e -> {
								neighbor.setStyle("-fx-background-color: green;");
							});
							pointTimeline++;
						
							evaluationTimeline.getKeyFrames().add(kf);
							
							neighbor.isInOpenSet(true);
							newPath = true;
							openSet.add(neighbor);
						}
						

					}
					if(newPath)
					{
						neighbor.hCost = heuristics(neighbor, goalCell);
						neighbor.fCost = neighbor.gCost + neighbor.hCost;
						neighbor.previous = current;

					}
					
				}
			}
			System.out.println("No solution!");
			
			return;
		}
	}
	
	public void animatePath()
	{
		System.out.println("path size: " + path.size());
		
		if(path.size() == 0)
		{
			Alert a = new Alert(AlertType.INFORMATION, "NO SOLUTION!", ButtonType.OK);
        	a.show();
        	return;
		}
		for(int i = 0; i < path.size(); i++)
		{
			Cell cell = path.get(i);
			KeyFrame kf = new KeyFrame(Duration.millis(i * animationSpeed), e -> {
				cell.setStyle("-fx-background-color: blue;");
			});
			
			pathTimeline.getKeyFrames().add(kf);
		}
		
		pathTimeline.play();
	}
	
	public void showGridLines()
	{
		bShowGridLines = !bShowGridLines;
		grid.setGridLinesVisible(bShowGridLines);
	}
	
	public void generateRandomWalls()
	{
		//TODO: animate this bullshit
		for(int i = 0; i < ROWS - 1; i++)
		{
			for(int j = 0; j < COLS; j++)
			{ 
				if(!cells[i][j].isStartCell() && !cells[i][j].isGoalCell())
				{
					if(r.nextFloat(1) < 0.4)
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
		for(int i = 0; i < ROWS - 1; i++)
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
		
		for(int i = 0; i < ROWS - 1; i++)
		{
			cells[i][0].isWall(true);
			cells[i][COLS - 2].isWall(true);
			
		}
		
		for(int j = 0; j < COLS - 1; j++)
		{
			cells[ROWS - 2][j].isWall(true);
			cells[0][j].isWall(true);
		}
		
		for(int i = 0; i < ROWS - 3; i++)
		{
			for(int j = 0; j < COLS - 2; j = j + 2)
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
		
		for(int i = 0; i < ROWS - 3; i++)
		{
			for(int j = 0; j < COLS - 2; j = j + 2)
			{
				
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
				cells[i][j].isInOpenSet(false);
				cells[i][j].isInClosedSet(false);
				startCell = null;
				goalCell = null;
				evaluationTimeline.stop();
				evaluationTimeline.getKeyFrames().clear();
				pathTimeline.stop();
				pathTimeline.getKeyFrames().clear();
				openSet.clear();
				closedSet.clear();
				pointTimeline = 0;
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
									if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
									{
										for(int i = 0; i < ROWS; i++)
										{
											for(int j = 0; j < COLS; j++)
											{
												cells[i][j].isInOpenSet(false);
												cells[i][j].isInClosedSet(false);
												evaluationTimeline.stop();
												evaluationTimeline.getKeyFrames().clear();
												pathTimeline.stop();
												pathTimeline.getKeyFrames().clear();
												openSet.clear();
												closedSet.clear();
												pointTimeline = 0;
											}
										}
										//clearGrid();
									}
									
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
								if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
								{
									for(int i = 0; i < ROWS; i++)
									{
										for(int j = 0; j < COLS; j++)
										{
											cells[i][j].isInOpenSet(false);
											cells[i][j].isInClosedSet(false);
											evaluationTimeline.stop();
											evaluationTimeline.getKeyFrames().clear();
											pathTimeline.stop();
											pathTimeline.getKeyFrames().clear();
											openSet.clear();
											closedSet.clear();
											pointTimeline = 0;
										}
									}
									//clearGrid();
								}
								
								Circle c = (Circle)ev.getPickResult().getIntersectedNode();
								Cell parentCell = (Cell)c.getParent();
								parentCell.isWall(false);
								parentCell.setStartCell(false);
								parentCell.setGoalCell(false);
							}
							if(ev.getPickResult().getIntersectedNode().toString().contains("Cell"))
							{
								
								Cell cell = (Cell)ev.getPickResult().getIntersectedNode();
								
								if(cell.isWall())
								{
									if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
									{
										for(int i = 0; i < ROWS; i++)
										{
											for(int j = 0; j < COLS; j++)
											{
												cells[i][j].isInOpenSet(false);
												cells[i][j].isInClosedSet(false);
												evaluationTimeline.stop();
												evaluationTimeline.getKeyFrames().clear();
												pathTimeline.stop();
												pathTimeline.getKeyFrames().clear();
												openSet.clear();
												closedSet.clear();
												pointTimeline = 0;
											}
										}
										//clearGrid();
									}
								}
								
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

			if(et.toString().contains("Grid") || et.toString().contains("Line"))
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
				
				if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
				{
					for(int i = 0; i < ROWS; i++)
					{
						for(int j = 0; j < COLS; j++)
						{
							cells[i][j].isInOpenSet(false);
							cells[i][j].isInClosedSet(false);
							evaluationTimeline.stop();
							evaluationTimeline.getKeyFrames().clear();
							pathTimeline.stop();
							pathTimeline.getKeyFrames().clear();
							openSet.clear();
							closedSet.clear();
							pointTimeline = 0;
						}
					}
					//clearGrid();
				}
				
				if(tilePickerComboBox.getValue() == null || tilePickerComboBox.getValue() == "Wall")
				{	
					if(cell == null)
					{
						return; 
					}
					
					if(!cell.isWall())
					{
						cell.isWall(true);
					}
				}
				else if(tilePickerComboBox.getValue() == "Source node") {
					System.out.println();
					if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED || evaluationTimeline.getStatus() == Status.STOPPED)
					{
						for(int i = 0; i < ROWS; i++)
						{
							for(int j = 0; j < COLS; j++)
							{
								cells[i][j].isInOpenSet(false);
								cells[i][j].isInClosedSet(false);
								evaluationTimeline.stop();
								evaluationTimeline.getKeyFrames().clear();
								pathTimeline.stop();
								pathTimeline.getKeyFrames().clear();
								openSet.clear();
								closedSet.clear();
								pointTimeline = 0;
							}
						}
						//clearGrid();
					}
					if(cell == null)
					{
						return;
					}
					
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
					
					
					cell.setStartCell(true);
					
				} else {
					if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
					{
						for(int i = 0; i < ROWS; i++)
						{
							for(int j = 0; j < COLS; j++)
							{
								cells[i][j].isInOpenSet(false);
								cells[i][j].isInClosedSet(false);
								evaluationTimeline.stop();
								evaluationTimeline.getKeyFrames().clear();
								pathTimeline.stop();
								pathTimeline.getKeyFrames().clear();
								openSet.clear();
								closedSet.clear();
								pointTimeline = 0;
							}
						}
						//clearGrid();
					}
					if(cell == null)
					{
						return;
					}
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
					
					
					
					cell.setGoalCell(true);
				}
			}
			if(ev.getButton() == MouseButton.SECONDARY) //DELETUS TYPE OF NODUS
			{
				
				if(et.toString().contains("Circle"))
				{
					if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
					{
						for(int i = 0; i < ROWS; i++)
						{
							for(int j = 0; j < COLS; j++)
							{
								cells[i][j].isInOpenSet(false);
								cells[i][j].isInClosedSet(false);
								evaluationTimeline.stop();
								evaluationTimeline.getKeyFrames().clear();
								pathTimeline.stop();
								pathTimeline.getKeyFrames().clear();
								openSet.clear();
								closedSet.clear();
								pointTimeline = 0;
							}
						}
						//clearGrid();
					}
					
					Circle c = (Circle)et;
					Cell parentCell = (Cell)c.getParent();
					parentCell.isWall(false);
					parentCell.setStartCell(false);
					parentCell.setGoalCell(false);
				}
				if(et.toString().contains("Cell"))
				{
					if(evaluationTimeline.getStatus() == Status.RUNNING || evaluationTimeline.getStatus() == Status.STOPPED)
					{
						for(int i = 0; i < ROWS; i++)
						{
							for(int j = 0; j < COLS; j++)
							{
								cells[i][j].isInOpenSet(false);
								cells[i][j].isInClosedSet(false);
								evaluationTimeline.stop();
								evaluationTimeline.getKeyFrames().clear();
								pathTimeline.stop();
								pathTimeline.getKeyFrames().clear();
								openSet.clear();
								closedSet.clear();
								pointTimeline = 0;
							}
						}
						//clearGrid();
					}
					
					cell.isWall(false);
					cell.setStartCell(false);
					cell.setGoalCell(false);
				}
			}
		}	
	};
	
}
