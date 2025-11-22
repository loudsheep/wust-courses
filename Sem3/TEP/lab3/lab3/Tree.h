#pragma once
#include <string>
#include <vector>
#include "Node.h"

class Tree
{
public:
	Tree();
	Tree(std::string formula);
	Tree(const Tree& other);
	~Tree();

	Tree& operator=(const Tree& other);
	Tree operator+(const Tree& other);

	void enter(std::string& formula);
	void vars();
	void print();
	void comp(const std::vector<double>& vars);
	void join(std::string& formula);

private:
	Node* root;

	std::vector<std::string> tokenize(const std::string& formula);
};

