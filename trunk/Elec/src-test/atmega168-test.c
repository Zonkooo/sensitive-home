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
int value = LOW; // previous value of the LED
long previousMillis = 0; // will store last time LED was updated

void setup() // run once, when the sketch starts
{
	pinMode(ledPin, OUTPUT); // sets the digital pin as output
}

void loop() {
	int add=500, i=0;
	long interval = 5000; // interval at which to blink (milliseconds)
	// here is where you'd put code that needs to be running all the time.

	// check to see if it's time to blink the LED; that is, is the difference
	// between the current time and last time we blinked the LED bigger than
	// the interval at which we want to blink the LED.
	for (i = 0; i < 10; i++) {

		if (millis() - previousMillis > interval) {
			previousMillis = millis(); // remember the last time we blinked the LED

			// if the LED is off turn it on and vice-versa.
			if (value == LOW)
				value = HIGH;
			else
				value = LOW;

			digitalWrite(ledPin, value);
		}
		interval = interval - add;
		delay(add);
	}
}
