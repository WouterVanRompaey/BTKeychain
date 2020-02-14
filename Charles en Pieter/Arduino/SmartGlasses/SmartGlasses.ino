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

void setup()
{
  T0 = 20 + 273.15;  
  pinMode(14, OUTPUT);
  pinMode(buzzer,OUTPUT);
  digitalWrite(14,LOW);
  
  
  //BLUETOOTH
  Serial.begin(9600);
  mySerial.begin(9600);

}

void loop()
{
  if(mySerial.available()) {
    char c = mySerial.read();                   
    digitalWrite(buzzer, LOW);
  }else {
    digitalWrite(buzzer, HIGH);
  }
  delay(5000);
}
/*


void getData() {
  text = "";
  boolean nieuwenotif = false;
  while(!nieuwenotif) {
          VRT = analogRead(A0);           
          VRT = (5.00 / 1023.00) * VRT;     
          VR = VCC - VRT;
          RT = VRT / (VR / R);               
        
          ln = log(RT / RT0);
          TX = (1 / ((ln / B) + (1 / T0))); 
          TX = TX - 273.15;

          if(TX > 24) {
            notif[0] = "Batterij";
            notif[1] = "TE";
            notif[2] = "TE WARM";
            notif[3] = "TE";
            notif[4] = "TE WARM";
            notif[5] = "TE";
            notif[6] = "TE WARM";
            nieuwenotif = true;
          }
            
          while(mySerial.available()) {
                  char c = mySerial.read();
                  text.concat(c);
                  text.replace("OK+CONN", "");
                  text.replace("OK+LOST", "");
                  if(text.indexOf("__") > 0) {
                        text.replace("_","");
                        for(int i = 0; i < 50; i++) {
                          if(notif[i] == "") {
                            notif[i] = text;
                            break;
                          }
                        }
                        text = "";
                  }
                  if(text == "~~") {
                    text = "";
                    nieuwenotif = true;
                  }     
        }
  }
}

void showData() {
  oled.clear(PAGE);
  oled.clear(ALL);

  alertMelding();
  
  delay(250);

  showMelding();

  text = "";
  for(int k = 0; k < 50; k++) {
    notif[k] = "";
  }

  delay(250);
}




void alertMelding() {
  oled.rect(0,0,LCDWIDTH,LCDHEIGHT);
  oled.setCursor(LCDWIDTH/5,LCDHEIGHT/5);
  oled.print("Nieuwe");
  oled.setCursor((LCDWIDTH/5),(LCDHEIGHT/5)+10);
  oled.print("melding");
  oled.display();

  delay(200);
  
  oled.clear(PAGE);
  oled.clear(ALL);

  delay(200);

  oled.rect(0,0,LCDWIDTH,LCDHEIGHT);
  oled.setCursor(LCDWIDTH/5,LCDHEIGHT/5);
  oled.print("Nieuwe");
  oled.setCursor((LCDWIDTH/5),(LCDHEIGHT/5)+10);
  oled.print("melding");
  oled.display();


  delay(200);
  
  oled.clear(PAGE);
  oled.clear(ALL);

  delay(200);

  oled.rect(0,0,LCDWIDTH,LCDHEIGHT);
  oled.setCursor(LCDWIDTH/5,LCDHEIGHT/5);
  oled.print("Nieuwe");
  oled.setCursor((LCDWIDTH/5),(LCDHEIGHT/5)+10);
  oled.print("melding");
  oled.display();

  delay(200);

  oled.clear(PAGE);
  oled.clear(ALL);
}

void showMelding() {
  for(int j = 1; j < 50; j++) {
    if(notif[j] != "") {
      oled.clear(PAGE);
      oled.clear(ALL);

      oled.rect(0,0,LCDWIDTH,LCDHEIGHT/2);
  
      setTitle(notif[0]);
      setText(notif[j]);
  
      delay(750);
    }
  }
}




void setTitle(String t) {
  oled.setCursor(5,7);
  oled.print(t);
  oled.display();
}

void setText(String t) {
  oled.setCursor(0,(LCDHEIGHT/2)+4);
  oled.print(t);
  oled.display();
}
*/
