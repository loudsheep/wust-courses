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
	void mutate(double mutProb, int numGroups, std::mt19937& rng);

	bool operator<(const Individual& other);

	static std::pair<Individual, Individual> crossover(const Individual& parent1, const Individual& parent2, double crossProb, std::mt19937& rng);
private:
	std::vector<int> genotype;
	double fitness;
};

