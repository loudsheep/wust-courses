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

    if id "$LOGIN" &>/dev/null; then
        userdel -r "$LOGIN"

        if [ $? -eq 0 ]; then
            echo "User $LOGIN has been deleted."
        else
            echo "Failed to delete user $LOGIN."
        fi
    else
        echo "User $LOGIN does not exist, skipping."
    fi
done