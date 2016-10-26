package javafx;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResultView extends Object {
	private SimpleIntegerProperty numb;
	private SimpleIntegerProperty rval ;
	private SimpleIntegerProperty gval ;
	private SimpleIntegerProperty bval ;
	
	private SimpleDoubleProperty x;
	private SimpleDoubleProperty y;
	private SimpleStringProperty hex;
	
	public ResultView () {
    }
	
	public ResultView(Integer num,Double xval, Double yval, Integer r, Integer g, Integer b, String hexadecimal){
		numb = new SimpleIntegerProperty(num);
		
		rval = new SimpleIntegerProperty(r);
		gval = new SimpleIntegerProperty(g);
		bval = new SimpleIntegerProperty(b);
		
		x = new SimpleDoubleProperty(xval);
		y = new SimpleDoubleProperty(yval);
		
		hex = new SimpleStringProperty(hexadecimal);
		
		
	}
     
 
    public Integer getNumb() {
        return numb.get();
    }
 
    public Integer getRval() {
        return rval.get();
    }
 
    public Integer getGval() {
        return gval.get();
    }
 
    public Integer getBval() {
        return bval.get();
    }
    
    public Double getX() {
        return Math.round(x.get() * 1000d) / 1000d;
    }
    
    public Double getY() {
    	return Math.round(y.get() * 1000d) / 1000d;
    }
    
    public String getHex(){    	
    	return hex.get();
    }
}