#pragma once
#include "ProblemData.h"
#include "SmartPointer.h"
#include <string>

class CVRPParser
{
public:
	CVRPParser(const std::string& folder_name, const std::string& instance);

	SmartPointer<ProblemData> load();
private:
	std::string folder_name;
	std::string instance;

	std::string trim(const std::string& str);
	std::vector<std::vector<double>> calculateDistanceMatrix(const std::vector<Point>& coords);
};

