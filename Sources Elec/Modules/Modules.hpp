#ifndef MODULES_HPP_
#define MODULES_HPP_
#define SLEEPTIMER 3

// définition des pins
const int ledInternal = 13;
const int luxPin = 1;
const int tempPin = 2;
const int supPin1 = 3;
const int supPin2 = 4;
// définition des valeurs
int luxVal1 = 0;
int tempVal1 = 0;
int supVal1 = 0;
int supVal2 = 0;

// variable bool pour endormir
bool isAsleep = false;
// variable int permettant de compter le nombre de boucles dans loop()
int count = 0;
// payload qui sera envoyé au XBee
uint8_t payload[];
#endif /*MODULES_HPP_*/
