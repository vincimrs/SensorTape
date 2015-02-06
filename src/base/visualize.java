package base;
import java.text.DecimalFormat;
import java.util.ArrayList;

import processing.core.PApplet;
public class visualize {
	private PApplet applet;
	
	int yline = 500;

	int ypos = 500;
	int xpos = 100; 
	public visualize(PApplet _applet) {
		applet = _applet;
	}
	
	public void drawSensors(ArrayList <sensorNode>listSensors, int type) { 
		  int [] foo = {1,2,3};
		  int [] sho = {1,0,0,1};
		  for (int i = 0; i<listSensors.size(); i++) { 
			  drawOneSensor(i, listSensors.get(i).getLatest(), type,listSensors.get(i).getTurn(), 
					  listSensors.get(i).getXPosition(), listSensors.get(i).getYPosition());
			  //listSensors.get(i).printLatest();
		  }	
	}
	
	public void drawOneSensor(int position, int[] data, int type, int orientation, int xpos,int ypos ) { 
		
		
		  if (orientation == 1) { 
			  yline = yline-60;
		  }
		  //System.out.println(data[1]);
		  
		  
		  if (type ==1) { // light sensor data
			  applet.fill(data[1],0,0); 
		  }
		  
		  if (type ==2) { // temperture sensor data
			//  applet.fill(data[2]/10,0,0);
			  //applet.fill(255,255,255); 
			  applet.textSize(20);
			  applet.text(convertToTemperature(data[2]), xpos, ypos);
		  }
		  
		  applet.rect(xpos,ypos, 60,60);
		 // applet.rect(10+70*position,60*orientation, 60,60);
		 // System.out.println(orientation);
		
	}
	
	
	String convertToTemperature(int RawADC) {
		 double Temp;
		 Temp = applet.log((float) (10000.0*((1024.0/RawADC-1)))); 
//		         =log(10000.0/(1024.0/RawADC-1)) // for pull-up configuration
		 Temp = 1 / (0.001129148 + (0.000234125 + (0.0000000876741 * Temp * Temp ))* Temp );
		 Temp = Temp - 273.15;            // Convert Kelvin to Celcius
		// Temp = (Temp * 9.0)/ 5.0 + 32.0; // Convert Celcius to Fahrenheit
		 
		 
		DecimalFormat df = new DecimalFormat("#.0");
		String formatedString = df.format(Temp);	    
		    
		 return formatedString;
		}
	
}
