/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 11, 2009
 * 
 * La documentation des fonctions est dans GenericFcts.h
 */

#include "GenericFcts.h"

void pwm(int pin, int start, int stop) {
	int val;
	if (start < stop) {
		for (val=start; val < stop; val+=5) {
			analogWrite(pin, val);
			delay(25);
		}
	} else {
		for (val=start; val > stop; val-=5) {
			analogWrite(pin, val);
			delay(25);
		}
	}
}

void flash(int led, int times, int wait) {
	int it;
	bool state=true;
	for (it=times*2; it > 0; it--) {
		digitalWrite(led, (state)?HIGH:LOW);
		state = !state;
	}
}
