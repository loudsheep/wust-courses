
#include <iostream>
#include "ProblemDataParser.h"

int main()
{
	std::string folder = "data/lcvrp/Vrp-Set-A/";
	std::string instance = "A-n32-k5";

	ProblemDataParser parser(folder, instance);


	ProblemData res = parser.load();

	auto perm = res.getCoords();
	for (int i = 0; i < perm.size(); i++)
	{
		std::cout << perm[i].x << " " << perm[i].y << std::endl;
	}
}
