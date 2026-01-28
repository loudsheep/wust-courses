#pragma once
#include "ProblemData.h"
#include "Individual.h"
#include "SmartPointer.h"

class Evaluator
{
public:
	Evaluator(SmartPointer<ProblemData> problemData, int numGroups);

	double evaluate(Individual& individual);

	int getGenotypeSize();
	int getNumGroups();

private:
	SmartPointer<ProblemData> problemData;
	int numGroups;
	std::vector<std::vector<int>> routeCache;

	double getDistance(int from, int to);
};

