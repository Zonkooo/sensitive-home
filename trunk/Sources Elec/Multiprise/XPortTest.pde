#include <string.h>
#include <avr/pgmspace.h>
#include "../libEEPROM/EEPROM.h"
#include "../AF_XPort/AF_XPort.h"
#include "../NewSoftSerial/NewSoftSerial.h"

/* Xport settings:
 *** Channel 1
 Baudrate 57600, I/F Mode 4C, Flow 02
 Port 00080
 Connect Mode : C4
 Send '+++' in Modem Mode enabled
 Show IP addr after 'RING' enabled
 Auto increment source port disabled
 Remote IP Adr: --- none ---, Port 00000
 Disconn Mode : 80  Disconn Time: 00:03
 Flush   Mode : 77
 */

// Strings stored in flash of the HTML we will be xmitting
const char http_404header[] PROGMEM = "HTTP/1.1 404 Not Found\nServer: arduino\nContent-Type: text/html\n\n<html><head><title>404 Error</title></head><body><h1>Sorry, that page cannot be found bitch!</h1></body>";
const char http_header[] PROGMEM = "HTTP/1.0 200 OK\nServer: atmega168 (arduino)\nContent-Type: text/html\n\n";
const char html_header[] PROGMEM = "<html><head><title>XPort Test</title></head> <body>";
const char html_body[] PROGMEM = "<h1>Hello</h1><div><p>Welcome on an XPort. Please check serial connection as well!</p></div></body></html>";

char linebuffer[128]; // a large buffer to store our data

// keep track of how many connections we've got
int requestNumber = 0;

// the xport!
#define XPORT_RX        2
#define XPORT_TX        3
#define XPORT_RESET     4
#define XPORT_CTS       6
#define XPORT_RTS       0 // not used
#define XPORT_DTR       0 // not used
AF_XPort
		xport = AF_XPort(XPORT_RX, XPORT_TX, XPORT_RESET, XPORT_DTR, XPORT_RTS, XPORT_CTS);

//////////////////////////////////////////////////////

uint16_t requested(void) {
	uint8_t read;
	Serial.println("Waiting for connection...");
	while (1) {
		read = xport.readline_timeout(linebuffer, 128, 200);
		if (read == 0) // nothing read (we timed out)
			return 0;
		if (read) // we got something! 
			Serial.println(linebuffer);
		if (strstr(linebuffer, "GET / ")) {
			return 200; // a valid request!
		}
		if (strstr(linebuffer, "GET /?")) {
			return 200; // a valid CGI request!
		}
		if (strstr(linebuffer, "GET ")) {
			return 404; // some other file, which we dont have
		}
	}
}

// Return the HTML place
void respond() {
	xport.flush(50);
	// first the stuff for the web client
	xport.ROM_print(http_header);
	// next the start of the html header
	xport.ROM_print(html_header);
	// the CSS code that will change the box to the right color when we first start
	xport.print("<style type=\"text/css\"> <!-- #sample_1 { background-color: #7777EE; } --> </style>");
	// the javascript for the color picker
	xport.ROM_print(html_body);
	// get rid of any other data left
	xport.flush(255);
	// disconnecting doesnt work on the xport direct by default? we will just use the timeout which works fine.
	xport.disconnect();

}

void setup() {
	Serial.begin(57600);
	Serial.println("serial port ready");
	xport.begin(57600);
	xport.reset();
	Serial.println("XPort ready");
}

void loop() {
	uint16_t ret;
	ret = requested();
	if (ret == 404) {
		xport.flush(250);
		// first the stuff for the web client
		xport.ROM_print(http_404header);
		xport.disconnect();
	} else if (ret == 200) {
		respond();
		Serial.print("Requested! No. ");
		Serial.println(requestNumber);
		delay(4000);
		pinMode(XPORT_RESET, HIGH);
		delay(50);
		pinMode(XPORT_RESET, LOW);

		requestNumber++;
	}
}

