#include "Tree.h"

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

Result<void, Error> Tree::enter(std::string& formula)
{
	std::vector<std::string> tokens = tokenize(formula);
	int offset = 0;

	if (tokens.empty()) return new Error("No tokens provided");

	Result<Node*, Error> res = Node::parse(tokens, offset);

	if (!res.isSuccess()) return new Error(*res.getErrors()[0]);

	if (offset < tokens.size()) {
		Node* createdTree = res.getValue();
		delete createdTree;

		return new Error("Leftover tokens");
	}

	this->clear();
	this->root = res.getValue();

	return Result<void, Error>::ok();
}

std::string Tree::vars()
{
	if (this->root == nullptr) {
		return "Empty root - no varaiables!";
	}

	std::set<std::string> vairables;
	this->root->getVariables(vairables);

	if (vairables.size() == 0) {
		return "No variables in formula!";
	}

	std::string res;
	for (std::set<std::string>::iterator it = vairables.begin(); it != vairables.end(); ++it) {
		res += *it + " ";
	}

	return res;
}

Result<double, Error> Tree::comp(const std::vector<double>& values)
{
	if (this->root == nullptr)
	{
		return new Error("Empty tree, cannot compute value");
	}

	std::set<std::string> variableNames;
	this->root->getVariables(variableNames);

	if (values.size() != variableNames.size()) {
		return new Error("Variable count mismatch. Expected " + std::to_string(variableNames.size()) + ", got " + std::to_string(values.size()));
	}

	std::map<std::string, double> varMap;
	int idx = 0;
	for (std::set<std::string>::iterator it = variableNames.begin(); it != variableNames.end(); ++it) {
		varMap[*it] = values[idx++];
	}

	return this->root->eval(varMap);
}

std::string Tree::toString()
{
	if (this->root == nullptr) {
		return "Empty root";
	}
	return this->root->print();
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
