#include "GeneticAlgorithm.h"
#include <algorithm>
#include <fstream>
#include <iomanip>
#include <set>
#include <chrono>
#include <iostream>

const int GeneticAlgorithm::LOG_FREQUENCY = 100;

GeneticAlgorithm::GeneticAlgorithm(int popSize, double crossProb, double mutProb, int numGroups)
	: popSize(popSize), crossProb(crossProb), mutProb(mutProb), evaluator(nullptr),
	maxIterations(1000), maxExecTime(60), numGroups(numGroups), problemData(nullptr),
	serializer(nullptr)
{
	std::random_device rd;
	this->rng = std::mt19937(rd());
}

void GeneticAlgorithm::init(SmartPointer<ProblemData> probmelData, SmartPointer<ResultSerializer> serializer)
{
	this->problemData = SmartPointer<ProblemData>(probmelData);
	this->serializer = serializer;
	this->evaluator = SmartPointer<Evaluator>(new Evaluator(probmelData, this->numGroups));

	this->currentPopulation.clear();
	this->currentPopulation.reserve(this->popSize);
	this->nextPopulation.clear();
	this->nextPopulation.reserve(this->popSize);

	int genotypeSize = this->evaluator->getGenotypeSize();
	for (int i = 0; i < this->popSize; i++) {
		this->nextPopulation.emplace_back(genotypeSize, this->numGroups, this->rng);
	}

	int groups = this->numGroups;

	for (int i = 0; i < this->popSize; i++)
	{
		Individual ind(genotypeSize, groups, this->rng);
		double fitness = this->evaluator->evaluate(ind);
		ind.setFitness(fitness);
		this->currentPopulation.push_back(std::move(ind));
	}

	this->updateBestSolution();
}

void GeneticAlgorithm::run()
{
	auto startTime = std::chrono::high_resolution_clock::now();

	for (int iter = 0; iter < this->maxIterations; iter++)
	{
		// time check
		if (iter > 0 && iter % LOG_FREQUENCY == 0) {
			auto now = std::chrono::high_resolution_clock::now();
			auto elapsed = std::chrono::duration_cast<std::chrono::seconds>(now - startTime).count();
			if (elapsed >= this->maxExecTime) break;
		}

		this->nextPopulation[0] = this->bestSolution;
		// for testing purposes
		this->nextPopulation[1] = Individual(this->evaluator->getGenotypeSize(), this->numGroups, this->rng);

		int currentIdx = 2;
		while (currentIdx < this->popSize)
		{
			Individual& parent1 = this->tournamentSelection();
			Individual& parent2 = this->tournamentSelection();

			if (currentIdx + 1 < this->popSize)
			{
				this->crossover(parent1, parent2, this->nextPopulation[currentIdx], this->nextPopulation[currentIdx + 1]);

				this->mutate(this->nextPopulation[currentIdx]);
				this->mutate(this->nextPopulation[currentIdx + 1]);

				this->evaluator->evaluate(this->nextPopulation[currentIdx]);
				this->evaluator->evaluate(this->nextPopulation[currentIdx + 1]);

				currentIdx += 2;
			}
			else
			{
				this->nextPopulation[currentIdx] = parent1;
				this->mutate(this->nextPopulation[currentIdx]);
				this->evaluator->evaluate(this->nextPopulation[currentIdx]);
				currentIdx += 1;
			}
		}

		std::swap(this->currentPopulation, this->nextPopulation);
		this->updateBestSolution();

		if (this->serializer.get() != nullptr) {
			this->serializer->logIteration(iter + 1, this->bestSolution.getFitness());
			this->serializer->collectSolution(this->bestSolution);
		}

		if (iter % (this->maxIterations / 10) == 0) {
			std::cout << "Iter: " << iter << " Best: " << this->bestSolution.getFitness() << std::endl;
		}
	}

	// print info about execution time
	auto endTime = std::chrono::high_resolution_clock::now();
	auto totalElapsedSeconds = std::chrono::duration_cast<std::chrono::seconds>(endTime - startTime).count();
	std::cout << "Genetic Algorithm finished in " << totalElapsedSeconds << " seconds." << std::endl;
}

void GeneticAlgorithm::setMaxIterations(int maxIterations)
{
	this->maxIterations = maxIterations;
}

void GeneticAlgorithm::setMaxExecTime(int maxExecTime)
{
	this->maxExecTime = maxExecTime;
}

void GeneticAlgorithm::updateBestSolution()
{
	if (this->currentPopulation.empty()) return;

	auto it = std::min_element(this->currentPopulation.begin(), this->currentPopulation.end());

	if (it == this->currentPopulation.end()) return;

	if (bestSolution.getFitness() == 0.0 || it->getFitness() < bestSolution.getFitness())
	{
		bestSolution = *it;
	}
}

Individual& GeneticAlgorithm::tournamentSelection()
{
	std::uniform_int_distribution<int> dist(0, this->popSize - 1);
	int idx1 = dist(rng);
	int idx2 = dist(rng);

	if (currentPopulation[idx1] < currentPopulation[idx2])
	{
		return currentPopulation[idx1];
	}
	else
	{
		return currentPopulation[idx2];
	}
}

void GeneticAlgorithm::mutate(Individual& individual)
{
	static std::uniform_real_distribution<double> probDist(0.0, 1.0);
	static std::uniform_int_distribution<int> groupDist(0, this->numGroups - 1);

	for (int& gene : individual.getRawGenotype())
	{
		if (probDist(this->rng) < this->mutProb)
		{
			gene = groupDist(this->rng);
		}
	}
}

void GeneticAlgorithm::crossover(Individual& parent1, Individual& parent2, Individual& child1, Individual& child2)
{
	std::uniform_real_distribution<double> probDist(0.0, 1.0);

	if (probDist(this->rng) >= this->crossProb)
	{
		child1 = parent1;
		child2 = parent2;
		return;
	}

	std::uniform_int_distribution<int> cutDist(1, parent1.getRawGenotype().size() - 1);
	int cutPoint = cutDist(this->rng);

	const auto& p1Genes = parent1.getRawGenotype();
	const auto& p2Genes = parent2.getRawGenotype();
	auto& c1Genes = child1.getRawGenotype();
	auto& c2Genes = child2.getRawGenotype();

	// just to be sure
	if (c1Genes.size() != p1Genes.size()) c1Genes.resize(p1Genes.size());
	if (c2Genes.size() != p2Genes.size()) c2Genes.resize(p2Genes.size());

	for (size_t i = 0; i < cutPoint; i++)
	{
		c1Genes[i] = p1Genes[i];
		c2Genes[i] = p2Genes[i];
	}

	for (size_t i = cutPoint; i < p1Genes.size(); i++)
	{
		c1Genes[i] = p2Genes[i];
		c2Genes[i] = p1Genes[i];
	}
}