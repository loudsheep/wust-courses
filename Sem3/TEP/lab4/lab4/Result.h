#pragma once
#include <vector>

template <typename T, typename E>
class Result
{
public:
	Result(const T& value);
	Result(E* error);
	Result(std::vector<E*>& errors);
	Result(const Result<T, E>& other);

	// TODO: destryktror czy powinien usuwaæ wskaŸnik
	~Result();

	static Result<T, E> ok(const T& value);
	static Result<T, E> fail(E* error);
	static Result<T, E> fail(std::vector<E*>& errors);

	Result<T, E>& operator=(const Result<T, E>& other);

	bool isSuccess();
	T getValue();
	std::vector<E*>& getErrors();
private:
	T* value;
	std::vector<E*> errors;
};


template <typename E>
class Result<void, E>
{
public:
	Result();
	Result(E* error);
	Result(std::vector<E*>& errors);
	Result(const Result<void, E>& other);

	~Result();

	static Result<void, E> ok();
	static Result<void, E> fail(E* error);
	static Result<void, E> fail(std::vector<E*>& errors);

	Result<void, E>& operator=(const Result<void, E>& other);

	bool isSuccess();
	std::vector<E*>& getErrors();
private:
	std::vector<E*> errors;
};


#include "Result.tpp";