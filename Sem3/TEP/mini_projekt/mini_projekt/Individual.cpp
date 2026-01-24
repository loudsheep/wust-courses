#include "Individual.h"

Individual::Individual(int genotypeSize, int numGroups, std::mt19937& rng)
{
	this->genotype.resize(genotypeSize);
	this->fitness = std::numeric_limits<double>::max();

	for (int i = 0; i < genotypeSize; ++i)
	{
		this->genotype[i] = (i * numGroups) / genotypeSize;
	}
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

void Individual::mutate(double mutProb, int numGroups, std::mt19937& rng)
{
	std::uniform_real_distribution<double> probDist(0.0, 1.0);
	std::uniform_int_distribution<int> groupDist(0, numGroups - 1);

	for (int& gene : this->genotype)
	{
		if (probDist(rng) < mutProb)
		{
			gene = groupDist(rng);
			// uniweaznic fitness??
		}
	}
}

bool Individual::operator<(const Individual& other)
{
	return this->fitness < other.fitness;
}

std::pair<Individual, Individual> Individual::crossover(const Individual& parent1, const Individual& parent2, double crossProb, std::mt19937& rng)
{
	std::uniform_real_distribution<double> probDist(0.0, 1.0);

	if (probDist(rng) >= crossProb)
	{
		return { Individual(parent1), Individual(parent2) };
	}

	std::uniform_int_distribution<int> cutDist(1, parent1.genotype.size() - 1);
	int cutPoint = cutDist(rng);

	std::vector<int> child1Genotype = parent1.genotype;
	std::vector<int> child2Genotype = parent2.genotype;

	for (size_t i = cutPoint; i < parent1.genotype.size(); ++i)
	{
		child1Genotype[i] = parent2.genotype[i];
		child2Genotype[i] = parent1.genotype[i];
	}

	return { Individual(child1Genotype), Individual(child2Genotype) };
}

