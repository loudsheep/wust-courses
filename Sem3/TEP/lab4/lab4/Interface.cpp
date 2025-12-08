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
	std::cout << this->tree.toString();
}