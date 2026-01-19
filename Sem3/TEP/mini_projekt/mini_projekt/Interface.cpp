#include <iostream>
#include "Interface.h"
#include "GeneticAlgorithm.h"

Interface::Interface()
{
}

Interface::~Interface()
{
}

void Interface::start()
{
	// TODO: parse number of groups from instance file
	GeneticAlgorithm ga(100, 0.6, 0.1, 5);
	ga.init("data/lcvrp/Vrp-Set-A/", "A-n32-k5");
	ga.setMaxIterations(5000);
	ga.setMaxExecTime(5 * 60);
	ga.setExportConfig(true, 5, "results.json");

	ga.run();

	Individual best = ga.getBestSolution();

	// Print best solution fitness and genotype to console
	std::cout << "Best solution fitness: " << best.getFitness() << std::endl;

	std::cout << "Best solution genotype: ";
	for (int gene : best.getGenotype())
	{
		std::cout << gene << " ";
	}
}
