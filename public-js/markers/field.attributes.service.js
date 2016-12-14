angular.module('iGovMarkers').service('FieldAttributesService', ['iGovMarkers', FieldAttributesService]);

function FieldAttributesService(MarkersFactory) {
  var self = this;

  this.EditableStatus = {
    EDITABLE: 1,
    READ_ONLY: 2,
    NOT_SET: 3
  };
  
  this.FieldMentioned = {

		  inPrintForm: function ( tableId, tablesCollection ) {

			  var result = false; 

			  if( tablesCollection && tablesCollection.length > 0 ) { 
				 result = _.contains( tablesCollection, tableId ); 
			  }
			  else {
				  result = grepByPrefix( "PrintForm_" ).some(function (entry) { 
					  return _.contains ( entry.aTable_ID, tableId ); 
				  }); 
			  }
		  }   

  };
  

  /** 
   * function enableStyles
   *  Enables styles for iGov from iGovMarkers -> attributes -> Style_<>  
   * 
   * @returns void 
   * @author Sysprog   
   */
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

				  var query = "[name=" + styles.aElement_ID[j] + "]";
				  var elem = $(query);  
				  
				  if( elem == null || elem.length < 1 ) { 

					  elem = window.angular.element(document).find( "#" + styles.aElement_ID[j] ); 

				  } 

				  if( (elem == null || elem.length < 1) ) { 
		  
					  elem = window.angular.element(document).find(styles.aElement_ID[j]);

				  }

				  this.stylify( query, commonStyle, elem);

				  /*
				  if( elem != null ) {

					  elem.css(commonStyle);
					  
					  console.log( "iGovMarkers.enableStyles -> oCommonStyle for '" + styles.aElement_ID[j] + "'  applied" );
			  
  
				  }
				  else { 
					  console.log( "iGovMarkers.enableStyles -> element '" + styles.aElement_ID[j] + "' not set" );				  
				  }

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

				  this.stylify( styles.aSelectors[j], commonStyle, elem );

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
  }

  
  /** 
   * function stylify( query, stylesCollection, elem ) 
   *  Allows to set CSS styles to elements  
   * 
   * @param {String} query - CSS selector used if @elem is null  
   * @param {Object} stylesCollection - collection of CSS styles {background:#000} 
   * @param {Element} elem - may be null 
   * @returns {Boolean} true on success, false if element not found or stylesCollection is empty 
   * @author Sysprog 
   * @see Styles 
   */ 
  this.stylify = function ( query, stylesCollection, elem ) {

	  var result = false; 

	  if( elem != null && elem.length > 1 ) {

		  elem.css( stylesCollection );
		  
		  console.log("Style for element " + elem.id + " applied"); 
		  
		  result = true; 

	  } else {  

		  if( stylesCollection != null && query.length > 0 ) { 

			  var style = "";
			  angular.forEach( stylesCollection, function (value, key, obj) { style = style + key + ":" + value + "; " });
	
			  $("<style>" + query + " {" + style + "}" + "</style>").appendTo(document.head);
	
			  console.log(" Applied "+ query + " {"+ style + "}");
			  
			  result = true; 
		  }
		  else {
			  console.log( "stylesCollection empty for query '" + query + "'" );
		  }
	  } 

	  return result; 
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
