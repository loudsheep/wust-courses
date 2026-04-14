#!/bin/bash

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <filename.csv>"
    exit 1
fi

FILE=$1

if [ ! -f "$FILE" ]; then
    echo "File not found: $FILE"
    exit 1
fi

tail -n +2 "$FILE" | while IFS=, read -r EmployeeID Department DistinguishedName Enabled GivenName mail Manager Name ObjectClass ObjectGUID OfficePhone SamAccountName SID sn Surname Title UserPrincipalName
do
    if [ -z "$SamAccountName" ]; then
        continue
    fi

    LOGIN="$SamAccountName"
    NAME="$Name"
    PASSWORD="$EmployeeID"

    if id "$LOGIN" &>/dev/null; then
        echo "User $LOGIN already exists, skipping."
    else
        useradd -m -c "$NAME" -p "$(echo "$PASSWORD" | openssl passwd -6 -stdin)" "$LOGIN"

        chage -d 0 "$LOGIN"

        echo "User $LOGIN, with name $NAME, has been created with password $PASSWORD."
    fi
done