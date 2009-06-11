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
#include "Modules.h"
#include "GenericFcts.h"
#include "Hibernate.h"
#include "XbeeCnx.h"

void setup() {
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
	if (isAsleep)
		sleepMode();
	// reinitialisation des valeurs de capteurs
	luxVal = 0;
	tempVal = 0;
	supVal1 = 0;
	supVal2 = 0;
	// lecture capteurs
	luxVal += analogRead(luxPin);
	luxVal /= 2;
	tempVal += analogRead(tempPin);
	tempVal /= 2;
	supVal1 += analogRead(supPin1);
	supVal1 /= 2;
	supVal2 += analogRead(supPin2);
	supVal2 /= 2;

	if (count >= SLEEPTIMER) {
		// pour le moment, on affiche les données en série.
		// Plus tard, on enverra sur le Xbee via la variable payLoad (uint8_t[]) 
		Serial.print("luxVal = "); Serial.println(luxVal);
		Serial.print("tempVal = "); Serial.println(tempVal);
		Serial.print("supVal1 = "); Serial.println(supVal1);
		Serial.print("supVal2 = "); Serial.println(supVal2);
		Serial.println("Going to sleep...");
		delay(100); // this delay is needed, the sleep function will provoke a Serial error otherwise!! 
		count = 0;
		prepareSleepMode(); // sleep function called here
	}
	count++;
	if(!isAsleep) delay(333); // on fait trois mesures
}
