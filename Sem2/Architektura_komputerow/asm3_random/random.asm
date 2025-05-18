.data
charset:		.asciiz	"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
newline:		.asciiz "\n"
char_count:		.word 10
string_count:		.word 10


.text
.globl main


main:
	# liczbik ciagow
	li $t0, 0
	lw $t4, char_count	# liczba znakow
	lw $t5, string_count	# liczba stringow
	
generate_all:
	bge $t0, $t5, done
	
	# licznik znakow w stringu
	li $t1, 0
	
generate_string:
	bgt $t1, $t4, next_string
	
	# losowanie liczby
	li $v0, 42
	li $a1, 62
	syscall
	
	# wybranie znaku na podstawie liczby losowek
	la $t2, charset
	add $t3, $t2, $a0
	
	lb $a0, 0($t3)
	li $v0, 11
	syscall
	
	addi $t1, $t1, 1
	j generate_string
	
next_string:
	li $v0, 4
	la $a0, newline
	syscall
	
	addi $t0, $t0, 1
	j generate_all

done:
	li $v0, 10
	syscall