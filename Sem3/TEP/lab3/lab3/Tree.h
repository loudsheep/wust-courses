#pragma once
#include <string>
#include <vector>
#include "Node.h"

class Tree
{
private:
	Node* root;

public:
	Tree();
	Tree(const Tree& other);
	~Tree();

	Tree& operator=(const Tree& other);
	Tree operator+(const Tree& other);

	std::string print();
};

