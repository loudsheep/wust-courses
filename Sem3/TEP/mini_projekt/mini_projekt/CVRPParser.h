#pragma once
#include <string>
#include "SmartPointer.h"
#include "ProblemData.h"

class CVRPParser
{
public:
	CVRPParser(const std::string& folder_name, const std::string& instance);

	SmartPointer<ProblemData> load();

private:
	std::string folder_name;
	std::string instance;

	std::string trim(const std::string& str);
};

