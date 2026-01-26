#include <iostream>
#include "Interface.h"
#include "GeneticAlgorithm.h"
#include "CVRPParser.h"
#include "SmartPointer.h"

Interface::Interface()
{
}

Interface::~Interface()
{
}

void Interface::start()
{
	CVRPParser parser("data/lcvrp/Vrp-Set-A", "A-n32-k5");
	auto problemData = parser.load();

	if (!problemData.isSuccess())
	{
		std::cerr << "Error loading problem data: ";
		for (auto err : problemData.getErrors())
		{
			std::cerr << err->getMessage() << " ";
		}
		std::cerr << std::endl;
		return;
	}

	// TODO: parse number of groups from instance file
	GeneticAlgorithm ga(1000, 0.5, 0.05, 5);
	ga.init(problemData.getValue());

	ga.setMaxIterations(1000);
	ga.setMaxExecTime(5 * 60);
	ga.setExportConfig(true, 5, "results.json");

	ga.run();

	Individual best = ga.getBestSolution();

	// Print best solution fitness and genotype to console
	std::cout << "Best solution fitness: " << best.getFitness() << std::endl;

	std::cout << "Best solution genotype: ";
	for (int gene : best.getRawGenotype())
	{
		std::cout << gene << " ";
	}
}
