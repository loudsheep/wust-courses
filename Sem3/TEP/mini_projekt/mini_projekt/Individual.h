#pragma once
#include <vector>

class Individual
{
public:
	Individual(int genotypeSize, int maxGeneValue);
	Individual(const Individual& other);
	~Individual();

	double getFitness();
	void setFitness(double fitness);

	std::vector<int>& getGenotype();
	void mutate(double mutProb, int maxGeneValue);


private:
	std::vector<int> genotype;
	double fitness;
};

