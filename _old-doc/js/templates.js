angular.module('templates-main', ['html/catalog/services.html', 'html/service/city/built-in/index.html', 'html/service/city/index.html', 'html/service/city/link/index.html', 'html/service/country/built-in/index.html', 'html/service/country/index.html', 'html/service/country/link/index.html', 'html/service/discussion.html', 'html/service/general.html', 'html/service/index.html', 'html/service/instruction.html', 'html/service/legislation.html', 'html/service/questions.html', 'html/service/region/built-in/index.html', 'html/service/region/index.html', 'html/service/region/link/index.html', 'html/documents/index.html', 'html/journal/index.html']);

angular.module("html/catalog/services.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/catalog/services.html",
    "<div class=\"container\">\n" +
    "	<div class=\"row\" ng-repeat=\"category in catalog.categories\">\n" +
    "		<h1>{{category.name}}</h1>\n" +
    "		<div class=\"row\">\n" +
    "			<div class=\"col-xs-4\" ng-repeat=\"subcategory in category.subcategories\">\n" +
    "				<h3>{{subcategory.name}}</h3>\n" +
    "				<ul class=\"list-unstyled\">\n" +
    "					<li ng-repeat=\"service in subcategory.services\">\n" +
    "						<a ui-sref=\"service.general({'id': service.id })\">{{service.name}}</a>\n" +
    "					</li>\n" +
    "				</ul>\n" +
    "				<p><a class=\"btn btn-default\" href=\"#\" role=\"button\">Всі послуги <span class=\"badge badge-info\">42</span></a></p>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/city/built-in/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/city/built-in/index.html",
    "<div class=\"list-group\">\n" +
    "	<div class=\"list-group-item\" ng-class=\"{'disabled': data.city}\">\n" +
    "		<div class=\"row no-margin-bottom\">\n" +
    "			<div class=\"col-md-11 form-inline\">\n" +
    "				<span>Крок 1. Оберіть свій регіон </span>\n" +
    "				<div style=\"display:inline-block\">\n" +
    "					<span>Область</span>\n" +
    "					<select class=\"form-control\" name=\"region\" ng-model=\"data.region\" ng-options=\"region as region.name for region in places.regions track by region.id\" ng-disabled=\"data.city\"></select>\n" +
    "				</div>\n" +
    "				<div style=\"display:inline-block\" ng-if=\"data.region\">\n" +
    "					<span>Місто</span>\n" +
    "					<select class=\"form-control\" name=\"city\" ng-model=\"data.city\" ng-options=\"city as city.name for city in data.region.cities track by city.id\" ng-disabled=\"data.city\"></select>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"col-md-1\" ng-if=\"data.city\">\n" +
    "				<div class=\"link-button\">\n" +
    "					<a href=\"#\">Змінити</a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div class=\"list-group-item\" ng-if=\"data.city\">\n" +
    "		<span>Крок 2. Увійдіть в систему через</span>\n" +
    "		<button class=\"btn btn-info\"><i class=\"icon-door-lock\"></i><span class=\"btn-text\">i.gov.ua</span></button>\n" +
    "		<button class=\"btn btn-info\"><i class=\"icon-bank-id\"></i><span class=\"btn-text\">BankID</span></button>\n" +
    "		<button class=\"btn\"><i class=\"icon-mobil-id\"></i></button>\n" +
    "		<button class=\"btn btn-success i-btn-badge\">\n" +
    "			<i class=\"icon-badge\"></i>\n" +
    "			<span class=\"btn-text\"><div>Сертифікат електронно-</div><div>цифрового підпису</div></span>\n" +
    "		</button>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/city/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/city/index.html",
    "<div class=\"row\">\n" +
    "	<div class=\"col-md-12\">\n" +
    "		<div ui-view=\"\"></div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/city/link/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/city/link/index.html",
    "<div class=\"list-group\">\n" +
    "	<div class=\"list-group-item\" ng-class=\"{'disabled': data.city}\">\n" +
    "		<div class=\"row no-margin-bottom\">\n" +
    "			<div class=\"col-md-11 form-inline\">\n" +
    "				<span>Крок 1. Оберіть свій регіон </span>\n" +
    "				<div style=\"display:inline-block\">\n" +
    "					<span>Область</span>\n" +
    "					<select class=\"form-control\" name=\"region\" ng-model=\"data.region\" ng-options=\"region as region.name for region in places.regions track by region.id\" ng-disabled=\"data.city\"></select>\n" +
    "				</div>\n" +
    "				<div style=\"display:inline-block\" ng-if=\"data.region\">\n" +
    "					<span>Місто</span>\n" +
    "					<select class=\"form-control\" name=\"city\" ng-model=\"data.city\" ng-options=\"city as city.name for city in data.region.cities track by city.id\" ng-disabled=\"data.city\"></select>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"col-md-1\" ng-if=\"data.city\">\n" +
    "				<div class=\"link-button\">\n" +
    "					<a href=\"#\">Змінити</a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div class=\"list-group-item disabled\" ng-if=\"data.city\">\n" +
    "		<span>Крок 2. Перейдіть за посиланням:</span>\n" +
    "		<a ng-href=\"{{service.serviceType.url}}\">{{service.name}}</a>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/country/built-in/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/country/built-in/index.html",
    "<div class=\"list-group\">\n" +
    "	<div class=\"list-group-item\">\n" +
    "		<span>Крок 1. Увійдіть в систему через</span>\n" +
    "		<button class=\"btn btn-info\"><i class=\"icon-door-lock\"></i><span class=\"btn-text\">i.gov.ua</span></button>\n" +
    "		<button class=\"btn btn-info\"><i class=\"icon-bank-id\"></i><span class=\"btn-text\">BankID</span></button>\n" +
    "		<button class=\"btn\"><i class=\"icon-mobil-id\"></i></button>\n" +
    "		<button class=\"btn btn-success i-btn-badge\">\n" +
    "			<i class=\"icon-badge\"></i>\n" +
    "			<span class=\"btn-text\"><div>Сертифікат електронно-</div><div>цифрового підпису</div></span>\n" +
    "		</button>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/country/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/country/index.html",
    "<div class=\"row\">\n" +
    "	<div class=\"col-md-12\">\n" +
    "		<div ui-view=\"\"></div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/country/link/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/country/link/index.html",
    "<div class=\"list-group\">\n" +
    "	<div class=\"list-group-item disabled\">\n" +
    "		<span>Крок 1. Перейдіть за посиланням:</span>\n" +
    "		<a ng-href=\"{{service.serviceType.url}}\">{{service.name}}</a>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/discussion.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/discussion.html",
    "");
}]);

angular.module("html/service/general.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/general.html",
    "");
}]);

angular.module("html/service/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/index.html",
    "<div class=\"container\">\n" +
    "	<div class=\"row\">\n" +
    "		<div class=\"col-md-12\">\n" +
    "			<ul class=\"nav nav-tabs\">\n" +
    "				<li ui-sref-active=\"active\" role=\"presentation\"><a ui-sref=\"service.general({id: service.id})\">Послуга</a></li>\n" +
    "				<li ui-sref-active=\"active\" role=\"presentation\"><a ui-sref=\"service.instruction\">Інструкція</a></li>\n" +
    "				<li ui-sref-active=\"active\" role=\"presentation\"><a ui-sref=\"service.legislation\">Законодавство</a></li>\n" +
    "				<li ui-sref-active=\"active\" role=\"presentation\"><a ui-sref=\"service.questions\">Поширенні питання</a></li>\n" +
    "				<li ui-sref-active=\"active\" role=\"presentation\"><a ui-sref=\"service.discussion\">Обговорення</a></li>\n" +
    "			</ul>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div ui-view=\"\"></div>\n" +
    "</div>");
}]);

angular.module("html/service/instruction.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/instruction.html",
    "");
}]);

angular.module("html/service/legislation.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/legislation.html",
    "");
}]);

angular.module("html/service/questions.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/questions.html",
    "");
}]);

angular.module("html/service/region/built-in/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/region/built-in/index.html",
    "<div class=\"list-group\">\n" +
    "	<div class=\"list-group-item\" ng-class=\"{'disabled': data.region}\">\n" +
    "		<div class=\"row no-margin-bottom\">\n" +
    "			<div class=\"col-md-11 form-inline\">\n" +
    "				<span>Крок 1. Оберіть свій регіон </span>\n" +
    "				<div style=\"display:inline-block\">\n" +
    "					<span>Область</span>\n" +
    "					<select class=\"form-control\" name=\"region\" ng-model=\"data.region\" ng-options=\"region as region.name for region in places.regions track by region.id\" ng-disabled=\"data.region\"></select>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"col-md-1\" ng-if=\"data.region\">\n" +
    "				<div class=\"link-button\">\n" +
    "					<a href=\"#\">Змінити</a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div class=\"list-group-item\" ng-if=\"data.region\">\n" +
    "		<span>Крок 2. Увійдіть в систему через</span>\n" +
    "		<button class=\"btn btn-info\"><i class=\"icon-door-lock\"></i><span class=\"btn-text\">i.gov.ua</span></button>\n" +
    "		<button class=\"btn btn-info\"><i class=\"icon-bank-id\"></i><span class=\"btn-text\">BankID</span></button>\n" +
    "		<button class=\"btn\"><i class=\"icon-mobil-id\"></i></button>\n" +
    "		<button class=\"btn btn-success i-btn-badge\">\n" +
    "			<i class=\"icon-badge\"></i>\n" +
    "			<span class=\"btn-text\"><div>Сертифікат електронно-</div><div>цифрового підпису</div></span>\n" +
    "		</button>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/region/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/region/index.html",
    "<div class=\"row\">\n" +
    "	<div class=\"col-md-12\">\n" +
    "		<div ui-view=\"\"></div>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/service/region/link/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/service/region/link/index.html",
    "<div class=\"list-group\">\n" +
    "	<div class=\"list-group-item\" ng-class=\"{'disabled': data.region}\">\n" +
    "		<div class=\"row no-margin-bottom\">\n" +
    "			<div class=\"col-md-11 form-inline\">\n" +
    "				<span>Крок 1. Оберіть свій регіон </span>\n" +
    "				<div style=\"display:inline-block\">\n" +
    "					<span>Область</span>\n" +
    "					<select class=\"form-control\" name=\"region\" ng-model=\"data.region\" ng-options=\"region as region.name for region in places.regions track by region.id\" ng-disabled=\"data.region\"></select>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "			<div class=\"col-md-1\" ng-if=\"data.region\">\n" +
    "				<div class=\"link-button\">\n" +
    "					<a href=\"#\">Змінити</a>\n" +
    "				</div>\n" +
    "			</div>\n" +
    "		</div>\n" +
    "	</div>\n" +
    "	<div class=\"list-group-item disabled\" ng-if=\"data.region\">\n" +
    "		<span>Крок 2. Перейдіть за посиланням:</span>\n" +
    "		<a ng-href=\"{{service.serviceType.url}}\">{{service.name}}</a>\n" +
    "	</div>\n" +
    "</div>");
}]);

angular.module("html/documents/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/documents/index.html",
    "<div class=\"container\">\n" +
    "documents\n" +
    "</div>");
}]);

angular.module("html/journal/index.html", []).run(["$templateCache", function($templateCache) {
  $templateCache.put("html/journal/index.html",
    "<div class=\"container\">\n" +
    "journal\n" +
    "</div>");
}]);
