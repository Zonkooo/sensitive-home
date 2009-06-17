/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 10, 2009
 * 
 * Cette partie est pour la multiprise.
 */

/* Principe de fonctionnement:
 * 1. Initialisation:
 * 		- envoie un paquet TCP/IP au serveur via le XPort
 * 		- reçoit une réponse avec le nombre de MdC à contrôler
 * 2. Fonctionnement normal:
 * 		- demande périodiquement aux MdC qu'il contrôle les valeurs des capteurs
 * 		- retransmet ces données au serveur principal via XPort
 * 		- reçoit périodiquement des données de contrôle du serveur
 * 		- contrôle la multiprise
 * 		- écoute le réseau XBee afin de savoir si un nouveau module a été ajouté
 * 			- si c'est le cas, alors il retransmet l'ID de ce MdC au serveur
 * 3. Fonctionnement en mode Mise à Jour:
 * 		- reçoit le nouveau firmware depuis le XPort
 * 		- stocke ce firmware quelque part (à revoir...) dans le CPLD
 * 		... TODO: concevoir la mise à jour de la multiprise
 * 4. Fonctionnement en mode Mise à Jour MdC:
 * 		- reçoit le nouveau firmware depuis le XPort
 * 		- stocke ce firmware dans la flash
 * 		- établie une connexion avec l'un des MdC
 * 		- entre chaque paquet du firmware vérifie les commandes du serveur (prioritaire sur la màj)  
 * 
 */
//#include "XbeeCnx.h"
//#include "MpCtrl.h"
#include "XPortCnx.h"
/* A MODIFIER!!
 * ATTENTION: les deux premières sont des prises NORMALES les autres PWM
 */
#define NB_PRISES 5
const int prisesPWM[NB_PRISES-1] = { 1, 2, 3, 4, 5 };
int iterator,valueI,prise;
char valueC[3]= { 0 };
#ifdef DEBUG
bool found = false;
#endif

void setup() {
	initXPortCnx();
	for (iterator=0; iterator<NB_PRISES-1; iterator++) {
		pinMode(prises[iterator], OUTPUT);
	}
}

void loop() {
	/*
	 * dans le loop on doit d'abord vérifier la connexion au serveur
	 * ensuite, le serveur doit nous dire si on est connu ou non.
	 * Si on est connu alors la multiprise nous envoye la liste des modules de capteurs
	 * que l'on contrôle 
	 */
	// XBeeCnx
	recvXPort(); // cette méthode écrit les données reçues dans la variable recvBuffer
	if (recvBuffer[0]=='/'&& recvBuffer[6] == '\\') { // le message est complet
		prise = recvBuffer[1];
		valueC[3]= { 0 };
		strncpy(valueC, recvBuffer[3], 3);
		valueI = atoi(valueC);
		for (iterator = 0; iterator < NB_PRISES - 1; iterator++) {
			if (prises[iterator] == prise) {
				if (iterator < 2) { // c'est une prise ON/OFF
#ifdef DEBUG
					if(valueI != 0 && valueI != 1) {
						sendXPort("ERREUR de valeur (pas 0 ni 1!)");
					}
					found = true;
#endif
					digitalWrite(prises[iterator], valueI);
				} else { // c'est une prise PWM
					analogWrite(prises[iterator], valueI);
#ifdef DEBUG
					found = false;
#endif 
				}
			}
		}
#ifdef DEBUG
		if(!found) {
			sendXPort("Prise non trouvée!");
		}
#endif
		char* ackMsg;
		sprintf(ackMsg, "/ACK:%s", recvBuffer[1]);
		sendXPort(ackMsg);
	} else {
#ifdef DEBUG
		sendXPort("Aucun message reçu ou message tronquée!");
#endif
	}
	// test XBee
}
