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

void Interface::vars()
{
	std::cout << this->tree.vars() << std::endl;
}

void Interface::comp(std::vector<double>& vals)
{
	Result<double, Error> res = this->tree.comp(vals);

	if (res.isSuccess()) {
		std::cout << res.getValue() << std::endl;
	}
	else {
		std::cout << "Errors occured when evaluating tree" << std::endl;
		for (int i = 0; i < res.getErrors().size(); i++)
		{
			std::cout << res.getErrors()[i]->getMessage() << std::endl;
		}
	}
}

void Interface::join(std::string& formula)
{
	Tree newTree;
	Result<void, Error> res = newTree.enter(formula);

	if (res.isSuccess()) {
		this->tree = this->tree + newTree;
		std::cout << "Success!" << std::endl;
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
