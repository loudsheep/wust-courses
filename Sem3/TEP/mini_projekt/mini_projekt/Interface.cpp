#include <iostream>
#include <sstream>
#include <string>
#include "Interface.h"
#include "GeneticAlgorithm.h"
#include "CVRPParser.h"
#include "SmartPointer.h"

Interface::Interface() : currentProblem(nullptr),
popSize(100), crossProb(0.5), mutProb(0.01), numGroups(5),
maxIterations(1000), maxExecTime(60)
{
}

Interface::~Interface()
{
}

void Interface::start()
{
	std::string line;
	bool isRunning = true;

	std::cout << "TEP Miniprojekt" << std::endl;

	while (isRunning)
	{
		std::cout << "> ";

		if (std::getline(std::cin, line))
		{
			if (!line.empty())
			{
				std::istringstream iss(line);
				std::string cmd;
				iss >> cmd;

				if (cmd == "exit")
				{
					isRunning = false;
				}
				else if (cmd == "load")
				{
					this->handleLoad(iss);
				}
				else if (cmd == "config")
				{
					this->handleConfig(iss);
				}
				else if (cmd == "run")
				{
					this->handleRun();
				}
				else
				{
					std::cout << "Unknown command." << std::endl;
				}
			}
		}
		else
		{
			isRunning = false;
		}
	}


	/*CVRPParser parser("data/lcvrp/Vrp-Set-A/A-n65-k9.lcvrp");
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

	serializer->save();*/
}

void Interface::handleLoad(std::istringstream& iss)
{
	std::string filename;

	if (iss >> filename)
	{
		CVRPParser parser(filename);
		auto result = parser.load();

		if (result.isSuccess())
		{
			this->currentProblem = result.getValue();
			std::cout << "Problem '" << filename << "' loaded successfully." << std::endl;
			std::cout << "Dimension: " << this->currentProblem->getDimension() << std::endl;
		}
		else
		{
			std::cerr << "Failed to load problem:" << std::endl;
			for (auto err : result.getErrors())
			{
				std::cerr << " -> " << err->getMessage() << std::endl;
			}
		}
	}
	else
	{
		std::cerr << "Usage: load <folder_path> <instance_name>" << std::endl;
	}
}

void Interface::handleConfig(std::istringstream& iss)
{
	int newPop, newGroups, newIter, newTime;
	double newCross, newMut;

	if (iss >> newPop >> newCross >> newMut >> newGroups >> newIter >> newTime)
	{
		bool valid = true;

		if (newPop < 2) {
			std::cerr << "Error: Population size must be >= 2." << std::endl;
			valid = false;
		}
		if (newCross < 0.0 || newCross > 1.0) {
			std::cerr << "Error: Crossover probability must be [0, 1]." << std::endl;
			valid = false;
		}
		if (newMut < 0.0 || newMut > 1.0) {
			std::cerr << "Error: Mutation probability must be [0, 1]." << std::endl;
			valid = false;
		}
		if (newGroups <= 0) {
			std::cerr << "Error: Number of groups must be positive." << std::endl;
			valid = false;
		}
		if (newIter <= 0) {
			std::cerr << "Error: Iterations must be positive." << std::endl;
			valid = false;
		}
		if (newTime <= 0) {
			std::cerr << "Error: Execution time must be positive." << std::endl;
			valid = false;
		}

		if (valid)
		{
			this->popSize = newPop;
			this->crossProb = newCross;
			this->mutProb = newMut;
			this->numGroups = newGroups;
			this->maxIterations = newIter;
			this->maxExecTime = newTime;
			std::cout << "Configuration updated successfully." << std::endl;
		}
	}
	else
	{
		std::cerr << "Usage: config <pop_size> <cross_prob> <mut_prob> <groups> <iter> <time_sec>" << std::endl;
		std::cerr << "Example: config 100 0.6 0.01 5 1000 60" << std::endl;
	}
}

void Interface::handleRun()
{
	if (this->currentProblem.get() != nullptr)
	{
		std::cout << "Starting Genetic Algorithm..." << std::endl;
		std::cout << "Config: Pop=" << popSize << ", Cross=" << crossProb
			<< ", Mut=" << mutProb << ", Groups=" << numGroups << std::endl;

		SmartPointer<ResultSerializer> serializer(new ResultSerializer());
		serializer->setConfig(5, "results.json", this->currentProblem);

		GeneticAlgorithm ga(this->popSize, this->crossProb, this->mutProb, this->numGroups);
		ga.setMaxIterations(this->maxIterations);
		ga.setMaxExecTime(this->maxExecTime);

		auto runResult = ga.run(this->currentProblem, serializer);

		if (runResult.isSuccess())
		{
			std::cout << "Execution finished successfully." << std::endl;
			std::cout << "Best Fitness Found: " << serializer->getBestFoundFitness() << std::endl;

			serializer->save();
			std::cout << "Results saved to 'results.json'." << std::endl;
		}
		else
		{
			std::cerr << "Algorithm execution failed:" << std::endl;
			for (auto err : runResult.getErrors())
			{
				std::cerr << " -> " << err->getMessage() << std::endl;
			}
		}
	}
	else
	{
		std::cerr << "Error: No problem data loaded. Use 'load' command first." << std::endl;
	}
}
