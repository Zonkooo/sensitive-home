#include <XBee.h>

#ifndef MODULES_HPP_
#define MODULES_HPP_
#define SLEEPTIMER 3

// définition des pins
const int ledInternal = 13;
const int luxPin = 1;
const int tempPin = 2;
const int supPin1 = 3;
const int supPin2 = 4;
// définition des valeurs
int luxVal = 0;
int tempVal = 0;
int supVal1 = 0;
int supVal2 = 0;

// variable bool pour endormir
bool isAsleep = false;
// variable int permettant de compter le nombre de boucles dans loop()
int count = 0;
#include <avr/sleep.h>
#include <inttypes.h>
// prototypes
void pwm();
void prepareSleepMode();
void wakeUp();
void sleepMode();
/*void setup();
void loop();
void init();

int main(void) {
	init();
	setup();
	for (;;)
		loop();
	return 0;
}
*/
#endif /*MODULES_HPP_*/

/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 11, 2009
 * 
 * Ce fichier est le header du fichier XbeeCnx.cpp.
 * Ce dernier contient les fonctions permettant d'exploiter la librairie Xbee
 * pour la communication Module de Capteurs <-> Mutliprise  
 */

/* variables des LED de communication
 * La LED de statut est interne: elle n'a pas besoin d'être visible
 * La LED d'erreur est externe (et rouge): en cas d'erreur elle clignote pendant 5 secondes 
 */
const int statusLed = 13; // led interne au microcontrolleur
const int errorLed = 0; // led externe.

// définition des variables de communication
XBee xbee = XBee();
XBeeAddress64 xbAddr = XBeeAddress64(0x0013a200, 0x4008ebef);
uint8_t rxOption = 0;
uint8_t rxData[] = {0,0};
uint8_t txOption = 0;
uint8_t txData[] = {0,0};

// variables de réponse 
XBeeResponse response = XBeeResponse(); 
ZBRxResponse rx = ZBRxResponse();
ModemStatusResponse msr = ModemStatusResponse();
// variables de transfert (i.e. envoie)
ZBTxRequest tx = ZBTxRequest(xbAddr, txData, sizeof(txData));
ZBTxStatusResponse txStatus = ZBTxStatusResponse();
/* readXB permet de lire des données du XBee
 * Les données reçues sont écrites dans la variable rxData et accessible via son accesseur getRxData();
 */
void readXB();
/* sendXB permet d'envoyer le payload à l'adresse XBee spécifiée. Cette addresse doit être modifiée via setXBAddr()
 * Il n'est pas nécessaire d'appeler cette fonction après l'appel à sendBroadcast(). 
 * Attention à bien écrire le payload à l'avance via la fonction setTxData().
 */
void sendXB();
/* sendBroadcast permet d'envoyer un message en broadcast (à tous les membres du réseau)
 * Le payload doit être écrit par avance. Après l'appel à cette fonction il n'est pas nécessaire de remettre la
 * bonne adresse XBee destinataire.
 */
void sendBroadcast();
/* setXBAddr permet de changer l'adresse du destinataire XBee.
 */
void setXBAddr(uint32_t msb, uint32_t lsb);
/* setTxData permet de changer le payload, c'est-à-dire le message à être envoyé via XBee.
 */
void setTxData(uint8_t newdata[2]);
/* getRxdata permet de lire les données reçues via XBee.
 */
uint8_t* getRxdata();
/* initMPXBCnx est la fonction permettant de connecter le module de capteurs à la multiprise.
 */
void initMPXBCnx();
extern "C" void __cxa_pure_virtual()
{
  while (1);
}

void readXBee() {

	xbee.readPacket();

	if (xbee.getResponse().isAvailable()) {
		if (xbee.getResponse().getApiId() == ZB_RX_RESPONSE) {
			xbee.getResponse().getZBRxResponse(rx); // permet de renseigner la classe Rx
			if (rx.getOption() == ZB_PACKET_ACKNOWLEDGED) { // on a reçu un ACK
				flash(statusLed, 1, 10);
			} else { // on a bien reçu une réponse mais l'envoyeur n'a reçu de ACK
				flash(errorLed, 2, 20);
			}
			rxData[0] = rx.getData(0); rxData[1] = rx.getData(1);
		} else if (xbee.getResponse().getApiId() == MODEM_STATUS_RESPONSE) {
			xbee.getResponse().getModemStatusResponse(msr);
			// the local XBee sends this response on certain events, like association/dissociation

			if (msr.getStatus() == ASSOCIATED) { // on est associé
				flash(statusLed, 10, 10);
			} else if (msr.getStatus() == DISASSOCIATED) { // on s'est fait désassocié
				flash(errorLed, 10, 10);
			} else { /// tout autre statut
				flash(statusLed, 5, 10);
			}
		} else { // n'importe quoi d'innattendu...
			flash(errorLed, 1, 25);
		}
	}
}

void sendXB() {
	xbee.send(tx);
	// après l'envoi d'un paquet, on attend un ACK et on laisse un timeout de 0.5 seconde.
	if (xbee.readPacket(500)) {
		// on a bien reçu quelque chose, reste à savoir ce que c'est...
		if (xbee.getResponse().getApiId() == ZB_TX_STATUS_RESPONSE) {
			xbee.getResponse().getZBTxStatusResponse(txStatus);
			// get the delivery status, the fifth byte
			if (txStatus.getDeliveryStatus() == SUCCESS) { // c'est un succès, le paquet a bien été transmis!
				flash(statusLed, 5, 50);
			} else { // erreur ... est-ce que le destinataire est bien allumé?
				flash(errorLed, 3, 500);
			}
		}
	} else { // le xbee local n'a pas généré de TX à temps (ne devrait pas arriver...)
		flash(errorLed, 2, 50);
	}
}

void sendBroadcast() {
	XBeeAddress64 bcAddr = XBeeAddress64(0x0, 0xfff);
	tx = ZBTxRequest(bcAddr, txData, sizeof(txData)); // on recrée l'objet
	sendXB();
	tx = ZBTxRequest(xbAddr, txData, sizeof(txData)); // on recrée l'objet d'origine
}

void setXBAddr(uint32_t msb, uint32_t lsb) {
	xbAddr.setMsb(msb);
	xbAddr.setLsb(lsb);
}

void setTxData(uint8_t newdata[2]) {
	txData[0] = newdata[0];
	txData[1] = newdata[1];
}

uint8_t* getRxdata() {
	return rxData;
}

void initMPXBCnx() {

}

#ifndef HIBERNATE_H_
#define HIBERNATE_H_

/**
 * prepareSleepMode place une variable à true.
 * A la prochaine boucle de loop() le système va s'endormir.
 * On utilise une variable pour éviter un double appel à la fonction de sommeil ce qui aurait des
 * conséquences inconnues... 
 */
void prepareSleepMode();
/**
 * justWokeUp est appelé dès le réveil du système.
 * Il est interdit d'utiliser des timers ou des connexions séries dans cette fonction.
 * Après l'appel à cette fonction, le code retourne dans loop() juste après l'appel à la fonction de sommeil.
 */
void wakeUp();
/**
 * sommeil permet de mettre le système en veille.
 * Pour avoir un avertissement, on fait rapidement clignoter la led interne (pin 13) pendant 2 secondes.
 * Enfin, on éteint cette led quand le système est en veille. On la rallume quand le système se réveille.
 */
void sleepMode();

#endif /*HIBERNATE_H_*/


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
/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 11, 2009
 * 
 * La documentation des fonctions est dans GenericFcts.h
 */


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

void flash(int led, int times, int wait) {
	int it;
	bool state=true;
	for (it=times*2; it > 0; it--) {
		digitalWrite(led, (state)?HIGH:LOW);
		state = !state;
	}
}
