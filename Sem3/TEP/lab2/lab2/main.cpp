
#include <iostream>
#include "Number.h"

int main()
{
	Number n1;
	n1 = 1234;

	Number n2;
	n2 = 8765;

	std::cout << n1.toStr() << std::endl;
	std::cout << n2.toStr() << std::endl;

	n1 = n2;
	n2 = 1;

	std::cout << n1.toStr() << std::endl;
	std::cout << n2.toStr() << std::endl;

	return 0;
}