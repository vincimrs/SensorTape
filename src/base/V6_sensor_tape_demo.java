package base;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import processing.core.*; //means we are importing everything from the processing core



public class V6_sensor_tape_demo extends PApplet {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PApplet.main(new String[] { "--present", "base.V4_sensor_tape_demo" });
	}
	
	visualize drawStuff = new visualize(this);

	int deviceID = 0; 
	DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

	
	
	Date dateToday = new Date();
	String filename= "testData" + "_" + dateFormat.format(dateToday) +".txt";
	String filenameTraining= "trainingData" + "_" + dateFormat.format(dateToday) +".arff";
	//System.out.println(filename);
	// Currentdate = new Date();
	boolean start = true; 
	Serial myPort;        // The serial port 
	int deviceCounter = 0;
	sensorNode[] tempArraySensors = new sensorNode[128];
	ArrayList <sensorNode>listSensors = new ArrayList<sensorNode>(); 
	String direction = "right";
	int visualizeSensorType  = 1;
	boolean orientationFlag = true; 
	
	public void setup() {
	  size(1850, 1000); // animation is much smoother in P2D; text looks better with JAVA2D
	  background(255);
	  
	  for (int i= 0; i<Serial.list().length; i++) { 
		  System.out.println(Serial.list()[i]);
	  }
	  
	  myPort = new Serial(this, Serial.list()[0], 9600);
	 

	  System.out.println("Start");
	  frameRate(30);
	  
	}//end setup
	 
	
	public void draw() {

	  smooth();
	  background(255);
	  
	  if (start) { 
		  textSize(35);
		  text("Searching: " + Integer.toString(deviceCounter) + "/128", 300,300);
		  text("Found: " + Integer.toString(listSensors.size()),300,400);
	  }
	  //if (!start) {  
	  if (visualizeSensorType == 1) { 
		  drawStuff.drawSensors(listSensors,1);
	  } 
	  else if (visualizeSensorType ==2)  { 
		  drawStuff.drawSensors(listSensors,2);
	  }
	  //}
	
		  if (!start && orientationFlag) {
			  determineOrientation(); 
			  orientationFlag = false; 
		  }
	}//end draw


	 public void serialEvent (Serial myPort)  {
		 try { 
		 // get the ASCII string:
		 String inString = myPort.readStringUntil('\n');	 
		 
		 //Check if the string is good
		 //I use the 'S' character at the beggining
		 //Otherwise, I get a null point, and computer may crash (MAC 10.6)
		 if (inString != null && inString.charAt(0) == 'A' && start) {
			 System.out.println(inString);
			 inString = trim(inString);
			 String[] list = split(inString, ',');
			 if(list.length == 8 ) {
				 deviceID = Integer.parseInt(list[1]);
				 if (Integer.parseInt(list[1]) == deviceCounter) {
					 System.out.println("Found: " + Integer.toString(deviceCounter));
					 
					 int [] cuts = {Integer.parseInt(list[4]),Integer.parseInt(list[5]),Integer.parseInt(list[6]),Integer.parseInt(list[7])};					 				 
					 sensorNode tempSensorNode = new sensorNode(Integer.parseInt(list[1]), 
							 Integer.parseInt(list[2]), Integer.parseInt(list[3]), cuts ); 	
					 listSensors.add(tempSensorNode);				 
			 		}				 
			 }
			 deviceCounter++;
		 }//end if start
		 
		
		 else if (inString != null && inString.charAt(0) == 'S') {
			 start = false;
		   // trim off any whitespace:
			 System.out.println(inString);
			 inString = trim(inString);
			 String[] list = split(inString, ',');
			 
			 sensorNode tempSensorNode = listSensors.get(Integer.parseInt(list[1])-1);
			// int tempInput[] = null;
			 int tempInput[]; // = {Integer.parseInt(list[2]), Integer.parseInt(list[3]),Integer.parseInt(list[4])};
			 tempInput = new int [list.length-1];
			 for (int i =1; i<list.length; i++) { 
				 tempInput[i-1] = Integer.parseInt(list[i]); 
			 }		 
			 tempSensorNode.addData(tempInput);	
			 listSensors.remove(Integer.parseInt(list[1])-1);
			 listSensors.add(Integer.parseInt(list[1])-1,tempSensorNode );
		 }//end if String is correct
		 
		 } catch(Exception e) {System.out.println("Error: " + e );}
		    
	 }//end serial Event
	
	 

	 
	public void mousePressed() { 
				
		for (int i=0; i<listSensors.size(); i++){ 
			if(mouseX > listSensors.get(i).getXPosition()  && mouseX < listSensors.get(i).getXPosition()+60  && mouseY > listSensors.get(i).getYPosition()&& mouseY < listSensors.get(i).getYPosition()+60) { 
				//The original if(mouseX >40 && mouseX<80 && mouseY>40 && mouseY<100)
				  char tempC = 'c'; 
				  sendCommand(i+1,tempC);
			}//end if 
		}//end for
		
	}//end mouse pressed 
	
	public void keyPressed()
	{
	  if (key == '1') {
		  visualizeSensorType  =1;
	  }
	  
	  else if (key == '2')
		  visualizeSensorType  =2;

	  else if (key =='3') { 
		  char c = 'a'; 
		  //myPort.write(c);
		  char tempC = 'c';
		  sendCommand(3,tempC);
	  }
	}//end keyPr essed
	
	
	// determing how it holds hands
	public void determineOrientation() { 
		
		
		int [] cutData = {0,0,0,0}; 
		//int [] arr1 = {0,0,0,1}; 
		int [] arr2 = {1,0,0,1}; 
		//int [] arr3 = {1,0,0,0};
		//int [] arr4 = {1,0,0,0};
		
		int xpos = 100; 
		int ypos = 300;
		
		
		for (int i=0; i<listSensors.size(); i++ ) {
			
		//	listSensors.get(i).printCutData();
			
			//cutData = listSensors.get(i).getCutData(); 
			if (Arrays.equals(listSensors.get(i).getCutData(), arr2)) { 
				 sensorNode tempSensorNode = listSensors.get(i);
				 tempSensorNode.setTurn(1);	
				 listSensors.remove(i);
				 listSensors.add(i,tempSensorNode);
			}
				
		}//end for
		
		

		
		for (int i=0; i<listSensors.size(); i++ ) { 
			
			if (listSensors.get(i).getTurn()==0) { 
				
				/*
				if (direction.equals("right")) 
					xpos = xpos +60; 
				else if (direction.equals("left"))
					xpos = xpos - 60;
				*/
				
				xpos = xpos + 60;
			}
			
			else if (listSensors.get(i).getTurn() ==1){ 
				ypos = ypos - 60; 
				xpos = xpos + 60; 
				direction = "left";
			}
			
			else if (listSensors.get(i).getTurn() ==2) { 
				ypos = ypos - 60; 
				xpos = xpos - 60;
			}
			
			 sensorNode tempSensorNode = listSensors.get(i);
			 tempSensorNode.setPosition(xpos,ypos);	
			 listSensors.remove(i);
			 listSensors.add(i,tempSensorNode);
			
		}
		
		
	}//end determineOrientation
	
	
	public void sendCommand( int node, char command) {
		//nice page explaining: 
		//http://blog.danielkerris.com/?p=349 		
		myPort.write((char)(node/256));
		myPort.write(node & 0xff);
		myPort.write(command);
		
	}
	
	
	
}//end class
	

//Check for 135 degree cuts
/*
if (cutData[3] == 1) { 
	 cutData = listSensors.get(i+1).getCutData();
	 if (cutData[0] == 1) { 
		 sensorNode tempSensorNode = listSensors.get(i+1);
		 tempSensorNode.setTurn(1);	
		 listSensors.remove(i+1);
		 listSensors.add(i+1,tempSensorNode);
		 
		 
	 }
	 else if (cutData[0])
}//end if cutData[3]
*/

 







