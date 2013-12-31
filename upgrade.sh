#!/bin/bash

APP=`cat application.properties | grep app.name | sed 's/^.*=//'`

if [ `whoami` != "root" ]; then
  echo "error: you must sudo this script"
  exit -1
fi

if [ -z "$1" ]; then
    echo "No environment supplied. Assuming Production..."
    echo "Installing under production environment..."
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
        prod )
            echo "Installing under production environment..."
            CATALINA_BASE="$CATALINA_HOME/instances/8080"
            ;;
        * )
            echo "Usage: sudo install.sh [(dev|stable|prod)]"
            exit 2
            ;;
    esac
fi

mysql -u root -p << EOFMYSQL

use $APP;

select "Executing Upgrade DDL..." as " ";
source src/sql/upgrade_ddl.sql;
select "...Upgrade DDL Executed." as " ";

select "Executing Upgrade DML..." as " ";
source src/sql/upgrade_dml.sql;
select "...Upgrade DML Executed." as " ";

EOFMYSQL

echo "TODO: Deploy War"

echo "Upgrade complete."

exit 0