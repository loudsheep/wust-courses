#include "CVRPParser.h"
#include <sstream>
#include <fstream>

CVRPParser::CVRPParser(const std::string& folder_name, const std::string& instance) : folder_name(folder_name), instance(instance)
{
}

SmartPointer<ProblemData> CVRPParser::load()
{
	std::string filepath = this->folder_name + "/" + this->instance + ".lcvrp";

	std::ifstream file(filepath);

	if (!file.is_open())
	{
		exit(1);
	}

	std::string name, weightType;
	int dimension = 0, capacity = 0, depotId = 0;
	std::vector<int> permutation;
	std::vector<Point> coords;
	std::vector<int> demands;
	std::vector<std::vector<double>> weights;

	std::string line;
	while (std::getline(file, line)) {
		std::string cleanLine = trim(line);

		// TODO: change that to not use conrtinue
		if (cleanLine.empty()) continue;
		if (cleanLine.find("EOF") != std::string::npos) break;

		if (cleanLine.find("NAME") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				name = cleanLine.substr(colon + 1);
			}
		}
		else if (cleanLine.find("DIMENSION") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				dimension = std::stoi(cleanLine.substr(colon + 1));
			}
		}
		else if (cleanLine.find("CAPACITY") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				capacity = std::stoi(cleanLine.substr(colon + 1));
			}
		}
		else if (cleanLine.find("EDGE_WEIGHT_TYPE") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			if (colon != std::string::npos) {
				weightType = cleanLine.substr(colon + 1);
			}
		}
		else if (cleanLine.find("PERMUTATION") != std::string::npos) {
			size_t colon = cleanLine.find(":");
			std::string permString = cleanLine.substr(colon + 1);

			std::stringstream ss(permString);
			int id;
			while (ss >> id) {
				permutation.push_back(id - 1);
			}
		}
		else if (cleanLine.find("NODE_COORD_SECTION") != std::string::npos) {
			for (int i = 0; i < dimension; i++)
			{
				int nodeId;
				double x, y;
				file >> nodeId >> x >> y;
				Point point;
				point.x = x;
				point.y = y;

				coords.push_back(point);
			}
		}
		else if (cleanLine.find("DEMAND_SECTION") != std::string::npos) {
			for (int i = 0; i < dimension; i++)
			{
				int nodeId, demand;
				file >> nodeId >> demand;
				demands.push_back(demand);
			}
		}
		else if (cleanLine.find("DEPOT_SECTION") != std::string::npos) {
			int depot;
			file >> depot;
			depotId = depot - 1;

			// -1
			int terminator;
			file >> terminator;
		}
	}


	return SmartPointer<ProblemData>(new ProblemData(
		name, dimension, capacity, weightType, depotId,
		permutation, coords, demands, weights
	));
}

std::string CVRPParser::trim(const std::string& str)
{
	size_t first = str.find_first_not_of(" \t\r\n");
	if (std::string::npos == first) {
		return str;
	}
	size_t last = str.find_last_not_of(" \t\r\n");
	return str.substr(first, (last - first + 1));
}
