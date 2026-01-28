#include "Evaluator.h"

Evaluator::Evaluator(SmartPointer<ProblemData> problemData, int numGroups) : problemData(problemData), numGroups(numGroups)
{
	this->routeCache.resize(numGroups);
}

double Evaluator::evaluate(Individual& individual)
{
	individual.getPhenotype(this->problemData->getPermutation(), this->routeCache);

	double totalDistance = 0.0;

	for (const auto& route : this->routeCache)
	{
		if (!route.empty()) {
			double routeDistance = 0.0;
			int currentLoad = 0;
			int lastNode = this->problemData->getDepotId();

			for (int customerId : route) {
				int demand = this->problemData->getDemands()[customerId];

				if (currentLoad + demand > this->problemData->getCapacity()) {
					routeDistance += getDistance(lastNode, this->problemData->getDepotId());
					lastNode = this->problemData->getDepotId();
					currentLoad = 0;
				}

				routeDistance += getDistance(lastNode, customerId);
				currentLoad += demand;
				lastNode = customerId;
			}

			routeDistance += getDistance(lastNode, this->problemData->getDepotId());
			totalDistance += routeDistance;
		}
	}

	individual.setFitness(totalDistance);

	return totalDistance;
}

int Evaluator::getGenotypeSize()
{
	return this->problemData->getPermutation().size();
}

int Evaluator::getNumGroups()
{
	return this->numGroups;
}

double Evaluator::getDistance(int from, int to)
{
	if (from < 0 || from >= this->problemData->getDimension() || to < 0 || to >= this->problemData->getDimension()) {
		return std::numeric_limits<double>::max();
	}

	return this->problemData->getWeights()[from][to];

	// TODO: replace by distance matrix in future
	/*double dx = this->problemData->getCoords()[from].x - this->problemData->getCoords()[to].x;
	double dy = this->problemData->getCoords()[from].y - this->problemData->getCoords()[to].y;
	return std::sqrt(dx * dx + dy * dy);*/
}

