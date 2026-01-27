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

Individual::Individual(Individual&& other) noexcept
{
	this->genotype = std::move(other.genotype);
	this->fitness = other.fitness;
}

Individual::~Individual()
{
}

Individual& Individual::operator=(Individual&& other) noexcept
{
	if (this != &other)
	{
		this->genotype = std::move(other.genotype);
		this->fitness = other.fitness;
	}
	return *this;
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

void Individual::getPhenotype(const std::vector<int>& permutation, std::vector<std::vector<int>>& outPhenotype)
{
	for (auto& route : outPhenotype) {
		route.clear();
	}

	if (outPhenotype.empty()) return;

	for (int i = 0; i < permutation.size(); i++)
	{
		int customerId = permutation[i];
		int groupIdx = this->genotype[i];

		if (groupIdx >= 0 && groupIdx < outPhenotype.size())
		{
			outPhenotype[groupIdx].push_back(customerId);
		}
	}
}

bool Individual::operator<(const Individual& other)
{
	return this->fitness < other.fitness;
}

