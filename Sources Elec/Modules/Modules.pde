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
#include "main.hpp"
#include "Sensors.hpp"

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
		prepareForSleep(); // sleep function called here
	}
	count++;
	if(!isAsleep) delay(333); // on fait trois mesures
}

void pwm(int pin, int start, int stop) {
	int val;
	if (start < stop) {
		for (val=start; val < stop; val+=5) {
			analogWrite(pin, val);
			delay(25);
		}
	} else {
		for (val=start; val > stop; val-=5) {
			analogWrite(pin, val);
			delay(25);
		}
	}
}
/**
 * prepareSleepMode place une variable à true.
 * A la prochaine boucle de loop() le système va s'endormir.
 * On utilise une variable pour éviter un double appel à la fonction de sommeil ce qui aurait des
 * conséquences inconnues... 
 */
void prepareSleepMode() {
	isAsleep = true;
}
/**
 * justWokeUp est appelé dès le réveil du système.
 * Il est interdit d'utiliser des timers ou des connexions séries dans cette fonction.
 * Après l'appel à cette fonction, le code retourne dans loop() juste après l'appel à la fonction de sommeil.
 */
void wakeUp() {
	digitalWrite(ledInternal, HIGH); // sets the LED on
}
/**
 * sommeil permet de mettre le système en veille.
 * Pour avoir un avertissement, on fait rapidement clignoter la led interne (pin 13) pendant 2 secondes.
 * Enfin, on éteint cette led quand le système est en veille. On la rallume quand le système se réveille.
 */
void sleepMode() {
	delay(20);
	int k;
	for (k=3; k!=0; k--) {
		digitalWrite(ledInternal, HIGH);
		delay(100);
		digitalWrite(ledInternal, LOW);
		delay(100);
	}
	digitalWrite(ledInternal, LOW); // on éteint cette LED

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
