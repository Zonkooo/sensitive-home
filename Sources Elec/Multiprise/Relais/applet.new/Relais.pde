int pinCmd = 4;
short int cmd = true;
void setup() {
	Serial.begin(9600); // permet de communiquer en Serial via Arduino IDE
}

void loop() {
	delay(50);
	if (Serial.available()) {
		int val = Serial.read();
		if (val == 'O') {
			Serial.println("Serial: Commande ON");
			digitalWrite(12, HIGH);
		} else if (val == 'F') {
			Serial.println("Serial: Commande OFF");
			digitalWrite(12, LOW);
		}
	}
}
