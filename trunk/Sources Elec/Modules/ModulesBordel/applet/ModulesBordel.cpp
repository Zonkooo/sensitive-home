#define SLEEPTIMER 3
#define DEBUG
#include <avr/sleep.h>
#include <inttypes.h>
#include <stdio.h>
#include <avr/pgmspace.h>
// d\u00e9finition des pins
#include "WProgram.h"
void pwm(int pin, int start, int stop);
void flash(int led, int times, int wait);
void prepareSleepMode();
void wakeUp();
void sleepMode();
int sendXB();
void setTxData(uint8_t *newdata);
void setup();
void loop();
const int ledInternal = 13;
const int luxPin = 1;
const int tempPin = 2;
const int supPin1 = 3;
const int supPin2 = 4;
// d\u00e9finition des valeurs
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

	// le mode de veille utilis\u00e9 ne consomme de 100nA. Il ne peut \u00eatre r\u00e9veill\u00e9 que par interruption.
	set_sleep_mode(SLEEP_MODE_PWR_DOWN);
	sleep_enable(); // non n\u00e9cessaire: syst\u00e8me de "s\u00e9curit\u00e9" du microcontrolleur
	// lorsque le pin 3 (d'o\u00f9 le 1) passe \u00e0 la masse (LOW), la fonction wakeUp est appel\u00e9e
	attachInterrupt(1, wakeUp, LOW);
	sleep_mode(); // met r\u00e9ellement le syst\u00e8me en veille
	// le programme continu \u00e0 partir d'ici apr\u00e8s le r\u00e9veil	
	sleep_disable();
	isAsleep=false;
	detachInterrupt(0); // disables interrupt 0 on pin 2 so the wakeUpNow code will not be executed during normal running time. 
#ifdef DEBUG
	Serial.println("Woke up!");
#endif
}

#include <XBee.h>
/* variables des LED de communication
 * La LED de statut est interne: elle n'a pas besoin d'\u00eatre visible
 * La LED d'erreur est externe (et rouge): en cas d'erreur elle clignote pendant 5 secondes 
 */
const int statusLed = 13; // led interne au microcontrolleur
const int errorLed = 12; // led externe.

// d\u00e9finition des variables de communication
XBee xbee = XBee();
XBeeAddress64 xbAddr = XBeeAddress64(0x0013a200, 0x4008ebef);
uint8_t rxOption = 0;
uint8_t *rxData;
uint8_t txOption = 0;
uint8_t *txData;
uint8_t tmpData[64];

// variables de r\u00e9ponse 
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
 if (rx.getOption() == ZB_PACKET_ACKNOWLEDGED) { // on a re\u00e7u un ACK
 flash(statusLed, 1, 10);
 } else { // on a bien re\u00e7u une r\u00e9ponse mais l'envoyeur n'a re\u00e7u de ACK
 flash(errorLed, 2, 20);
 }
 rxData[0] = rx.getData(0);
 rxData[1] = rx.getData(1);
 } else if (xbee.getResponse().getApiId() == MODEM_STATUS_RESPONSE) {
 xbee.getResponse().getModemStatusResponse(msr);
 // the local XBee sends this response on certain events, like association/dissociation

 if (msr.getStatus() == ASSOCIATED) { // on est associ\u00e9
 flash(statusLed, 10, 10);
 } else if (msr.getStatus() == DISASSOCIATED) { // on s'est fait d\u00e9sassoci\u00e9
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
	xbee.send(tx);
	// apr\u00e8s l'envoi d'un paquet, on attend un ACK et on laisse un timeout de 0.5 seconde.
	if (xbee.readPacket(500)) {
		// on a bien re\u00e7u quelque chose, reste \u00e0 savoir ce que c'est...
		if (xbee.getResponse().getApiId() == ZB_TX_STATUS_RESPONSE) {
			xbee.getResponse().getZBTxStatusResponse(txStatus);
			// get the delivery status, the fifth byte
			if (txStatus.getDeliveryStatus() == SUCCESS) { // c'est un succ\u00e8s, le paquet a bien \u00e9t\u00e9 transmis!
				flash(statusLed, 5, 50);
				return 1;
			} else { // erreur ... est-ce que le destinataire est bien allum\u00e9?
				flash(errorLed, 3, 500);
				return 0;
			}
		}
	} else { // le xbee local n'a pas g\u00e9n\u00e9r\u00e9 de TX \u00e0 temps (ne devrait pas arriver...)
		flash(errorLed, 2, 50);
		return -1;
	}
}
/*
 int sendBroadcast() {
 XBeeAddress64 bcAddr = XBeeAddress64(0x0, 0xfff);
 tx = ZBTxRequest(bcAddr, txData, sizeof(txData)); // on recr\u00e9e l'objet
 int rtn = sendXB();
 tx = ZBTxRequest(xbAddr, txData, sizeof(txData)); // on recr\u00e9e l'objet d'origine
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
	xbee.begin(9600);
	Serial.begin(9600); // permet de communiquer en s\u00e9rie via Arduino (\u00e0 virer pour le produit final)
	attachInterrupt(1, wakeUp, LOW); // voir commentaire dans sleepMode
	// on pr\u00e9cise que les pin sont des pins de lecture:
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
	if (isAsleep)
		sleepMode();
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
		setTxData(tmpData);
		int rtn = sendXB();
		if(rtn == 0) {
			Serial.println("Erreur 0");
		} else if(rtn == 1) {
			Serial.println("Succes 1");
		} else {
			Serial.print("Erreur "); Serial.println(rtn);
		}
		/* pour le moment, on affiche les donn\u00e9es en s\u00e9rie.
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
	delay(333); /* one third of a second!*/

}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

