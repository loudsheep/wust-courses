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

	SmartPointer<ResultSerializer> serializer(new ResultSerializer());
	serializer->setConfig(5, "results.json", problemData.getValue());

	GeneticAlgorithm ga(1000, 0.5, 0.05, 5);
	auto initResult = ga.init(problemData.getValue(), serializer);

	if (!initResult.isSuccess())
	{
		std::cerr << "Initialization Failed:" << std::endl;
		for (auto err : initResult.getErrors())
		{
			std::cerr << " -> " << err->getMessage() << std::endl;
		}
		return;
	}

	ga.setMaxIterations(1000);
	ga.setMaxExecTime(5 * 60);

	ga.run();

	serializer->save();
}
