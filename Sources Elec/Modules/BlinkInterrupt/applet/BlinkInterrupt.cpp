#include "WProgram.h"
void onInterrupt();
void prepareForSleep();
void setup();
void loop();
int ledPin = 13;                // LED connected to digital pin 13
short int isAsleep=false;

void onInterrupt(){
  isAsleep=true;
}

void prepareForSleep()
{
  delay(20);
  int k;
  for(k=20;k!=0;k--){
      digitalWrite(ledPin, HIGH);   // sets the LED on
      delay(200);                  // waits for a second
      digitalWrite(ledPin, LOW);    // sets the LED off
      delay(200);
  }
  isAsleep=false;
}

void setup()                    // run once, when the sketch starts
{
  pinMode(ledPin, OUTPUT);   // sets the digital pin as output
   // attachInterrupt(0, wakeUpNow, LOW);
  attachInterrupt(1, onInterrupt, FALLING);
}

void loop()                     // run over and over again
{
  digitalWrite(ledPin, HIGH);   // sets the LED on
  delay(1000);                  // waits for a second
  digitalWrite(ledPin, LOW);    // sets the LED off
  delay(1000); 
  if(isAsleep) prepareForSleep();
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

