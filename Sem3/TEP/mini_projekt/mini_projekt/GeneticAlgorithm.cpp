#include "GeneticAlgorithm.h"
#include <algorithm>

GeneticAlgorithm::GeneticAlgorithm(int popSize, double crossProb, double mutProb, int iterations, int numGroups)
	: popSize(popSize), crossProb(crossProb), mutProb(mutProb), iterations(iterations)
{
	std::random_device rd;
	this->rng = std::mt19937(rd());
}

void GeneticAlgorithm::init(const std::string& folder, const std::string& instance)
{
	this->evaluator.loadInstance(folder, instance);

	this->population.clear();
	this->population.reserve(this->popSize);

	int genotypeSize = this->evaluator.getGenotypeSize();
	int groups = this->evaluator.getNumGroups();

	for (int i = 0; i < this->popSize; i++)
	{
		Individual ind(genotypeSize, groups, this->rng);
		double fitness = this->evaluator.evaluate(ind);
		ind.setFitness(fitness);
		this->population.push_back(std::move(ind));
	}

	this->updateBestSolution();
}

void GeneticAlgorithm::run()
{
	for (int iter = 0; iter < this->iterations; iter++)
	{
		std::vector<Individual> newPopulation;
		newPopulation.reserve(this->popSize);

		newPopulation.push_back(this->bestSolution);

		while (newPopulation.size() < this->popSize)
		{
			const Individual& parent1 = this->tournamentSelection();
			const Individual& parent2 = this->tournamentSelection();

			// TODO: Crossover implementation, mutation, evaluation
		}
	}
}

Individual GeneticAlgorithm::getBestSolution()
{
	return this->bestSolution;
}

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
