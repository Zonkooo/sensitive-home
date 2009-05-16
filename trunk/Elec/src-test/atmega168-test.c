/*
 * Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on May 16, 2009
 */
#include "../lib/WProgram.h" 
#include "../lib/main.cxx"

/* Blink without Delay
 *
 * http://www.arduino.cc/en/Tutorial/BlinkWithoutDelay
 */
int ledPin = 13; // LED connected to digital pin 13

void setup() // run once, when the sketch starts
{
	pinMode(ledPin, OUTPUT); // sets the digital pin as output
}

void loop() // run over and over again
{
	digitalWrite(ledPin, HIGH); // sets the LED on
	delay(1000); // waits for a second
	digitalWrite(ledPin, LOW); // sets the LED off
	delay(1000); // waits for a second
}
