#pragma once
#include <vector>
#include "Individual.h"
#include "Evaluator.h"
#include "SmartPointer.h"
#include "ResultSerializer.h"
#include "Result.h"
#include "Error.h"

class GeneticAlgorithm
{
public:
	GeneticAlgorithm(int popSize, double crossProb, double mutProb, int numGroups);

	Result<void, Error> init(SmartPointer<ProblemData> data, SmartPointer<ResultSerializer> resultSerializer);
	Result<void, Error> run();

	void setMaxIterations(int maxIterations);
	void setMaxExecTime(int maxExecTime);

private:
	int popSize;
	double crossProb;
	double mutProb;
	int numGroups;
	int maxIterations;
	int maxExecTime;

	SmartPointer<Evaluator> evaluator;
	SmartPointer<ProblemData> problemData;
	SmartPointer<ResultSerializer> serializer;

	std::vector<Individual> currentPopulation;
	std::vector<Individual> nextPopulation;
	Individual bestSolution;

	std::mt19937 rng;

	void updateBestSolution();
	Individual& tournamentSelection();
	void mutate(Individual& individual);
	void crossover(Individual& parent1, Individual& parent2, Individual& child1, Individual& child2);

	static const int LOG_FREQUENCY;
};

