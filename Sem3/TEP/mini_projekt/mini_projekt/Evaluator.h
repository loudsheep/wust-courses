#pragma once
#include "ProblemData.h"
#include "Individual.h"

class Evaluator
{
public:
	Evaluator(int numGroups);

	void loadInstance(const std::string& folder, const std::string& instance);

	double evaluate(Individual& individual);

	int getGenotypeSize();
	int getNumGroups();

	ProblemData& getProblemData();

private:
	ProblemData problemData;
	int numGroups;

	double calculateDistance(int from, int to);
};

