#pragma once
#include <string>
#include <vector>

class LcVRP
{
public:
	LcVRP();

	bool loadFromFile(const std::string& filepath);

	double evaluate(const std::vector<int>& genotype);

	int getDimension();
	int getNumGroups();

private:

};

