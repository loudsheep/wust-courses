#include "Evaluator.h"
#include "ProblemDataParser.h"

Evaluator::Evaluator(int numGroups) : numGroups(numGroups)
{
}

void Evaluator::loadInstance(const std::string& folder, const std::string& instance)
{
	ProblemDataParser parser(folder, instance);
	this->problemData = parser.load();
}

double Evaluator::evaluate(Individual& individual)
{
	if (individual.getGenotype().size() != this->getGenotypeSize()) return std::numeric_limits<double>::max();

	std::vector<std::vector<int>> routes(this->numGroups);

	for (int i = 0; i < this->problemData.getPermutation().size(); i++)
	{
		int customerId = this->problemData.getPermutation()[i];
		int groupIdx = individual.getGenotype()[i];

		if (groupIdx >= 0 && groupIdx < this->numGroups)
		{
			routes[groupIdx].push_back(customerId);
		}
	}

	double totalDistance = 0.0;

	for (const auto& route : routes)
	{
		if (!route.empty()) {
			double routeDistance = 0.0;
			int currentLoad = 0;
			int lastNode = this->problemData.getDepotId();

			for (int customerId : route) {
				int demand = this->problemData.getDemands()[customerId];

				if (currentLoad + demand > this->problemData.getCapacity()) {
					routeDistance += calculateDistance(lastNode, this->problemData.getDepotId());
					lastNode = this->problemData.getDepotId();
					currentLoad = 0;
				}

				routeDistance += calculateDistance(lastNode, customerId);
				currentLoad += demand;
				lastNode = customerId;
			}

			routeDistance += calculateDistance(lastNode, this->problemData.getDepotId());
			totalDistance += routeDistance;
		}
	}

	individual.setFitness(totalDistance);

	return totalDistance;
}

int Evaluator::getGenotypeSize()
{
	return this->problemData.getPermutation().size();
}

int Evaluator::getNumGroups()
{
	return this->numGroups;
}

ProblemData& Evaluator::getProblemData()
{
	return this->problemData;
}

double Evaluator::calculateDistance(int from, int to)
{
	// TODO: replace by distance matrix in future
	double dx = this->problemData.getCoords()[from].x - this->problemData.getCoords()[to].x;
	double dy = this->problemData.getCoords()[from].y - this->problemData.getCoords()[to].y;
	return std::sqrt(dx * dx + dy * dy);
}

