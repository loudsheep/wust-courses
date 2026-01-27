#pragma once
#include <vector>
#include <random>

class Individual
{
public:
	Individual(int genotypeSize, int numGroups, std::mt19937& rng);
	Individual(const Individual& other);
	Individual(const std::vector<int>& genotype);
	Individual(Individual&& other) noexcept;
	~Individual();

	Individual& operator=(const Individual& other);
	Individual& operator=(Individual&& other) noexcept;

	double getFitness();
	void setFitness(double fitness);

	std::vector<int>& getRawGenotype();
	void getPhenotype(const std::vector<int>& permutation, std::vector<std::vector<int>>& outPhenotype);

	bool operator<(const Individual& other);

private:
	std::vector<int> genotype;
	double fitness;
};

