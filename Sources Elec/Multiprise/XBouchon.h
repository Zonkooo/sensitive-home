/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 25, 2009
 */
 /* Ce code remplace le code XBee pour la journée des projets: les commentaires sont erronnés (à part celui-ci)
  */
#ifndef XBOUCHON_H_
#define XBOUCHON_H_
#define MAX_SENSOR_MODULES 5
#define ADDR_LENGTH 9
#define SEND_NEW 0
#define SEND_ACK 1
#define SEND_REQ 2
/* xbSentMsgLength contient le nombre d'octets du message envoyé aux modules de capteurs
 * pour leur demander les informations et pour leur envoyer un accusé
 * Le message est: /REQ\ ou /ACK\
 */
const int xbSentMsgLength = 5;

/* xbRecvMsgLength contient le nombre maximum d'octets que l'on lit du XBee
 * Exemple de message: /304:374:129:810\
 */
const int xbRecvMsgLength = 17;

char *getRegisteredAddr(char who);
char sendXB(char what, char who);
//int *readXB();
void addObj(char* xbAddrStr);
char getRegisteredNumber();
#endif /*XBOUCHON_H_*/
