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
	  var selectors = iGovMarkersDefaults.attributes;

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
		  if( styles.sCommonStyle != null && styles.sCommonStyle.length > 0 ) { 
			  commonStyle = styles.sCommonStyle; 
		  }
		  
		  if( styles.sCentralStyle != null && styles.sCommonStyle.length > 0 ) { 
			  centralStyle = styles.sCentralStyle;  
		  }
		  else { // Встановлюємо загальний стиль  
			  centralStyle = commonStyle; 
		  }

		  if( styles.sRegionStyle != null && styles.sRegionStyle.length > 0 ) { 
			  regionStyle = styles.sRegionStyle; 
		  } 
		  else { // Встановлюємо загальний стиль  
			  regionStyle = commonStyle; 
		  }
 
		  if( (commonStyle.length > 0 || centralStyle.length > 0 || regionStyle > 0) && styles.aElement_IDs != null && styles.aElement_IDs.length > 0 ) {
			  
			  for( var j = 0; j < styles.aElement_IDs.length; j++ ) {

				  var elem = window.angular.element(document).find("#"+styles.aElement_IDs[j]);  
				  elem.css(commonStyle);

				  if ( StatesRepositoryProvider.isCentral() ) { 
					  elem.css(centralStyle); 
				  }
				  else { 
					  elem.css(regionStyle);
				  }

			  }

		  }

		  if( styles.aSelectors != null && styles.aSelectors.length > 0 ) { 

			  for ( var j = 0; j < styles.aSelectors.length; j++ ) { 

				  var elem = window.angular.element(document).find(styles.aSelectors[j]);
				  elem.css(commonStyle); 
				  
				  if( StatesRepositoryProvider.isCentral() ) { 
					  elem.css(centralStyle);
				  }
				  else { 
					  elem.css(regionStyle);
				  }
			  }
		  }
		  
	  }	  
  }

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
