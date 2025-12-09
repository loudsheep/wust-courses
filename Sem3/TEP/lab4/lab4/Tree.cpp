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

Result<void, Error> Tree::enter(std::string& formula)
{
	std::vector<std::string> tokens = tokenize(formula);
	int offset = 0;

	if (tokens.empty())
	{
		return new Error("No tokens provided");
		//std::cout << "ERROR: No tokens provided" << std::endl;
		//return;
	}


	//bool error = false;
	//this->root = Node::parse(tokens, offset, error);
	//if (error) {
	//	//return Error("No tokens provided");
	//	std::cout << "Given formula had errors, auto-fix logic applied!" << std::endl;
	//}

	Result<Node*, Error> res = Node::parse(tokens, offset);
	if (!res.isSuccess()) return res.getErrors();
	if (offset < tokens.size()) return new Error("Leftover tokens");

	this->clear();
	this->root = res.getValue();

	//std::cout << "Tokens ignored: ";
	//for (int i = offset; i < tokens.size(); i++)
	//{
	//	std::cout << tokens[i] << " ";
	//}
	//std::cout << std::endl;

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
