// lab1.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>

const int FILL_VALUE = 34;

void alloc_table_fill_34(int size) {
	if (size <= 0) {
		std::cout << "Incorrect array size";
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

	delete[] array;
}

bool alloc_table_2_dim(int*** table, int sizeX, int sizeY) {
	if (sizeX <= 0 || sizeY <= 0) return false;

	*table = new int* [sizeX];

	for (int i = 0; i < sizeX; i++)
	{
		(*table)[i] = new int[sizeY];
	}

	return true;
}

bool dealloc_table_2_dim(int*** table, int sizeX, int sizeY) {
	if (sizeX <= 0 || sizeY <= 0) return false;

	for (int i = 0; i < sizeX; i++)
	{
		delete (*table)[i];
	}
	delete* table;
}



int main()
{
	alloc_table_fill_34(10);

	int** table;
	alloc_table_2_dim(&table, 5, 3);

	dealloc_table_2_dim(&table, 5, 3);

	return 0;
}

// Run program: Ctrl + F5 or Debug > Start Without Debugging menu
// Debug program: F5 or Debug > Start Debugging menu

// Tips for Getting Started: 
//   1. Use the Solution Explorer window to add/manage files
//   2. Use the Team Explorer window to connect to source control
//   3. Use the Output window to see build output and other messages
//   4. Use the Error List window to view errors
//   5. Go to Project > Add New Item to create new code files, or Project > Add Existing Item to add existing code files to the project
//   6. In the future, to open this project again, go to File > Open > Project and select the .sln file
