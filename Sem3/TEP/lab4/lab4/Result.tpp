#include "Result.h"

template<typename T, typename E>
Result<T, E>::Result(const T& value)
{
	this->value = new T(value);
}

template<typename T, typename E>
Result<T, E>::Result(E* error)
{
	this->value = nullptr;
	this->errors.push_back(new E(*error));
}

template<typename T, typename E>
Result<T, E>::Result(std::vector<E*>& errors)
{
	this->value = nullptr;

	for (int i = 0; i < errors.size(); i++)
	{
		this->errors.push_back(new E(*(errors[i])));
	}
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
		this->value = new T(*other.value);
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

template<typename T, typename E>
inline Result<T, E> Result<T, E>::ok(const T& value)
{
	return Result<T, E>(value);
}

template<typename T, typename E>
inline Result<T, E> Result<T, E>::fail(E* error)
{
	return Result<T, E>(error);
}

template<typename T, typename E>
inline Result<T, E> Result<T, E>::fail(std::vector<E*>& errors)
{
	return Result<T, E>(errors);
}

template<typename T, typename E>
inline Result<T, E>& Result<T, E>::operator=(const Result<T, E>& other)
{
	if (this == &other) return *this;

	delete this->value;
	for (size_t i = 0; i < errors.size(); ++i) {
		delete errors[i];
	}
	errors.clear();

	if (other.value == nullptr)
	{
		this->value = nullptr;
	}
	else
	{
		this->value = new T(*other.value);
	}

	for (int i = 0; i < other.errors.size(); i++)
	{
		this->errors.push_back(new E(*(other.errors[i])));
	}

	return *this;
}

template<typename T, typename E>
inline bool Result<T, E>::isSuccess()
{
	return this->value != nullptr && this->errors.empty();
}

template<typename T, typename E>
inline T Result<T, E>::getValue()
{
	if (this->value == nullptr)
	{
		return T();
	}
	return *this->value;
}

template<typename T, typename E>
inline std::vector<E*>& Result<T, E>::getErrors()
{
	return this->errors;
}


// implemnencja dla void

template<typename E>
Result<void, E>::Result()
{
}

template<typename E>
Result<void, E> Result<void, E>::ok()
{
	return Result<void, E>();
}