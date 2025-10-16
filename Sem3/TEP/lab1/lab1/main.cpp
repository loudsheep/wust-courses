#include <iostream>
#include "Table.h"
#include "lab1.h"

const int TABLE_SIZE_X = 5;
const int TABLE_SIZE_Y = 3;

int main() {
	// Zadanie 1
	allocTableFill34(-4);
	allocTableFill34(0);
	allocTableFill34(4);

	// Zadanie 2
	int** table;

	std::cout << "Alloc -3 x -2: " << allocTable2Dim(&table, -3, -2) << std::endl;
	std::cout << "Alloc 3 x 0: " << allocTable2Dim(&table, 3, 0) << std::endl;
	std::cout << "Alloc 0 x 2: " << allocTable2Dim(&table, 0, 2) << std::endl;
	std::cout << "Alloc TABLE_SIZE: " << allocTable2Dim(&table, TABLE_SIZE_X, TABLE_SIZE_Y) << std::endl;

	// Zadanie 3
	deallocTable2Dim(&table, TABLE_SIZE_X, TABLE_SIZE_Y);
	deallocTable2Dim(&table, TABLE_SIZE_X, TABLE_SIZE_Y);

	// Zadanie 4
	Table t;
	t.setName("nowe");
	t.setNewSize(3);

	t.set(0, 1);
	t.set(1, 2);
	t.set(2, 3);

	t.printTable();

	Table other("other", 4);
	other.set(0, 4);
	other.set(1, 5);
	other.set(2, 6);
	other.set(3, 7);
	other.printTable();

	std::cout << "NOWE TAB:" << std::endl;
	t.insertHere(other, 2);

	t.printTable();
	other.printTable();

	modTab(&t, 10);

	modTab(t, 20);

	return 0;
}