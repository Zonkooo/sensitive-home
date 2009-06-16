#ifndef MODULES_HPP_
#define MODULES_HPP_
#define SLEEPTIMER 4
#define DEBUG

#include <inttypes.h>
#include <stdio.h>
// définition des pins
static const int ledInternal = 13;
static const int luxPin = 1;
static const int tempPin = 2;
static const int supPin1 = 3;
static const int supPin2 = 4;
// définition des valeurs
static int luxVal = 0;
static int tempVal = 0;
static int supVal1 = 0;
static int supVal2 = 0;

// variable int permettant de compter le nombre de boucles dans loop()
static int count = 0;
#endif /*MODULES_HPP_*/
