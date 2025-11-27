#include "Interface.h"
#include <iostream>

Interface::Interface()
{
}

Interface::~Interface()
{
}

void Interface::run()
{
	std::string line;

	while (true) {
		std::cout << ">";

		if (!std::getline(std::cin, line)) return;
		if (!line.empty()) {
			std::istringstream iss(line);
			std::string cmd;
			iss >> cmd;

			if (cmd == "exit") {
				return;
			}
			else if (cmd == "enter") {
				std::string formula;
				std::getline(iss, formula);
				enter(formula);

				std::cout << "Tree created" << std::endl;
				std::cout << "Current tree: ";
				print();
			}
			else if (cmd == "vars") {
				vars();
			}
			else if (cmd == "print") {
				print();
			}
			else if (cmd == "comp") {
				std::vector<double> values;
				double val;
				while (iss >> val) {
					values.push_back(val);
				}
				comp(values);
			}
			else if (cmd == "join") {
				std::string formula;
				std::getline(iss, formula);

				join(formula);

				std::cout << "Tree joined. New structure: ";
				tree.print();
			}
			else {
				std::cout << "Unknown command" << std::endl;
			}
		}
	}
}

void Interface::enter(std::string& formula)
{
	tree.enter(formula);
}

void Interface::print()
{
	tree.print();
}

void Interface::vars()
{
	tree.vars();
}

void Interface::comp(std::vector<double>& vals)
{
	tree.comp(vals);
}

void Interface::join(std::string& formula)
{
	Tree newTree;
	newTree.enter(formula);

	tree = tree + newTree;
}
