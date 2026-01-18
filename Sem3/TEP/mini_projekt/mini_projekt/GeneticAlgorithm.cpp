#include "GeneticAlgorithm.h"
#include <algorithm>

void GeneticAlgorithm::updateBestSolution()
{
	if (this->population.empty()) return;

	auto it = std::min_element(this->population.begin(), this->population.end());

	if (it == this->population.end()) return;

	if (bestSolution.getFitness() == 0.0 || it->getFitness() < bestSolution.getFitness())
	{
		bestSolution = *it;
	}
}

Individual& GeneticAlgorithm::tournamentSelection()
{
	std::uniform_int_distribution<int> dist(0, popSize - 1);
	int idx1 = dist(rng);
	int idx2 = dist(rng);

	if (population[idx1].getFitness() < population[idx2].getFitness())
	{
		return population[idx1];
	}
	else
	{
		return population[idx2];
	}
}
