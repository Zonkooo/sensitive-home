#include "XBeeCnx.h"
void readXBee() {
	xbee.readPacket();

	if (xbee.getResponse().isAvailable()) {
		// réponse XBee
		if (xbee.getResponse().getApiId() == RX_16_RESPONSE || xbee.getResponse().getApiId() == RX_64_RESPONSE) {
			// le paquet est de type rx16 ou rx64
			if (xbee.getResponse().getApiId() == RX_16_RESPONSE) {
				xbee.getResponse().getRx16Response(rx16);
				option = rx16.getOption();
				data = rx16.getData(0);
			} else {
				xbee.getResponse().getRx64Response(rx64);
				option = rx64.getOption();
				data = rx64.getData(0);
			}
		} else { // quelque chose d'innattendu
			flashLed(errorLed, 1, 25);
		}
	}
}

void sendXB() {
	xb.send(txData);
}

void sendBroadcast() {
	XBeeAddress64 bcAddr = XBeeAddress64(0x0, 0xfff);
	ZBTxRequest zbTx = ZBTxRequest(addr64, txData, sizeof(txData));
	zbTX.setOption(0x8);
	xb.send(zbTx);
}

void setXBAddr(uint32_t msb, uint32_t lsb){
	xbAddr.setMsb(msb);
	xbAddr.setLsb(lsb);
}

void setTxData(uint8_t newdata){
	txData = newdata;
}

uint8_t getRxdata(){
	return rxData;
}

void initMPXBCnx(){
	
}

