#pragma once
#include <string>
#include <vector>

struct Point {
	double x, y;
};


class ProblemData
{
public:
	ProblemData(std::string name, int dimension, int capacity, std::string weightType, int depotId,
		std::vector<int> permutation, std::vector<Point> coords,
		std::vector<int> demands, std::vector<std::vector<double>> weights);

	const std::string& getName();
	int getDimension() const;
	const std::string& getWeightType();
	int getCapacity() const;
	int getDepotId() const;
	const std::vector<int>& getPermutation();
	const std::vector<Point>& getCoords();
	const std::vector<int>& getDemands();
	const std::vector<std::vector<double>>& getWeights();

private:
	const std::string name;
	const int dimension;
	const std::string weightType;
	const int capacity;
	const int depotId;

	const std::vector<int> permutation;
	const std::vector<Point> coords;
	const std::vector<int> demands;
	const std::vector<std::vector<double>> weights;
};

