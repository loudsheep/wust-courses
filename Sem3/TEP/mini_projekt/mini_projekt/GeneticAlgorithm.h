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

private:
	int popSize;
	double crossProb;
	double mutProb;

	int maxIterations;
	int maxExecTime;

	Evaluator evaluator;
	std::vector<Individual> population;
	Individual bestSolution = Individual(0, 2, rng); // Placeholder initialization
	std::mt19937 rng;

	void updateBestSolution();
	Individual& tournamentSelection();
};

