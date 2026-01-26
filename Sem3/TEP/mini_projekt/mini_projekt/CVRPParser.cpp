#include "CVRPParser.h"
#include <sstream>
#include <fstream>
#include <cmath>

CVRPParser::CVRPParser(const std::string& folder_name, const std::string& instance) : folder_name(folder_name), instance(instance)
{
}

Result<SmartPointer<ProblemData>, ParsingError> CVRPParser::load()
{
	std::string filepath = this->folder_name + "/" + this->instance + ".lcvrp";

	std::ifstream file(filepath);

	if (!file.is_open())
	{
		return new ParsingError("Could not open file: " + filepath);
	}

	std::string name = "Unknown", weightType = "", weightFormat = "";
	int dimension = -1, capacity = -1, depotId = -1;

	std::vector<int> permutation;
	std::vector<Point> coords;
	std::vector<int> demands;
	std::vector<std::vector<double>> weights;

	bool readingCoords = false;
	bool readingDemands = false;
	bool readingDepots = false;
	bool readingWeights = false;

	int matrixRow = 0;
	int matrixCol = 0;

	std::string line;
	bool finished = false;
	bool errorFound = false;
	std::string errorMessage = "";

	while (!finished && !errorFound && std::getline(file, line)) {
		std::string cleanLine = trim(line);

		if (!cleanLine.empty())
		{
			if (cleanLine.find("EOF") != std::string::npos)
			{
				finished = true;
			}
			else
			{
				if (cleanLine.find("NAME") != std::string::npos) {
					size_t colon = cleanLine.find(":");
					if (colon != std::string::npos) {
						name = trim(cleanLine.substr(colon + 1));
					}
				}
				else if (cleanLine.find("DIMENSION") != std::string::npos) {
					size_t colon = cleanLine.find(":");
					if (colon != std::string::npos) {
						if (!parseInt(cleanLine.substr(colon + 1), dimension) || dimension <= 0) {
							errorFound = true;
							errorMessage = "Error: Invalid or non-positive DIMENSION.";
						}
					}
				}
				else if (cleanLine.find("CAPACITY") != std::string::npos) {
					size_t colon = cleanLine.find(":");
					if (colon != std::string::npos) {
						if (!parseInt(cleanLine.substr(colon + 1), capacity) || capacity < 0) {
							errorFound = true;
							errorMessage = "Error: Invalid or negative CAPACITY.";
						}
					}
				}
				else if (cleanLine.find("EDGE_WEIGHT_TYPE") != std::string::npos) {
					size_t colon = cleanLine.find(":");
					if (colon != std::string::npos) {
						weightType = trim(cleanLine.substr(colon + 1));
						if (weightType != "EUC_2D" && weightType != "EXPLICIT") {
							errorFound = true;
							errorMessage = "Error: Unsupported EDGE_WEIGHT_TYPE: " + weightType;
						}
					}
				}
				else if (cleanLine.find("EDGE_WEIGHT_FORMAT") != std::string::npos) {
					size_t colon = cleanLine.find(":");
					if (colon != std::string::npos) {
						weightFormat = trim(cleanLine.substr(colon + 1));
						if (weightFormat != "FULL_MATRIX") {
							errorFound = true;
							errorMessage = "Error: Unsupported EDGE_WEIGHT_FORMAT: " + weightFormat;
						}
					}
				}
				else if (cleanLine.find("PERMUTATION") != std::string::npos) {
					size_t colon = cleanLine.find(":");
					if (colon != std::string::npos) {
						std::string permString = cleanLine.substr(colon + 1);
						std::stringstream ss(permString);
						int id;
						while (ss >> id) {
							permutation.push_back(id - 1);
						}
					}
				}
				else if (cleanLine.find("NODE_COORD_SECTION") != std::string::npos) {
					readingCoords = true; readingDemands = false; readingDepots = false; readingWeights = false;
				}
				else if (cleanLine.find("DEMAND_SECTION") != std::string::npos) {
					readingCoords = false; readingDemands = true; readingDepots = false; readingWeights = false;
				}
				else if (cleanLine.find("DEPOT_SECTION") != std::string::npos) {
					readingCoords = false; readingDemands = false; readingDepots = true; readingWeights = false;
				}
				else if (cleanLine.find("EDGE_WEIGHT_SECTION") != std::string::npos) {
					readingCoords = false; readingDemands = false; readingDepots = false; readingWeights = true;
					if (dimension > 0) {
						weights.resize(dimension, std::vector<double>(dimension));
					}
					else {
						errorFound = true;
						errorMessage = "Error: DIMENSION not defined before EDGE_WEIGHT_SECTION.";
					}
				}
				else if (readingCoords) {
					std::stringstream ss(cleanLine);
					int id;
					double x, y;
					if (!(ss >> id >> x >> y)) {
						errorFound = true;
						errorMessage = "Error: Malformed line in NODE_COORD_SECTION.";
					}
					else {
						Point p; p.x = x; p.y = y;
						coords.push_back(p);
					}
				}
				else if (readingDemands) {
					std::stringstream ss(cleanLine);
					int id, demand;
					if (!(ss >> id >> demand)) {
						errorFound = true;
						errorMessage = "Error: Malformed line in DEMAND_SECTION.";
					}
					else {
						if (demand < 0) {
							errorFound = true;
							errorMessage = "Error: Negative demand found.";
						}
						else {
							demands.push_back(demand);
						}
					}
				}
				else if (readingDepots) {
					int val;
					if (parseInt(cleanLine, val)) {
						if (val != -1) {
							depotId = val - 1;
						}
						else {
							readingDepots = false;
						}
					}
				}
				else if (readingWeights) {
					std::stringstream ss(cleanLine);
					double w;
					while (ss >> w && !errorFound) {
						if (matrixRow < dimension && matrixCol < dimension) {
							weights[matrixRow][matrixCol] = w;
							matrixCol++;
							if (matrixCol >= dimension) {
								matrixCol = 0;
								matrixRow++;
							}
						}
					}
				}
			}
		}
	}
	if (!errorFound) {
		if (dimension == -1) {
			errorFound = true; errorMessage = "Error: Missing DIMENSION.";
		}
		else if (capacity == -1) {
			errorFound = true; errorMessage = "Error: Missing CAPACITY.";
		}
		else if (weightType == "EUC_2D" && coords.size() != dimension) {
			errorFound = true; errorMessage = "Error: Coordinate count mismatch.";
		}
		else if (weightType == "EXPLICIT" && (weights.empty() || weights.size() != dimension)) {
			errorFound = true; errorMessage = "Error: Matrix data incomplete or missing.";
		}
		else if (demands.size() != dimension) {
			errorFound = true; errorMessage = "Error: Demand count mismatch.";
		}
		else if (depotId == -1) {
			errorFound = true; errorMessage = "Error: Missing DEPOT_SECTION.";
		}
	}

	if (errorFound) {
		return new ParsingError(errorMessage);
	}

	if (weightType == "EUC_2D") {
		weights = calculateDistanceMatrix(coords);
	}

	return SmartPointer<ProblemData>(new ProblemData(
		name, dimension, capacity, weightType, depotId,
		permutation, coords, demands, weights
	));
}

std::vector<std::vector<double>> CVRPParser::calculateDistanceMatrix(const std::vector<Point>& coords)
{
	int n = coords.size();
	std::vector<std::vector<double>> distanceMatrix(n, std::vector<double>(n, 0.0));

	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++)
		{
			if (i != j)
			{
				double dx = coords[i].x - coords[j].x;
				double dy = coords[i].y - coords[j].y;
				distanceMatrix[i][j] = std::sqrt(dx * dx + dy * dy);
			}
		}
	}

	return distanceMatrix;
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

bool CVRPParser::parseInt(const std::string& str, int& out)
{
	try {
		size_t processed = 0;
		out = std::stoi(str, &processed);
		return processed == str.length();
	}
	catch (...) {
		return false;
	}
}

bool CVRPParser::parseDouble(const std::string& str, double& out)
{
	try {
		size_t processed = 0;
		out = std::stod(str, &processed);
		return processed == str.length();
	}
	catch (...) {
		return false;
	}
}
