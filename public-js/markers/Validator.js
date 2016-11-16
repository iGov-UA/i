
(function() {

	var Validator = function () { 

		// id of the field to validate 
		this.fieldId = ""; 
		// message that will be sent if validation empty 
		this.message = ""; 

		// type of the validator 
		this.type = "clear"; 

		this.validate = function () { 

			return false; 
		} 

		return this; 
	} 

	var ValidationFactory = function ()  { 

	} 

})(); 