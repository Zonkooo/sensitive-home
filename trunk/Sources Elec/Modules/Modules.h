#ifndef MODULES_HPP_
#define MODULES_HPP_
#define SLEEPTIMER 4
#define DEBUG
#include <avr/sleep.h>
#include <inttypes.h>
#include <stdio.h>
// définition des pins
const int ledInternal = 13;
const int luxPin = 1;
const int tempPin = 2;
const int supPin1 = 3;
const int supPin2 = 4;
// définition des valeurs
int luxVal = 0;
int tempVal = 0;
int supVal1 = 0;
int supVal2 = 0;
uint8_t tmpData[64];
// variable bool pour endormir
bool isAsleep = false;
// variable int permettant de compter le nombre de boucles dans loop()
int count = 0;
// prototypes
void pwm();
void prepareSleepMode();
void wakeUp();
void sleepMode();
#endif /*MODULES_HPP_*/
