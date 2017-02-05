package javafx;

import java.awt.Desktop;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.opencsv.CSVReader;
import com.sun.prism.impl.Disposer.Record;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableRow;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class DraggingLineChartMain extends Application {


	//public static double[][] d65 = parseCSV("src/data.csv");
    public static volatile int globalInt=1;
    public static ObservableList entry;
    private Desktop desktop = Desktop.getDesktop();

	@Override
    public void start(Stage primaryStage) {
		TableView<Data<Number, Number>> tableView = new TableView<>();
		LineChart<Number,Number> lineChart;
		XYChart.Series<Number, Number> series;
		List list = new ArrayList();
		DataHandler datahandler = new DataHandler();
		TableView<ResultView> tableResult = new TableView<>();
		SuperScatterChart  CIEScatter;
		ArrayList<XYChart.Series<Number, Number>> seriesHistory = new ArrayList<XYChart.Series<Number, Number>>();
		
		

		
		
    	BorderPane root = new BorderPane();
    	
    	
        Scene scene  = new Scene(root,800,600);
        //scene.getStylesheets().add("stylesheet.css");
        //root.getStylesheets().add(getClass().getResource("../stylesheet.css").toExternalForm());
        //root.getStylesheets().add("stylesheet.css");
        
        root.getStylesheets().add(getClass().getResource("/stylesheet.css").toExternalForm());
        
		final NumberAxis xAxis2 = new NumberAxis(0, 0.9, 0.1);//0.73
	    final NumberAxis yAxis2 = new NumberAxis(0, 0.9, 0.1);//0.83     
	    CIEScatter = new SuperScatterChart (xAxis2,yAxis2 );      
	    CIEScatter.setLegendVisible(false);
       
    	
    	
        primaryStage.setTitle("Plot");
        //defining the axes
        final NumberAxis xAxis = new NumberAxis(300, 800, 100);
        final NumberAxis yAxis = new NumberAxis(0, 1, 0.1);
        xAxis.setLabel("Wavelength");
        yAxis.setLabel("Reflectance");
        //creating the chart
         lineChart = new LineChart<Number,Number>(xAxis,yAxis);
         lineChart.setLegendVisible(false);

        lineChart.setTitle("Equalizer (Plot "+globalInt+")");

        lineChart.setAnimated(false);

        //defining a series
        series = new XYChart.Series<>();
        series.setName("CIEPlot");
        //populating the series with data
 
        for(int i=6;i<=16;i++){
        	series.getData().add(new XYChart.Data<Number,Number>(i*50, 0.5));
        	
        }
        
        lineChart.getData().add(series);
        //Node node = data.getNode() ;
        series.getNode().setOnMouseClicked(new EventHandler<MouseEvent>() {
       	 
            @Override
            public void handle(MouseEvent click) {
            	if (click.getClickCount() == 2) {
            		Data<Number, Number> newData = new XYChart.Data<>(xAxis.getValueForDisplay(click.getX()),
            				yAxis.getValueForDisplay(click.getY()));
            				
            		series.getData().add(newData);
            		series.getData().sort(Comparator.comparingDouble(d -> d.getXValue().doubleValue()));
            		InteractiveNodes(newData, xAxis, yAxis,series,list,datahandler,tableResult, CIEScatter);
            		dynamicTableRow(series,list,datahandler,tableResult,CIEScatter);
            		
            	}            
            }
            
            
        });
        for (Data<Number, Number> data : series.getData()) {
            InteractiveNodes(data, xAxis, yAxis,series,list,datahandler,tableResult, CIEScatter);          
            
        }
        
        //Table Stuff
        
        tableView.setEditable(true);
        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
            @Override
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
  
        TableColumn columnWave = new TableColumn("Wavelength");
        columnWave.setCellValueFactory(
                new PropertyValueFactory<XYChart.Data,Number>("XValue"));        
        
          
        TableColumn columnReflectance = new TableColumn("Reflectance");
        columnReflectance.setCellValueFactory(
                new PropertyValueFactory<XYChart.Data,Number>("YValue"));
        
        
        TableColumn number = new TableColumn("Plot Number");
        number.setCellValueFactory(
                new PropertyValueFactory("numb"));
        
        TableColumn Rvalue = new TableColumn("R");
        Rvalue.setCellValueFactory(
                new PropertyValueFactory("rval"));
        
        TableColumn Gvalue = new TableColumn("G");
        Gvalue.setCellValueFactory(
                new PropertyValueFactory("gval"));
        
        TableColumn Bvalue = new TableColumn("B");
        Bvalue.setCellValueFactory(
                new PropertyValueFactory("bval"));
        
        TableColumn xvalue = new TableColumn("x");
        xvalue.setCellValueFactory(
                new PropertyValueFactory("x"));
        
        TableColumn yvalue = new TableColumn("y");
        yvalue.setCellValueFactory(
                new PropertyValueFactory("y"));
        
        TableColumn hexvalue = new TableColumn("Hex");
        hexvalue.setCellValueFactory(
                new PropertyValueFactory("Hex"));
        
//        columnWave.setSortable(true);
//        columnReflectance.setSortable(false);
        columnWave.setCellFactory(cellFactory);
        columnReflectance.setCellFactory(cellFactory);
        columnWave.setOnEditCommit(
            new EventHandler<CellEditEvent<XYChart.Data,Number>>() {
                @Override
                public void handle(CellEditEvent<XYChart.Data,Number> t) {
                     ((XYChart.Data)t.getTableView().getItems().get(
                        t.getTablePosition().getRow())
                        ).setXValue(t.getNewValue());                 	
                     	series.getData().sort(Comparator.comparingDouble(d -> d.getXValue().doubleValue()));
                     	dynamicTableRow(series,list,datahandler,tableResult,CIEScatter);
                }
                
            }
        );
        
        columnReflectance.setOnEditCommit(
                new EventHandler<CellEditEvent<XYChart.Data,Number>>() {
                    @Override
                    public void handle(CellEditEvent<XYChart.Data,Number> t) {
                         ((XYChart.Data)t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setYValue(t.getNewValue());
                         	dynamicTableRow(series,list,datahandler,tableResult,CIEScatter);
                    }
                }
            );
        

         
        
      //--- Prepare TableView
        ObservableList<Data<Number, Number>> newList = series.getData();
        tableView.setItems(newList);
        
        
        tableView.getColumns().addAll(columnWave, columnReflectance);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefWidth(200);
        
        
      //--- Prepare ResultView        
        //tableResult.setItems(newList);
        
        
        tableResult.getColumns().setAll(number,xvalue,yvalue,Rvalue,Gvalue,Bvalue,hexvalue);
       
        tableResult.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableResult.setPrefHeight(175);

        
       
        
        setDefaultData(list,tableResult);
        Button addBtn = new Button ("Add New");
        //---
        
        addBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	seriesHistory.add(series);
            	globalInt+=1;
            	series.getData().clear();
            	//series = new XYChart.Series<Number, Number>();
                series.setName("CIEPlot");
                
                lineChart.setTitle("Equalizer (Plot "+globalInt+")");
                for(int i=6;i<=16;i++){
                	series.getData().add(new XYChart.Data<Number,Number>(i*50, 0.5));
                	
                }       

                for (Data<Number, Number> data : series.getData()) {
                    InteractiveNodes(data, xAxis, yAxis,series,list,datahandler,tableResult,CIEScatter);          
                    
                } 
                 
            	
            	//List list = new ArrayList();
                list.add(new ResultView(globalInt,0.0,0.0,0,0,0,""));
                entry = FXCollections.observableList(list);     
                
                tableResult.setItems(entry);
                //////////////////////////////////////////////////////////////
                
                 
                
                //CIEScatter.getData().retainAll(series1);
                XYChart.Series<Number, Number> seriesx = new XYChart.Series<Number, Number>();
        		seriesx.getData().add(new XYChart.Data(datahandler.x,datahandler.y));
        		CIEScatter.getData().add(seriesx);
                
            }
        });
        
        Button saveValue = new Button ("Save Values");
        saveValue.setStyle("-fx-font: 15 arial; -fx-base: #b6e7c9;");
        saveValue.setStyle("-fx-font: 15 arial; -fx-base: #b6e7c9;");
        saveValue.setOnAction(new EventHandler<ActionEvent>() {
	    	public void handle(ActionEvent e) {
	    		FileChooser fileChooser = new FileChooser();
	    		  
	              //Set extension filter
	              FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.csv)", "*.csv");
	              fileChooser.getExtensionFilters().add(extFilter);
	              
	              //Show save file dialog
	              File file = fileChooser.showSaveDialog(primaryStage);
	              
	              if(file != null){
	            	  ObservableList<ResultView> allContents;
	                  allContents = tableResult.getItems();
	                  String fin = "Plot Number,x,y,R,G,B,Hex"+System.lineSeparator();
	                  for(ResultView i:allContents){
	                	fin += i.getNumb()+","+i.getX()+","+i.getY()+","+i.getRval()+","+i.getGval()+","+i.getBval()+","+i.getHex()+System.lineSeparator();	                  	  
	                  }
	                  
	                  SaveFile(fin,file);
	              }
	    	}
	    });
        
        Button delBtn = new Button ("Delete");
        delBtn.setStyle("-fx-font: 15 arial; -fx-base: #b6e7c9;");
        addBtn.setStyle("-fx-font: 15 arial; -fx-base: #b6e7c9;");   	    
    	
	    delBtn.setOnAction(new EventHandler<ActionEvent>() {
	    	public void handle(ActionEvent e) {
	            if(globalInt>=1){
	            	int start = 1;
	            	globalInt--;
	            	lineChart.setTitle("Equalizer (Plot "+globalInt+")");
	            	ObservableList<ResultView> Selected, allContents;
	                allContents = tableResult.getItems();
	                Selected = tableResult.getSelectionModel().getSelectedItems();
	                
	                int index = Selected.get(0).getNumb()-1;
	                 
	                CIEScatter.getData().remove(index);
	                
	                
	                
	                Selected.forEach(allContents::remove);
	               // System.out.println(entry); 
	                for(ResultView i:allContents){
	                	//System.out.println(i.getNumb()+"  "+ start);
	                	if(i.getNumb()!=start){
	                		
  	                		
	                		list.set(start-1, new ResultView(start,i.getX(),i.getY(),i.getRval(),i.getGval(),i.getBval(),i.getHex()));
	                		//list.add(new ResultView(globalInt,0.0,0.0,0,0,0,""));
	                		
	                	}
	                	start++;
	                }
	                
	                
	            	
	                
	            }
	    	}
	    });
	    
	    Button saveCIE = new Button("Save CIE Plot");
	    saveCIE.setOnAction(new EventHandler<ActionEvent>() {
	    	public void handle(ActionEvent e) {
	    		FileChooser fileChooser = new FileChooser();
	    		  
	              //Set extension filter
	              FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.png");
	              fileChooser.getExtensionFilters().add(extFilter);
	              
	              //Show save file dialog
	              File file = fileChooser.showSaveDialog(primaryStage);
	              
	              if(file != null){	            	   
	                  saveAsPng(CIEScatter,file);
	            	  
	              }
	    	}
	    });
	    Button importBtn = new Button ("Import");
	    importBtn.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				final FileChooser fileChooser = new FileChooser();
				configureFileChooser(fileChooser);
                File file = fileChooser.showOpenDialog(primaryStage);
                if (file != null) {
                    String input = file.toPath().toString();
                    ImportHandler ih  = new ImportHandler(input);
                    DataHandler dh_import = new DataHandler();
                    for(int i=0;i<ih.inputSeries.size();i++){
                    	globalInt+=1;
                    	display_import(ih.inputSeries.get(i), list, dh_import, tableResult, CIEScatter);
                    	
                    }
                }
				
			}
	    	
	    });
	    //Button saveImg = new Button ("Save CIE Diagram");
	    
	    
	    	
	    ToolBar toolBar1 = new ToolBar();
        toolBar1.getItems().addAll(                
                importBtn,
                new Separator(),
                saveCIE
                 
               
            );
    	root.setTop(toolBar1);
        
        HBox hBox1 = new HBox(1);        
        hBox1.getChildren().addAll(lineChart,tableView);      
        
        root.setLeft(hBox1);       
        //root.getChildren().add(hBox);
     
        
        
        HBox buttBox = new HBox(175);
        buttBox.getChildren().addAll(addBtn,saveValue,delBtn);        
        VBox vBox2 = new VBox(10);
        vBox2.getChildren().addAll(CIEScatter,tableResult,buttBox);    
         
        root.setRight(vBox2);        
        
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
	
	private void SaveFile(String content, File file){
        try {
            FileWriter fileWriter = null;
             
            fileWriter = new FileWriter(file);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException ex) {
            //Logger.getLogger(JavaFX_Text.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
	
	@FXML
	public void saveAsPng(SuperScatterChart  CIEScatter,File file) {
	    WritableImage image = CIEScatter.snapshot(new SnapshotParameters(), null);

	    try {
	        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
	    } catch (IOException e) {
	        // TODO: handle exception here
	    }
	}
    
    public static void InteractiveNodes(Data<Number, Number> data,NumberAxis xAxis,NumberAxis yAxis,XYChart.Series<Number, Number> series,List list,DataHandler datahandler,TableView<ResultView> tableResult,SuperScatterChart CIEScatter){
    	Node node = data.getNode() ;
        node.setCursor(Cursor.HAND);
        node.setOnMouseDragged(e -> {
        	 
            Point2D pointInScene = new Point2D(e.getSceneX(), e.getSceneY());
            double xAxisLoc = xAxis.sceneToLocal(pointInScene).getX();
            double yAxisLoc = yAxis.sceneToLocal(pointInScene).getY();
            Number x = xAxis.getValueForDisplay(xAxisLoc);
            Number y = yAxis.getValueForDisplay(yAxisLoc);
            data.setXValue(x);
            data.setYValue(y);
            series.getData().sort(Comparator.comparingDouble(d -> d.getXValue().doubleValue()));
            
            
            
        });
        
        node.setOnMouseClicked(e -> {
        	if(e.getClickCount()==2){
        		series.getData().remove(data);
        		dynamicTableRow(series,list,datahandler,tableResult, CIEScatter);
        	}
       	 
            
            
            
        });
         
        node.setOnMouseReleased(e -> {  	
        	dynamicTableRow(series,list,datahandler,tableResult, CIEScatter);
	        
            
        });
 
    }
    
    public static void setDefaultData(List list,TableView<ResultView> tableResult){
    	//List list = new ArrayList();
        list.add(new ResultView(globalInt,0.0,0.0,0,0,0,""));
        entry = FXCollections.observableList(list);      
        
        tableResult.setItems(entry);
    }
    public static void dynamicTableRow(XYChart.Series<Number, Number> series,List list , DataHandler datahandler,TableView<ResultView> tableResult,SuperScatterChart CIEScatter){
    	double[][] d65r = datahandler.multiplyReflectance(series);
    	datahandler.calc(d65r);
    	XYChart.Series<Number, Number> series1 = new XYChart.Series<Number, Number>();	
        //series1 = new XYChart.Series();
        //series1.setName("Plot");
        
       		
        if(!series1.getData().isEmpty()){
            	series1.getData().clear();            
        }
        series1.getData().add(new XYChart.Data<Number,Number>(datahandler.x,datahandler.y));     
            
            
        if(!CIEScatter.getData().isEmpty() && CIEScatter.getData().size()==globalInt ){
           //.out.println("size: "+CIEScatter.getData().size()); 	
           CIEScatter.getData().remove(globalInt-1, globalInt);          
        }
          
           
        CIEScatter.getData().add(series1);
        
        
         
        //List list = new ArrayList();
        list.remove(globalInt-1);
        list.add(new ResultView(globalInt,datahandler.x,datahandler.y,datahandler.R,datahandler.G,datahandler.B,datahandler.hex));
        //System.out.println(globalInt+" "+ datahandler.x+" "+datahandler.y+" "+datahandler.R+" "+datahandler.G+" "+datahandler.B+" "+datahandler.hex);
        entry = FXCollections.observableList(list);      
        
        tableResult.setItems(entry);
    }
    
    public static void display_import(XYChart.Series<Number, Number> series,List list , DataHandler datahandler,TableView<ResultView> tableResult,SuperScatterChart CIEScatter){
    	double[][] d65r = datahandler.multiplyReflectance(series);
    	datahandler.calc(d65r);
    	XYChart.Series<Number, Number> series1 = new XYChart.Series<Number, Number>();	
    	series1.getData().add(new XYChart.Data<Number,Number>(datahandler.x,datahandler.y));
    	CIEScatter.getData().add(series1);
    	
    	list.add(new ResultView(globalInt,datahandler.x,datahandler.y,datahandler.R,datahandler.G,datahandler.B,datahandler.hex));
        //System.out.println(globalInt+" "+ datahandler.x+" "+datahandler.y+" "+datahandler.R+" "+datahandler.G+" "+datahandler.B+" "+datahandler.hex);
        entry = FXCollections.observableList(list);      
        
        tableResult.setItems(entry);
        
    }
    
    public static double[][] parseCSV(String uri){
    	int UPPER_LIMIT = 471;
		CSVReader reader;
		double[][] result=new double[UPPER_LIMIT][4];
		int count=0;
	     
	     try {
	    	 reader = new CSVReader(new FileReader(uri));
	    	 String [] nextLine = reader.readNext();
			while (nextLine != null) {				
					
				double[] temp = new double[nextLine.length];
				for (int i = 0; i < temp.length; i++) {
				    temp[i] = Double.parseDouble(nextLine[i]);
				}

					
					result[count] = temp;
					count++;
					nextLine = reader.readNext();
			 }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	     return result;
		
	}

    public static void main(String[] args) {
//    	System.out.println("Working Directory = " +
//                System.getProperty("user.dir"));
    	
        launch(args);
        
    }
    
    private static void configureFileChooser(
            final FileChooser fileChooser) {      
                fileChooser.setTitle("View Pictures");
                fileChooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
                );                 
                fileChooser.getExtensionFilters().addAll(
//                    new FileChooser.ExtensionFilter("All Images", "*.*"),
                    new FileChooser.ExtensionFilter("CSV", "*.csv")
//                    new FileChooser.ExtensionFilter("PNG", "*.png")
                );
        }
     
        private void openFile(File file) {
            try {
                desktop.open(file);
            } catch (IOException ex) {
                Logger.getLogger(FileChooserSample.class.getName()).log(
                    Level.SEVERE, null, ex
                );
            }
        }
}

class EditingCell extends TableCell<XYChart.Data, Number> {
	 
    private TextField textField;
    
    public EditingCell() {}
    
    @Override
    public void startEdit() {
        super.startEdit();
        
        if (textField == null) {
            createTextField();
        }
        
        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.selectAll();
    }
    
    @Override
    public void cancelEdit() {
        super.cancelEdit();
        
        setText(String.valueOf(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(Number item, boolean empty) {
        super.updateItem(item, empty);
        
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap()*2);
        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            
            @Override
            public void handle(KeyEvent t) {
            	 
                if (t.getCode() == KeyCode.ENTER) {
                    commitEdit(Double.parseDouble(textField.getText()));
                } else if (t.getCode() == KeyCode.ESCAPE) {
                    cancelEdit();
                }
            }
        });
    }
    
    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
    
    
    
    
}

class SuperScatterChart extends ScatterChart<Number, Number>{

    private final ImageView iv1 ;

    public SuperScatterChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);
        //iv1 = new ImageView(new Image("file:res/cietrace1.png",905,1090,false,true));
        iv1 = new ImageView(new Image(getClass().getResourceAsStream("/cietrace1.png"),825,1090,false,true)); 
        getPlotChildren().add(iv1);
    }
		
		
		
	
    @Override
    protected void layoutPlotChildren() {


        super.layoutPlotChildren();
    }
    
    public static void saveToFile(Image image) {
        File outputFile = new File("C:/JavaFX/");
        BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
        try {
          ImageIO.write(bImage, "png", outputFile);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
		
		
		
	}



