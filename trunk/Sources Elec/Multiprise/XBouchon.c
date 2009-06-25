#include "XBouchon.h"
/* Il n'y a qu'un seul objet XBee. Par contre il a un tableau d'objets Tx. 
 */
XBee xb = XBee();
/* Contient toutes les adresses sous forme de char*
 * Permet d'éviter la duplication de module de capteurs
 */
char xbSensorModulesAddr[MAX_SENSOR_MODULES][ADDR_LENGTH+1];
// variable de positions
char xbSensorModulesAddrPos = 0, xbTabPos=0;
XBeeAddress64 xbAddr;
// variables de réponse 
XBeeResponse response = XBeeResponse();
Rx64Response rx64 = Rx64Response();
Rx16Response rx16 = Rx16Response();
uint8_t rxOption = 0;
uint8_t *rxData;
/* variables de transfert (i.e. envoi)
 * On définit les variables constantes. Ainsi il suffit de modifier vers quoi pointe actualData avant l'envoi.
 */
uint8_t reqData[] = { '/', 'R', 'E', 'Q', '\\' }, ackData[]= { '/', 'A', 'C',
		'K', '\\' }, welcomeData[]= { '/', 'N', 'E', 'W', '\\' };
uint8_t *actualData;
int it=0;
bool duplicate;
TxStatusResponse txStatus = TxStatusResponse();
/* Cette variable stocke tous les objets permettant d'envoyer de messages
 */
Tx64Request tx =Tx64Request(xbAddr, actualData, sizeof(reqData));

void addObj(char* xbAddrStr) {
	duplicate = false;
	for (it=xbTabPos-1; it >= 0; it++) {
		if (strcmp(xbSensorModulesAddr[it], xbAddrStr)==0) {
			duplicate = true;
			break;
		}
	}
	if (!duplicate) {
		strncpy(xbSensorModulesAddr[xbTabPos],xbAddrStr,9);
		xbTabPos++;
		sendXB(SEND_NEW, getRegisteredNumber()-1); // on envoie un message de bienvenue
	}
}

char getRegisteredNumber() {
	return xbTabPos;
}

char *getRegisteredAddr(char who){
	return xbSensorModulesAddr[who];
}
char sendXB(char what, char who) {
	if (what==0) { // on veut envoyer NEW
		actualData = welcomeData;
	} else if (what==1) { // on veut envoyer ACK
		actualData = ackData;
	} else { // on veut envoyer une requête (REQ) 
		actualData = reqData;
	}
	Serial.print(actualData);
}

uint8_t *readXB() {
	xb.readPacket();
	response = xb.getResponse();
	if (response.isAvailable()) {
		if (response.getApiId() == RX_16_RESPONSE || response.getApiId()
				== RX_64_RESPONSE) {
			// got a rx packet
			if (response.getApiId() == RX_16_RESPONSE) {
				response.getRx16Response(rx16);
				rxOption = rx16.getOption();
				for (it=18/*((RxResponse())rx16).getDatalength()-1*/; it>0; it--) {
					rxData[it] = rx16.getData(it);
				}
			} else {
				response.getRx64Response(rx64);
				rxOption = rx64.getOption();
				for (it=18/*((RxResponse())rx16).getDatalength()-1*/; it>0; it--) {
					rxData[it] = rx64.getData(it);
				}
			}
			//			flash(statusLed, 1, 10);
		} else {
			//			flash(errorLed, 1, 25);
		}
	}
	return rxData;
}
