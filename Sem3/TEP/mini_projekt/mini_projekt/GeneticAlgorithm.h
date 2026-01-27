#pragma once
#include <vector>
#include "Individual.h"
#include "Evaluator.h"
#include "SmartPointer.h"

class GeneticAlgorithm
{
public:
	GeneticAlgorithm(int popSize, double crossProb, double mutProb, int numGroups);

	void init(SmartPointer<ProblemData> problemData);
	void run();

	Individual getBestSolution();

	void setMaxIterations(int maxIterations);
	void setMaxExecTime(int maxExecTime);
	void setExportConfig(bool enable, int topN, const std::string& filename);

private:
	int popSize;
	double crossProb;
	double mutProb;
	int numGroups;
	int maxIterations;
	int maxExecTime;

	bool exportEnabled = false;
	int exportTopN = 0;
	std::string exportFilename;
	std::vector<std::pair<int, double>> fitnessHistory;

	SmartPointer<Evaluator> evaluator;
	SmartPointer<ProblemData> problemData;

	std::vector<Individual> currentPopulation;
	std::vector<Individual> nextPopulation;
	Individual bestSolution = Individual(0, 2, rng);
	std::mt19937 rng;

	void updateBestSolution();
	Individual& tournamentSelection();
	void saveResultsToJson();

	void mutate(Individual& individual);
	std::pair<Individual, Individual> crossover(Individual& parent1, Individual& parent2);
};

