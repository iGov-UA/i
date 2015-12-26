module.exports.markersSchema =
{
  title:"Markers Schema",
  type:"object",
  definitions: {
    aField_IDType: {
      properties: {
        aField_ID: {
          type:"array",
          minItems:1,
          items:{ type:"string" },
          uniqueItems: true
        }
      },
      required: ["aField_ID"]
    },
    fieldsOnConditionType: {
      type:"object",
      allOf: [{"$ref": "#/definitions/aField_IDType"}],
      properties:{
        asID_Field:{},
        sCondition:{
          type:"string"
        }
      },
      required: ["aField_ID", "asID_Field", "sCondition"]
    },
    validatorWith_aField_IDType: {
      type: "object",
      allOf: [{"$ref": "#/definitions/aField_IDType"}],
      required: ["aField_ID"]
    },
    stringArray: {
      type: "array",
      items: { type: "string" },
      minItems: 1
    }
  },
  properties:{
    motion:{
      type:"object",
      patternProperties:{
        "^ShowFieldsOnCondition_":{
          "$ref": "#/definitions/fieldsOnConditionType"
        },
        "^ValuesFieldsOnCondition_": {
          allOf: [
            { "$ref": "#/definitions/fieldsOnConditionType" }
          ],
          properties: {
            asID_Field_sValue: {"$ref": "#/definitions/stringArray"}
          },
          required: ["asID_Field_sValue"]
        },
        "^RequiredFieldsOnCondition_": {
          "$ref": "#/definitions/fieldsOnConditionType"
        }
      },
      additionalProperties:false
    },
    attributes: {
      type: "object",
      patternProperties: {
        "^Editable_": {
          type: "object",
          allOf: [{"$ref": "#/definitions/aField_IDType"}],
          properties: {
            bValue: { type: "boolean" }
          },
          required: ["bValue"]
        }
      },
      additionalProperties:false
    },
    validate: {
      type: "object",
      properties: {
        PhoneUA: { "$ref": "#/definitions/validatorWith_aField_IDType" }
        ,Mail: { "$ref": "#/definitions/validatorWith_aField_IDType" }
        ,AutoVIN: { "$ref": "#/definitions/validatorWith_aField_IDType" }
        ,TextUA: { "$ref": "#/definitions/validatorWith_aField_IDType" }
        ,TextRU: { "$ref": "#/definitions/validatorWith_aField_IDType" }
        ,FileSign: { "$ref": "#/definitions/validatorWith_aField_IDType" }
        ,DateFormat: {
          type: "object",
          allOf: [{$ref: "#/definitions/validatorWith_aField_IDType"}],
          properties: {
            sFormat: {type:"string"}
          },
          required: ['sFormat']
        }
        ,DateElapsed: {
          type: "object",
          allOf: [{$ref: "#/definitions/validatorWith_aField_IDType"}],
          properties: {
            bFuture: {type: "boolean"},
            bLess: {type: "boolean"},
            nDays: {type: "integer"},
            nMonths: {type: "integer"},
            nYears: {type: "integer"},
            sFormat: {type: "string"}
          }
        }
      }
    }
  },
  additionalProperties:false
};