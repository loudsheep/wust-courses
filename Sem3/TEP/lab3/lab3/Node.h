#pragma once
#include <string>
#include <vector>
#include <map>
#include <set>
#include <sstream>

class Node
{
public:
	Node(std::string value);
	Node(const Node& other);
	~Node();

	Node& operator=(const Node& other);

	double eval(const std::map<std::string, double>& variables);
	void print();
	void getVariables(std::set<std::string>& variables);
	bool isLeaf();

	static Node* parse(std::vector<std::string>& tokens, int& offset);

private:
	std::string value;
	std::vector<Node*> children;

	Node& getLeftmostLeaf();

	static bool isOperator(std::string& value);
	static bool isNumber(std::string& value);
	static bool isVariable(std::string& value);
	static double stringToDouble(std::string& str);
	static int expectedArgs(std::string& op);

	friend class Tree;
};

