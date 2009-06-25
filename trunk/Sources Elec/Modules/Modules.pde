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
#define SERIES_1
#define JDP
#include "GenericFcts.h"
#include "Hibernate.h"
#include "Modules.h"
#ifndef JDP
#include "XbeeCnx.h"
#endif

// définition des valeurs
int sensor1Val = 0;
int sensor2Val = 0;
int sensor3Val = 0;
int sensor4Val = 0;

// variable int permettant de compter le nombre de boucles dans loop()
int count = 0;

void setup() {
#ifdef JDP
	Serial.begin(9600); // permet de communiquer en série via Arduino (à virer pour le produit final)
#else
	initXB(9600);
	attachInterrupt(1, wakeUp, LOW); // voir commentaire dans sleepMode
#endif
	// on précise que les pin sont des pins de lecture:
	pinMode(sensor1Pin, INPUT);
	pinMode(sensor2Pin, INPUT);
	pinMode(sensor3Pin, INPUT);
	pinMode(sensor4Pin, INPUT);
	// on allume la led interne
	pinMode(ledInternal,OUTPUT);
	digitalWrite(ledInternal, HIGH);
#ifndef JDP
	initMPXBCnx();
#endif
	flash(ledInternal, 5, 50);
}

void loop() {
	if (isAsleep()) {
		digitalWrite(ledInternal, LOW);
		delay(50); // le temps d'éteindre la LED (on sait jamais...)
#ifndef JDP
		sleepMode();
#endif
	}
	sensor1Val += analogRead(sensor1Pin);
	sensor1Val /= 2;
	sensor2Val += analogRead(sensor2Pin);
	sensor2Val /= 2;
	sensor3Val += analogRead(sensor3Pin);
	sensor3Val /= 2;
	sensor4Val += analogRead(sensor4Pin);
	sensor4Val /= 2;

	if (count > SLEEPTIMER) {
#ifndef JDP
		uint8_t tmpData[64];
		sprintf_P((char*)tmpData, "%i:%i:%i:%i",sensor1Val,sensor2Val,sensor3Val,sensor4Val);
		setTxData(tmpData);
		sendXB();
		delay(100); /* this delay is needed, the sleep function will provoke a Serial error otherwise!! */
#else
		char tmpData[64];
		sprintf_P(tmpData, "%i:%i:%i:%i",sensor1Val,sensor2Val,sensor3Val,sensor4Val);
		Serial.println(tmpData);
#endif
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
