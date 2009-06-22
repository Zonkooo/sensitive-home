#include "XbeeCnx.h"
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
Rx64Response rx64 = Rx64Response();
Rx16Response rx16 = Rx16Response();
//static ModemStatusResponse msr = ModemStatusResponse();
// variables de transfert (i.e. envoie)
Tx64Request tx = Tx64Request(xbAddr, txData, sizeof(txData));
TxStatusResponse txStatus = TxStatusResponse();

void initXB(int speed) {
	xb.begin(speed);
}

void readXB() {
	xb.readPacket();
	response = xb.getResponse();
	if (response.isAvailable()) {
		if (response.getApiId() == RX_16_RESPONSE || response.getApiId()
				== RX_64_RESPONSE) {
			// got a rx packet
			if (response.getApiId() == RX_16_RESPONSE) {
				response.getRx16Response(rx16);
				rxOption = rx16.getOption();
				rxData[0] = rx16.getData(0);
				rxData[1] = rx16.getData(1);
			} else {
				response.getRx64Response(rx64);
				rxOption = rx64.getOption();
				rxData[0]= rx64.getData(0);
				rxData[1]= rx64.getData(1);
			}
#ifdef DEBUG
			flash(statusLed, 1, 10);
#endif
		} else {
#ifdef DEBUG
			flash(errorLed, 1, 25);
#endif
		}
	}
}

int sendXB() {
	xb.send(tx);
	// après l'envoi d'un paquet, on attend un ACK et on laisse un timeout de 0.5 seconde.
	if (xb.readPacket(500)) {
		// on a bien reçu quelque chose, reste à savoir ce que c'est...
		if (xb.getResponse().getApiId() == TX_STATUS_RESPONSE) {
			xb.getResponse().getZBTxStatusResponse(txStatus);
			// get the delivery status, the fifth byte
			if (txStatus.getStatus() == SUCCESS) { // c'est un succès, le paquet a bien été transmis
#ifdef DEBUG
				flash(statusLed, 5, 50);
#endif
				return 1;
			} else { // erreur ... est-ce que le destinataire est bien allumé?
#ifdef DEBUG
				flash(errorLed, 3, 500);
#endif
				return 0;
			}
		}
	} else { // le xbee local n'a pas généré de TX à temps (ne devrait pas arriver...)
#ifdef DEBUG
		flash(errorLed, 2, 50);
#endif
		return -1;
	}
}

int sendBroadcast() {
	XBeeAddress64 bcAddr = XBeeAddress64(0x0, 0xfff);
	tx = Tx64Request(bcAddr, txData, sizeof(txData)); // on recrée l'objet
	tx.setOption(0x8);
	int rtn = sendXB();
	tx = Tx64Request(xbAddr, txData, sizeof(txData)); // on recrée l'objet d'origine
	return rtn;
}

void setXBAddr(uint32_t msb, uint32_t lsb) {
	xbAddr.setMsb(msb);
	xbAddr.setLsb(lsb);
}

void setTxData(uint8_t newdata[]) {
	//strcpy_P(txData, newdata);
	int ite=16;
	for (ite=16; ite > 0; ite--) {
		txData[ite] = newdata[ite];
	}
	txData[17] = 0;
}

uint8_t* getRxdata() {
	return rxData;
}

void initMPXBCnx() {
	uint8_t hello[] ="/::::NOUVEAU::::\\";
	setTxData(hello);
	delay(50);
	sendBroadcast();
	readXB();
	if (strcmp((char*)rxData,"/NEW\\")==0) {
		xbAddr=rx64.getRemoteAddress64(); /* on considère l'adresse de la réponse comme la multiprise */
	}
#ifdef DEBUG
			else {
				digitalWrite(errorLed,HIGH);
				delay(500);
				digitalWrite(errorLed,LOW);
			}
#endif

		}
