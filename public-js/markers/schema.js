/**
 * JSON-схема для валидации маркеров
 *
 * Имя маркера проверяется на соответствие заданному через регулярные выражения
 * Перечень допустимых полей для маркера - находятся в свойстве properties
 * Перечень обязательных полей для маркера - перечислены в массиве свойства required
 *
 * Более подробная документация:
 * http://epoberezkin.github.io/ajv/
 */
angular.module('iGovMarkers')
    .constant('iGovMarkersSchema', {
        options: {
            allErrors: true,
            useDefaults: true
        }

    });