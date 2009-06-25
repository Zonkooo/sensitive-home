/*
 buffer.c - Buffer library for Wiring & Arduino
 Based on Hernando Barragan's original C implementation
 Copyright (c) 2006 Nicholas Zambetti.  All right reserved.
 
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.
 
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

#include <inttypes.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include "buffer.h"

// critical sections courtesy of Pascal Stang
// fix critical section buffer bug thanks to Bjoern Hartman bjoern@stanford.edu


#define CRITICAL_SECTION_START unsigned char _sreg = SREG; cli()
#define CRITICAL_SECTION_END SREG = _sreg

/*******************************************************************************
 Buffer Routines
 *******************************************************************************/

void buffer_init(buffer_t* b, char* data, uint16_t length)
{
	CRITICAL_SECTION_START;
	b->len = length;
	b->cnt = 0;
	b->in  = data;
	b->out = data;
	b->buf = data;
	CRITICAL_SECTION_END;
	
}

void buffer_put(buffer_t* b, char c)
{
	CRITICAL_SECTION_START;
	// do nothing if full
	if (!(b->cnt >= b->len)) {
		// place in buffer
		*b->in++ = c;
		b->cnt++;
		// wrap around to beginning, if needed
		if (b->in >= b->buf + b->len) {
			b->in = b->buf;
		}
	}
	CRITICAL_SECTION_END;
}

uint16_t buffer_get(buffer_t* b)
{
	char c;
	// return -1 if empty
	CRITICAL_SECTION_START;
	if (b->cnt == 0) {
		c= -1;
	} else {
		// pop char off buffer
		c = *b->out++;
		b->cnt--;
		// wrap around to beginning, if needed
		if (b->out >= b->buf + b->len) {
			b->out = b->buf;
		}
	}
	CRITICAL_SECTION_END;
	// return popped char
	return c;
}

uint8_t buffer_available(buffer_t* b)
{
	uint8_t count;
	CRITICAL_SECTION_START;
	count = b->cnt;
	CRITICAL_SECTION_END;
	return b->cnt; 
}


uint8_t buffer_look(buffer_t* b)
{
	uint8_t result;
	CRITICAL_SECTION_START;
	// return if empty
	if (b->cnt == 0) {
		result= 0;
	} else{
		result = *b->out;
	}
	CRITICAL_SECTION_END;
	return result;
}

uint8_t buffer_find(buffer_t* b, char c)
{
	char* p;
	uint8_t count;
	uint8_t result=0;
	CRITICAL_SECTION_START;
	// do nothing if empty
	if (b->cnt == 0) {
		result=0;
	} else {
		// non-empty, start search
		count = 0;
		p = b->out;
		while (p != b->in) {
			// return char if found
			if (*p == c){
				result= c;
				break;
			}
			// keep looking
			count++;
			p++;
			// return 0 if end is reached
			if (count >= b->cnt) {
				result=0;
				break;
			}
			// wrap around to beginning, if needed
			if (p > b->buf + b->len) {
				p = b->buf;
			}
		}
		//return 0;
	}
	CRITICAL_SECTION_END;
	return result;
}

