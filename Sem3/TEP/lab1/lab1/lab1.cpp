// lab1.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>

const int FILL_VALUE = 34;

void v_alloc_table_fill_34(int iSize) {
	if (iSize <= 0) {
		std::cout << "Incorrect array size";
		return;
	}

	int* array = new int[iSize];

	// sret all values to 34
	for (int i = 0; i < iSize; i++) {
		array[i] = FILL_VALUE;
	}

	// print array
	for (int i = 0; i < iSize; i++)
	{
		std::cout << array[i] << " ";
	}

	delete array;
}

int main()
{
	v_alloc_table_fill_34(10);
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
