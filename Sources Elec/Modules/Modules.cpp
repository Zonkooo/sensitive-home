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
	pinMode(luxPin, INPUT);
	pinMode(tempPin, INPUT);
	pinMode(supPin1, INPUT);
	pinMode(supPin2, INPUT);
	// on allume la led interne
	digitalWrite(ledInternal, HIGH);
}

void loop() {
	if (isAsleep) {
		digitalWrite(ledInternal, LOW);
		sleepMode();
	}
#ifdef DEBUG
	Serial.print("Reading ... ");
	Serial.println(count);
#endif
	luxVal += analogRead(luxPin);
	luxVal /= 2;
	tempVal += analogRead(tempPin);
	tempVal /= 2;
	supVal1 += analogRead(supPin1);
	supVal1 /= 2;
	supVal2 += analogRead(supPin2);
	supVal2 /= 2;
	/*        payload[0] = luxVal >> 8 & 0xff;
	 payload[1] = luxVal & 0xff;*/
	if (count >= SLEEPTIMER) {
		uint8_t tmpData[64];
		sprintf_P((char*)tmpData, "%i:%i:%i:%i",luxVal,tempVal,supVal1,supVal2);
#ifdef DEBUG
		Serial.print("Sending: ");
		Serial.println((char*)tmpData);
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

		Serial.print("luxVal = ");
		Serial.println(luxVal);
		Serial.print("tempVal = ");
		Serial.println(tempVal);
		Serial.print("supVal1 = ");
		Serial.println(supVal1);
		Serial.print("supVal2 = ");
		Serial.println(supVal2);
		Serial.println("Going to sleep...");
#endif
		delay(100); /* this delay is needed, the sleep function will provoke a Serial error otherwise!! */
		count = 0;
		luxVal = 0;
		tempVal = 0;
		supVal1 = 0;
		supVal2=0;
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