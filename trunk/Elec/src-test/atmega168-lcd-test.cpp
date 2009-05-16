//example use of LCD4Bit_mod library

#include <LCD4Bit_mod.h> 
//create object to control an LCD.  
//number of lines in display=1
#include "WProgram.h"
void setup();
void loop();
int get_key(unsigned int input);
LCD4Bit_mod lcd = LCD4Bit_mod(2); 

//Key message
char msgs[5][17] = {"No right action ", 
                    "Incremented time", 
                    "Decremented time", 
                    "No left action  ", 
                    "Interval:       "};
char lcds[4][17] = {"Welcome by Chris", "Up/down <> +/-  ", "OK: show time  ", "Press any key  "};
int  adc_key_val[5] ={30, 150, 360, 535, 760 };
#define RIGHT 0
#define UP 1
#define DOWN 2
#define LEFT 3
#define SELECT 4
int NUM_KEYS = 5;
int adc_key_in;
int key=-1;
int oldkey=-1;
long interval = 1000, previousMillis=0;
char interChar[5];
short int value = LOW;

void setup() { 
  pinMode(13, OUTPUT);  //we'll use the debug LED to output a heartbeat

  lcd.init();
  //optionally, now set up our application-specific display settings, overriding whatever the lcd did in lcd.init()
  //lcd.commandWrite(0x0F);//cursor on, display on, blink on.  (nasty!)
  lcd.clear();
  int i;
  for(i=0; i < 3; i++){ // cycle all messages
    lcd.cursorTo(1,0); lcd.printIn(lcds[i]);
    lcd.cursorTo(2,0); lcd.printIn(lcds[i+1]);
    delay(1000);
  }
  lcd.clear();
  lcd.cursorTo(1,0); lcd.printIn(lcds[3]); // print the last message
  digitalWrite(13, value); // turn off the LED
}

void loop() {

  adc_key_in = analogRead(0);    // read the value from the sensor  
  key = get_key(adc_key_in); // convert into key press
	
  if (key != oldkey) { // if keypress is detected
    delay(50);		// wait for debounce time
    adc_key_in = analogRead(0);    // read the value from the sensor  
    key = get_key(adc_key_in);		        // convert into key press
    if (key != oldkey){			
      oldkey = key;
      if (key >=0){
        lcd.cursorTo(2, 0);  //line=2, x=0
        lcd.printIn(msgs[key]);
        if(key == SELECT){
          lcd.cursorTo(2,10);
          itoa(interval/1000,interChar,10);
          lcd.printIn(interChar);
          lcd.cursorTo(2,15); lcd.printIn("s");
        }else if(key == UP){
          interval += 250;
        }else if(key == DOWN){
          interval -= 250;
        }
      }
    }
  }
  
  // blink LED or not:
  if (millis() - previousMillis > interval) {
    previousMillis = millis();   // remember the last time we blinked the LED
    // if the LED is off turn it on and vice-versa.
    value = !value;
    digitalWrite(13, value);
  }
  
}

// Convert ADC value to key number
int get_key(unsigned int input){
  int k;
  for (k = 0; k < NUM_KEYS; k++){
    if (input < adc_key_val[k]){
      return k;
    }
  }
  if (k >= NUM_KEYS)  k = -1;     // No valid key pressed
  return k;
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}
