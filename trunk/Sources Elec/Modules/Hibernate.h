/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 22, 2009
 */
 
#ifndef HIBERNATE_H_
#define HIBERNATE_H_
#include <avr/sleep.h>
#include "WProgram.h"
#include "Modules.h"

/**
 * prepareSleepMode place une variable à true.
 * A la prochaine boucle de loop() le système va s'endormir.
 * On utilise une variable pour éviter un double appel à la fonction de sommeil ce qui aurait des
 * conséquences inconnues... 
 */
void prepareSleepMode();
/**
 * justWokeUp est appelé dès le réveil du système.
 * Il est interdit d'utiliser des timers ou des connexions séries dans cette fonction.
 * Après l'appel à cette fonction, le code retourne dans loop() juste après l'appel à la fonction de sommeil.
 */
void wakeUp();
/**
 * sommeil permet de mettre le système en veille.
 * Pour avoir un avertissement, on fait rapidement clignoter la led interne (pin 13) pendant 2 secondes.
 * Enfin, on éteint cette led quand le système est en veille. On la rallume quand le système se réveille.
 */
void sleepMode();

void setAsleep(bool d);
bool isAsleep();
#endif /*HIBERNATE_H_*/
