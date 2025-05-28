.data
prompt1:    .asciiz     "Podaj liczbe ciagow (1-10): "
prompt2:    .asciiz     "Podaj ciag: "
newline:    .asciiz     "\n"
buffer:     .space      128

.text
.globl main

main:
	# ustawienie stosu
	li $sp, 0x7ffffffc

    	li $v0, 4
    	la $a0, prompt1
    	syscall

    	li $v0, 5
    	syscall
    	move $t0, $v0        # $t0 - liczba ciągów

    	li $t1, 0            # $t1 - licznik pętli

read_loop:
    	bge $t1, $t0, show_loop

    	li $v0, 4
    	la $a0, prompt2
    	syscall

    	li $v0, 8
    	la $a0, buffer
    	li $a1, 128
    	syscall

    	la $t2, buffer

remove_newline:
    	lb $t3, 0($t2)
    	
    	beqz $t3, done_removing_newline
    	
    	li $t4, '\n'
    	
    	beq $t3, $t4, replace_with_null
    	
    	addi $t2, $t2, 1
    	
    	j remove_newline

replace_with_null:
    	sb $zero, 0($t2)

done_removing_newline:
    	la $t2, buffer

next_word:
    	lb $t3, 0($t2)
    	
    	beqz $t3, next_read_loop

    	li $t4, ' '

skip_spaces:
    	beq $t3, $t4, skip_one_char
    	
    	j collect_word

skip_one_char:
    	addi $t2, $t2, 1
    	lb $t3, 0($t2)
    	
    	j skip_spaces

collect_word:
    	move $t5, $t2        # początek słowa
    	li $t6, 0            # długość słowa

count_loop:
    	lb $t3, 0($t2)
    
    	beqz $t3, push
    	beq $t3, $t4, push
    
    	addi $t2, $t2, 1
    	addi $t6, $t6, 1
    	j count_loop

push:
    	li $t7, 0            # licznik kopiowanych znaków
    	move $t9, $t5        # źródło

    	# zarezerwuj 32 bajty na stosie
    	addi $sp, $sp, -32
    	move $s1, $sp        # $s1 wskazuje na miejsce na stosie

copy_loop:
    	beq $t7, $t6, add_null

    	lb $a1, 0($t9)
    	sb $a1, 0($s1)

    	addi $s1, $s1, 1
    	addi $t9, $t9, 1
    	addi $t7, $t7, 1
    	j copy_loop

add_null:
    	li $a1, 0
    
    	sb $a1, 0($s1)
    
    	j next_word

next_read_loop:
    	addi $t1, $t1, 1
    	j read_loop

show_loop:
    	# koniec gdy $sp == 0x7ffffffc
    	li $t8, 0x7ffffffc
    
    	beq $sp, $t8, done

    	move $a0, $sp
    	li $v0, 4
    	syscall

    	li $v0, 4
    	la $a0, newline
    	syscall

    	addi $sp, $sp, 32
    	j show_loop

done:
    	li $v0, 10
    	syscall
