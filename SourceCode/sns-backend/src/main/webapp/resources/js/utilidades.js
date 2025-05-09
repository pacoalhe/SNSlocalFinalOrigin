/**
 * Función encargada de evitar el backspace en los inputs que no sean editables.
 */
function deshabilitarBackSpace() {
	document.onkeydown = function() {
		// Se obtiene la tecla pulsada
		var key = event.keyCode;	
		if(key == null || ( key != 8 && key != 13)) {
			return true;
		}

		// Se obtiene el nombre del TAG que generó el evento
		var nombre = document.activeElement.name;
		// Si se pulso en la pagina general se bloquea el history.back()
		if (document.activeElement.name == null) {
			event.keyCode = 505;
			return false;
		}
		else {
			// Se obtiene el tipo de TAG, y si es de solo lectura
			try {
				var type = document.getElementsByName(document.activeElement.name)[0].type.toUpperCase();
				var tag = document.getElementsByName(document.activeElement.name)[0].nodeName.toUpperCase();
				var readOnly = document.getElementsByName(document.activeElement.name)[0].readOnly;

				// Si el evento se generó en un input, tex o textarea
				if ( tag == 'INPUT' || type == 'TEXT' || type == 'TEXTAREA') {
					// Si era de solo lectura se bloquea el history.back()
					if(readOnly == true ) {
						event.keyCode = 505;
						return false;
					}
					// Si era un radio o un checbox se bloquea el histoy.back()
					if( ((tag == 'INPUT' && type == 'RADIO') || (tag == 'INPUT' && type == 'CHECKBOX')) && (key == 8 || key == 13) ) {
						event.keyCode = 505;
						return false;
					}
					return true;
				}

				return !(key == 8 || key == 13);
			} catch (error) {
				return true;
			}
		}
	}
}

function soloNumeros(event) {
	if (48 > (event.which || event.keyCode) || (event.which || event.keyCode) > 57) { 
		return false;
	} 
	return true;
}

function soloBinario(event) {
	if ((event.which || event.keyCode) == 48 || (event.which || event.keyCode) == 49) { 
		return true;
	} 
	return false;
}

function idoInBcd(e) {
	var x = PF("TXT_Ido").jq.val();
	if(e.key >= 0 && e.key <= 9){
		PF("TXT_Bcd").jq.val( (x.length == 3) ? x : x + e.key );
	}else if(e.keyCode == 8){
		PF("TXT_Bcd").jq.val(PF("TXT_Bcd").jq.val().substring(0, PF("TXT_Bcd").jq.val().length-1));
	}
}