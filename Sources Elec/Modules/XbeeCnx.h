/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 11, 2009
 * 
 * Ce fichier est le header du fichier XbeeCnx.cpp.
 * Ce dernier contient les fonctions permettant d'exploiter la librairie Xbee
 * pour la communication Module de Capteurs <-> Mutliprise  
 */

#include <Xbee.h>
/* variables des LED de communication
 * La LED de statut est interne: elle n'a pas besoin d'être visible
 * La LED d'erreur est externe (et rouge): en cas d'erreur elle clignote pendant 5 secondes 
 */
const int statusLed = 13; // led interne au microcontrolleur
const int errorLed = 0; // led externe.

// définition des variables de communication
XBee xb = XBee();
XBeeAddress64 xbAddr = XBeeAddress64(0x0013a200, 0x4008ebef);
uint8_t rxOption = 0;
uint8_t rxData = 0;
uint8_t txOption = 0;
uint8_t txData = 0;

// variables de réponse 
Rx16Response rx16 = Rx16Response();
Rx64Response rx64 = Rx64Response();
// variables de transfert (i.e. envoie)
Tx64Request tx = Tx64Request(addr64, txData, sizeof(txData));
TxStatusResponse txStatus = TxStatusResponse();
/* readXB permet de lire des données du XBee
 * Les données reçues sont écrites dans la variable rxData et accessible via son accesseur getRxData();
 */
void readXB();
/* sendXB permet d'envoyer le payload à l'adresse XBee spécifiée. Cette addresse doit être modifiée via setXBAddr()
 * Il n'est pas nécessaire d'appeler cette fonction après l'appel à sendBroadcast(). 
 * Attention à bien écrire le payload à l'avance via la fonction setTxData().
 */
void sendXB();
/* sendBroadcast permet d'envoyer un message en broadcast (à tous les membres du réseau)
 * L'addresse xbAddr n'est modifiée que localement. Le payload doit être écrit par avance.
 */
void sendBroadcast();
/* setXBAddr permet de changer l'adresse du destinataire XBee.
 */
void setXBAddr(uint32_t msb, uint32_t lsb);
/* setTxData permet de changer le payload, c'est-à-dire le message à être envoyé via XBee.
 */
void setTxData(uint8_t newdata);
/* getRxdata permet de lire les données reçues via XBee.
 */
uint8_t getRxdata();
/* initMPXBCnx est la fonction permettant de connecter le module de capteurs à la multiprise.
 */
void initMPXBCnx();
