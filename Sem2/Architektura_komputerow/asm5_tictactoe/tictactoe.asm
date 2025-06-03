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
	blednyInput: .asciiz "Wprowadzono bledne dane\n"
	prompt3: .asciiz "Twoj ruch! Postaw X w wybranym miejscu wpisujac numer dostepnego pola"
	round: .asciiz "RUNDA NR "
	prompt4: .asciiz "Runde wygrywa "
	prompt5: .asciiz "Aktualny wynik"
	prompt6: .asciiz "KONIEC GRY! ZWYCIEZCA PARTII ZOSTAJE "
	prompt7: .asciiz "Remis! Obaj gracze otrzymuja punkt"
	prompt8: .asciiz "Bledny ruch! Wybierz inne pole"
	prompt10: .asciiz "KONIEC GRY! PARTIA ZAKONCZONA REMISEM"
	prompt11: .asciiz "Komputer wykonal swoj ruch!"
	
	# Inne
	plansza: .space 9
.text
	# Ogolne
	main:
		move $s1, $zero
		move $s2, $zero
		move $s3, $zero
		
		j liczbaRund
		
	wroc:
    		jr $ra
    		
    	exit:
    		li $v0, 10
    		syscall
    		
	blednyInput_link:
    		li $v0, 4
    		la $a0, blednyInput
    		syscall
    		j wroc
    		
    	# Plansza
    	wyswietlPlansze_link:
    		move $t0, $zero
    		
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal petlaWyswietlPlansze_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		
    		j wroc
    	petlaWyswietlPlansze_link:
    		move $t1, $zero
    		
    		li $v0, 11
    		lb $a0, space
    		syscall
    		
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal petlaWyswietlPlanszeRzad_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		
    		addi $t0, $t0, 1
    		
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		
    		beq $t0, 3, wroc
    		
    		li $v0, 4
    		la $a0, floor
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		j petlaWyswietlPlansze_link
    	petlaWyswietlPlanszeRzad_link:
    		mul $t2, $t0, 3
    		add $t2, $t2, $t1
    		li $v0, 11
    		lb $a0, plansza($t2)
    		syscall
    		
    		addi $t1, $t1, 1
    		beq $t1, 3, wroc
    		lb $a0, space
    		syscall
    		lb $a0, wall
    		syscall
    		lb $a0, space
    		syscall
    		j petlaWyswietlPlanszeRzad_link
	resetPlansza_link:
		addi $t0, $zero, 1
		la $t1, plansza
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal petlaResetPlansza_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		
		j wroc
		
	petlaResetPlansza_link:
		bgt $t0, 10, wroc
		addi $t2, $t0, 48
		sb $t2, 0($t1)
		addi $t1, $t1, 1
		addi $t0, $t0, 1
		j petlaResetPlansza_link
	
	
	# Organizacja gry
	liczbaRund:
		li $v0, 4
    		la $a0, prompt1
    		syscall
    		la $a0, inputIndicator
    		syscall
    		li $v0, 5
    		syscall
    		move $s0, $v0
    		bgt $s0, 0, mainGameLoop
    		jal blednyInput_link
    		j liczbaRund
    		
	mainGameLoop:
    		bge $s3, $s0, wyniki
    		addi $s3, $s3, 1
    		
    		jal resetPlansza_link
    		
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
    		jal rundaGracz_link
    		
    		j mainGameLoop
    	punktX:
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal wyswietlPlansze_link
    		addi $s1, $s1, 1
    		
    		li $v0, 4
    		la $a0, prompt4
    		syscall
    		li $v0, 11
    		lb $a0, X
    		syscall
    		lb $a0, newLine
    		syscall
    		
		jal aktualnyWynik_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		
    		j wroc
    	punktO:
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal wyswietlPlansze_link
    		addi $s2, $s2, 1
    		
    		li $v0, 4
    		la $a0, prompt4
    		syscall
    		li $v0, 11
    		lb $a0, O
    		syscall
    		lb $a0, newLine
    		syscall
    		
		jal aktualnyWynik_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		
    		j wroc
    	remis:
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal wyswietlPlansze_link
    		#addi $s1, $s1, 1
    		#addi $s2, $s2, 1
    		
    		li $v0, 4
    		la $a0, prompt7
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		
		jal aktualnyWynik_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		
    		j wroc
    		
    	aktualnyWynik_link:
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
    		j wroc
    	wyniki:
    		sub $s3, $s1, $s2
    		beqz $s3, wynikRemis
    		li $v0, 4
    		la $a0, prompt6
    		syscall
    		li $v0, 11
    		bgezal $s3, ustawX
    		bltzal $s3, ustawO
    		syscall
    		lb $a0, newLine
    		syscall
    		j exit
    	wynikRemis:
    		li $v0, 4
    		la $a0, prompt10
    		syscall
    		li $v0, 11
    		lb $a0, newLine
    		syscall
    		j exit
	sprawdzStanPlanszy_link:
		la $t5, plansza
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		# Rzad 1
		lb $t0,0($t5)
		lb $t1,1($t5)
		lb $t2,2($t5)
		jal sprawdzLinie_link
		# Rzad 2
		lb $t0,3($t5)
		lb $t1,4($t5)
		lb $t2,5($t5)
		jal sprawdzLinie_link
		# Rzad 3
		lb $t0,6($t5)
		lb $t1,7($t5)
		lb $t2,8($t5)
		jal sprawdzLinie_link
		# Kolumna 1
		lb $t0,0($t5)
		lb $t1,3($t5)
		lb $t2,6($t5)
		jal sprawdzLinie_link
		# Kolumna 2
		lb $t0,1($t5)
		lb $t1,4($t5)
		lb $t2,7($t5)
		jal sprawdzLinie_link
		# Kolumna 3
		lb $t0,2($t5)
		lb $t1,5($t5)
		lb $t2,8($t5)
		jal sprawdzLinie_link
		# Przekatna 1
		lb $t0,0($t5)
		lb $t1,4($t5)
		lb $t2,8($t5)
		jal sprawdzLinie_link
		# Przekatna 2
		lb $t0,2($t5)
		lb $t1,4($t5)
		lb $t2,6($t5)
		jal sprawdzLinie_link
		# Remis
		move $t0, $zero
		move $t1, $zero
		jal sprawdzRemis_link
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	sprawdzLinie_link:
		beq $t0, $t1, sprawdzDrugie
		j wroc
	sprawdzDrugie:
		beq $t0, $t2, sprawdzZnak
		j wroc
	sprawdzZnak:
		beq $t0, 'X', pktX
		beq $t0, 'O', pktO
		j wroc
	podliczLinie:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		move $t7, $zero
		move $t8, $zero
		move $t9, $zero
		move $s4, $t0
		jal zliczaj
		move $s4, $t1
		jal zliczaj
		move $s4, $t2
		jal zliczaj
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	zliczaj:
		beq $s4, 'X', dodajX
		beq $s4, 'O', dodajO
		j dodajWolne
	dodajX:
		addi $t7, $t7, 1
		j wroc
	dodajO:
		addi $t8, $t8, 1
		j wroc
	dodajWolne:
		addi $t9, $t9, 1
		j wroc
	pktX:
		li $t3, 1
		j wroc
	pktO:
		li $t3, 2
		j wroc
	ustawRemis:
		li $t3, 3
		j wroc
	sprawdzRemis_link:
		beq $t0, 9, ustawRemis
		beq $t1, 9, wroc
		lb $t2, 0($t5)
		addi $t5, $t5, 1
		addi $t1, $t1, 1
		beq $t2, 'X', dolicz
		beq $t2, 'O', dolicz
		j sprawdzRemis_link
	dolicz:
		addi $t0, $t0, 1
		j sprawdzRemis_link
    	# Gra
    	rundaGracz_link:
    		beq $t3, 1, punktX
    		beq $t3, 2, punktO
    		beq $t3, 3, remis
    		
    		subi $sp, $sp, 4
		sw $ra, 0($sp)
    		jal wyswietlPlansze_link
		
		bgezal $t4, TG_ruchGracza
    		bltzal $t4, TG_ruchKomputera
    		mul $t4, $t4, -1
    		
    		jal sprawdzStanPlanszy_link
		lw $ra, 0($sp)
		addi $sp, $sp, 4
    		j rundaGracz_link
	TG_ruchGracza:
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
		jal zaznaczPole
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	TG_ruchKomputera:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		jal wyznaczOptymalnePole
		jal zaznaczPole
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		li $v0, 4
		la $a0, prompt11
		syscall
		li $v0, 11
		lb $a0, newLine
		syscall
		j wroc
	zaznaczPole:
		bltz $t6, blednyRuch
		bge $t6, 9, blednyRuch
		lb $t7, plansza($t6)
		beq $t7, 'X', blednyRuch
		beq $t7, 'O', blednyRuch
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		bgezal  $t4, ustawX
		bltzal  $t4, ustawO
		sb $a0, plansza($t6)
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	blednyRuch:
		li $v0, 4
		la $a0, prompt8
		syscall
		li $v0, 11
		lb $a0, newLine
		syscall
		
		j rundaGracz_link
	ustawX:
		lb $a0, X
		j wroc
	ustawO:
		lb $a0, O
		j wroc
	wyznaczOptymalnePole:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		subi $t6, $zero, 1
		
		bltzal $t6, sprobujWygrac
		bltzal $t6, sprobujZablokowac
		bltzal $t6, sprobujZajacSrodek
		bltzal $t6, sprobujZajacRogi
		bltzal $t6, zajmijLosoweMiejsce
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	sprobujWygrac:
		la $t5, plansza
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		# Rzad 1
		lb $t0,0($t5)
		lb $t1,1($t5)
		lb $t2,2($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Rzad 2
		lb $t0,3($t5)
		lb $t1,4($t5)
		lb $t2,5($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Rzad 3
		lb $t0,6($t5)
		lb $t1,7($t5)
		lb $t2,8($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Kolumna 1
		lb $t0,0($t5)
		lb $t1,3($t5)
		lb $t2,6($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Kolumna 2
		lb $t0,1($t5)
		lb $t1,4($t5)
		lb $t2,7($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Kolumna 3
		lb $t0,2($t5)
		lb $t1,5($t5)
		lb $t2,8($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Przekatna 1
		lb $t0,0($t5)
		lb $t1,4($t5)
		lb $t2,8($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		# Przekatna 2
		lb $t0,2($t5)
		lb $t1,4($t5)
		lb $t2,6($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszWygrac
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	sprawdzCzyMozeszWygrac:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		move $s4, $zero
		bgezal $t4, sprawdzCzyDwaX
		bltzal $t4, sprawdzCzyDwaO
		jal sprawdzCzyJednoWolne
		beq $s4, 2, toTutaj
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	znalezionoPole:
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	sprawdzCzyDwaX:
		beq $t7, 2, incrementS4
		j wroc
	sprawdzCzyDwaO:
		beq $t8, 2, incrementS4
		j wroc
	sprawdzCzyJednoWolne:
		beq $t9, 1, incrementS4
		j wroc
	toTutaj:
		move $s4, $t0
		blt $s4, 'A', setT6toS4
		move $s4, $t1
		blt $s4, 'A', setT6toS4
		move $s4, $t2
		j setT6toS4
	incrementS4:
		addi $s4, $s4, 1
		j wroc
	setT6toS4:
		subi $t6, $s4, '0'
		subi $t6, $t6, 1
		j wroc
	sprobujZablokowac:
		la $t5, plansza
		
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		
		# Rzad 1
		lb $t0,0($t5)
		lb $t1,1($t5)
		lb $t2,2($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Rzad 2
		lb $t0,3($t5)
		lb $t1,4($t5)
		lb $t2,5($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Rzad 3
		lb $t0,6($t5)
		lb $t1,7($t5)
		lb $t2,8($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Kolumna 1
		lb $t0,0($t5)
		lb $t1,3($t5)
		lb $t2,6($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Kolumna 2
		lb $t0,1($t5)
		lb $t1,4($t5)
		lb $t2,7($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Kolumna 3
		lb $t0,2($t5)
		lb $t1,5($t5)
		lb $t2,8($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Przekatna 1
		lb $t0,0($t5)
		lb $t1,4($t5)
		lb $t2,8($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		# Przekatna 2
		lb $t0,2($t5)
		lb $t1,4($t5)
		lb $t2,6($t5)
		jal podliczLinie
		jal sprawdzCzyMozeszZablokowac
		
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	sprawdzCzyMozeszZablokowac:
		subi $sp, $sp, 4
		sw $ra, 0($sp)
		move $s4, $zero
		bgezal $t4, sprawdzCzyDwaO
		bltzal $t4, sprawdzCzyDwaX
		jal sprawdzCzyJednoWolne
		beq $s4, 2, toTutaj
		lw $ra, 0($sp)
		addi $sp, $sp, 4
		j wroc
	sprobujZajacSrodek:
		li $s5, 4
		lb $s4, plansza($s5)
		blt $s4, 'A', setT6toS4
		j wroc
	sprobujZajacRogi:
		li $s5, 0
		lb $s4, plansza($s5)
		blt $s4, 'A', setT6toS4
		li $s5, 2
		lb $s4, plansza($s5)
		blt $s4, 'A', setT6toS4
		li $s5, 6
		lb $s4, plansza($s5)
		blt $s4, 'A', setT6toS4
		li $s5, 8
		lb $s4, plansza($s5)
		blt $s4, 'A', setT6toS4
		j wroc
	zajmijLosoweMiejsce:
		la $t5, plansza
	petlaZajmijLosoweMiejsce:
		lb $s4, 0($t5)
		blt $s4, 'A', setT6toS4
		addi $t5, $t5, 1
		j petlaZajmijLosoweMiejsce
