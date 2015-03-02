
//This is for tape v3. Some pins are different in the new version
#include "I2Cdev.h"
#include "MPU6050_6Axis_MotionApps20.h"
#include <SPI.h>
#include <Wire.h>
#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>


MPU6050 mpu;
SoftwareSerial p2pSerial(9,10); //RX, TX
//#define OUTPUT_READABLE_QUATERNION
//#define OUTPUT_READABLE_EULER
//#define OUTPUT_READABLE_YAWPITCHROLL
//#define OUTPUT_READABLE_REALACCEL
//#define OUTPUT_READABLE_WORLDACCEL

#define OUTPUT_TEAPOT
#define PIN            7
#define NUMPIXELS      1

bool blinkState = false;

// MPU control/status vars
bool dmpReady = false;  // set true if DMP init was successful
uint8_t mpuIntStatus;   // holds actual interrupt status byte from MPU
uint8_t devStatus;      // return status after each device operation (0 = success, !0 = error)
uint16_t packetSize;    // expected DMP packet size (default is 42 bytes)
uint16_t fifoCount;     // count of all bytes currently in FIFO
uint8_t fifoBuffer[64]; // FIFO storage buffer

// orientation/motion vars
Quaternion q;           // [w, x, y, z]         quaternion container
VectorInt16 aa;         // [x, y, z]            accel sensor measurements
VectorInt16 aaReal;     // [x, y, z]            gravity-free accel sensor measurements
VectorInt16 aaWorld;    // [x, y, z]            world-frame accel sensor measurements
VectorFloat gravity;    // [x, y, z]            gravity vector
float euler[3];         // [psi, theta, phi]    Euler angle container
float ypr[3];           // [yaw, pitch, roll]   yaw/pitch/roll container and gravity vector

// packet structure for InvenSense teapot demo
uint8_t teapotPacket[14] = { '$', 0x02, 0,0, 0,0, 0,0, 0,0, 0x00, 0x00, '\r', '\n' };


int numberOfSensors = 3;
int deviceType = 1;
int deviceID = 0; 

int ledBlue  =6; 
int ledRed = 5;
//int resistanceSenseEnable = 5;  
int cut1pin = A1; 
int cut2pin = 8; 
//int cut3pin = 3; 
//int cut4pin = 4; 
boolean startUp = true; 
int incomingByte = 0;

byte val1; 
byte val2;  

int lightAnalogRead = 2000; 
int thermistorAnalogRead = 3000; 
int positionAnalogRead = 4000;
String valueString = "a"; 
byte valAll[1]; 

Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUMPIXELS, PIN, NEO_GRB + NEO_KHZ800);

// ================================================================
// ===               INTERRUPT DETECTION ROUTINE                ===
// ================================================================

volatile bool mpuInterrupt = false;     // indicates whether MPU interrupt pin has gone high
void dmpDataReady() {
    mpuInterrupt = true;
}



// ================================================================
// ===                      INITIAL SETUP                       ===
// ================================================================

void setup() {
    
  pinMode(ledBlue, OUTPUT);   
  pinMode(ledRed, OUTPUT); 
  
  digitalWrite(ledBlue, HIGH); 
  
  SPI.begin();
  Serial.begin(9600);
  p2pSerial.begin(9600);
  
  
  
  
  I2Cdev::i2c_init();
  mpu.initialize();
  devStatus = mpu.dmpInitialize();

    // supply your own gyro offsets here, scaled for min sensitivity
    mpu.setXGyroOffset(220);
    mpu.setYGyroOffset(76);
    mpu.setZGyroOffset(-85);
    mpu.setZAccelOffset(1788); // 1688 factory default for my test chip
    mpu.setDMPEnabled(true);
        // enable Arduino interrupt detection
    attachInterrupt(0, dmpDataReady, RISING);
    mpuIntStatus = mpu.getIntStatus();
    dmpReady = true;
    packetSize = mpu.dmpGetFIFOPacketSize();
    
     digitalWrite(ledRed, LOW); 
     
    pixels.begin(); // This initializes the NeoPixel library.
    pixels.setPixelColor(0, pixels.Color(10,100,10)); // Moderately bright green color.
    pixels.show(); // This sends the updated pixel color to the hardware.
     
}



// ================================================================
// ===                    MAIN PROGRAM LOOP                     ===
// ================================================================

void loop() {

    // wait for MPU interrupt or extra packet(s) available
    while (!mpuInterrupt && fifoCount < packetSize) {
      
     if (p2pSerial.available()>0) { 
      incomingByte = p2pSerial.read();
      deviceID = incomingByte;
       digitalWrite(ledBlue, LOW);       
      p2pSerial.write(incomingByte+1);
        Wire.begin(deviceID); // Start I2C Bus as a Slave (Device Number 2)
        Wire.onRequest(requestEvent); // register event to send data to master (respond to requests)
        Wire.onReceive(receiveEvent); //register even to receice data from master
    }
    }

    // reset interrupt flag and get INT_STATUS byte
    mpuInterrupt = false;
    mpuIntStatus = mpu.getIntStatus();

    // get current FIFO count
    fifoCount = mpu.getFIFOCount();

    // check for overflow (this should never happen unless our code is too inefficient)
    if ((mpuIntStatus & 0x10) || fifoCount == 1024) {
        // reset so we can continue cleanly
        mpu.resetFIFO();
        Serial.println(F("FIFO overflow!"));

    // otherwise, check for DMP data ready interrupt (this should happen frequently)
    } else if (mpuIntStatus & 0x02) {
        // wait for correct available data length, should be a VERY short wait
        while (fifoCount < packetSize) fifoCount = mpu.getFIFOCount();

        // read a packet from FIFO
        mpu.getFIFOBytes(fifoBuffer, packetSize);
        
        // track FIFO count here in case there is > 1 packet available
        // (this lets us immediately read more without waiting for an interrupt)
        fifoCount -= packetSize;

       
            // display quaternion values in InvenSense Teapot demo format:
        teapotPacket[2] = fifoBuffer[0];
        teapotPacket[3] = fifoBuffer[1];
        teapotPacket[4] = fifoBuffer[4];
        teapotPacket[5] = fifoBuffer[5];
        teapotPacket[6] = fifoBuffer[8];
        teapotPacket[7] = fifoBuffer[9];
        teapotPacket[8] = fifoBuffer[12];
        teapotPacket[9] = fifoBuffer[13];
        teapotPacket[11]++; // packetCount, loops at 0xFF on purpose

    }
    
}



void requestEvent()
{
    if (startUp) { 
//   delay(10);
        byte a = deviceID & 0xFF;
        byte b = (deviceID >>8 ) & 0xFF;
        
        byte c = numberOfSensors & 0xFF; ; 
        byte d = (numberOfSensors >>8 ) & 0xFF;
        
        byte e = deviceType & 0xFF; ; 
        byte f = (deviceType >>8 ) & 0xFF;
        
        int tempread = digitalRead(cut1pin); 
        byte g = tempread & 0xFF; ; 
        byte h = (tempread >>8 ) & 0xFF;     
        
        tempread = digitalRead(cut2pin); 
        byte i = tempread & 0xFF; ; 
        byte j = (tempread >>8 ) & 0xFF;   
        
        tempread = digitalRead(cut2pin); 
        byte k = tempread & 0xFF; ; 
        byte l = (tempread >>8 ) & 0xFF; 
        
        tempread = digitalRead(cut2pin); 
        byte m = tempread & 0xFF; ; 
        byte n = (tempread >>8 ) & 0xFF; 
        
        byte All [] = {a,b,c,d,e,f,g,h,i,j,k,l,m,n};  
           
        Wire.write(All, 14); // respond with message 
        startUp = false;
    } 
    
    else { 
        digitalWrite(ledRed, HIGH);
        lightAnalogRead = analogRead(A6); // Read light value 
        thermistorAnalogRead = analogRead(A7); //Read thermistor valie
      //  digitalWrite(resistanceSenseEnable, LOW);  
        delayMicroseconds(500);  
        positionAnalogRead = analogRead(A1); //Read position
        delayMicroseconds(500);
      //  digitalWrite(resistanceSenseEnable, HIGH);
        
        byte a = deviceID & 0xFF;
        byte b = (deviceID >>8 ) & 0xFF;
        
        byte c = lightAnalogRead & 0xFF; ; 
        byte d = (lightAnalogRead >>8 ) & 0xFF;
     
        byte e = thermistorAnalogRead & 0xFF; ; 
        byte f = (thermistorAnalogRead >>8 ) & 0xFF;
        
        byte g = positionAnalogRead & 0xFF; ; 
        byte h = (positionAnalogRead >>8 ) & 0xFF;     
       
        byte All [] = {a,b,c,d,e,f,g,h, 
                      fifoBuffer[1], fifoBuffer[0], 
                      fifoBuffer[5], fifoBuffer[4], 
                      fifoBuffer[9], fifoBuffer[8],
                      fifoBuffer[13], fifoBuffer[12]};  
                    
        Wire.write(All, 16); // respond with message of 6 bytes
        digitalWrite(ledRed, LOW);    
    }//end else        

}

void receiveEvent(int howMany)
{

  if (Wire.available()) { 
    char x  = Wire.read(); 
    digitalWrite(ledBlue, !digitalRead(ledBlue));     
  }
  
}

