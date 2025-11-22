#include "Tree.h"
#include <iostream>

Tree::Tree()
{
	this->root = nullptr;
}

Tree::Tree(const Tree& other)
{
	if (other.root != nullptr) {
		this->root = new Node(*other.root);
	}
	else {
		this->root = nullptr;
	}
}

Tree::~Tree()
{
	this->clear();
}

Tree& Tree::operator=(const Tree& other)
{
	if (this != &other) {
		this->clear();

		if (other.root != nullptr) {
			this->root = new Node(*other.root);
		}
	}
	return *this;
}

void Tree::print()
{
	if (this->root == nullptr) {
		std::cout << "Empty root" << std::endl;
		return;
	}

	this->root->print();
	std::cout << std::endl;
}

void Tree::clear() 
{
	if (this->root != nullptr) {
		delete root;
		this->root = nullptr;
	}
}

std::vector<std::string> Tree::tokenize(const std::string& formula)
{
	std::vector<std::string> tokens;

	std::istringstream iss(formula);
	std::string s;

	while (iss >> s) {
		tokens.push_back(s);
	}
	return tokens;
}
