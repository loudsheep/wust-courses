#include "Node.h"
#include <iostream>

const std::string DEFAULT_VALUE = "1";

Node::Node(std::string value)
{
	this->value = value;
}

Node::Node(const Node& other)
{
	this->value = other.value;
	for (int i = 0; i < other.children.size(); i++)
	{
		this->children.push_back(new Node(*other.children[i]));
	}
}

Node::~Node()
{
	for (int i = 0; i < this->children.size(); i++)
	{
		delete this->children[i];
	}
	children.clear();
}

double Node::eval(const std::map<std::string, double>& variables)
{
	if (isNumber(this->value)) return stringToDouble(value);

	if (isVariable(this->value)) {
		if (variables.find(this->value) != variables.end()) {
			return variables.at(value);
		}
		return 0;
	}

	double v1 = this->children.size() > 0 ? this->children[0]->eval(variables) : 0;
	double v2 = this->children.size() > 1 ? this->children[1]->eval(variables) : 0;

	if (this->value == "+") return v1 + v2;
	if (this->value == "-") return v1 - v2;
	if (this->value == "*") return v1 * v2;
	if (this->value == "/") {
		if (v2 == 0) return 0;
		return v1 / v2;
	}

	if (this->value == "sin") return std::sin(v1);
	if (this->value == "cos") return std::cos(v1);

	return 0;
}

void Node::print()
{
	std::cout << this->value << " ";
	for (int i = 0; i < this->children.size(); i++)
	{
		this->children[i]->print();
	}
}

void Node::getVariables(std::set<std::string>& variables)
{
	if (isVariable(this->value)) {
		variables.insert(this->value);
	}
	for (int i = 0; i < this->children.size(); i++)
	{
		this->children[i]->getVariables(variables);
	}
}

bool Node::isLeaf()
{
	return this->children.empty();
}

bool Node::isOperator(std::string& value)
{
	return value == "+" || value == "-" || value == "*" || value == "/" || value == "sin" || value == "cos";
}

bool Node::isNumber(std::string& value)
{
	if (value.empty()) return false;

	for (int i = 0; i < value.length(); i++)
	{
		if (!isdigit(value[i]) && value[i] != '.') return false;
	}
	return true;
}

bool Node::isVariable(std::string& value)
{
	return !isNumber(value) && !isOperator(value);
}

double Node::stringToDouble(std::string& str)
{
	return std::atof(str.c_str());
}


