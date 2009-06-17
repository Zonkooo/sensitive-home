#include "XPortCnx.h"
#include "WProgram.h"
extern "C"void __cxa_pure_virtual(void){
	// call to a pure virtual function happened ... wow, should never happen ... stop
	while(1);
}
void initXPortCnx() {
	if (hasBeenInit)
		return;
	Serial.begin(9600);
}

void sendXPort(const char* data) {
	Serial.print(data);
}

void recvXPort() {
	time = millis();
	while ((Serial.available() > 0) && ((millis() - time) < recvDelay) && (recvIt < recvMsgLength)) {
		recvBuffer[recvIt++] = Serial.read();
	}
	recvIt = 0;
}
