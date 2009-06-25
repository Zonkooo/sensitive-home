/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 17, 2009
 */

/* Ce fichier est le header pour la connexion série basique pour le XPort.
 */
#ifndef XPORTCNX_H_
#define XPORTCNX_H_
#include "WProgram.h"
#include "HardwareSerial.h"
/* recvMsgLength contient le nombre maximum d'octets que l'on lit du XPort
 */
const int recvMsgLength = 11;
/* time est utilisé pour savoir le nombre de millisecondes depuis lequel on lit les données série
 */
//long time = 0;
/* hasBeenInit permet de ne pas initialiser deux fois le module XPort
 */ 
//static bool hasBeenInit = false;
/* recvDelay est le delais pendant lequel le microcontrolleur va essayer de lire des données séries (en ms)
 */
const int recvDelay = 250;
/* recvIt sert à se positionner dans la chaine reçue depuis le XPort
 */
//int recvIt = 0;
/* les variables suivantes sont des constantes pour la communication. Il serait intéressant de les stocker
 * définitivement en EEPROM une fois le protocole complètement fini.
 */
const char beginMsg = '/', endMsg='\\', reqMsg ='R', beginAckMsg[8]="/ACK:%s";
/* Cette méthode est appelée depuis la fonction loop() principale.
 */
void recvXPort();
/* la variable n'est pas déclarée en statique donc on a un getter.
 */
char *getRecvBuffer();
/* afin de remettre à NULL la variable (pour ne pas envoyer 40 fois le même accusé), on remet la variable à NULL
 */
void resetRecvBuffer();
#endif /*XPORTCNX_H_*/
