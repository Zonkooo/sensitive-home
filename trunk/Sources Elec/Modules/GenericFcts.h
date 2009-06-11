/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 11, 2009
 * 
 * Ce fichier contient des fonctions dites génériques (e.g. flash d'une LED, PWM, etc.)
 */
 
#ifndef GENERICFCTS_H_
#define GENERICFCTS_H_

/* pwm permet de gérer le PWM d'une sortie (pin).
 * Cette fonction peut augmenter comme descendre progressivement.
 * Il suffit de renseigner start et stop (respectivement début et fin).
 */
void pwm(int pin, int start, int stop);

/* flash permet de faire flasher une LED. 
 * led: le pin de la LED (attention à bien la définir en tant que OUTPUT)
 * times: nombres de flash à effectuer
 * wait: durée en ms à attendre entre allumé/éteint et éteint/allumé
 */
void flash(int led, int times, int wait);
#endif /*GENERICFCTS_H_*/
