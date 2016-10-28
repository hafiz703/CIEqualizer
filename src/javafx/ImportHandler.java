package javafx;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import com.opencsv.CSVReader;

import javafx.scene.chart.XYChart;

public class ImportHandler {
	String inputUrl;
	ArrayList<XYChart.Series<Number,Number>> inputSeries;
	DataHandler dh;
	
	
	
	
	public ImportHandler(String url,double[][] d65){
		inputUrl = url;
		inputSeries = parseCSV(inputUrl);
		
		
 		
		
		
	}
	
	public static ArrayList<XYChart.Series<Number,Number>> parseCSV(String uri){
    	
		CSVReader reader;
		List<String[]> data = null;
		double[][] container = null;		
		
		ArrayList<XYChart.Series<Number,Number>> resFinal = new ArrayList<>();
		int count=0;
		HashSet<Integer> toRemove = new HashSet<>();
	     
	    try{
	    	 reader = new CSVReader(new FileReader(uri));
	    	 data = reader.readAll();
	    	 for(int i=0;i<data.size();i++){
	    		 for(String j : data.get(i)){
	    			 try{
	    				 Double d = Double.parseDouble(j);
	    				 
	    			 }catch(NumberFormatException e){
	    				 toRemove.add(i);
	    			 }
	    		 }
	    		 
	    	 }
	    	 
	    	 for(int i:toRemove){
	    		  data.remove(i);
	    	 }
 	    	 	
				
			 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    container = new double[data.get(0).length][data.size()];
	    
	        
	    for(int i=0;i<data.get(0).length;i++){
	    	double[] inner = new double[data.size()];
	    	for(int j=0;j<data.size();j++){
	    		inner[j] = Double.parseDouble(data.get(j)[i]);
	    	}
	    	container[i]= inner; 
	    }
	    
	    for(int i=1;i<container.length;i++){
	    	XYChart.Series<Number,Number> resInner = new XYChart.Series<Number,Number>();
	    	for(int j=0;j<container[i].length;j++){
	    		resInner.getData().add(new XYChart.Data<Number,Number>(container[0][j], container[i][j]));
	    	}
	    	resFinal.add(resInner);
//	    	System.out.println(resInner.getData().toString());
	    }
	   
//	    for(XYChart.Series<Number,Number> k: resFinal){
//	    	System.out.println(k.getData().toString());
//	    }
	    return resFinal;
		
	}

}
