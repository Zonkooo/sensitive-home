#ifndef MODULES_HPP_
#define MODULES_HPP_
#define SLEEPTIMER 4
#define DEBUG

#include <inttypes.h>
#include <stdio.h>
// définition des pins
static const int ledInternal = 13;
static const int sensor1Pin = 1;
static const int sensor2Pin = 2;
static const int sensor3Pin = 3;
static const int sensor4Pin = 4;
// définition des valeurs
static int sensor1Val = 0;
static int sensor2Val = 0;
static int sensor3Val = 0;
static int sensor4Val = 0;

// variable int permettant de compter le nombre de boucles dans loop()
static int count = 0;
#endif /*MODULES_HPP_*/
