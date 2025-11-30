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

template<typename T, typename E>
Result<T, E>::Result(const T& value)
{
	this->value = new T(value);
}

template<typename T, typename E>
Result<T, E>::Result(E* error)
{
	this->value = nullptr;
	this->errors.push_back(error);
}

template<typename T, typename E>
Result<T, E>::Result(std::vector<E*>& errors)
{
	this->value = nullptr;
	this->errors = errors;
}

template<typename T, typename E>
Result<T, E>::Result(const Result<T, E>& other)
{
	if (other.value == nullptr)
	{
		this->value = nullptr;
	}
	else
	{
		this->value = new T(other.value);
	}

	for (int i = 0; i < other.errors.size(); i++)
	{
		this->errors.push_back(new E(*(other.errors[i])));
	}
}

template<typename T, typename E>
Result<T, E>::~Result()
{
	delete this->value;
	for (int i = 0; i < this->errors.size(); i++)
	{
		delete this->errors[i];
	}
	this->errors.clear();
}


