#include <avr/sleep.h>
int ledPin = 13; // LED connected to digital pin 13
//int wakePin = 2; // pin used for waking up
#define BEDTIME 10 // durée à attendre avant de dormir
short int isAsleep=false;
short int LedOn = HIGH;

void bedTime() {
	isAsleep = true;
}

void setup() {
	Serial.begin(9600); // permet de communiquer en Serial via Arduino IDE
	pinMode(ledPin, OUTPUT); // sets the digital pin as output
	attachInterrupt(1, bedTime, LOW);
	digitalWrite(ledPin, HIGH); // sets the LED on
}

void loop() {
	//	digitalWrite(ledPin, LedOn); // sets the LED on
	//	if(LedOn==HIGH) LedOn=LOW;
	//	else LedOn=HIGH;

	if (isAsleep)
		bedTime();
	// display information about the counter
	Serial.print("Awake for ");
	Serial.print(count);
	Serial.println("sec");
	count++;
	delay(1000); // waits for a second

	// compute the serial input
	if (Serial.available()) {
		int val = Serial.read();
		if (val == 'S') {
			Serial.println("Serial: Entering Sleep mode");
			delay(100); // this delay is needed, the sleep 
			//function will provoke a Serial error otherwise!!
			count = 0;
			bedTime(); // sleep function called here
		}
		if (val == 'A') {
			Serial.println("Woke up!"); // classic dummy message
		}
	}

	// check if it should go to sleep because of time
	if (count >= BEDTIME) {
		Serial.println("Timer: Entering Sleep mode");
		delay(100); // this delay is needed, the sleep 
		//function will provoke a Serial error otherwise!!
		count = 0;
		bedTime(); // sleep function called here
	}

}

//

/* Sleep Demo Serial
 * -----------------
 * Example code to demonstrate the sleep functions in an Arduino.
 *
 * use a resistor between RX and pin2. By default RX is pulled up to 5V
 * therefore, we can use a sequence of Serial data forcing RX to 0, what
 * will make pin2 go LOW activating INT0 external interrupt, bringing
 * the MCU back to life
 *
 * there is also a time counter that will put the MCU to sleep after 10 secs
 *
 * NOTE: when coming back from POWER-DOWN mode, it takes a bit
 *       until the system is functional at 100%!! (typically <1sec)
 *
 */

int sleepStatus = 0; // variable to store a request for sleep
int count = 0; // counter

void wakeUpNow() {
	// execute code here after wake-up before returning to the loop() function
	// timers and code using timers (serial.print and more...) will not work here.
	// we don't really need to execute any special functions here, since we
	// just want the thing to wake up
}

void sleepNow() {
	// on fait rapidement clignoter la LED puis on l'éteint et on met le microp en veille
	delay(20);
	int k;
	for (k=20; k!=0; k--) {
		digitalWrite(ledPin, HIGH); // sets the LED on
		delay(200);
		digitalWrite(ledPin, LOW); // sets the LED off
		delay(200);
	}
	isAsleep=false;
	//digitalWrite(ledPin, LOW); // on éteint la LED

	set_sleep_mode(SLEEP_MODE_PWR_DOWN); // sleep mode is set here
	sleep_enable(); // enables the sleep bit in the mcucr register
	// so sleep is possible. just a safety pin 
	attachInterrupt(0, wakeUpNow, LOW); // use interrupt 0 (pin 2) and run function
	// wakeUpNow when pin 2 gets LOW 
	sleep_mode(); // here the device is actually put to sleep!!
	// THE PROGRAM CONTINUES FROM HERE AFTER WAKING UP

	sleep_disable(); // first thing after waking from sleep
	detachInterrupt(0); // disables interrupt 0 on pin 2 so the 
	// wakeUpNow code will not be executed 
	// during normal running time.

}
