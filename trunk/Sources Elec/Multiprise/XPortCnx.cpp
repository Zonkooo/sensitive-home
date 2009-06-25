#include "XPortCnx.h"

/* recvIt sert à se positionner dans la chaine reçue depuis le XPort
 */
int recvIt = 0;
long time = 0;
/* recvBuffer contient les données recues par le XPort
 */
char recvBuffer[12];
/*extern "C"void __cxa_pure_virtual(void){
	// call to a pure virtual function happened ... wow, should never happen ... stop
	while(1);
}*/
void recvXPort() {
	time = millis();
	if(Serial1.available() <= 0) return;
	while (((millis() - time) < recvDelay) && (recvIt < recvMsgLength)) {
		recvBuffer[recvIt++] = Serial.read();
	}
	recvBuffer[recvIt]=0;
	recvIt = 0;
}

char *getRecvBuffer(){
	return recvBuffer;
}

void resetRecvBuffer(){
	*recvBuffer={0};
}
