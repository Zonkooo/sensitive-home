#define JDP
#include "XBouchon.h"
#include "WProgram.h"
/* Contient toutes les adresses sous forme de char*
 * Permet d'éviter la duplication de module de capteurs
 */
char xbSensorModulesAddr[MAX_SENSOR_MODULES][ADDR_LENGTH+1];
// variable de positions
char xbSensorModulesAddrPos = 0, xbTabPos=0;
/* variables de transfert (i.e. envoi)
 * On définit les variables constantes. Ainsi il suffit de modifier vers quoi pointe actualData avant l'envoi.
 */
#ifndef JDP
uint8_t reqData[] = { '/', 'R', 'E', 'Q', '\\' }, ackData[]= { '/', 'A', 'C',
		'K', '\\' }, welcomeData[]= { '/', 'N', 'E', 'W', '\\' };
uint8_t *actualData;
#else
char reqData[] = { '/', 'R', 'E', 'Q', '\\' }, ackData[]= { '/', 'A', 'C',
		'K', '\\' }, welcomeData[]= { '/', 'N', 'E', 'W', '\\' };
char *actualData;
#endif
int it=0;
bool duplicate;
byte rxData[30];
/* Cette variable stocke tous les objets permettant d'envoyer de messages
 */
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
/*
byte *readXB() {
	int pos=0;
	if(Serial.available()>0){
		rxData[pos++] = Serial.read();
	}
	return rxData;
}
*/
