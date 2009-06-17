#include "WProgram.h"
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
#include "XPortCnx.h"
#define NB_PRISES 5
#define NB_PRISES_PWM 3

// permet d'avoir un code clair mais concis en mémoire (moins d'appels)
#define sendXPort Serial.print
#define initXPort Serial.begin
/* prises définit les pins qui contrôlent les prises. La commande contient le numéro de la prise et non le pin associé.
 * A MODIFIER!!
 * ATTENTION: les deux premières sont des prises NORMALES les autres PWM
 */
const unsigned char prises[NB_PRISES] = { 7, 8, 9, 10, 11 };
unsigned char pwmObj[NB_PRISES_PWM]= { 255, 0, 0 };
unsigned char pwmAct[NB_PRISES_PWM]= { 0, 0, 0 };
unsigned char valueI, priseI;
char valueC[3], ackMsg[recvMsgLength+4], *recvXP, priseC[1], iterator;

void setup() {
	initXPort(9600);
	for (iterator=0; iterator<NB_PRISES-1; iterator++) {
		pinMode(prises[iterator], OUTPUT);
	}
}

void loop() {
	/*
	 * dans le loop on doit d'abord vérifier si on a des PWM à augmenter puis la connexion au serveur
	 * ensuite, le serveur doit nous dire si on est connu ou non.
	 * Si on est connu alors la multiprise nous envoye la liste des modules de capteurs
	 * que l'on contrôle 
	 */
	// XBeeCnx
	for (iterator=NB_PRISES_PWM-1; iterator >= 0; iterator--) {
		if (pwmObj[iterator] == pwmAct[iterator])
			continue;
		if (pwmObj[iterator] - pwmAct[iterator] > 0) {
			analogWrite(prises[iterator+2], pwmAct[iterator]++);
		} else {
			analogWrite(prises[iterator+2], pwmAct[iterator]--);
		}
	}
	recvXPort(); // cette méthode écrit les données reçues dans la variable recvBuffer
	recvXP = getRecvBuffer();
	if (recvXP[0]=='/'&& recvXP[6] == '\\') { // le message est complet
		strncpy(priseC, &recvXP[1], 1);
		priseI = atoi(priseC);
		strncpy(valueC, &recvXP[3], 3);
		valueI = atoi(valueC);
		if ((priseI < 0 )&& (priseI - NB_PRISES) != 0) {
			resetRecvBuffer();
			return;
		}
		if (priseI - 2== 0) { // c'est une prise ON/OFF
			digitalWrite(prises[priseI], (valueI==1) ? HIGH : LOW);
		} else { // c'est une prise PWM
			pwmObj[priseI-2]=valueI;
			//analogWrite(prises[priseI], valueI);
		}
		sprintf(ackMsg, "/ACK:%s", &recvXP[1]);
		sendXPort(ackMsg);
		resetRecvBuffer();
	}
	// test XBee
	// on ne met pas de delay parce qu'il y en a un avec la lecture XPort (voir XPortCnx.h).
	delay(50);
}
int main(void) {
	init();
	setup();
	for (;;) {
		loop();
	}
	return 0;
}