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
