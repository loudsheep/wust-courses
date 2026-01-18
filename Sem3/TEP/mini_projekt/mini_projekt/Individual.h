#pragma once
#include <vector>
#include <random>

class Individual
{
public:
	Individual(int genotypeSize, int numGroups, std::mt19937& rng);
	Individual(const Individual& other);
	Individual(const std::vector<int>& genotype);
	~Individual();

	double getFitness();
	void setFitness(double fitness);

	std::vector<int>& getGenotype();
	void mutate(double mutProb, int numGroups);

	bool operator<(const Individual& other);
private:
	std::vector<int> genotype;
	double fitness;
};

