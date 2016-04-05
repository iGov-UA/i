vStep=$1

echo "Please, do the following if it has not been done yet"
echo
echo "Download https://nodejs.org/en/download/"
echo "    for example https://nodejs.org/dist/v4.2.5/node-v4.2.5-linux-x64.tar.xz"
echo
echo "Extract archive"
echo "    for example"
echo "        tar xf ./node-v4.2.5-linux-x64.tar.xz"
echo
echo "Go to the new directory/folder"
echo "    for example"
echo "        cd ./node-v4.2.5-linux-x64"
echo
echo "copy present files to the root dir"
echo "    for example"
echo "        sudo cp -r ./bin/* /bin/"
echo "        sudo cp -r ./lib/* /lib/"
echo
echo "copy server/config/local.env.sample.js to server/config/local.env.js"
echo "    for example"
echo "        cp server/config/local.env.sample.js server/config/local.env.js"
echo
echo "Run this script with --step2"

if [ "x${vStep}" != "x--step2" ]; then
    echo "Usage:" $0 "--step2"
    exit
fi

set -e -x

echo "Trying to become a root ..."
sudo apt-get -y install gcc
#sudo apt-get -y install npm
sudo apt-get -y install gem
sudo apt-get -y install ruby

sudo npm cache clean -f
#sudo npm install -g n
#sudo n 0.11.14
#node -v

sudo npm install -g grunt
sudo npm install -g grunt-cli
sudo npm install -g bower
sudo npm install -g gem

sudo gem install sass
npm install 
bower install

