package javafx;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.opencsv.CSVReader;

import javafx.scene.chart.XYChart;

public class DataHandler {
	public static final int UPPER_LIMIT = 471;
	double x;
	double y;
	int R;
	int G;
	int B;
	String hex;
	double[][] d65;
	public XYChart.Series<Number, Number> savedSeries;
	public DataHandler(double[][] d65data){
		//series1 = new XYChart.Series<Number, Number>();		
		this.d65 = d65data;
		
	}
 
	
	
	
	public double[][] multiplyReflectance(XYChart.Series<Number,Number> seriesdata){
		double[][] result=new double[471][3];
		int max = seriesdata.getData().get(seriesdata.getData().size()-1).getXValue().intValue();
		int min = seriesdata.getData().get(0).getXValue().intValue();
		int z=0;
		//System.out.println("max: "+max+" min: "+min);
		for(int i=0;i<seriesdata.getData().size()-1;i++){
			double reflectancePoints[][] = getLineEqn(seriesdata.getData().get(i).getXValue().intValue(), seriesdata.getData().get(i).getYValue().doubleValue(), seriesdata.getData().get(i+1).getXValue().intValue(), seriesdata.getData().get(i+1).getYValue().doubleValue());
			//System.out.println(Arrays.deepToString(reflectancePoints));
			for(int j=0;j<reflectancePoints.length;j++){
				if(reflectancePoints[j][0]>=360 && reflectancePoints[j][0]<=830){
					int wlength = (int) reflectancePoints[j][0];
					//System.out.println("j: "+j);
					//System.out.println("d65x: "+d65data[z][2]+" z: "+z+" wave: "+wlength);
					
					result[z][0] = d65[z][2]*reflectancePoints[j][1];
					result[z][1] = d65[z][3]*reflectancePoints[j][1];
					result[z][2] = d65[z][4]*reflectancePoints[j][1];
					z++;
					
					
					
				}
			
				
			}
		
			
		
		}	
		return result;
	}
	
	public static double[][] getLineEqn(int x1, double y1, int x2, double y2){
		double grad = (y2-y1)/(x2-x1);
		double[][] result = new double[x2-x1][2];
		int start = x1;
		for(int i=0;i<result.length;i++){
			double[] temp = new double[2];
			if(start>=360 && start<=830){
				temp[0] = start;
				temp[1] = grad*(start-x1)+y1;
				result[i] = temp;
			}
			start++;
			//System.out.println(start);
		}
//		System.out.println("result: "+Arrays.deepToString(result));
		return result;
		
	}
	
	public void calc(double[][] rxyz){
		double[][] srgb = new double[][]{{3.24045,-1.53714,-0.49853},{-0.96927,1.87601,0.04156 },{0.05564,-0.20403,1.05723}};
		
		
		double sumd65y = 10567.08167;
		
		
		double sumd65xMultR=0;
		double sumd65yMultR=0;
		double sumd65zMultR=0;
		
		for(int i=0;i<rxyz.length;i++){
			sumd65xMultR+=rxyz[i][0];
			sumd65yMultR+=rxyz[i][1];
			sumd65zMultR+=rxyz[i][2];
			 
		}
		
		double X = (100.0/sumd65y)*sumd65xMultR;
		double Y = (100.0/sumd65y)*sumd65yMultR;
		double Z = (100.0/sumd65y)*sumd65zMultR;
		
		double smallx = X/(X+Y+Z);
		double smally = Y/(X+Y+Z);
		double smallz = Z/(X+Y+Z);
		
		this.x = smallx;
		this.y = smally;
		
		
		double Xto1 = X/100.0;
		double Yto1 = Y/100.0;
		double Zto1 = Z/100.0;
		
		double[][] XYZto1matrix = new double[][]{{Xto1},{Yto1},{Zto1}};
		double[][] linear = multiplicar(srgb,XYZto1matrix);
		
		 
		
		double Rlinear = linear[0][0];
		double Glinear = linear[1][0];
		double Blinear = linear[2][0];
		
		double rnonlinear;
		double gnonlinear;
		double bnonlinear;
		
		if(Rlinear>0.0031308){
			rnonlinear = 1.055*Math.pow(Rlinear, 1.0/2.4) - 0.055;
		}
		
		else{
			rnonlinear = Rlinear*12.92;
		}
		
		if(Glinear>0.0031308){
			gnonlinear = 1.055*Math.pow(Glinear, 1.0/2.4) - 0.055;
		}
		
		else{
			gnonlinear = Glinear*12.92;
		}
		
		if(Blinear>0.0031308){
			bnonlinear = 1.055*Math.pow(Blinear, 1.0/2.4) - 0.055;
		}
		
		else{
			bnonlinear = Blinear*12.92;
		}
		
		if (rnonlinear>1){
			rnonlinear=1;
		}
		if (gnonlinear>1){
			gnonlinear=1;
		}
		if (bnonlinear>1){
			bnonlinear=1;
		}
		if (rnonlinear<0){
			rnonlinear=0;
		}
		if (gnonlinear<0){
			gnonlinear=0;
		}
		if (bnonlinear<0){
			bnonlinear=0;
		}
		
		//System.out.println(rnonlinear*255 + " "+ gnonlinear*255 + " "+ bnonlinear*255);
		
		int Rto255 = (int) (Math.round(rnonlinear*255));
		int Gto255 = (int) (Math.round(gnonlinear*255));
		int Bto255 = (int) (Math.round(bnonlinear*255));
		
		//System.out.println(Rto255+" "+ Gto255+ " "+ Bto255);
		
		this.R = Rto255;
		this.G = Gto255;
		this.B = Bto255;
		
		
		
		Color color = new Color(Rto255,Gto255,Bto255);
		String hex = Integer.toHexString(color.getRGB() & 0xffffff);
		while (hex.length() < 6) {
		    hex = "0" + hex;
		}
		hex = "#" + hex;
		this.hex = hex;
		//System.out.println(this.x+"  "+this.y +" "+ hex);
		
		
		
		
		
		
	}
	
	public static double[][] multiplicar(double[][] A, double[][] B) {

        int aRows = A.length;
        int aColumns = A[0].length;
        int bRows = B.length;
        int bColumns = B[0].length;

        if (aColumns != bRows) {
            throw new IllegalArgumentException("A:Rows: " + aColumns + " did not match B:Columns " + bRows + ".");
        }

        double[][] C = new double[aRows][bColumns];
        for (int i = 0; i < aRows; i++) {
            for (int j = 0; j < bColumns; j++) {
                C[i][j] = 0.00000;
            }
        }

        for (int i = 0; i < aRows; i++) { // aRow
            for (int j = 0; j < bColumns; j++) { // bColumn
                for (int k = 0; k < aColumns; k++) { // aColumn
                    C[i][j] += A[i][k] * B[k][j];
                }
            }
        }
        
        
        return C;
    }
	
	


}
