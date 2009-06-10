#include <avr/sleep.h>
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

int ledPin = 13; // LED connected to digital pin 13
int ledAwake = 9; // LED qui sera mise à 50% quand le système dors.
//int wakePin = 2; // pin used for waking up
#define SLEEPTIMER 3 // durée à attendre avant de dormir
short int isAsleep=false;
short int LedOn = HIGH;
int count = 0; // compteur
int luxVal = 0; // valeur de la photorésistance
int luxPin = 2; // pin dans la partie ANALOG du microp

void pwmLed(int pin, int start, int stop) {
	int val;
	if (start < stop) {
		for (val=start; val < stop; val+=5) {
			analogWrite(pin, val); delay(25);
		}
	} else {
		for (val=start; val > stop; val-=5) {
			analogWrite(pin, val); delay(25);
		}
	}
}

void prepareForSleep() {
	isAsleep = true;
}

void wakeUpNow() {
	// execute code here after wake-up before returning to the loop() function
	// timers and code using timers (serial.print and more...) will not work here.
	// we don't really need to execute any special functions here, since we
	// just want the thing to wake up
	digitalWrite(ledPin, HIGH); // sets the LED on

}

void sleepNow() {
	// on fait rapidement clignoter la LED puis on l'éteint et on met le microp en veille
	delay(20);
	int k;
	for (k=3; k!=0; k--) {
		digitalWrite(ledPin, HIGH); // sets the LED on
		delay(100);
		digitalWrite(ledPin, LOW); // sets the LED off
		delay(100);
	}
	digitalWrite(ledPin, LOW); // on éteint cette LED
	pwmLed(ledAwake,255,32); // on baisse la LED d'allumage


	set_sleep_mode(SLEEP_MODE_PWR_DOWN); // le microp ne se réveille pas par du série
	sleep_enable(); // enables the sleep bit in the mcucr register so sleep is possible. just a safety pin 
	attachInterrupt(1, wakeUpNow, LOW); // use interrupt 1 (pin 3) and run function wakeUpNow when pin 3 gets LOW 
	sleep_mode(); // here the device is actually put to sleep!!
	// THE PROGRAM CONTINUES FROM HERE AFTER WAKING UP

	sleep_disable(); // first thing after waking from sleep
	isAsleep=false;
	detachInterrupt(0); // disables interrupt 0 on pin 2 so the wakeUpNow code will not be executed during normal running time. 
	Serial.println("Woke up!");
	pwmLed(ledAwake, 32, 255); // on réallume la LED d'allumage
}

void setup() {
	Serial.begin(9600); // permet de communiquer en Serial via Arduino IDE
	pinMode(ledPin, OUTPUT); // sets the digital pin as output
	attachInterrupt(1, wakeUpNow, LOW);
	digitalWrite(ledPin, HIGH); // sets the LED on
	pwmLed(ledAwake,0,255);
	pinMode(luxPin, INPUT);
}

void loop() {
	if (isAsleep) {
		sleepNow();
	}
	luxVal += analogRead(luxPin);
	luxVal /= 2; // on fait une moyenne sur deux valeurs

	// compute the serial input
//	if (Serial.available()) {
//		int val = Serial.read();
//		if (val == 'S') {
//			Serial.println("Serial: Entering Sleep mode");
//			delay(100); // this delay is needed, the sleep function will provoke a Serial error otherwise!!
//			prepareForSleep(); // sleep function called here
//		}
//	}

//	 check if it should go to sleep because of time
		if (count >= SLEEPTIMER) {
			Serial.print("Lux = ");
			Serial.println(luxVal); // on envoie la valeur sur le port série
			Serial.println("Going to sleep...");
			delay(100); // this delay is needed, the sleep function will provoke a Serial error otherwise!! 
			count = 0;
			prepareForSleep(); // sleep function called here
		}
		count++;
		delay(333); // on fait trois mesures
}
