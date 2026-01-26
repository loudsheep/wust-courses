#pragma once
#include "ProblemData.h"
#include "SmartPointer.h"
#include "Result.h"
#include "ParsingError.h"
#include <string>
#include <vector>

class CVRPParser
{
public:
	CVRPParser(const std::string& folder_name, const std::string& instance);

	Result<SmartPointer<ProblemData>, ParsingError> load();

private:
	std::string folder_name;
	std::string instance;

	std::string trim(const std::string& str);
	bool parseInt(const std::string& str, int& out);
	bool parseDouble(const std::string& str, double& out);

	std::vector<std::vector<double>> calculateDistanceMatrix(const std::vector<Point>& coords);
};

