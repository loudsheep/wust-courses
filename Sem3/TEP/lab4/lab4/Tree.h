#pragma once
#include <string>
#include <vector>
#include "Node.h"
#include "Result.h"
#include "Error.h"

class Tree
{
public:
	Tree();
	Tree(const Tree& other);
	~Tree();

	Tree& operator=(const Tree& other);
	Tree operator+(const Tree& other);

	Result<void, Error> enter(std::string& formula);
	std::string vars();
	Result<double, Error> comp(const std::vector<double>& values);

	std::string toString();
private:
	Node* root;

	void clear();
	std::vector<std::string> tokenize(const std::string& formula);
};

