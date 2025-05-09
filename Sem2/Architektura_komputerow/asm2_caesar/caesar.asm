.data
prompt1:	.asciiz "Rodzaj operacji (0 - szyfrowanie, 1 - odszyfrowanie): "
prompt2:	.asciiz "Podaj przesuniecie: "
prompt3:	.asciiz "Wpisz text: "
text:		.space 16

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
	la $a0, text
	li $a1, 17
	syscall
	
	# klucz * -1 jeżeli deszyfrowanie
	beq $t0, 1, negate_key
	j encode_loop_start
	
	
	negate_key:
		sub $t1, $zero, $t1	# $t1 = -$t1
		
	encode_loop_start:
		la $t3, text	# adres pierwszego znaku
		
	encode_loop:
		lb $t4, 0($t3)
		beqz $t4, done	# koniec jezeli nie ma już znaków
		
		# zakres znaków A-Z
		li $t5, 65
		li $t6, 90
		blt $t4, $t5, skip_char
		bgt $t4, $t6, skip_char
		
		sub $t7, $t4, $t5
		add $t7, $t7, $t1
		li $t8, 26
		
		rem $t7, $t7, $t8
		bltz $t7, negative
	
	negative:
		add $t7, $t7, $t8
		
		add $t4, $t7, $t5
	
	skip_char:
		sb $t4, 0($t3)
		addi $t3, $t3, 1
		j encode_loop	
		
	done:
		li $v0, 4
		la $a0, text
		
		syscall
		
		li $v0, 10
		syscall
		
	
	
