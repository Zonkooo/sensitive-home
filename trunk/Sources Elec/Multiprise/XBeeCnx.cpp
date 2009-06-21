#include "XBeeCnx.h"
/* Ce tableau d'objets XBee contient tous les objets liés au modules de capteurs à gérer.
 * En effet, à chaque nouveau module de capteur, on créé un nouvel objet et l'on n'y touche plus 
 * par la suite.
 */
XBee xbTab[MAX_SENSOR_MODULES];
/* Ce tableau stocke tous les objets permettant d'envoyer de messages
 */
Tx64Request txTab[MAX_SENSOR_MODULES];

// variable de positions
char xbSensorModulesAddrPos = 0, xbTabPos=0;
XBeeAddress64 xbAddrTmp;
// variables de réponse 
XBeeResponse response = XBeeResponse();
Rx64Response rx64 = Rx64Response();
Rx16Response rx16 = Rx16Response();
//static ModemStatusResponse msr = ModemStatusResponse();
// variables de transfert (i.e. envoi)
uint8_t reqData[] = {'R','E','Q'}, ackData[]={'A','C','K'};
TxStatusResponse txStatus = TxStatusResponse();

void addObj(char* xbAddrStr) {
	xbTab[xbTabPos] = XBee();
	xbAddrTmp = XBeeAddress64(0x0013a200, reinterpret_cast<uint32_t>(xbAddrStr));
	txTab[xbTabPos]=Tx64Request(xbAddrTmp, reqData, sizeof(reqData));
	xbTabPos++;
}

XBee *getXbTab(){
	return xbTab;
}
Tx64Request *getTxTab(){
	return txTab;
}

char getTabPos(){
	return xbTabPos;
}
