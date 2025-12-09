#include <iostream>
#include "Interface.h"
#include "ResultSaver.h"

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
			}
			else if (cmd == "save") {
				std::string formula;
				std::getline(iss, formula);

				save(formula);
			}
			else if (cmd == "print") {
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
	Result<void, Error> res = tree.enter(formula);

	if (res.isSuccess()) {
		std::cout << "Tree created successfully" << std::endl;
		std::cout << "Current tree: ";
		print();
	}
	else {
		std::cout << "Errors occured when creating tree" << std::endl;
		for (int i = 0; i < res.getErrors().size(); i++)
		{
			std::cout << res.getErrors()[i]->getMessage() << std::endl;
		}
	}
}

void Interface::print()
{
	std::cout << this->tree.toString();
	std::cout << std::endl;
}

void Interface::save(std::string& formula)
{
	Result<Tree*, Error> res = Result<Tree*, Error>::ok(&this->tree);
	ResultSaver<Tree*>::saveToFile(res, "savetest.txt");

	Result<Tree*, Error> res2 = Result<Tree*, Error>::fail(new Error("Some error"));
	ResultSaver<Tree*>::saveToFile(res2, "savetest.txt");
}
