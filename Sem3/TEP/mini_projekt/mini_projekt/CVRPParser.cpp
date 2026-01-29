#include "CVRPParser.h"
#include <sstream>
#include <fstream>
#include <cmath>

const std::string KEY_NAME = "NAME";
const std::string KEY_DIMENSION = "DIMENSION";
const std::string KEY_CAPACITY = "CAPACITY";
const std::string KEY_EDGE_WEIGHT_TYPE = "EDGE_WEIGHT_TYPE";
const std::string KEY_EDGE_WEIGHT_FORMAT = "EDGE_WEIGHT_FORMAT";
const std::string KEY_PERMUTATION = "PERMUTATION";

const std::string SECTION_COORDS = "NODE_COORD_SECTION";
const std::string SECTION_DEMAND = "DEMAND_SECTION";
const std::string SECTION_DEPOT = "DEPOT_SECTION";
const std::string SECTION_WEIGHTS = "EDGE_WEIGHT_SECTION";
const std::string KEY_EOF = "EOF";

const std::string VAL_EUC_2D = "EUC_2D";
const std::string VAL_EXPLICIT = "EXPLICIT";
const std::string VAL_FULL_MATRIX = "FULL_MATRIX";

CVRPParser::CVRPParser(const std::string& filepath) : filepath(filepath)
{
}

Result<SmartPointer<ProblemData>, Error> CVRPParser::load()
{
	std::ifstream file(this->filepath);

	if (!file.is_open()) return new Error("Could not open file: " + filepath);

	ParsingContext ctx;
	std::string line;
	std::string errorMessage;
	bool finished = false;

	bool readingCoords = false;
	bool readingDemands = false;
	bool readingDepots = false;
	bool readingWeights = false;

	while (!finished && std::getline(file, line)) {
		std::string cleanLine = trim(line);

		if (!cleanLine.empty()) {
			if (cleanLine.find("EOF") != std::string::npos) {
				finished = true;
			}
			else {
				if (parseHeaderLine(cleanLine, ctx, errorMessage)) {
					if (!errorMessage.empty()) return new Error(errorMessage);
				}
				else if (cleanLine == SECTION_COORDS) {
					readingCoords = true; readingDemands = false; readingDepots = false; readingWeights = false;
				}
				else if (cleanLine == SECTION_DEMAND) {
					readingCoords = false; readingDemands = true; readingDepots = false; readingWeights = false;
				}
				else if (cleanLine == SECTION_DEPOT) {
					readingCoords = false; readingDemands = false; readingDepots = true; readingWeights = false;
				}
				else if (cleanLine == SECTION_WEIGHTS) {
					readingCoords = false; readingDemands = false; readingDepots = false; readingWeights = true;
					if (ctx.dimension > 0) {
						ctx.weights.resize(ctx.dimension, std::vector<double>(ctx.dimension));
					}
					else {
						return new Error("Error: DIMENSION not defined before EDGE_WEIGHT_SECTION.");
					}
				}
				else if (!parseSectionLine(cleanLine, ctx, readingCoords, readingDemands, readingDepots, readingWeights, errorMessage)) {
					return new Error(errorMessage);
				}
			}
		}
	}

	if (!validateData(ctx, errorMessage)) {
		return new Error(errorMessage);
	}

	if (ctx.weightType == VAL_EUC_2D) {
		ctx.weights = calculateDistanceMatrix(ctx.coords);
	}

	return SmartPointer<ProblemData>(new ProblemData(
		ctx.name,
		static_cast<unsigned int>(ctx.dimension),
		static_cast<unsigned int>(ctx.capacity),
		ctx.weightType,
		ctx.depotId,
		ctx.permutation, ctx.coords, ctx.demands, ctx.weights
	));
}

bool CVRPParser::parseHeaderLine(const std::string& line, ParsingContext& ctx, std::string& errorMsg)
{
	size_t colon = line.find(":");
	if (colon == std::string::npos) return false;

	std::string key = trim(line.substr(0, colon));
	std::string value = trim(line.substr(colon + 1));

	if (key == KEY_NAME) {
		ctx.name = value;
		return true;
	}
	if (key == KEY_DIMENSION) {
		if (!parseInt(value, ctx.dimension) || ctx.dimension <= 0) {
			errorMsg = "Error: Invalid or non-positive DIMENSION.";
		}
		return true;
	}
	if (key == KEY_CAPACITY) {
		if (!parseInt(value, ctx.capacity) || ctx.capacity < 0) {
			errorMsg = "Error: Invalid or negative CAPACITY.";
		}
		return true;
	}
	if (key == KEY_EDGE_WEIGHT_TYPE) {
		ctx.weightType = value;
		if (value != VAL_EUC_2D && value != VAL_EXPLICIT) {
			errorMsg = "Error: Unsupported EDGE_WEIGHT_TYPE: " + value;
		}
		return true;
	}
	if (key == KEY_EDGE_WEIGHT_FORMAT) {
		ctx.weightFormat = value;
		if (value != VAL_FULL_MATRIX) {
			errorMsg = "Error: Unsupported EDGE_WEIGHT_FORMAT: " + value;
		}
		return true;
	}
	if (key == KEY_PERMUTATION) {
		std::stringstream ss(value);
		std::string token;
		while (ss >> token) {
			int id;
			if (parseInt(token, id)) {
				ctx.permutation.push_back(id - 1); // zmiana na indeks od 0
			}
			else {
				errorMsg = "Error: Invalid value in PERMUTATION.";
			}
		}
		return true;
	}

	return false;
}

bool CVRPParser::parseSectionLine(const std::string& line, ParsingContext& ctx, bool& readingCoords, bool& readingDemands, bool& readingDepots, bool& readingWeights, std::string& errorMsg)
{
	std::stringstream ss(line);
	std::string sVal1, sVal2, sVal3;

	if (readingCoords) {
		if (ss >> sVal1 >> sVal2 >> sVal3) {
			int id;
			double x, y;

			if (!parseInt(sVal1, id)) {
				errorMsg = "Error: Invalid Node ID in coords section.";
				return false;
			}
			if (!parseDouble(sVal2, x) || !parseDouble(sVal3, y)) {
				errorMsg = "Error: Invalid coordinate value.";
				return false;
			}

			if (ctx.dimension != -1 && (id < 1 || id > ctx.dimension)) {
				errorMsg = "Error: Node ID " + std::to_string(id) + " out of bounds.";
				return false;
			}

			Point p;
			p.x = x;
			p.y = y;
			ctx.coords.push_back(p);
			return true;
		}
		errorMsg = "Error: Malformed line in NODE_COORD_SECTION (expected: ID X Y).";
		return false;
	}

	if (readingDemands) {
		if (ss >> sVal1 >> sVal2) {
			int id, demand;
			if (!parseInt(sVal1, id)) {
				errorMsg = "Error: Invalid Node ID in demand section.";
				return false;
			}
			if (!parseInt(sVal2, demand)) {
				errorMsg = "Error: Invalid demand value.";
				return false;
			}
			if (demand < 0) {
				errorMsg = "Error: Negative demand not allowed.";
				return false;
			}
			ctx.demands.push_back(demand);
			return true;
		}
		errorMsg = "Error: Malformed line in DEMAND_SECTION (expected: ID DEMAND).";
		return false;
	}

	if (readingDepots) {
		if (ss >> sVal1) {
			int val;
			if (parseInt(sVal1, val)) {
				if (val == -1) {
					readingDepots = false;
				}
				else {
					ctx.depotId = val - 1; // zmiana na indeks od 0
				}
				return true;
			}
			errorMsg = "Error: Invalid value in DEPOT_SECTION.";
			return false;
		}
	}

	if (readingWeights) {
		while (ss >> sVal1) {
			double w;
			if (parseDouble(sVal1, w)) {
				if (ctx.matrixRow < ctx.dimension && ctx.matrixCol < ctx.dimension) {
					ctx.weights[ctx.matrixRow][ctx.matrixCol] = w;
					ctx.matrixCol++;
					if (ctx.matrixCol >= ctx.dimension) {
						ctx.matrixCol = 0;
						ctx.matrixRow++;
					}
				}
			}
			else {
				errorMsg = "Error: Invalid weight value.";
				return false;
			}
		}
		return true;
	}

	return true;
}

bool CVRPParser::validateData(const ParsingContext& ctx, std::string& errorMsg)
{
	if (ctx.dimension <= 0) {
		errorMsg = "Error: Missing or invalid DIMENSION.";
		return false;
	}
	if (ctx.capacity < 0) {
		errorMsg = "Error: Missing or invalid CAPACITY.";
		return false;
	}

	if (ctx.weightType == VAL_EUC_2D && ctx.coords.size() != static_cast<size_t>(ctx.dimension)) {
		errorMsg = "Error: Coordinate count (" + std::to_string(ctx.coords.size()) +
			") does not match DIMENSION (" + std::to_string(ctx.dimension) + ").";
		return false;
	}

	if (ctx.demands.size() != static_cast<size_t>(ctx.dimension)) {
		errorMsg = "Error: Demand count does not match DIMENSION.";
		return false;
	}

	if (ctx.depotId < 0 || ctx.depotId >= ctx.dimension) {
		errorMsg = "Error: Missing or invalid DEPOT ID.";
		return false;
	}

	if (!ctx.permutation.empty()) {
		if (ctx.permutation.size() != static_cast<size_t>(ctx.dimension - 1)) {
			errorMsg = "Error: Permutation size (" + std::to_string(ctx.permutation.size()) +
				") does not match DIMENSION - 1 (" + std::to_string(ctx.dimension - 1) + ").";
			return false;
		}
	}

	return true;
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