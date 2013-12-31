#!/bin/bash

APP=`cat application.properties | grep app.name | sed 's/^.*=//'`

WAR_FILE=`ls target | grep .war`
WAR_DIR=`echo "$WAR_FILE" | sed s/\.war//`

if [ "`echo "$WAR_FILE" | sed s/.*\.war/1/`" != "1" ]; then
  echo "error: more than one .war file in target/ directory"
  exit 2
fi

if [ `whoami` != "root" ]; then
  echo "error: you must sudo this script"
  exit -1
fi

if [ -z "$1" ]; then
    echo "No environment supplied. Assuming Production..."
    echo "Installing under production environment..."
    CATALINA_BASE="$CATALINA_HOME/instances/8080"
else
    case $1 in
        dev )
            echo "Installing under dev environment..."
            APP=$APP"_dev"
            CATALINA_BASE="$CATALINA_HOME/instances/8080"
            ;;
        stable )
            echo "Installing under stable environment..."
            APP=$APP"_stable"
            CATALINA_BASE="$CATALINA_HOME/instances/8180"
            ;;
        * )
            echo "Usage: sudo install.sh [(dev|stable)]"
            exit 2
            ;;
    esac
fi

mysql -u root -p << EOFMYSQL

use mysql;

select "Creating user $APP..." as " ";
call createUser('$APP','$APP');
select "... user $APP created." as " ";

select "Creating database $APP and granting all to user $APP" as " ";
CREATE DATABASE IF NOT EXISTS \`$APP\`;
GRANT ALL ON $APP.* TO '$APP'@'localhost';
select "... database and grant successful." as " ";

use $APP;

select "Executing Install DDL..." as " ";
source src/sql/install_ddl.sql;
select "...Install DDL Executed." as " ";

select "Executing Install DML..." as " ";
source src/sql/install_dml.sql;
select "...Install DML Executed." as " ";

EOFMYSQL

echo "Deploying App..."

TARGET_WAR="target/$WAR_FILE"
COPY_TO="$CATALINA_BASE/webapps/$WAR_FILE"

echo "Copying $TARGET_WAR to $COPY_TO ..."

cp -f $TARGET_WAR $COPY_TO

echo "Installation complete. Your app will be available shortly."

exit 0
