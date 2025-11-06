
#include <iostream>
#include "Number.h"

int main()
{
	Number n1;
	n1 = 123456789;

	n1 = n1;

	Number n2;
	n2 = 987654321;

	std::cout << n1.toStr() << std::endl;
	std::cout << n2.toStr() << std::endl;

	Number n3;
	n3 = n1 + n2;
	std::cout << n3.toStr() << std::endl;


	Number n4;
	n4 = n2 - n1;
	std::cout << n4.toStr() << std::endl;

	Number n5;
	n5 = n2 * n1 * n1 * n1 * n1 * n1;
	std::cout << n5.toStr() << std::endl;

	Number n6;
	n6 = n5 / n1;
	std::cout << n6.toStr() << std::endl;

	return 0;
}