/* -*- mode: jde; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Wiring project - http://wiring.org.co

  Copyright (c) 2004-08 Hernando Barragan

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/


#ifdef __cplusplus
extern "C" {
#endif

/*************************************************************
 * C Includes
 *************************************************************/

#include <math.h>
#include <inttypes.h>
#include <stdlib.h>
#include <stdio.h>
#include <stdarg.h>
#include <avr/io.h>
#include <avr/interrupt.h>
#include "WConstants.h"
#include "Binary.h"

/*************************************************************
 * C Core API Functions
 *************************************************************/

// main program prototypes
void setup(void);
void loop(void);

// timing prototypes
void initWiring(void);
void delay(long);
void delayMicroseconds(unsigned int);
long millis(void);

// pin prototypes
void pinMode(uint8_t, uint8_t);
uint8_t digitalRead(uint8_t);
void digitalWrite(uint8_t, uint8_t);
void portMode(uint8_t, uint8_t);
uint8_t portRead(uint8_t);
void portWrite(uint8_t, uint8_t);
int analogRead(uint8_t);
void analogWrite(uint8_t, uint16_t);

uint16_t makeWord(uint8_t, uint8_t);
// pulse prototypes
// unsigned long pulseIn(uint8_t, uint8_t);
void shiftOut(uint8_t, uint8_t, uint8_t, uint8_t);

// interrupt management prototypes
void attachInterrupt(uint8_t, void (*)(void), uint8_t mode);
void detachInterrupt(uint8_t);
void interruptMode(uint8_t, uint8_t);

// timer management prototypes
void timerAttach(uint8_t, void(*userFunc)(void));
void timerDetach(uint8_t);
void timer1PWMOff(void);

#ifdef __cplusplus
}
#endif

/*************************************************************
 * C++ Core API Functions
 *************************************************************/

#ifdef __cplusplus
#include "WString.h"
#include "WPrint.h"
// random prototypes
float random(float);
float random(float, float);
float map(float, float, float, float, float);
void randomSeed(unsigned int);
void shiftOut(uint8_t, uint8_t, uint8_t, byte);
// pulse prototypes
unsigned long pulseIn(uint8_t, uint8_t);
unsigned long pulseIn(uint8_t, uint8_t, unsigned long);
// pulse generation
void pulseOut(uint8_t, uint16_t, uint16_t);
void pulseOut(uint8_t, uint16_t);
uint16_t pulseRunning(uint8_t);
void pulseStop(uint8_t);
#endif

