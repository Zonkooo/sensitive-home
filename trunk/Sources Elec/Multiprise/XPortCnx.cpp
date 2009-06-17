#include "XPortCnx.h"
#include "WProgram.h"
/* recvBuffer contient les données recues par le XPort
 */
char recvBuffer[recvMsgLength+1];
extern "C"void __cxa_pure_virtual(void){
	// call to a pure virtual function happened ... wow, should never happen ... stop
	while(1);
}
void recvXPort() {
	time = millis();
	if(Serial.available() <= 0) return;
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
	recvBuffer[0]=0;
}
