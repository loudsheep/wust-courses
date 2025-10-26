
#include <iostream>
#include "Number.h"

int main()
{
	Number n1;
	n1 = 1234;

	Number n2;
	n2 = -8765;

	std::cout << n1.toStr() << std::endl;
	std::cout << n2.toStr() << std::endl;

	Number n3;
	n3 = n1 + n2;
	std::cout << n3.toStr() << std::endl;


	Number n4;
	n4 = n2 - n1;
	std::cout << n4.toStr() << std::endl;

	return 0;
}