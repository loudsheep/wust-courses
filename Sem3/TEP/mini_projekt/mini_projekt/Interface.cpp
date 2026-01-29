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
	CVRPParser parser("data/lcvrp/Vrp-Set-A/A-n65-k9.lcvrp");
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

	SmartPointer<ResultSerializer> serializer(new ResultSerializer());
	serializer->setConfig(5, "results.json", problemData.getValue());

	GeneticAlgorithm ga(5000, 0.5, 0.05, 9);
	ga.setMaxIterations(5000);
	ga.setMaxExecTime(5 * 60);

	auto runResult = ga.run(problemData.getValue(), serializer);
	if (!runResult.isSuccess())
	{
		std::cerr << "Execution Failed:" << std::endl;
		for (auto err : runResult.getErrors())
		{
			std::cerr << " -> " << err->getMessage() << std::endl;
		}
		return;
	}

	std::cout << "Best found fitness: " << serializer->getBestFoundFitness() << std::endl;
	std::cout << "Simulation executed successfully in " << serializer->getExecTimeSeconds() << " seconds." << std::endl;
	std::cout << "Saving results to results.json" << std::endl;

	serializer->save();
}

void Interface::handleLoad(std::istringstream& iss)
{
}

void Interface::handleConfig(std::istringstream& iss)
{
}

void Interface::handleRun()
{
}

void Interface::handleHelp()
{
}
