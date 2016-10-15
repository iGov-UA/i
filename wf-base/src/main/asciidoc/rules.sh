#!/bin/bash
# 
# Скрипт для создания из командной строки
# Swagger-документации - Наши стандарты, или просто "правила хорошего тона"
# 
# Для работы инсталировать ruby и две программы:
# gem install asciidoctor
# gem install coderay

BASE_DIR=.
DEST_DIR=.

asciidoctor --backend xhtml5 --doctype book --section-numbers \
            --base-dir $BASE_DIR --destination-dir $DEST_DIR \
            -a toc2 -a toclevels=3 -a source-highlighter=coderay \
             rules.adoc


