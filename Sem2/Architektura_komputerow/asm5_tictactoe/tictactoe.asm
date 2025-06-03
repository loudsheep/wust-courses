.data
board:		.byte		'.','.','.','.','.','.','.','.','.'
corners:	.word		0, 2, 6, 8
edges:		.word		1, 3, 5, 7
lines:		.word 		0,1,2, 3,4,5, 6,7,8, 0,3,6, 1,4,7, 2,5,8, 0,4,8, 2,4,6 	# Wining positions

# Messages
prompt_rounds:		.asciiz		"Liczba rund(1-5): "
invalid_rounds:		.asciiz		"Nie prawidlowa ilosc, wpisz od 1 do 5\n"
round_msg:		.asciiz		"Runda "
newline: 		.asciiz 	"\n"
bar_str: 		.asciiz 	" | "
prompt_move: 		.asciiz 	"Podaj ruch (1-9): "
invalid_move: 		.asciiz 	"Nie prawidłowy ruch. Wpisz jeszcze raz\n"
human_win_msg: 		.asciiz 	"Wygrywa czlowiek!\n"
computer_win_msg: 	.asciiz 	"Komputer wygrywa!\n"
tie_msg: 		.asciiz 	"Remis!\n"
results_msg: 		.asciiz 	"Wynik gry:\n"
human_wins_msg: 	.asciiz 	"Wygrane czlowieka: "
computer_wins_msg: 	.asciiz 	"Wygrane komputera: "
ties_msg: 		.asciiz 	"Remisy: "
numbering_msg: 		.asciiz 	"Pozycje w grze:\n1|2|3\n4|5|6\n7|8|9\n"
current_board: 		.asciiz 	"Aktualna pozycja:\n"

# Score
human_score:		.word		0
computer_score:		.word		0
tie_score:		.word		0

# Game state
current_player:		.byte		'X'
round_count:		.word		0



.text
.globl main


main:
	la $a0, numbering_msg
	li $v0, 4
	syscall
	
get_rounds:
	la $a0, prompt_rounds
	li $v0, 4
	syscall
	
	li $v0, 5
	syscall
	move $s0, $v0		# $s0 - liczba rund
	
	blt $s0, 1, invalid_round
	bgt $s0, 5, invalid_round
	j init_rounds
	
invalid_round:
	la $a0, invalid_rounds
	li $v0, 4
	syscall
	
	j get_rounds
	
init_rounds:
	li $v0, 10
	syscall
