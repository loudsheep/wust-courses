#include "ProblemData.h"


ProblemData::ProblemData(const std::string& name, int dimension, int capacity, const std::string& weightType,
	int depotId, const std::vector<int>& permutation, const std::vector<Point>& coords, const std::vector<int>& demands,
	const std::vector<std::vector<double>>& weights)
	: name(name), dimension(dimension), capacity(capacity),
	weightType(weightType), depotId(depotId),
	permutation(permutation), coords(coords),
	demands(demands), weights(weights)
{
}

const std::string& ProblemData::getName()
{
	return this->name;
}

int ProblemData::getDimension() const
{
	return this->dimension;
}

const std::string& ProblemData::getWeightType()
{
	return this->weightType;
}

int ProblemData::getCapacity() const
{
	return this->capacity;
}

int ProblemData::getDepotId() const
{
	return this->depotId;
}

const std::vector<int>& ProblemData::getPermutation()
{
	return this->permutation;
}

const std::vector<Point>& ProblemData::getCoords()
{
	return this->coords;
}

const std::vector<int>& ProblemData::getDemands()
{
	return this->demands;
}

const std::vector<std::vector<double>>& ProblemData::getWeights()
{
	return this->weights;
}
