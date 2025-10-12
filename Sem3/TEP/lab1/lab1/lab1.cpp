#include "lab1.h"
#include <iostream>

void allocTableFill34(int size)
{
	if (size <= 0) {
		std::cout << "Incorrect array size" << std::endl;
		return;
	}

	int* array = new int[size];

	// sret all values to 34
	for (int i = 0; i < size; i++) {
		array[i] = FILL_VALUE;
	}

	// print array
	for (int i = 0; i < size; i++)
	{
		std::cout << array[i] << " ";
	}
	std::cout << std::endl;

	delete[] array;
}

bool allocTable2Dim(int*** table, int sizeX, int sizeY)
{
	if (sizeX <= 0 || sizeY <= 0) return false;

	*table = new int* [sizeX];

	for (int i = 0; i < sizeX; i++)
	{
		(*table)[i] = new int[sizeY];
	}

	return true;
}

bool deallocTable2Dim(int*** table, int sizeX, int sizeY)
{
	if (sizeX <= 0 || sizeY <= 0) return false;

	for (int i = 0; i < sizeX; i++)
	{
		delete[](*table)[i];
	}
	delete[] * table;
}

void modTab(Table* table, int newSize)
{
	table->setNewSize(newSize);
}

void modTab(Table table, int newSize)
{
	table.setNewSize(newSize);
}
