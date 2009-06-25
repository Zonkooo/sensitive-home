#include <avr/wdt.h> 
uint8_t mcusr_mirror;
void get_mcusr(void) __attribute__((naked)) __attribute__((section(".init3")));

int main(void) {
	get_mcusr(); // Disable the watchdog timer
	setup();
	for (;;) {
		loop();
	}
	return 0;
}

void get_mcusr(void) {
      mcusr_mirror = MCUSR;
      MCUSR = 0;
      wdt_disable(); 
}