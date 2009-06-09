#include "WProgram.h"
void onInterrupt();
void interrupted();
void setup();
void loop();
int ledPin = 13;                // LED connected to digital pin 13
short int isInterrupted=false;

void onInterrupt(){
  isInterrupted=true;
}

void interrupted()
{
  delay(20);
  int k;
  for(k=20;k!=0;k--){
      digitalWrite(ledPin, HIGH);   // sets the LED on
      delay(200);                  // waits for a second
      digitalWrite(ledPin, LOW);    // sets the LED off
      delay(200);
  }
  isInterrupted=false;
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
  if(isInterrupted) interrupted();
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

