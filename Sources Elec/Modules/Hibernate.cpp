#include "Hibernate.h"

void prepareSleepMode() {
	isAsleep = true;
}

void wakeUp() {
	digitalWrite(ledInternal, HIGH); // sets the LED on
}

void sleepMode() {
	delay(20);
	flash(13,4,100);

	// le mode de veille utilisé ne consomme de 100nA. Il ne peut être réveillé que par interruption.
	set_sleep_mode(SLEEP_MODE_PWR_DOWN);
	sleep_enable(); // non nécessaire: système de "sécurité" du microcontrolleur
	// lorsque le pin 3 (d'où le 1) passe à la masse (LOW), la fonction wakeUp est appelée
	attachInterrupt(1, wakeUp, LOW);
	sleep_mode(); // met réellement le système en veille
	// le programme continu à partir d'ici après le réveil	
	sleep_disable();
	isAsleep=false;
	detachInterrupt(0); // disables interrupt 0 on pin 2 so the wakeUpNow code will not be executed during normal running time. 
	Serial.println("Woke up!");
}
