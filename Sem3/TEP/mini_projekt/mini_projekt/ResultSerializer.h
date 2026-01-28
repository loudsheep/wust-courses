#pragma once
#include <string>
#include <vector>
#include <fstream>
#include "ProblemData.h"
#include "SmartPointer.h"
#include "Individual.h"

struct IterationLog
{
	int iteration;
	double bestFitness;
};

class ResultSerializer
{
public:
	ResultSerializer();
	~ResultSerializer();

	void setConfig(int topN, const std::string& filename, SmartPointer<ProblemData> problemData);

	void logIteration(int iter, double currentBestFitness);
	void collectSolution(Individual& solution);

	void save();
	double getBestFoundFitness();

private:
	int topN;
	std::string filename;
	SmartPointer<ProblemData> problemData;

	std::vector<IterationLog> history;
	std::vector<Individual> topSolutions;

	static const std::string JSON_OPEN;
	static const std::string JSON_CLOSE;

	void writeHeader(std::ofstream& json);
	void writeNodes(std::ofstream& json);
	void writeHistory(std::ofstream& json);
	void writeSolutions(std::ofstream& json);

	void writeSingleSolutionRoutes(std::ofstream& json, Individual& sol);
};

