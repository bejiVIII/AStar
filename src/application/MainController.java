package application;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Circle;

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
	
	private Cell endCell;
	
	private static final int ROWS = 40;
    private static final int COLS = 60;
	
	private Cell[][] cells;
	
	private Random r = new Random();
	
	private boolean bShowGridLines = false;
	
	private Tooltip tt = new Tooltip();

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
		

		for(int i = 0; i < 40; i++)
		{
			for(int j = 0; j < 60; j++)
			{	
				Cell cell;
				cell = new Cell();
				
				cells[i][j] = cell;
				grid.add(cell, j, i);
			}
		}
		
	}
	
	public void executeAlgo()
	{
//		Path path = new Path();
//		path.setStrokeWidth(3);
//		path.setStroke(Color.CORNFLOWERBLUE);
//		path.getElements().add(new MoveTo(400, 50));
//		path.getElements().add(new LineTo(1000, 300));
//		
//		grid.getChildren().add(path);
		
		//actually use background colors for the path
		for(int i = 0; i < ROWS / 2; i++)
		{
			for(int j = 0; j < COLS /2; j++)
			{
				if(cells[i][j].isStartCell())
				{
					startCell = cells[i][j];
				}
				if(cells[i][j].isEndCell())
				{
					endCell = cells[i][j];
				}

				System.out.printf("Cells(i: %d, j: %d): \n", i, j);
				System.out.printf("layout x: %.2f\n",cells[i][j].getLayoutX());
				System.out.printf("layout y: %.2f\n",cells[i][j].getLayoutY());

			}
		}
		
		System.out.println(startCell);
		System.out.println(endCell);
	}
	
	public void showGridLines()
	{
		bShowGridLines = !bShowGridLines;
		grid.setGridLinesVisible(bShowGridLines);
	}
	
	public void generateRandomWalls()
	{

		for(int i = 0; i < ROWS; i++)
		{
			for(int j = 0; j < COLS; j++)
			{ 
				if(!cells[i][j].isStartCell() && !cells[i][j].isEndCell())
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
				   !cells[i][j].isEndCell() && !cells[i][j].isEndCell()	)
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
				if(!cells[i][0].isStartCell() && !cells[i][0].isEndCell())
				cells[i][0].isWall(true);

			}
			
			n = r.nextInt(4);
			if(n < 2)
			{
				if(!cells[i][COLS - 1].isStartCell() && !cells[i][COLS - 1].isEndCell())
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
						if(!cells[i][j - 1].isStartCell() && !cells[i][j - 1].isEndCell())
						{
							cells[i][j - 1].isWall(true);
						}
					}
					else
					{
						if(!cells[i + 1][j].isStartCell() && !cells[i + 1][j].isEndCell())
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
				cells[i][j].setEndCell(false);
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
								parentCell.setEndCell(false);
							}
							if(ev.getPickResult().getIntersectedNode().toString().contains("Cell"))
							{
								Cell cell = (Cell)ev.getPickResult().getIntersectedNode();
								cell.isWall(false);
								cell.setStartCell(false);
								cell.setEndCell(false);
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
							if(cells[i][j].isEndCell())
							{
								cells[i][j].setEndCell(false);
							}
						}
					}
					
					Cell c = (Cell)et;
					c.setEndCell(true);
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
					parentCell.setEndCell(false);
				}
				if(et.toString().contains("Cell"))
				{
					cell.isWall(false);
					cell.setStartCell(false);
					cell.setEndCell(false);
				}
			}
		}	
	};
	
}
