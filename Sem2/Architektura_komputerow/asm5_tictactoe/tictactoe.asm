.data
	inputIndicator: .asciiz ": "
	newLine: .byte '\n'
	space: .byte ' '
	wall: .byte '|'
	floor: .asciiz "-----------"
	X: .byte 'X'
	O: .byte 'O'
	
	# Prompty
	prompt1: .asciiz "Podaj liczbe rund do rozegrania"
	badInput: .asciiz "Wprowadzono bledne dane\n"
	prompt3: .asciiz "Twoj ruch! Postaw X w wybranym miejscu wpisujac numer dostepnego pola"
	round: .asciiz "RUNDA NR "
	prompt4: .asciiz "Runde wygrywa "
	prompt5: .asciiz "Aktualny wynik"
	prompt6: .asciiz "KONIEC GRY! ZWYCIEZCA PARTII ZOSTAJE "
	prompt7: .asciiz "Tie! Obaj gracze otrzymuja punkt"
	prompt8: .asciiz "Bledny ruch! Wybierz inne pole"
	prompt10: .asciiz "KONIEC GRY! PARTIA ZAKONCZONA REMISEM"
	prompt11: .asciiz "Komputer wykonal swoj ruch!"
	
	# Inne
	board: .space 9
.text
	# Ogolne
	main:
		move $s1, $zero
		move $s2, $zero
		move $s3, $zero
		
		j number_of_rounds
		
	back:
    		jr $ra
    		
    	exit:
    		li $v0, 10
    		syscall
    		
	badInput_link:
    		li $v0, 4
    		la $a0, badInput
    		syscall
    		j back
    		
    	# Board
    	showBoard_link:
    		move $t0, $zero
    		
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal showBoardLoop_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		
    		j back
    	showBoardLoop_link:
    		move $t1, $zero
    		
    		li $v0, 11
    		lb $a0, space
    		syscall
    		
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal showBoardRowLoop_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		
    		addi $t0, $t0, 1
    		
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		
    		beq $t0, 3, back
    		
    		li $v0, 4
    		la $a0, floor
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		j showBoardLoop_link

    	showBoardRowLoop_link:
    		mul $t2, $t0, 3
    		add $t2, $t2, $t1
    		li $v0, 11
    		lb $a0, board($t2)
    		syscall
    		
    		addi $t1, $t1, 1
    		beq $t1, 3, back
    		lb $a0, space
    		syscall
    		lb $a0, wall
    		syscall
    		lb $a0, space
    		syscall
    		j showBoardRowLoop_link

	resetBoard_link:
		addi $t0, $zero, 1
		la $t1, board
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal resetBoardLoop_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		
		j back
		
	resetBoardLoop_link:
		bgt $t0, 10, back
		addi $t2, $t0, 48
		sb $t2, 0($t1)
		addi $t1, $t1, 1
		addi $t0, $t0, 1
		j resetBoardLoop_link
	
	
	# Organizacja gry
	number_of_rounds:
		li $v0, 4
    		la $a0, prompt1
    		syscall
    		la $a0, inputIndicator
    		syscall
    		li $v0, 5
    		syscall
    		move $s0, $v0
    		bgt $s0, 0, mainGameLoop
    		jal badInput_link
    		j number_of_rounds
    		
	mainGameLoop:
    		bge $s3, $s0, results
    		addi $s3, $s3, 1
    		
    		jal resetBoard_link
    		
    		li $v0, 4
    		la $a0, round
    		syscall
    		li $v0, 1
    		move $a0, $s3
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		
    		move $t3, $zero
    		li $t4, 1
    		jal humanTurn_link
    		
    		j mainGameLoop
    	pointX:
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal showBoard_link
    		addi $s1, $s1, 1
    		
    		li $v0, 4
    		la $a0, prompt4
    		syscall
    		li $v0, 11
    		lb $a0, X
    		syscall
    		lb $a0, newLine
    		syscall
    		
		jal currentScore_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		
    		j back
    	punktO:
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal showBoard_link
    		addi $s2, $s2, 1
    		
    		li $v0, 4
    		la $a0, prompt4
    		syscall
    		li $v0, 11
    		lb $a0, O
    		syscall
    		lb $a0, newLine
    		syscall
    		
		jal currentScore_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		
    		j back
    	tie:
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal showBoard_link
    		#addi $s1, $s1, 1
    		#addi $s2, $s2, 1
    		
    		li $v0, 4
    		la $a0, prompt7
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		
		jal currentScore_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		
    		j back
    		
    	currentScore_link:
    		li $v0, 4
    		la $a0, prompt5
    		syscall
    		la $a0, inputIndicator
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		lb $a0, X
    		syscall
    		li $v0, 4
    		la $a0, inputIndicator
    		syscall
    		li $v0, 1
    		move $a0, $s1
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		lb $a0, O
    		syscall
    		li $v0, 4
    		la $a0, inputIndicator
    		syscall
    		li $v0, 1
    		move $a0, $s2
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		j back

    	results:
    		sub $s3, $s1, $s2
    		beqz $s3, resultTie
    		li $v0, 4
    		la $a0, prompt6
    		syscall
    		li $v0, 11
    		bgezal $s3, setX
    		bltzal $s3, setO
    		syscall
    		lb $a0, newLine
    		syscall
    		j exit

    	resultTie:
    		li $v0, 4
    		la $a0, prompt10
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		j exit

	checkBoardState_link:
		la $t5, board
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		# Rzad 1
		lb $t0,0($t5)
		lb $t1,1($t5)
		lb $t2,2($t5)
		jal checkLine_link
		# Rzad 2
		lb $t0,3($t5)
		lb $t1,4($t5)
		lb $t2,5($t5)
		jal checkLine_link
		# Rzad 3
		lb $t0,6($t5)
		lb $t1,7($t5)
		lb $t2,8($t5)
		jal checkLine_link
		# Kolumna 1
		lb $t0,0($t5)
		lb $t1,3($t5)
		lb $t2,6($t5)
		jal checkLine_link
		# Kolumna 2
		lb $t0,1($t5)
		lb $t1,4($t5)
		lb $t2,7($t5)
		jal checkLine_link
		# Kolumna 3
		lb $t0,2($t5)
		lb $t1,5($t5)
		lb $t2,8($t5)
		jal checkLine_link
		# Przekatna 1
		lb $t0,0($t5)
		lb $t1,4($t5)
		lb $t2,8($t5)
		jal checkLine_link
		# Przekatna 2
		lb $t0,2($t5)
		lb $t1,4($t5)
		lb $t2,6($t5)
		jal checkLine_link
		# Tie
		move $t0, $zero
		move $t1, $zero
		jal checkTie_link
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	checkLine_link:
		beq $t0, $t1, checkSecond
		j back

	checkSecond:
		beq $t0, $t2, checkCharacter
		j back

	checkCharacter:
		beq $t0, 'X', pktX
		beq $t0, 'O', pktO
		j back

	countLines:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		move $t7, $zero
		move $t8, $zero
		move $t9, $zero
		move $s4, $t0
		jal counting
		move $s4, $t1
		jal counting
		move $s4, $t2
		jal counting
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	counting:
		beq $s4, 'X', addX
		beq $s4, 'O', addO
		j addFree

	addX:
		addi $t7, $t7, 1
		j back

	addO:
		addi $t8, $t8, 1
		j back

	addFree:
		addi $t9, $t9, 1
		j back

	pktX:
		li $t3, 1
		j back

	pktO:
		li $t3, 2
		j back

	setTie:
		li $t3, 3
		j back

	checkTie_link:
		beq $t0, 9, setTie
		beq $t1, 9, back
		lb $t2, 0($t5)
		addi $t5, $t5, 1
		addi $t1, $t1, 1
		beq $t2, 'X', addcount
		beq $t2, 'O', addcount
		j checkTie_link

	addcount:
		addi $t0, $t0, 1
		j checkTie_link
    	# Gra
    	humanTurn_link:
    		beq $t3, 1, pointX
    		beq $t3, 2, punktO
    		beq $t3, 3, tie
    		
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
    		jal showBoard_link
		
		bgezal $t4, TG_humanTurn
    		bltzal $t4, TG_computerTurn
    		mul $t4, $t4, -1
    		
    		jal checkBoardState_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		j humanTurn_link

	TG_humanTurn:
		li $v0, 4
		la $a0, prompt3
		syscall
		la $a0, inputIndicator
		syscall
		li $v0, 5
		syscall
		move $t6, $v0
		subi $t6, $t6, 1
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal selectField
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	TG_computerTurn:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal selectOptimizedField
		jal selectField
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		li $v0, 4
		la $a0, prompt11
		syscall
		li $v0, 11
		lb $a0, newLine
		syscall
		j back

	selectField:
		bltz $t6, invaidMove
		bge $t6, 9, invaidMove
		lb $t7, board($t6)
		beq $t7, 'X', invaidMove
		beq $t7, 'O', invaidMove
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		bgezal  $t4, setX
		bltzal  $t4, setO
		sb $a0, board($t6)
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	invaidMove:
		li $v0, 4
		la $a0, prompt8
		syscall
		li $v0, 11
		lb $a0, newLine
		syscall
		
		j humanTurn_link

	setX:
		lb $a0, X
		j back

	setO:
		lb $a0, O
		j back

	selectOptimizedField:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		subi $t6, $zero, 1
		
		bltzal $t6, tryWin
		bltzal $t6, tryBlock
		bltzal $t6, tryCenter
		bltzal $t6, tryEdge
		bltzal $t6, tryRandom
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	tryWin:
		la $t5, board
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		# Rzad 1
		lb $t0,0($t5)
		lb $t1,1($t5)
		lb $t2,2($t5)
		jal countLines
		jal checkIfCanWin
		# Rzad 2
		lb $t0,3($t5)
		lb $t1,4($t5)
		lb $t2,5($t5)
		jal countLines
		jal checkIfCanWin
		# Rzad 3
		lb $t0,6($t5)
		lb $t1,7($t5)
		lb $t2,8($t5)
		jal countLines
		jal checkIfCanWin
		# Kolumna 1
		lb $t0,0($t5)
		lb $t1,3($t5)
		lb $t2,6($t5)
		jal countLines
		jal checkIfCanWin
		# Kolumna 2
		lb $t0,1($t5)
		lb $t1,4($t5)
		lb $t2,7($t5)
		jal countLines
		jal checkIfCanWin
		# Kolumna 3
		lb $t0,2($t5)
		lb $t1,5($t5)
		lb $t2,8($t5)
		jal countLines
		jal checkIfCanWin
		# Przekatna 1
		lb $t0,0($t5)
		lb $t1,4($t5)
		lb $t2,8($t5)
		jal countLines
		jal checkIfCanWin
		# Przekatna 2
		lb $t0,2($t5)
		lb $t1,4($t5)
		lb $t2,6($t5)
		jal countLines
		jal checkIfCanWin
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	checkIfCanWin:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		move $s4, $zero
		bgezal $t4, checkIfTwoX
		bltzal $t4, checkIdTwoO
		jal checkIdOneFree
		beq $s4, 2, itsHere
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	fieldFound:
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	checkIfTwoX:
		beq $t7, 2, incrementS4
		j back

	checkIdTwoO:
		beq $t8, 2, incrementS4
		j back

	checkIdOneFree:
		beq $t9, 1, incrementS4
		j back

	itsHere:
		move $s4, $t0
		blt $s4, 'A', setT6toS4
		move $s4, $t1
		blt $s4, 'A', setT6toS4
		move $s4, $t2
		j setT6toS4

	incrementS4:
		addi $s4, $s4, 1
		j back

	setT6toS4:
		subi $t6, $s4, '0'
		subi $t6, $t6, 1
		j back

	tryBlock:
		la $t5, board
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		# Rzad 1
		lb $t0,0($t5)
		lb $t1,1($t5)
		lb $t2,2($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Rzad 2
		lb $t0,3($t5)
		lb $t1,4($t5)
		lb $t2,5($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Rzad 3
		lb $t0,6($t5)
		lb $t1,7($t5)
		lb $t2,8($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Kolumna 1
		lb $t0,0($t5)
		lb $t1,3($t5)
		lb $t2,6($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Kolumna 2
		lb $t0,1($t5)
		lb $t1,4($t5)
		lb $t2,7($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Kolumna 3
		lb $t0,2($t5)
		lb $t1,5($t5)
		lb $t2,8($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Przekatna 1
		lb $t0,0($t5)
		lb $t1,4($t5)
		lb $t2,8($t5)
		jal countLines
		jal checkIfYouCanBlock
		# Przekatna 2
		lb $t0,2($t5)
		lb $t1,4($t5)
		lb $t2,6($t5)
		jal countLines
		jal checkIfYouCanBlock
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	checkIfYouCanBlock:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		move $s4, $zero
		bgezal $t4, checkIdTwoO
		bltzal $t4, checkIfTwoX
		jal checkIdOneFree
		beq $s4, 2, itsHere
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j back

	tryCenter:
		li $s5, 4
		lb $s4, board($s5)
		blt $s4, 'A', setT6toS4
		j back

	tryEdge:
		li $s5, 0
		lb $s4, board($s5)
		blt $s4, 'A', setT6toS4
		li $s5, 2
		lb $s4, board($s5)
		blt $s4, 'A', setT6toS4
		li $s5, 6
		lb $s4, board($s5)
		blt $s4, 'A', setT6toS4
		li $s5, 8
		lb $s4, board($s5)
		blt $s4, 'A', setT6toS4
		j back

	tryRandom:
		la $t5, board

	pickRandomLoop:
		lb $s4, 0($t5)
		blt $s4, 'A', setT6toS4
		addi $t5, $t5, 1
		j pickRandomLoop
