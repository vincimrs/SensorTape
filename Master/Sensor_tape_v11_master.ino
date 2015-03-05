//In V7 added ability for master to talk to the nodes, not only request data
//This is for the new PCB (V2)
//Implemeted new auto search 
//Added software peer to peer serial comunications 
//Added more delay, so the IMU has time to setup
#include <Wire.h>
#include <SoftwareSerial.h>

int ledBlue  =8; 
int ledRed = 9;
int resistanceSenseEnable = 5; 
boolean lookUpPresence[128];
int lookUpNumberOfSensors[128]; 
unsigned int output = 0 ;
unsigned int output2 = 0; 
unsigned int  output3 = 0;
unsigned int deviceID = 200;
unsigned char char1 = 0; 
unsigned char char2 =  0; 
unsigned char char3 = 0; 
unsigned char char4 = 0;
unsigned char char5 = 0; 
unsigned char char6 = 0; 
unsigned char char7 = 0; 
unsigned char char8 = 0; 
int x = 0;
int incomingByte = 0; 
char c = 'a';

SoftwareSerial p2pSerial(2,6); //RT, TX
// the setup routine runs once when you press reset:
void setup() {                
  // initialize the digital pin as an output.
  pinMode(ledBlue, OUTPUT);   
  pinMode(resistanceSenseEnable, OUTPUT);
  Serial.begin(9600); 
  Wire.begin(); // Start I2C Bus as Master
  digitalWrite(resistanceSenseEnable,LOW);
  
  
  p2pSerial.begin(9600);
  delay(3500);//master should transmit only after slaves are ready
  int foo = 1;
  p2pSerial.write(foo);
  delay(50);//slaves need some time to determine their addresses
  findWhoIsAround();
   
}

// the loop routine runs over and over again forever:
void loop() {
  //digitalWrite(ledBlue, LOW); 
  delay(50);               // wait for a second 
  //digitalWrite(ledBlue, HIGH);
  //delay(25);               // wait for a second
   digitalWrite(ledBlue, !digitalRead(ledBlue)); 
 
 
 
  for(int i=0; i<128; i++) { 
    if(lookUpPresence[i]) { 
      if (i == 8 ) {
      requestFromDevice(i,16); //this is for the motion sensor 
    }
    else requestFromDevice(i,16);
    }
  }//end for
  
  /*
   if (Serial.available() > 0) {
        // read the incoming byte:
        if (Serial.read() == c) {
          sendByteToSlave(3); 
        }
    }
  */
  if (Serial.available() >= 3) { 
    int whereToSend = Serial.read() *256; 
    whereToSend = whereToSend + Serial.read(); 
    byte command1 = Serial.read();
    byte command2 = Serial.read(); 
    byte commant3 = Serial.read(); 
    sendByteToSlave(whereToSend, command1,command2,commant3);
  }
  
  

}//end loop


void setToZeros() { 
  unsigned int output = 0 ;
  unsigned int output2 = 0; 
  unsigned int  output3 = 0;
  unsigned int deviceID = 200 ;
  unsigned char char1 = 0; 
  unsigned char char2 =  0; 
  unsigned char char3 = 0; 
  unsigned char char4 = 0;
  unsigned char char5 = 0; 
  unsigned char char6 = 0; 
  unsigned char char7 = 0; 
  unsigned char char8 = 0; 
  
}


void findWhoIsAround() { 
    for (int i=0; i<128; i++) { 
      requestFromDeviceDuringStart(i,14); 
      if(deviceID==i){ 
        Serial.print(i);
        Serial.println(" is present");
        lookUpPresence[i] = true; 
      } 
      else {
        Serial.print(", ");
        Serial.print(i);
        Serial.println(" is not present"); 
        lookUpPresence[i] = false;
      } 
      //delay(1000);
      digitalWrite(ledRed, !digitalRead(ledRed)); 
  }//end for
}//end whoIsAround



void requestFromDevice(int device, int bytes) { 
    unsigned char inputArray[bytes] ;
    setToZeros(); 
    Wire.requestFrom(device, bytes);   
    while(Wire.available())    // slave may send less than requested
    { 
      unsigned char  c= Wire.read(); 
      inputArray[x] = c; 
      x++;   
    }
    x = 0; 
  
    Serial.print("S");
    Serial.print(",");
    for(int i=0; i<bytes; i = i+2) { 
      Serial.print((inputArray[i+1]<<8) + inputArray[i]);
      if (i==bytes-2) { 
      //DO nothing
        }
      else
        Serial.print(",");
    }
    Serial.println(" ");
    deviceID = (inputArray[1]<<8) + inputArray[0];
    delay(5);
    
    
}

void requestFromDeviceDuringStart(int device, int bytes) { 
    unsigned char inputArray[bytes] ;
    setToZeros(); 
    Wire.requestFrom(device, bytes);   
    while(Wire.available())    // slave may send less than requested
    { 
      unsigned char  c= Wire.read(); 
      inputArray[x] = c; 
      x++;   
    }
    x = 0; 
  
    Serial.print("A");
    Serial.print(",");
    for(int i=0; i<bytes; i = i+2) { 
      Serial.print((inputArray[i+1]<<8) + inputArray[i]);
      if (i==bytes-2) { 
      //DO nothing
        }
      else
        Serial.print(",");
    }
    Serial.println(" ");
    deviceID = (inputArray[1]<<8) + inputArray[0];
    delay(5);
}

void sendByteToSlave(int address, uint8_t command1, uint8_t command2, uint8_t command3) { 
  Wire.beginTransmission(address); 
  Wire.write(command1); 
  Wire.write(command2); 
  Wire.write(command3); 
  Wire.endTransmission(); 
  
} 
