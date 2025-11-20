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

public:
	Node();
	Node(std::string value);
	Node(const Node& other);
	~Node();

	Node& operator=(const Node& other);

	double eval(const std::map<std::string, double>& variables);
	void getVariables(std::set<std::string>& variables);
	std::string toString();
};

