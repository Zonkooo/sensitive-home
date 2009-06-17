/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 17, 2009
 */

/* Ce fichier est le header pour la connexion série basique pour le XPort
 */
#ifndef XPORTCNX_H_
#define XPORTCNX_H_
/* hasBeenInit permet de ne pas initialiser deux fois le module XPort
 */ 
bool hasBeenInit = false;
/* recvDelay est le delais pendant lequel le microcontrolleur va essayer de lire des données séries (en ms)
 */
int recvDelay = 100;
/* recvBuffer contient les données recues par le XPort
 */
char* recvBuffer;
/* Cette méthode permet d'initialiser la connexion XPort<->PC
 */
void initXPortCnx();
/* Permet d'envoyer au PC les données dans le paramètre data
 */
void sendXPort(const char* data);
/* Cette méthode est appelée depuis la fonction loop() principale.
 */
void recvXPort();
#endif /*XPORTCNX_H_*/
