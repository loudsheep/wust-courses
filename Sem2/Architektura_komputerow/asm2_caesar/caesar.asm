.data
prompt1:	.asciiz "Rodzaj operacji (0 - szyfrowanie, 1 - odszyfrowanie): "
prompt2:	.asciiz "Podaj przesuniecie: "
prompt3:	.asciiz "Wpisz text: "
name:		.space 16

# instrukcje syscall (wartość $v0)
# 1 - wypisanie liczby w $a0
# 4 - wypisanie textu z adresu w $a0
# 5 - wczytanie liczby do $v0
# 8 - wczytanie textu do $a0
# 10 - exit program

.text
.globl main


main:
	# rodzaj operacji
	li $v0, 4
	la $a0, prompt1
	syscall
	
	li $v0, 5
	syscall
	move $t0, $v0	# $t0 - rodzaj operacji
	
	# przesunięcie
	li $v0, 4
	la $a0, prompt2
	syscall
	
	li $v0, 5
	syscall
	move $t1, $v0
	
	# text do szyfrowania/deszyfrowania
	li $v0, 4
	la $a0, prompt3
	syscall
	
	li $v0, 8
	la $a0, name
	li $a1, 17
	syscall
	
	beq $t0, 1, negate_key
	j encode
	
	
	negate_key:
		sub $t1, $zero, $t1	# $t1 = -$t1
		
	encode:
		la $t3, name	# adres pierwszego znaku
		
		# TODO
	
	