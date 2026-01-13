#include "Node.h"
#include <algorithm>

const std::string DEFAULT_VALUE = "1";
const std::string OPERATOR_ADD = "+";
const std::string OPERATOR_SUB = "-";
const std::string OPERATOR_MUL = "*";
const std::string OPERATOR_DIV = "/";
const std::string OPERATOR_SIN = "sin";
const std::string OPERATOR_COS = "cos";

Node::Node(const std::string& value)
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

Node& Node::operator=(const Node& other)
{
	if (this == &other) return *this;

	this->value = other.value;
	for (int i = 0; i < this->children.size(); i++)
	{
		delete this->children[i];
	}
	children.clear();

	for (int i = 0; i < other.children.size(); i++)
	{
		this->children.push_back(new Node(*other.children[i]));
	}

	return *this;
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

	if (this->value == OPERATOR_ADD) return v1 + v2;
	if (this->value == OPERATOR_SUB) return v1 - v2;
	if (this->value == OPERATOR_MUL) return v1 * v2;
	if (this->value == OPERATOR_DIV) {
		if (v2 == 0) return 0;
		return v1 / v2;
	}

	if (this->value == OPERATOR_SIN) return std::sin(v1);
	if (this->value == OPERATOR_COS) return std::cos(v1);

	return 0;
}

std::string Node::print()
{
	std::string res = this->value + " ";
	for (int i = 0; i < this->children.size(); i++)
	{
		res += this->children[i]->print();
	}
	return res;
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

Node* Node::parse(const std::vector<std::string>& tokens, int& offset, bool& syntaxErrors)
{
	if (offset >= tokens.size()) {
		syntaxErrors = true;
		return new Node(DEFAULT_VALUE);
	}

	std::string token = tokens[offset];
	offset++;

	if (!isTokenValid(token)) {
		syntaxErrors = true;
		return parse(tokens, offset, syntaxErrors);
	}

	Node* newNode = new Node(token);
	if (isOperator(token)) {
		int numArgs = expectedArgs(token);
		for (int i = 0; i < numArgs; i++)
		{
			newNode->children.push_back(parse(tokens, offset, syntaxErrors));
		}
	}

	return newNode;
}

Node& Node::getLeftmostLeaf()
{
	if (this->isLeaf()) return *this;
	return this->children[0]->getLeftmostLeaf();
}

bool Node::isOperator(const std::string& value)
{
	return value == OPERATOR_ADD ||
		value == OPERATOR_SUB ||
		value == OPERATOR_MUL ||
		value == OPERATOR_DIV ||
		value == OPERATOR_SIN ||
		value == OPERATOR_COS;
}

bool Node::isNumber(const std::string& value)
{
	if (value.empty()) return false;

	for (int i = 0; i < value.length(); i++)
	{
		if (!isdigit(value[i]) && value[i] != '.') return false;
	}
	return true;
}

bool Node::isVariable(const std::string& value)
{
	return !isNumber(value) && !isOperator(value) && isStrAlfanum(value);
}

bool Node::isStrAlfanum(const std::string& value)
{
	for (int i = 0; i < value.size(); i++)
	{
		if (!std::isalnum(value[i])) return false;
	}
	return true;
}

bool Node::isTokenValid(const std::string& token)
{
	return isOperator(token) || isNumber(token) || isVariable(token);
}

double Node::stringToDouble(const std::string& str)
{
	return std::atof(str.c_str());
}

int Node::expectedArgs(const std::string& op)
{
	if (op == OPERATOR_ADD || op == OPERATOR_SUB || op == OPERATOR_MUL || op == OPERATOR_DIV) return 2;
	if (op == OPERATOR_SIN || op == OPERATOR_COS) return 1;
	return 0;
}


