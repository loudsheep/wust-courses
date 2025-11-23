#include <iostream>
#include "Tree.h"

void runInterface() 
{
	Tree tree;
	std::string line;

	while (true) {
		std::cout << ">";

		if (!std::getline(std::cin, line)) break;
		if (line.empty()) continue;

		std::istringstream iss(line);
		std::string cmd;
		iss >> cmd;

		if (cmd == "exit") {
			return;
		} 
		else if (cmd == "enter") {
			std::string formula;
			std::getline(iss, formula);
			tree.enter(formula);

			std::cout << "Tree created (with auto-repair logic applied)." << std::endl;
			std::cout << "Current tree: ";
			tree.print();
		}
		else if (cmd == "vars") {
			tree.vars();
		}
		else if (cmd == "print") {
			tree.print();
		}
		else if (cmd == "comp") {
			std::vector<double> values;
			double val;
			while (iss >> val) {
				values.push_back(val);
			}
			tree.comp(values);
		}
		else if (cmd == "join") {
			std::string formula;
			std::getline(iss, formula);

			Tree newTree;
			newTree.enter(formula);

			tree = tree + newTree;
			std::cout << "Tree joined. New structure: ";
			tree.print();
		}
		else {
			std::cout << "Unknown command" << std::endl;
		}
	}
}

int main()
{
	runInterface();
}