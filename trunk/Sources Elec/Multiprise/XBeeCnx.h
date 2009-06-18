/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 18, 2009
 */
 
#ifndef XBEECNX_H_
#define XBEECNX_H_
#define MAX_SENSOR_MODULES 5
/* xbSentMsgLength contient le nombre d'octets du message envoyé aux modules de capteurs
 * pour leur demander les informations et pour leur envoyer un accusé
 * Le message est: /REQ\ ou /ACK\
 */
const int xbSentMsgLength = 5;

/* xbRecvMsgLength contient le nombre maximum d'octets que l'on lit du XBee
 * Exemple de message: /304:374:129:810\
 */
const int xbRecvMsgLength = 17;

int xbSensorModulesAddrPos = 0;
char xbSensorModulesAddr[MAX_SENSOR_MODULES][9+1];
char xbSensorModulesAddrTmp[9+1];
#endif /*XBEECNX_H_*/
