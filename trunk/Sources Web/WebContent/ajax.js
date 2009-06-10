
function createXhrObject()
{
    if (window.XMLHttpRequest)
        return new XMLHttpRequest();
 
    if (window.ActiveXObject)
    {
        var names = [
            "Msxml2.XMLHTTP.6.0",
            "Msxml2.XMLHTTP.3.0",
            "Msxml2.XMLHTTP",
            "Microsoft.XMLHTTP"
        ];
        for(var i in names)
        {
            try{ return new ActiveXObject(names[i]); }
            catch(e){}
        }
    }
    window.alert("Votre navigateur ne prend pas en charge l'objet XMLHTTPRequest.");
    return null; // non supporté
}


function ajax_retour()
 {
  // Creation de l'objet XMLHttpRequest
  xhr = createXhrObject();
  xhr.onreadystatechange = function()
   {
    if(xhr.readyState == 4 && xhr.status == 200)
     {
      // Que fera AJAX si tout se passe bien, il va inserer dans le div "iris" le resultat de la page appellée
      document.getElementById('ajax_retour').innerHTML = xhr.responseText;
     }
   }
  // Nous allons interroger ajaxiris.php pour recuperer la reponse
  xhr.open("POST",'Ajax',true);
  xhr.setRequestHeader('Content-Type','x-www-form-urlencoded');
  xhr.send();
 }

