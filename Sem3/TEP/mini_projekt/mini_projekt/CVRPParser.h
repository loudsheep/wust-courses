#pragma once
#include "ProblemData.h"
#include "SmartPointer.h"
#include "Result.h"
#include "Error.h"
#include <string>
#include <vector>

class CVRPParser
{
public:
	CVRPParser(const std::string& filepath);

	Result<SmartPointer<ProblemData>, Error> load();

private:
	std::string filepath;

	struct ParsingContext {
		std::string name = "Unknown";
		std::string weightType = "";
		std::string weightFormat = "";
		int dimension = -1;
		int capacity = -1;
		int depotId = -1;

		std::vector<int> permutation;
		std::vector<Point> coords;
		std::vector<int> demands;
		std::vector<std::vector<double>> weights;

		int matrixRow = 0;
		int matrixCol = 0;
	};

	bool parseHeaderLine(const std::string& line, ParsingContext& ctx, std::string& errorMsg);
	bool parseSectionLine(const std::string& line, ParsingContext& ctx,
		bool& readingCoords, bool& readingDemands,
		bool& readingDepots, bool& readingWeights,
		std::string& errorMsg);

	bool validateData(const ParsingContext& ctx, std::string& errorMsg);

	std::string trim(const std::string& str);
	bool parseInt(const std::string& str, int& out);
	bool parseDouble(const std::string& str, double& out);
	std::vector<std::vector<double>> calculateDistanceMatrix(const std::vector<Point>& coords);
};

