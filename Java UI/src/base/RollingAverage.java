package base;


public class RollingAverage {
	 	private int size;
	    private double total = 0d;
	    private int index = 0;
	    private double samples[];
	    private double sum = 0; 
	    private double differenceArray []; 
	  //  private double tempArray []; 
	    
	    public RollingAverage(int size) {
	        this.size = size;
	        samples = new double[size];
	        differenceArray = new double[size]; 
	       // tempArray = new double[size]; 
	        for (int i = 0; i < size; i++) samples[i] = 0d;
	        //for (int i = 0; i < size; i++) tempArray[i] = 0d;
	        for (int i = 0; i < size; i++) differenceArray[i] = 0d;
	        
	        //ArrayList a = new ArrayList(); 
	        
	    }

	    public void add(double input) {
	    	

	    	
	    	double tempArray []; 
	    	tempArray = new double[size];
	    	sum = 0;
	    	
	    	for(int i =1; i<size; i++) { 
	    		tempArray[i] = samples[i-1]; 
	    	}
	    	tempArray[0] = input; 
	    	
	    	samples = tempArray;
	    	               
	    	for(int i =0; i<size; i++) { 
	    		sum = samples[i] + sum; 
	    	}
	        

	       // System.out.println("average is: " + sum);
	    	   
	    }

	    public double getAverage() {
	    	//System.out.println(total / size); 
	        return sum / size;
	    }   
	    
	    public double getStandardDev() {
	    	
	    	double stdev = 0; 	
	    	double temp = 0; 
	    	double sumStdev = 0; 
	    	
	    	for(int i =0; i<size; i++) { 
	    		temp = samples[i] - getAverage();
	    		sumStdev = Math.pow(temp,2) + sumStdev; 
	    	}
	    	
	    	stdev = Math.sqrt(sumStdev/size);
	    	//system.out.println("st dev: " + stdev);
	    	return stdev;
	    			
	    }
	    
		public boolean isDevicePresent(int deviceID) {
			boolean present = false ;
			for(int i =0; i<size; i++) { 
				if (samples[i]==(double)deviceID) { 
					present = true; 
				}
			}
			return present; 
			
		}
	    
}//end rolling average



