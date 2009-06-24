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
 * ------------------------------
 * Protocole de communication:
 * 1. Commande:
 * 	a. réception d'une commande de type: /REQ:Numéro-de-prise:Valeur\
 * 		Exemple: /REQ:0:001\, /REQ:2:255\
 * 	b. envoi d'un accusé de réception de type: /ACK:Numéro-de-prise:Valeur\
 * 		Exemple: /ACK:0:001\, /ACK:2:255\
 * 2. Réception d'un message d'un nouveau module de capteurs:
 * 	a. réception via XBee d'un message du type: /----NOUVEAU----\
 * 	b. redirection de ce message au serveur en y ajoutant l'adresse du XBee. 
 * 		Exemple: /0013479A3:----NOUVEAU----\
 * 3. Réception d'un message de contrôle de module de capteurs du serveur:
 * 	a. Message du type: /Adresse-du-Xbee\
 * 		Exemple: /0013479A3\
 * 	b. Envoi d'un message de bienvenue au module de capteurs	
 * 		Exemple: /BIENVENUE\
 * Si le module de capteurs est déjà identifié sur une multiprise, alors il change de multiprise.
 */
#define SERIES_1
#define resetChar(x) x[0]=0;
#define NB_PRISES 5
#define NB_PRISES_PWM 3
#include "XBeeCnx.h"
#include "XPortCnx.h"
// permet d'avoir un code clair mais concis en mémoire (moins d'appels)
#define sendXPort Serial1.print
#define initXPort Serial1.begin
/* prises définit les pins qui contrôlent les prises. La commande contient le numéro de la prise et non le pin associé.
 * A MODIFIER!!
 * ATTENTION: les deux premières sont des prises NORMALES les autres PWM
 */
const unsigned char prises[NB_PRISES] = { 7, 8, 9, 10, 11 };
unsigned char pwmObj[NB_PRISES_PWM]= { 0, 0, 0 };
unsigned char pwmAct[NB_PRISES_PWM]= { 0, 0, 0 };
unsigned char valueI, priseI;
char valueC[3], ackMsg[12], *recvXP, priseC[1], iterator,
		sensorToXpMsg[xbRecvMsgLength+ADDR_LENGTH+1+1], count=0;
/* en static pour ne pas avoir de problème de compilation mais je ne sais pas où elle est redéfinie*/
static char xbSensorModulesAddrTmp[ADDR_LENGTH+1];

void setup() {
	initXPort(9600);
	for (iterator=NB_PRISES-1; iterator>=0; iterator--) {
		pinMode(prises[(int)iterator], OUTPUT);
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
	if (++count == 40) {
		// toutes les deux secondes on envoie des requêtes de données aux modules de capteurs
		count=0;
		// implémentation bouchon: envoi de données fausses de capteurs
		for (iterator=getRegisteredNumber()-1; iterator >= 0; iterator --) {
			sendXB(SEND_REQ, iterator);
			sprintf(sensorToXpMsg, "/%s:%s\\", getRegisteredAddr(iterator),
					readXB());
			//sprintf(sensorToXpMsg, "/%s\\",readXB());
			sendXPort(sensorToXpMsg);
			resetChar(sensorToXpMsg);
			sendXB(SEND_ACK, iterator);
		}
	}
	for (iterator=NB_PRISES_PWM-1; iterator >= 0; iterator--) {
		if (pwmObj[(int)iterator] == pwmAct[(int)iterator])
			continue;
		if (pwmObj[(int)iterator] > pwmAct[(int)iterator]) {
			analogWrite(prises[(int)iterator+2], pwmAct[(int)iterator]++);
		} else {
			analogWrite(prises[(int)iterator+2], pwmAct[(int)iterator]--);
		}
	}
	recvXPort(); // cette méthode écrit les données reçues dans la variable recvBuffer
	recvXP = getRecvBuffer();
	if (recvXP[0]==beginMsg && recvXP[recvMsgLength-1] == endMsg) { // le message est complet
		if (recvXP[1]==reqMsg) { // c'est une requête de commande
			strncpy(priseC, &recvXP[5], 1);
			priseI = atoi(priseC);
			strncpy(valueC, &recvXP[7], 3);
			valueI = atoi(valueC);
			if (priseI < 0&& priseI >= NB_PRISES) {
				resetRecvBuffer();
				return;
			}
			if (priseI < 2) { // c'est une prise ON/OFF
				digitalWrite(prises[priseI], (valueI==1) ? HIGH : LOW);
			} else { // c'est une prise PWM
				pwmObj[priseI-2]=valueI;
			}
		} else { // c'est un message demandant de gérer un module de capteurs
			if (getRegisteredNumber()< MAX_SENSOR_MODULES) {
				strncpy(xbSensorModulesAddrTmp, &recvXP[1], 9);
				xbSensorModulesAddrTmp[9]=0;
				addObj(xbSensorModulesAddrTmp);
				resetChar(xbSensorModulesAddrTmp);
			}
		}
		sprintf(ackMsg, beginAckMsg, &recvXP[5]);
		sendXPort(ackMsg);
		resetRecvBuffer();
	}
	// on ne met pas de delai parce qu'il y en a un avec la lecture XPort (voir XPortCnx.h).
	delay(50);
}
