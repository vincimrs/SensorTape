package base;
import java.text.DecimalFormat;

import processing.core.*; //means we are importing everything from the processing core
//import processing.opengl.*;
import toxi.geom.*;

import java.util.ArrayList;

import processing.core.PApplet;
public class visualize {
	private PApplet applet;
	
	int yline = 500;

	int ypos = 500;
	int xpos = 100; 
	
	float xglobal = 0 ; 
	float yglobal = 0 ;
	
	public visualize(PApplet _applet) {
		applet = _applet;
	}
	
	public void drawSensors(ArrayList <sensorNode>listSensors, int type) { 
		  int [] foo = {1,2,3};
		  int [] sho = {1,0,0,1};
		  for (int i = 0; i<listSensors.size(); i++) { 
			  if(i>0) { 
			  drawOneSensor(i, listSensors.get(i).getLatest(), type,listSensors.get(i).getTurn(), 
					  listSensors.get(i).getXPosition(), listSensors.get(i).getYPosition(), 
					  listSensors.get(i-1).getXPosition(), listSensors.get(i-1).getYPosition(), 
					  listSensors.get(i-1).getLatest());
			  }
			  else  { 
				  drawOneSensor(i, listSensors.get(i).getLatest(), type,listSensors.get(i).getTurn(), 
				       listSensors.get(i).getXPosition(), listSensors.get(i).getYPosition(), 
					   listSensors.get(i).getXPosition(), listSensors.get(i).getYPosition(),
					   listSensors.get(i).getLatest());
			  }
			  //listSensors.get(i).printLatest();
		  }	
		  xglobal = 0; 
		  yglobal = 0 ;
	}
	
	public void drawOneSensor(int position, int[] data, int type, int orientation, int xpos,int ypos, int xprev, int yprev, int[] prevData) { 
		
			//applet.scale((float) 0.9);
		  if (orientation == 1) { 
			  yline = yline-60;
		  }
		  //System.out.println(data[1]);
		  if (type ==1) {
			  //applet.fill(200,200,0); //or fill with data[1], to change color
			  applet.fill(data[1],0,0);
			  applet.textSize(20); 
			  applet.text(data[1], xpos, ypos);
			  
		  }
		  
		  if (type ==2) { 
			  applet.fill(data[1],0,0);
			  //applet.fill(255,255,255); 
			  applet.textSize(20);
			  applet.text(convertToTemperature(data[2]), xpos, ypos);
			  
		  }
		  
		 
		  applet.rect(xpos,ypos, 60,60);
		 // applet.rect(10+70*position,60*orientation, 60,60);
		 // System.out.println(orientation);
		  
		  applet.pushMatrix(); 
		  
		 // applet.translate(xpos,ypos+400); 
		  
		  if (data.length>5) {
				float[] q = new float[4];
				Quaternion quat = new Quaternion(1, 0, 0, 0);
				 q[0] = (data[4]) / 16384.0f;
				 q[1] = (data[5]) / 16384.0f;
				 q[2] = (data[6]) / 16384.0f;
				 q[3] = (data[7]) / 16384.0f;
				 for (int i = 0; i < 4; i++) if (q[i] >= 2) q[i] = -4 + q[i];
				 quat.set(q[0], q[1], q[2], q[3]);
				 
			    float[] axis = quat.toAxisAngle();
	    
			    float newPosX = xglobal+200*applet.cos(axis[2]*axis[0]); 
			    float newPosY = (yglobal)-200*applet.sin(axis[2]*axis[0]);
			    
			    applet.line(xglobal,yglobal+800,newPosX,newPosY+800);
			    xglobal = newPosX; 
			    yglobal = newPosY; 
			    
			    //System.out.println(data[0] +"," + xglobal + ", " + yglobal + "  ," + xpos + "," + ypos);
			    
    				//applet.line(xpos, ypos+400, newPosX, newPosY);
			    
			    applet.translate(newPosX, newPosY+800);
			    applet.rotate(axis[0], -axis[1], axis[3], axis[2]);
		  
		  }else  
		  { 
			  applet.rotateX(applet.radians(-20));
		  }
		  
		  //drawCylinder(50, 50,  -data[1]/2, 40, data[1]  ); 
		  drawCylinder(50, 50,  -50, 40, 50  ); 
		  
		  applet.popMatrix(); 
		
		  applet.stroke(126);
		  applet.line(30, 20, 85, 75);
		  
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
	
	
	void drawCylinder(float topRadius, float bottomRadius, float tall, int sides, int colorFill) {
	    float angle = 0;
	    float angleIncrement = applet.TWO_PI / sides;
	    
	    applet.noStroke();
	    applet.beginShape(applet.QUAD_STRIP);
	    
	    
	    for (int i = 0; i < sides + 1; ++i) {
	    		applet.vertex(topRadius*applet.cos(angle), 0, topRadius*applet.sin(angle));
	    		applet.vertex(bottomRadius*applet.cos(angle), tall, bottomRadius*applet.sin(angle));
	        angle += angleIncrement;
	    }
	    applet.endShape();
	    
	    //applet.stroke(154);
	    // If it is not a cone, draw the circular top cap
	    if (topRadius != 0) {
	        angle = 0;
	        applet.beginShape(applet.TRIANGLE_FAN);
	        //applet.fill(200, 0, 0, 255);
	        // Center point
	        applet.vertex(0, 0, 0);
	        for (int i = 0; i < sides + 1; i++) {
	        	applet.vertex(topRadius * applet.cos(angle), 0, topRadius *applet. sin(angle));
	            angle += angleIncrement;
	        }
	        applet.endShape();
	    }
	  
	    // If it is not a cone, draw the circular bottom cap
	    if (bottomRadius != 0) {
	        angle = 0;
	        applet. beginShape(applet.TRIANGLE_FAN);
	    
	        // Center point
	        applet. vertex(0, tall, 0);
	        for (int i = 0; i < sides + 1; i++) {
	        	applet.vertex(bottomRadius * applet.cos(angle), tall, bottomRadius * applet.sin(angle));
	            angle += angleIncrement;
	        }
	        applet.endShape();
	    }
	}
	
	
	void drawAirplane(){ 
		  applet.pushMatrix();
		  applet.translate(0, 0, -120);
		  applet.rotateX(applet.PI/2);
		  drawCylinder(0, 20, 20, 8);
		  applet.popMatrix();
		    
		    // draw wings and tail fin in green
		  applet. fill(0, 255, 0, 200);
		  applet. beginShape(applet.TRIANGLES);
		  applet. vertex(-100,  2, 30); applet.vertex(0,  2, -80); applet.vertex(100,  2, 30);  // wing top layer
		  applet.  vertex(-100, -2, 30);applet. vertex(0, -2, -80);applet. vertex(100, -2, 30);  // wing bottom layer
		  applet.  vertex(-2, 0, 98); applet.vertex(-2, -30, 98);applet. vertex(-2, 0, 70);  // tail left layer
		  applet.  vertex( 2, 0, 98);applet. vertex( 2, -30, 98);applet. vertex( 2, 0, 70);  // tail right layer
		  applet.  endShape();
		  applet.   beginShape(applet.QUADS);
		  applet.  vertex(-100, 2, 30);applet. vertex(-100, -2, 30); applet.vertex(  0, -2, -80); applet.vertex(  0, 2, -80);
		  applet.  vertex( 100, 2, 30); applet.vertex( 100, -2, 30); applet.vertex(  0, -2, -80); applet.vertex(  0, 2, -80);
		  applet.  vertex(-100, 2, 30); applet.vertex(-100, -2, 30); applet.vertex(100, -2,  30); applet.vertex(100, 2,  30);
		  applet.  vertex(-2,   0, 98); applet.vertex(2,   0, 98); applet.vertex(2, -30, 98); applet.vertex(-2, -30, 98);
		  applet.  vertex(-2,   0, 98); applet.vertex(2,   0, 98); applet.vertex(2,   0, 70); applet.vertex(-2,   0, 70);
		  applet.  vertex(-2, -30, 98); applet.vertex(2, -30, 98); applet.vertex(2,   0, 70); applet.vertex(-2,   0, 70);
		  applet.  endShape();
		    
		  applet.  popMatrix();
	}
	
	void drawCylinder(float topRadius, float bottomRadius, float tall, int sides) {
	    float angle = 0;
	    float angleIncrement = applet.TWO_PI / sides;
	    applet.beginShape(applet.QUAD_STRIP);
	    for (int i = 0; i < sides + 1; ++i) {
	    	applet.vertex(topRadius*applet.cos(angle), 0, topRadius*applet.sin(angle));
	    	applet.vertex(bottomRadius*applet.cos(angle), tall, bottomRadius*applet.sin(angle));
	        angle += angleIncrement;
	    }
	    applet.endShape();
	    
	    // If it is not a cone, draw the circular top cap
	    if (topRadius != 0) {
	        angle = 0;
	        applet.beginShape(applet.TRIANGLE_FAN);
	        
	        // Center point
	        applet.vertex(0, 0, 0);
	        for (int i = 0; i < sides + 1; i++) {
	        	applet.vertex(topRadius * applet.cos(angle), 0, topRadius * applet.sin(angle));
	            angle += angleIncrement;
	        }
	        applet.endShape();
	    }
	  
	    // If it is not a cone, draw the circular bottom cap
	    if (bottomRadius != 0) {
	        angle = 0;
	        applet.beginShape(applet.TRIANGLE_FAN);
	    
	        // Center point
	        applet.vertex(0, tall, 0);
	        for (int i = 0; i < sides + 1; i++) {
	        	applet.vertex(bottomRadius * applet.cos(angle), tall, bottomRadius * applet.sin(angle));
	            angle += angleIncrement;
	        }
	        applet.endShape();
	    }
	}
	
}//end class

