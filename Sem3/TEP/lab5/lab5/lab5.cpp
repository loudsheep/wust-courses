#include <iostream>
#include "SmartPointer.h"

int main()
{
	int* x = new int;
	*x = 5;

	SmartPointer<int> d(x);

	std::cout << *d;
}