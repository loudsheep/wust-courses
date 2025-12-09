#pragma once
#include <fstream>
#include <string>
#include <iostream>
#include "Error.h"
#include "Result.h"
#include "Tree.h"


template<typename T>
class ResultSaver
{
public:
	static bool saveToFile(Result<T, Error>& result, const std::string& fileName) {
		std::ofstream file(fileName.c_str(), std::ios::app);

		if (!file.is_open()) return false;

		if (!result.isSuccess()) {
			file << "Error occured:\n";

			std::vector<Error*>& errors = result.getErrors();
			for (int i = 0; i < errors.size(); i++)
			{
				file << errors[i]->getMessage() << "\n";
			}
		}

		file.close();
		return true;
	}
};

template<>
class ResultSaver<Tree*>
{
public:
	static bool saveToFile(Result<Tree*, Error>& result, const std::string& fileName) {
		std::ofstream file(fileName.c_str(), std::ios::app);

		if (!file.is_open()) return false;

		if (result.isSuccess())
		{
			file << result.getValue()->toString() << "\n";
		}
		else
		{
			file << "Error occured:\n";

			std::vector<Error*>& errors = result.getErrors();
			for (int i = 0; i < errors.size(); i++)
			{
				file << errors[i]->getMessage() << "\n";
			}
		}

		file.close();
		return true;
	}
};