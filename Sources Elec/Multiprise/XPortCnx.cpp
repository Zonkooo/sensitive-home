void initXPortCnx(){
	if(hasBeenInit) return;
	Serial.begin(9600);
}

void sendXPort(const char* data){
	Serial.print(data);
}

void recvXPort(){
	long time = millis();
	while((millis() - time) < recvDelay){
		if(Serial.available()){
			recvBuffer = Serial.read();
		}
	}
}
