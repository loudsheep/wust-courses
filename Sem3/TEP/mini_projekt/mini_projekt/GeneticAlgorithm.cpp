#include "GeneticAlgorithm.h"
#include <algorithm>
#include <fstream>
#include <iomanip>
#include <set>
#include <chrono>
#include <iostream>

GeneticAlgorithm::GeneticAlgorithm(int popSize, double crossProb, double mutProb, int numGroups)
	: popSize(popSize), crossProb(crossProb), mutProb(mutProb), evaluator(nullptr), maxIterations(1000), maxExecTime(60), numGroups(numGroups), problemData(nullptr)
{
	std::random_device rd;
	this->rng = std::mt19937(rd());
}

void GeneticAlgorithm::init(SmartPointer<ProblemData> probmelData)
{
	this->problemData = SmartPointer<ProblemData>(probmelData);
	this->evaluator = SmartPointer<Evaluator>(new Evaluator(probmelData, this->numGroups));

	this->fitnessHistory.clear();
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
		auto currentTime = std::chrono::high_resolution_clock::now();
		auto elapsedSeconds = std::chrono::duration_cast<std::chrono::seconds>(currentTime - startTime).count();
		if (elapsedSeconds >= this->maxExecTime) {
			if (this->exportEnabled) this->saveResultsToJson();
			return;
		}

		this->nextPopulation[0] = this->bestSolution;
		int currentIdx = 1;
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

		this->fitnessHistory.push_back({ iter, this->bestSolution.getFitness() });

		if (iter == 0 || iter % (this->maxIterations / 10) == 0) {
			std::cout << "Iteration " << iter + 1
				<< " | Best Fitness: " << std::fixed << std::setprecision(2)
				<< this->bestSolution.getFitness() << std::endl;
		}
	}
	if (this->exportEnabled) this->saveResultsToJson();

	// print info about execution time
	auto endTime = std::chrono::high_resolution_clock::now();
	auto totalElapsedSeconds = std::chrono::duration_cast<std::chrono::seconds>(endTime - startTime).count();
	std::cout << "Genetic Algorithm finished in " << totalElapsedSeconds << " seconds." << std::endl;
}

Individual GeneticAlgorithm::getBestSolution()
{
	return this->bestSolution;
}

void GeneticAlgorithm::setMaxIterations(int maxIterations)
{
	this->maxIterations = maxIterations;
}

void GeneticAlgorithm::setMaxExecTime(int maxExecTime)
{
	this->maxExecTime = maxExecTime;
}

void GeneticAlgorithm::setExportConfig(bool enable, int topN, const std::string& filename)
{
	this->exportEnabled = enable;
	this->exportTopN = topN;
	this->exportFilename = filename;
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

void GeneticAlgorithm::saveResultsToJson()
{
	std::ofstream json(this->exportFilename);
	if (!json.is_open()) return;

	std::vector<Individual> sortedPop = this->currentPopulation;
	std::sort(sortedPop.begin(), sortedPop.end());

	std::vector<Individual> topSolutions;
	std::set<double> seenFitness;

	for (auto& ind : sortedPop) {
		if (seenFitness.find(ind.getFitness()) == seenFitness.end()) {
			topSolutions.push_back(ind);
			seenFitness.insert(ind.getFitness());
		}
		if (topSolutions.size() >= this->exportTopN) break;
	}

	auto& problem = *this->problemData;
	auto& permutation = const_cast<ProblemData&>(problem).getPermutation();
	auto& demands = const_cast<ProblemData&>(problem).getDemands();

	json << "{\n";

	json << "  \"instance\": \"" << const_cast<ProblemData&>(problem).getName() << "\",\n";
	json << "  \"capacity\": " << const_cast<ProblemData&>(problem).getCapacity() << ",\n";
	json << "  \"depot\": { \"id\": " << problem.getDepotId() + 1 << ", \"x\": "
		<< problem.getCoords()[problem.getDepotId()].x << ", \"y\": "
		<< problem.getCoords()[problem.getDepotId()].y << " },\n";

	json << "  \"nodes\": [\n";
	for (int i = 0; i < problem.getCoords().size(); i++) {
		json << "    { \"id\": " << i + 1 << ", \"x\": " << problem.getCoords()[i].x
			<< ", \"y\": " << problem.getCoords()[i].y
			<< ", \"demand\": " << demands[i] << " }";
		if (i < problem.getCoords().size() - 1) json << ",";
		json << "\n";
	}
	json << "  ],\n";

	json << "  \"history\": [\n";
	for (size_t i = 0; i < this->fitnessHistory.size(); i++) {
		json << "    { \"iter\": " << fitnessHistory[i].first << ", \"fitness\": " << fitnessHistory[i].second << " }";
		if (i < fitnessHistory.size() - 1) json << ",";
		json << "\n";
	}
	json << "  ],\n";

	json << "  \"solutions\": [\n";
	for (size_t s = 0; s < topSolutions.size(); s++) {
		Individual& sol = topSolutions[s];
		json << "    {\n";
		json << "      \"rank\": " << s + 1 << ",\n";
		json << "      \"fitness\": " << sol.getFitness() << ",\n";
		json << "      \"routes\": [\n";

		std::vector<std::vector<int>> rawRoutes(this->evaluator->getNumGroups());
		for (size_t i = 0; i < permutation.size(); i++) {
			int group = sol.getRawGenotype()[i];
			if (group >= 0 && group < rawRoutes.size()) {
				rawRoutes[group].push_back(permutation[i]);
			}
		}

		bool firstRoute = true;
		for (const auto& r : rawRoutes) {
			if (r.empty()) continue;

			int currentLoad = 0;
			std::vector<int> currentSubRoute;

			for (int customerId : r) {
				int demand = demands[customerId];
				if (currentLoad + demand > problem.getCapacity()) {
					if (!firstRoute) json << ",\n";
					json << "        [";
					for (size_t n = 0; n < currentSubRoute.size(); ++n) {
						json << currentSubRoute[n] + 1; // +1 for 1-based IDs
						if (n < currentSubRoute.size() - 1) json << ", ";
					}
					json << "]";
					firstRoute = false;

					currentSubRoute.clear();
					currentLoad = 0;
				}
				currentSubRoute.push_back(customerId);
				currentLoad += demand;
			}

			if (!currentSubRoute.empty()) {
				if (!firstRoute) json << ",\n";
				json << "        [";
				for (size_t n = 0; n < currentSubRoute.size(); ++n) {
					json << currentSubRoute[n] + 1;
					if (n < currentSubRoute.size() - 1) json << ", ";
				}
				json << "]";
				firstRoute = false;
			}
		}
		json << "\n      ]\n";
		json << "    }";
		if (s < topSolutions.size() - 1) json << ",";
		json << "\n";
	}
	json << "  ]\n";

	json << "}\n";
	json.close();
}