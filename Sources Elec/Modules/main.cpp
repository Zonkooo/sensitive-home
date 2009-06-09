/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 9, 2009
 */

#include <Xbee.h>

// constants
/* every variable which starts with XB is for the Xbee communication
 * @variable XBstatusLed  is the pin which has the status LED of XBee
 * @variable XBerrorLed is the pin which has the error LED of XBee
 */
const int XBstatusLed=0, XBerrorLed=0;
/* @variable idPin is the pin which has the unique id component
 */
const int idPin = 0; 
// @variable sensorPins is an array which contains the pins of the sensors
const int sensorPins[4] = { 0, 0, 0, 0 };
/* @variable connectedSensors is an array which determines which sensors are connected
 * if the use connects a new sensors he/she must reset the module. Doing so will reinitialize 
 * the value of connectedSensors.
 */
const short int connectedSensors[4] = { false, false, false, false };

/*
 * During the setup, the sensor module sends its ID over the network
 * (as a broadcast since it doesn't know the XBee receiver i.e. the multi-outlet).
 * 
 */
void setup() {

}

void loop() {

}
