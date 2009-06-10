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
#include "Defines.h"
//#include "XbeeCnx.h"
//#include "MpCtrl.h"
//#include "XportCnx.h"
void setup(){
	
}

void loop(){
	/*
	 * dans le loop on doit d'abord vérifier la connexion au serveur
	 * ensuite, le serveur doit nous dire si on est connu ou non.
	 * Si on est connu alors la multiprise nous envoye la liste des modules de capteurs
	 * que l'on contrôle 
	 */
}
