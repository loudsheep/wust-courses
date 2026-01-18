#include "ProblemData.h"

ProblemData::ProblemData() :
	dimension(0),
	capacity(0),
	depotId(1)
{
}

std::string& ProblemData::getName()
{
	return this->name;
}

int ProblemData::getDimension()
{
	return this->dimension;
}

std::string& ProblemData::getWeightType()
{
	return this->weightType;
}

int ProblemData::getCapacity()
{
	return this->capacity;
}

int ProblemData::getDepotId()
{
	return this->depotId;
}

std::vector<int>& ProblemData::getPermutation()
{
	return this->permutation;
}

std::vector<Point>& ProblemData::getCoords()
{
	return this->coords;
}

std::vector<int>& ProblemData::getDemands()
{
	return this->demands;
}

std::vector<std::vector<double>>& ProblemData::getWeights()
{
	return this->weights;
}

void ProblemData::setName(const std::string& name)
{
	this->name = name;
}

void ProblemData::setDimension(int dimension)
{
	this->dimension = dimension;
}

void ProblemData::setCapacity(int capacity)
{
	this->capacity = capacity;
}

void ProblemData::setDepotId(int depotId)
{
	this->depotId = depotId;
}

void ProblemData::setWeightType(const std::string& weightType)
{
	this->weightType = weightType;
}

void ProblemData::setPermutation(const std::vector<int>& permutation)
{
	this->permutation = permutation;
}

void ProblemData::setCoords(const std::vector<Point>& coords)
{
	this->coords = coords;
}

void ProblemData::setDemands(const std::vector<int>& demands)
{
	this->demands = demands;
}

void ProblemData::setWeights(const std::vector<std::vector<double>>& weights)
{
	this->weights = weights;
}


