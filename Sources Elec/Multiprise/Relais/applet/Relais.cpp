#include "WProgram.h"
void setup();
void loop();
int pinCmd = 12;
short int cmd = true;
void setup() {
	Serial.begin(9600); // permet de communiquer en Serial via Arduino IDE
	pinMode(pinCmd, OUTPUT); // sets the digital pin as output
}

void loop() {
	delay(50);
	if (Serial.available()) {
		int val = Serial.read();
		if (val == 'O') {
			Serial.println("Serial: Commande ON");
			digitalWrite(pinCmd, HIGH);
		} else if (val == 'F') {
			Serial.println("Serial: Commande OFF");
			digitalWrite(pinCmd, LOW);
		}
	}
}

int main(void)
{
	init();

	setup();
    
	for (;;)
		loop();
        
	return 0;
}

