#pragma once
#include <string>
#include <vector>
#include <map>
#include <set>
#include <sstream>

class Node
{
public:
	Node(const std::string& value);
	Node(const Node& other);
	~Node();

	Node& operator=(const Node& other);

	double eval(const std::map<std::string, double>& variables);
	void print();
	void getVariables(std::set<std::string>& variables);
	bool isLeaf();

	static Node* parse(const std::vector<std::string>& tokens, int& offset, bool& syntaxErrors);

private:
	std::string value;
	std::vector<Node*> children;

	Node& getLeftmostLeaf();

	static bool isOperator(const std::string& value);
	static bool isNumber(const std::string& value);
	static bool isVariable(const std::string& value);
	static bool isStrAlfanum(const std::string& value);
	static bool isTokenValid(const std::string& token);
	static double stringToDouble(const std::string& str);
	static int expectedArgs(const std::string& op);

	friend class Tree;
};

