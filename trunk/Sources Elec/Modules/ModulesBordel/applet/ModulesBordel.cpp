#include "WProgram.h"
#define SLEEPTIMER 4
#define DEBUG
#include <avr/sleep.h>
#include <inttypes.h>
#include <stdio.h>
#include <avr/pgmspace.h>
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
	bool state=true;
	for (int it=times*2; it > 0; it--) {
		digitalWrite(led, (state) ? HIGH : LOW);
		state = !state;
		delay(wait);
	}
}

void prepareSleepMode() {
	isAsleep = true;
#ifdef DEBUG
	Serial.println("prepared for sleep");
#endif
}

void wakeUp() {
	digitalWrite(ledInternal, HIGH); // sets the LED on
}

void sleepMode() {
	delay(20);
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
#ifdef DEBUG
	Serial.println("Woke up!");
#endif
}

#include <XBee.h>
/* variables des LED de communication
 * La LED de statut est interne: elle n'a pas besoin d'être visible
 * La LED d'erreur est externe (et rouge): en cas d'erreur elle clignote pendant 5 secondes 
 */
const int statusLed = 13; // led interne au microcontrolleur
const int errorLed = 12; // led externe.

// définition des variables de communication
XBee xb = XBee();
XBeeAddress64 xbAddr = XBeeAddress64(0x0013a200, 0x4008ebef);
uint8_t rxOption = 0;
uint8_t *rxData;
uint8_t txOption = 0;
uint8_t *txData;
uint8_t tmpData[64];

// variables de réponse 
XBeeResponse response = XBeeResponse();
ZBRxResponse rx = ZBRxResponse();
ModemStatusResponse msr = ModemStatusResponse();
// variables de transfert (i.e. envoie)
ZBTxRequest tx = ZBTxRequest(xbAddr, txData, sizeof(txData));
ZBTxStatusResponse txStatus = ZBTxStatusResponse();
/*
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
 rxData[0] = rx.getData(0);
 rxData[1] = rx.getData(1);
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
 }*/

int sendXB() {
	xb.send(tx);
	// après l'envoi d'un paquet, on attend un ACK et on laisse un timeout de 0.5 seconde.
	if (xb.readPacket(500)) {
		// on a bien reçu quelque chose, reste à savoir ce que c'est...
		if (xb.getResponse().getApiId() == ZB_TX_STATUS_RESPONSE) {
			xb.getResponse().getZBTxStatusResponse(txStatus);
			// get the delivery status, the fifth byte
			if (txStatus.getDeliveryStatus() == SUCCESS) { // c'est un succès, le paquet a bien été transmis!
				flash(statusLed, 5, 50);
				return 1;
			} else { // erreur ... est-ce que le destinataire est bien allumé?
				flash(errorLed, 3, 500);
				return 0;
			}
		}
	} else { // le xbee local n'a pas généré de TX à temps (ne devrait pas arriver...)
		flash(errorLed, 2, 50);
		return -1;
	}
}
/*
 int sendBroadcast() {
 XBeeAddress64 bcAddr = XBeeAddress64(0x0, 0xfff);
 tx = ZBTxRequest(bcAddr, txData, sizeof(txData)); // on recrée l'objet
 int rtn = sendXB();
 tx = ZBTxRequest(xbAddr, txData, sizeof(txData)); // on recrée l'objet d'origine
 return rtn;
 }

 void setXBAddr(uint32_t msb, uint32_t lsb) {
 xbAddr.setMsb(msb);
 xbAddr.setLsb(lsb);
 }*/

void setTxData(uint8_t *newdata) {
	txData = newdata;
	tx = ZBTxRequest(xbAddr, txData, sizeof(txData));
}

/*uint8_t* getRxdata() {
 return rxData;
 }

 void initMPXBCnx() {
 

 }*/

void setup() {
	xb.begin(9600);
	Serial.begin(9600); // permet de communiquer en série via Arduino (à virer pour le produit final)
	attachInterrupt(1, wakeUp, LOW); // voir commentaire dans sleepMode
	// on précise que les pin sont des pins de lecture:
	pinMode(statusLed, OUTPUT);
	pinMode(errorLed, OUTPUT);
	pinMode(13, OUTPUT);
	pinMode(luxPin, INPUT);
	pinMode(tempPin, INPUT);
	pinMode(supPin1, INPUT);
	pinMode(supPin2, INPUT);
	// on allume la led interne
	digitalWrite(ledInternal, HIGH);
	flash(statusLed, 1, 100);
}

void loop() {
	if (isAsleep) {
		digitalWrite(ledInternal, LOW);
		sleepMode();
                delay(50);
	}
#ifdef DEBUG
	Serial.print("Reading ... ");
#endif
	luxVal += analogRead(luxPin);
	luxVal /= 2;
	tempVal += analogRead(tempPin);
	tempVal /= 2;
	supVal1 += analogRead(supPin1);
	supVal1 /= 2;
	supVal2 += analogRead(supPin2);
	supVal2 /= 2;
	if (count >= SLEEPTIMER) {
		uint8_t tmpData[64];
		sprintf_P((char*)tmpData, "%i:%i:%i:%i",luxVal,tempVal,supVal1,supVal2);
#ifdef DEBUG
		Serial.print("Sending...");
#endif
		setTxData(tmpData);
		int rtn = sendXB();
		if(rtn == 0) {
			Serial.println("Erreur 0");
		} else if(rtn == 1) {
			Serial.println("Succes 1");
		} else {
			Serial.print("Erreur "); Serial.println(rtn);
		}
		/* pour le moment, on affiche les données en série.
		 Plus tard, on enverra sur le Xbee via la variable payLoad (uint8_t[]) 
		 */
		Serial.print("luxVal = "); Serial.println(luxVal);
		Serial.print("tempVal = "); Serial.println(tempVal);
		Serial.print("supVal1 = "); Serial.println(supVal1);
		Serial.print("supVal2 = "); Serial.println(supVal2);
		Serial.println("Going to sleep...");
		delay(100); /* this delay is needed, the sleep function will provoke a Serial error otherwise!! */
		count = 0; luxVal = 0; tempVal = 0; supVal1 = 0; supVal2=0;
		prepareSleepMode(); /* sleep function called here*/
	}
	count++;
	delay(100); /* one third of a second!*/

}
int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

