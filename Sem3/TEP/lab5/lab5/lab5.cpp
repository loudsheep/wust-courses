#include <iostream>
#include "SmartPointer.h"
#include "BorrowingPointer.h"

class TestClass {
public:
	int value;
	TestClass(int v) {
		this->value = v;
	}
};

int main()
{
	SmartPointer<TestClass> owner1(new TestClass(10));
	SmartPointer<TestClass> owner2(new TestClass(20));

	BorrowingPointer<TestClass> borrower1 = owner1;//.borrow();
	BorrowingPointer<TestClass> borrower2(owner1);

	std::cout << borrower1.isValid();

	owner1.~SmartPointer();

	std::cout << borrower2.isValid();
}