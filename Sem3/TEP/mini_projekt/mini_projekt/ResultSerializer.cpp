#include "ResultSerializer.h"
#include <algorithm>
#include <iomanip>

const std::string ResultSerializer::JSON_OPEN = "{\n";
const std::string ResultSerializer::JSON_CLOSE = "}\n";

ResultSerializer::ResultSerializer()
	: topN(5), filename("results.json"), problemData(nullptr), execTimeSeconds(0)
{
	// unkamy alokacji w locie
	this->history.reserve(1000);
	this->topSolutions.reserve(20);
}

ResultSerializer::~ResultSerializer()
{
}

void ResultSerializer::setConfig(int topN, const std::string& filename, SmartPointer<ProblemData> problemData)
{
	this->topN = topN;
	this->filename = filename;
	this->problemData = problemData;
}

void ResultSerializer::logIteration(int iter, double currentBestFitness)
{
	IterationLog log;
	log.iteration = iter;
	log.bestFitness = currentBestFitness;
	this->history.push_back(log);
}

void ResultSerializer::collectSolution(Individual& solution)
{
	for (size_t i = 0; i < this->topSolutions.size(); i++)
	{
		if (std::abs(this->topSolutions[i].getFitness() - solution.getFitness()) < 0.00001) {
			return;
		}
	}

	this->topSolutions.push_back(solution);

	std::sort(this->topSolutions.begin(), this->topSolutions.end());

	if (this->topSolutions.size() > this->topN) {
		this->topSolutions.pop_back();
	}
}

void ResultSerializer::setExecTime(unsigned int seconds)
{
	this->execTimeSeconds = seconds;
}

void ResultSerializer::save()
{
	if (this->problemData.get() == nullptr) return;

	std::ofstream json(this->filename);
	if (!json.is_open()) return;

	json << JSON_OPEN;
	this->writeHeader(json);
	this->writeNodes(json);
	this->writeHistory(json);
	this->writeSolutions(json);
	json << JSON_CLOSE;

	json.close();
}

double ResultSerializer::getBestFoundFitness()
{
	if (this->topSolutions.empty()) return -1.0;
	return this->topSolutions[0].getFitness();
}

unsigned int ResultSerializer::getExecTimeSeconds()
{
	return this->execTimeSeconds;
}

void ResultSerializer::writeHeader(std::ofstream& json)
{
	json << "  \"instance\": \"" << this->problemData->getName() << "\",\n";
	json << "  \"capacity\": " << this->problemData->getCapacity() << ",\n";

	int depotIndex = this->problemData->getDepotId();
	json << "  \"depot\": { \"id\": " << depotIndex + 1
		<< ", \"x\": " << this->problemData->getCoords()[depotIndex].x
		<< ", \"y\": " << this->problemData->getCoords()[depotIndex].y << " },\n";
}

void ResultSerializer::writeNodes(std::ofstream& json)
{
	const auto& coords = this->problemData->getCoords();
	const auto& demands = this->problemData->getDemands();

	json << "  \"nodes\": [\n";

	for (size_t i = 0; i < coords.size(); ++i) {
		json << "    { \"id\": " << i + 1
			<< ", \"x\": " << coords[i].x
			<< ", \"y\": " << coords[i].y
			<< ", \"demand\": " << demands[i] << " }";

		if (i < coords.size() - 1) {
			json << ",";
		}
		json << "\n";
	}

	json << "  ],\n";
}

void ResultSerializer::writeHistory(std::ofstream& json)
{
	json << "  \"history\": [\n";
	for (size_t i = 0; i < this->history.size(); i++) {
		json << "    { \"iter\": " << this->history[i].iteration
			<< ", \"fitness\": " << std::fixed << std::setprecision(2) << this->history[i].bestFitness << " }";
		if (i < this->history.size() - 1) json << ",";
		json << "\n";
	}
	json << "  ],\n";
}

void ResultSerializer::writeSolutions(std::ofstream& json)
{
	json << "  \"solutions\": [\n";
	for (size_t s = 0; s < this->topSolutions.size(); s++) {
		json << "    {\n";
		json << "      \"rank\": " << s + 1 << ",\n";
		json << "      \"fitness\": " << this->topSolutions[s].getFitness() << ",\n";
		json << "      \"routes\": [\n";

		this->writeSingleSolutionRoutes(json, this->topSolutions[s]);

		json << "\n      ]\n";
		json << "    }";
		if (s < this->topSolutions.size() - 1) json << ",";
		json << "\n";
	}
	json << "  ]\n";
}

void ResultSerializer::writeSingleSolutionRoutes(std::ofstream& json, Individual& sol)
{
	const auto& permutation = this->problemData->getPermutation();
	const auto& demands = this->problemData->getDemands();
	int capacity = this->problemData->getCapacity();

	std::vector<int> genotype = sol.getRawGenotype();

	int maxGroup = 0;
	for (int g : genotype) if (g > maxGroup) maxGroup = g;

	std::vector<std::vector<int>> rawRoutes(maxGroup + 1);
	for (size_t i = 0; i < permutation.size(); i++) {
		int group = genotype[i];
		if (group >= 0) rawRoutes[group].push_back(permutation[i]);
	}

	bool firstRouteGlobal = true;
	for (const auto& r : rawRoutes) {
		if (r.empty()) continue;

		int currentLoad = 0;
		std::vector<int> currentSubRoute;

		for (int customerId : r) {
			int demand = demands[customerId];
			if (currentLoad + demand > capacity) {
				if (!firstRouteGlobal) json << ",\n";
				json << "        [";
				for (size_t n = 0; n < currentSubRoute.size(); ++n) {
					json << currentSubRoute[n] + 1;
					if (n < currentSubRoute.size() - 1) json << ", ";
				}
				json << "]";
				firstRouteGlobal = false;
				currentSubRoute.clear();
				currentLoad = 0;
			}
			currentSubRoute.push_back(customerId);
			currentLoad += demand;
		}
		if (!currentSubRoute.empty()) {
			if (!firstRouteGlobal) json << ",\n";
			json << "        [";
			for (size_t n = 0; n < currentSubRoute.size(); ++n) {
				json << currentSubRoute[n] + 1;
				if (n < currentSubRoute.size() - 1) json << ", ";
			}
			json << "]";
			firstRouteGlobal = false;
		}
	}
}
