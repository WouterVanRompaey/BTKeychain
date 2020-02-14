#include <Wire.h>  // Include Wire if you're using I2C
#include <SPI.h>  // Include SPI if you're using SPI
#include <SoftwareSerial.h>

float RT, VR, ln, TX, T0, VRT;

#define RT0 50000  
#define B 3000     
#define VCC 5       
#define R 51000

#define PIN_RESET 3  // Connect RST to pin 9 (req. for SPI and I2C)
#define PIN_DC    4  // Connect DC to pin 8 (required for SPI)
#define PIN_CS    10 // Connect CS to pin 10 (required for SPI)
#define DC_JUMPER 0

SoftwareSerial mySerial(8,9);

String text = "";
String notif[100];
int buzzer = 16;
int buttonPin = 14;
char c;
int buttonState =0;

void setup()
{  
  pinMode(10, OUTPUT);
  pinMode(buzzer,OUTPUT);
  pinMode(buttonPin, INPUT);
  digitalWrite(buzzer,LOW);  
  digitalWrite(10,LOW);
  c =-1;
  
  //BLUETOOTH
  bool on = false;
  
  //Serial.begin(9600);
  while(1){
  buttonState = digitalRead(buttonPin);
    if(buttonState==HIGH){     
    break;
    }
  }
   
    mySerial.begin(9600);

}

void loop()
{
  
    if(mySerial.available()) {
      c = mySerial.read();   
      text = String(c);
      Serial.write(c);
      digitalWrite(buzzer, LOW);
    }else{    
      if(text=="N"){    
        digitalWrite(buzzer, LOW);
      }else{
        digitalWrite(buzzer, HIGH);    
      }
    }
  
  delay(50);
}
