openssl genrsa -des3 -passout pass:<passphrase> -out <name>.prk 2048
openssl req -new -key <name>.prk -passin pass:<passphrase> -out <name>.crt -x509 -sha256 -days 365 -subj "/C=UA/L=Kyiv/ST=Kyiv/O=ExampleOrganization/OU=ExampleOrganizationDepartment/CN=ExampleOrganizationClient/emailAddress=info@example.com"
