#include "ProblemDataParser.h"
#include <sstream>
#include <fstream>
#include <iostream>

ProblemDataParser::ProblemDataParser(const std::string& folder_name, const std::string& instance) : folder_name(folder_name), instance(instance)
{
}

ProblemData ProblemDataParser::load()
{
	ProblemData problemData;
	std::string filepath = this->folder_name + this->instance + ".lcvrp";

	this->parseFile(filepath, problemData);

	return problemData;
}

void ProblemDataParser::parseFile(const std::string& filepath, ProblemData& problemData)
{
	std::ifstream file(filepath);

	if (!file.is_open())
	{
		std::cerr << "Error: Cannot open file: " << filepath << std::endl;
		exit(1);
		return;
	}

	std::string line;
	bool readingCoords = false;
	bool readingDemands = false;

	while (std::getline(file, line)) {
		std::string cleanLine = trim(line);

		// TODO: change that to not use conrtinue
		if (cleanLine.empty()) continue;
		if (cleanLine.find("EOF") != std::string::npos) return;

		if (cleanLine.find("NAME") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				std::string name = cleanLine.substr(colon + 1);
				problemData.setName(name);
			}
		}
		else if (cleanLine.find("DIMENSION") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				int dimension = std::stoi(cleanLine.substr(colon + 1));
				problemData.setDimension(dimension);
			}
		}
		else if (cleanLine.find("CAPACITY") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				int capacity = std::stoi(cleanLine.substr(colon + 1));
				problemData.setCapacity(capacity);
			}
		}
		else if (cleanLine.find("EDGE_WEIGHT_TYPE") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				std::string weightType = cleanLine.substr(colon + 1);
				problemData.setWeightType(weightType);
			}
		}
		else if (cleanLine.find("PERMUTATION") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			std::string permString = cleanLine.substr(colon + 1);

			std::stringstream ss(permString);
			std::vector<int> permutation;
			int id;
			while (ss >> id) {
				permutation.push_back(id - 1);
			}
			problemData.setPermutation(permutation);
		}
		else if (cleanLine.find("NODE_COORD_SECTION") != std::string::npos) {
			readingCoords = true;
			readingDemands = false;

			int dimension = problemData.getDimension();
			std::vector<Point> coords(dimension);

			for (int i = 0; i < dimension; i++)
			{
				int nodeId;
				double x, y;
				file >> nodeId >> x >> y;
				Point point;
				point.x = x;
				point.y = y;
				coords[i] = point;
			}

			problemData.setCoords(coords);
		}
		else if (cleanLine.find("DEMAND_SECTION") != std::string::npos) {
			readingCoords = false;
			readingDemands = true;

			int dimension = problemData.getDimension();
			std::vector<int> demands(dimension);

			for (int i = 0; i < dimension; i++)
			{
				int nodeId, demand;
				file >> nodeId >> demand;
				demands[i] = demand;
			}

			problemData.setDemands(demands);
		}
		else if (cleanLine.find("DEPOT_SECTION") != std::string::npos) {
			readingCoords = false;
			readingDemands = false;

			int depot;
			file >> depot;
			problemData.setDepotId(depot - 1);

			// -1
			int terminator;
			file >> terminator;
		}
	}
}

std::string ProblemDataParser::trim(const std::string& str)
{
	size_t first = str.find_first_not_of(" \t\r\n");
	if (std::string::npos == first) {
		return str;
	}
	size_t last = str.find_last_not_of(" \t\r\n");
	return str.substr(first, (last - first + 1));
}
