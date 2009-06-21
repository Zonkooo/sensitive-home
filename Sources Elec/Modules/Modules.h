#ifndef MODULES_HPP_
#define MODULES_HPP_
#define SLEEPTIMER 4
#define DEBUG

#include <inttypes.h>
#include <stdio.h>
// définition des pins
const int ledInternal = 13;
const int sensor1Pin = 1;
const int sensor2Pin = 2;
const int sensor3Pin = 3;
const int sensor4Pin = 4;
// définition des valeurs
int sensor1Val = 0;
int sensor2Val = 0;
int sensor3Val = 0;
int sensor4Val = 0;

// variable int permettant de compter le nombre de boucles dans loop()
int count = 0;
#endif /*MODULES_HPP_*/
