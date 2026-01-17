#pragma once
#include <vector>
#include "Individual.h"
#include "Evaluator.h"

class GeneticAlgorithm
{
public:
	GeneticAlgorithm(int popSize, double crossProb, double mutProb);

	void run(int maxIterations, Evaluator& evaluator);

	Individual getBestSolution();

private:
	int popSize;
	double crossProb;
	double mutProb;
	std::vector<Individual> population;
};

