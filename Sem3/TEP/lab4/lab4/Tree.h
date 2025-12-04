#pragma once
#include <string>
#include <vector>
#include "Node.h"

class Tree
{
public:
	Tree();
	Tree(const Tree& other);
	~Tree();

	Tree& operator=(const Tree& other);
	Tree operator+(const Tree& other);

	void enter(std::string& formula);
	void vars();
	void print();
	void comp(const std::vector<double>& values);

private:
	Node* root;

	void clear();
	std::vector<std::string> tokenize(const std::string& formula);
};

