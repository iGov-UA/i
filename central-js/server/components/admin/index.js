var NodeCache = require("node-cache" );
//var uuid = require("uuid");
var crypto = require('crypto');

var adminKeysCache = new NodeCache();
var cacheKey = 'admin-keys-map';
// ��� �������
var aAdminInn = [
	'3119325858'
	,'2943209693' //Белявцев
	,'3167410996' //Забрудский
        ,'2817305057' //Свидрань
        ,'3075311805' //Грек
        ,'3314612661' //Войт
        ,'2268819154' //Братусь Михаил Владимирович
        ,'1806101517' //Конных Юрий Николаевич
        ,'2007813996' //Шипов Александр Юрьевич
        ,'2181117011' //Тарасенко Александр Иванович
        ,'2451100643' //Дручкова Жанна Іванівна
        ,'2955913663' //Рыбкина Екатерина Николаевна
        ,'3273602580' //Фещенко Юлия Витальевна
        ,'3364507588' //Сторож Алена Александровна
        ,'3017805029' //Аскерова Аида Александровна
        ,'3410304829' //Проскурова Олеся Олеговна
        ,'3375301375' //Чепурко Сергей Сергеевич
        ,'3453103770' //Кордин Виталий Юрьевич
        ,'3360509887' //Фалько Елена Николаевна
        ,'3364309262' //Юрченко Елизавета Юрьевна
        ,'3218506761' //Моисеева Галина Викторовна
        ,'3335600202' //Кулинич Ольга Григорівна
        ,'3356809424' //Соколова Дарья Андреевна
        ,'3384604904' //Левченко Юлия Вадимовна
        ,'3507906677' //Гавриш Олег Анатольевич
];

var getAdminKeys = function () {
	var result = adminKeysCache.get(cacheKey);
	if (!result) {
		result = {};
		setAdminKeys(result);
	}
	return result;
};

var setAdminKeys = function (value) {
	adminKeysCache.set(cacheKey, value);
};

var generateAdminToken = function (inn) {
	var unhashed = inn + (new Date()).toString();
	var result = crypto.createHash('sha1').update(unhashed).digest('hex'); //uuid.v1();
	var keys = getAdminKeys();
	keys[inn] = result;
	setAdminKeys(keys);
	return result;
};

var isAdminInn = function(inn) {
	return aAdminInn.indexOf(inn) > -1;
};

var Admin = function() {

};

Admin.generateAdminToken = generateAdminToken;
Admin.isAdminInn = isAdminInn;

module.exports = Admin;
