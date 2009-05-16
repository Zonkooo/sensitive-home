//example use of LCD4Bit_mod library

//example use of LCD4Bit_mod library

#include <LCD4Bit_mod.h> 
//create object to control an LCD.  
//number of lines in display=1
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
// if the correct password is entered the interval is reset to 1000 ms
const int pwdlen=5;
int password[pwdlen] = {UP, DOWN, LEFT, RIGHT, DOWN};
int pwdCursor=0; // cursor in the password
short int pwdGood=true; // to check if correct pwd
int NUM_KEYS = 5;
int adc_key_in;
int key=-1;
int oldkey=-1;
unsigned long interval = 1000, previousMillis=0;
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
    delay(3000);		// wait for debounce time (keydown keyup)
    adc_key_in = analogRead(0);    // read the value from the sensor  
    key = get_key(adc_key_in);		        // convert into key press
    if (key != oldkey){
      oldkey = key;
      if (key >=0){
        lcd.cursorTo(2, 0);  //line=2, x=0
        lcd.printIn(msgs[key]);
        if(key == SELECT){
          lcd.cursorTo(2,10);
          itoa(interval,interChar,10);
          lcd.printIn(interChar);
          lcd.cursorTo(2,14); lcd.printIn("ms");
        }else if(key == UP){
          interval += 250;
        }else if(key == DOWN){
          interval>250? interval-= 250:interval=0;
        }
      }
      // end interval check - start password check
      if(pwdCursor == 0) pwdGood=true;
      pwdGood = pwdGood && (key == password[pwdCursor++]);
      if(pwdGood && pwdCursor == (pwdlen+1)){
        interval = 1000;
        lcd.clear();
        lcd.cursorTo(1,0); lcd.printIn("--Access Granted--");
        lcd.cursorTo(2,0); lcd.printIn("-Interval reseted-");
        delay(1500);
        pwdCursor = 0;
      }else{
        if(pwdGood){
          lcd.cursorTo(1,0); lcd.printIn("--Correct key--");
        }else{
          lcd.cursorTo(1,0); lcd.printIn("---Wrong key---");
          //delete the next line when completed
          pwdCursor=0;
        }
        delay(500);
      }
      if(pwdCursor > pwdlen) pwdCursor = 0;
      lcd.cursorTo(1,0); lcd.printIn(lcds[3]); // print the last message
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
