#pragma once
#include "tree.h"

class Interface
{
public:
	Interface();
	~Interface();

	void run();

private:
	Tree tree;

	void enter(std::string& formula);
	void print();
	void vars();
	void comp(std::vector<double>& vals);
	void join(std::string& formula);
};

