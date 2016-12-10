angular.module('iGovMarkers').service('FieldAttributesService', ['iGovMarkers', FieldAttributesService]);

function FieldAttributesService(MarkersFactory) {
  var self = this;

  this.EditableStatus = {
    EDITABLE: 1,
    READ_ONLY: 2,
    NOT_SET: 3
  };
  
  // enables styles from the iGovMarkersDefaults -> attributes 
  this.enableStyles = function () { 
	  var selectors = grepByPrefix("Style_"); 

	  if(selectors == null || selectors.length < 1 ) { 
		  console.log( "FieldAttributesService iGovMarkersDefaults.attributes is not set" ); 
		  return ;
	  } 
 
	  // цикл за селекторами iGovMarkersDefaults -> attributes  
	  for ( var i = 0; i < selectors.length; i++ ) { 

		  var styles = selectors[i];

		  var commonStyle = {}; 
		  var centralStyle = {}; 
		  var regionStyle = {}; 

		  // перевіряємо чи маємо, що встановлювати 
		  if( styles.oCommonStyle != null || styles["oCommonStyle"] != null ) { 
			  commonStyle = styles.oCommonStyle; 
			  
			  console.log("iGovMarkers.enableStyles -> oCommonStyle for '" + styles + "' is set"); 
		  }
		  
		  if( styles.oCentralStyle != null || styles["oCentralStyle"] != null ) { 
			  centralStyle = styles.oCentralStyle;
			  
			  console.log("iGovMarkers.enableStyles -> oCentralStyle for '" + styles + "' is set");
		  }
		  else { // Встановлюємо загальний стиль  
			  centralStyle = commonStyle;
			  
			  console.log("iGovMarkers.enableStyles -> oCommonStyle is set - oCentralStyle empty"); 
		  }

		  if( styles.oRegionStyle != null || styles["oRegionStyle"] != null ) { 
			  regionStyle = styles.oRegionStyle; 
			  
			  console.log("iGovMarkers.enableStyles -> oRegionStyle for '" + styles + "' is set");
		  } 
		  else { // Встановлюємо загальний стиль  
			  regionStyle = commonStyle;
			  
			  console.log("iGovMarkers.enableStyles -> oCommonStyle is set - oRegionStyle empty");
		  }

		  if( (commonStyle || centralStyle || regionStyle) && styles.aElement_ID != null && styles.aElement_ID.length > 0 ) {
			  
			  for( var j = 0; j < styles.aElement_ID.length; j++ ) {

				  var elem = window.angular.element(document).find(styles.aElement_ID[j]);

				  if( elem == null ) { 

					  elem = window.angular.element(document).find( "#" + styles.aElement_ID[j] ); 

				  } 

				  if( elem != null ) {

					  elem.css(commonStyle);
					  
					  console.log( "iGovMarkers.enableStyles -> oCommonStyle for '" + styles.aElement_ID[j] + "'  applied" ); 
				  }
				  else { 
					  console.log( "iGovMarkers.enableStyles -> element '" + styles.aElement_ID[j] + "' not set" );				  
				  }
/*
				  if ( StatesRepositoryProvider.isCentral() ) { 
					  elem.css(centralStyle); 
				  }
				  else { 
					  elem.css(regionStyle);
				  }
*/
			  }

		  }

		  if( styles.aSelectors != null && styles.aSelectors.length > 0 ) { 

			  for ( var j = 0; j < styles.aSelectors.length; j++ ) { 

				  var elem = window.angular.element(document).find(styles.aSelectors[j]);

				  if( elem != null ) {

					  elem.css(commonStyle);   

					  console.log("iGovMarkers.enableStyles -> oCommonStyle applied");
				  }
				  else {
					  console.log("iGovMarkers.enableStyles -> aSelector '"+ styles.aSelectors[j] +"' not found");
				  }

				  /*
				  if( StatesRepositoryProvider.isCentral() ) { 
					  elem.css(centralStyle);
				  }
				  else { 
					  elem.css(regionStyle);
				  }
				  */ 
			  }
		  }
		  
	  }	  
  }; 

  this.editableStatusFor = function(fieldId) {
    var result = self.EditableStatus.NOT_SET;
    grepByPrefix('Editable_').some(function(e) {
       if(_.indexOf(e.aField_ID, fieldId) > -1) {
         result = e.bValue ? self.EditableStatus.EDITABLE : self.EditableStatus.READ_ONLY;
         return true;
       } else {
         return false;
       }
    });
    return result;
  };

  this.insertSeparators = function (sID) {
    var markers = grepByPrefix('Line_');
    for (var i = 0; i < markers.length; i++){
      for (var j = 0; j < markers[i].aElement_ID.length; j++){
        if(markers[i].aElement_ID[j] === sID){
          return {
            bShow: true,
            sText: markers[i].sValue ? markers[i].sValue : ''
          }
        }
      }
    }
    return {
      bShow: false,
      sText: ''
    };
  };

  function grepByPrefix(prefix) {
    return MarkersFactory.grepByPrefix('attributes', prefix);
  }
}
