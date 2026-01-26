#include "Individual.h"

Individual::Individual(int genotypeSize, int numGroups, std::mt19937& rng)
{
	this->genotype.resize(genotypeSize);
	this->fitness = std::numeric_limits<double>::max();

	std::uniform_int_distribution<int> dist(0, numGroups - 1);
	for (int i = 0; i < genotypeSize; ++i)
	{
		this->genotype[i] = dist(rng);
	}

	/*for (int i = 0; i < genotypeSize; ++i)
	{
		this->genotype[i] = (i * numGroups) / genotypeSize;
	}*/
}

Individual::Individual(const Individual& other)
{
	this->genotype = other.genotype;
	this->fitness = other.fitness;
}

Individual::Individual(const std::vector<int>& genotype)
{
	this->genotype = genotype;
	this->fitness = 0.0;
}

Individual::~Individual()
{
}

double Individual::getFitness()
{
	return this->fitness;
}

void Individual::setFitness(double fitness)
{
	this->fitness = fitness;
}

std::vector<int>& Individual::getRawGenotype()
{
	return this->genotype;
}

std::vector<std::vector<int>> Individual::getPhenotype(const std::vector<int>& permutation, int numGroups)
{
	std::vector<std::vector<int>> phenotype(numGroups);

	for (int i = 0; i < permutation.size(); i++)
	{
		int customerId = permutation[i];
		int groupIdx = this->genotype[i];

		if (groupIdx >= 0 && groupIdx < numGroups)
		{
			phenotype[groupIdx].push_back(customerId);
		}
	}

	return phenotype;
}

bool Individual::operator<(const Individual& other)
{
	return this->fitness < other.fitness;
}

