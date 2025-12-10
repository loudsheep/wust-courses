#include <iostream>
#include "Interface.h"
#include "Result.h";
#include "Error.h"

int main()
{
	Interface interfcae;
	interfcae.run();

	/*Error* e = new Error("Jakiœ b³¹d");
	{
		Result<int, Error> res = Result<int, Error>::fail(e);
		std::cout << e->getMessage() << std::endl;
	}
	std::cout << e->getMessage() << std::endl;*/

}