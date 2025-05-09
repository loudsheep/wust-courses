.data
prompt1:    .asciiz "Podaj pierwszy skladnik: "
prompt2:    .asciiz "Podaj kod operacji (0-dodawanie, 1-odejmowanie, 2-dzielenie, 3-mnozenie): "
prompt3:    .asciiz "Podaj drugi skladnik: "
resultMsg:  .asciiz "Wynik: "
nextMsg:    .asciiz "\nCzy chcesz wykonac kolejna operacje? (1 - tak, 0 - nie): "
newline:    .asciiz "\n"
errMsg:     .asciiz "Blad: dzielenie przez zero!\n"

# Instrukcje syscall (wartość $v0)
# 1 - wypisanie liczby w $a0
# 4 - wypisanie textu z adresu w $a0
# 5 - wczytanie liczby do $v0
# 10 - exit program

.text
.globl main

main:
    loop:
        # Wczytaj pierwszy skladnik
        li $v0, 4
        la $a0, prompt1
        syscall

        li $v0, 5
        syscall
        move $t0, $v0   # $t0 = pierwszy skladnik

        # Wczytaj kod operacji
        li $v0, 4
        la $a0, prompt2
        syscall

        li $v0, 5
        syscall
        move $t1, $v0   # $t1 = kod operacji

        # Wczytaj drugi skladnik
        li $v0, 4
        la $a0, prompt3
        syscall

        li $v0, 5
        syscall
        move $t2, $v0   # $t2 = drugi skladnik

        # Wykonaj operacje
        li $t3, 0       # wynik

        beq $t1, 0, dodawanie
        beq $t1, 1, odejmowanie
        beq $t1, 2, dzielenie
        beq $t1, 3, mnozenie
        j blad

    dodawanie:
        add $t3, $t0, $t2
        j wyswietl

    odejmowanie:
        sub $t3, $t0, $t2
        j wyswietl

    dzielenie:
        beq $t2, 0, dzielenie_blad
        div $t0, $t2
        mflo $t3
        j wyswietl

    dzielenie_blad:
        li $v0, 4
        la $a0, errMsg
        syscall
        j zapytaj

    mnozenie:
        mul $t3, $t0, $t2
        j wyswietl

    blad:
        li $t3, 0  # domyślny wynik w razie błędnego kodu

    wyswietl:
        li $v0, 4
        la $a0, resultMsg
        syscall

        li $v0, 1
        move $a0, $t3
        syscall

    zapytaj:
        li $v0, 4
        la $a0, nextMsg
        syscall

        li $v0, 5
        syscall
        beq $v0, 1, loop

    koniec:
        li $v0, 10
        syscall
