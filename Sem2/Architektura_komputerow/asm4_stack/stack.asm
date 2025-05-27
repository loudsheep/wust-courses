.data
prompt1:	.asciiz		"Podaj liczbe ciagow (1-10): "
prompt2:	.asciiz		"Podaj ciag: "
newline:		.asciiz		"\n"
buffer:		.space 		128
stack:		.space		2048
pointer:	.word		0



.text
.globl main


main:
	li $v0, 4
	la $a0, prompt1
	syscall
	
	li $v0, 5
	syscall
	move $t0, $v0	# $t0 - liczba ciagow
	
	li $t1, 0	# $t1 - licznik petli
	
read_loop:
	bge $t1, $t0, show_loop		# koniec wczytywania $t1 >= $t0
	
	li $v0, 4
	la $a0, prompt2
	syscall
	
	li $v0, 8
	la $a0, buffer
	li $a1, 128
	syscall
	
	la $t2, buffer			# $t2 - wskaznik na poczatek kolenego ciagu
	
next_word:
	lb $t3, 0($t2)			# $t3 - kolejny bajt
	
	beqz $t3, next_read_loop	# if end - next iter
	
	li $t4, ' '
	
skip_spaces:
	beq $t3, $t4, skip_one_char	# pomin jezeli spacja
	j collect_word
	
skip_one_char:
	addi $t2, $t2, 1
	lb $t3, 0($t2)
	
	j skip_spaces
	
collect_word:
	move $t5, $t2		# $t5 - poczatek slowa
	li $t6, 0		# $t6 - dlugosc slowa
	
count_loop:
	lb $t3, 0($t2)
	
	# dodajemy na stos jezeli koniec lub spacja
	beqz $t3, push
	beq $t3, $t4, push
	
	# kolejny adres znaku, zwiekszamy dlugosc w $t6
	addi $t2, $t2, 1
	addi $t6, $t6, 1
	j count_loop
	
push:
	la $t7, stack
	lw $t8, pointer
	add $t7, $t7, $t8	# $t7 - miejsce na nowy wyraz
	
	move $t9, $t5		# $t5 - zrodlo
	li $s0, 0
	
copy_loop:
	beq $s0, $t6, add_null
	
	lb $a1, 0($t9)
	sb $a1, 0($t7)
	
	addi $t7, $t7, 1
	addi $t9, $t9, 1
	addi $s0, $s0, 1
	j copy_loop
	
add_null:
	li $a1, 0
	sb $a1, 0($t7)
	
	lw $t8, pointer
	addi $t8, $t8, 32	# TODO: max word len is 32
	sw $t8, pointer
	
	j next_word

next_read_loop:
	addi $t1, $t1, 1
	j read_loop
	
show_loop:
	lw $t8, pointer
	
	beqz $t8, done
	
	sub $t8, $t8, 32
	sw $t8, pointer
	
	la $t7, stack
	add $t7, $t7, $t8
	
	li $v0, 4
	move $a0, $t7
	syscall
	
	li $v0, 4
	la $a0, newline
	syscall
	
	j show_loop
	
done:
	li $v0, 10
	syscall
	
	
	
	
	
	
	
	
	
	
