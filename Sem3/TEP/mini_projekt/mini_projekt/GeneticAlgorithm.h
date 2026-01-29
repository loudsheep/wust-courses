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
	GeneticAlgorithm(unsigned int popSize, double crossProb, double mutProb, unsigned int numGroups);

	Result<void, Error> run(SmartPointer<ProblemData> data, SmartPointer<ResultSerializer> resultSerializer);

	void setMaxIterations(unsigned int maxIterations);
	void setMaxExecTime(unsigned int maxExecTime);

private:
	unsigned int popSize;
	double crossProb;
	double mutProb;
	unsigned int numGroups;
	unsigned int maxIterations;
	unsigned int maxExecTime;

	SmartPointer<Evaluator> evaluator;
	SmartPointer<ProblemData> problemData;
	SmartPointer<ResultSerializer> serializer;

	std::vector<Individual> currentPopulation;
	std::vector<Individual> nextPopulation;
	Individual bestSolution;

	std::mt19937 rng;

	Result<void, Error> initializeInternal(SmartPointer<ProblemData> data, SmartPointer<ResultSerializer> serializer);
	void initializePopulations(int groups);

	void updateBestSolution();
	Individual& tournamentSelection();
	void mutate(Individual& individual);
	void crossover(Individual& parent1, Individual& parent2, Individual& child1, Individual& child2);

	static const int LOG_FREQUENCY;
};

