#pragma once
#include <vector>
#include "Individual.h"
#include "Evaluator.h"

class GeneticAlgorithm
{
public:
	GeneticAlgorithm(int popSize, double crossProb, double mutProb, int numGroups);

	void init(const std::string& folder, const std::string& instance);
	void run();

	Individual getBestSolution();

	void setMaxIterations(int maxIterations);
	void setMaxExecTime(int maxExecTime);
	void setExportConfig(bool enable, int topN, const std::string& filename);

private:
	int popSize;
	double crossProb;
	double mutProb;

	int maxIterations;
	int maxExecTime;
	bool exportEnabled = false;
	int exportTopN = 0;
	std::string exportFilename;
	std::vector<std::pair<int, double>> fitnessHistory;

	Evaluator evaluator;
	std::vector<Individual> population;
	Individual bestSolution = Individual(0, 2, rng);
	std::mt19937 rng;

	void updateBestSolution();
	Individual& tournamentSelection();

	void saveResultsToJson();
};

