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

	// TODO: parse number of groups from instance file
	GeneticAlgorithm ga(1000, 0.5, 0.05, 5);
	ga.init(problemData.getValue(), serializer);

	ga.setMaxIterations(1000);
	ga.setMaxExecTime(5 * 60);

	ga.run();

	serializer->save();
}
