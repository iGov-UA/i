echo "Trying to become a root ..."
sudo apt-get -y install gcc
sudo apt-get -y install npm
sudo apt-get -y install gem

sudo npm cache clean -f
sudo npm install -g n
sudo n 0.11.14
node -v

sudo npm install -g grunt-cli
sudo npm install -g bower
sudo npm install -g gem
