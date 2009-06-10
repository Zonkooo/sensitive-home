/* Christopher Rabotin
 * Sensitive Home (http://sensitive-home.googlecode.com)
 * Created on Jun 10, 2009
 * 
 * Cette partie est pour le module de capteurs.
 */
 
/* Définitions:
 * ID: identifiant, c'est l'adresse MAC du XBee
 * 
 *  Principe de fonctionnement:
 * 1. Initialisation
 * 		- envoie de son ID en broadcast (i.e. envoie à (0x0, 0xfff) ).
 * 2. Fonctionnement normal
 * 		- est en veille
 * 		- attend une interruption du XBee pour le réveiller
 * 		- transmet au XBee la moyenne sur une seconde des 4 capteurs (même si non branchés)
 * 		- Attend un ACK du XBee-multiprise indiquant la bonne réception des informations
 * 		- se remet en veille
 * 3. Fonctionnement en mise à jour:
 * 		- idem qu'en fonctionnement normal
 * 		- sauf que la multiprise ne revoit pas un ACK mais un message de début de MàJ
 * 		- stocke le nouveau firmware dans la flash 
 * 		... TODO fin de conception MàJ 
 * 
 */
