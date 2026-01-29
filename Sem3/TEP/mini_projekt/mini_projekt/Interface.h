#pragma once
#include <sstream>
#include "SmartPointer.h"
#include "ProblemData.h"

class Interface
{
public:
	Interface();
	~Interface();

	void start();

private:
	SmartPointer<ProblemData> currentProblem;

	int popSize;
	double crossProb;
	double mutProb;
	int numGroups;
	int maxIterations;
	int maxExecTime;

	void handleLoad(std::istringstream& iss);
	void handleConfig(std::istringstream& iss);
	void handleRun();
};

