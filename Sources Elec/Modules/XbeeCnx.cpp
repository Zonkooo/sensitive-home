#include "XbeeCnx.h"
void readXBee() {

	xb.readPacket();

	if (xb.getResponse().isAvailable()) {
		if (xb.getResponse().getApiId() == ZB_RX_RESPONSE) {
			xb.getResponse().getZBRxResponse(rx); // permet de renseigner la classe Rx
			if (rx.getOption() == ZB_PACKET_ACKNOWLEDGED) { // on a reçu un ACK
				flash(statusLed, 1, 10);
			} else { // on a bien reçu une réponse mais l'envoyeur n'a reçu de ACK
				flash(errorLed, 2, 20);
			}
			rxData[0] = rx.getData(0);
			rxData[1] = rx.getData(1);
		} else if (xb.getResponse().getApiId() == MODEM_STATUS_RESPONSE) {
			xb.getResponse().getModemStatusResponse(msr);
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

int sendXB() {
	xb.send(tx);
	// après l'envoi d'un paquet, on attend un ACK et on laisse un timeout de 0.5 seconde.
	if (xb.readPacket(500)) {
		// on a bien reçu quelque chose, reste à savoir ce que c'est...
		if (xb.getResponse().getApiId() == ZB_TX_STATUS_RESPONSE) {
			xb.getResponse().getZBTxStatusResponse(txStatus);
			// get the delivery status, the fifth byte
			if (txStatus.getDeliveryStatus() == SUCCESS) { // c'est un succès, le paquet a bien été transmis!
				flash(statusLed, 5, 50); return 1;
			} else { // erreur ... est-ce que le destinataire est bien allumé?
				flash(errorLed, 3, 500); return 0;
			}
		}
	} else { // le xbee local n'a pas généré de TX à temps (ne devrait pas arriver...)
		flash(errorLed, 2, 50); return -1;
	}
}

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
}

void setTxData(uint8_t newdata[]) {
	//strcpy_P(txData, newdata);
	txData = newdata;
}

uint8_t* getRxdata() {
	return rxData;
}

void initMPXBCnx() {
	uint8_t hello[] = {'3','C','8','7'};
	setTxData(hello);
	
}

