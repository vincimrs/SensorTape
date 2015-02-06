package base;
import processing.core.PApplet; 

public class Channel {
	
	//color colour;    // color of dots to plot
	  
	  int dataPos;     // where does next data point go?
	  int startPos;    // where do we start plotting?
	  int nPoints;     // number of points currently in the array
	  int nSamples;    // number of samples (copy this from parent)
	  float [ ] points;  // the data points to plot
	  private float prevX;    // remember previous point
	  private float prevY;
	  private Stripchart parent;
	  boolean visible;  // allows you to hide or show a channel
	 
	  PApplet parent2; 
	  
	  Channel(PApplet p) {
	    // All stripes start at 0
		  parent2 = p;
		  int colour = parent2.color(100,100,100); 
	  }
	  
	  
	  
	  Channel(Stripchart parent)
	  {
	    nPoints = 0;
	    dataPos = 0;
	    startPos = 0;
	    this.parent = parent;
	    nSamples = parent.nSamples;
	    points = new float[parent.nSamples];
	    //colour = c;
	    visible = true;
	  }
	 
	  public void addData(float value)
	  {
		//System.out.println("addData called"); 
	    points[dataPos] = value;   
	    dataPos = (dataPos + 1) % nSamples; // wrap around when array fills
	    
	     //* If the array isn't full yet, add to the end of the array
	     //* Otherwise, the start point for plotting moves through
	     //* the array.
	     
	    if (nPoints < nSamples)
	    {
	      nPoints++;
	    }
	    else
	    {
	      startPos = (startPos + 1) % nSamples;
	    }
	   // System.out.println("addData done"); 
	  }
	 
	  public void display()
	  {
		
	    int arrayPos;
	    float yPos;
	 
	    for (int i = 0; i < nPoints; i++)
	    {
	      arrayPos = (startPos + i) % nSamples;
	      if (parent.period > 0 && arrayPos % parent.period == 0)
	      {
	        parent2.stroke(192);
	        parent2.line(nSamples - nPoints + i, Stripchart.VSPACE,
	        nSamples - nPoints + i, parent.h - Stripchart.VSPACE);
	      }
	      if (visible)
	      {
	        parent2.stroke(parent2.color(100,100,100));
	        yPos = (float) (Stripchart.VSPACE +
	          parent.h * (1.0 - (points[arrayPos] - parent.minValue) /
	          (parent.maxValue - parent.minValue)));
	 
	        // Draw a point for the first item, then connect all the other points with lines
	        if (i == 0)
	        {
	          parent2.point(nSamples - nPoints + i, yPos);
	        }
	        else
	        {
	          parent2.line(prevX, prevY, nSamples - nPoints + i, yPos);
	        }
	        prevX = nSamples - nPoints + i;
	        prevY = yPos;
	      }
	    }
	  }
	 
	  void toggle()
	  {
	    visible = ! visible;
	  }
	
	

}//end Channel
