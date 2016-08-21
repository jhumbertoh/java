
function setFilterParams(aoData) {
	var frm=document.getElementById(config.form);
	buildFilter(frm,aoData);			
}

function buildFilter(frm,aoData){
	$(frm).find('input[type=text], input[type=radio], input[type=checkbox], select, textarea,input[type=hidden]').each(function(index,elem){				
		aoData.push( { "name": $(elem).attr('name'),"value":$(elem).val()} );				
	});	
} 

/**
 * Only for input not null or even not empty
 * @param aoData
 */
function setFilterParamsNotNull(aoData) {
	
	var frm=document.getElementById(config.form);
	buildFilterNotEmpty(frm,aoData);			
}
/**
 * Only for input not null or even not empty
 * @param frm
 * @param aoData
 */
function buildFilterNotEmpty(frm,aoData){
	
	$(frm).find('input[type=text], input[type=radio], input[type=checkbox], select, textarea, input[type=hidden]').each(function(index,elem){
		if($(elem).val() != ''){
		
			aoData.push( { "name": $(elem).attr('name'),"value":$(elem).val()});
		}
	});	
}

function Buscar(){
	var getTable=eval('oTable_'+config.tableId);
	getTable.fnReloadAjax();					
}

function renderDateFormat(data,type,full){
	return $.format.date(new Date(data),config.formatDate);
}

function downloadCsv(){
	var frm=$(document.getElementById(config.form));
	var urlSearch=frm.attr('action');
	var urlDownload=config.urlCsv;
	frm.attr('action',urlDownload);
	frm.submit();
	frm.attr('action',urlSearch);
}

function renderModifyAction(data,type,full){
	var url=config.urlModify+data;

	return '<a href="' + url + '">Modificar</a>';
}

function renderDesactivarAction(data,type,full){
  
  if(data=='ACTIVO'){    
    var onLink = "desactivarProducto('" + full.id  +"','"  + full.nombre+ "');"
    return '<a href="#" onclick="' + onLink + '">Desactivar</a>';              
  }else{
    return 'Desactivar';
  }
    
}

function renderNumberFormat(data,type,full){
	return parseFloat(data).toFixed(2);
}

function renderVerCargos(data,type,full){
	var url=config.urlVerCargo+data;
	return '<a href="' + url + '"> Ver Cargos / Desafiliar</a>';
}

function renderVerCargosHistorico(data,type,full){
	var url=config.urlVerCargoHistorico+data;
	return '<a href="' + url + '"> Ver Cargos</a>';
}

function renderViewAction(data,type,full){
	var url=config.urlView+data;
	return '<a href="' + url + '">Ver Afiliacion</a>';
} 

function renderEditAction(data,type,full){
	var url=config.urlView+data;
	return '<a href="' + url + '">Editar Afiliacion</a>';
} 

function renderViewModificarFecha(data,type,full){
	var michi= '#';
	var onLink = "javascript:mostrarPopUp();"
	return '<a href="' + michi + '" onclick="' + onLink + '" >Modificar Fecha</a>';
}

function renderViewModificarCargo(data,type,full){
	var michi= '#';
	var onLink = "javascript:mostrarPopUp();"
	return '<a href="' + michi + '" onclick="' + onLink + '" >Modificar</a>';
}

function mostrarPopUp(){
	$('#popup').fadeIn('slow');
	$('.popup-overlay').fadeIn('slow');
	$('.popup-overlay').height($(window).height());
	return false;
}

function renderViewSolicitudAction(data,type,full){
	var url=config.urlView+data;
	return '<a href="' + url + '">Ver Solicitud</a>';
} 

function customRequest(sSource, aoData, fnCallback, oSettings) {	
	if(config.pagina!=undefined){
		oSettings._iDisplayStart=(+config.pagina)	
		
		aoData[3].value=(+config.pagina)	
	}
		
	oSettings.jqXHR = $.ajax({
		"dataType" : 'json',
		"type" : "GET",
		"url" : sSource,
		"data" : aoData,	
		"success" : function(data){			
			fnCallback(data);
			config.pagina=undefined;
			if( data.error!=''){
				if(data.error!='empty')
				document.getElementById(config.errorContent).innerHTML=data.error;
				if(document.getElementById(config.tableId+'_info') != null)
				{	
					document.getElementById(config.tableId+'_info').style.display='none';
				}
				if(document.getElementById(config.tableId+'_paginate') != null)
				{	
					document.getElementById(config.tableId+'_paginate').style.display='none';
				}	
				$('#btnExportar').prop("disabled",true);
			}else{
				if(!config.errorContent == undefined)
				document.getElementById(config.errorContent).innerHTML='';
				if(document.getElementById(config.tableId+'_info') != null)
				{	
					document.getElementById(config.tableId+'_info').style.display='block';
				}
				if(document.getElementById(config.tableId+'_paginate') != null)
				{	
					document.getElementById(config.tableId+'_paginate').style.display='block';
				}	
				$('#btnExportar').prop("disabled",false);
			}
		}
	});
}

function customRequestCheckbox(sSource, aoData, fnCallback, oSettings) {	
	if(config.pagina!=undefined){
		oSettings._iDisplayStart=(+config.pagina)	
		
		aoData[3].value=(+config.pagina)	
	}
		
	oSettings.jqXHR = $.ajax({
		"dataType" : 'json',
		"type" : "GET",
		"url" : sSource,
		"data" : aoData,	
		"success" : function(data){			
			fnCallback(data);
			config.pagina=undefined;
			if( data.error!=''){
				if(data.error!='empty')
				document.getElementById(config.errorContent).innerHTML=data.error;
				document.getElementById(config.tableId+'_info').style.display='none';
				document.getElementById(config.tableId+'_paginate').style.display='none';
				$('#btnExportar').prop("disabled",true);
			}else{
				if(!config.errorContent == undefined)
				document.getElementById(config.errorContent).innerHTML='';
				document.getElementById(config.tableId+'_info').style.display='block';
				document.getElementById(config.tableId+'_paginate').style.display='block';
				$('#btnExportar').prop("disabled",false);
			}
			
			inicializaColumnas();
		}
	});
}

function parsearFecha(date){
    var parts = date.split("/");
    return new Date(parts[2], parts[1] - 1, parts[0]);
 }

 function obtenerFechaCadena(fechaPermitida)
 {
 	var dd = fechaPermitida.getDate();
     var mm = fechaPermitida.getMonth()+1; //January is 0!

     var yyyy = fechaPermitida.getFullYear();
     if(dd<10)
     {
     	dd='0'+dd;
     } 
     if(mm<10)
     {
     	mm='0'+mm;
     } 
     
     return dd+'/'+mm+'/'+yyyy;
 }

 function setReadOnly(strElement)
 {        
     var elem = document.getElementById(strElement);
     if (elem != null) {
         elem.onfocus = function preventFocus(e) { this.blur(); };
         elem.style.color = '#ACA899';
         elem.style.cursor = 'default';
         elem.onselectstart = function () { return false; } // ie
         elem.onmousedown = function () { return false; } //
         elem.onmouseover = function () { return false; } //
         elem.onmouseout = function () { return false; } //
         elem.onclick = function () { return false; } //
         elem.ondblclick = function () { return false; } //
     }

 }
 
 function renderDateFormatData(data,type,full){
	 
	 if(data != ''){
		return $.format.date(new Date(data),"dd/MM/yyyy");
 	}
}
 
 function limpiar()
 {
	 $('#'+ config.form).trigger("reset");
 }
