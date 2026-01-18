#pragma once
#include <string>
#include <vector>

struct Point {
	double x, y;
};


class ProblemData
{
public:
	ProblemData();

	// getters
	std::string& getName();
	int getDimension();
	std::string& getWeightType();
	int getCapacity();
	int getDepotId();
	std::vector<int>& getPermutation();
	std::vector<Point>& getCoords();
	std::vector<int>& getDemands();
	std::vector<std::vector<double>>& getWeights();

	// setters
	void setName(const std::string& name);
	void setDimension(int dimension);
	void setCapacity(int capacity);
	void setDepotId(int depotId);
	void setWeightType(const std::string& weightType);

	void setPermutation(const std::vector<int>& permutation);
	void setCoords(const std::vector<Point>& coords);
	void setDemands(const std::vector<int>& demands);
	void setWeights(const std::vector<std::vector<double>>& weights);

private:
	std::string name;
	int dimension;
	std::string weightType;
	int capacity;
	int depotId;

	std::vector<int> permutation;
	std::vector<Point> coords;
	std::vector<int> demands;
	std::vector<std::vector<double>> weights;
};

