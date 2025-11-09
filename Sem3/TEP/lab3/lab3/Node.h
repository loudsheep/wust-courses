#pragma once
#include <string>
#include <vector>
#include <map>
#include <set>
#include <sstream>

class Node
{
private:
	std::string value;
	int type;
	std::vector<Node*> children;

	void clearChildren();

	static int getNodeType(const std::string& value);
	static bool isNumber(const std::string& value);
	static bool isValidVariable(const std::string& value);
	static int getArgsNeeded(const std::string& oper);

public:
	Node(std::string value);
	Node(const Node& other);
	~Node();

	Node& operator=(const Node& other);

	double evaluate(const std::map<std::string, double>& variables);
	void getVariables(std::set<std::string>& variables);
	std::string toString();
};

