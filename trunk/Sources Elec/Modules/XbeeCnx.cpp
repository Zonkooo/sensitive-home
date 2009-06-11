#include "XBeeCnx.h"
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

