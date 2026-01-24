#include "ProblemData.h"


ProblemData::ProblemData(std::string name, int dimension, int capacity, std::string weightType,
	int depotId, std::vector<int> permutation, std::vector<Point> coords, std::vector<int> demands,
	std::vector<std::vector<double>> weights) : name(std::move(name)), dimension(dimension), capacity(capacity),
	weightType(std::move(weightType)), depotId(depotId),
	permutation(std::move(permutation)), coords(std::move(coords)),
	demands(std::move(demands)), weights(std::move(weights))
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
