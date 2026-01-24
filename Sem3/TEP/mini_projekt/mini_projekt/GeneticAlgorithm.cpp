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

	//this->evaluator.loadInstance(folder, instance);
	this->fitnessHistory.clear();
	this->population.clear();
	this->population.reserve(this->popSize);

	int genotypeSize = this->evaluator->getGenotypeSize();
	int groups = this->numGroups;

	for (int i = 0; i < this->popSize; i++)
	{
		Individual ind(genotypeSize, groups, this->rng);
		double fitness = this->evaluator->evaluate(ind);
		ind.setFitness(fitness);
		this->population.push_back(std::move(ind));
	}

	this->updateBestSolution();
}

void GeneticAlgorithm::run()
{
	auto startTime = std::chrono::high_resolution_clock::now();

	for (int iter = 0; iter < this->maxIterations; iter++)
	{
		auto currentTime = std::chrono::high_resolution_clock::now();
		auto elapsedSeconds = std::chrono::duration_cast<std::chrono::seconds>(currentTime - startTime).count();
		if (elapsedSeconds >= this->maxExecTime) {
			if (this->exportEnabled) this->saveResultsToJson();
			return;
		}

		std::vector<Individual> newPopulation;
		newPopulation.reserve(this->popSize);

		newPopulation.push_back(this->bestSolution);

		while (newPopulation.size() < this->popSize)
		{
			Individual& parent1 = this->tournamentSelection();
			Individual& parent2 = this->tournamentSelection();

			auto children = this->crossover(parent1, parent2);

			this->mutate(children.first);
			this->mutate(children.second);

			this->evaluator->evaluate(children.first);
			this->evaluator->evaluate(children.second);

			newPopulation.push_back(std::move(children.first));
			if (newPopulation.size() < this->popSize)
			{
				newPopulation.push_back(std::move(children.second));
			}
		}

		this->population = std::move(newPopulation);
		this->updateBestSolution();

		this->fitnessHistory.push_back({ iter, this->bestSolution.getFitness() });

		if (iter == 0 || iter % (this->maxIterations / 10) == 0) {
			std::cout << "Iteration " << iter + 1
				<< " | Best Fitness: " << std::fixed << std::setprecision(2)
				<< this->bestSolution.getFitness() << std::endl;
		}
	}
	if (this->exportEnabled) this->saveResultsToJson();
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
	std::uniform_int_distribution<int> dist(0, this->popSize - 1);
	int idx1 = dist(rng);
	int idx2 = dist(rng);

	if (population[idx1] < population[idx2])
	{
		return population[idx1];
	}
	else
	{
		return population[idx2];
	}
}

void GeneticAlgorithm::mutate(Individual& individual)
{
	std::uniform_real_distribution<double> probDist(0.0, 1.0);
	std::uniform_int_distribution<int> groupDist(0, this->numGroups - 1);

	for (int& gene : individual.getRawGenotype())
	{
		if (probDist(this->rng) < this->mutProb)
		{
			gene = groupDist(this->rng);
		}
	}
}

std::pair<Individual, Individual> GeneticAlgorithm::crossover(Individual& parent1, Individual& parent2)
{
	std::uniform_real_distribution<double> probDist(0.0, 1.0);

	if (probDist(this->rng) >= this->crossProb)
	{
		return { Individual(parent1), Individual(parent2) };
	}

	std::uniform_int_distribution<int> cutDist(1, parent1.getRawGenotype().size() - 1);
	int cutPoint = cutDist(this->rng);

	std::vector<int> child1Genotype = parent1.getRawGenotype();
	std::vector<int> child2Genotype = parent2.getRawGenotype();

	for (size_t i = cutPoint; i < parent1.getRawGenotype().size(); ++i)
	{
		child1Genotype[i] = parent2.getRawGenotype()[i];
		child2Genotype[i] = parent1.getRawGenotype()[i];
	}

	return { Individual(child1Genotype), Individual(child2Genotype) };
}

void GeneticAlgorithm::saveResultsToJson()
{
	std::ofstream json(this->exportFilename);
	if (!json.is_open()) return;

	std::vector<Individual> sortedPop = this->population;
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