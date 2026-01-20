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
	GeneticAlgorithm ga(1000, 0.5, 0.05, 10);
	ga.init("data/lcvrp/Vrp-Set-A/", "A-n80-k10");

	ga.setMaxIterations(1000000);
	ga.setMaxExecTime(20 * 60);
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
