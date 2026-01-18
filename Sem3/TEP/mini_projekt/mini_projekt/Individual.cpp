#include "Individual.h"

Individual::Individual(int genotypeSize, int numGroups, std::mt19937& rng)
{
	std::uniform_int_distribution<int> dist(0, numGroups - 1);
	this->genotype.resize(genotypeSize);
	for (int i = 0; i < genotypeSize; ++i)
	{
		this->genotype[i] = dist(rng);
	}
	this->fitness = 0.0;
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

std::vector<int>& Individual::getGenotype()
{
	return this->genotype;
}

bool Individual::operator<(const Individual& other)
{
	return this->fitness < other.fitness;
}

