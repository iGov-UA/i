/**
 * JSON-схема для валидации маркеров
 *
 * Имя маркера проверяется на соответствие заданному через регулярные выражения
 * Перечень допустимых полей для маркера - находятся в свойстве properties
 * Перечень обязательных полей для маркера - перечислены в массиве свойства required
 *
 * Более подробная документация:
 * http://epoberezkin.github.io/ajv/
 * alpha
 */
angular.module('iGovMarkers')
    .constant('iGovMarkersSchema', {
        options: {
            allErrors: true,
            useDefaults: true
        },
        schema: {
            title: "Markers Schema",
            type: "object",
            definitions: {
                stringArray: {
                    type: "array",
                    minItems: 1,
                    items: {type: "string"},
                    uniqueItems: true
                }
            },
            properties: {
                motion: {
                    type: "object",
                    patternProperties: {
                        "^ReplaceTextSymbols_": {
                            type: "object",
                            properties: {
                                sID_Field: {type: "string"},
                                nSymbols: {type: "integer"},
                                sValueNew: {type: "string"},
                                sID_Element_sValue: {type: "string"}
                            },
                            required: ["sID_Field", "nSymbols", "sValueNew", "sID_Element_sValue"],
                            additionalProperties: false
                        },
                        "^ReplaceTextLastSymbols_": {
                            type: "object",
                            properties: {
                                sID_Field: {type: "string"},
                                nSymbols: {type: "integer"},
                                sValueNew: {type: "string"},
                                sID_Element_sValue: {type: "string"}
                            },
                            required: ["sID_Field", "nSymbols", "sValueNew", "sID_Element_sValue"],
                            additionalProperties: false
                        },
                        "^RequiredFieldsOnCondition": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                asID_Field: {type: "object", patternProperties: {"^[A-Za-z]": {type: "string"}}},
                                sCondition: {type: "string"},
                                sNote: {type: "string"}
                            },
                            required: ["aField_ID", "asID_Field", "sCondition"],
                            additionalProperties: false
                        },
                        "^WritableFieldsOnCondition_": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                asID_Field: {type: "object", patternProperties: {"^[A-Za-z]": {type: "string"}}},
                                sCondition: {type: "string"},
                                sNote: {type: "string"}
                            },
                            required: ["aField_ID", "asID_Field", "sCondition"],
                            additionalProperties: false
                        },
                        "^ValuesFieldsOnCondition": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                asID_Field: {type: "object", patternProperties: {"^[A-Za-z]": {type: "string"}}},
                                sCondition: {type: "string"},
                                asID_Field_sValue: {
                                    type: "array",
                                    minItems: 1,
                                    items: {type: "string"},
                                    uniqueItems: false
                                },
                                sNote: {type: "string"}
                            },
                            required: ["aField_ID", "asID_Field", "sCondition", "asID_Field_sValue"],
                            additionalProperties: false
                        },
                        "^ShowElementsOnTrue_": {
                            type: "object",
                            properties: {
                                aElement_ID: {"$ref": "#/definitions/stringArray"},
                                asID_Field: {type: "object", patternProperties: {"^[A-Za-z]": {type: "string"}}},
                                sCondition: {type: "string"},
                                sNote: {type: "string"}
                            },
                            required: ["aElement_ID", "asID_Field", "sCondition"],
                            additionalProperties: false
                        },
                        "^ShowFieldsOnNotEmpty_": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                sField_ID_s: {type: "string"}
                            },
                            required: ["aField_ID", "sField_ID_s"],
                            additionalProperties: false
                        },
                        "^ShowFieldsOnCondition_": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                asID_Field: {type: "object", patternProperties: {"^[A-Za-z]": {type: "string"}}},
                                sCondition: {type: "string"},
                                sNote: {type: "string"}
                            },
                            required: ["aField_ID", "asID_Field", "sCondition"],
                            additionalProperties: false
                        },
                        "^SplitTextHalf_": {
                            type: "object",
                            properties: {
                                sID_Field: {type: "string"},
                                sSpliter: {type: "string"},
                                sID_Element_sValue1: {type: "string"},
                                sID_Element_sValue2: {type: "string"}
                            },
                            required: ["sID_Field", "sSpliter", "sID_Element_sValue1", "sID_Element_sValue2"],
                            additionalProperties: false
                        }
                    },
                    additionalProperties: false
                },
                attributes: {
                    type: "object",
                    patternProperties: {
                        "^Editable_": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                bValue: {type: "boolean"}
                            },
                            required: ["aField_ID", "bValue"],
                            additionalProperties: false
                        },
                        "^Line_": {
                            type: "object",
                            properties: {
                                aElement_ID: {"$ref": "#/definitions/stringArray"},
                                sValue: {type: "string"}
                            },
                            required: ["aElement_ID"],
                            additionalProperties: false
                        }
                    },
                    additionalProperties: false
                },
                validate: {
                    type: "object",
                    patternProperties: {
                        "PhoneUA": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "Mail": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "AutoVIN": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "TextUA": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "TextRU": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "DateFormat": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                sFormat: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat'],
                            additionalProperties: false
                        },
                        "DocumentDate": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                sFormat: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat'],
                            additionalProperties: false
                        },
                        "^DateElapsed": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                bFuture: {type: "boolean"},
                                bLess: {type: "boolean"},
                                nDays: {type: "integer"},
                                nMonths: {type: "integer"},
                                nYears: {type: "integer"},
                                sFormat: {type: "string"},
                                inheritedValidator: {type: "string"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat'],
                            additionalProperties: false
                        },
                        "CodeKVED": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "CodeEDRPOU": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "CodeMFO": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "^StringRange": { 
                        	type: "object",
                        	properties: {
                        		aField_ID: {"$ref": "#/definitions/stringArray"},
                        		aField_Type: {"$ref": "#/definitions/stringArray"}, 
                        		nMin: {type: "integer"},
                        		nMax: {type: "integer"}, 
                        		sMessage: {type: "string"}
                        	},
                        	required: ["aField_ID"],
                        	additionalProperties: false
                        },
                        "^LongNumber": {
                        	type: "object",
                        	properties: {
                        		aField_ID: {"$ref": "#/definitions/stringArray"},
                        		aField_Type: {"$ref": "#/definitions/stringArray"}, 
                        		nMin: {type: "integer"},
                        		nMax: {type: "integer"},
                        		sMessage: {type: "string"}
                        	},
                        	required: ["aField_ID"],
                        	additionalProperties: false
                        },
                        "^DoubleNumber": { 
                        	type: "object", 
                        	properties: {
                        		aField_ID: {"$ref": "#/definitions/stringArray"},
                        		aField_Type: {"$ref": "#/definitions/stringArray"}, 
                        		nMin: {type: "integer"}, 
                        		nMax: {type: "integer"},
                        		sMessage: {type: "string"}                     		
                        	}, 
                        	required: ["aField_ID"], 
                        	additionalProperties: false
                        }, 
                        "^NumberBetween": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                nMin: {type: "integer"},
                                nMax: {type: "integer"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'nMin', "nMax"],
                            additionalProperties: false
                        },
                        "^NumberFractionalBetween": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                nMin: {type: "number"},
                                nMax: {type: "number"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'nMin', "nMax"],
                            additionalProperties: false
                        },
                        "^Numbers_Accounts": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "^CustomFormat_": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                sFormat: {type: "string"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat'],
                            additionalProperties: false
                        },
                        "FileSign": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        },
                        "^FileExtensions_": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"},
                                saExtension: {type: "string"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'saExtension'],
                            additionalProperties: false
                        },
                        "FieldNotEmptyAndNonZero": {
                            type: "object",
                            properties: {
                                aField_ID: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID"],
                            additionalProperties: false
                        }
                    },
                    additionalProperties: false
                }
            },
            additionalProperties: false
        }
    });
