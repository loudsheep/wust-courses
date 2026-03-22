#!/bin/bash

if [ $# -lt 3 ]; then
    echo "Blad: Wymagane co najmniej 3 parametry."
    exit 1
fi

FIRST=$1
SECOND=$2
THIRD=$3

echo "Istnieja parametry $#, ktore obejmuja $@. Pierwszy to $FIRST, drugi to $SECOND, trzeci to $THIRD"