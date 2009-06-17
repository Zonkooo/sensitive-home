#include "WProgram.h"
/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 10, 2009
 * 
 * Cette partie est pour le module de capteurs.
 */

/* Définitions:
 * ID: identifiant, c'est l'adresse MAC du XBee
 * 
 *  Principe de fonctionnement:
 * 1. Initialisation
 * 		- envoie de son ID en broadcast (i.e. envoie à (0x0, 0xfff) ).
 * 2. Fonctionnement normal
 * 		- est en veille
 * 		- attend une interruption du XBee pour le réveiller
 * 		- transmet au XBee la moyenne sur une seconde des 4 capteurs (même si non branchés)
 * 		- Attend un ACK du XBee-multiprise indiquant la bonne réception des informations
 * 		- se remet en veille
 * 3. Fonctionnement en mise à jour:
 * 		- idem qu'en fonctionnement normal
 * 		- sauf que la multiprise ne revoit pas un ACK mais un message de début de MàJ
 * 		- stocke le nouveau firmware dans la flash 
 * 		... TODO fin de conception MàJ 
 * 
 */
#include "GenericFcts.h"
#include "Hibernate.h"
#include "Modules.h"
#include "XbeeCnx.h"

void setup() {
	xb.begin(9600);
	Serial.begin(9600); // permet de communiquer en série via Arduino (à virer pour le produit final)
	attachInterrupt(1, wakeUp, LOW); // voir commentaire dans sleepMode
	// on précise que les pin sont des pins de lecture:
	pinMode(sensor1Pin, INPUT);
	pinMode(sensor2Pin, INPUT);
	pinMode(sensor3Pin, INPUT);
	pinMode(sensor4Pin, INPUT);
	// on allume la led interne
	digitalWrite(ledInternal, HIGH);
}

void loop() {
	if (isAsleep()) {
#ifdef DEBUG
		Serial.print("Dodo: "); Serial.println((isAsleep())?"true":"false");
		delay(100); // pour le Serial
#endif
		digitalWrite(ledInternal, LOW);
		delay(50); // le temps d'éteindre la LED (on sait jamais...)
		sleepMode();
	}
#ifdef DEBUG
	Serial.print("Reading ... ");
	Serial.println(count);
#endif
	sensor1Val += analogRead(sensor1Pin);
	sensor1Val /= 2;
	sensor2Val += analogRead(sensor2Pin);
	sensor2Val /= 2;
	sensor3Val += analogRead(sensor3Pin);
	sensor3Val /= 2;
	sensor4Val += analogRead(sensor4Pin);
	sensor4Val /= 2;

	if (count > SLEEPTIMER) {
		uint8_t tmpData[64];
		//sprintf_P((char*)tmpData, "%i:%i:%i:%i",sensor1Val,sensor2Val,sensor3Val,sensor4Val);
#ifdef DEBUG
		Serial.print("Sending");
		//Serial.println((char*)tmpData);
#endif
		setTxData(tmpData);
		int rtn = sendXB();
#ifdef DEBUG
		if (rtn == 0) {
			Serial.println("Erreur 0");
		} else if (rtn == 1) {
			Serial.println("Succes 1");
		} else {
			Serial.print("Erreur ");
			Serial.println(rtn);
		}
		/* pour le moment, on affiche les données en série.
		 Plus tard, on enverra sur le Xbee via la variable payLoad (uint8_t[]) 
		 */

		Serial.print("sensor1Val = ");
		Serial.println(sensor1Val);
		Serial.print("sensor2Val = ");
		Serial.println(sensor2Val);
		Serial.print("sensor3Val = ");
		Serial.println(sensor3Val);
		Serial.print("sensor4Val = ");
		Serial.println(sensor4Val);
		Serial.println("Going to sleep...");
#endif
		delay(100); /* this delay is needed, the sleep function will provoke a Serial error otherwise!! */
		count = 0;
		sensor1Val = 0;
		sensor2Val = 0;
		sensor3Val = 0;
		sensor4Val=0;
		prepareSleepMode(); /* sleep function called here*/
	}
	count++;
	delay(100); /* one third of a second!*/

}
int main(void) {
	init();
	setup();
	for (;;) {
		loop();
	}
	return 0;
}