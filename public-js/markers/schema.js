/**
 * http://epoberezkin.github.io/ajv/
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
                aField_IDType: {
                    type: "object",
                    properties: {
                        aField_ID: {
                            type: "array",
                            minItems: 1,
                            items: {type: "string"},
                            uniqueItems: true
                        }
                    },
                    required: ["aField_ID"]
                },
                fieldsOnConditionType: {
                    type: "object",
                    allOf: [{"$ref": "#/definitions/aField_IDType"}],
                    properties: {
                        asID_Field: {
                            type: "object"
                        },
                        sCondition: {
                            type: "string"
                        },
                        sNote: {
                            type: "string"
                        }
                    },
                    required: ["aField_ID", "asID_Field", "sCondition"]
                },
                stringArray: {
                    type: "array",
                    items: {type: "string"},
                    minItems: 1
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
                        "^ShowFieldsOnNotEmpty_": {
                            allOf: [{"$ref": "#/definitions/aField_IDType"}],
                            properties: {
                                sField_ID_s: {type: "string"}
                            },
                            required: ["aField_ID", "sField_ID_s"]
                        },
                        "^ShowFieldsOnCondition_": {
                            "$ref": "#/definitions/fieldsOnConditionType"
                        },
                        "^RequiredFieldsOnCondition_": {
                            "$ref": "#/definitions/fieldsOnConditionType"
                        },
                        "^WritableFieldsOnCondition_": {
                            "$ref": "#/definitions/fieldsOnConditionType"
                        },
                        "^ValuesFieldsOnCondition_": {
                            allOf: [
                                {"$ref": "#/definitions/fieldsOnConditionType"}
                            ],
                            properties: {
                                asID_Field_sValue: {"$ref": "#/definitions/stringArray"}
                            },
                            required: ["aField_ID", "asID_Field", "sCondition", "asID_Field_sValue"]
                        }
                    },
                    additionalProperties: false
                },
                attributes: {
                    type: "object",
                    patternProperties: {
                        "^Editable_": {
                            type: "object",
                            allOf: [{"$ref": "#/definitions/aField_IDType"}],
                            properties: {
                                bValue: {type: "boolean"}
                            },
                            required: ["bValue"]
                        }
                    },
                    additionalProperties: false
                },
                validate: {
                    type: "object",
                    patternProperties: {
                        "PhoneUA": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "Mail": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "AutoVIN": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "TextUA": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "TextRU": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "DateFormat": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                sFormat: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat']
                        },
                        "DocumentDate": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                sFormat: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat']
                        },
                        "DateElapsed": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                bFuture: {type: "boolean"},
                                bLess: {type: "boolean"},
                                nDays: {type: "integer"},
                                nMonths: {type: "integer"},
                                nYears: {type: "integer"},
                                sFormat: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat']
                        },
                        "^DateElapsed_": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                inheritedValidator: {type: "string"},
                                sFormat: {type: "string"},
                                bFuture: {type: "boolean"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat']
                        },
                        "CodeKVED": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "CodeEDRPOU": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "CodeMFO": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "NumberBetween": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                nMin: {type: "integer"},
                                nMax: {type: "integer"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'nMin', "nMax"]
                        },
                        "NumberFractionalBetween": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                nMin: {type: "number"},
                                nMax: {type: "number"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'nMin', "nMax"]
                        },
                        "Numbers_Accounts": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                sMessage: {type: "string"}
                            }
                        },
                        "^CustomFormat_": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                sFormat: {type: "string"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'sFormat']
                        },
                        "FileSign": {
                            "$ref": "#/definitions/aField_IDType"
                        },
                        "^FileExtensions_": {
                            type: "object",
                            allOf: [{$ref: "#/definitions/aField_IDType"}],
                            properties: {
                                saExtension: {type: "string"},
                                sMessage: {type: "string"}
                            },
                            required: ["aField_ID", 'saExtension']
                        },
                        "FieldNotEmptyAndNonZero": {
                            "$ref": "#/definitions/aField_IDType"
                        }
                    },
                    additionalProperties: false
                }
            },
            additionalProperties: false
        }
    });