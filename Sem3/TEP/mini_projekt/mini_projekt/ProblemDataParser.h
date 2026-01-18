#pragma once
#include <string>
#include <vector>
#include "ProblemData.h"


class ProblemDataParser
{
public:
	ProblemDataParser(std::string& folder_name, std::string& instance);

	ProblemData load();

private:
	std::string folder_name;
	std::string instance;

	void parseFile(const std::string& filepath, ProblemData& problemData);

	std::string trim(const std::string& str);
};

