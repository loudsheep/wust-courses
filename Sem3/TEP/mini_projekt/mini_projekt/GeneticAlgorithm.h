#pragma once
#include <vector>
#include "Individual.h"
#include "Evaluator.h"

class GeneticAlgorithm
{
public:
	GeneticAlgorithm(int popSize, double crossProb, double mutProb, int iterations, int numGroups);

	void init(const std::string& folder, const std::string& instance);
	void run();

	Individual getBestSolution();

private:
	int popSize;
	double crossProb;
	double mutProb;
	int iterations;

	Evaluator evaluator;
	std::vector<Individual> population;
	Individual bestSolution;
	std::mt19937 rng;

	void updateBestSolution();
	Individual& tournamentSelection();
};

