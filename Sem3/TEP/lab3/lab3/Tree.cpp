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

Tree Tree::operator+(const Tree& other)
{
	Tree res(*this);

	if (other.root == nullptr) return res;

	if (res.root == nullptr) {
		res.root = new Node(*other.root);
		return res;
	}

	Node& toReplace = res.root->getLeftmostLeaf();

	toReplace = *other.root;

	return res;
}

void Tree::enter(std::string& formula)
{
	std::vector<std::string> tokens = tokenize(formula);
	int offset = 0;

	if (tokens.empty())
	{
		std::cout << "ERROR: No tokens provided" << std::endl;
		return;
	}

	this->root = Node::parse(tokens, offset);

	if (offset < tokens.size()) {
		std::cout << "Tokens ignored: ";
		for (int i = offset; i < tokens.size(); i++)
		{
			std::cout << tokens[i] << " ";
		}
		std::cout << std::endl;
	}
}

void Tree::vars()
{
	if (this->root == nullptr) return;

	std::set<std::string> vairables;
	this->root->getVariables(vairables);

	for (std::set<std::string>::iterator it = vairables.begin(); it != vairables.end(); ++it) {
		std::cout << *it << " ";
	}

	std::cout << std::endl;
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

void Tree::comp(const std::vector<double>& values)
{
	if (this->root == nullptr)
	{
		std::cout << "Empty tree, cannot compute value" << std::endl;
		return;
	}

	std::set<std::string> variableNames;
	this->root->getVariables(variableNames);

	if (values.size() != variableNames.size()) {
		std::cout << "ERROR Variable count mismatch. Expected " << variableNames.size()
			<< ", got " << values.size() << std::endl;
		return;
	}

	std::map<std::string, double> varMap;
	int idx = 0;
	for (std::set<std::string>::iterator it = variableNames.begin(); it != variableNames.end(); ++it) {
		varMap[*it] = values[idx++];
	}

	std::cout << this->root->eval(varMap) << std::endl;
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
